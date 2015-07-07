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

package org.erasmusmc.dataimport.UMLS;

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.HomonymAnalyzer;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.ontology.ontologyutilities.evaluationScripts.OntologyFrequencyCount;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

public class UMLSloader {
	public static boolean generateCurationInformation = true;
	public static String normaliserCacheFile = "/home/public/Peregrine/standardNormCache2006.bin";
	public static String randomPMIDSampleFile = "/home/public/PMIDs/Random100.000.PMIDs";
	public static String tempFolder = "/home/khettne/temp/";

	public static String version = "2010AB";
	public static String timestamp = "180211";
	//This file will contain the rewritten terms
	public static String rewrittenMRCONSOfile = "/home/khettne/Projects/UMLS/"+version+"/MRCONSO"+version+"_"+timestamp+"_rewrittenAndSuppressed_test.RRF";
	//Rewrite and suppress log
	public static String rewriteAndSuppressLog = "/home/khettne/Projects/UMLS/"+version+"/UMLS"+version+"_"+timestamp+"_rewriting_log_test.log";
//	This file contains concept IDs, terms, and vocabularies
	public static String MRCONSOfile = "/home/public/thesauri/UMLS"+version+"/META/MRCONSO.RRF";
	//This file contains semantic types for a concept
	public static String MRSTYfile = "/home/public/thesauri/UMLS"+version+"/META/MRSTY.RRF";
	public static String SRDEFfile = "/home/public/thesauri/UMLS"+version+"/NET/SRDEF";
//	This file contains abbreviations
	public static String LRABRfile = "/home/public/thesauri/UMLS"+version+"/LEX/LRABR";
	//This file contains the concept definitions
	public static String MRDEFfile = "/home/public/thesauri/UMLS"+version+"/META/MRDEF.RRF";
	//Create loading log
	public static List<String> log_output = new ArrayList<String>();
	public static String logname = "/home/khettne/Projects/UMLS/"+version+"/UMLS"+version+"_loading_log_test.log";
	//Curation file 
	public static String curationFilePath = "/home/khettne/Projects/UMLS/"+version+"/UMLS_curation_file_updatedFor"+version+".txt";
	//Name of the ontology
	public static String ontologyName = "UMLS"+version+"_"+timestamp;
	public static String ontologyPath = "/home/khettne/Projects/UMLS/"+version+"/UMLS"+version+"_"+timestamp+".ontology";

	public static void main(String[] args) {

		System.out.println("Starting script: "+StringUtilities.now());


		Ontology newOntology = new OntologyStore();
		newOntology.setName(ontologyName);    

		//Filter the UMLS
		System.out.println("Rewriting and suppressing MRCONSO... "+StringUtilities.now());
		RewriteAndSuppressUMLSusingCasper rewriteAndSuppress = new RewriteAndSuppressUMLSusingCasper();
		rewriteAndSuppress.run(MRCONSOfile, MRSTYfile, rewrittenMRCONSOfile, rewriteAndSuppressLog);

		//Fill the ontology
		System.out.println("Executing MRCONSOLoader... "+StringUtilities.now());
		RewrittenMRCONSOLoader.loadFromRewrittenMRCONSO(newOntology, rewrittenMRCONSOfile, log_output, LRABRfile);

		System.out.println("Executing MRSTYLoader... "+StringUtilities.now());
		MRSTYLoader.addSemanticType(newOntology, MRSTYfile, SRDEFfile);

		System.out.println("Executing MRDEFLoader... "+StringUtilities.now());
		MRDEFLoader.addDefinition(newOntology, MRDEFfile, log_output);

//		Save to log
		System.out.println("Saving to log file "+StringUtilities.now());
		TextFileUtilities.saveToFile(log_output, logname);

		System.out.println("Formatting UMLS database IDs: "+StringUtilities.now());
		newOntology = AddUMLSidAsDatabaseID.addUMLSid(newOntology);

		System.out.println("Mapping ontology: "+StringUtilities.now());		
		newOntology = MapGoAndOmimToOntology.mapGoAndOMIMFromMRCONSOtoOntology(newOntology, MRCONSOfile);

		//Curate
		System.out.println(StringUtilities.now() + "\tApplying generic filter");
		OntologyUtilities.filterOntology(newOntology, OntologyUtilities.stopwordsForFiltering);

		OntologyCurator curator = new OntologyCurator(curationFilePath);
		curator.curateAndPrepare(newOntology);

		if (generateCurationInformation)
			generateCurationInformation(newOntology);

//		Save ontology
		OntologyFileLoader loader = new OntologyFileLoader();
		loader.save((OntologyStore)newOntology, ontologyPath);

//		Load ontology into database
		OntologyManager ontologyManager = new OntologyManager();
		ontologyManager.dumpStoreInDatabase((OntologyStore)newOntology);  

	}

	private static void generateCurationInformation(Ontology newOntology) {

		System.out.println(StringUtilities.now() + "\tAnalyzing homonyms");
		HomonymAnalyzer homcount = new HomonymAnalyzer();
		homcount.destroyOntologyDuringRelease = false;
		homcount.normaliser.loadCacheBinary(normaliserCacheFile);
		homcount.setOntology(newOntology);
		homcount.countHomonyms(tempFolder+"umls_homonyms.txt");

		System.out.println(StringUtilities.now() + "\tCounting frequencies");
		OntologyFrequencyCount.disambiguate = false;
		OntologyFrequencyCount.pmidsFile = randomPMIDSampleFile;
		OntologyFrequencyCount.outputFile = tempFolder+"umls_frequencyCounts.txt";
		new OntologyFrequencyCount(newOntology);
	}
}
