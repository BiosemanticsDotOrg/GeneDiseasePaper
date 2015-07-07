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

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.ontologyutilities.GeneTermVariantGenerator;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;

public class OCUMLSHomologeneMouseRatHuman {
  public static Ontology contructOntology(){
    
    String psfFile = "/home/public/thesauri/homologene/testThes.psf"; //testThes.psf";//120806UMLS_homologene_filtered3.psf";
    String curationfile = "/home/public/Thesauri/homologene/GeneThesaurusCurationFile.txt";
    
    System.out.println("Loading thesaurus");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = false;
    loader.loadFromPSF(psfFile);  
    
    GeneTermVariantGenerator.generateVariants(loader.ontology);
    OntologyCurator curator = new OntologyCurator(curationfile);
    OntologyUtilities.minWordSize = 3;
    curator.curateAndPrepare(loader.ontology);
    loader.ontology.setName("UMLSHomologeneMouseRatHuman");
    
    return loader.ontology;
  }

}
