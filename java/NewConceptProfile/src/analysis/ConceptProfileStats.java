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
import static KnowledgeTransfer.ConceptProfileUtil.readConceptProfile;
import static KnowledgeTransfer.PathConfigs.CONCEPT_PROFILES_DIR;
import static KnowledgeTransfer.PathConfigs.THESAURUS_DISEASE_CIDS;
import static KnowledgeTransfer.PathConfigs.HPRD_GENE_CIDS;
import static KnowledgeTransfer.PathConfigs.RESULTS_BASE_DIR;
import static KnowledgeTransfer.PathConfigs.CPGP_BASE_DIR;
import static KnowledgeTransfer.PathConfigs.MEDLINE_GROUNDHOG_FOLDER_NAME;
import static com.google.common.collect.Sets.intersection;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.utilities.WriteCSVFile;

/**
 * 
 * @author Eelke van der horst
 * @author Mark thompson
 * @author Rajaram Kaliyaperumal
 * 
 * @version 0.1.1
 * @since 27-August-2014
 */
public class ConceptProfileStats {
	private Groundhog documentProfilesGroundhog;
	private final List<Integer> genes = readCidFile(HPRD_GENE_CIDS);
	private final List<Integer> diseases = readCidFile(THESAURUS_DISEASE_CIDS);
	private final String ANALYSIS_FOLDER_NAME = RESULTS_BASE_DIR + "analysis2/";

	public ConceptProfileStats() {

		GroundhogManager groundhogmanager = new GroundhogManager(CPGP_BASE_DIR);
		documentProfilesGroundhog = groundhogmanager
				.getGroundhog(MEDLINE_GROUNDHOG_FOLDER_NAME);

	}

	/**
	 * <p>
	 * Genes concept profiles stats (Figure d:)
	 * </p>
	 */
	public void geneStats() {

		WriteCSVFile output = new WriteCSVFile(ANALYSIS_FOLDER_NAME
				+ "geneConceptProfileStats");
		output.write(Arrays.asList("#gene", "conceptProfileLength",
				"NoOfPMids4Gene"));

		System.out.println("Genes stats");

		List<Integer> cpLengths = new ArrayList<Integer>();
		
		for (Integer gene : genes) {
			File file = new File((CONCEPT_PROFILES_DIR + String.valueOf(gene)));
			if (file.exists()) {
				Map<Integer, Double> cpContent = readConceptProfile((CONCEPT_PROFILES_DIR + String
						.valueOf(gene)));
				Set<Integer> pmids = documentProfilesGroundhog
						.getRecordIDsForConcept(gene);
				output.write(Arrays.asList(String.valueOf(gene),
						String.valueOf(cpContent.size()),
						String.valueOf(pmids.size())));
				cpLengths.add(cpContent.size());

			}
		}
		
		Collections.sort(cpLengths);
		System.out.println(String.format("gene number of profiles: %d (%d total)", cpLengths.size(), genes.size()));
		System.out.println(String.format("gene median profile length: %d (min: %d, max: %d)", cpLengths.get(cpLengths.size()/2), cpLengths.get(0), cpLengths.get(cpLengths.size()-1) ));

		output.close();

	}

	/**
	 * <p>
	 * Diseases concept profiles stats (Figure e:)
	 * </p>
	 */
	public void diseasesStats() {
		
		WriteCSVFile output = new WriteCSVFile(ANALYSIS_FOLDER_NAME
				+ "diseasesConceptProfileStats");
		output.write(Arrays.asList("#disease", "conceptProfileLength",
				"NoOfPMids4Disease"));

		System.out.println("Diseases stats");

		List<Integer> cpLengths = new ArrayList<Integer>();
		
		for (Integer disease : diseases) {
			File file = new File(
					(CONCEPT_PROFILES_DIR + String.valueOf(disease)));
			if (file.exists()) {
				Map<Integer, Double> cpContent = readConceptProfile((CONCEPT_PROFILES_DIR + String
						.valueOf(disease)));
				Set<Integer> pmids = documentProfilesGroundhog
						.getRecordIDsForConcept(disease);
				output.write(Arrays.asList(String.valueOf(disease),
						String.valueOf(cpContent.size()),
						String.valueOf(pmids.size())));
				cpLengths.add(cpContent.size());
			}

		}
		
		Collections.sort(cpLengths);
		System.out.println(String.format("disease number of profiles: %d (%d total)", cpLengths.size(), diseases.size()));
		System.out.println(String.format("disease median profile length: %d (min: %d, max: %d)", cpLengths.get(cpLengths.size()/2), cpLengths.get(0), cpLengths.get(cpLengths.size()-1) ));

		output.close();

	}

	/**
	 * <p>
	 * Gene and diseases concept profiles overlapping concepts (Figure f:)
	 * </p>
	 */
	public void geneDiseaseOverlappingStats() {

		WriteCSVFile output = new WriteCSVFile(ANALYSIS_FOLDER_NAME
				+ "gene-diseases-ConceptProfile-overlapping-Stats");

		// gene_id disease_id
		// Filter list of genes for having CP.
		// Filter list of diseases for having CP.
		//
		output.write(Arrays.asList("#gene", "disease", "overlappingConcepts"));

		int pairs = 0;

		System.out.println("Overlapping stats");

		List<Entry<Integer, Set<Integer>>> diseaseCPs = new ArrayList<Entry<Integer, Set<Integer>>>();

		for (Integer disease : diseases) {

			File fileDisease = new File(
					(CONCEPT_PROFILES_DIR + String.valueOf(disease)));

			if (fileDisease.exists()) {
				Map<Integer, Double> diseaseCPContent = readConceptProfile((CONCEPT_PROFILES_DIR + String
						.valueOf(disease)));
				diseaseCPs.add(new AbstractMap.SimpleImmutableEntry<Integer, Set<Integer>>(disease,
						diseaseCPContent.keySet()));
			}
		}

		for (Integer gene : genes) {
			File fileGene = new File(
					(CONCEPT_PROFILES_DIR + String.valueOf(gene)));

			if (fileGene.exists()) {
				Map<Integer, Double> geneCPContent = readConceptProfile(fileGene
						.getAbsolutePath());
				Set<Integer> geneConcept = geneCPContent.keySet();

				for (Entry<Integer, Set<Integer>> diseaseEntry : diseaseCPs) {
					Set<Integer> diseaseConcepts = diseaseEntry.getValue();
					Integer disease = diseaseEntry.getKey();
					pairs++;

					int commonConcepts = 0;

					commonConcepts = intersection(geneConcept, diseaseConcepts)
							.size();

					output.write(Arrays.asList(String.valueOf(gene),
							String.valueOf(disease),
							String.valueOf(commonConcepts)));

					if (pairs % 100000 == 0) {
						System.out.println("Computation done for " + pairs
								+ " pairs");
					}
				}
			}
		}

		output.close();

	}

	public static void main(String args[]) {
		ConceptProfileStats test = new ConceptProfileStats();
		 test.geneStats();
		 test.diseasesStats();
//		test.geneDiseaseOverlappingStats();
	}
}
