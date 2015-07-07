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

package JochemBuilder.KEGGdrug;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;

import JochemBuilder.SharedCurationScripts.CasperForJochem;
import JochemBuilder.SharedCurationScripts.CurateUsingManualCurationFile;
import JochemBuilder.SharedCurationScripts.RemoveDictAndCompanyNamesAtEndOfTerm;
import JochemBuilder.SharedCurationScripts.RewriteFurther;
import JochemBuilder.SharedCurationScripts.SaveOnlyCASandInchiEntries;

public class KEGGdrugImport {
	public static String date = "110809";
	public static String home = "/home/khettne/Projects/Jochem";

	public static String keggdImportFile = home+"/KEGG/Drug/drug";
	public static String keggdToInchiMappingFile = home+"/KEGG/Drug/drug.inchi";
	public static String keggdDictionariesLog =  home+"/KEGG/Drug/KEGGd_dictionaries_"+date+".log";
	public static String keggdRewriteLog =  home+"/KEGG/Drug/KEGGdCAS_casperFiltered_"+date+".log";
	public static String keggdLowerCaseLog =  home+"/KEGG/Drug/KEGGdCAS_lowerCase_"+date+".log";

	public static String termsToRemove = "keggdTermsToRemove.txt";
	public static String keggdCuratedOntologyPath =  home+"/KEGG/Drug/KEGGdCAS_curated_"+date+".ontology";  
	public static String keggdCuratedLog =  home+"/KEGG/Drug/KEGGdCAS_curated_"+date+".log";

	public static void main(String[] args) {
		OntologyStore ontology = new OntologyStore();
		OntologyFileLoader loader = new OntologyFileLoader();

		//Make unprocessed thesaurus
		ChemicalsFromKEGGdrug keggdrugs = new ChemicalsFromKEGGdrug();
		ontology = keggdrugs.run(keggdImportFile);    

		RemoveDictAndCompanyNamesAtEndOfTerm remove = new RemoveDictAndCompanyNamesAtEndOfTerm();
		ontology = remove.run(ontology, keggdDictionariesLog);

		MapKEGGd2InChI mapOntology = new MapKEGGd2InChI();
		ontology = mapOntology.map(ontology, keggdToInchiMappingFile);

		SaveOnlyCASandInchiEntries make = new SaveOnlyCASandInchiEntries();
		ontology = make.run(ontology);

		//Rewrite
		CasperForJochem casper = new CasperForJochem();
		casper.run(ontology, keggdRewriteLog);

		//  Make some entries lower case and filter further
		RewriteFurther rewrite = new RewriteFurther();
		ontology = rewrite.run(ontology, keggdLowerCaseLog);

		//Remove terms based on medline frequency
		CurateUsingManualCurationFile curate = new CurateUsingManualCurationFile(); 
		ontology = curate.run(ontology, keggdCuratedLog,termsToRemove);
		
		//Set default flags and save ontology
		OntologyCurator curator = new OntologyCurator();
	    curator.curateAndPrepare(ontology);
	    loader.save(ontology,keggdCuratedOntologyPath);
	    
		System.out.println("Done! " + StringUtilities.now());

	}
}
