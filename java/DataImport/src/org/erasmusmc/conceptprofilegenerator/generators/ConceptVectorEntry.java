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

package org.erasmusmc.conceptprofilegenerator.generators;

import java.util.Comparator;


public class ConceptVectorEntry {
    public Integer key;
    public double value;
    
    public ConceptVectorEntry(Integer key, double value) {
      this.key = key;
      this.value = value;
    }
  
  public static Comparator <ConceptVectorEntry> fingerprintEntryComparator() {
    return new Comparator<ConceptVectorEntry>() {
      @Override
	public int compare(ConceptVectorEntry object1, ConceptVectorEntry object2) {
        if (object1.value < object2.value){
          return 1;
        }
        else if (object1.value == object2.value){
          return 0;
        }
        else {
          return -1;
        }
      }
    };
  }
}
