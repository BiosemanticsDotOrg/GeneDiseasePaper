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

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.StringUtilities;

public class OCGenelistHumanv206 {
  public static OntologyStore contructOntology(String psfFile){   
    System.out.println("Loading thesaurus");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadFromPSF(psfFile);
    
    System.out.println("Preparing thesaurus");
    setMatchingFlags(loader.ontology);
    loader.ontology.setName("Genelist");
    
    return loader.ontology;
  }
  public static OntologyStore contructOntology(){
    return contructOntology("/home/public/thesauri/GenelistHuman/GeneList_v2.0.6. Disambiguation filter.psf");
  }
  
  public static void setMatchingFlags(Ontology ontology){ //Sets the matching flags
    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
    while (conceptIterator.hasNext()){
      Concept concept = conceptIterator.next();
      for (TermStore term : concept.getTerms())
        SetMatchingFlags(term);
    }
  }
  
  protected static void SetMatchingFlags(TermStore term){
    term.orderSensitive = true;  
    
    if (isSymbol(term.text)) { //gene symbol 
      term.normalised = false;
      if (StringUtilities.containsNumber(term.text)) { //symbol with number
        term.caseSensitive = false;
      } else { //symbol without number
        term.caseSensitive = true;
      } 
    }else { //long form
      term.caseSensitive = false;
      term.normalised = true;
      term.orderSensitive = false; //*** remove this!!
    }
  }
  
 
  protected static boolean isSymbol(String string){
    return !((string.contains(" ") || !StringUtilities.isAbbr(string)) && string.length() > maxSymbolLength);
  }
  
  public static int maxSymbolLength = 6;
}
