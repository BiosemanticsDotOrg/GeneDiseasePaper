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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.CurationFileParser.DatabaseIDmap;
//import org.erasmusmc.utilities.LVGNormaliser;
import org.erasmusmc.textMining.LVG.LVGNormaliser;

public class OntologyCurator {

	private CurationFileParser curationFileParser;
	private NormaliseUsingLVG lvg;

	public OntologyCurator(String filename) {
		curationFileParser = new CurationFileParser(filename);
		lvg = new NormaliseUsingLVG();
	}

	public OntologyCurator() {

	}

	public void curateAndPrepare(Ontology ontology) {
		if (curationFileParser != null){
			removeConcepts(ontology);
			mapConcepts(ontology);
			removeSuppressedTermsByDatabaseID(ontology);
			addTermsByDatabaseID(ontology);
		}

		for (Concept concept : ontology){
			boolean ofGeneVoc = OntologyUtilities.hasGeneVoc(concept, ontology);
			boolean ofChemVoc = OntologyUtilities.hasChemVoc(concept, ontology);

			Set<String> previousTerms = new HashSet<String>();
			Iterator<TermStore> termIterator = concept.getTerms().iterator();
			while (termIterator.hasNext()) {
				TermStore term = termIterator.next();

				//Set matching flags:
				if (ofGeneVoc || ofChemVoc) 
					OntologyUtilities.setGeneChemMatchingFlags(term);
				else 
					OntologyUtilities.setDefaultMatchingFlags(term);

				//Remove duplicate terms:
				if (!previousTerms.add(term.text)) 
					termIterator.remove();
			}

			if (curationFileParser != null){ //Remove suppressed terms:
				removeSuppressedTermsPerVocabulary(ontology, concept);
				removeSuppressedTermsAllVocs(concept);
			}
		}
	}

	private void addTermsByDatabaseID(Ontology ontology) {
		for (DatabaseID databaseID: curationFileParser.addedTermsPerDatabaseID.keySet()) {
			Set<Integer> ids = ontology.getConceptIDs(databaseID);
			Set<String> addedTerms = curationFileParser.addedTermsPerDatabaseID.get(databaseID);
			for (Integer id: ids) {
				Concept concept = ontology.getConcept(id);
				for (String term : addedTerms)
					concept.getTerms().add(new TermStore(term));
			}
		}
	}

	private void removeSuppressedTermsPerVocabulary(Ontology ontology, Concept concept) {
		List<Relation> vocs = ontology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.fromVocabulary);
		Set<String> suppressedTerms = new HashSet<String>();
		for (Relation voc: vocs) {
			Set<String> temp = curationFileParser.suppressedTermsPerVoc.get(ontology.getConcept(voc.object).getName());
			if (temp != null) 
				suppressedTerms.addAll(temp);
		}
		removeTerms(concept, suppressedTerms);
	}

	private void removeSuppressedTermsByDatabaseID(Ontology ontology) {
		for (DatabaseID databaseID: curationFileParser.suppressedTermsPerDatabaseID.keySet()) {
			Set<Integer> ids = ontology.getConceptIDs(databaseID);
			Set<String> suppressedTerms = curationFileParser.suppressedTermsPerDatabaseID.get(databaseID);
			for (Integer id: ids) {
				Concept concept = ontology.getConcept(id);
				removeTerms(concept, suppressedTerms);
			}
		}
	}

	private void removeSuppressedTermsAllVocs(Concept concept) {
		Set<String> suppressedTerms = curationFileParser.suppressedTermsAllVocs;
		removeTerms(concept, suppressedTerms);
	}

	private void removeTerms(Concept concept, Set<String> suppressedTerms){
		Iterator<TermStore> termIterator = concept.getTerms().iterator();
		while (termIterator.hasNext()) {
			TermStore term = termIterator.next();
			String normTerm = CurationFileParser.normalizeTerm(term.text);
			String lvgNormTerm = "";
			if (!OntologyUtilities.isGeneSymbol(term.text))
				lvgNormTerm = lvg.lvgnormalise(term.text);
			if (suppressedTerms.contains(normTerm) || suppressedTerms.contains(lvgNormTerm)){
				termIterator.remove();
				System.out.println(concept.getID()+ "\t"+ concept.getName()+ "\t"+ term.text+ "\t"+ normTerm);
			}
		}
	}

	public void mapConcepts(Ontology ontology) {
		for (DatabaseIDmap databaseIDmap: curationFileParser.mappingsFromToDBID) {
			Set<Integer> fromIDs = ontology.getConceptIDs(databaseIDmap.from);
			Set<Integer> toIDs = ontology.getConceptIDs(databaseIDmap.to);
			for (Integer fromID: fromIDs) {
				for (Integer toID: toIDs) {
					if (fromID != toID) {
						OntologyUtilities.mergeConcepts(ontology, fromID, toID);
					}
				}
			}
		}
	}

	public void removeConcepts(Ontology ontology){
		for (DatabaseID databaseID: curationFileParser.suppressedWholeUMLSConcepts) {
			Set<Integer> ids = ontology.getConceptIDs(databaseID);
			for (Integer id: ids) {
				ontology.removeConcept(id);
			}
		}
	}

	private class NormaliseUsingLVG extends LVGNormaliser{
		public String lvgnormalise(String string){
			return externalnormalise(string);
		}
	}

}
