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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class MapGoAndOmimToOntology {
	public static Ontology mapGoAndOMIMFromMRCONSOtoOntology(Ontology ontology, String inFilename) {
		Map<String,List<DatabaseID>> dbMap = new TreeMap<String, List<DatabaseID>>();
		int cuiCol = 0;
		int vocCol = 11;
		int dbCol = 13;

		int cui = -1;
		int prevCui = -1;
		List<DatabaseID> dblist = new ArrayList<DatabaseID>();
		ReadTextFile textFile = new ReadTextFile(inFilename);
		Iterator<String> fileIterator = textFile.getIterator();
		int lineCount = 0;
		while (fileIterator.hasNext()) {
			lineCount++;
			if (lineCount % 100000 == 0)
				System.out.println(lineCount);
			String line = fileIterator.next();
			if (line.length() != 0) {
				String[] columns = line.split("\\|");
				cui = Integer.parseInt(columns[cuiCol].trim().substring(1, columns[cuiCol].length()));
				if (prevCui != cui) {
					if (!dblist.isEmpty()){
						dbMap.put(StringUtilities.formatNumber("C0000000", prevCui), dblist);
					}
					prevCui = cui;
					dblist = new ArrayList<DatabaseID>();
				}
				// Add GO-identifier
				String voc = columns[vocCol].trim();
				if (voc.equals("GO")) {
					String GOstring = columns[dbCol].trim();
					if (GOstring.equals("GO:0016410")){
						System.out.println("debug");
					}
					DatabaseID databaseID = new DatabaseID("GO", GOstring);
					if (!dblist.contains(databaseID))
						dblist.add(databaseID);
				}
				// Add OMIM-identifier
				if (voc.equals("OMIM")) {
					String OMIMstring = columns[dbCol].trim();
					DatabaseID databaseID = new DatabaseID("OM", OMIMstring);
					if (!dblist.contains(databaseID))
						dblist.add(databaseID);
				}
			}
		}

		Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		while (conceptIterator.hasNext()){
			Concept concept = conceptIterator.next();
			if (concept.getID()>-1){
				boolean add = false;
				String umlsID = "";
				List<DatabaseID> dbListForConcept = ontology.getDatabaseIDsForConcept(concept.getID());
				for (DatabaseID databaseId: dbListForConcept){
					if (databaseId.database.equals("UMLS") && dbMap.containsKey(databaseId.ID)){
						add = true;
						umlsID = databaseId.ID;
					}	
				}
				if (add){
					List<DatabaseID> dbids = dbMap.get(umlsID);
					for (DatabaseID dbid: dbids)
						ontology.setDatabaseIDForConcept(concept.getID(), dbid);
				}
			}
		}
		return ontology;
	}
}
