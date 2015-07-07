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

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

public class WordNet {
  public static void main(String[] args){
    System.out.println("Processing data files");
    List<String> dataFiles = new ArrayList<String>();
    dataFiles.add("/home/Data/Wordnet3.0/dict/data.adj");
    dataFiles.add("/home/Data/Wordnet3.0/dict/data.adv");
    dataFiles.add("/home/Data/Wordnet3.0/dict/data.noun");
    dataFiles.add("/home/Data/Wordnet3.0/dict/data.verb");
    
    OntologyStore ontology = new OntologyStore();
    ontology.setName("Wordnet3_0");
    int offset = 0;
    for (String datafile : dataFiles){
      addToOntology(datafile, ontology, offset);
      offset += 100000000;
    }
    System.out.println("Setting flags");
    setFlags(ontology);
    
    System.out.println("Storing ontology");
    OntologyManager ontologyManager = new OntologyManager();
    //ontologyManager.deleteOntology(ontology.getName());
    ontologyManager.dumpStoreInDatabase(ontology);
    //OntologyPSFLoader loader = new OntologyPSFLoader();
    //loader.ontology = ontology;
    //loader.SaveToPSF("/home/temp/wordnet3.0.psf");
    
  }

  private static void setFlags(OntologyStore ontology) {
    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
    while (conceptIterator.hasNext()){
      for (TermStore term : conceptIterator.next().getTerms()){
        if (term.text.toLowerCase() == term.text){
          term.caseSensitive = false;
          term.normalised = true;
          term.orderSensitive = true;
        } else {
          term.caseSensitive = true;
          term.normalised = false;
          term.orderSensitive = true;          
        }
      }
    }
  }

  private static void addToOntology(String datafile, OntologyStore ontology, int offset) {
    List<String> lines = TextFileUtilities.loadFromFile(datafile);
    for (int i = 0; i < lines.size(); i++){
      if (Character.isDigit(lines.get(i).charAt(0)))
        processLine(lines.get(i), ontology, offset);
    }
    
  }

  private static void processLine(String line, OntologyStore ontology, int offset) {
    //Fetch concept ID:
    String conceptID = line.substring(0, 8);
    
    Concept concept = new Concept(Integer.parseInt(conceptID) + offset);
    
    //Fetch terms:
    List<TermStore> terms = new ArrayList<TermStore>();
    for (String word : line.substring(17).split(" ")){
      if (word.length() > 1 && StringUtilities.containsLetter(word) && noIllegalChars(word)){
        if (word.contains("("))
          word = word.substring(0, word.indexOf("("));
        word = word.replace('_', ' ');
        terms.add(new TermStore(word));
      } else if (word.equals("|") || word.equals("@")) break;
    }
    if (terms.size() != 0 ) concept.setTerms(terms);
    
    //Fetch definition:
    if (line.contains("|"))
      concept.setDefinition(line.substring(line.indexOf("|")+1).trim());
    
    //Fetch parents:
    for (int i = 17; i < line.length(); i++)
      if (line.charAt(i) == '@'){
        String parentID;
        if (line.charAt(i+1) != ' ')
          parentID = line.substring(i+3, i+11);
        else
          parentID = line.substring(i+2, i+10); 
        Relation relation = new Relation(Integer.parseInt(parentID) + offset, DefaultTypes.isParentOf, concept.getID());
        ontology.setRelation(relation);
      }       
    
    //Add to ontology:
    ontology.setConcept(concept);
  }

  private static boolean noIllegalChars(String word) {
    for (Character ch : word.toCharArray()){
      if (!(Character.isLetterOrDigit(ch) || ch == '_' || ch =='-'|| ch =='('|| ch ==')'|| ch =='\'')) return false;
    }
    return true;
  }
}
