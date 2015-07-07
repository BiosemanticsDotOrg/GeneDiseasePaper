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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class UMLSGenelistJochemToxMerger {
	
	public static String umlsGeneChemOntologyName = "UMLS2010ABHomologeneJochemV1_1";
	public static String toxGlossaryName = "toxGlossary_310811";
	public static String mergedOntologyName = "UMLS2010ABHomologeneJochemToxV1_6";
	public static String normCacheFileName = "/home/public/Peregrine/standardNormCache2006.bin";
	public static String tempThesaurusPath = "/home/khettne/temp/overlapTestUMLStox.ontology";
	public static String thesaurusPath = "/home/khettne/temp/UMLS2010ABHomologeneJochemToxV1_6.ontology";
public static WriteTextFile mergelog = new WriteTextFile("/home/khettne/temp/mergelogTox.log");
	
	public String ontologyName;
	public Integer geneVocIDLimit = 3000000; // Set to -1 to use GENE voc to
	// identify gene ontology instead
	// (much slower)
	public Integer toxVocIDLimit = 2999000;
	private List<CUImap> mappingsFromToCUI = new ArrayList<CUImap>();
	List<Concept> checkForToxOverlap = new ArrayList<Concept>();

	public static void main(String[] args) {
		System.out.println(StringUtilities.now() + "\tLoading ontologies");

		//OntologyManager manager = new OntologyManager();
		//Ontology umls = manager.fetchStoreFromDatabase(umlsGeneChemOntologyName);
		OntologyFileLoader loader = new OntologyFileLoader();
		Ontology umls = loader.load("/home/khettne/temp/"+umlsGeneChemOntologyName+".ontology");
		OntologyManager toxManager = new OntologyManager();
		Ontology toxlist = toxManager.fetchStoreFromDatabase(toxGlossaryName);
		new UMLSGenelistJochemToxMerger(umls, toxlist, mergedOntologyName);
	}

	public UMLSGenelistJochemToxMerger(Ontology umls, Ontology toxlist, String name) {		
		ontologyName = name;
		concatenate(umls, toxlist);
		System.gc();
		mergedOntology = ontologyManager.fetchClient(ontologyName);

		System.out.println(StringUtilities.now() + "\tEvaluating UMLS concepts");
		OntologyStore tempThesaurus = findToxConceptsInUMLS();
		System.gc();
		checkForToxOverlap(tempThesaurus);
		mergelog.close();
		System.gc();
		OntologyStore toMerge = ontologyManager.fetchStoreFromDatabase(ontologyName);
		merge(toMerge);
		OntologyFileLoader loader = new OntologyFileLoader();
		loader.save((OntologyStore)toMerge, thesaurusPath);
		ontologyManager.deleteOntology(ontologyName);
		System.out.println(StringUtilities.now() + "\tDone");
	}

	private void merge(OntologyStore toMerge){
		for (CUImap cuimap: mappingsFromToCUI) {
			OntologyUtilities.mergeConcepts(toMerge, cuimap.from, cuimap.to);
		}
	}

	private OntologyStore findToxConceptsInUMLS() {
		OntologyStore tempThesaurus = new OntologyStore();
		tempThesaurus.setName("overlapTestUMLStox");
		System.gc();

		// Evaluate UMLS concepts:
		Iterator<Concept> conceptIterator = mergedOntology.getConceptIterator();
		while (conceptIterator.hasNext()) {
			Concept concept = conceptIterator.next();
			//Evaluate UMLS concepts
			if (concept.getID() >= toxVocIDLimit && concept.getID() < geneVocIDLimit) {
				checkForToxOverlap.add(concept);
				tempThesaurus.setConcept(concept);
			}
			else {
				tempThesaurus.setConcept(concept);
			} // Its umls concept, so add to tempthesaurus for overlap testing
		}
		OntologyFileLoader loader = new OntologyFileLoader();
		loader.save(tempThesaurus,tempThesaurusPath);
		return tempThesaurus;
	}
	private void checkForToxOverlap(Ontology tempThesaurus){
		Map<Integer, Map<Integer, List<String>>> cui2cuis = fetchToxOverlap(tempThesaurus);
		for (Concept concept: checkForToxOverlap) {
			String report = overlapsWithTox(concept, cui2cuis);
			if (report != null) {
				mergelog.writeln("MERGE " + concept.getID() + " BECAUSE " + report);
			}
		}
	}

	// Check whether the string ends on a letter or number or mix
	public boolean endsWithID(String name) {
		int tokenstart = -1;
		for (int i = name.length() - 2; i > 0; i--)
			if (!Character.isLetterOrDigit(name.charAt(i))) {
				tokenstart = i + 1;
				break;
			}
		if (tokenstart != -1)
			if (tokenstart == name.length() - 1 || name.substring(tokenstart, name.length()).equals(name.substring(tokenstart, name.length()).toUpperCase()))
				return true;

		return false;
	}

	// Check whether the terms of the concept overlap sufficiently with a tox concept
	private String overlapsWithTox(Concept concept, Map<Integer, Map<Integer, List<String>>> cui2cuis) {
		Map<Integer, List<String>> id2overlap = cui2cuis.get(concept.getID());
		if (id2overlap == null)
			return null;
		int maxOverlap = 0;
		int maxLFOverlap = 0;
		int maxOverlapConcept = 0;
		for (Entry<Integer, List<String>> entry: id2overlap.entrySet()) {
			boolean toxVoc = false;
			if (!(entry.getKey() >= toxVocIDLimit && entry.getKey() < geneVocIDLimit))
				toxVoc = true;
			if (toxVoc) { // it is another gene: look at overlap
				int overlap = entry.getValue().size();
				if (overlap >= maxOverlap) {
					int lfOverlap = 0;
					for (String term: entry.getValue())
						if (!OntologyUtilities.isGeneSymbol(term))
							lfOverlap++;
					if (overlap > maxOverlap || lfOverlap > maxLFOverlap) {
						maxOverlap = overlap;
						maxLFOverlap = lfOverlap;
						maxOverlapConcept = entry.getKey();
					}
				}
			}
		}

		if (maxOverlap > 0) {
			int termcount = concept.getTerms().size();
			if (((maxOverlap == termcount) || (maxOverlap >= termcount / 2 && maxLFOverlap > 0)) || (maxLFOverlap == termcount) || (maxLFOverlap > 1)) {
				StringBuffer report = new StringBuffer();
				report.append("OVERLAP WITH CONCEPT " + maxOverlapConcept + " (");
				for (String term: id2overlap.get(maxOverlapConcept)) {
					report.append(term);
					report.append(";");
				}
				report.append(")");
				mappingsFromToCUI.add(new CUImap(concept.getID(), maxOverlapConcept));
				return report.toString();
			}
		}
		return null;
	}

	private Map<Integer, Map<Integer, List<String>>> fetchToxOverlap(Ontology tempThesaurus) {
		System.out.println(StringUtilities.now() + "\tExamining overlap");
		HomonymAnalyzer analyzer = new HomonymAnalyzer();
		analyzer.normaliser.loadCacheBinary(normCacheFileName);
		Iterator<Concept> iterator = tempThesaurus.getConceptIterator();
		while (iterator.hasNext())
			for (TermStore term: iterator.next().getTerms()) {
				term.text = OntologyUtilities.tokenizeAndRemoveStopwordsFromString(term.text, analyzer.stopwords);
				OntologyUtilities.setDefaultMatchingFlags(term);
			}
		analyzer.setOntology(tempThesaurus);
		return analyzer.compareConcepts();
	}

	private void concatenate(Ontology umlsGene, Ontology toxlist) {
		// Concatenate and dump into one ontology(client):
		System.out.println(StringUtilities.now() + "\tConcatening ontologies");
		umlsGene.setName(ontologyName);
		ontologyManager.deleteOntology(ontologyName);
		mergedOntology = umlsGene;

		Iterator<Concept> conceptIterator = toxlist.getConceptIterator();
		while (conceptIterator.hasNext()) {
			Concept concept = conceptIterator.next();
			if (concept.getID()  < -999) {// vocabulary ID for toxlist: make sure no
				// overlap with umls and Gene vocs
				Concept newConcept = new Concept(concept.getID()-3000);
				newConcept.setDefinition(concept.getDefinition());
				newConcept.setName(concept.getName());
				newConcept.setTerms(concept.getTerms());
				concept = newConcept;
			}

			mergedOntology.setConcept(concept);
			chemCUIs.add(concept.getID());

			List<DatabaseID> databaseIDs = toxlist.getDatabaseIDsForConcept(concept.getID());
			if (databaseIDs != null)
				for (DatabaseID databaseID: databaseIDs)
					mergedOntology.setDatabaseIDForConcept(concept.getID(), databaseID);
		}
		for (Relation relation: toxlist.getRelations()) {
			if (relation.object < -999)
				relation.object -= 3000;
			if (relation.subject < -999)
				relation.subject -= 3000;
			mergedOntology.setRelation(relation);
		}
		System.out.println(StringUtilities.now() + "\tdumping "+mergedOntology.getName()+" in database");
		ontologyManager.dumpStoreInDatabase((OntologyStore) mergedOntology);
	}

	private Ontology mergedOntology;
	private Set<Integer> chemCUIs = new HashSet<Integer>();
	private static OntologyManager ontologyManager = new OntologyManager();
	
	private class CUImap {
		int from;
		int to;

		public CUImap(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}
}
