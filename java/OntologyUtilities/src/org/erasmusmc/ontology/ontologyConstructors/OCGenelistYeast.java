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

import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.FamilyNameFinder;
import org.erasmusmc.ontology.ontologyutilities.GeneTermVariantGenerator;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;

public class OCGenelistYeast {
  public static void main(String[] args){
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = constructOntology();
    loader.saveToPSF("/home/temp/yeast.psf");
    
  }
  
  public static OntologyStore constructOntology(){
    //String psfFile = "/home/public/thesauri/GenesNonHuman/SGD_june2007.psf";
    String psfFile = "/home/public/thesauri/GenesNonHuman/GenelistYeast_june2007.psf";

    System.out.println("Loading thesaurus");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadFromPSF(psfFile);  
    

    System.out.println("Preparing thesaurus");
    GeneTermVariantGenerator.generateVariants(loader.ontology);
    OntologyUtilities.filterOntology(loader.ontology, OntologyUtilities.getDefaultStopWordsForFiltering());
    OntologyUtilities.removeTerms(loader.ontology, FamilyNameFinder.findFamilyNamesListOutput(loader.ontology));
    OntologyCurator curator = new OntologyCurator("/home/public/thesauri/GenesNonHuman/YeastGeneThesaurusCurationFile.txt");
    curator.curateAndPrepare(loader.ontology);
    loader.ontology.setName("SGD");

    return loader.ontology;
  }
}