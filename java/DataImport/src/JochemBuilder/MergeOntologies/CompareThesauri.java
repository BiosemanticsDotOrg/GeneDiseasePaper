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

package JochemBuilder.MergeOntologies;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class CompareThesauri {
	public  Integer numberOfTerms = 0;
	public  Integer numberOfSignificantTerms = 0;
	public Integer numberOfConcepts = 0;
	public Integer numberOfOverlappingIdentifiers = 0;
	public Set<Integer> numberOfsignificantConcepts = new HashSet<Integer>();

	public Map<String, Set<Integer>> allDatabaseIDsWithCuis(Ontology ontology){
		Map<String, Set<Integer>> allDatabaseIds = new HashMap<String, Set<Integer>>();
		Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		while (conceptIterator.hasNext()){
			Concept concept = conceptIterator.next();
			List<DatabaseID> dbIds = ontology.getDatabaseIDsForConcept(concept.getID());
			Iterator<DatabaseID> idIterator = dbIds.iterator();
			while (idIterator.hasNext()){
				DatabaseID dbid = idIterator.next();
				StringBuffer id = new StringBuffer();
				id.append(dbid.database+"_");
				id.append(dbid.ID);
				String idString = id.toString();
				if (allDatabaseIds.containsKey(idString)){
					Set<Integer> cuis = allDatabaseIds.get(idString);
					cuis.add(concept.getID());
					allDatabaseIds.put(idString, cuis);
				}else {
					Set<Integer> cuis = new HashSet<Integer>();
					cuis.add(concept.getID());
					allDatabaseIds.put(idString, cuis);
				}
			}      
		}
		return allDatabaseIds;    
	}
	public OntologyStore compareConceptsInOneThesaurusBasedOnIdentifiers(Concept conceptToCompare, OntologyStore ontology, Map<String, Set<Integer>> allDbIds, Integer startID, WriteTextFile writeFile) {
		boolean match = false;
		List<DatabaseID> databaseIDsToCompare = ontology.getDatabaseIDsForConcept(conceptToCompare.getID());
		Set<String> databaseStringsToCompare = new HashSet<String>();
		for (DatabaseID id: databaseIDsToCompare){
			StringBuffer idString = new StringBuffer();
			idString.append(id.database+"_");
			idString.append(id.ID);
			databaseStringsToCompare.add(idString.toString());
		}
		Set<Integer> cuis = getCuisForDatabaseIDsToCompare(databaseStringsToCompare, allDbIds, ontology);
		Map<Integer,Concept> concepts = ontology.getConcepts(cuis);
		Collection<Concept> conceptList = concepts.values();
		Iterator<Concept> conceptListIterator = conceptList.iterator();
		while (conceptListIterator.hasNext()){
			Concept concept = conceptListIterator.next();
			if (concept!=null && concept.getID()>startID && !(concept.getID().equals(conceptToCompare.getID()))){
				List<DatabaseID> databaseIDsForConcept = ontology.getDatabaseIDsForConcept(concept.getID());
				Set<String> databaseStringsForConcept = new HashSet<String>();        
				for (DatabaseID id: databaseIDsForConcept){
					StringBuffer idString = new StringBuffer();
					idString.append(id.database+"_");
					idString.append(id.ID);
					databaseStringsForConcept.add(idString.toString());
				}
				Iterator<String> idIterator = databaseStringsForConcept.iterator();
				while (idIterator.hasNext()){
					String id = idIterator.next();
					if (databaseStringsToCompare.contains(id)){
						match = true;
					}
				}
			}
			if (match){
				numberOfConcepts++;
				OntologyUtilities.mergeConcepts(ontology, conceptToCompare.getID(), concept.getID());
				List<TermStore> terms = concept.getTerms();
				OntologyUtilities.removeDuplicateTerms(terms);
				concept.setTerms(terms);
				String definition = concept.getDefinition();
				if (definition.length()!=0){
					if (!definition.endsWith(".") && definition.length()<=1024){
						definition = definition+".";
					} else if (!definition.endsWith(".") && definition.length()>1024){
						definition = definition.substring(0, 1023)+".";
					} else if (definition.length()>1024){
						definition = definition.substring(0, 1023)+".";
					}
					concept.setDefinition(definition);
				}
				writeFile.writeln(concept.getID()+"\t"+concept.getName()+"\t"+conceptToCompare.getID()+"\t"+conceptToCompare.getName());
			}
			match = false;
		}
		return ontology;
	}
	public Set<Integer> getCuisForDatabaseIDsToCompare(Set<String> dbIDsToCompare, Map<String, Set<Integer>> allDatabaseIDs, Ontology ontology){
		Set<Integer> cuis = new HashSet<Integer>();
		for (String dbid: dbIDsToCompare){
			Set<Integer> cuisPerDbId = new HashSet<Integer>();
			cuisPerDbId = allDatabaseIDs.get(dbid);
			if (cuisPerDbId!=null){
				cuis.addAll(cuisPerDbId);
			}
		}
		return cuis;
	}

}
