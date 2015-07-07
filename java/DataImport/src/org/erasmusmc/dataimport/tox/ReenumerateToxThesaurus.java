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

package org.erasmusmc.dataimport.tox;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;

public class ReenumerateToxThesaurus {

	public static void main(String[] args) {
		OntologyStore newOntology = new OntologyStore();
		newOntology.setName("toxGlossary_310811");

		int semID = 169;
		Concept semtype = new Concept(-semID);
		semtype = new Concept(-semID);
		semtype.setDefinition("A concept which is of interest because it pertains to the carrying out of a process or activity.");
		semtype.setName("Functional Concept");
		newOntology.setConcept(semtype);

		int vocID = 1500;
		Concept voc = new Concept(-vocID);
		voc.setName("TOX");
		newOntology.setConcept(voc);

		OntologyManager manager = new OntologyManager();
		OntologyStore ontology = manager.fetchStoreFromDatabase("toxGlossary_290509");
		for (Concept concept: ontology){
			if (concept.getID()>0){
				Concept newConcept = new Concept(concept.getID()+99000);
				newConcept.setName(concept.getName());
				newConcept.setTerms(concept.getTerms());
				newConcept.setDefinition(concept.getDefinition());
				newOntology.setConcept(newConcept);
				Relation vocrelation = new Relation(newConcept.getID(), DefaultTypes.fromVocabulary, -1500);
				newOntology.setRelation(vocrelation);
				Relation semrelation = new Relation(newConcept.getID(), DefaultTypes.isOfSemanticType, -semID);
				newOntology.setRelation(semrelation);
			}
		}
		manager.dumpStoreInDatabase(newOntology);
	}

}
