/*
 * Concept profile generation and analysis for Gene-Disease paper
 * Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
 *  Leiden, The Netherlands
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

package analysis;

import static KnowledgeTransfer.PathConfigs.CPGP_BASE_DIR;
import static KnowledgeTransfer.PathConfigs.MATCH_SCORE_FILENAME;
import static KnowledgeTransfer.PathConfigs.MEDLINE_GROUNDHOG_FOLDER_NAME;
import static com.google.common.collect.Sets.intersection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.utilities.ReadCSVFile;
import org.erasmusmc.utilities.WriteCSVFile;

import KnowledgeTransfer.PathConfigs;

public class AddCoOccToMatchScore {

//	public static final String OUTPUT = MATCH_SCORE_FILENAME + "-coOcc.txt";
	public static final String OUTPUT = PathConfigs.RESULTS_BASE_DIR + "concept_pair_data.txt";
	public static Groundhog documentProfilesGroundhog;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<Integer, Set<Integer>> pubMedIdsPerConcept = new HashMap<Integer, Set<Integer>>();
		
		GroundhogManager groundhogmanager = new GroundhogManager(CPGP_BASE_DIR);
		documentProfilesGroundhog = groundhogmanager.getGroundhog(MEDLINE_GROUNDHOG_FOLDER_NAME);

		WriteCSVFile output = new WriteCSVFile(OUTPUT);
		
		ReadCSVFile input = new ReadCSVFile(MATCH_SCORE_FILENAME);
		Iterator<List<String>> it = input.iterator();
		
		int n = 1; 
		
		System.out.println("Start processing match score file.");
		
		while(it.hasNext()) {
			List<String> fields = it.next();
			
			Integer conceptA = Integer.parseInt(fields.get(0));
			Integer conceptB = Integer.parseInt(fields.get(1));
			Double matchScore = Double.parseDouble(fields.get(2));
			
			if (Double.isNaN(matchScore))
			{
				continue ;
			}

			Set<Integer> pmidsA = null;
			Set<Integer> pmidsB = null;
			
			if (pubMedIdsPerConcept.containsKey(conceptA))
			{
				pmidsA = pubMedIdsPerConcept.get(conceptA);
			}
			else
			{
				pmidsA = documentProfilesGroundhog.getRecordIDsForConcept(conceptA);
				pubMedIdsPerConcept.put(conceptA, pmidsA);
			}
			
			if (pubMedIdsPerConcept.containsKey(conceptB))
			{
				pmidsB = pubMedIdsPerConcept.get(conceptB);
			}
			else
			{
				pmidsB = documentProfilesGroundhog.getRecordIDsForConcept(conceptB);
				pubMedIdsPerConcept.put(conceptB, pmidsB);
			}

			Set<Integer> i2 = intersection(pmidsA, pmidsB);
//			output.write(Arrays.asList(fields.get(0), fields.get(1), fields.get(2), i2.toString()));
			output.write(Arrays.asList(fields.get(0), fields.get(1), String.valueOf(i2.size()), fields.get(2)));
			
			if (n++ % 1000 == 0)
			{
				System.out.println("Processed " + n + " concept pairs.");		
			}
		}

		output.close();
		//TODO: collecting statistics
		
		System.out.println("Done!");
	}
}
