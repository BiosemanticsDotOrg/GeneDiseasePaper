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

package org.erasmusmc.dataimport.UMLS;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class UMLSFiltersBeforeOntologyCreation {
  
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
  
  public static boolean isFromBadVocabulary(String[] columns) {
    int vocCol = 11;
    String voc = columns[vocCol];
    if (voc.equals("LNC") || (voc.equals("NCI-CTCAE")) ){
      return true;
    }
    return false;
  }
  
  public static String convertToLowerCaseIfWordsMoreThan2AndCharactersMoreThan10AndNotAbbreviationOrAcronym(String term, String voc, List abbreviationsOrAcronyms) {       
    if (!abbreviationsOrAcronyms.contains(term)) {
      if ( voc.equals("HCPCS") || (voc.equals("SPN") || voc.equals("COSTAR") || voc.equals("RXNORM") || voc.equals("VANDF") || voc.equals("DXP") || voc.equals("MTHFDA") || voc.equals("MCM"))){
        return term.toLowerCase();    
      }else if (termMoreThan2WordsAndLongerThan10Characters(term)){
        return term.toLowerCase();
      }
    }
    return term;
  }
  
  public static boolean termMoreThan2WordsAndLongerThan10Characters(String term){
    List<String> wordsInTerm = StringUtilities.mapToWords(term);
    int noOfwords = wordsInTerm.size();
    int noOfCharacters = term.length();
    if (noOfwords >2 && noOfCharacters >10){
      return true;
    }
    return false;
  }
  
  public static ArrayList<String> getAbbreviationsAndAcronyms(String filename) {
    ArrayList<String> abbreviationsOrAcronyms = new ArrayList<String>();
    ReadTextFile textFile = new ReadTextFile(filename);
    Iterator<String> fileIterator = textFile.getIterator();    
    while (fileIterator.hasNext()) {
      String line = fileIterator.next();
      if (line.length() != 0) {
        List<String> columns = StringUtilities.safeSplit(line, '|');
        String abbrOrAcr = columns.get(1).trim();
        abbreviationsOrAcronyms.add(abbrOrAcr);
      } 
    }
    return abbreviationsOrAcronyms;
  }
}
