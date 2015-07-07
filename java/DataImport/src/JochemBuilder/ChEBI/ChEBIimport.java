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

package JochemBuilder.ChEBI;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;

import JochemBuilder.SharedCurationScripts.CasperForJochem;
import JochemBuilder.SharedCurationScripts.CurateUsingManualCurationFile;
import JochemBuilder.SharedCurationScripts.RemoveDictAndCompanyNamesAtEndOfTerm;
import JochemBuilder.SharedCurationScripts.RewriteFurther;
import JochemBuilder.SharedCurationScripts.SaveOnlyCASandInchiEntries;

public class ChEBIimport {
	public static String date = "110809";
	public static String home = "/home/khettne/Projects/Jochem";

	public static String chebiImportFile = home+"/ChEBI/chebi.obo";

	public static String chebiDictionariesLog =  home+"/ChEBI/ChEBI_dictionaries_"+date+".log";
	public static String chebiRewriteLog =  home+"/ChEBI/ChEBICAS_casperFiltered_"+date+".log";
	public static String chebiLowerCaseLog =  home+"/ChEBI/ChEBICAS_lowerCase_"+date+".log";

	public static String termsToRemove = "chebiTermsToRemove.txt";
	public static String chebiCuratedOntologyPath =  home+"/ChEBI/ChEBICAS_curated_"+date+".ontology";
	public static String chebiCuratedLog =  home+"/ChEBI/ChEBICAS_curated_"+date+".log";

	public static void main(String[] args) {
		OntologyStore ontology = new OntologyStore();
		OntologyFileLoader loader = new OntologyFileLoader();

		//Make unprocessed thesaurus
		ChemicalsFromChEBI chebi = new ChemicalsFromChEBI();
		ontology = chebi.run(chebiImportFile);
		RemoveDictAndCompanyNamesAtEndOfTerm remove = new RemoveDictAndCompanyNamesAtEndOfTerm();
		ontology = remove.run(ontology, chebiDictionariesLog);

//		CAS and InChI
		SaveOnlyCASandInchiEntries make = new SaveOnlyCASandInchiEntries();
		ontology = make.run(ontology);

//		Rewrite
		CasperForJochem casper = new CasperForJochem();
		ontology = casper.run(ontology, chebiRewriteLog);
		
//		Make some entries lower case and filter further
		RewriteFurther rewrite = new RewriteFurther();
		ontology = rewrite.run(ontology, chebiLowerCaseLog);

		//Remove terms based on medline frequency
		CurateUsingManualCurationFile curate = new CurateUsingManualCurationFile();
		ontology = curate.run(ontology, chebiCuratedLog,termsToRemove);
		
		//Set default flags and save ontology
		OntologyCurator curator = new OntologyCurator();
	    curator.curateAndPrepare(ontology);
		loader.save(ontology,chebiCuratedOntologyPath);
		
		System.out.println("Done! " + StringUtilities.now());
	}

}
