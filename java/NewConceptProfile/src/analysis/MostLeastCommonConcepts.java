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
import static KnowledgeTransfer.ConceptProfileUtil.loadConceptFrequencies;
import static KnowledgeTransfer.ConceptProfileUtil.readCidFile;
import static KnowledgeTransfer.PathConfigs.CONCEPT_FREQUENCIES_FILENAME;
import static KnowledgeTransfer.PathConfigs.HPRD_GENE_CIDS;
import static KnowledgeTransfer.PathConfigs.THESAURUS_DISEASE_CIDS;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;

import KnowledgeTransfer.PathConfigs;

public class MostLeastCommonConcepts {

	private static final int MAX_ITEMS = 25;
	
	public static void main(String[] args) {
		OntologyManager ontologyManager = new OntologyManager();
		Ontology ontology = ontologyManager.fetchClient(PathConfigs.ONTOLOGY_NAME);

		PrintWriter output = null;
		try {
			output = new PrintWriter(PathConfigs.RESULTS_BASE_DIR + "BestAndWorstConcepts.tsv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final HashMap<Integer,Integer> conceptid2frequency = loadConceptFrequencies(CONCEPT_FREQUENCIES_FILENAME);
		List<Integer> genes    = readCidFile(HPRD_GENE_CIDS);
		List<Integer> diseases = readCidFile(THESAURUS_DISEASE_CIDS);

		Collections.sort(genes, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return getCidFreq(o1, conceptid2frequency).compareTo(getCidFreq(o2, conceptid2frequency));
			}
		});
		
		for(Integer i=0; i<MAX_ITEMS; i++) {
			Integer most = genes.get(genes.size()-i-1);
			Integer least = genes.get(i);
			output.print(String.format("%10s\t %6d\t", ontology.getConcept(least).getTerms().get(0), getCidFreq(least, conceptid2frequency)));
			output.println(String.format("%10s\t %6d", ontology.getConcept(most).getTerms().get(0), getCidFreq(most, conceptid2frequency)));
		}
	
		output.println("");
		
		Collections.sort(diseases, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return getCidFreq(o1, conceptid2frequency).compareTo(getCidFreq(o2, conceptid2frequency));
			}
		});
		
		for(Integer i=0; i<MAX_ITEMS; i++) {
			Integer most = diseases.get(diseases.size()-i-1);
			Integer least = diseases.get(i);
			output.print(String.format("%40s\t %6d\t", ontology.getConcept(least).getTerms().get(0), getCidFreq(least, conceptid2frequency)));
			output.println(String.format("%40s\t %6d", ontology.getConcept(most).getTerms().get(0), getCidFreq(most, conceptid2frequency)));
		}
		
		output.close();
	}			

	private static Integer getCidFreq(Integer cid, Map<Integer,Integer> conceptid2frequency) {
		Integer result = conceptid2frequency.get(cid);
		if(result == null)
			result = 0;
		return result;
	}
}
