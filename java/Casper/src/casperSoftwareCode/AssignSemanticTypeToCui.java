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

package casperSoftwareCode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class AssignSemanticTypeToCui {
    
  public static Map<Integer, Set<Integer>> getCuisWithSemanticTypes(String mrstyFilepath){
    int cui = -1;
    int prevCui =-1;
    int cuiCol = 0;
    int semID = -1;
    int semIDCol = 1;

    /**  Connect concepts with semantic types */
    ReadTextFile mrstyTextFile = new ReadTextFile(mrstyFilepath);
    Iterator<String> mrstyFileIterator = mrstyTextFile.getIterator();
    Map<Integer, Set<Integer>> conceptsWithSemTypes = new HashMap<Integer, Set<Integer>>();      
    Set<Integer> semtypes  = new HashSet<Integer>();

    while (mrstyFileIterator.hasNext()) {
      String mrstyLine = mrstyFileIterator.next();
      if (mrstyLine.length() != 0) {
        List<String> columns = StringUtilities.safeSplit(mrstyLine, '|');
        cui = Integer.parseInt(columns.get(cuiCol).trim().substring(1, columns.get(cuiCol).length()));
        semID = Integer.parseInt(columns.get(semIDCol).trim().substring(1, columns.get(semIDCol).length()));             
        if (cui != prevCui){ 
          semtypes = new HashSet<Integer>();
          conceptsWithSemTypes.put(cui, semtypes);
        }
        prevCui = cui;
        semtypes.add(semID);
      }
    }
    return conceptsWithSemTypes;
  }
 }
