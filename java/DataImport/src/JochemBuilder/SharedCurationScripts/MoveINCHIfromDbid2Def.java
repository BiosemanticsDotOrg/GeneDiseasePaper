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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.utilities.StringUtilities;

public class MoveINCHIfromDbid2Def {

	public OntologyStore run(OntologyStore ontology) {
		OntologyStore newOntology = new OntologyStore();
		String name = ontology.getName();
		newOntology.setName(name);
		Set<Integer> includedCUIs = new HashSet<Integer>();
		System.out.println("Iterating. "+StringUtilities.now());
		Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		int lineCount = 0;
		while (conceptIterator.hasNext()) {
			lineCount++;
			if (lineCount % 10000 == 0)
				System.out.println(lineCount);
			Concept concept = conceptIterator.next();
			includedCUIs.add(concept.getID());
			newOntology.setConcept(concept);
			if (concept.getID()>=0){
				List<DatabaseID> toRemove = new ArrayList<DatabaseID>();
				String definition = concept.getDefinition();
				if (definition.endsWith("\\;")){
					definition = definition.substring(0, definition.length()-2);
				} 
				List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
				boolean first = true;
				StringBuffer buffer = new StringBuffer();
				for (DatabaseID databaseID: databaseIDs){
					if (databaseID.database.equals("INCH")){
						toRemove.add(databaseID);
						String inchi = databaseID.ID;
						if (first){
							buffer.append(inchi);
							first = false;
						}else
							buffer.append("\n"+inchi);
					}
				}
				String inchis = buffer.toString();
				if (definition.length()+inchis.length()<=10000){
					if (definition.isEmpty()){
						definition = inchis;						
					}else
						definition = definition+"\n"+inchis;
				}else 
					definition = definition+"\n"+"InChI string exceeded size limit";
				databaseIDs.removeAll(toRemove);
				for (DatabaseID id: databaseIDs)
					newOntology.setDatabaseIDForConcept(concept.getID(), id);
				concept.setDefinition(definition);
			}
		}
		//		 Copy relationships:
		List<Relation> relations = ontology.getRelations();
		for (Relation relation: relations)
			if (includedCUIs.contains(relation.subject)&& includedCUIs.contains(relation.object))
				newOntology.setRelation(relation);
		return newOntology;
	}
}
