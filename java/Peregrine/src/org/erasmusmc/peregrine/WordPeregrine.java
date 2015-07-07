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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.erasmusmc.math.CRC32;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.textMining.LVG.LVGNormaliser;
import org.erasmusmc.utilities.AbstractNormaliser;
//import org.erasmusmc.utilities.LVGNormaliser;
import org.erasmusmc.utilities.StringUtilities;

/** Identifies words in the text. 
 * Each word is given a unique identifier, using a CRC32 as hashcode.*/
public class WordPeregrine extends AbstractPeregrine{
  
  /** If true, words are first normalised before the CRC32 is calculated.
   * <br><br>The default value is True.*/
  public boolean normaliseWords = true; 
  
  /** If true, words are first converted to lowercase before the CRC32 is calculated.
   * <br><br>The default value is True.*/
  public boolean lowercaseWords = true; 
  
  /** Construct an ontology during indexation. 
   * This ontology can be used to translate the CRC32 codes back to words.
   * <br><br>The default value is False.*/
  public boolean buildOntology = false; 
  
  /** If true, Peregrine will keep an internal list of created conceptIDs and will not check the ontology whether 
   *  a concept already exists. This will speed up the process.
   * <br><br>The default value is False.*/
  public boolean assumeEmptyOntology = false;
  
  /**
   * If true, Peregrine will always insert the concept into the ontology, and will not check whether it exists
   */
  public boolean alwaysInsertIntoOntology = false;
  
  /** Defines which n-grams will be returned by Peregrine. 
   * <br><br>The default value is {1}, meaning that only single words are detected */
  public int[] ngrams = {1};
  
  /** For n-grams with n greater than 1, breakChars defines the characters that a n-gram cannot cross.
   * This serves to limit the number of different (spurious) n-grams.
   * <br><br>The default value is [,;():\\"]*/
  public Set<Character> breakChars = new TreeSet<Character>();
    
  public WordPeregrine() {
    normaliser = new LVGNormaliser();
    tokenizer = new SBDtokenizer();
    breakChars.add(',');
    breakChars.add(';');
    breakChars.add('(');
    breakChars.add(')');
    breakChars.add(':');
    breakChars.add('\'');
    breakChars.add('"');
  }
  
  public WordPeregrine(AbstractNormaliser normaliser){
  	this.normaliser = normaliser; 
  }
  
  public void release() {
    
  }

  public void index(String string) {
    initializeIndex(string);
    resultConcepts.clear();
    id2concept = new TreeMap<Integer, ResultConcept>();
    id2string = new TreeMap<Integer, String>();
    int start = 0;
    for (int i = 0; i < tokenizer.tokens.size(); i++){
      String token = tokenizer.tokens.get(i);
      if (!token.equals("")){
        if (precededByBreakChar(i) || tokenizer.endOfSentence.contains(i))
          start = i;
        //if ((!StringUtilities.isAbbr(token) || token.length() == 1)  && stopwords.contains(token.toLowerCase())) {
        if (stopwords.contains(StringUtilities.firstLetterToLowerCase(token))) {
          start = i+1;
        } else {
          for (int n : ngrams){
            int firstWord = i-n+1;
            if (firstWord >= start)
              addTerm(firstWord, i);
          }
        }
      }
    }
    if (buildOntology) addNewWordsToOntology();
  }
  private String text;
  private boolean precededByBreakChar(int tokenpos) {
    int pos = tokenizer.startpositions.get(tokenpos)-1;
    while (pos > 0 && !Character.isLetterOrDigit(text.charAt(pos))){
      if (breakChars.contains(text.charAt(pos)))
        return true;
      pos--;
    }
    return false;
  }

  private Map<Integer, ResultConcept> id2concept;
  private Map<Integer, String> id2string;
  //private SortedIntListSet conceptIDs = new SortedIntListSet(20000000);
  private Set<Integer> conceptIDs = new HashSet<Integer>(20000000);
  
  private void addTerm(int start, int end) {
    String string = StringUtilities.join(tokenizer.tokens.subList(start, end+1), " ");
    int hashcode = crc32.crc32(string);
    ResultTerm resultTerm = new ResultTerm();
    resultTerm.words = new int[end-start+1];
    for (int i = start; i <= end; i++){
      resultTerm.words[i-start] = i;
    }
    ResultConcept resultconcept = id2concept.get(hashcode);
    if (resultconcept == null) {
      resultconcept = new ResultConcept();
      resultconcept.conceptId = hashcode;
      id2concept.put(hashcode, resultconcept);
      resultConcepts.add(resultconcept);
    }
    resultconcept.terms.add(resultTerm);  
    resultTerms.add(resultTerm);
    id2string.put(hashcode, string);
  }

  private void addNewWordsToOntology() {
    for (ResultConcept resultConcept : resultConcepts){
      boolean addConcept;
      if (alwaysInsertIntoOntology)
        addConcept = true;
      else {
        if (assumeEmptyOntology){
          if (conceptIDs.contains(resultConcept.conceptId)){
            addConcept = false;
          } else {
            addConcept = true;
            conceptIDs.add(resultConcept.conceptId);
          }
        } else {//do not assume an empty ontology, so check whether concept already in ontology:
          Concept concept = ontology.getConcept(resultConcept.conceptId);
          addConcept = (concept == null);
        }
      }
      if (addConcept){
        Concept concept = new Concept(resultConcept.conceptId);
        concept.setName(id2string.get(resultConcept.conceptId));
        ontology.setConcept(concept);       
      }
    }
  }
  
  private void initializeIndex(String string){
    text = string;
    resultTerms.clear();
    if (string == null)
      string = "";
    
    tokenizer.tokenize(string);    
    
    if (normaliseWords) 
      tokenizer.tokens = normalise(tokenizer.tokens);  
    else if (lowercaseWords)
      tokenizer.tokens = toLowercase(tokenizer.tokens);    
  }
 
  protected CRC32 crc32= new CRC32();
}
