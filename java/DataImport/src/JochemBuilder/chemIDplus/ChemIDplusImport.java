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

package JochemBuilder.chemIDplus;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;

import JochemBuilder.SharedCurationScripts.CasperForJochem;
import JochemBuilder.SharedCurationScripts.CurateUsingManualCurationFile;
import JochemBuilder.SharedCurationScripts.RemoveDictAndCompanyNamesAtEndOfTerm;
import JochemBuilder.SharedCurationScripts.RewriteFurther;
import JochemBuilder.SharedCurationScripts.SaveOnlyCASandInchiEntries;

public class ChemIDplusImport {

	/**The commented code below was used to map ChemIDplus IDs to CAS numbers. This was done since 
	 * many CAS numbers were missing in the original ChemIDplus file. ChemIDplus IDs used to be
	 * based on CAS numbers and I used this property to map them to CAS numbers that I had parsed
	 * from the PubChem database. This procedure is unfortunately not completely reliable and 
	 * according the the ChemIDplus people not necessary anymore, since the errors in the CAS field
	 * have been solved. Apart from that, the structure of the ChemIDplus IDs have changed. The code is thus
	 * not likely to work with releases of ChemIDplus after the year 2008, but probably also not needed.
	 */
	public static String date = "110809";
	public static String home = "/home/khettne/Projects/Jochem";


	public static String chemiIDplusImportFile = home+"/ChemIDplus/chemid.xml.20081028";
	public static String mergedSemanticTypeslog = home+"/ChemIDplus/mergedSemanticTypes.txt";
	public static String chemIDplusDictionariesLog =  home+"/ChemIDplus/ChemIDplus_dictionaries_"+date+".log";
	public static String chemIDplusRewriteLog =  home+"/ChemIDplus/ChemIDplusCAS_casperFiltered_"+date+".log";
	public static String chemIdplusLowerCaseLog =  home+"/ChemIDplus/ChemIDplusCAS_lowerCase_"+date+".log";

	public static String termsToRemove = "chemIDplusTermsToRemove.txt";
	public static String chemIdplusCuratedOntologyPath =  home+"/ChemIDplus/ChemIDplusCAS_curated_"+date+".ontology";
	public static String chemIdplusCuratedLog =  home+"/ChemIDplus/ChemIDplusCAS_curated_"+date+".log";

	public static void main(String[] args) {
		OntologyStore ontology = new OntologyStore();
		OntologyFileLoader loader = new OntologyFileLoader();

		//Make unprocessed thesaurus
		ChemicalsFromChemIDplus chemIDplusChemicals = new ChemicalsFromChemIDplus();
		ontology = chemIDplusChemicals.run(chemiIDplusImportFile, mergedSemanticTypeslog);		

/** the commented code is probably not needed for newer updates
		//This is file output, change the path to your own location!
		String chemIDplusIDs = home+"/ChemIDplus/chids.txt";

		GetIDsfromChemIDplus getIDs = new GetIDsfromChemIDplus();
		getIDs.run(ontology, chemIDplusIDs);

//		This is file input, copy the content from the file "casnumbers.txt" in JochemBuilder.chemIDplus to your own location!
		String casFromPubChem = home+"/ChemIDplus/casnumbers.txt";

		MappChemIDplusIDsToCASfromPubChem mappedChemIDplus = new MappChemIDplusIDsToCASfromPubChem();
		ontology = mappedChemIDplus.run(ontology, chemIDplusIDs, casFromPubChem);
*/
		RemoveDictAndCompanyNamesAtEndOfTerm remove = new RemoveDictAndCompanyNamesAtEndOfTerm();
		ontology = remove.run(ontology, chemIDplusDictionariesLog);

//		CAS and InChI
		SaveOnlyCASandInchiEntries make = new SaveOnlyCASandInchiEntries();
		ontology = make.run(ontology);

//		Rewrite
		CasperForJochem casper = new CasperForJochem();
		ontology = casper.run(ontology, chemIDplusRewriteLog);

//		Make some entries lower case and filter further
		RewriteFurther rewrite = new RewriteFurther();
		ontology = rewrite.run(ontology, chemIdplusLowerCaseLog);

		//Remove terms based on medline frequency
		CurateUsingManualCurationFile curate = new CurateUsingManualCurationFile(); 
		ontology = curate.run(ontology, chemIdplusCuratedLog, termsToRemove);

		//Set default flags and save ontology
		OntologyCurator curator = new OntologyCurator();
		curator.curateAndPrepare(ontology);
		loader.save(ontology, chemIdplusCuratedOntologyPath);
		
		System.out.println("Done! " + StringUtilities.now());
	}
}
