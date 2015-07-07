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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.dataimport.UMLS.UMLSFilteringAfterOntologyCreation;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.HomonymAnalyzer;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;

public class MergeSGDAndSwissProt {

  public static void main (String[] args){
    new MergeSGDAndSwissProt(); 
  }  
  
  public MergeSGDAndSwissProt(){
    System.out.println(StringUtilities.now() + "\tLoading ontologies");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadFromPSF("/home/public/Thesauri/GenesNonHuman/SGD_june2007.psf");
    OntologyStore SGD = loader.ontology;
    loader.saveToPSF("/temp/test.psf");
    loader.loadFromPSF("/home/public/Thesauri/GenesNonHuman/SP_yeast.psf");
    OntologyStore SP = loader.ontology;
    
    cleanup(SP);
    
    SGD.createIndexForDatabaseIDs();
    
    System.out.println(StringUtilities.now() + "\tMerging");
    Iterator<Concept> iterator = SP.getConceptIterator();
    while (iterator.hasNext()){
      Concept concept = iterator.next();
      List<DatabaseID> databaseIDs = SP.getDatabaseIDsForConcept(concept.getID());
      Set<Integer> cuis = new HashSet<Integer>();
      for (DatabaseID databaseID : databaseIDs){
        cuis.addAll(SGD.getConceptIDs(databaseID));
      }
      if (cuis.size() == 0)
        System.out.println("No SGD ids found for SP concept " + concept.getName() + "\t" + concept.getID());
      else if (cuis.size() > 1)
        System.out.println("Multiple SGD ids found for SP concept " + concept.getName());
      else {
        for (int cui : cuis){
          List<TermStore> terms = SGD.getConcept(cui).getTerms();
          terms.addAll(concept.getTerms());
          for (DatabaseID databaseID : databaseIDs)
            SGD.setDatabaseIDForConcept(cui, databaseID);
          OntologyUtilities.removeDuplicateTerms(terms);
        }
      }
    }
    loader = new OntologyPSFLoader();
    loader.ontology = SGD;
    loader.saveToPSF("/home/public/Thesauri/GenesNonHuman/GenelistYeast_june2007_2.psf");
  }

  private void cleanup(OntologyStore sp) {
    System.out.println(StringUtilities.now() + "\tCleaning up Swiss-Prot");
    HomonymAnalyzer analyzer = new HomonymAnalyzer();
    analyzer.setOntology(sp);
    Map<Integer,Map<Integer,List<String>>> homonyms = analyzer.compareConcepts();    
    Set<Integer> homonymIDs = new HashSet<Integer>();
    for (Map.Entry<Integer,Map<Integer,List<String>>> entry : homonyms.entrySet()){
      homonymIDs.addAll(entry.getValue().keySet());
      homonymIDs.add(entry.getKey());
    }
    
    Iterator<Concept> iterator = sp.getConceptIterator();
    while (iterator.hasNext()){
      Concept concept = iterator.next();
      if (homonymIDs.contains(concept.getID()))
        iterator.remove();
      else{
        List<TermStore> terms = concept.getTerms();
        Iterator<TermStore> termIterator = terms.iterator();
        while (termIterator.hasNext()){
          TermStore term = termIterator.next();
          if (UMLSFilteringAfterOntologyCreation.ECPattern.matcher(term.text).find())
            termIterator.remove();
        }
      }
    }  

    
    
  }
}
