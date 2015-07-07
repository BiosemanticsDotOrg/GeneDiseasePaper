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
import java.util.Set;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.StringUtilities;

/** 
 * Script for removing gene names from GO. Procedure: all GO terms with a name ending in 'activity' are 
 * checked. If one of their terms is also a gene name (from the GENE voc), this term is removed. 
 * For example: "IL-2" is a synonym of the GO concept "interleukin-2 receptor binding activity", and is removed.  
 * @author schuemie
 *
 */
public class FilterGOGeneTerms {

	public static String sourceOntologyFilename = "/home/public/thesauri/UMLS2006Homologene_v1_6c.ontology";
	public static String targetOntologyFilename = "/home/public/thesauri/UMLS2006Homologene_v1_7.ontology";
	public static String targetOntologyName = "UMLS2006Homologene_v1_7";
	
	public static void main(String[] args) {
		OntologyStore ontology = loadOntology();
		filter(ontology);
		saveOntology(ontology);
	}
	private static void saveOntology(OntologyStore ontology) {
		StringUtilities.outputWithTime("Saving ontology");
		ontology.setName(targetOntologyName);
		OntologyFileLoader loader = new OntologyFileLoader();
		loader.save(ontology, targetOntologyFilename);
	}
	private static void filter(OntologyStore ontology) {
		StringUtilities.outputWithTime("Filtering ontology");
		Set<Integer> goConceptIDs = getGoConceptIDs(ontology);
		Set<String> geneNames = getGeneNames(ontology);
		for (int goConceptID : goConceptIDs)
			filterGOConcept(ontology.getConcept(goConceptID), geneNames);
	}
	private static Set<String> getGeneNames(OntologyStore ontology) {
		int geneVocID = -1;
		for (Concept concept : ontology)
			if (concept.getName().equals("GENE") && concept.getID() < 0){
				geneVocID = concept.getID();
				break;
			}
				
		Set<String> geneNames = new HashSet<String>();
		for (Relation relation : ontology.getRelations())
			if (relation.predicate == DefaultTypes.fromVocabulary)
				if (relation.object == geneVocID)
					for (TermStore term : ontology.getConcept(relation.subject).getTerms())
						geneNames.add(term.text);
		return geneNames;
	}
	private static void filterGOConcept(Concept concept, Set<String> geneNames) {
		if (concept.getName().endsWith("activity")){
			Iterator<TermStore> iterator = concept.getTerms().iterator();
			while (iterator.hasNext()){
				TermStore term = iterator.next();
				if (!term.text.endsWith("activity") && geneNames.contains(term.text)){
					System.out.println("Removing term \""+term.text+"\" from concept \""+concept.getName()+"\"");
					iterator.remove();
				}
			}
		}
	}
	private static Set<Integer> getGoConceptIDs(OntologyStore ontology) {
		int goVocID = -1;
		for (Concept concept : ontology)
			if (concept.getName().equals("GO") && concept.getID() < 0){
				goVocID = concept.getID();
				break;
			}
				
		Set<Integer> goConceptIDs = new HashSet<Integer>();
		for (Relation relation : ontology.getRelations())
			if (relation.predicate == DefaultTypes.fromVocabulary)
				if (relation.object == goVocID)
					goConceptIDs.add(relation.subject);
		
		return goConceptIDs;
	}
	private static OntologyStore loadOntology() {
		StringUtilities.outputWithTime("Loading ontology");
		OntologyFileLoader loader = new OntologyFileLoader();
		return loader.load(sourceOntologyFilename);
	}

}
