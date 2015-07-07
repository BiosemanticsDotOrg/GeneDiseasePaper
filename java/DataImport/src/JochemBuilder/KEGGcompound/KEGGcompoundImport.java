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

package JochemBuilder.KEGGcompound;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;

import JochemBuilder.SharedCurationScripts.CasperForJochem;
import JochemBuilder.SharedCurationScripts.CurateUsingManualCurationFile;
import JochemBuilder.SharedCurationScripts.RemoveDictAndCompanyNamesAtEndOfTerm;
import JochemBuilder.SharedCurationScripts.RewriteFurther;
import JochemBuilder.SharedCurationScripts.SaveOnlyCASandInchiEntries;

public class KEGGcompoundImport {
	public static String date = "110809";
	public static String home = "/home/khettne/Projects/Jochem";

	public static String keggcImportFile = home+"/KEGG/Compound/compound";
	public static String compoundToDrugMappingOutFile = home+"/KEGG/Compound/compoundToDrugMapping";
	public static String keggcToInchiMappingFile = home+"/KEGG/Compound/compound.inchi";
	
	public static String keggcDictionariesLog =  home+"/KEGG/Compound/KEGGc_dictionaries_"+date+".log";
	public static String keggcRewriteLog =  home+"/KEGG/Compound/KEGGcCAS_casperFiltered_"+date+".log";
	public static String keggcLowerCaseLog =  home+"/KEGG/Compound/KEGGcCAS_lowerCase_"+date+".log";

	public static String termsToRemove = "keggcTermsToRemove.txt";
	public static String keggcCuratedOntologyPath =  home+"/KEGG/Compound/KEGGcCAS_curated_"+date+".ontology";  
	public static String keggcCuratedLog =  home+"/KEGG/Compound/KEGGcCAS_curated_"+date+".log";

	public static void main(String[] args) {
		OntologyStore ontology = new OntologyStore();
		OntologyFileLoader loader = new OntologyFileLoader();

		//Make unprocessed thesaurus
		ChemicalsFromKEGGcompound keggchem = new ChemicalsFromKEGGcompound();
		ontology = keggchem.run(keggcImportFile, compoundToDrugMappingOutFile);    

		RemoveDictAndCompanyNamesAtEndOfTerm remove = new RemoveDictAndCompanyNamesAtEndOfTerm();
		ontology = remove.run(ontology, keggcDictionariesLog);

		MapKEGGc2InChI mapOntology = new MapKEGGc2InChI();
		ontology = mapOntology.map(ontology, keggcToInchiMappingFile);

//		CAS and InChI
		SaveOnlyCASandInchiEntries make = new SaveOnlyCASandInchiEntries();
		ontology = make.run(ontology);

		//Rewrite
		CasperForJochem casper = new CasperForJochem();
		casper.run(ontology, keggcRewriteLog);

//		Make some entries lower case and filter further
		RewriteFurther rewrite = new RewriteFurther();
		ontology = rewrite.run(ontology, keggcLowerCaseLog);

		//Remove terms based on medline frequency
		CurateUsingManualCurationFile curate = new CurateUsingManualCurationFile();    
		ontology = curate.run(ontology, keggcCuratedLog,termsToRemove);
		
		//Set default flags and save ontology
		OntologyCurator curator = new OntologyCurator();
	    curator.curateAndPrepare(ontology);
		loader.save(ontology,keggcCuratedOntologyPath);
		
		System.out.println("Done! " + StringUtilities.now());
	}
}
