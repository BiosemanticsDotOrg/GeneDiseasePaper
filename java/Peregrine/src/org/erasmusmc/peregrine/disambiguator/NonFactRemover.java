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

package org.erasmusmc.peregrine.disambiguator;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.erasmusmc.peregrine.AbstractPeregrine;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.utilities.StringUtilities;

public class NonFactRemover {
  public static Set<String> negationWords = createNegationWords();
  public static Set<String> hypotheticalWords = createHypotheticalWords();
  public static Set<String> doubtWords = createDoubtWords();
  
  public static void removeAll(AbstractPeregrine peregrine){
    removeNonFacts(peregrine, negationWords, false);
    removeNonFacts(peregrine, hypotheticalWords, false);
    //removeNonFacts(peregrine, doubtWords, true);
    removeDoubts(peregrine, doubtWords);
  }
  


  public static void removeNonFacts(AbstractPeregrine peregrine, Set<String> triggerWords, boolean deleteLeft){
    int lastEOS = 0;
    for (int eos : peregrine.tokenizer.endOfSentence){
      
      //has negation?
      int negation = -1;
      for (int i = lastEOS; i < eos; i++)
        if (triggerWords.contains(StringUtilities.firstLetterToLowerCase(peregrine.tokenizer.tokens.get(i)))){
          negation = i;
          break;
        }
      
      //remove terms after negation:
      if (negation != -1){
        int start = negation;
        if (deleteLeft)
          start = lastEOS;
        Iterator<ResultTerm> iterator = peregrine.resultTerms.iterator();
        while(iterator.hasNext()){
          ResultTerm term = iterator.next();
          if (term.words[0] >= start && term.words[0] < eos){
            iterator.remove();
          }  
        }
      }
      

      mapTerms2Concepts(peregrine);
     
      lastEOS = eos;
    }
  }
  
  public static void removeDoubts(AbstractPeregrine peregrine, Set<String> triggerWords){

    for (int i = 0; i < peregrine.tokenizer.tokens.size(); i++)
      if (triggerWords.contains(StringUtilities.firstLetterToLowerCase(peregrine.tokenizer.tokens.get(i)))){
        //triggerword found: remove and concepts directly before and after
        Iterator<ResultTerm> iterator = peregrine.resultTerms.iterator();
        while(iterator.hasNext()){
          ResultTerm term = iterator.next();
          if (contains(term.words, i-1) || contains(term.words, i+1)){
            iterator.remove();
          }
        }
      }
    mapTerms2Concepts(peregrine);
  }
  
  private static boolean contains(int[] words, int i) {
    for (int word : words)
      if (word == i) return true;
    return false;
  }

  private static Set<String> createHypotheticalWords() {
    Set<String> result = new HashSet<String>();
    result.add("can");
    result.add("consistent");
    result.add("could");
    result.add("either");
    result.add("evaluate");
    result.add("favor");
    result.add("likely");
    result.add("may");
    result.add("might");
    result.add("most");
    result.add("or");
    result.add("possibility");
    result.add("possible");
    result.add("possibly");
    result.add("presume");
    result.add("probable");
    result.add("probably");
    result.add("question");
    result.add("questionable");
    result.add("rule");
    result.add("should");
    result.add("sometimes");
    result.add("suggest");
    result.add("suggestion");
    result.add("suggestive");
    result.add("suspect");
    result.add("unless");
    result.add("unsure");
    result.add("will");
    result.add("would");
    return result;
  }
  
  private static Set<String> createDoubtWords() {
    Set<String> result = new HashSet<String>();
    result.add("or");
    return result;
  }

 
  //Generate resultConcepts based on resultTerms:
  protected static void mapTerms2Concepts(AbstractPeregrine peregrine){  
    //remove concepts:
    Set<ResultTerm> remainingTerms = new HashSet<ResultTerm>(peregrine.resultTerms);
    Iterator<ResultConcept> conceptIterator = peregrine.resultConcepts.iterator();
    while (conceptIterator.hasNext()){
      ResultConcept concept = conceptIterator.next();
      Iterator<ResultTerm> termIterator = concept.terms.iterator();
      while (termIterator.hasNext()){
        ResultTerm term = termIterator.next();
        if (!remainingTerms.contains(term)) 
          termIterator.remove();
      }
      if (concept.terms.size() == 0)
        conceptIterator.remove();
    }    
    
    /*peregrine.resultConcepts.clear();
    Map<Integer, ResultConcept> id2concept = new TreeMap<Integer, ResultConcept>();
    int conceptId;
    for (ResultTerm resultterm : peregrine.resultTerms){
      for (int i = 0; i < resultterm.term.conceptId.size(); i++){
        conceptId = resultterm.term.conceptId.get(i);
        ResultConcept resultconcept = id2concept.get(conceptId);
        if (resultconcept == null) {
          resultconcept = new ResultConcept();
          resultconcept.conceptId = conceptId;
          id2concept.put(conceptId, resultconcept);
          peregrine.resultConcepts.add(resultconcept);
        }
        resultconcept.terms.add(resultterm);  
      }
    }*/
  }

  private static Set<String> createNegationWords() {
    Set<String> result = new HashSet<String>();
    result.add("cannot");
    result.add("no");
    result.add("not");
    result.add("vs");
    result.add("versus");
    result.add("without");
    //result.add("exclude");
    return result;
  }
}
