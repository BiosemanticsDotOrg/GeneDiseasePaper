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

package org.erasmusmc.rmi.ontology.conceptrelations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ConceptRelation implements Serializable {
  private static final long serialVersionUID = -6606123079514509954L;
  public int parentid;
  public int conceptid;
  
  public ConceptRelation(int parentid, int conceptid) {
    this.parentid = parentid;
    this.conceptid = conceptid;
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    parentid = in.readInt();
    conceptid = in.readInt();
  }  
  
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeInt(parentid);
    out.writeInt(conceptid);
  }
  
  public String toString() {
    return parentid + "-" + conceptid;
  }
  
  public boolean equals(Object other) {
    if(other instanceof ConceptRelation) {
      if(this.parentid == ((ConceptRelation)other).parentid && this.conceptid == ((ConceptRelation)other).conceptid) return true;
    }
    return false;
  }

  @Override
  public int hashCode() {
    // TODO Auto-generated method stub
    return parentid + conceptid;
  }
  
  
  
}
