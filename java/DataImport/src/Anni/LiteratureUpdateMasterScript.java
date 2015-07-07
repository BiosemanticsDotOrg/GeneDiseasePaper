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

package Anni;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import org.erasmusmc.applications.indexer.IndexerMainForGroundhog;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.groundhog.GroundhogStatistics;
import org.erasmusmc.medline.FetchPMIDsFromOnlinePubmed;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class LiteratureUpdateMasterScript {
	//Settings:
//	public static String beginning =      "1980/01/01"; //yyyy/mm/dd
	public static String beginning =      "1990/07/16"; //yyyy/mm/dd
	public static String now =            "1990/07/17"; //yyyy/mm/dd
	public static int geneCUIcutoff = 3000000; //Gene conceptIDs start above this number
	public static int chemCUIcutoff = 4000000; //Chemical conceptIDs start above this number
	public static String temp = "/tmp/"; //Temp folder. Make sure it is empty before proceeding
//	public static Set<Integer> removeSemTypes = getRemoveSemTypes();
	public static int maxPMIDsPerConcept = 10000; //Truncate lists of PMIDs per concept to this number

	//Sources:
	public static String ontologyName = "UMLS2010ABHomologeneJochemToxV1_6";
	public static String geneOntologyFile = "/home/reinout/AnniUpdate/GO/go_20120714-assocdb.rdf-xml"; //Download latest file from http://archive.geneontology.org/latest-lite/

	//Targets:
	public static String groundhogRoot = "/tmp/";
	public static String groundhogName =  "Medline1980till17Jul2012_UMLS2010ABHomologeneJochemToxV1_6-test29";
	public static String genegroundhogName =  "GeneGroundhog_Anni_July2012";
	public static String integerSetStore = "/home/reinout/AnniUpdate/IntegerSetStores/IntegerSetStore_Anni_July2012/";
	public static String conceptProfilesGroundhog =  "ConceptProfiles_Anni_July2012";
	public static String cooccurrenceDBName = "CoOccurrence_Anni_July2012"; //Stored in groundhog folder
	public static String titlesDatabase = "MedlineTitles_Anni_Jxuly2012"; //Stored on MySQL server

	public static void main(String[] args){
		System.out.println("start");
		OntologyManager ontologyManager = new OntologyManager();
		Ontology ontology = ontologyManager.fetchClient(ontologyName);
		
		System.out.println(StringUtilities.now() + "\tCreating complete groundhog");

		//for toxicology subset only:
//		String query = beginning+"[PDat] : "+now+"[PDat] AND tox[sb] NOT Comment[ptyp] NOT Editorial[ptyp] NOT News[ptyp] NOT Historical Article[ptyp] NOT Congresses[ptyp] NOT Biography[ptyp] NOT Newspaper Article[ptyp] NOT Guideline[ptyp] NOT Practice Guideline[ptyp] NOT Interview[ptyp] NOT Bibliography[ptyp] NOT Legal Cases[ptyp] NOT Lectures[ptyp] NOT Consensus Development Conference[ptyp] NOT Addresses[ptyp] NOT Clinical Conference[ptyp] NOT Patient Education Handout[ptyp] NOT Directory[ptyp] NOT Technical Report[ptyp] NOT Festschrift[ptyp] NOT Retraction of Publication[ptyp] NOT Retracted Publication[ptyp] NOT Duplicate Publication[ptyp] NOT Scientific Integrity Review[ptyp] NOT Published Erratum[ptyp] NOT Consensus Development Conference, NIH[ptyp] NOT Periodical Index[ptyp] NOT Dictionary[ptyp] NOT Legislation[ptyp] NOT Government Publications[ptyp]";
		
		//for human, mouse and rat:
//		String query = beginning+"[PDat] : "+now+"[PDat]  ((humans[MeSH Terms]) OR (rats[MeSH Terms]) OR (mice[MeSH Terms])) NOT Comment[ptyp] NOT Editorial[ptyp] NOT News[ptyp] NOT Historical Article[ptyp] NOT Congresses[ptyp] NOT Biography[ptyp] NOT Newspaper Article[ptyp] NOT Guideline[ptyp] NOT Practice Guideline[ptyp] NOT Interview[ptyp] NOT Bibliography[ptyp] NOT Legal Cases[ptyp] NOT Lectures[ptyp] NOT Consensus Development Conference[ptyp] NOT Addresses[ptyp] NOT Clinical Conference[ptyp] NOT Patient Education Handout[ptyp] NOT Directory[ptyp] NOT Technical Report[ptyp] NOT Festschrift[ptyp] NOT Retraction of Publication[ptyp] NOT Retracted Publication[ptyp] NOT Duplicate Publication[ptyp] NOT Scientific Integrity Review[ptyp] NOT Published Erratum[ptyp] NOT Consensus Development Conference, NIH[ptyp] NOT Periodical Index[ptyp] NOT Dictionary[ptyp] NOT Legislation[ptyp] NOT Government Publications[ptyp]";

		//for the whole Medline: (commented out to time thesaurus loading)
		String query = beginning+"[PDat] : "+now+"[PDat] NOT Comment[ptyp] NOT Editorial[ptyp] NOT News[ptyp] NOT Historical Article[ptyp] NOT Congresses[ptyp] NOT Biography[ptyp] NOT Newspaper Article[ptyp] NOT Guideline[ptyp] NOT Practice Guideline[ptyp] NOT Interview[ptyp] NOT Bibliography[ptyp] NOT Legal Cases[ptyp] NOT Lectures[ptyp] NOT Consensus Development Conference[ptyp] NOT Addresses[ptyp] NOT Clinical Conference[ptyp] NOT Patient Education Handout[ptyp] NOT Directory[ptyp] NOT Technical Report[ptyp] NOT Festschrift[ptyp] NOT Retraction of Publication[ptyp] NOT Retracted Publication[ptyp] NOT Duplicate Publication[ptyp] NOT Scientific Integrity Review[ptyp] NOT Published Erratum[ptyp] NOT Consensus Development Conference, NIH[ptyp] NOT Periodical Index[ptyp] NOT Dictionary[ptyp] NOT Legislation[ptyp] NOT Government Publications[ptyp]";
		saveRestrictedPMIDsToFile(query, temp+"Restricted1980tillnow.PMIDs"); 
		indexMedline(groundhogName, ontology, temp+"Restricted1980tillnow.PMIDs");

//		//System.out.println(StringUtilities.now() + "\tCreating gene groundhog");
//		
//		//for all species:
//		//query = "(protein OR gene) AND (mammal OR melanogaster OR gallus OR elegans OR rerio OR cerevisiae OR coli) AND " +beginning+"[PDat] : "+now+"[PDat] NOT plant NOT Comment[ptyp] NOT Editorial[ptyp] NOT News[ptyp] NOT Historical Article[ptyp] NOT Congresses[ptyp] NOT Biography[ptyp] NOT Newspaper Article[ptyp] NOT Guideline[ptyp] NOT Practice Guideline[ptyp] NOT Interview[ptyp] NOT Bibliography[ptyp] NOT Legal Cases[ptyp] NOT Lectures[ptyp] NOT Consensus Development Conference[ptyp] NOT Addresses[ptyp] NOT Clinical Conference[ptyp] NOT Patient Education Handout[ptyp] NOT Directory[ptyp] NOT Technical Report[ptyp] NOT Festschrift[ptyp] NOT Retraction of Publication[ptyp] NOT Retracted Publication[ptyp] NOT Duplicate Publication[ptyp] NOT Scientific Integrity Review[ptyp] NOT Published Erratum[ptyp] NOT Consensus Development Conference, NIH[ptyp] NOT Periodical Index[ptyp] NOT Dictionary[ptyp] NOT Legislation[ptyp] NOT Government Publications[ptyp]";
//		
//		//for mammals only:
//		//query = "(protein OR gene) AND mammals AND " +beginning+"[PDat] : "+now+"[PDat] NOT Comment[ptyp] NOT Editorial[ptyp] NOT News[ptyp] NOT Historical Article[ptyp] NOT Congresses[ptyp] NOT Biography[ptyp] NOT Newspaper Article[ptyp] NOT Guideline[ptyp] NOT Practice Guideline[ptyp] NOT Interview[ptyp] NOT Bibliography[ptyp] NOT Legal Cases[ptyp] NOT Lectures[ptyp] NOT Consensus Development Conference[ptyp] NOT Addresses[ptyp] NOT Clinical Conference[ptyp] NOT Patient Education Handout[ptyp] NOT Directory[ptyp] NOT Technical Report[ptyp] NOT Festschrift[ptyp] NOT Retraction of Publication[ptyp] NOT Retracted Publication[ptyp] NOT Duplicate Publication[ptyp] NOT Scientific Integrity Review[ptyp] NOT Published Erratum[ptyp] NOT Consensus Development Conference, NIH[ptyp] NOT Periodical Index[ptyp] NOT Dictionary[ptyp] NOT Legislation[ptyp] NOT Government Publications[ptyp]";
//
//		//for human, mouse and rat:
////		query = "(protein OR gene) AND ((humans[MeSH Terms]) OR (rats[MeSH Terms]) OR (mice[MeSH Terms])) AND " +beginning+"[PDat] : "+now+"[PDat] NOT Comment[ptyp] NOT Editorial[ptyp] NOT News[ptyp] NOT Historical Article[ptyp] NOT Congresses[ptyp] NOT Biography[ptyp] NOT Newspaper Article[ptyp] NOT Guideline[ptyp] NOT Practice Guideline[ptyp] NOT Interview[ptyp] NOT Bibliography[ptyp] NOT Legal Cases[ptyp] NOT Lectures[ptyp] NOT Consensus Development Conference[ptyp] NOT Addresses[ptyp] NOT Clinical Conference[ptyp] NOT Patient Education Handout[ptyp] NOT Directory[ptyp] NOT Technical Report[ptyp] NOT Festschrift[ptyp] NOT Retraction of Publication[ptyp] NOT Retracted Publication[ptyp] NOT Duplicate Publication[ptyp] NOT Scientific Integrity Review[ptyp] NOT Published Erratum[ptyp] NOT Consensus Development Conference, NIH[ptyp] NOT Periodical Index[ptyp] NOT Dictionary[ptyp] NOT Legislation[ptyp] NOT Government Publications[ptyp]";
//		
////		for mammals in toxicology only:
////		query = "(protein OR gene) AND mammals AND " +beginning+"[PDat] : "+now+"[PDat] AND tox[sb] NOT Comment[ptyp] NOT Editorial[ptyp] NOT News[ptyp] NOT Historical Article[ptyp] NOT Congresses[ptyp] NOT Biography[ptyp] NOT Newspaper Article[ptyp] NOT Guideline[ptyp] NOT Practice Guideline[ptyp] NOT Interview[ptyp] NOT Bibliography[ptyp] NOT Legal Cases[ptyp] NOT Lectures[ptyp] NOT Consensus Development Conference[ptyp] NOT Addresses[ptyp] NOT Clinical Conference[ptyp] NOT Patient Education Handout[ptyp] NOT Directory[ptyp] NOT Technical Report[ptyp] NOT Festschrift[ptyp] NOT Retraction of Publication[ptyp] NOT Retracted Publication[ptyp] NOT Duplicate Publication[ptyp] NOT Scientific Integrity Review[ptyp] NOT Published Erratum[ptyp] NOT Consensus Development Conference, NIH[ptyp] NOT Periodical Index[ptyp] NOT Dictionary[ptyp] NOT Legislation[ptyp] NOT Government Publications[ptyp]";
//		
//		//saveGenePMIDsToFile(query, temp+"RestrictedGene1980tillnow.PMIDs");
//		//indexMedline(genegroundhogName, ontology,temp+"RestrictedGene1980tillnow.PMIDs");
//
//		//System.out.println(StringUtilities.now() + "\tCreating gene integersetstore");
//		//saveGeneCUIsToFile(ontology, temp+"Genes.CUIs", temp+"NonGenes.CUIs");
//		//CreateIntegerSetStoreFromGroundhog.create(groundhogRoot+genegroundhogName, temp+"IntegerSetStoreGene/", temp+"Genes.CUIs");
//
//		//System.out.println(StringUtilities.now() + "\tCreating GO integersetstore");
//		//GeneOntology geneOntology = new GeneOntology(geneOntologyFile);
//		//geneOntology.dumpPMIDs(ontology, temp+"IntegerSetStoreGO/");
//		
//		//code for creating UMLS integersetstore gives an error message that the store can't be closed. Need to stop and run from this point.
//		//System.out.println(StringUtilities.now() + "\tCreating UMLS integersetstore");
//		//filterCUIs(temp+"NonGenes.CUIs", temp+"NonGenesFiltered.CUIs", ontology);
//		//CreateIntegerSetStoreFromGroundhog.create(groundhogRoot + groundhogName, temp+"IntegerSetStoreUMLS/", temp+"NonGenesFiltered.CUIs");
//
//		//System.out.println(StringUtilities.now() + "\tCombining integersetstores");
//		//MergeIntegerSetStores mergeIntegerSetStores = new MergeIntegerSetStores(integerSetStore);
//		//mergeIntegerSetStores.sizeUpperCutoff = maxPMIDsPerConcept;
//		//mergeIntegerSetStores.addStore(temp+"IntegerSetStoreGene/");
//		//mergeIntegerSetStores.addStore(temp+"IntegerSetStoreGO/");
//		//mergeIntegerSetStores.addStore(temp+"IntegerSetStoreUMLS/");
//
//		//System.out.println(StringUtilities.now() + "\tGenerating concept profiles");
//		//GenerateConceptProfilesFromIntegerSetStore generator = new GenerateConceptProfilesFromIntegerSetStore();
//		//generator.groundhogRoot = groundhogRoot;
//		//generator.sourceGroundhogName = groundhogName;
//		//generator.targetGroundhogName = conceptProfilesGroundhog;
//		//generator.integerSetStoreFilename = integerSetStore;
//		//generator.groundhogStatisticsFilename = groundhogRoot + groundhogName + "/GroundhogStatistics.txt";
//		//generator.maximumNumberOfPmidsForCP = maxPMIDsPerConcept;
//		//generator.run();
//
//		//System.out.println(StringUtilities.now() + "\tCreate cooccurrence database");
//		//CooccurrenceDatabase cooccurrenceDB = new CooccurrenceDatabase(groundhogRoot + cooccurrenceDBName);
//		//GroundhogManager groundhogManager = new GroundhogManager(groundhogRoot);
//		//Groundhog groundhog = groundhogManager.getGroundhog(groundhogName);
//		//cooccurrenceDB.makeFromGroundhog(groundhog);
//
//		//System.out.println(StringUtilities.now() + "\tCreate medline titles database");
//		//CreateTitlesDatabase.create(titlesDatabase, temp+"Restricted1980tillnow.PMIDs");

		System.out.println(StringUtilities.now() + "\tDone!");

	}

//	private static void filterCUIs(String sourceCUIfile, String targetCUIfile, Ontology ontology) {
//		Set<Integer> semFilter = OntologyUtilities.getSemanticFilter(ontology, removeSemTypes);
//		ReadTextFile in = new ReadTextFile(sourceCUIfile);
//		WriteTextFile out = new WriteTextFile(targetCUIfile);
//		for (String line : in){
//			Integer cui = Integer.parseInt(line);
//			if (!semFilter.contains(cui))
//				out.writeln(cui.toString());
//		}
//		out.close();
//	}
//	private static void saveGeneCUIsToFile(Ontology ontology, String geneFilename, String nongeneFilename) {
//		Iterator<Concept> iterator = ontology.getConceptIterator();
//		WriteTextFile geneout = new WriteTextFile(geneFilename);
//		WriteTextFile nongeneout = new WriteTextFile(nongeneFilename);
//		while (iterator.hasNext()){
//			int cui = iterator.next().getID();
//			if (cui > geneCUIcutoff && cui < chemCUIcutoff){
//				geneout.writeln(Integer.toString(cui));
//			} else
//				nongeneout.writeln(Integer.toString(cui));
//		}
//		geneout.close();  
//		nongeneout.close();
//	}

	protected static void indexMedline(String groundhogFolder, Ontology ontology, String pmidFile){
		System.out.println(StringUtilities.now() + "\tIndexing Medline");
		GroundhogManager groundhogManager = new GroundhogManager(groundhogRoot);

		Groundhog groundhog = null;
		try {
			groundhog = groundhogManager.createNewGroundhog(groundhogFolder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		groundhog.setBulkImportMode(true);

		IndexerMainForGroundhog indexer = new IndexerMainForGroundhog();
		indexer.medlineIterator.fetchSubstances = true;		
		indexer.disambiguate = true;
		indexer.medlineIterator.pmidsFile = pmidFile;
		indexer.groundhog = groundhog;
		indexer.ontology = ontology;
		indexer.start();
		groundhog.setBulkImportMode(false);

		System.out.println(StringUtilities.now() + "\tSaving statistics ");

		GroundhogStatistics wholeGroundhogStatistics = groundhog.getGroundhogStatistics();
		File file = new File(groundhogRoot+groundhogFolder+"/GroundhogStatistics.txt");
		try {
			wholeGroundhogStatistics.saveGroundhogStatisticsToFile(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		groundhog.close();
	}

	private static void saveRestrictedPMIDsToFile(String query, String filename) {
		List<Integer> pmids = FetchPMIDsFromOnlinePubmed.getPMIDs(query,  "e.vanmulligen@erasmsumc.nl");
		WriteTextFile file = new WriteTextFile(filename);
		for (Integer pmid : pmids)
			file.writeln(pmid.toString());
		file.close();
		System.out.println("Found " + pmids.size() + " PMIDS");
	}

//	private static void saveGenePMIDsToFile(String query, String filename) {
//		List<Integer> pmids = FetchPMIDsFromOnlinePubmed.getPMIDs(query,  "erasmsumc@erasmsumc.nl");
//		WriteTextFile file = new WriteTextFile(filename);
//		for (Integer pmid : pmids)
//			file.writeln(pmid.toString());
//		file.close();
//		System.out.println("Found " + pmids.size() + " gene PMIDS");
//	}

//	private static Set<Integer> getRemoveSemTypes(){
//		Set<Integer> result = new HashSet<Integer>();
//		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(LiteratureUpdateMasterScript.class.getResourceAsStream("RemoveSemTypes.txt")));
//		try {
//			while (bufferedReader.ready()){
//				String line = bufferedReader.readLine();
//				String[] cols = line.split("\t");
//				result.add(Integer.parseInt("-"+cols[0]));
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
}
