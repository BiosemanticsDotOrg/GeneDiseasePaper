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
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class RewriteFurther {

	public OntologyStore run(OntologyStore originalOntology, String logfilePath) {

		System.out.println("Starting script: "+StringUtilities.now());

		/** Create log */
		WriteTextFile logFile = new WriteTextFile(logfilePath);

		OntologyStore newOntology = new OntologyStore();

		Set<Integer> includedCUIs = new HashSet<Integer>();

		/** Set ontology variables*/
		int rewrittenTermsCount = 0;
		int suppressedTermsCount = 0;
		boolean suppressed = false;
		Concept concept = null;

		System.out.println("Rewriting... ");

		Iterator<Concept> conceptIterator = originalOntology.getConceptIterator();
		int lineCount = 0;
		while (conceptIterator.hasNext()) {
			lineCount++;
			if (lineCount % 10000 == 0)
				System.out.println(lineCount);
			concept = conceptIterator.next();
			if (concept.getID() > 0) {
				List<TermStore> terms = concept.getTerms();
				Iterator<TermStore> termIterator = terms.iterator();
				while (termIterator.hasNext()) {
					TermStore term = termIterator.next();
					if (term.text.startsWith("CID")
							|| term.text.startsWith("ZINC0") 
							|| term.text.startsWith("AIDS") 
							|| term.text.startsWith("MOLI") 
							|| term.text.startsWith("N/A")
							|| term.text.startsWith("MLS0") 
							|| term.text.startsWith("SMR0") 
							|| term.text.startsWith("UgiM1_")          
							|| term.text.contains("Beilstein Handbook Reference")
					){
						suppressed = true;
						suppressedTermsCount++;
						termIterator.remove();
						logFile.writeln("SUPPRESSED: "+ term.text);
					}
					if (!suppressed){
						List<String> words = StringUtilities.mapToWords(term.text);
						if (words.size() > 2 || term.text.length() > 10) {
							boolean check = true;
							for (String word: words) {
								if (!(StringUtilities.countsCharactersInUpperCase(word) == word.length())) {
									check = false;
								}
							}
							if (check) {
								logFile.writeln("REWRITTEN "+term.text+" TO: "+term.text.toLowerCase() );
								term.text = term.text.toLowerCase();
								rewrittenTermsCount++;
							}
						}
					}
					suppressed = false;
				}

				concept.setTerms(terms);
				OntologyUtilities.removeDuplicateTerms(concept.getTerms());
			}
			if (!concept.getTerms().isEmpty() || concept.getID() < 0) {
				includedCUIs.add(concept.getID());
				newOntology.setConcept(concept);
			}
		}
//		Copy relationships:
		List<Relation> relations = originalOntology.getRelations();
		for (Relation relation: relations)
			if (includedCUIs.contains(relation.subject) && includedCUIs.contains(relation.object))
				newOntology.setRelation(relation);

		// Copy databaseIDs:
		List<DatabaseID> databaseIDs;
		for (int cui: includedCUIs) {
			databaseIDs = originalOntology.getDatabaseIDsForConcept(cui);
			if (databaseIDs != null)
				for (DatabaseID databaseID: databaseIDs)
					newOntology.setDatabaseIDForConcept(cui, databaseID);
		}
		/**  Save to ontologyfile and log */
		System.out.println("Closing logfile and saving new ontology: "+StringUtilities.now());
		logFile.close();
		System.out.println(rewrittenTermsCount+ " terms were rewritten");
		System.out.println(suppressedTermsCount+ " terms were suppressed");
		return newOntology;
	}

}
