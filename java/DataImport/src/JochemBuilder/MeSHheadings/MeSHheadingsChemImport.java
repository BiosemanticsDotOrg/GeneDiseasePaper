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

package JochemBuilder.MeSHheadings;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;

import JochemBuilder.SharedCurationScripts.CasperForJochem;
import JochemBuilder.SharedCurationScripts.CurateUsingManualCurationFile;
import JochemBuilder.SharedCurationScripts.RemoveDictAndCompanyNamesAtEndOfTerm;
import JochemBuilder.SharedCurationScripts.RewriteFurther;
import JochemBuilder.SharedCurationScripts.SaveOnlyCASandInchiEntries;

public class MeSHheadingsChemImport {
	public static String date = "110809";
	public static String home = "/home/khettne/Projects/Jochem";

	public static String meshImportFile = home+"/MeSH/d2009.txt";
	public static String meshUnprocessedOntologyPath =  home+"/MeSH/MeSH_unprocessed_"+date+".ontology";  
	
	public static String meshDictionariesLog =  home+"/MeSH/MeSH_dictionaries_"+date+".log";	
	public static String meshRewriteLog =  home+"/MeSH/MeSHCAS_casperFiltered_"+date+".log";
	public static String meshLowerCaseLog =  home+"/MeSH/MeSHCAS_lowerCase_"+date+".log";

	public static String termsToRemove = "meshhTermsToRemove.txt";
	public static String meshCuratedOntologyPath =  home+"/MeSH/MeSHCAS_curated_"+date+".ontology";  
	public static String meshCuratedLog =  home+"/MeSH/MeSHCAS_curated_"+date+".log";

	public static void main(String[] args) {

		OntologyFileLoader loader = new OntologyFileLoader();
		OntologyStore ontology = new OntologyStore();

		//Make unprocessed thesaurus
		ChemicalsFromMeSHheadings mesh = new ChemicalsFromMeSHheadings();
		ontology = mesh.run(meshImportFile);
		RemoveDictAndCompanyNamesAtEndOfTerm remove = new RemoveDictAndCompanyNamesAtEndOfTerm();
		ontology = remove.run(ontology, meshDictionariesLog);
		loader.save(ontology, meshUnprocessedOntologyPath);

//		CAS and InChI
		SaveOnlyCASandInchiEntries make = new SaveOnlyCASandInchiEntries();
		ontology = make.run(ontology);

		//    Rewrite
		CasperForJochem casper = new CasperForJochem();
		ontology = casper.run(ontology, meshRewriteLog);

//	  Make some entries lower case and filter further
		RewriteFurther rewrite = new RewriteFurther();
		ontology = rewrite.run(ontology, meshLowerCaseLog);

		//Remove terms based on medline frequency
		CurateUsingManualCurationFile curate = new CurateUsingManualCurationFile();    
		ontology = curate.run(ontology, meshCuratedLog,termsToRemove);
		
		//Set default flags and save ontology
		OntologyCurator curator = new OntologyCurator();
	    curator.curateAndPrepare(ontology);
	    loader.save(ontology,meshCuratedOntologyPath);
	    
		System.out.println("Done! " + StringUtilities.now());

	}

}
