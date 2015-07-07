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

package org.erasmusmc.dataimport.UMLS;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.CountingSet;
import org.erasmusmc.collections.OneToManySet;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.OntologyClient;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class InjectICD9CMLOINC {

  public static String mrConsoFilename = "/home/public/thesauri/UMLS2008AB/META/MRCONSO.RRF";
  public static String ontologyName = "Anni2_1_June2009";
  //public static String[] idCodes = new String[]{"ICD9CM","LNC"};
  //public static String[] dbCodes = new String[]{"ICD9","LNC"};
  public static String[] idCodes = new String[]{"RXNORM"};
  public static String[] dbCodes = new String[]{"RXN"};
  public static int umlsIdCol = 0;
  public static int vocCol = 11; 
  public static int otherIdCol = 13;
  
	public static void main(String[] args) {
		OneToManySet<DatabaseID, DatabaseID> umlsId2OtherId = loadMapping();
		insertMapping(umlsId2OtherId);
		StringUtilities.outputWithTime("Done");
	}

	private static void insertMapping(OneToManySet<DatabaseID, DatabaseID> umlsId2OtherId) {
		StringUtilities.outputWithTime("Injecting other IDs in ontology");
		OntologyManager ontologyManager = new OntologyManager("bios5.erasmusmc.nl");
		OntologyClient ontology = ontologyManager.fetchClient(ontologyName);
		int umlsCount = 0;
		int newIdCount = 0;
		for (Map.Entry<DatabaseID, Set<DatabaseID>> entry : umlsId2OtherId.entrySet()){
			Set<Integer> conceptIds = ontology.getConceptIDs(entry.getKey());
			boolean added = false;
			for (Integer conceptId : conceptIds)
				for (DatabaseID otherId : entry.getValue()){
					//ontology.setDatabaseIDForConcept(conceptId, otherId);
					System.out.println(conceptId + "\t" + otherId.toString());
					newIdCount++;
					added = true;
				}
			if (added)
				umlsCount++;
		}
		System.out.println("Added " + newIdCount + " new IDs to " + umlsCount + " UMLS concepts");
	}

	private static OneToManySet<DatabaseID, DatabaseID> loadMapping() {
		StringUtilities.outputWithTime("Loading other IDs from MRCONSO file");
		OneToManySet<DatabaseID, DatabaseID> umlsId2OtherId = new OneToManySet<DatabaseID, DatabaseID>();
		Map<String,String> id2dbId = createMap();

		
		CountingSet<String> vocCounts = new CountingSet<String>();
		for (String line : new ReadTextFile(mrConsoFilename)){
			String[] columns = line.split("\\|");
			if (id2dbId.containsKey(columns[vocCol])){
				DatabaseID umlsId = new DatabaseID("UMLS", columns[umlsIdCol]);
				DatabaseID otherId = new DatabaseID(id2dbId.get(columns[vocCol]), columns[otherIdCol]);
				umlsId2OtherId.put(umlsId, otherId);
				vocCounts.add(columns[vocCol]);
			}
		}
		System.out.println("Found these IDs for " + umlsId2OtherId.size() + " UMLS IDs:");
		vocCounts.printCounts();
		return umlsId2OtherId;
	}

	private static Map<String, String> createMap() {
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < idCodes.length; i++)
			map.put(idCodes[i], dbCodes[i]);
		return map;
	}

}

