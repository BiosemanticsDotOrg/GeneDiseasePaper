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

import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.ontologyutilities.GeneTermVariantGenerator;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;

public class OCGenelistEColi {
  public static void main(String[] args){
    OntologyStore ontology = constructOntology();

    OntologyStore newOntology = new OntologyStore();
    Iterator<Concept> iterator = ontology.getConceptIterator();
    Concept voc = new Concept(-1000);
    voc.setName("GENE");
    Concept semType = new Concept(-116);
    semType.setName("Amino Acid, Peptide, or Protein");
    newOntology.setConcept(voc);
    newOntology.setConcept(semType);
    int id = 2500000;
    while (iterator.hasNext()){
      Concept concept = iterator.next();
      if (concept.getID() >= 0 ){
        Concept newConcept = new Concept(id++);
        newConcept.setDefinition(concept.getDefinition());
        newConcept.setTerms(concept.getTerms());
        newOntology.setConcept(newConcept);
        List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
        for (DatabaseID databaseID : databaseIDs)
          newOntology.setDatabaseIDForConcept(newConcept.getID(), databaseID);
        Relation isofSemType = new Relation(newConcept.getID(), DefaultTypes.isOfSemanticType, semType.getID());
        newOntology.setRelation(isofSemType);
        Relation isofVoc = new Relation(newConcept.getID(), DefaultTypes.fromVocabulary, voc.getID());
        newOntology.setRelation(isofVoc);
      }
        
    }
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = newOntology;
    loader.saveToPSF("/home/schuemie/leiden/ecoli/ecoliCurated.psf");
  }
  
  public static OntologyStore constructOntology(){
    //String psfFile = "/home/public/thesauri/GenesNonHuman/SGD_june2007.psf";
    String psfFile = "/home/schuemie/Leiden/ecoli/ecoli.psf";

    System.out.println("Loading thesaurus");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadFromPSF(psfFile);  
    

    System.out.println("Preparing thesaurus");
    GeneTermVariantGenerator.generateVariants(loader.ontology);
    OntologyCurator curator = new OntologyCurator("/home/schuemie/leiden/ecoli/EColiCurationFile.txt");
    curator.curateAndPrepare(loader.ontology);
    loader.ontology.setName("EColi");

    return loader.ontology;
  }
}