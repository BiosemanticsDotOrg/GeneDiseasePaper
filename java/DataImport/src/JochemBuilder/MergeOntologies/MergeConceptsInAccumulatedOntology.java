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

package JochemBuilder.MergeOntologies;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class MergeConceptsInAccumulatedOntology {
	public static Integer startID = 0;
	public static String date = "110809";
	public static String home = "/home/khettne//Projects/Jochem/";
	public static String parameter = "curated";

	public static String accumulatedOntologyPath = home+"chem_accumulated_"+parameter+"_"+date+".ontology";
	public static String mergedOntologyPath = home+"chem_merged_"+parameter+"1_"+date+".ontology";  
	public static String logpath = home+"mergeLog_combined_"+parameter+"1_"+date+".log";

//	public static String accumulatedOntologyPath = home+"chem_merged_"+parameter+"1_"+date+".ontology";
//	public static String mergedOntologyPath = home+"chem_merged_"+parameter+"2_"+date+".ontology";
//	public static String logpath = home+"mergeLog_combined_"+parameter+"2_"+date+".log";

	// public static String accumulatedOntologyPath = home+"chem_merged_"+parameter+"2_"+date+".ontology";
//	public static String mergedOntologyPath = home+"chem_merged_"+parameter+"3_"+date+".ontology";
//	public static String logpath = home+"mergeLog_combined_"+parameter+"3_"+date+".log";

	//  public static String accumulatedOntologyPath = home+"chem_merged_"+parameter+"3_"+date+".ontology";
//	public static String mergedOntologyPath = home+"chem_merged_"+parameter+"4_"+date+".ontology";
//	public static String logpath = home+"mergeLog_combined_"+parameter+"4_"+date+".log";

	public static void main(String[] args) {

		System.out.println("Starting script "+StringUtilities.now());
		WriteTextFile writeFile = new WriteTextFile(logpath);
		OntologyFileLoader loader = new OntologyFileLoader();
		OntologyStore ontology = loader.load(accumulatedOntologyPath);

		OntologyFileLoader loader2 = new OntologyFileLoader();
		OntologyStore ontology2 = loader2.load(accumulatedOntologyPath);

		System.out.println("Starting comparison "+StringUtilities.now());  
		CompareThesauri comparison = new CompareThesauri();

		Map<String, Set<Integer>> allDatabaseIDs = new HashMap<String, Set<Integer>>();
		allDatabaseIDs = comparison.allDatabaseIDsWithCuis(ontology);

		int conceptCount = 0;
		Iterator<Concept> ontologyIterator = ontology.getConceptIterator();
		while (ontologyIterator.hasNext()){
			Concept conceptToCompare = ontologyIterator.next();
			if (conceptToCompare!=null && conceptToCompare.getID()>startID){
				conceptCount++;
				if (conceptCount % 10000 == 0)
					System.out.println(conceptCount+" concepts processed");
				ontology = comparison.compareConceptsInOneThesaurusBasedOnIdentifiers(conceptToCompare, ontology2, allDatabaseIDs, startID, writeFile);
			}
		}

		ontology2 = ontology;
		loader.save(ontology,mergedOntologyPath);
		writeFile.close();
		comparison.numberOfConcepts = comparison.numberOfConcepts*2;
		System.out.println("Number of concepts before merge " + conceptCount);
		System.out.println("Number of merged concepts " + comparison.numberOfConcepts);
		System.out.println("Done! " + StringUtilities.now());
	}
}
