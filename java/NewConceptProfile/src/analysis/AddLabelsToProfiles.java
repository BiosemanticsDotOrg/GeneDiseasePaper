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

import static KnowledgeTransfer.ConceptProfileUtil.readCidFile;
import static KnowledgeTransfer.PathConfigs.HPRD_GENE_CIDS;
import static KnowledgeTransfer.PathConfigs.ONTOLOGY_NAME;
import static KnowledgeTransfer.PathConfigs.THESAURUS_DISEASE_CIDS;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.utilities.ReadCSVFile;

public class AddLabelsToProfiles {	
	public static void main(String[] args) throws IOException {
//		for (String file: new String[]{"1855923", "3053655"}) {
//			addLabelToProfile(file);
//		}
		addLabelToProfile("data");
	}	
	
	public static void addLabelToProfile(String filename) {
		File outfile = new File("/tmp/" + filename + "-WithLabels");
//		ReadCSVFile input = new ReadCSVFile(PathConfigs.CONCEPT_PROFILES_DIR + filename);
		ReadCSVFile input = new ReadCSVFile("/tmp/cpgp/results/matchscores.txt");
		Iterator<List<String>> it = input.iterator();
		PrintWriter output = null;
		
		List<Integer> columnsToTranslate = Arrays.asList(0, 1);
		
		try {
			output = new PrintWriter(outfile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert input != null && output != null;

		OntologyManager ontologyManager = new OntologyManager();
		Ontology ontology = ontologyManager.fetchClient(ONTOLOGY_NAME);

		List<Integer> genes = readCidFile(HPRD_GENE_CIDS);
		List<Integer> diseases = readCidFile(THESAURUS_DISEASE_CIDS);

		List<Integer> allConceptIDS = new ArrayList<Integer>();
		allConceptIDS.addAll(genes);
		allConceptIDS.addAll(diseases);

		int cnt = 0;
		Map<Integer, String> labels = new HashMap<Integer, String>();
		for(Integer concept: allConceptIDS) {
			if( ++cnt%1000 == 0 ) {
				System.out.println(cnt);
			}
			String label = ontology.getConcept(concept).getTerms().get(0).text;
			labels.put(concept, label);
		}
		System.out.println("Finished loading labels");
		
		cnt = 0;
		while(it.hasNext()) {
			if( ++cnt%1000 == 0 ) {
				System.out.println(cnt);
				output.flush();
			}
			List<String> fields = it.next();
			String result = "";
			for(int i=0; i<fields.size(); i++) {
				if(i!=0) {
					result += ",";
				}
				result += fields.get(i);
				if(columnsToTranslate.contains(i)) {
					Integer concept = Integer.parseInt(fields.get(i));
					//result += "," + ontology.getConcept(concept).getTerms().get(0);
					result += ",\"" + labels.get(concept) + "\"";
				}
			}
			output.println(result);
			
//			Integer conceptA = Integer.parseInt(fields.get(0));
//			Integer conceptB = Integer.parseInt(fields.get(1));
//			String uncertaintyCoeff = fields.get(2);
//			
//			output.println(String.format("\"%s\",\"%s\",%s",
//										 ontology.getConcept(conceptA).getTerms().get(0),
//										 ontology.getConcept(conceptB).getTerms().get(0),
//										 uncertaintyCoeff));
		}
		output.close();
	}
}
