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

package org.erasmusmc.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StringListReader {
  public static List<String> readAsList(InputStream stream){
    List<String> result = new ArrayList<String>();
    read(stream, result);

    return result;  	
  }
  
	public static Set<String> readAsSet(InputStream stream){
    Set<String> result = new HashSet<String>();
    read(stream, result);
    return result;  	
  } 
  
  private static void read(InputStream stream, Collection<String> result) {
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
    try {
      while (bufferedReader.ready()){
        result.add(bufferedReader.readLine());
      }
    } catch (IOException e) {
       e.printStackTrace();
    }
	}
}
