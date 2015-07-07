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

package org.erasmusmc.ontology.ontologyutilities.evaluationScripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ReleasedTerm;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.UMLSGeneChemTokenizer;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails;
import org.erasmusmc.peregrine.disambiguator.DisambiguatorRuleRegistry;
import org.erasmusmc.peregrine.disambiguator.GeneDisambiguator;
import org.erasmusmc.peregrine.disambiguator.HasSynonymRule;
import org.erasmusmc.peregrine.disambiguator.UMLSDisambiguator;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult.ExtraData;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

//Gena and Biocreative evaluation sets for evaluating gene name identification in texts

public class GeneNameIdentificationEvaluation {

	public static void main(String[] args){new GeneNameIdentificationEvaluation();}
	public boolean verbose = true;

	public boolean disambiguatorDetails = true;

	//Pick evaluation set:
	//public String set = "GENAHS"; //Homo Sapiens
	//public String set = "BiocreativeMM"; //Mus Musculus
	//public String set = "BiocreativeDM"; //Drosophila Melanogaster
	//public String set = "BiocreativeSC"; //Saccharomyces Cerevisiae
	public String set = "Biocreative2Training"; //Homo Sapiens  
	//public String set = "Biocreative2Test"; //Homo Sapiens 

	public GeneDisambiguator geneDisambiguator;
	public UMLSDisambiguator umlsDisambiguator;

	//Baseline: 
	//UMLS2006HomologeneV1.6, SBDTokenizer, BCII: Precision=0.744131455399061 Recall=0.8076433121019109 F-measure= 0.7745876603543067

	//UMLS2006HomologeneV1.6, UMLSGeneChemTokenizer, BCII: Precision=0.7582017010935601 Recall=0.7949044585987262 F-measure= 0.7761194029850746
	//Since Feb 2011:Precision=0.7309941520467836 Recall=0.7961783439490446 F-measure= 0.7621951219512195

	//GeneListHumanMouseRatV5_0.ontology, UMLSGeneChemTokenizer, BCII: Precision=0.7676240208877284 Recall=0.7490445859872611 F-measure= 0.7582205029013538
	//UMLS2010ABHomologeneJochemToxV1_3.ontology, UMLSGeneChemTokenizer, BCII: Precision=0.786774628879892 Recall=0.7426751592356687 F-measure= 0.764089121887287


	public GeneNameIdentificationEvaluation(){   
		String normaliserCacheFile = "/home/public/Peregrine/standardNormCache2006.bin";

		indexer = new ConceptPeregrine();

		OntologyFileLoader loader = new OntologyFileLoader();
		//Ontology ontology = loader.load("/home/khettne/Projects/UMLS2010ABHomologeneJochemToxV1_1.ontology");
		//Ontology ontology = loader.load("/home/khettne/Projects/GeneList/GeneListHumanMouseRatV5_0.ontology");
		//Ontology ontology = loader.load("/home/public/thesauri/Homologene_v1_6c.ontology");
		Ontology ontology = loader.load("/home/public/thesauri/UMLS2006Homologene_v1_6c.ontology");
		//Ontology ontology = OCUMLS2006Homologene.constructOntology();
		indexer.setOntology(ontology);
		indexer.normaliser.loadCacheBinary(normaliserCacheFile);

		System.out.println("Releasing thesaurus");
		indexer.tokenizer = new UMLSGeneChemTokenizer();
		long start = System.currentTimeMillis();
		indexer.release();
		System.out.println("Release time: " + (System.currentTimeMillis()-start) + "ms");
		geneDisambiguator = new GeneDisambiguator(indexer, 2000000, Integer.MAX_VALUE);
		evaluate();
	}  

	private void evaluate(){  
		getSettings(set); 
		loadGoldenStandard();
		loadValidIDs();

		int GlobalFP = 0;
		int GlobalTP = 0;
		int GlobalFN = 0;
		for (String file : new ReadTextFile(fileList)){
			Map<String, Occurrence> ids = file2ids.get(file);
			if (ids == null) 
				ids = new HashMap<String, Occurrence>();

			List<String> lines = TextFileUtilities.loadFromFile(file);
			if (verbose){
				System.out.println();
				System.out.println(lines.toString());
			}
			indexer.index(lines.toString());
			if (disambiguatorDetails){
				DisambiguationDetails details = geneDisambiguator.disambiguateWithDetails(indexer);
				outputDetails(details,validIDs, ids, indexer);
			} else geneDisambiguator.disambiguate(indexer);
			//umlsDisambiguator.disambiguate(indexer);

			Set<String> TP = new HashSet<String>();
			Set<String> FN = new HashSet<String>();
			Set<String> FP = new HashSet<String>();
			if (ids != null){
				//Evaluate Peregrine output
				for (ResultConcept concept : indexer.resultConcepts){
					Set<String> geneIDs = ExtractGeneID(geneIDprefix, concept.conceptId, validIDs);
					for (String geneID : geneIDs){
						Occurrence occurrence = ids.get(geneID);
						if (occurrence != null) {
							TP.add(geneID);
							occurrence.found = true;
							if (verbose){
								System.out.print("TP: ");
								displayTerm(indexer, concept, geneID);
							}
						} else {
							if (verbose){
								System.out.print("FP: ");
								displayTerm(indexer, concept, geneID);
							}
							FP.add(geneID);
						}
					}
				}

				for (Map.Entry<String, Occurrence> entry : ids.entrySet()){
					if (!entry.getValue().found){
						FN.add(entry.getKey());
						if (verbose){
							System.out.print("FN: ("+entry.getKey()+") ");
							for (String name : entry.getValue().names) System.out.print(name+";");
							System.out.println();
						}
					}
				}
			}  
			GlobalFP += FP.size();
			GlobalTP += TP.size();
			GlobalFN += FN.size();
			if (verbose)
				System.out.println(file + " True: "+(TP.size()+FN.size())+" TP:"+TP.size()+" FP:"+FP.size()+" FN:"+FN.size());
		}
		double P = (double)GlobalTP / (double)(GlobalFP+GlobalTP);
		double R = (double)GlobalTP / (double)(GlobalTP+GlobalFN);
		double F = (2*P*R) / (P + R);
		System.out.println("TP="+GlobalTP+" FP="+GlobalFP+" FN="+GlobalFN);
		System.out.println("Precision="+P+" Recall="+R+" F-measure= "+F);
	}

	private void loadValidIDs() {
		if (!validIDsFile.equals("")){
			List<String> lines = TextFileUtilities.loadFromFile(validIDsFile);
			validIDs = new HashSet<String>();
			for (String line : lines){
				String[] cols = line.split("_");
				DatabaseID id = new DatabaseID(cols[0], cols[1]);
				validIDs.add(id.ID);
			}
		}
	}

	private void loadGoldenStandard() {
		file2ids = new HashMap<String, Map<String, Occurrence>>();
		List<String> lines = TextFileUtilities.loadFromFile(goldenStandardFile);
		String previousFile = "";
		Map<String, Occurrence> ids = null;
		for (String line : lines){
			String[] cols = line.split("\t");
			if (!cols[0].equals(previousFile)){
				previousFile = cols[0];
				ids = new HashMap<String, Occurrence>();
				file2ids.put(cols[0], ids);
			}

			Occurrence occurrence = new Occurrence();
			for (int i = 2; i < cols.length; i++)
				occurrence.names.add(cols[i]);
			ids.put(cols[1], occurrence);
		}
	}

	private void getSettings(String set) {
		if (set.equals("GENAHS")){
			fileList = "/home/public/datasets/GENA/GenaFilesHS.txt";
			goldenStandardFile = "/home/public/datasets/GENA/GenaGoldenStandardHS.txt";
			geneIDprefix = "EG";  
			validIDsFile = "";
		}

		if (set.equals("BiocreativeMM")){
			System.out.println("Biocreative Mus Musculus test set selected");
			fileList = "/home/public/datasets/Biocreative/BiocreativeFilesMM.txt";   
			goldenStandardFile = "/home/public/datasets/Biocreative/BiocreativeGoldenStandardMM.txt";
			geneIDprefix = "MGI";
			validIDsFile = "";
		}

		if (set.equals("BiocreativeDM")){
			System.out.println("Biocreative Drosophila Melanogaster test set selected");
			fileList = "/home/public/datasets/Biocreative/BiocreativeFilesDM.txt";   
			goldenStandardFile = "/home/public/datasets/Biocreative/BiocreativeGoldenStandardDM.txt";
			geneIDprefix = "FB";
			validIDsFile = "";
		}    

		if (set.equals("BiocreativeSC")){
			System.out.println("Biocreative Saccharomyces Cerevisiae test set selected");
			fileList = "/home/public/datasets/Biocreative/BiocreativeFilesSC.txt";   
			goldenStandardFile = "/home/public/datasets/Biocreative/BiocreativeGoldenStandardSC.txt";
			geneIDprefix = "SGD";
			validIDsFile = "";
		}       

		if (set.equals("Biocreative2Training")){
			System.out.println("Biocreative 2 Training set selected");
			fileList = "/home/public/datasets/Biocreative2/Training/Files.txt";   
			System.out.println("fl: "+fileList);
			goldenStandardFile = "/home/public/datasets/Biocreative2/Training/GoldenStandardHS.txt";
			geneIDprefix = "EG";    
			validIDsFile = "/home/public/datasets/Biocreative2/validIDs.txt";
		}

		if (set.equals("Biocreative2Test")){
			System.out.println("Biocreative 2 Test set selected");
			fileList = "/home/public/datasets/Biocreative2/Test/Files.txt";   
			System.out.println("fl: "+fileList);
			goldenStandardFile = "/home/public/datasets/Biocreative2/Test/GoldenStandardHS.txt";
			geneIDprefix = "EG"; 
			validIDsFile = "/home/public/datasets/Biocreative2/validIDs.txt"; 
		}   
	}

	private static void displayTerm(ConceptPeregrine indexer, ResultConcept concept, String geneID) {
		StringBuffer term = new StringBuffer();
		for (Integer word : concept.terms.get(0).words){
			term.append(indexer.tokenizer.tokens.get(word));
			term.append(" ");
		}
		StringBuilder termIDs = new StringBuilder();
		for (int termID : concept.terms.get(0).term.termId)
			termIDs.append(termID + " ");
		System.out.println(term.toString()+ " termid:"+termIDs.toString() + "\t("+ geneID+")");
	}

	private Set<String> ExtractGeneID(String geneIDprefix, int conceptid, Set<String> valid) {
		Set<String> result = new HashSet<String>();
		List<DatabaseID> databaseIDs = indexer.getOntology().getDatabaseIDsForConcept(conceptid);
		if (databaseIDs != null)
			for (DatabaseID databaseID : databaseIDs)
				if (databaseID.database.equals(geneIDprefix))
					result.add(databaseID.ID);

		filterValidIDs(result, valid);
		return result;
	}

	private void filterValidIDs(Set<String> result, Set<String> valid) {
		if (valid != null){
			Iterator<String> iterator = result.iterator();
			while (iterator.hasNext()){
				if (!valid.contains(iterator.next()))
					iterator.remove();
			}
		}
	}

	private String fileList = "";
	private String goldenStandardFile = "";
	private String geneIDprefix = "";  
	private String validIDsFile = "";
	private ConceptPeregrine indexer;
	private Set<String> validIDs = null;

	private Map<String, Map<String, Occurrence>> file2ids;

	private class Occurrence{
		boolean found = false;
		List<String> names = new ArrayList<String>();
	}

	private void outputDetails(DisambiguationDetails details, Set<String> validIDs, Map<String, Occurrence> correctIDs, ConceptPeregrine indexer2) {    
		Set<Integer> removedCIDs = new HashSet<Integer>();
		for (ResultConcept concept : details.removedConcepts)
			removedCIDs.add(concept.conceptId);


		for (Map.Entry<Integer, List<EvaluationResult>> entry : details.conceptID2EvaluationResult.entrySet()){
			List<String> ids = new ArrayList<String>();

			//Find resultconcept:
			ResultConcept resultConcept = null;
			for (ResultConcept concept : indexer.resultConcepts)
				if (entry.getKey().equals(concept.conceptId)){
					resultConcept = concept;
					break;
				}
			for (ResultConcept concept : details.removedConcepts)
				if (entry.getKey().equals(concept.conceptId)){
					resultConcept = concept;
					break;
				}

			//Find gene IDs:
			for (DatabaseID databaseID : indexer.getOntology().getDatabaseIDsForConcept(entry.getKey()))
				if (databaseID.database.equals(geneIDprefix) && validIDs.contains(databaseID.ID))
					ids.add(databaseID.ID + (correctIDs.containsKey(databaseID.ID)?"+":"-"));
			if (ids.size() == 0)
				continue;

			//Show details:
			System.out.println("Evaluating concept: " + buildTerm(indexer, resultConcept.terms.get(0)) + "("+entry.getKey()+")");
			for (EvaluationResult evaluationResult : entry.getValue()) {
				String ruleName = DisambiguatorRuleRegistry.getRuleName(evaluationResult.ruleID);
				System.out.println(ruleName + " (result: " + evaluationResult.result + ")");
				if (evaluationResult.extraDatas != null)
					for (ExtraData extraData : evaluationResult.extraDatas){
						String typeString = ExtraData.typeStrings[extraData.type];
						System.out.println("- " + typeString + ": " + extraData.value);
					}
				if (ruleName.equals(HasSynonymRule.class.getSimpleName())){
					Set<ReleasedTerm> uniqueTerms = new HashSet<ReleasedTerm>();
					for (ResultTerm term: resultConcept.terms) {
						if (uniqueTerms.add(term.term) && uniqueTerms.size() != 1) {
							System.out.println("- Synonym: " + buildTerm(indexer, term));
							break;
						}
					}
				}
			}
			if (removedCIDs.contains(entry.getKey()))
				System.out.println("Concept "+StringUtilities.join(ids, ", ") + " removed");
			else
				System.out.println("Concept "+StringUtilities.join(ids, ", ") + " kept");
			System.out.println();
		}
	}
	
	private String buildTerm(ConceptPeregrine indexer, ResultTerm resultTerm){
		StringBuffer term = new StringBuffer();
		for (Integer word : resultTerm.words){
			term.append(indexer.tokenizer.tokens.get(word));
			term.append(" ");
		}
		return term.toString();
	}
}
