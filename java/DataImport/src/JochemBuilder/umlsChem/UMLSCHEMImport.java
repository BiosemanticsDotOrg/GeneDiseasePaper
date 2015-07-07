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

package JochemBuilder.umlsChem;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;

import JochemBuilder.SharedCurationScripts.CasperForJochem;
import JochemBuilder.SharedCurationScripts.CurateUsingManualCurationFile;
import JochemBuilder.SharedCurationScripts.RemoveDictAndCompanyNamesAtEndOfTerm;
import JochemBuilder.SharedCurationScripts.RewriteFurther;
import JochemBuilder.SharedCurationScripts.SaveOnlyCASandInchiEntries;

public class UMLSCHEMImport {
	public static String date = "110809";
	public static String home = "/home/khettne/Projects/Jochem";

	public static String mrconsoPath = home+"/UMLSCHEM/MRCONSO.RRF";
	public static String mrstyPath = home+"/UMLSCHEM/MRSTY.RRF";
	public static String chemRRFPath =  home+"/UMLSCHEM/UMLSCHEM_"+date+".RRF";
	public static String rrfLoadingLogpath = home+"/UMLSCHEM/UMLSCHEM_rrfLoadingLog_"+date+".log";
	public static String srdefPath = home+"/UMLSCHEM/SRDEF";
	public static String lrabrPath = home+"/UMLSCHEM/LRABR";
	public static String mrdefPath = home+"/UMLSCHEM/MRDEF.RRF";
	public static String logPath =  home+"/UMLSCHEM/UMLSCHEM_psfConversionLog_"+date+".log";  
	public static String ontologyPath =  home+"/UMLSCHEM/UMLSCHEM_beforeSemanticFilter_"+date+".ontology";

	public static String semanticTypeFilteredOntologyPath = home+"/UMLSCHEM/UMLSCHEM_unprocessed_"+date+".ontology";
	public static String semanticTypeFilteredDictionariesLog = home+"/UMLSCHEM/UMLSCHEM_dictionaries_"+date+".log";

	public static String meshhOntologypath = home+"/MeSH/MeSH_unprocessed_"+date+".ontology";
	public static String meshsOntologypath =	home+"/MeSH/MeSHSupp_unprocessed_"+date+".ontology";
	public static String casMapFileName = home+"/MeSH/MeSH2CAS.txt";
	public static String rewriteLog =  home+"/UMLSCHEM/UMLSCHEMCAS_rewriteLog_"+date+".log";
	public static String lowerCaseLog =  home+"/UMLSCHEM/UMLSCHEMCAS_lowerCase_"+date+".log";

	public static String termsToRemove = "umlsChemTermsToRemove.txt";
	public static String curatedOntologyPath =  home+"/UMLSCHEM/UMLSCHEMCAS_curated_"+date+".ontology";
	public static String curatedLog =  home+"/UMLSCHEM/UMLSCHEMCAS_curated_"+date+".log";

	public static void main(String[] args) {

		//Make unprocessed thesaurus
		FilterMRCONSOforChem umlsMrconsoChemicals = new FilterMRCONSOforChem();
		umlsMrconsoChemicals.getChemicalsFromMRCONSO(mrconsoPath, mrstyPath, chemRRFPath, rrfLoadingLogpath);

		ChemicalMRCONSO2Ontology chemicalMRCONSOConverter = new ChemicalMRCONSO2Ontology();
		chemicalMRCONSOConverter.convertChemicalMRCONSO2Ontology(chemRRFPath, mrstyPath, srdefPath, lrabrPath, mrdefPath, logPath, ontologyPath);

		UMLSchemFilterForSemanticTypeAfterOntologyCreation semfilter = new UMLSchemFilterForSemanticTypeAfterOntologyCreation();
		semfilter.run(ontologyPath, semanticTypeFilteredOntologyPath);

		OntologyStore ontology = new OntologyStore();
		OntologyFileLoader loader = new OntologyFileLoader();
		ontology = loader.load(semanticTypeFilteredOntologyPath);

		MeSH2CASmapping createMappingFile = new MeSH2CASmapping();
		createMappingFile.getMeSH2CASmappings(casMapFileName, meshhOntologypath, meshsOntologypath);
		MapUMLS2CASfromMeSH mappCAS = new MapUMLS2CASfromMeSH();  
		ontology = mappCAS.run(ontology, casMapFileName);

		RemoveDictAndCompanyNamesAtEndOfTerm remove = new RemoveDictAndCompanyNamesAtEndOfTerm();
		ontology = remove.run(ontology, semanticTypeFilteredDictionariesLog);
		loader.save(ontology,semanticTypeFilteredOntologyPath);

//		CAS and InChI
		SaveOnlyCASandInchiEntries make = new SaveOnlyCASandInchiEntries();
		ontology = make.run(ontology);

		//  Rewrite
		CasperForJochem casper = new CasperForJochem();
		ontology = casper.run(ontology, rewriteLog);

		//  Make some entries lower case and filter further
		RewriteFurther rewrite = new RewriteFurther();
		ontology = rewrite.run(ontology, lowerCaseLog);

		//Remove terms based on medline frequency
		CurateUsingManualCurationFile curate = new CurateUsingManualCurationFile();
		ontology = curate.run(ontology, curatedLog,termsToRemove);
		
		//Set default flags and save ontology
		OntologyCurator curator = new OntologyCurator();
	    curator.curateAndPrepare(ontology);
		loader.save(ontology,curatedOntologyPath);
		
		System.out.println("Done! " + StringUtilities.now());
	}
}
