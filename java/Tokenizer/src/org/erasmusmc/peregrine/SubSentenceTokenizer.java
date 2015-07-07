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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Tokenizer based on the SBDTokenizer that also detects sub-sentence boundaries (e.g. commas)
 * @author martijn
 *
 */
public class SubSentenceTokenizer extends SBDtokenizer implements Serializable {

  /**
   * The list of characters that can form a subsentence boundary.
   */
  public char[] subSentenceDividers = new char[]{',',';',':'};
  
  private int lastDivider;
  protected List<Integer> subEndOfSentence = new ArrayList<Integer>();
  
  public void tokenize(String string){
    super.tokenize(string);
    subEndOfSentence.clear();
    lastDivider = 0;
    boolean hitDivider = false;
    int i = 0;
    for (; i < string.length(); i++){
      char ch = string.charAt(i);
      if (hitDivider)
        if (Character.isWhitespace(ch))
          storeDivider(i-1);  

      hitDivider = false;
      for (char divider : subSentenceDividers)
        if (ch == divider){
          hitDivider = true;
          break;
        }
    }
    
    merge();
  }
  
  /** Merge subEndOfSentence and endOfSentence lists
   * 
   */
  private void merge() {
    List<Integer> newList = new ArrayList<Integer>();
    int index1 = 0;
    int index2 = 0;
    int previous = -1;
    int value1;
    int value2;
    while (index1 < subEndOfSentence.size() && index2 < endOfSentence.size()){
      value1 = subEndOfSentence.get(index1);
      value2 = endOfSentence.get(index2);
      if (value1 < value2){
        if (value1 != previous) //also delete duplicates in subEOS list
          newList.add(value1);
        previous = value1;
        index1++;
      } else if (value1 > value2){
        newList.add(value2);
        index2++;
      } else {
        newList.add(value1);
        index1++;
        index2++;
      }
    }
    while (index1 < subEndOfSentence.size()) {
      value1 = subEndOfSentence.get(index1);
      if (value1 != previous) //also delete duplicates in subEOS list
        newList.add(value1);
      previous = value1;
      index1++;      
    }
    while (index2 < endOfSentence.size()) {
      value2 = endOfSentence.get(index2);
      newList.add(value2);
      index2++;      
    }     
    subEndOfSentence = newList;
  }

  private void storeDivider(int pos) {
    for (; lastDivider < endpositions.size(); lastDivider++)
      if (endpositions.get(lastDivider) > pos){
        if (lastDivider != 0 && endpositions.get(lastDivider-1) < pos)
          subEndOfSentence.add(lastDivider);
        break;
      }
  }
  
  public void removeToken(int index){
    super.removeToken(index);
    int value;
    for (int i = subEndOfSentence.size()-1; i >= 0; i--){
      value = subEndOfSentence.get(i);
      if (value > index){
        subEndOfSentence.set(i, value-1);
      } else break;
    }
  }

  private static final long serialVersionUID = 1L;

  /**
   * Similar to the endOfSentences, but now for subsentences.
   * @return
   */
  public List<Integer> getSubEndOfSentences() {
    return subEndOfSentence;
  }


}
