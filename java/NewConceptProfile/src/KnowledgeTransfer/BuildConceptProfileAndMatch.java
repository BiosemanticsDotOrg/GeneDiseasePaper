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


package KnowledgeTransfer;

import static KnowledgeTransfer.ConceptProfileUtil.loadConceptFrequencies;
import static KnowledgeTransfer.ConceptProfileUtil.readCidFile;
import static KnowledgeTransfer.ConceptProfileUtil.readConceptProfilesByID;
import static KnowledgeTransfer.PathConfigs.CONCEPT_FREQUENCIES_FILENAME;
import static KnowledgeTransfer.PathConfigs.CONCEPT_PROFILES_DIR;
import static KnowledgeTransfer.PathConfigs.CPGP_BASE_DIR;
import static KnowledgeTransfer.PathConfigs.HPRD_GENE_CIDS;
import static KnowledgeTransfer.PathConfigs.MATCH_SCORE_FILENAME;
import static KnowledgeTransfer.PathConfigs.MEDLINE_GROUNDHOG_FOLDER_NAME;
import static KnowledgeTransfer.PathConfigs.THESAURUS_DISEASE_CIDS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.ConceptVectorRecord;
import org.erasmusmc.utilities.WriteCSVFile;

import textmining.myprofiles.ContingencyTable;

/**
 * @author
 * 
 * Create concept profiles for all gene and disease concepts and calculate a match score for
 *  each gene-disease combination (if both the gene and disease have a profile).<br><br>
 * 
 * INPUT REQUIRED 1: concept frequencies
 *  {@link KnowledgeTransfer.ConceptFrequencies}<br><br>
 * 
 * INPUT REQUIRED 2: medline index
 *  A BerkeleyDB (aka groundhog) that provides two lookup functions:<br>
 *   1) getRecordIDsForConcept(conceptid): return a list of all PMIDs in which this concept occurs<br>
 *   2) get(pmid): get a list of concepts that occur in this PMID<br><br>
 * 
 * OUTPUT 1: concept profiles (each file named with conceptIdA)<br>
 *  format: <code>conceptIdB , weight</code><br>
 *  example row: <code>35203,4.4286979627989374E-5</code><br><br>
 *  
 * OUTPUT 2: match scores<br>
 *  format: <code>conceptIdA> , conceptIdB , match score</code><br>
 *  example row: <code></code>
 *  
 */
public class BuildConceptProfileAndMatch {
	
	static final int TOTAL_NUMBER_OF_ABSTRACTS = 17062496;
	
	static Groundhog documentProfilesGroundhog;
	static HashMap<Integer, Integer> conceptid2frequency = new HashMap<Integer, Integer>();

	
	public static void main(String[] args) {
		conceptid2frequency = loadConceptFrequencies(CONCEPT_FREQUENCIES_FILENAME);

		// Declareer een medline groundhog volgens de legacy code.
		GroundhogManager groundhogmanager2 = new GroundhogManager(CPGP_BASE_DIR);
		documentProfilesGroundhog = groundhogmanager2.getGroundhog(MEDLINE_GROUNDHOG_FOLDER_NAME);

		List<Integer> genes = readCidFile(HPRD_GENE_CIDS);
		List<Integer> diseases = readCidFile(THESAURUS_DISEASE_CIDS);

		// System.out.println((new java.util.Date()) +
		// " start creating concept profiles");
		List<Integer> allConceptIDS = new ArrayList<Integer>();
		allConceptIDS.addAll(genes);
		allConceptIDS.addAll(diseases);

		generateAndWriteConceptProfiles(allConceptIDS, conceptid2frequency);

		Map<Integer, Map<Integer, Double>> conceptProfiles = readConceptProfilesByID(
				allConceptIDS, CONCEPT_PROFILES_DIR);

		writeMatchScores(genes, diseases, conceptProfiles);

		System.out.println((new java.util.Date()) + " Done!");

		// TextFileUtilities.saveToFile(conceptprofile, fileout);
	}

	public static void writeMatchScores(List<Integer> genes,
			List<Integer> diseases,
			Map<Integer, Map<Integer, Double>> conceptProfiles) {
		// List<ConceptPairData> result = new ArrayList<ConceptPairData>();
		int cnt = 0;

		System.out.println((new java.util.Date())
				+ " writing match scores to: " + MATCH_SCORE_FILENAME);

		WriteCSVFile output = new WriteCSVFile(MATCH_SCORE_FILENAME);

		for (Integer gene : genes) {
			for (Integer dis : diseases) {
				if ((++cnt % 10000) == 0) {
					System.out.println((new java.util.Date())
							+ " number of profiles matched: " + cnt);
				}
				// //3063788,1834821,NaN
				// if (gene == 3063788 && dis == 1834821)
				// {
				// System.out.println("Stop! Hammer time!");
				// }
				Map<Integer, Double> cp1 = conceptProfiles.get(gene);
				Map<Integer, Double> cp2 = conceptProfiles.get(dis);
				double ip = SingleMatchscore.InnerProduct(cp1, cp2);

				output.write(Arrays.asList(String.valueOf(gene),
						String.valueOf(dis), String.valueOf(ip)));

				// result.add(new ConceptPairData(gene, dis, ip));
			}
		}

		output.close();
		// return result;
	}

	public static Map<Integer, Map<Integer, Double>> generateAndWriteConceptProfiles(
			List<Integer> cids, HashMap<Integer, Integer> cid2count) {
		Map<Integer, Map<Integer, Double>> result = new HashMap<Integer, Map<Integer, Double>>();

		int cnt = 0;
		for (Integer cid : cids) {
			if ((++cnt % 10) == 0) {
				System.out.println((new java.util.Date())
						+ " number of profiles created: " + cnt);
			}
			Map<Integer, Double> cp = generateConceptProfile(cid, cid2count);
			if (cp != null) {
				result.put(cid, cp);
			}
		}

		return result;
	}

	
	public static Map<Integer, Double> generateConceptProfile(
			Integer conceptid, HashMap<Integer, Integer> cid2count) {
		HashMap<Integer, Double> result = new HashMap<Integer, Double>();
		SortedIntListSet pmids = documentProfilesGroundhog
				.getRecordIDsForConcept(conceptid);

		if (pmids.size() < 6 || pmids.size() > 140000)
			return null;

		HashMap<Integer, Integer> concept2frequency = new HashMap<Integer, Integer>();
		for (Integer pmid : pmids) {

			ConceptVectorRecord cvr = documentProfilesGroundhog.get(pmid);
			ConceptVector cv = cvr.getConceptVector();
			IntList w = cv.values.keys();
			for (Integer concept : w) {
				Integer frequency = concept2frequency.get(concept);
				if (frequency == null)
					frequency = 0;
				frequency++;
				concept2frequency.put(concept, frequency);
			}
		}
		int frequencyA = pmids.size();
		for (Integer key : concept2frequency.keySet()) {
			int frequencyB = cid2count.get(key);
			double M11 = concept2frequency.get(key);
			double M01 = frequencyB - M11;
			double M10 = frequencyA - M11;
			double M00 = TOTAL_NUMBER_OF_ABSTRACTS - M11 - M01 - M10; 
			double uc = ContingencyTable.UncertaintyCoefficient(M11, M10, M01, M00);
			result.put(key, uc);
		}

		WriteCSVFile output = new WriteCSVFile(CONCEPT_PROFILES_DIR
				+ Integer.toString(conceptid));

		for (Integer concept : result.keySet()) {
			output.write(Arrays.asList(Integer.toString(concept),
					Double.toString(result.get(concept))));
		}

		output.close();

		return result;
	}

}
