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

package org.erasmusmc.dataimport.genes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.ReadTextFile;

/**
 * Imports genes from an SGD tab delimited file (registry.genenames.tab) into an ontology structure
 * @author Schuemie
 *
 */
public class SGDGeneImport {
  public static void main(String[] args){
    new SGDGeneImport();
  }
  
  private int cui = 2300000;
  
  public SGDGeneImport(){
    OntologyStore ontology = new OntologyStore();
    
    ReadTextFile sgdFile = new ReadTextFile("/data/Saccharomyces Genome Database/registry.genenames.tab");
    Iterator<String> iterator = sgdFile.getIterator();
    while (iterator.hasNext()){
      addToOntology(iterator.next(), ontology);
    }
    
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = ontology;
    loader.saveToPSF("/home/public/Thesauri/GenesNonHuman/SGD_june2007.psf");
  }

  private void addToOntology(String line, OntologyStore ontology) {
    Concept concept = new Concept(cui);

    String[] cols = line.split("\t");
    List<TermStore> terms = new ArrayList<TermStore>();
    terms.add(newTerm(cols[0]));
    
    if (!cols[1].equals("")){ 
      String[] synonyms = cols[1].split("\\|");
      for (String synonym : synonyms)
        terms.add(newTerm(synonym));
    }
    String OLN = cols[5];
    if (!OLN.equals("")){
      terms.add(newTerm(OLN));
      ontology.setDatabaseIDForConcept(cui, new DatabaseID("OLN", OLN));
    }  
    ontology.setDatabaseIDForConcept(cui, new DatabaseID("SGD", cols[6]));
    concept.setTerms(terms);
    ontology.setConcept(concept);
    cui++;    
  }

  private TermStore newTerm(String term) {
    TermStore result = new TermStore(term);
    result.caseSensitive = true;
    result.normalised = false;
    result.orderSensitive = true;
    return result;
  }

}
