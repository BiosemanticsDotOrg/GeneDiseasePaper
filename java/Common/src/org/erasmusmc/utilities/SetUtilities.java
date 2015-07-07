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

package org.erasmusmc.utilities;

import java.util.HashSet;
import java.util.Set;

public class SetUtilities {
  @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set Intersection(Set set1, Set set2){
    Set result = new HashSet(set1);
    result.retainAll(set2);
    return result;
   }
  
  /**
   *  1 minus 2, or the entries in 1 that are not in 2
   * @param set1
   * @param set2
   * @return
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Set substraction(Set set1, Set set2){
    Set result = new HashSet(set1);
    result.removeAll(set2);
    return result;    
  }
  
  public static int sizeOfIntersectionSet(Set<?> set1,Set<?> set2){
     return Intersection(set1,set2).size();
  }
}
