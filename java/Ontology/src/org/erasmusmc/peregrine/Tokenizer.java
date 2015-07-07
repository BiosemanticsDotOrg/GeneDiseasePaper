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

/** Abstract class defining the common functionality for tokenizers */
public class Tokenizer implements Serializable {
  
  /** The list of tokens found in the text */
  public List<String> tokens = new ArrayList<String>();
  
  /** The start-positions (in characters) of the tokens in the tokens list. 
   * The first character in the string has positions 0.*/  
  public List<Integer> startpositions = new ArrayList<Integer>(); //character position in the string
  
  /** The end-positions (in characters) of the tokens in the tokens list. 
   * The first character in the string has positions 0.*/    
  public List<Integer> endpositions = new ArrayList<Integer>(); //character position in the string
  
  /** The indices of the tokens that are at the beginning of a new sentence. 
   * For example, in the sentence "Malaria is transmitted by mosquitos.", the first (and only) 
   * end-of-sentence is 5.*/ 
  public List<Integer> endOfSentence = new ArrayList<Integer>(); //token position in the list
  
  /** Tokenizes the input string, and stores the tokens, start- and end-positions and end-of-sentences 
   * in the appropriate data structures.
   * @param string  The input string.*/
  public void tokenize(String string){}
  
  public Tokenizer(){}
  
  /** Creates a new tokenizer and copies the data of the source tokenizer. 
   * @param tokenizer   The source tokenizer*/
  public Tokenizer(Tokenizer tokenizer){
    this.tokens = new ArrayList<String>(tokenizer.tokens);
    this.startpositions = new ArrayList<Integer>(tokenizer.startpositions);
    this.endpositions = new ArrayList<Integer>(tokenizer.endpositions);
    this.endOfSentence = new ArrayList<Integer>(tokenizer.endOfSentence);
  }
  
  /** Returns all the tokens belonging to one sentence in the text.
   * @param lineNumber  Specifies which sentence should be returned.
   * @return    Returns a list of tokens.*/
  public List<String> line(int lineNumber){
    if (lineNumber == 0)
      return tokens.subList(0,endOfSentence.get(0));
    else
      return tokens.subList(endOfSentence.get(lineNumber-1), endOfSentence.get(lineNumber));
  }
  
  /** Removes a single token and all of its accompanying data from the data structures.
   * @param index   The index of the token to be removed. The first token has index 0.*/
  public void removeToken(int index){
    tokens.remove(index);
    startpositions.remove(index);
    endpositions.remove(index);
    int value;
    for (int i = endOfSentence.size()-1; i >= 0; i--){
      value = endOfSentence.get(i);
      if (value > index){
        endOfSentence.set(i, value-1);
      } else break;
    }
  }
    
  private static final long serialVersionUID = 1L;
}
