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

package org.erasmusmc.groundhog;

import java.io.IOException;
import java.io.Serializable;


public class ConceptStatistic implements Serializable{

  private static final long serialVersionUID = -4774220148592361158L;
  public int docFrequency;
  public int termFrequency;
  public ConceptStatistic(){
    docFrequency=0;
    termFrequency=0;
  }
  public String toString(){
    return docFrequency + "\t" + termFrequency + "\n";
  }
  
  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    out.writeInt(docFrequency);
    out.writeInt(termFrequency);
  }
  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    docFrequency = in.readInt();
    termFrequency = in.readInt();
  }
  
  
}


