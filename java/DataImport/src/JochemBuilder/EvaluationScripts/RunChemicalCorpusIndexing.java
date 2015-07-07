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

package JochemBuilder.EvaluationScripts;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;

public class RunChemicalCorpusIndexing {
	//	Baseline: UMLS2010ABHomologeneJochemToxV1_5.ontology (CHEMICAL voc): 
	//	Precision: 0.6218375499334221,	Recall: 0.38723051409618575
	//	Recall IUPAC: 	0.20460358056265984
//	Recall PART: 	0.03260869565217391
//	Recall SUM: 	0.2653061224489796
//	Recall TRIV: 	0.7822784810126582
//	Recall ABB: 	0.2236024844720497
//	Recall FAM: 	0.1414141414141414

	//BaselineUMLS2010ABHomologeneJochemToxV1_5.ontology (CHEMICAL voc && !GENE voc):
//	 Precision: Precision: 0.6923076923076923, Recall: 0.3805970149253731
//	Recall IUPAC: 	0.20460358056265984
//	Recall PART: 	0.03260869565217391
//	Recall SUM: 	0.2653061224489796
//	Recall TRIV: 	0.7772151898734178
//	Recall ABB: 	0.2111801242236025
//	Recall FAM: 	0.10101010101010101
	
	public static String home = "/home/khettne/Projects/Jochem/Indexing/";
	public static String ontologyFile = "/home/khettne/temp/UMLS2010ABHomologeneJochemToxV1_5.ontology";
	public static String fileWithfilesToIndex = home+"files_to_index.txt";
	
	public static String indexresultFileName = home+"index_result.txt";
	public static String falsePositivePositionsFileName = home+"falsePosPositions.txt";
	public static String falseNegativePositionsFileName = home+"falseNegPositions.txt";
	public static String truePositivePositionsFileName = home+"truePosPositions.txt";
	
	public static void main(String[] args) {
		System.out.println("Starting script: "+StringUtilities.now());
		IndexChemicalCorpus index = new IndexChemicalCorpus();
		
		System.out.println("Processing thesaurus. "+StringUtilities.now());
		OntologyFileLoader loader = new OntologyFileLoader();
		OntologyStore ontology = loader.load(ontologyFile);
		index.run(home, fileWithfilesToIndex, indexresultFileName, falsePositivePositionsFileName, falseNegativePositionsFileName, truePositivePositionsFileName, ontology);
		
		System.out.println("Done! "+StringUtilities.now());
	}
}
