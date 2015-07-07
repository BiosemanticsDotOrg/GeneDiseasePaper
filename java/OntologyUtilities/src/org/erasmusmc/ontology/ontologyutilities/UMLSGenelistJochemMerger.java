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
import java.util.TreeSet;
import java.util.Map.Entry;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class UMLSGenelistJochemMerger {
	public static String umlsGeneOntologyName = "UMLS2010ABHomologeneV5_1";
	public static String jochemName = "Jochem_V1_5";
	public static String mergedOntologyName = "UMLS2010ABHomologeneJochemV1_1";
	public static String normCacheFileName = "/home/public/Peregrine/standardNormCache2006.bin";
	public static String tempThesaurusPath = "/home/khettne/temp/overlapTestUMLSGeneChem_V1_1.ontology";
	public static String thesaurusPath = "/home/khettne/temp/UMLS2010ABHomologeneJochemV1_1.ontology";

	public static WriteTextFile removelog = new WriteTextFile("/home/khettne/temp/removelogUMLSGeneChem_V1_1.log");
	public static WriteTextFile mergelog = new WriteTextFile("/home/khettne/temp/mergelogUMLSGeneChem_V1_1.log");

	public String ontologyName;
	public Integer geneVocIDLimit = 3000000; // Set to -1 to use GENE voc to
	// identify gene ontology instead
	// (much slower)
	public Integer chemVocIDLimit = 4000000;
	List<Integer> removelist = new ArrayList<Integer>();
	private List<CUImap> mappingsFromToCUI = new ArrayList<CUImap>();
	Set<Integer> chemicalSemanticTypesToRemove = getChemicalSemanticTypesToRemove();
	Set<Integer> chemicalSemanticTypesToCheckForMerge = getChemicalSemanticTypesToCheckForMerge();

	public static void main(String[] args) {
		System.out.println(StringUtilities.now() + "\tLoading ontologies");

		OntologyManager manager = new OntologyManager();
		Ontology umlsGene = manager.fetchStoreFromDatabase(umlsGeneOntologyName);
		OntologyManager chemManager = new OntologyManager();
		Ontology chemlist = chemManager.fetchStoreFromDatabase(jochemName);

		new UMLSGenelistJochemMerger(umlsGene, chemlist, mergedOntologyName);
	}

	public UMLSGenelistJochemMerger(Ontology umlsGene, Ontology chemlist, String name) {		
		ontologyName = name;
		concatenate(umlsGene, chemlist);
		System.gc();
		mergedOntology = ontologyManager.fetchClient(ontologyName);

		System.out.println(StringUtilities.now() + "\tEvaluating concepts");
		findChemicalsInUMLSGenelist();
		remove();
		System.gc();		
		OntologyStore toMerge = ontologyManager.fetchStoreFromDatabase(ontologyName);
		merge(toMerge);
		System.gc();
		OntologyFileLoader loader = new OntologyFileLoader();
		loader.save((OntologyStore)toMerge, thesaurusPath);
		ontologyManager.deleteOntology(ontologyName);
		System.out.println(StringUtilities.now() + "\tDone");
	}

	private void remove() {
		System.out.println(StringUtilities.now() + "\tRemoving " + removelist.size() + " concepts");
		for (Integer cui: removelist) {
			mergedOntology.removeConcept(cui);
		}
	}

	private void merge(OntologyStore toMerge){
		for (CUImap cuimap: mappingsFromToCUI) {
			OntologyUtilities.mergeConcepts(toMerge, cuimap.from, cuimap.to);
		}
	}

	private void findChemicalsInUMLSGenelist() {
		List<Concept> checkForUMLSOverlap = new ArrayList<Concept>();
		List<Concept> checkForGeneOverlap = new ArrayList<Concept>();
		
		OntologyStore tempThesaurus = new OntologyStore();
		tempThesaurus.setName("overlapTestUMLSGeneChem");
		System.gc();

		// Evaluate UMLS and Chemical concepts:
		Iterator<Concept> conceptIterator = mergedOntology.getConceptIterator();
		while (conceptIterator.hasNext()) {
			Concept concept = conceptIterator.next();
			//Evaluate UMLS concepts
			if (concept.getID() < geneVocIDLimit) {
				// Check semantic types
				boolean potentialChemical = false;
				for (Relation relation: mergedOntology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.isOfSemanticType)) {
					if (chemicalSemanticTypesToRemove.contains(relation.object)){
						removelist.add(concept.getID());
					} else if (chemicalSemanticTypesToCheckForMerge.contains(relation.object)){
						potentialChemical = true;
					}
				}
				if (potentialChemical) {
					tempThesaurus.setConcept(concept);
					checkForUMLSOverlap.add(concept);
				}
			}
			// Evaluate chemical concepts
			else if (concept.getID() >= chemVocIDLimit){
				if (concept.getName().toLowerCase().contains(" protein, ")){
					removelist.add(concept.getID());
					removelog.writeln("REMOVE " + concept.getID() + " BECAUSE OF protein pattern (" + concept.getName() + ")");
				} else {
					tempThesaurus.setConcept(concept);
					checkForGeneOverlap.add(concept);
				}
			}
			else {
				tempThesaurus.setConcept(concept);
			} // Its gene concept, so add to tempthesaurus for overlap testing
		}
		OntologyFileLoader loader = new OntologyFileLoader();
		loader.save(tempThesaurus, tempThesaurusPath);
		
		Map<Integer, Map<Integer, List<String>>> cui2cuis = fetchUMLSChemOverlap(tempThesaurus);
		for (Concept concept: checkForUMLSOverlap) {
			String report = overlapsWithUMLS(concept, cui2cuis);
			if (report != null) {
				mergelog.writeln("MERGE " + concept.getID() + " BECAUSE " + report);
			}
		}
		
		Map<Integer, Map<Integer, List<String>>> cuiToCuis = fetchGeneChemOverlap(tempThesaurus);
		for (Concept concept: checkForGeneOverlap) {
			String report = overlapsWithGene(concept, cuiToCuis);
			if (report != null) {
				mergelog.writeln("MERGE " + concept.getID() + " BECAUSE " + report);
			}
		}
		removelog.close();
		mergelog.close();
	}
/**	private void checkForUMLSOverlap(Ontology tempThesaurus){
		Map<Integer, Map<Integer, List<String>>> cui2cuis = fetchUMLSChemOverlap(tempThesaurus);
		for (Concept concept: checkForUMLSOverlap) {
			String report = overlapsWithUMLS(concept, cui2cuis);
			if (report != null) {
				mergelog.writeln("MERGE " + concept.getID() + " BECAUSE " + report);
			}
		}
	}*/
/**	private void checkForGeneOverlap(Ontology tempThesaurus){
		Map<Integer, Map<Integer, List<String>>> cui2cuis = fetchGeneChemOverlap(tempThesaurus);
		for (Concept concept: checkForGeneOverlap) {
			String report = overlapsWithGene(concept, cui2cuis);
			if (report != null) {
				mergelog.writeln("MERGE " + concept.getID() + " BECAUSE " + report);
			}
		}
	}*/

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

	// Check whether the terms of the concept overlap sufficiently with a umls chemical
	private String overlapsWithUMLS(Concept concept, Map<Integer, Map<Integer, List<String>>> cui2cuis) {
		Map<Integer, List<String>> id2overlap = cui2cuis.get(concept.getID());
		if (id2overlap == null)
			return null;
		int maxOverlap = 0;
		int maxLFOverlap = 0;
		int maxOverlapConcept = 0;
		for (Entry<Integer, List<String>> entry: id2overlap.entrySet()) {
			boolean chemVoc = false;
			if (entry.getKey() > chemVocIDLimit)
				chemVoc = true;
			if (chemVoc) { // it is another chemical: look at overlap
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
	// Check whether the terms of the concept overlap sufficiently with a gene
	private String overlapsWithGene(Concept concept, Map<Integer, Map<Integer, List<String>>> cui2cuis) {
		Map<Integer, List<String>> id2overlap = cui2cuis.get(concept.getID());
		if (id2overlap == null)
			return null;
		int maxOverlap = 0;
		int maxLFOverlap = 0;
		int maxOverlapConcept = 0;
		for (Entry<Integer, List<String>> entry: id2overlap.entrySet()) {
			boolean geneVoc = false;
			if (entry.getKey() >= geneVocIDLimit && entry.getKey() < chemVocIDLimit)
				geneVoc = true;
			if (geneVoc) { // it is another gene: look at overlap
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

	private Map<Integer, Map<Integer, List<String>>> fetchUMLSChemOverlap(Ontology tempThesaurus) {
		System.out.println(StringUtilities.now() + "\tExamining overlap");
		HomonymAnalyzer analyzer = new HomonymAnalyzer();
		analyzer.normaliser.loadCacheBinary(normCacheFileName);
		Iterator<Concept> iterator = tempThesaurus.getConceptIterator();
		while (iterator.hasNext())
			for (TermStore term: iterator.next().getTerms()) {
				term.text = OntologyUtilities.tokenizeAndRemoveStopwordsFromString(term.text, analyzer.stopwords);
				OntologyUtilities.setGeneChemMatchingFlags(term);
			}
		analyzer.setOntology(tempThesaurus);
		return analyzer.compareConcepts();
	}

	private Map<Integer, Map<Integer, List<String>>> fetchGeneChemOverlap(Ontology tempThesaurus) {
		System.out.println(StringUtilities.now() + "\tExamining overlap");
		HomonymAnalyzer analyzer = new HomonymAnalyzer();
		analyzer.normaliser.loadCacheBinary(normCacheFileName);
		analyzer.stopwords = HomonymAnalyzer.getDefaultStopWordsForIndexing();
		analyzer.stopwords.add("human");
		analyzer.stopwords.add("protein");
		analyzer.stopwords.add("gene");
		analyzer.stopwords.add("antigen");
		analyzer.stopwords.add("product");
		Iterator<Concept> iterator = tempThesaurus.getConceptIterator();
		while (iterator.hasNext())
			for (TermStore term: iterator.next().getTerms()) {
				term.text = OntologyUtilities.tokenizeAndRemoveStopwordsFromString(term.text, analyzer.stopwords);
				OntologyUtilities.setGeneChemMatchingFlags(term);
			}
		analyzer.setOntology(tempThesaurus);
		return analyzer.compareConcepts();
	}

	private void concatenate(Ontology umlsGene, Ontology chemlist) {
		// Concatenate and dump into one ontology(client):
		System.out.println(StringUtilities.now() + "\tConcatening ontologies");
		umlsGene.setName(ontologyName);
		ontologyManager.deleteOntology(ontologyName);
		mergedOntology = umlsGene;

		Iterator<Concept> conceptIterator = chemlist.getConceptIterator();
		while (conceptIterator.hasNext()) {
			Concept concept = conceptIterator.next();
			if (concept.getID() < -999) {// vocabulary ID for chemlist: make sure no
				// overlap with umls and Gene vocs
				Concept newConcept = new Concept(concept.getID()-2000);
				newConcept.setDefinition(concept.getDefinition());
				newConcept.setName(concept.getName());
				newConcept.setTerms(concept.getTerms());
				concept = newConcept;
			}

			mergedOntology.setConcept(concept);
			chemCUIs.add(concept.getID());

			List<DatabaseID> databaseIDs = chemlist.getDatabaseIDsForConcept(concept.getID());
			if (databaseIDs != null)
				for (DatabaseID databaseID: databaseIDs)
					mergedOntology.setDatabaseIDForConcept(concept.getID(), databaseID);
		}
		for (Relation relation: chemlist.getRelations()) {
			if (relation.object < -999)
				relation.object -= 2000;
			if (relation.subject < -999)
				relation.subject -= 2000;
			mergedOntology.setRelation(relation);
		}
		System.out.println(StringUtilities.now() + "\tdumping "+mergedOntology.getName()+" in database");
		ontologyManager.dumpStoreInDatabase((OntologyStore) mergedOntology);
	}

	private Ontology mergedOntology;
	private Set<Integer> chemCUIs = new HashSet<Integer>();
	private static OntologyManager ontologyManager = new OntologyManager();

	private static Set<Integer> getChemicalSemanticTypesToRemove() {
		Set<Integer> result = new TreeSet<Integer>();
		result.add(-103); //Chemical
		result.add(-104); //Chemical viewed structurally
		result.add(-109); //Organic chemical
		result.add(-114); //Organophosphorous compound
		result.add(-115); //Nucleic acid, nucleoside or nucleotide
		result.add(-118); //Carbohydrate
		result.add(-119); //Lipid
		result.add(-110); //Steroid
		result.add(-111); //Eicosanoid
		result.add(-196); //Element, ion or isotope
		result.add(-197); //Inorganic chemical
		result.add(-120); //Chemical viewed functionally
		result.add(-121); //Pharmacologic substance
		result.add(-195); //Antibiotic
		result.add(-122); //Biomedical or dental material
		result.add(-123); //Biologically active substance
		result.add(-124); //Neuroreactive substance or biogenic amine
		result.add(-125); //Hormone
		result.add(-127); //Vitamin
		result.add(-129); //Immunologic factor
		result.add(-130); //Indicator, reagent, or diagnostic aid
		result.add(-131); //Hazardous or poisonous substance
		result.add(-200); //Clinical drug
		return result;
	}

	private static Set<Integer> getChemicalSemanticTypesToCheckForMerge() {
		Set<Integer> result = new TreeSet<Integer>();
		result.add(-116); //Amino acid, peptide or protein
		result.add(-126); //Enzyme
		result.add(-192); //Receptor
		return result;
	}

	private class CUImap {
		int from;
		int to;

		public CUImap(int from, int to) {
			this.from = from;
			this.to = to;
		}
	}
}
