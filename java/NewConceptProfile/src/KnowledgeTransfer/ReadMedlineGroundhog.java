/*
 * Concept profile generation and analysis for Gene-Disease paper
 * Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
 *  Leiden, The Netherlands
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

package KnowledgeTransfer;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.collections.SortedIntList2FloatMap;
import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.ConceptVectorRecord;

public class ReadMedlineGroundhog {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String path2folder = "/home/hvanhaagen/textmining/Groundhogs/";
		//String folder = "Medline1980till17Jul2012_UMLS2010ABHomologeneJochemToxV1_6";
		String folder = "Medline1980till17Jul2012_UMLS2010ABHomologeneJochemToxV1_6-test38";
		
		// Voorbeeld concept
		Integer cid1 = 20179;	//Huntington Disease
		
		// Declareer een medline groundhog volgens de legacy code. 
		Groundhog documentProfilesGroundhog;
		GroundhogManager groundhogmanager2 = new GroundhogManager(path2folder);
		documentProfilesGroundhog = groundhogmanager2.getGroundhog(folder);
		
		// concept id (1) -> PMIDs (many)
		// dit is de eerste mapping die in de medline groundhog moet zitten. 
		SortedIntListSet pmids = documentProfilesGroundhog.getRecordIDsForConcept(cid1);
		
		// Loop over de PMIDs. Controleer voor sommige PMIDs of je Huntington in het abstract terugvindt. 
	//	for(Integer pmid:pmids){
	//		System.out.println(pmid);
	//	}
		
		// Bereken het aantal artikelen waar Huntington in voorkomt. 
		System.out.println(pmids.size());
		
/////////////////////////////////////////////////////////////////////////////////////////
		
		// Neem een willekeurig PMID 
		int random_pmid = 1280937;
		//Haal de concept IDs op die in dat abstract voorkomen. PMID (1) -> concept ids (many)
		ConceptVectorRecord cvr = documentProfilesGroundhog.get(random_pmid);
		
		// Haal de bananenschil eraf (legacy code)
		ConceptVector cv = cvr.getConceptVector();
		SortedIntList2FloatMap sil2fm = cv.values;
		IntList keys = sil2fm.keys();
		
		// Loop over de keys. De keys zijn de concept ids. Voor elk concept checken we de frequentie
		// hoevaak deze voorkomt in het abstract
		for(Integer key:keys){
			float frequency = sil2fm.get(key);
			System.out.println(key+"\t"+frequency);
		}
		
	}

}
