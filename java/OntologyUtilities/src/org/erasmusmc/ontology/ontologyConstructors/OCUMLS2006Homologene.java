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
import java.util.List;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.utilities.StringUtilities;

public class OCUMLS2006Homologene {
	
	public static void main(String[] args){
		Ontology ontology = constructOntology();
	  ConceptPeregrine peregrine = new ConceptPeregrine();
    System.out.println("Loading normaliser cache. "+StringUtilities.now());
    String normaliserCacheFile = "/home/public/Peregrine/standardNormCache2006.bin";
    peregrine.normaliser.loadCacheBinary(normaliserCacheFile);
    peregrine.setOntology(ontology);
		System.gc();
		System.out.println("Releasing. "+StringUtilities.now());
		long start = System.currentTimeMillis();
    long memStart = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();  
    peregrine.release();
    long timeTaken = System.currentTimeMillis() - start;
	  System.gc();
	  long memGrowthSize = memStart - (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
	  System.out.println(timeTaken + " ms");
	  System.out.println(memGrowthSize + " b");
	  peregrine.index("test");
	  System.out.println(peregrine.resultConcepts.size());
	  
	}

  public static OntologyStore constructOntology(){
    String psfFile = "/home/public/thesauri/UMLS2006Homologene_v1_6c.psf";
    
    System.out.println("Loading thesaurus");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadFromPSF(psfFile);  
    
    System.out.println("Preparing thesaurus");   
    /*
    for (Concept concept : loader.ontology)
      if (concept.getID() > 2000000){
        List<TermStore> newTerms = new ArrayList<TermStore>();
    	  for (TermStore term : concept.getTerms()){
      	  String[] tokens = term.text.split(" ");
      	  if (tokens.length > 1){
      	  	String lastToken = tokens[tokens.length-1]; 
      	  	if (StringUtilities.isNumber(lastToken) || StringUtilities.isRomanNumeral(lastToken)){
      	  		TermStore newTerm = new TermStore(term.text.substring(0,term.text.length() - lastToken.length()-1) + "-" + lastToken);
      	  		newTerms.add(newTerm);
      	  	}	
      	  }
      	  tokens = term.text.split("-");
      	  if (tokens.length > 1){
      	  	String lastToken = tokens[tokens.length-1]; 
      	  	if (StringUtilities.isNumber(lastToken) || StringUtilities.isRomanNumeral(lastToken)){
      	  		TermStore newTerm = new TermStore(term.text.substring(0,term.text.length() - lastToken.length()-1) + " " + lastToken);
      	  		newTerms.add(newTerm);
      	  	}	
      	  }
        }
    	  concept.getTerms().addAll(newTerms);
      }
    */
    OntologyCurator curator = new OntologyCurator();
    curator.curateAndPrepare(loader.ontology);
    //for (Concept concept : loader.ontology)
    //  for (TermStore term : concept.getTerms()){
    //    term.caseSensitive = false;
    //    term.orderSensitive = true;
    //    term.normalised = false;
    //  }
    loader.ontology.setName("UMLS2006Homologene");
    
    return loader.ontology;
  }
}
