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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.erasmusmc.utilities.StringUtilities;

public class StatementFilterTokenizer extends SBDtokenizer {
  private static final long serialVersionUID = 6227202980575935265L;
  public boolean questionmarkFilter = true;
  public boolean slashFilter = true;
  public boolean negationWordFilter = true;
  public boolean speculationWordFilter = true;
  public boolean alternativeWordFilter = true;
  
  public Set<String> speculationWords = new HashSet<String>();
  public Set<String> negationWords = new HashSet<String>();
  public Set<String> alternativesWords = new HashSet<String>();
  
  public StatementFilterTokenizer(){
    setSpeculationWords();
    setNegationWords();
    setAlternativesWords();
  }
  
  public void tokenize(String string){
    super.tokenize(string);
    if (questionmarkFilter)
      applyCharSentenceFilter('?');
    if (slashFilter)
      applyCharAroundFilter('/');
    if (alternativeWordFilter)
      applyWordFilter(alternativesWords, true);
    if (negationWordFilter)
      applyWordFilter(negationWords, false);
    if (speculationWordFilter)
      applyWordFilter(speculationWords, false);
  }

  private void applyWordFilter(Set<String> words, boolean bothSides) {
    List<Integer> indices = findAll(words);
    for (int index : indices){
      deleteSentenceContaining(index, bothSides);
    }    
  }

  private void applyCharSentenceFilter(char ch) {
    List<Integer> charPos = findAll(ch);
    for (int pos : charPos){
      deleteSentenceContaining(pos);
    }
  }
  
  private void applyCharAroundFilter(char ch) {
    List<Integer> charPos = findAll(ch);
    for (int pos : charPos){
      deleteTokensAround(pos);
    }
  }

  private void deleteTokensAround(int pos) {
    for (int i = 0; i < startpositions.size(); i++)
      if (startpositions.get(i) > pos){
        if (i != 0)
          tokens.set(i-1, "");
        tokens.set(i, "");
        break;
      }    
  }

  private List<Integer> findAll(Set<String> words) {
    List<Integer> positions = new ArrayList<Integer>();
    for (int i = 0; i < tokens.size(); i++)
      if (words.contains(StringUtilities.firstLetterToLowerCase(tokens.get(i))))
        positions.add(i);
    return positions;
  }

  private List<Integer> findAll(char ch) {
    List<Integer> positions = new ArrayList<Integer>();
    for (int i = 0; i < string.length(); i++)
      if (string.charAt(i) == ch)
        positions.add(i);
    return positions;
  }

  private void deleteSentenceContaining(int pos) {
    int sos = 0;
    for (int i = 0; i < endOfSentence.size(); i++){
      int eos = endOfSentence.get(i);
      int startPos = startpositions.get(sos);
      int endPos;
      if (eos == startpositions.size())
        endPos = string.length();
      else
        endPos = startpositions.get(eos);
      if (pos >= startPos && pos < endPos)
        deleteSentence(sos,eos);
      sos = eos;
    }
  }
  
  private void deleteSentenceContaining(int index, boolean bothSides) {
    int sos = 0;
    for (int i = 0; i < endOfSentence.size(); i++){
      int eos = endOfSentence.get(i);
      if (index >= sos && index < eos){
        if (bothSides)
          deleteSentence(sos,eos);
        else
          deleteSentence(index, eos);
      }
      sos = eos;
    }
  }

  private void deleteSentence(int sos, int eos) {
    for (int i = sos; i < eos; i++)
      tokens.set(i, "");
  }

  private void setSpeculationWords(){
    speculationWords.add("can");
    speculationWords.add("either");
    speculationWords.add("may");
    speculationWords.add("might");
    speculationWords.add("would");
    speculationWords.add("likely");
    speculationWords.add("should");
    speculationWords.add("could");
    speculationWords.add("probably");
    speculationWords.add("probable");
    speculationWords.add("possible");
    speculationWords.add("possibly");
    speculationWords.add("suggestion");
    speculationWords.add("suggesting");
    speculationWords.add("suggestive");
    speculationWords.add("unsure");
    speculationWords.add("rule");
    speculationWords.add("question");
    speculationWords.add("questions");
    speculationWords.add("questionable");
    speculationWords.add("most");
    speculationWords.add("sometimes");
    speculationWords.add("unless");
    speculationWords.add("evaluate");
    speculationWords.add("suggests");
    speculationWords.add("probable");
    speculationWords.add("favor");
    speculationWords.add("favored");
    speculationWords.add("presumes");
    speculationWords.add("presumed");
    speculationWords.add("presume");
    speculationWords.add("suspect");
    speculationWords.add("suspected");
    speculationWords.add("suspecting");
    speculationWords.add("consistent");
    speculationWords.add("will");
  }
  
  private void setNegationWords(){
    negationWords.add("cannot");
    negationWords.add("no");
    negationWords.add("without");
    negationWords.add("not");
  }
  
  private void setAlternativesWords(){
    alternativesWords.add("vs");
    alternativesWords.add("versus");
    speculationWords.add("or");
  }
}
