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

import static KnowledgeTransfer.ConceptProfileUtil.readConceptProfile;
import static KnowledgeTransfer.PathConfigs.CONCEPT_PROFILES_DIR;
import static KnowledgeTransfer.PathConfigs.MATCH_SCORE_FILENAME;
import static KnowledgeTransfer.PathConfigs.ONTOLOGY_NAME;
import static KnowledgeTransfer.ConceptProfileUtil.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.utilities.ReadCSVFile;

import com.google.common.collect.Sets;

public class TopMatchScoresWithConceptLabels {
	public static final Double MINIMAL_VALUE = Math.pow(10, -1.5);
	public static final File outfile = new File(MATCH_SCORE_FILENAME + "-coOcc." + MINIMAL_VALUE + ".txt.test3");
	
	public static void main(String[] args) throws IOException {
		ReadCSVFile input = new ReadCSVFile(MATCH_SCORE_FILENAME + "-coOcc.txt");
		Iterator<List<String>> it = input.iterator();
		Integer lineCount = 0;
		PrintWriter output = new PrintWriter(outfile);
		
		OntologyManager ontologyManager = new OntologyManager();
		Ontology ontology = ontologyManager.fetchClient(ONTOLOGY_NAME);
		
		while(it.hasNext()) {
			List<String> fields = it.next();
			Integer conceptA = Integer.parseInt(fields.get(0));
			Integer conceptB = Integer.parseInt(fields.get(1));
			Double matchScore = Double.parseDouble(fields.get(2));
			String coOccurrences = fields.get(3);
			
			if(matchScore > MINIMAL_VALUE) {
				output.write(matchScore + "\t" + ontology.getConcept(conceptA).getTerms() + "\t" + ontology.getConcept(conceptB).getTerms() + "\t" + coOccurrences.substring(0, Math.min(coOccurrences.length(), 100)));
				
				// print out labels for connecting concepts
				if(coOccurrences.equals("[]")) {
					// get concept profiles
					Map<Integer, Double> profileA = readConceptProfile(CONCEPT_PROFILES_DIR + conceptA);
					Map<Integer, Double> profileB = readConceptProfile(CONCEPT_PROFILES_DIR + conceptB);
					assert profileA != null && profileB != null;
					
					// intersect and inner product sort
					Map<Integer, Double> product = new HashMap<Integer, Double>();
					for (Integer sharedC: Sets.intersection(profileA.keySet(), profileB.keySet()) ) {
						product.put(sharedC, profileA.get(sharedC) * profileB.get(sharedC));
					}
					TreeSet<Entry<Integer, Double>> sortedSharedConcepts = (TreeSet<Entry<Integer, Double>>) entriesSortedByValues(product);
					
					// select required concepts until percentile
					String connectingConcepts = "";
					Double msContribMin = 1.0;
					for(Entry<Integer, Double> e: sortedSharedConcepts.descendingSet()) {
						if(e.getValue()/matchScore*100 < msContribMin)
							break;
						connectingConcepts += String.format("%d=%s (%.2f) ",
															e.getKey(),
															ontology.getConcept(e.getKey()).getTerms().get(0),
															e.getValue()/matchScore*100);
					}
					output.write("\t" + connectingConcepts);
				}
				output.write("\n");
				output.flush();
				//break;
			}
			
			if( lineCount % 10000 == 0 ) {
				System.out.println("Processed: " + lineCount);
			}
			lineCount++;
		}
		output.close();
	}
}
