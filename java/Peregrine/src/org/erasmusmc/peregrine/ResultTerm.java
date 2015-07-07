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

/** Data structure for storing the term occurrences found in a text after indexation.*/
public class ResultTerm implements Serializable{ 
  
  /** Specifies the words in the text that belong to this term. 
   * The integers in this array refer to the tokens in the Tokenizer of the Peregrine. */
  public int[] words;
  
  /** Refers to the internal datastructure of Peregrine, where more information on this term can be found,
   * for instance which concepts it is associated with. */
  public ReleasedTerm term;
  
  private static final long serialVersionUID = 1L;
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    if (term.conceptId.length != 0) buffer.append(Integer.toString(term.conceptId[0]));
    for (int i = 1; i < term.conceptId.length; i++){
      buffer.append("/");
      buffer.append(Integer.toString(term.conceptId[i]));
    }
    return buffer.toString();
  }
}
