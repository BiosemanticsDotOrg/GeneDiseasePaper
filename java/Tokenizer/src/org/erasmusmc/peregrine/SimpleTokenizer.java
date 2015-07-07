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

/** Simple tokenizer: everything that is not a letter or a number is a word-separator. 
 * Sentences are split by detecting line-feed characters.*/
public class SimpleTokenizer extends Tokenizer{
  
  public SimpleTokenizer(){
    super();
  }
  
  public SimpleTokenizer(Tokenizer tokenizer) {
    super(tokenizer);
  }  
  
 
  public void tokenize(String string){
    tokens.clear(); 
    startpositions.clear(); 
    endpositions.clear();  
    endOfSentence.clear();
    
    
    int start = 0;
    int i = 0;
    char ch;
    for (; i < string.length(); i++){
      ch = string.charAt(i);
      if (!Character.isLetterOrDigit(ch) &&
          //!(ch == '-') &&
          !(ch == '\'' && i>0 && Character.isLetter(string.charAt(i-1)) && string.length()-1 > i && string.charAt(i+1) == 's' && (string.length()-2 == i || !Character.isLetterOrDigit(string.charAt(i+2))))){ //leaves ' in possesive pattern    
        if (start != i) {
            tokens.add(string.substring(start,i));
            startpositions.add(start);
            endpositions.add(i-1);
        }
        start = i+1;
        if ((int)ch == 10){
          endOfSentence.add(tokens.size());
        }
      }
    }
    if (start != i) {
      tokens.add(string.substring(start,i));
      startpositions.add(start);
      endpositions.add(i);      
    } 
    //Add end of line at end of text if not already in list:
    if (endOfSentence.size() == 0 || endOfSentence.get(endOfSentence.size()-1) != tokens.size()){
      endOfSentence.add(tokens.size());
    }  
  }
  
  private static final long serialVersionUID = 1L;
}
