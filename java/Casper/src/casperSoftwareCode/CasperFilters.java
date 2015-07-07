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

public class CasperFilters {
  
  public static boolean isSuppressable(String[] columns) {
    int suppressCol = 16;
    String flag = columns[suppressCol];
    if (flag.equals("Y") || flag.equals("E") || flag.equals("O")){
      return true;
    }
    return false;
  }
  
  public static boolean isMoreThan255(String[] columns) {
    int termCol = 14;
    String term = columns[termCol];
    if (term.length() > 255){
      return true;
    }
    return false;
  }
  
  public static boolean notRightLanguage(String[] columns) {
    int languageCol = 1;
    String language = "ENG";
    return !columns[languageCol].equals(language);
  }
}
