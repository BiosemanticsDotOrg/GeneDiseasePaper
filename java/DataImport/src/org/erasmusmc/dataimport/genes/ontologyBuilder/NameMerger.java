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

package org.erasmusmc.dataimport.genes.ontologyBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.OneToManyList;
import org.erasmusmc.collections.OneToManySet;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.HomonymAnalyzer;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.peregrine.AbstractPeregrine;
import org.erasmusmc.utilities.StringUtilities;

public class NameMerger {
	public static int minOverlapScore = 50;
	public static String normCacheFileName = "/home/public/Peregrine/standardNormCache2006.bin";
  public static int startConceptNumber = 4000000;
	private Ontology ontology;
	
  public void merge(Ontology ontology){
  	this.ontology = ontology;
  	Map<Integer,Map<Integer,List<String>>> homonyms = findHomonyms();
  	OneToManySet<Integer, Integer> mapping = findSufficientOverlap(homonyms);
  	Map<Integer, Integer> concept2clusterID = createClusters(mapping);
    mergeClusters(concept2clusterID);
  }
  
	private void mergeClusters(Map<Integer, Integer> concept2clusterID) {
		OneToManyList<Integer, Integer> cluster2conceptID = new OneToManyList<Integer, Integer>();
		for (Map.Entry<Integer, Integer> entry : concept2clusterID.entrySet())
			cluster2conceptID.put(entry.getValue(), entry.getKey());
		int newCID = startConceptNumber;
		
    for (Integer clusterID : cluster2conceptID.keySet()){
    	List<Integer> cluster = cluster2conceptID.get(clusterID);
    	Integer humanCID = findHuman(cluster);
    	Concept concept = new Concept(newCID);
    	ontology.setConcept(concept);
    	if (humanCID != null)
    		OntologyUtilities.mergeConcepts(ontology, humanCID, newCID);
    	for (Integer conceptID : cluster)
    		if (conceptID != humanCID)
    			OntologyUtilities.mergeConcepts(ontology, conceptID, newCID);
    	newCID++;
    }
	}

	private Integer findHuman(List<Integer> cluster) {
		for (Integer conceptID : cluster){
			List<Relation> relations = ontology.getRelationsForConceptAsSubject(conceptID, DefaultTypes.fromVocabulary);
			for (Relation relation : relations)
				if (relation.object == -1001)
					return conceptID;
		}
		return null;
	}

	private Map<Integer, Integer> createClusters(OneToManySet<Integer, Integer> mapping) {
		Map<Integer, Integer> concept2clusterID = new HashMap<Integer, Integer>();
    int nextCluster = 0;
  	for (Map.Entry<Integer, Set<Integer>> entry : mapping.entrySet()){
  		Integer cluster = concept2clusterID.get(entry.getKey());
  		Iterator<Integer> iterator = entry.getValue().iterator();
  		while (cluster == null && iterator.hasNext())
  			cluster = concept2clusterID.get(iterator.next());
  		if (cluster == null)
  			cluster = nextCluster++;
  		concept2clusterID.put(entry.getKey(), cluster);
  		for (Integer cid : entry.getValue())
  			concept2clusterID.put(cid, cluster);
  	}		
  	System.out.println(nextCluster + " clusters with an average of " + (concept2clusterID.size() / (double)nextCluster) + " genes");
  	return concept2clusterID;
	}

	private OneToManySet<Integer, Integer> findSufficientOverlap(Map<Integer, Map<Integer, List<String>>> homonyms) {
		OneToManySet<Integer, Integer> mapping = new OneToManySet<Integer, Integer>();
		int startCount = 0;
		int endCount = 0;
		for (Map.Entry<Integer, Map<Integer, List<String>>> entry : homonyms.entrySet()){
			Integer conceptID1 = entry.getKey();
			for (Map.Entry<Integer,List<String>> entry2 : entry.getValue().entrySet()){
				startCount++;
				Integer conceptID2 = entry2.getKey();
				int overlapScore = computeOverlapScore(conceptID1, conceptID2, entry2.getValue());
				if (overlapScore >= minOverlapScore){
					endCount++;
					mapping.put(conceptID1, conceptID2);
				}
			}
		}
		System.out.println("Of the " + startCount + " homonym concept pairs, " + endCount + " will be merged");
		return mapping;
	}
	
	private int computeOverlapScore(Integer conceptID1, Integer conceptID2,List<String> overlapTerms) {
	  int score = 0;
	  if (identicalPreferredTerm(conceptID1, conceptID2))
	  	score += 15;
	  for (String term : overlapTerms){
	  	if (OntologyUtilities.isGeneSymbol(term))
	  		if (StringUtilities.containsNumber(term))
	  			score += 20; //Symbol with number
	  		else
	  		score += 10; //Symbol without number
	  	else
	  		score += 25; //Long form
	  }
		return score;
	}
	
	
	private boolean identicalPreferredTerm(Integer conceptID1, Integer conceptID2) {
		String term1 = ontology.getConcept(conceptID1).getName();
		String term2 = ontology.getConcept(conceptID2).getName();
		return term1.toLowerCase().equals(term2.toLowerCase());
	}
	private Map<Integer, Map<Integer, List<String>>> findHomonyms() {
  	for (Concept concept : ontology)
  		for (TermStore term : concept.getTerms())
  			OntologyUtilities.setGeneChemMatchingFlags(term);
  	HomonymAnalyzer homonymAnalyzer = new HomonymAnalyzer();
  	homonymAnalyzer.normaliser.loadCacheBinary(normCacheFileName);
  	homonymAnalyzer.stopwords = AbstractPeregrine.getDefaultStopWordsForIndexing();
  	homonymAnalyzer.stopwords.add("human");
  	homonymAnalyzer.stopwords.add("protein");
  	homonymAnalyzer.stopwords.add("gene");
  	homonymAnalyzer.stopwords.add("antigen");
  	homonymAnalyzer.stopwords.add("product");
  	homonymAnalyzer.setOntology(ontology);
  	return homonymAnalyzer.compareConcepts();	
  }
}
