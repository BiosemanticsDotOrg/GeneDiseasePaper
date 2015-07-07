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


/**
 * 
 * CPGP_BASE_DIR/
 *     conceptid2frequency.txt
 *     groundhogs/MedlineBlaBlav16/
 *     workspace/resources/
 *         Thesaurus2010v16_diseases.cid
 *         HPRD_genes_v16.cid
 *     results/
 *         profiles/[..]
 *         analysis/[..]
 *         matchscores.txt
 *
 */
public class PathConfigsDemo {
	public static final String CPGP_BASE_DIR                 = "/tmp/cpgp/"; // "/media/Meuk/Groundhogs/";
	public static final String CONCEPT_FREQUENCIES_FILENAME  = CPGP_BASE_DIR + "conceptid2frequency.txt";
	public static final String MEDLINE_GROUNDHOG_FOLDER_NAME = "groundhogs/Medline1980till17Jul2014_UMLS2010ABHomologeneJochemToxV1_6/";

	public static final String CONCEPT_IDS_DIR       = CPGP_BASE_DIR + "workspace/resources/";
    public static final String THESAURUS_DISEASE_CIDS = CONCEPT_IDS_DIR + "Thesaurus2010v16_diseases.cid";
    public static final String OMIM_DISEASE_CIDS = CONCEPT_IDS_DIR + "OMIM_diseases.cid";	
	public static final String HPRD_GENE_CIDS    = CONCEPT_IDS_DIR + "HPRD_genes_v16.cid";
	public static final String OMIM_GENE_CIDS    = CONCEPT_IDS_DIR + "OMIM_genes_v16.cid";
	
	public static final String RESULTS_BASE_DIR     = CPGP_BASE_DIR + "results/";
	public static final String CONCEPT_PROFILES_DIR = RESULTS_BASE_DIR + "profiles/";
	public static final String ANALYSIS_DIR         = RESULTS_BASE_DIR + "analysis/";
	public static final String MATCH_SCORE_FILENAME = RESULTS_BASE_DIR + "matchscores.txt";
	
	public static final String ONTOLOGY_NAME = "UMLS2010ABHomologeneJochemToxV1_6";

}
