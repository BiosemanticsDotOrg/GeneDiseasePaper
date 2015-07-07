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

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.FamilyNameFinder;
import org.erasmusmc.ontology.ontologyutilities.GeneTermVariantGenerator;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;

public class OCGenelistHumanV240 {
  public static void main(String[] args){
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = contructOntology();
    OntologyManager manager = new OntologyManager();
    manager.dumpStoreInDatabase(loader.ontology);
  }
  public static OntologyStore contructOntology(){
    
    String psfFile = "/home/public/thesauri/GenelistHuman/GenelistHuman_v2.4.0.psf";
    //String psfFile = "/home/public/thesauri/GenesNonHuman/MGD_dec2006.psf";
    //String psfFile = "/home/public/thesauri/GenesNonHuman/RGD_dec2006.psf";
    String curationFile = "/home/public/thesauri/homologene/GeneThesaurusCurationFile.txt";
    
    System.out.println("Loading thesaurus");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadFromPSF(psfFile);  
    OntologyStore ontology = loader.ontology;
    
    System.out.println("Preparing thesaurus");   
    GeneTermVariantGenerator.generateVariants(ontology);
    OntologyUtilities.filterOntology(ontology, OntologyUtilities.getDefaultStopWordsForFiltering());
    OntologyUtilities.removeTerms(ontology, FamilyNameFinder.findFamilyNamesListOutput(ontology));
    OntologyCurator curator = new OntologyCurator(curationFile);
    curator.curateAndPrepare(ontology);
    //for (Concept concept : ontology)
    //  for (TermStore term : concept.getTerms()){
    //    term.caseSensitive = false;
    //    term.text = term.text.toLowerCase();
    //  }
    ontology.setName("GenelistHuman");
    
    return ontology;
  }
}