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

import java.util.Iterator;

import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;

public class CalculateCooccurrence {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String path2folder = "/home/hvanhaagen/textmining/Groundhogs/";
		//String folder = "Medline1980till17Jul2012_UMLS2010ABHomologeneJochemToxV1_6";
		String folder = "Medline1980till17Jul2012_UMLS2010ABHomologeneJochemToxV1_6-test38";
		
		// Voorbeeld concept
		Integer cid1 = 20179;	//Huntington Disease
		Integer cid2 = 3063476;	//HIP1 (huntington interaction partner 1)
		
		// Declareer een medline groundhog volgens de legacy code. 
		Groundhog documentProfilesGroundhog;
		GroundhogManager groundhogmanager2 = new GroundhogManager(path2folder);
		documentProfilesGroundhog = groundhogmanager2.getGroundhog(folder);

		
		double cooc = countCoOccurrrences(cid1, cid2, documentProfilesGroundhog);
		
		
		System.out.println(cooc);
		
	}

	
	public static double countCoOccurrrences(Integer conc1, Integer conc2, Groundhog documentProfilesGroundhog) {

		// Voor beide concept, geef een lijst terug van PMIDs
		SortedIntListSet silt = documentProfilesGroundhog.getRecordIDsForConcept(conc1);
		SortedIntListSet silt2 = documentProfilesGroundhog.getRecordIDsForConcept(conc2);
		if(silt.size()==0 || silt2.size()==0)
			return Double.NaN;

		int count = 0;
		int countA = 0;
		
		//Loop over de ene lijst en kijk of het concept in de andere lijst voorkomt. 
		Iterator<Integer> it = silt.iterator();
		while (it.hasNext()) {
			countA++;
			Integer key = it.next();
			if (silt2.contains(key)) {
				count++;
			}
		}
		return count;
	}
}
