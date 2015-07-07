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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.utilities.AbstractNormaliser;
import org.erasmusmc.utilities.StringUtilities;

/** Defines the elements that the different Peregrines have in common. */
public abstract class AbstractPeregrine {
  
  /** Set the ontology that is to be used for indexing. */  
  public void setOntology(Ontology ontology){
    this.ontology = ontology;
  }
    
  public Ontology getOntology(){
    return ontology;
  }
  

  /**
   * Defines the list of stopwords that will be used for indexation.
   * Should be specified before releasing the thesaurus.
   * Stopwords should be in lowercase.
   */
  public Set<String> stopwords = getDefaultStopWordsForIndexing();
  
    
  protected boolean doNotNormaliseAbbreviations = false;
  
  /** Tokenizer used to tokenize both thesaurus terms and texts. By default, the SubSentenceTokenizer is used. */
  public Tokenizer tokenizer = null;
  
  /** Normalizer used during release and indexation. */
  public AbstractNormaliser normaliser;
  
  /** After indexation, this list will contain all concepts found in the text */
  public List<ResultConcept> resultConcepts = new ArrayList<ResultConcept>();
  
  /** After indexation, this list will contain all terms found in the text. 
   * These terms can also be accessed through the resultConcepts. */
  public List<ResultTerm> resultTerms = new ArrayList<ResultTerm>();  

  
  /** Call this method after setting the ontology, stopwords, and other parameters to prepare 
   * Peregrine for indexation. */
  public abstract void release();
  
  /** Finds all concepts in the text. After indexation, the concepts are listed in resultConcepts, 
   * the terms are listed in resultTerms, and the Tokenizer contains the tokens found in the text.
   * 
   * @param string  The text to be indexed
   */
  public abstract void index(String string);
  
  /**
   * Fetches the default stopword used for indexing (i.e. the stopwords used in Medline)
   * @return    The set of stopwords
   */public static Set<String> getDefaultStopWordsForIndexing(){
    Set<String> result = new TreeSet<String>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(AbstractPeregrine.class.getResourceAsStream("DefaultStopwordsForIndexing.txt")));
    try {
      while (bufferedReader.ready()){
        result.add(bufferedReader.readLine());
      }
    } catch (IOException e) {
       e.printStackTrace();
    }
    return result;
  }
  protected void removeStopwords() {
    String word;
    for (int i = tokenizer.tokens.size()-1; i > -1; i--){
      word = tokenizer.tokens.get(i); 
      if (!StringUtilities.isAbbr(word) && stopwords.contains(word.toLowerCase())) {
        tokenizer.removeToken(i);
      }
    }
  }  
  
  protected List<String> normalise(List<String> tokens){
    return normaliser.normalise(tokens);
  }
  
  protected List<String> toLowercase(List<String> tokens){
    List<String> result = new ArrayList<String>(tokens.size());
    for (int i = 0; i < tokens.size(); i++)
    	result.add(i, tokens.get(i).toLowerCase());
        
    return result;
  }  
  
  /**
   * Converts tokens to lowercase if only their first letter is a capital
   * @param tokens
   * @return
   */
  protected List<String> casesentiveCaseNorm(List<String> tokens) {
    List<String> result = new ArrayList<String>(tokens.size());
    for (int i = 0; i < tokens.size(); i++){
      result.add(i, StringUtilities.firstLetterToLowerCase(tokens.get(i)));
    }
    return result;
  }
  
   
  
  protected Ontology ontology;

  /**
   * If True, abbreviations (tokens with a majority of uppercase letters) will not be normalised, 
   * even if the term as a whole is set to be matched normalised.
   * <br><br>The default value is True.
   */
  public boolean isDoNotNormaliseAbbreviations() {
    return doNotNormaliseAbbreviations;
  }

  /**
   * If True, abbreviations (tokens with a majority of uppercase letters) will not be normalised, 
   * even if the term as a whole is set to be matched normalised.
   * <br><br>The default value is True.
   */
  public void setDoNotNormaliseAbbreviations(boolean doNotNormaliseAbbreviations) {
    this.doNotNormaliseAbbreviations = doNotNormaliseAbbreviations;
    normaliser.doNotNormaliseAbbreviations = doNotNormaliseAbbreviations;
  }
}
