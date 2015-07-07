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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** Data structure for storing the concepts found in a text after indexation.*/
public class ResultConcept implements Serializable{
  /** The conceptID of a concept found in the text */
  public int conceptId = -1;
  
  /** The ResultTerms associated with this concept. 
   * Each occurrence of the concept in the text is a single ResultTerm. 
   * Retrieving the count of this list therefore gives you the concept-frequency. */
  public List<ResultTerm> terms = new ArrayList<ResultTerm>();
  
  private static final long serialVersionUID = -7952183865655145010L;  
  
  private void readObject(
      ObjectInputStream aInputStream
  ) throws ClassNotFoundException, IOException {
    aInputStream.defaultReadObject();
  }
  
  
  private void writeObject(
      ObjectOutputStream aOutputStream
  ) throws IOException {
    aOutputStream.defaultWriteObject();
  }  
}
