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

import java.util.List;

public class IntegerUtilities {
  public static String intToTwoHexDigits(int value) {
    String result = Integer.toHexString(value);

    while (result.length() < 2)
      result = '0' + result;

    return result;
  }
  
  static public int binarySearch(List<Integer> array, int target){
    int low = 0, middle, high = array.size();
    
    while (low < high) {
      middle = (low+high) / 2;
      
      if (array.get(middle)>target)
        high = middle;
      else
        low = middle+1;
    }
    
    return low;
  }
}
