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
import java.util.Map;
import java.util.Set;

import org.erasmusmc.utilities.ReadTextFile;


public class ExtractCUIsAndTermsFromMRCONSO {

  public static Map<Integer, Set<String>> extractCuisAndTermsAsMap(String filename){

    int cuiCol = 0;
    int termTextCol = 14;
    int cui = -1;
    int prevCui =-1;
    
    /**  Add semantic type to each cui */
    Map<Integer, Set<String>> conceptsWithTerms = new HashMap<Integer, Set<String>>();
    Set<String> terms  = new HashSet<String>();
    ReadTextFile textFile = new ReadTextFile(filename);
    Iterator<String> fileIterator = textFile.getIterator();
    int lineCount = 0;
    while (fileIterator.hasNext()) {
      lineCount++;
      if (lineCount % 500000 == 0)
        System.out.println(lineCount+" lines processed in creating cui and term map for homonym check");
      String line = fileIterator.next();
      if (line.length() != 0) {
        String[] columns = line.split("\\|");
        if (CasperFilters.isMoreThan255(columns)) {
        }
        else if (CasperFilters.notRightLanguage(columns)) {
        }
        else if (CasperFilters.isSuppressable(columns)) {
        }
        else {
          cui = Integer.parseInt(columns[cuiCol].trim().substring(1, columns[cuiCol].length()));
          String term = columns[termTextCol].trim();
          String newTermText = Rules.makeLowerCaseAndRemoveEos(term);
          if (prevCui != cui) {
            terms = new HashSet<String>();
            conceptsWithTerms.put(cui, terms); 
          }
          prevCui = cui;
          terms.add(newTermText);
        }
      }
    }
    return conceptsWithTerms;
  }
  
}
