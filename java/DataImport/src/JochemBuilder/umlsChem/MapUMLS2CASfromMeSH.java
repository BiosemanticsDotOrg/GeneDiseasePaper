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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.ReadTextFile;

public class MapUMLS2CASfromMeSH {
	public OntologyStore run(OntologyStore ontology, String mapFileName){
		ReadTextFile mapFile = new ReadTextFile(mapFileName);
		Map<String, String> idToCAS = new HashMap<String, String>();
		Iterator<String> iterator = mapFile.getIterator();
		while(iterator.hasNext()){
			String line = iterator.next();
			String[] columns = line.split("\\|");
			String key = columns[1];
			String value = columns[0];
			idToCAS.put(key, value);
		}

		Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		while (conceptIterator.hasNext()){
			Concept concept = conceptIterator.next();
			List<DatabaseID> newDbIds = new ArrayList<DatabaseID>();
			List<DatabaseID> dbIds = ontology.getDatabaseIDsForConcept(concept.getID());
			for (DatabaseID dbId: dbIds){
				String db = dbId.database;
				if (db.equals("MESH")){
					String id = dbId.ID;
					if (idToCAS.containsKey(id)){
						newDbIds.add(new DatabaseID("CAS", idToCAS.get(id)));
					}
				}
			}
			if (!newDbIds.isEmpty()){
				for (DatabaseID id: newDbIds)
					ontology.setDatabaseIDForConcept(concept.getID(), id);
			}
		}
		return ontology;    
	}      

}
