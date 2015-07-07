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

package org.erasmusmc.dataimport;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.ReadTextFile;

public class KEGGimport {
  public static void main (String[] args){
    OntologyStore ontology = new OntologyStore();
    
    ReadTextFile file = new ReadTextFile("/data/KEGG/compound");
    Iterator<String> iterator = file.getIterator();
    boolean name = false;
    List<TermStore> terms = null;
    String keggID = "";
    int cui = 5000000;
    while(iterator.hasNext()){
      String line = iterator.next();
      if (line.startsWith("NAME")){
        name = true;
        line = line.substring("NAME".length());
        terms = new ArrayList<TermStore>();
      } else if (line.startsWith("ENTRY")){
        line = line.substring("ENTRY".length());
        line = line.trim().split(" ")[0];
        //line = line.replace("Compound", "");
        //line = line.replace("Peptide", "");
        keggID = line.trim();
      }
      if (name){
        if (line.startsWith(" ")){
          String term = line.trim();
          if (term.endsWith(";"))
            term = term.substring(0, term.length()-1);
          if (!term.contains("Transferred to"))
            terms.add(new TermStore(term));
        } else {
          name = false;
          if (terms.size() != 0){
            Concept concept = new Concept(cui++);
            concept.setTerms(terms);
            ontology.setDatabaseIDForConcept(concept.getID(), new DatabaseID("KEGG", keggID));
            ontology.setConcept(concept);
          }  
        }
      }
    }
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = ontology;
    loader.saveToPSF("/home/schuemie/TREC2007/thesauri/BiologicalSubstances.psf");
  }
}
