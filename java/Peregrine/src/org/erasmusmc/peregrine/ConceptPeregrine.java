/*
 * Concept profile generation tool suite
 * Copyright (C) 2015 Biosemantics Group, Erasmus University Medical Center,
 *  Rotterdam, The Netherlands
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.erasmusmc.peregrine;

//Usage:
// First load an ontology in the 'ontology 'field
// (optional) Load normaliser cache from disc
// (optional) Load stopwords
// Release thesaurus
// Index
// Retrieve results from 'concepts' field

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.textMining.LVG.LVGNormaliser;
//import org.erasmusmc.utilities.LVGNormaliser;

/** Finds concepts that are defined in an ontology in text. */
public class ConceptPeregrine extends AbstractPeregrine{
  /** Specifies the window size for finding the next word of a term. 
   * A window size of 1 means that no other words are allowed between the words of a term. 
   * <br><br>The default value is 1.
   */
  public int windowSize = 1;


  /** Specifies whether the input text should also be normalised before matching. 
   * Default is set to false, but if at least one term in the ontology has the normalisation flag set,
   * it will automatically be turned to true.
   * <br><br>The default value is false
   */  
  public boolean normalize = false;

  /** If several terms map to the same words, only the term consisting of the most words will be selected,
   * if this parameter is set to true.
   * e.g.: Suppose 'Alzheimer's disease' maps to two terms: 'Alzheimer's disease' and 'disease', then
   * only the first term will be selected if this parameter is set to true. 
   * <br><br>The default value is True.*/
  public boolean biggestMatchOnly = true;

  /** If true, the entire ontology structure will be destroyed during release, thus saving memory.
   * <br><br>The default value is False.*/
  public boolean destroyOntologyDuringRelease = false;

  /** If true, statistics on the use of tokens will be collected during release that can be used by some external modules. 
   * <br><br>The default value is True.*/ 
  public boolean countTokenUsage = true; //Token usage is used by disambiguator 

  public ConceptPeregrine() {
    normaliser = new LVGNormaliser();
    tokenizer = new UMLSGeneChemTokenizer();
  }

  public ConceptPeregrine(String lvgPropertiesPath) {
    if (lvgPropertiesPath != null)
      normaliser = new LVGNormaliser(lvgPropertiesPath);
    tokenizer = new UMLSGeneChemTokenizer();
  }
  private boolean newTerm;
  
  public void release(){
    if (destroyOntologyDuringRelease && !(ontology instanceof OntologyStore)) {
      destroyOntologyDuringRelease = false;
    }
    words.clear();
    normwords.clear();
    lcwords.clear();
    terms.clear();
    token2Term.clear();
    pair2Termlinks.clear();
    current = 0;
    lastTokenID = 0;
    ontologyName = ontology.getName(); 
    if (countTokenUsage) 
      token2count = new TreeMap<Integer, Count>();

    Set<Integer> hashCache = new HashSet<Integer>();
    List<String> tokens;
    int[] tokenIDs;
    TermStore term;
    ReleasedTerm releasedTerm;
    Map<String, Integer> wordlist;

    Iterator<Concept> values = ontology.getConceptIterator();
    while (values.hasNext()){
      Concept concept = values.next();
      List<TermStore> terms = concept.getTerms();
      for (int j = 0; j < terms.size(); j++){      
        term = terms.get(j);
        
        initializeIndex(term.text); //Implies tokenization and stopword removal

        if (term.normalised) {
          tokens = normalise(tokenizer.tokens);
          wordlist = normwords;
          normalize = true; // at least one normalised term: turn normalisation on
        } else if (term.caseSensitive) {
          tokens = casesentiveCaseNorm(tokenizer.tokens);
          wordlist = words;
        } else {
          tokens = toLowercase(tokenizer.tokens);
          wordlist = lcwords;         
        }
        if (tokens.size() > 127){
          System.err.println("Error: terms longer than 127 tokens are not supported! Concatenating term: " + term.text);
          tokens = tokens.subList(0, 127);
        }  

        newTerm = false;
        releasedTerm = null;

        tokenIDs = tokens2NewTokenIDs(tokens, wordlist);

        int hash = tokensHash(tokens);        
        if (!newTerm){ //Quick check using hashCache to see if term is new:
          if (!hashCache.contains(hash)) {
            newTerm = true; 
            hashCache.add(hash);
          }
        } else
          hashCache.add(hash);

        if (!newTerm){ //Exhaustive check for homonyms:
          newTerm = true;
          checkTokens(tokens2TokenIDs(tokens, wordlist), 0, tokens.size()-1);        
          for (int t = 0; t < resultTerms.size(); t++){
            releasedTerm = resultTerms.get(t).term;
            if (releasedTerm.length == tokens.size() && 
                releasedTerm.ordered == term.orderSensitive) {
              newTerm = false;
              break;
            }
          }         
        }

        if (newTerm){//no homonym found: add term to thesaurus 
          releasedTerm = addTerm(term, tokens.size(), concept.getID(), j);    
          if (tokenIDs.length == 1){                        //Single token term:
            token2Term.put(tokenIDs[0], releasedTerm);  
          } else {                                          //Multi-token term: 
            if (term.orderSensitive){
              for (int w1=0; w1<tokenIDs.length-1; w1++){
                TokenPair tokenPair = new TokenPair(tokenIDs[w1],tokenIDs[w1+1]);
                addTokenPair(tokenPair, releasedTerm, w1, w1+1);
              }
            } else { //order insensitive:
              for (int w1=0; w1<tokenIDs.length; w1++)
                for (int w2=0; w2<tokenIDs.length; w2++)
                  if (w1 != w2){
                    TokenPair tokenPair = new TokenPair(tokenIDs[w1],tokenIDs[w2]);
                    addTokenPair(tokenPair, releasedTerm, w1, w2);
                  }
            }
          }
        }
        //If duplicate terms per concept: only accept first term:
        if (releasedTerm.conceptId[releasedTerm.conceptId.length-1] != concept.getID()) {
          releasedTerm.addConceptAndTermID(concept.getID(), j);
        }
      }
      if (destroyOntologyDuringRelease) values.remove();
    }
    if (destroyOntologyDuringRelease) ontology = null;
    trimMemory();  
  }

  private void addTokenPair(TokenPair tokenPair, ReleasedTerm releasedTerm, int w1, int w2){
    List<TermLink> termlinks = pair2Termlinks.get(tokenPair);
    if (termlinks == null){
      termlinks = new ArrayList<TermLink>();
      pair2Termlinks.put(tokenPair, termlinks);
    }
    termlinks.add(new TermLink(releasedTerm, w1, w2));
  }
  public void index(String string){
    initializeIndex(string);
    int lineStart = 0;
    int lineEnd;

    tokenIDslist.clear();
    tokenIDslist.add(tokens2TokenIDs(casesentiveCaseNorm(tokenizer.tokens), words));
    if (normalize)
      tokenIDslist.add(tokens2TokenIDs(normalise(tokenizer.tokens), normwords));
    tokenIDslist.add(tokens2TokenIDs(toLowercase(tokenizer.tokens), lcwords));

    List<Integer> endOfSentence;
    if (tokenizer instanceof SubSentenceTokenizer)
      endOfSentence = ((SubSentenceTokenizer)tokenizer).getSubEndOfSentences();
    else
      endOfSentence = tokenizer.endOfSentence;

    for (int i = 0; i < endOfSentence.size(); i++){ //find matches per sentence:
      lineEnd = endOfSentence.get(i)-1;
      for (int[] tokenIDs : tokenIDslist){
        checkTokens(tokenIDs, lineStart, lineEnd);
      }     
      lineStart = lineEnd + 1;
    }

    if (biggestMatchOnly) {removeSmallMatches(resultTerms);}

    mapTerms2Concepts(resultTerms,resultConcepts);
  }

  private IndexTerm createAndAddIndexTerm(ReleasedTerm term){
    IndexTerm indexTerm = new IndexTerm();
    indexTerm.checkedWordPos = new int[term.length];
    for (int i = 0; i < term.length; i++)
      indexTerm.checkedWordPos[i] = -1;
    term.modified = current+indexTerms.size();
    indexTerms.add(indexTerm);
    return indexTerm;
  }

  protected void checkTokens(int[] tokenIDs, int lineStart, int lineEnd){
    current += indexTerms.size()+1;
    if (current > Integer.MAX_VALUE-10000000){
      current = 1;
      for (ReleasedTerm term : terms){
        term.modified = 0;
      }
    }
    indexTerms.clear(); 

    List <TermLink> termLinks;
    ReleasedTerm currentterm;       
    for (int w1=lineStart; w1<=lineEnd; w1++){
      if (tokenIDs[w1] != -1){
        //Check single token terms:
        currentterm = token2Term.get(tokenIDs[w1]);
        if (currentterm != null) {
          createAndAddIndexTerm(currentterm).insert(w1);
          addMatch(currentterm);
        }

        //Generate token-pairs:
        TokenPair tokenPair = new TokenPair(0,0);
        int last = Math.min(lineEnd, w1+windowSize);
        for (int w2=w1+1; w2 <= last; w2++){
          if (tokenIDs[w2] != -1) {
            tokenPair.setTokens(tokenIDs[w1],tokenIDs[w2]);
            termLinks = pair2Termlinks.get(tokenPair);
            if (termLinks != null){
              for (int t = 0; t < termLinks.size(); t++){
                TermLink termlink = termLinks.get(t);
                currentterm = termlink.term;
                IndexTerm indexTerm;
                if (current > currentterm.modified){
                  indexTerm = createAndAddIndexTerm(currentterm);
                } else {
                	//Delete this:
                	if (currentterm.modified-current < 0)
                		System.out.println("Strange difference: " + currentterm.modified + "-" + current);
                			
                  indexTerm = indexTerms.get(currentterm.modified-current);
                  if (w2 - indexTerm.lastChecked > windowSize)
                    indexTerm.clear();
                }
                //check if this word was not already used to match this term
                if (w2 != indexTerm.lastChecked){                  
                  if (currentterm.ordered){
                    if (termlink.wordPos1 == 0){ //First pair of this term
                      if (indexTerm.checkedCount == 0)
                        indexTerm.insertFirst(termlink, w1, w2);  
                      else if  (!otherPairOfThisTerm(termLinks, currentterm, t)){ //checkedcount != 0  
                        indexTerm.clear();
                        indexTerm.insertFirst(termlink, w1, w2);
                      }
                    } else { //Following pairs of this term
                      if (indexTerm.checkedCount == termlink.wordPos2) //Following pairs of this term
                        indexTerm.insert(termlink, w1, w2);
                      //else if (!otherPairOfThisTerm(termLinks, currentterm, t))
                      //    indexTerm.clear();
                    }
                  } else { //unordered
                    if (indexTerm.checkedCount == 0) //First pair of this term
                      indexTerm.insertFirst(termlink, w1, w2);
                    else {
                      if (indexTerm.checkedWordPos[termlink.wordPos1] == w1 &&
                          indexTerm.checkedWordPos[termlink.wordPos2] == -1) //Following pairs of this term
                        indexTerm.insert(termlink, w1, w2);
                      else { //Didn't fit
                        if (!otherPairOfThisTerm(termLinks, currentterm, t)){ //There's not going to be another pair that will fit
                          indexTerm.clear();
                          indexTerm.insertFirst(termlink, w1, w2);
                        }
                      }
                    }
                  }
                  if (indexTerm.checkedCount == currentterm.length){
                    addMatch(currentterm);
                  }
                }
              }
            }
          }
        }
      }  
    } 
  }

  private final boolean otherPairOfThisTerm(List<TermLink> termLinks, ReleasedTerm term, int t) {
    if (t == termLinks.size()-1)
      return false;
    
    if (termLinks.get(t+1).term == term)
        return true;
    return false;
  }

  //Generate resultConcepts based on resultTerms:
  protected static void mapTerms2Concepts(List<ResultTerm> resultTerms, List<ResultConcept> resultConcepts){  
    resultConcepts.clear();
    Map<Integer, ResultConcept> id2concept = new TreeMap<Integer, ResultConcept>();
    int conceptId;
    for (ResultTerm resultterm : resultTerms){
      for (int i = 0; i < resultterm.term.conceptId.length; i++){
        conceptId = resultterm.term.conceptId[i];
        ResultConcept resultconcept = id2concept.get(conceptId);
        if (resultconcept == null) {
          resultconcept = new ResultConcept();
          resultconcept.conceptId = conceptId;
          id2concept.put(conceptId, resultconcept);
          resultConcepts.add(resultconcept);
        }
        resultconcept.terms.add(resultterm);  
      }
    }
  }

  protected int tokensHash(List<String> tokens){
    int hash = 0;
    for (String token : tokens){
      hash += token.hashCode();
    }
    return hash;
  }

  protected int[] tokens2NewTokenIDs(List<String> tokens, Map<String, Integer> wordlist){
    int[] result = new int[tokens.size()];
    Integer id;
    Count count;
    for (int i =0; i < tokens.size(); i++){
      id = wordlist.get(tokens.get(i));
      if (id == null){

        if (countTokenUsage) {
          count = new Count();        
          token2count.put(lastTokenID, count);
        }

        result[i] = lastTokenID;
        wordlist.put(tokens.get(i), lastTokenID);          
        lastTokenID++;
        newTerm = true;

      } else {
        if (countTokenUsage) token2count.get(id).value++;
        result[i] = id;
      }
    }   
    return result;
  }

  protected int[] tokens2TokenIDs(List<String> tokens, Map<String, Integer> wordlist){
    int[] tokenIDs = new int[tokens.size()];
    Integer id = 0;
    for (int i = 0; i < tokens.size(); i++){
      id = wordlist.get(tokens.get(i));
      if (id == null)
        tokenIDs[i] = -1;
      else
        tokenIDs[i] = id;
    }
    return tokenIDs;
  }

  protected ReleasedTerm addTerm(TermStore term, int size, int cid, int termID){
    ReleasedTerm releasedTerm;
    releasedTerm = new ReleasedTerm();
    releasedTerm.length = (byte)size;
    releasedTerm.ordered = term.orderSensitive; 
    releasedTerm.addConceptAndTermID(cid, termID);
    terms.add(releasedTerm);  
    return releasedTerm;
  }

  protected void initializeIndex(String string){
    resultTerms.clear();
    if (string != null) 
      tokenizer.tokenize(string);
    removeStopwords();  
  }

  protected static void removeSmallMatches(List<ResultTerm> resultTerms){
    Map<Integer, List<ResultTerm>> word2term  = new TreeMap<Integer, List<ResultTerm>>();
    List<ResultTerm> mappedterms;
    for (ResultTerm resultterm : resultTerms) {
      for (int word : resultterm.words){
        mappedterms = word2term.get(word);
        if (mappedterms == null){
          mappedterms = new ArrayList<ResultTerm>();
          mappedterms.add(resultterm);
          word2term.put(word, mappedterms);
        } else {
          for (ResultTerm otherterm : mappedterms){
            if (otherterm.term != null){
              if (otherterm.term.length < resultterm.term.length) { //other term is shorter
                otherterm.term = null;               
              } else if (otherterm.term.length > resultterm.term.length) { //this term is shorter
                resultterm.term = null;
                break;
              }
            }
          }
          if (resultterm.term == null) break; else mappedterms.add(resultterm); 
        }
      }
    }
    for (int i = resultTerms.size()-1; i >= 0; i--) {
      if (resultTerms.get(i).term == null) {
        resultTerms.remove(i);
      }
    }
  }  

  protected void addMatch(ReleasedTerm aterm){
    ResultTerm resultterm = new ResultTerm();
    resultterm.words = new int[aterm.length];
    IndexTerm indexTerm = indexTerms.get(aterm.modified-current);
    for (int i = 0; i < aterm.length; i++){
      resultterm.words[i] = indexTerm.checkedWordPos[i];
    }
    if (!aterm.ordered){
      //Sort words in order:
      int temp;
      for (int i = 0; i < resultterm.words.length; i++)
        for (int j = i+1; j < resultterm.words.length; j++)
          if (resultterm.words[i] > resultterm.words[j]){
            temp = resultterm.words[i];
            resultterm.words[i] = resultterm.words[j];
            resultterm.words[j] = temp;
          }
    }      
    resultterm.term = aterm;
    resultTerms.add(resultterm);
    indexTerm.clear();
  }

  protected void trimMemory(){
    if (pair2Termlinks instanceof TokenPairToTermLinksMap)
      ((TokenPairToTermLinksMap)pair2Termlinks).trimToSize();
    for (List<TermLink> termLinks : pair2Termlinks.values())
      ((ArrayList<TermLink>) termLinks).trimToSize();
    ((ArrayList<ReleasedTerm>)terms).trimToSize();
  }


  protected Map<String, Integer> words = new HashMap<String, Integer>();
  protected Map<String, Integer> normwords = new HashMap<String, Integer>();
  protected Map<String, Integer> lcwords = new HashMap<String, Integer>();

  protected List<ReleasedTerm> terms = new ArrayList<ReleasedTerm>();
  protected Map<Integer, ReleasedTerm> token2Term = new HashMap<Integer, ReleasedTerm>();
  protected Map<TokenPair, List<TermLink>> pair2Termlinks = new TokenPairToTermLinksMap();
  protected List<IndexTerm> indexTerms = new ArrayList<IndexTerm>();

  protected static class TokenPair implements Serializable, Comparable<TokenPair>{
    int token1, token2;
    public int hashCode(){
      return token1+token2;
    }
    public boolean equals(Object other) {
      TokenPair otherPair = (TokenPair) other;
      return (this.token1 == otherPair.token1) && (this.token2 == otherPair.token2);
    }

    public int compareTo(TokenPair otherPair){
      int result = this.token1 - otherPair.token1;
      if (result == 0) return this.token2 - otherPair.token2; else return result;
    }

    public TokenPair(int t1, int t2){
      token1 = t1;
      token2 = t2;
    }
    public void setTokens (int t1, int t2){
      token1 = t1;
      token2 = t2;      
    }
    protected static final long serialVersionUID = -8370205486737997308L;    
  }

  protected class Count{
    int value = 1;
  }

  protected int lastTokenID = 0;
  protected int current;
  protected String ontologyName = "";

  //Additions for disambiguator:
  protected Map<Integer, Count> token2count;
  protected List<int[]> tokenIDslist = new ArrayList<int[]>();

  protected static class TermLink implements Serializable{ 
    public ReleasedTerm term;
    public int wordPos1 = 0;
    public int wordPos2 = 0;
    public TermLink(ReleasedTerm aterm, int wordPos1, int wordPos2){
      term = aterm;
      this.wordPos1 = wordPos1;
      this.wordPos2 = wordPos2;
    }
    protected static final long serialVersionUID = -1147776745742497983L;
  }

  public void setOntology(Ontology ontology) {
    super.setOntology(ontology);
  }
}
