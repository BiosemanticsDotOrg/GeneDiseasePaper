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

package org.erasmusmc.dataimport.UMLS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.medline.MedlineIterator;
import org.erasmusmc.medline.MedlineListener;
import org.erasmusmc.medline.MedlineRecord;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ReleasedTerm;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class UMLSMedlineFilter implements MedlineListener{
	public String pmidFile = "/home/khettne/Public/PMIDs/all_2010.PMIDs";
	public String tempFolder = "/home/khettne/temp/";
	private ConceptPeregrine peregrine;
	private Map<Integer, Set<Integer>> cui2termids;

	public static String ontologySourceName = "/home/khettne/Projects/UMLS/2010AB/UMLS2010AB_180211.ontology";
	public static String ontologyTargetOutputFile = "/home/khettne/Projects/UMLS/2010AB/UMLS2010AB_180211_medlinefilter.ontology";


	/**
	 * Filters a thesaurus so only terms found at least once in Medline remain
	 * @param args
	 */
	public static void main(String[] args) {
		UMLSMedlineFilter filter = new UMLSMedlineFilter();
		filter.filter(ontologySourceName,ontologyTargetOutputFile);
	}

	private void filter(String source, String target) {
		initPeregrine(loadOntologyForIndexing(source));

		cui2termids = new HashMap<Integer, Set<Integer>>();

		System.out.println(StringUtilities.now() + "\tIndexing medline");
		MedlineIterator medlineIterator = new MedlineIterator();
		medlineIterator.pmidsFile = pmidFile;
		medlineIterator.iterate(this);

		saveResults();

		peregrine = null;
		System.gc();

		loadResults();
		filterOntology(loadOntologyForFiltering(source), target);
		System.out.println(StringUtilities.now() + "\tDone");
	}

	private void filterOntology(OntologyStore ontology, String target) {
		System.out.println(StringUtilities.now() + "\tFiltering ontology");
		OntologyStore removedOntology = new OntologyStore();
		Iterator<Concept> iterator = ontology.iterator();
		int origConcepts = 0;
		int origTerms = 0;
		int newConcepts = 0;
		int newTerms = 0;
		while (iterator.hasNext()){
			Concept concept = iterator.next();
			if (concept.getID() < 0)
				removedOntology.setConcept(concept);

			origConcepts++;
			origTerms += concept.getTerms().size();
			Set<Integer> termIDs = cui2termids.get(concept.getID());
			if (termIDs == null && concept.getID() > 0){
				copyConcept(ontology, removedOntology, concept);
				iterator.remove();
			} else {
				newConcepts++;
				if (newConcepts % 10000 == 0)
					System.out.println(newConcepts + " new concepts");
				List<TermStore> terms = concept.getTerms();
				List<TermStore> newList = new ArrayList<TermStore>();
				List<TermStore> deletedList = new ArrayList<TermStore>();
				for (int i = 0; i < terms.size(); i++)
					if (termIDs.contains(i))
						newList.add(terms.get(i));
					else
						deletedList.add(terms.get(i));
				concept.setTerms(newList);
				newTerms += newList.size();
				if (deletedList.size() != 0){
					Concept copyConcept = copyConcept(ontology, removedOntology, concept);
					copyConcept.setTerms(deletedList);
				}
			}
		}
		OntologyFileLoader loader = new OntologyFileLoader();
		System.out.println("Saving ontology");
		loader.save(ontology,target);
		loader.save(removedOntology,tempFolder + "/umlsMedlineFilter_removedTerms.ontology");

		System.out.println("Original: " + origConcepts + " concepts, " + origTerms + " terms");
		System.out.println("Filtered: " + newConcepts + " concepts, " + newTerms + " terms");
	}

	private Concept copyConcept(OntologyStore ontology, OntologyStore removedOntology, Concept concept) {
		Concept copy = new Concept(concept.getID());
		List<TermStore> terms = new ArrayList<TermStore>(concept.getTerms());
		copy.setTerms(terms);
		removedOntology.setConcept(copy);
		for (Relation relation : ontology.getRelationsForConceptAsSubject(concept.getID()))
			removedOntology.setRelation(relation);
		return copy;
	}

	private void saveResults() {
		System.out.println(StringUtilities.now() + "\tSaving results");
		WriteTextFile out = new WriteTextFile(tempFolder +"umlsMedlineFilter_termIDs.txt");
		for (Map.Entry<Integer, Set<Integer>> entry : cui2termids.entrySet()){
			StringBuilder line = new StringBuilder();
			line.append(entry.getKey());
			line.append("\t");
			line.append(StringUtilities.join(entry.getValue(), ";"));
			out.writeln(line.toString());
		}
		out.close();    
	}

	private void loadResults() {
		cui2termids = new HashMap<Integer, Set<Integer>>();
		ReadTextFile in = new ReadTextFile(tempFolder +"umlsMedlineFilter_termIDs.txt");
		for (String line : in){
			String[] cols = line.split("\t");
			int cui = Integer.parseInt(cols[0]);
			String[] tids = cols[1].split(";");
			Set<Integer> termIDs = new HashSet<Integer>();
			for (String tid : tids)
				termIDs.add(Integer.parseInt(tid));
			cui2termids.put(cui, termIDs);
		}
	}

	private Ontology loadOntologyForIndexing(String source){
		Ontology ontology;
		if (source.toLowerCase().endsWith(".ontology")){
			OntologyFileLoader loader = new OntologyFileLoader();
			loader.setLoadTermsOnly(true);
			ontology = loader.load(source);
		} else {
			OntologyManager manager = new OntologyManager();
			ontology = manager.fetchClient(source);
		}
		return ontology;
	}

	private OntologyStore loadOntologyForFiltering(String source){
		OntologyStore ontology;
		if (source.toLowerCase().endsWith(".ontology")){
			OntologyFileLoader loader = new OntologyFileLoader();
			ontology = loader.load(source);
		} else {
			OntologyManager manager = new OntologyManager();
			ontology = manager.fetchStoreFromDatabase(source);
		}
		return ontology;
	}

	private void initPeregrine(Ontology ontology) {
		System.out.println(StringUtilities.now() + "\tLoading ontology");
		peregrine = new ConceptPeregrine();
		peregrine.normaliser.loadCacheBinary("/home/public/Peregrine/standardNormCache2006.bin");
		System.out.println("Normalizer cache size = " + peregrine.normaliser.getCacheSize());
		peregrine.setOntology(ontology);
		peregrine.destroyOntologyDuringRelease = true;
		System.out.println(StringUtilities.now() + "\tReleasing ontology");
		peregrine.release();
	}

	@Override
	public void processMedlineRecords(List<MedlineRecord> records) {
		for (MedlineRecord record : records){
			peregrine.index(record.titleAbsMesh());
			for (ResultTerm resultTerm : peregrine.resultTerms){
				ReleasedTerm releasedTerm = resultTerm.term;
				for (int i = 0; i < releasedTerm.conceptId.length; i++){
					Set<Integer> termIDs = cui2termids.get(releasedTerm.conceptId[i]);
					if (termIDs == null){
						termIDs = new HashSet<Integer>(1);
						cui2termids.put(releasedTerm.conceptId[i], termIDs);
					}
					termIDs.add(releasedTerm.termId[i]);
				}
			}
		}
	}
}
