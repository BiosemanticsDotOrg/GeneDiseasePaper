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

package org.erasmusmc.ontology.ontologyConstructors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.Concept.DisambiguationType;
import org.erasmusmc.ontology.ontologyutilities.FamilyNameFinder;
import org.erasmusmc.ontology.ontologyutilities.GeneTermVariantGenerator;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;

public class OCHomologeneV2 {
	public static void main(String[] args){
		OntologyStore ontology = contructOntology();
		
	}
	
  public static OntologyStore contructOntology(){
    
    String psfFile = "/home/public/thesauri/homologeneV2/HomologeneV2_0.psf";
  	//String psfFile = "/home/public/thesauri/UMLS2008ABHomologeneChemToxV1_2.psf";
    String curationFile = "/home/public/thesauri/homologeneV2/GeneThesaurusCurationFilev2_1.txt";
    
    System.out.println("Loading thesaurus");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadFromPSF(psfFile);  
    OntologyStore ontology = loader.ontology;
    
    System.out.println("Preparing thesaurus");   
    OntologyCurator curator = new OntologyCurator(curationFile);   
    curator.curateAndPrepare(ontology);
    
    int termCount = 0;
    int idCount = 0;  	
    Iterator<Concept> iterator = ontology.iterator();
    while (iterator.hasNext()){
    	Concept concept = iterator.next();
    	if (concept.getTerms().size()==0 || concept.getTerms().get(0).text.length() == 0)
    		iterator.remove();
    	else {
      	termCount += concept.getTerms().size();
      	idCount += ontology.getDatabaseIDsForConcept(concept.getID()).size();
      	concept.setDisambiguationType(DisambiguationType.strict);    		
    	}
    }
    System.out.println("Concepts: " + ontology.size() + "\tTerms: " + termCount + "\tIDs: " + idCount);
    
    ontology.setName("HomologeneV2_0");   
    //ontology.setName("UMLS2008ABHomologeneChemToxV1_3");
    OntologyFileLoader newLoader = new OntologyFileLoader();
    newLoader.save(ontology, "/home/public/thesauri/homologeneV2/HomologeneV2_0.ontology");
    return ontology;
  }
}
