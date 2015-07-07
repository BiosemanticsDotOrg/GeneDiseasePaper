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

package JochemBuilder.SharedCurationScripts;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.utilities.StringUtilities;

public class SaveOnlyCASandInchiEntries {
	public OntologyStore run(OntologyStore ontology){
		System.out.println("Starting script: "+StringUtilities.now());
		Set<Integer> includedCUIs = new HashSet<Integer>();    
		OntologyStore newOntology = new OntologyStore();
		Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		while (conceptIterator.hasNext()){
			Concept concept = conceptIterator.next();
			if (concept.getID()<0){
				newOntology.setConcept(concept);
				includedCUIs.add(concept.getID());
			}else{
				List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
				boolean add = false;
				for (DatabaseID databaseID: databaseIDs){
					String db = databaseID.database;
					if (db.equals("CAS") || db.equals("INCH")){
						add = true;
					}
				}
				if (add){        
					newOntology.setConcept(concept);
					includedCUIs.add(concept.getID());
				}
			}
		}
//		Copy relationships:
		List<Relation> relations = ontology.getRelations();
		for (Relation relation: relations)
			if (includedCUIs.contains(relation.subject) && includedCUIs.contains(relation.object))
				newOntology.setRelation(relation);

		// Copy databaseIDs:
		List<DatabaseID> databaseIDs;
		for (Integer cui: includedCUIs) {
			databaseIDs = ontology.getDatabaseIDsForConcept(cui);
			if (databaseIDs != null)
				for (DatabaseID databaseID: databaseIDs)
					newOntology.setDatabaseIDForConcept(cui, databaseID);
		}
		return newOntology;
	}

}
