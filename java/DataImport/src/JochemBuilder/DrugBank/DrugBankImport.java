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

package JochemBuilder.DrugBank;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;

import JochemBuilder.SharedCurationScripts.CasperForJochem;
import JochemBuilder.SharedCurationScripts.CurateUsingManualCurationFile;
import JochemBuilder.SharedCurationScripts.RemoveDictAndCompanyNamesAtEndOfTerm;
import JochemBuilder.SharedCurationScripts.RewriteFurther;
import JochemBuilder.SharedCurationScripts.SaveOnlyCASandInchiEntries;

public class DrugBankImport {
	public static String date = "110809";
	public static String home = "/home/khettne/Projects/Jochem";

	public static String drugbankImportFile = home+"/DrugBank/drugcards.txt";

	public static String drugbankDictionariesLog =  home+"/DrugBank/DrugBank_dictionaries_"+date+".log";
	public static String drugbankRewriteLog =  home+"/DrugBank/DrugBankCAS_casperFiltered_"+date+".log";
	public static String drugbankLowerCaseLog =  home+"/DrugBank/DrugBankCAS_lowerCase_"+date+".log";

	public static String termsToRemove = "drugbankTermsToRemove.txt";
	public static String drugbankCuratedOntologyPath =  home+"/DrugBank/DrugBankCAS_curated_"+date+".ontology";  
	public static String drugbankCuratedLog =  home+"/DrugBank/DrugBankCAS_curated_"+date+".log";

	public static void main(String[] args) {
		OntologyStore ontology = new OntologyStore();
		OntologyFileLoader loader = new OntologyFileLoader();

		//Make unprocessed thesaurus
		ChemicalsFromDrugBank drugbank = new ChemicalsFromDrugBank();
		ontology = drugbank.run(drugbankImportFile);
		RemoveDictAndCompanyNamesAtEndOfTerm remove = new RemoveDictAndCompanyNamesAtEndOfTerm();
		ontology = remove.run(ontology, drugbankDictionariesLog);

//		CAS and InChI
		SaveOnlyCASandInchiEntries make = new SaveOnlyCASandInchiEntries();
		ontology = make.run(ontology);

//		Rewrite
		CasperForJochem casper = new CasperForJochem();
		ontology = casper.run(ontology, drugbankRewriteLog);

		//Make some entries lower case and rewrite further
		RewriteFurther rewrite = new RewriteFurther();
		ontology = rewrite.run(ontology, drugbankLowerCaseLog);

		//Remove terms based on medline frequency
		CurateUsingManualCurationFile curate = new CurateUsingManualCurationFile();    
		ontology = curate.run(ontology, drugbankCuratedLog,termsToRemove);

//		Set default flags and save ontology
		OntologyCurator curator = new OntologyCurator();
	    curator.curateAndPrepare(ontology);
		loader.save(ontology,drugbankCuratedOntologyPath);
		
		System.out.println("Done! " + StringUtilities.now());
	}
}
