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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.textMining.LVG.LVGNormaliser;
//import org.erasmusmc.utilities.LVGNormaliser;
import org.erasmusmc.utilities.StringUtilities;

/**
 * Peregrine for looking up terms in the ontology. It will only return a match when the whole input text
 *  is matched to a term in the ontology. Saves memory when compared to a normal ConceptPeregrine.
 * @author martijn
 *
 */
  public class LookupPeregrine extends AbstractPeregrine {
  
  /** Specifies whether the input text should also be normalised before matching. 
   * Default is set to false, but if at least one term in the ontology has the normalisation flag set,
   * it will automatically be turned to true.
   * <br><br>The default value is false
   */  
  public boolean normalize = false;
  
  /** If true, the entire ontology structure will be destroyed during release, thus saving memory.
   * <br><br>The default value is False.*/
  public boolean destroyOntologyDuringRelease = false;
  
  public LookupPeregrine() {
    normaliser = new LVGNormaliser();
    tokenizer = new SimpleTokenizer();
  }
  
  public LookupPeregrine(String lvgPropertiesPath) {
    if (lvgPropertiesPath != null)
      normaliser = new LVGNormaliser(lvgPropertiesPath);
    tokenizer = new SubSentenceTokenizer();
  }
  
  @Override
  public void index(String string){
    initializeIndex(string);
    checkString(casesentiveCaseNorm(tokenizer.tokens), words);
    if (normalize)
      checkString(normalise(tokenizer.tokens), normwords);
    checkString(toLowercase(tokenizer.tokens), lcwords);
  }

  private void checkString(List<String> tokens, Map<String, IntList> wordList) {
    String neatString = StringUtilities.join(tokens, " ");
    IntList conceptIDs = wordList.get(neatString);
    if (conceptIDs != null)
      for (int conceptID : conceptIDs){
        ResultTerm resultTerm = new ResultTerm();
        resultTerm.words = new int[tokens.size()];
        for (int i = 0; i < tokens.size(); i++)
          resultTerm.words[i] = i;
        resultTerms.add(resultTerm);
        
        ResultConcept resultConcept = null;
        for (ResultConcept concept : resultConcepts)
          if (concept.conceptId == conceptID){
            resultConcept = concept;
            break;
          }
            
        if (resultConcept == null){
          resultConcept = new ResultConcept();
          resultConcept.conceptId = conceptID;
          resultConcepts.add(resultConcept);
        }
        resultConcept.terms.add(resultTerm);
      }
  }

  @Override
  public void release(){
    if (destroyOntologyDuringRelease && !(ontology instanceof OntologyStore)) {
      destroyOntologyDuringRelease = false;
    }
    words.clear();
    normwords.clear();
    lcwords.clear();
    TermStore term;
    List<String> tokens;
    Map<String, IntList> wordlist;
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
        String neatTerm = StringUtilities.join(tokens, " ");
        
        IntList conceptIDs = wordlist.get(neatTerm);
        if (conceptIDs == null){
          conceptIDs = new IntList(1);
          wordlist.put(neatTerm, conceptIDs);
        }
        if (conceptIDs.size() == 0 || !conceptIDs.get(conceptIDs.size()-1).equals(concept.getID()))
          conceptIDs.add(concept.getID());
      }
      if (destroyOntologyDuringRelease) values.remove();
    }
    if (destroyOntologyDuringRelease) ontology = null;
  }
  
  protected void initializeIndex(String string){
    resultTerms.clear();
    resultConcepts.clear();
    if (string != null) 
      tokenizer.tokenize(string);
    removeStopwords();  
  }
  
  protected Map<String, IntList> words = new HashMap<String, IntList>();
  protected Map<String, IntList> normwords = new HashMap<String, IntList>();
  protected Map<String, IntList> lcwords = new HashMap<String, IntList>();

}
