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

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import org.erasmusmc.collections.IntList;
import org.erasmusmc.collections.SortedIntList2FloatMap;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.ConceptVectorRecord;


public class ConceptFrequencies {
	
	public static final String GROUNDHOG_BASE_DIR            = "/tmp/cpgp/groundhogs/";
	public static final String CONCEPT_FREQUENCIES_FILENAME  = "/tmp/results/" + "conceptid2frequency.txt";
	public static final String MEDLINE_GROUNDHOG_FOLDER_NAME = "Medline1980till17Jul2014_UMLS2010ABHomologeneJochemToxV1_6";

		public static void main(String[] args) {

			//This script is designed to parse the old format of the medline groundhog to
			// a flat text (.csv) file with the following format:
			// 12345	825
			// where the first column is the CID and the second column are the number of articles that concept
			// occurs is. 
			// This flat file is imported into SQLite3

			GroundhogManager groundhogmanager2 = new GroundhogManager(GROUNDHOG_BASE_DIR);
			Groundhog documentProfilesGroundhog = groundhogmanager2.getGroundhog(MEDLINE_GROUNDHOG_FOLDER_NAME);
			Iterator<ConceptVectorRecord> iter = documentProfilesGroundhog.getIterator();
			HashMap<Integer,Integer> cid2pmidcount = new HashMap<Integer, Integer>();
			

			int count = 0;
			while(iter.hasNext()){
				ConceptVectorRecord cvr = iter.next();
				if(cvr!=null){

					ConceptVector cv = cvr.getConceptVector();
					SortedIntList2FloatMap silt = cv.values;
					IntList keys = silt.keys();
					for(Integer cid:keys){

						Integer freq = cid2pmidcount.get(cid);
						if(freq==null)
							freq = 0;
						freq++;
						cid2pmidcount.put(cid, freq);

					}

					count++;
					if(count%10000==0){
						System.out.println(count);
					}
				}
			}

			try{
				FileOutputStream output = new FileOutputStream(CONCEPT_FREQUENCIES_FILENAME);
				PrintStream printer = new PrintStream(output);
				for(Integer cid:cid2pmidcount.keySet()){
					int freq = cid2pmidcount.get(cid);
					printer.println(cid+"\t"+freq);
				}
				printer.flush();

			}catch(Exception e){
				e.printStackTrace();
			}
		}

}
