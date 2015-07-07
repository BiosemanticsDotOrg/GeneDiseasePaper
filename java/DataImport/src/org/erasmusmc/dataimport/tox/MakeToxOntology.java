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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.TextFileUtilities;

public class MakeToxOntology {
	public static int vocID = -1500;
	public static String vocName = "TOX";
	public static String thesarusTermsAsText = "/home/khettne/Projects/ToxThesaurus/toxGlossary_withDefinitions.txt";
	public static String termsToRemoveFileName = "termsToRemoveToxGlossary.txt";
	public static String ontologyName = "toxGlossary_290509";
	public static void main(String[] args) {
		MakeToxOntology make = new MakeToxOntology();
		make.run();
	}
	public void run(){
		Set<String> termsToRemove = getUndesiredTermsToFilterOut(termsToRemoveFileName);
		OntologyStore ontology = new OntologyStore();
		ontology.setName(ontologyName);
		Concept generalVocabulary = new Concept(vocID);
		generalVocabulary.setName(vocName);
		ontology.setConcept(generalVocabulary);	
		int cui = 2900000;
		List<String> terms = TextFileUtilities.loadFromFile(thesarusTermsAsText);
		for (String termline: terms){
			Concept concept = new Concept(cui++);
			System.out.println(cui);
			String[] parts = termline.split("\\|");
			String definition = parts[1].trim();
			concept.setDefinition(definition);
			List<TermStore> termStorelist = new ArrayList<TermStore>();
			String[] termlist = parts[0].split(";");
			for (String term: termlist){
				term = term.trim();
				if (term.length()!=0 && !termsToRemove.contains(term)){
					concept.setName(term);
					termStorelist.add(new TermStore(term));
				}
			}
			if (!termStorelist.isEmpty()){
				concept.setTerms(termStorelist);
				ontology.setConcept(concept);
				Relation vocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, vocID);
				ontology.setRelation(vocRelation);
			}
		}
		OntologyCurator curator = new OntologyCurator();
		curator.curateAndPrepare(ontology);	    
		OntologyManager manager = new OntologyManager();
		manager.deleteOntology(ontologyName);
		manager.dumpStoreInDatabase(ontology);
	}
	public static Set<String> getUndesiredTermsToFilterOut(String filename){
		Set<String> result = new HashSet<String>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(MakeToxOntology.class.getResourceAsStream(filename)));
		try {
			while (bufferedReader.ready()) {
				result.add(bufferedReader.readLine().trim().toLowerCase());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}


}
