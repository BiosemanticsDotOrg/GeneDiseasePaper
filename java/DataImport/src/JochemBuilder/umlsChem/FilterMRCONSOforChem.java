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

package JochemBuilder.umlsChem;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

import casperSoftwareCode.AssignSemanticTypeToCui;
import casperSoftwareCode.CasperFilters;

public class FilterMRCONSOforChem {

	  //public static boolean chemical = false;
	  public static Set<Integer> chemicalSemanticTypes = getAllChemicalSemanticTypes();  

	  public void getChemicalsFromMRCONSO(String mrconsoPath, String mrstyPath, String ontologyPath, String logfile) {

	    System.out.println("Starting script: "+StringUtilities.now());
	    WriteTextFile logFile = new WriteTextFile(logfile);

	    /** Add semantic types from MRSTY */
	    System.out.println("Adding semantic types to concepts...");
	    Map<Integer, Set<Integer>> conceptsWithSemTypes = AssignSemanticTypeToCui.getCuisWithSemanticTypes(mrstyPath);

	    /** Set ontology variables*/
	    WriteTextFile newOntologyFile = new WriteTextFile(ontologyPath);
	    int cuiCol = 0;
	    int termTextCol = 14;
	    int cui = -1;
	    int chemicalTermsCount = 0;
	    int moreThan255count = 0;
	    int nonEnglishTermsCount = 0;
	    int suppressableTermsCount = 0;
	    DummyChemConcept concept = null;

	    /** Filter MRCONSO*/
	    System.out.println("Filtering MRCONSO file... ");
	    ReadTextFile textFile = new ReadTextFile(mrconsoPath);
	    Iterator<String> fileIterator = textFile.getIterator();
	    int lineCount = 0;
	    while (fileIterator.hasNext()) {
	      lineCount++;
	      if (lineCount % 100000 == 0)
	        System.out.println(lineCount+" lines processed from MRCONSO.RRF");
	      String line = fileIterator.next();
	      if (line.length() != 0) {
	        String[] columns = line.split("\\|");
	        cui = Integer.parseInt(columns[cuiCol].trim().substring(1, columns[cuiCol].length()));
	        String term = columns[termTextCol].trim();
	        concept = new DummyChemConcept();            
	        concept.setCUI(cui);
	        concept.setTermText(term);
	        concept.setSemType(conceptsWithSemTypes.get(concept.getCUI()));
	        if (conceptHasChemicalSemanticType(concept)){
	          if (CasperFilters.isMoreThan255(columns)) {
	            logFile.writeln("TERM FIELD MORE THAN 255 CHARACTERS|" + line);
	            moreThan255count++;
	          }
	          else if (CasperFilters.notRightLanguage(columns)) {
	            logFile.writeln("TERMS NOT ENGLISH LANGUAGE|" + line);
	            nonEnglishTermsCount++;
	          }
	          else if (CasperFilters.isSuppressable(columns)) {
	            logFile.writeln("TERMS MARKED AS SUPPRESSABLE BY NLM|" + line);
	            suppressableTermsCount++;
	          }else{
	            chemicalTermsCount++;
	            newOntologyFile.writeln(line);
	          }
	        }         
	      }
	    }

	    newOntologyFile.close();
	    logFile.close();
	    System.out.println(chemicalTermsCount+ " chemical terms were added");
	    System.out.println(moreThan255count+ " terms were removed due to >255 characters");
	    System.out.println(nonEnglishTermsCount+ " terms were removed due to non-English language");
	    System.out.println(suppressableTermsCount+ " terms were removed due to suppressability by NLM");

	  }
	  public static boolean conceptHasChemicalSemanticType(DummyChemConcept concept){
	    Set<Integer> semsForConcept = concept.getSemType(); 
	    if(semsForConcept==null){
	      return false;
	    }
	    Iterator<Integer> semIterator = semsForConcept.iterator();
	    while (semIterator.hasNext()){
	      Integer semID = semIterator.next();
	      if (chemicalSemanticTypes.contains(-semID)){
	        return true;
	      }
	    }
	    return false;
	  }
	  private static Set<Integer> getAllChemicalSemanticTypes() {
	    Set<Integer> result = new TreeSet<Integer>();
	    result.add(-103);
	    result.add(-104);
	    result.add(-109);
	    result.add(-114);
	    result.add(-115);
	    result.add(-116);
	    result.add(-118);
	    result.add(-119);
	    result.add(-110);
	    result.add(-111);
	    result.add(-196);
	    result.add(-197);
	    result.add(-120);
	    result.add(-121);
	    result.add(-195);
	    result.add(-122);
	    result.add(-123);
	    result.add(-124);
	    result.add(-125);
	    result.add(-126);
	    result.add(-127);
	    result.add(-129);
	    result.add(-192);
	    result.add(-130);
	    result.add(-131);
	    result.add(-200);
	    return result;
	  }

}
