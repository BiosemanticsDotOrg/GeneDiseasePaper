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
import org.erasmusmc.ontology.ontologyutilities.GeneTermVariantGenerator;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;

public class OCGenelistHumanV230 {
  public static OntologyStore contructOntology(){
    String psfFile = "/home/public/thesauri/GenelistHuman/GenelistHuman_v2.3.0.psf";
    String curationFile = "/home/public/Thesauri/homologene/GeneThesaurusCurationFile.txt";
    
    System.out.println("Loading thesaurus");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadFromPSF(psfFile);  
    
    System.out.println("Preparing thesaurus");   
    GeneTermVariantGenerator.generateVariants(loader.ontology);
    OntologyCurator curator = new OntologyCurator(curationFile);
    curator.curateAndPrepare(loader.ontology);
    loader.ontology.setName("GenelistHuman");
    
    return loader.ontology;
  }
}
