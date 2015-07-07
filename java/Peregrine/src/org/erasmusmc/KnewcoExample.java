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

package org.erasmusmc;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.disambiguator.GeneDisambiguator;
import org.erasmusmc.peregrine.disambiguator.UMLSDisambiguator;

public class KnewcoExample {

	public static String lvgPropertiesPath = "/home/public/LVG/lvg2006lite/data/config/lvg.properties";
	public static String normCachePath = "/home/public/Peregrine/standardNormcache2006.bin";
	public static String ontologyPath = "/home/public/thesauri/UMLS2008AB_medlinefilter.psf";
	public static int minConceptID = 2000000; //Concept ID where the genes start
	
	public static void main(String[] args) {
		// Load the ontology:
		OntologyPSFLoader loader = new OntologyPSFLoader();
		//loader.loadDefinitions = false; // Use this to save memory
		loader.loadHierarchy = false;
		loader.loadFromPSF(ontologyPath);
		OntologyStore ontology = loader.ontology;
        
		//Set the matching flags:
		OntologyCurator curator = new OntologyCurator();
		curator.curateAndPrepare(ontology); 
		
		//Initialize Peregrine:
		ConceptPeregrine peregrine = new ConceptPeregrine();
		peregrine.normaliser.loadCacheBinary(normCachePath);
		peregrine.setOntology(ontology);
		//peregrine.destroyOntologyDuringRelease = true; //Use this if you are not going to use the ontology afterwards. Saves memory
		peregrine.release();
		
		//Initalize disambiguators:
		GeneDisambiguator geneDisambiguator = new GeneDisambiguator(peregrine, minConceptID, Integer.MAX_VALUE);
		UMLSDisambiguator umlsDisambiguator = new UMLSDisambiguator(0, minConceptID);
		
		//Do this for every indexation:
		peregrine.index("malaria");
		geneDisambiguator.disambiguate(peregrine);
		umlsDisambiguator.disambiguate(peregrine);
		
		//Presto: results are now found in peregrine.resultConcepts (similar to RMIPeregrine)
		//you can use the ontology object to retrieve information (Similar to RMIOntology)
		//Note: only works if you did not set destroyOntologyDuringRelease to true
		int conceptID = peregrine.resultConcepts.get(0).conceptId;
		Concept concept = ontology.getConcept(conceptID);
		System.out.println(concept.getName());
	}
}
