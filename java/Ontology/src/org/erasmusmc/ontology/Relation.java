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

package org.erasmusmc.ontology;

import java.io.Serializable;

/**
 * Specifies the relation between two concepts
 * @author Schuemie
 *
 */
public class Relation implements Serializable {
  private static final long serialVersionUID = -8959087468385064232L;
  /** The concept ID of the subject of the relation. */
  public int subject;
  /** The type of relation between the subject and the object */
  public int predicate = -1; 
  /** The concept ID of the object of the relation. */
  public int object;
  public Relation(int subject, int predicate, int object){
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
  }
  public boolean equals(Relation other){
    if (other.subject == subject && other.object == object && other.predicate == predicate){
      return true;
    }
    return false;
  }
  
  public String toString(){
    if(predicate == -1) return subject + " unknown " + object;
    else return subject + " " + predicate + " " + object;
  }
}
