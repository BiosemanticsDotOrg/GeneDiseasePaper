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

package org.erasmusmc.ontology.ontologyutilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class MapConceptWikiIDsToOntology {
	public static String ontologyPath = "/home/khettne/temp/UMLS2010ABHomologeneJochemToxV1_6_def.ontology";
	public static String newOntologyPath = "/home/khettne/Thesauri/UMLS2010ABHomologeneJochemToxV1_6.ontology";

	public static String cWikiMappingFile = "/home/khettne/Projects/Anni Update/mapping.csv";
	public static void main(String[] args) {
		OntologyFileLoader loader = new OntologyFileLoader();
		OntologyStore ontology = loader.load(ontologyPath);
		ontology = addConceptWikiId(ontology);
		loader.save(ontology, newOntologyPath);
	}


	public static Map<String, String> getConceptWikiMap(String filename){
		Map<String,String> cWikiMap = new HashMap<String, String>();
		ReadTextFile textFile = new ReadTextFile(filename);
		Iterator<String> fileIterator = textFile.getIterator();
		while (fileIterator.hasNext()) {
			String line = fileIterator.next();
			if (line.length() != 0) {
				String[] columns = line.split("\t");
				if (columns[2].equals("UMLS") || columns[2].equals("SP")){
					cWikiMap.put(columns[1], columns[0]);
				}
			}
		}

		return cWikiMap;		
	}

	public static OntologyStore addConceptWikiId(OntologyStore ontology){
		Map<String,String> cWikiMap = getConceptWikiMap(cWikiMappingFile);
		for (Concept concept: ontology){
			if (concept.getID()>-1){
				List<DatabaseID> dblist = ontology.getDatabaseIDsForConcept(concept.getID());
				List<DatabaseID> dbIdsToAdd = new ArrayList<DatabaseID>();
				for (DatabaseID id: dblist){
					if (id.database.equals("SP") || id.database.equals("UMLS")){
						String dbID = id.ID;
						if(cWikiMap.containsKey(dbID)){
							DatabaseID databaseID = new DatabaseID("WIKI", cWikiMap.get(dbID));
							dbIdsToAdd.add(databaseID);
						}
					}
				}
				if (!dbIdsToAdd.isEmpty()){
					for (DatabaseID id: dbIdsToAdd)
						ontology.setDatabaseIDForConcept(concept.getID(), id);
				}
			}
		}
		return ontology;
	}
}