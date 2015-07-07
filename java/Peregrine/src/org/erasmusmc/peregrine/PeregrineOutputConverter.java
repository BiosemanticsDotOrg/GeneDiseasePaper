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

package org.erasmusmc.peregrine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.userinterface.components.Occurrence;

/** Converts the output of a Peregrine indexation into various different formats.*/
public class PeregrineOutputConverter {

  //This method translates the index results into an array of Occurences:
  public static Occurrence[] resultOccurrences(AbstractPeregrine peregrine){
    int count = 0;
    Ontology ontology = peregrine.ontology;
    for (ResultConcept concept : peregrine.resultConcepts){
      for (ResultTerm term : concept.terms){
        count += term.words.length;
      }
    }
    Occurrence[] result = new Occurrence[count];
    count = 0;
    Occurrence occurrence; 
    for (ResultConcept concept : peregrine.resultConcepts){
      for (ResultTerm term : concept.terms){
        for (int word : term.words){
          occurrence = new Occurrence();
          occurrence.concept = ontology.getConcept(concept.conceptId);
          
          //Remove this!!!!
          //if (occurrence.concept == null) occurrence.concept = new Concept(1);
          
          occurrence.cui = occurrence.concept.getID();
          occurrence.start = peregrine.tokenizer.startpositions.get(word);
          occurrence.end = peregrine.tokenizer.endpositions.get(word);        
          result[count] = occurrence;
          count++;
        }
      }
    }    
    return result;
  }  
  
  //This method translates the index results into a simple text representation:
  public static List<String> simpleText(ConceptPeregrine peregrine){
    List<String> result = new ArrayList<String>(peregrine.resultConcepts.size());
    Ontology ontology = peregrine.ontology;
    for (ResultConcept concept : peregrine.resultConcepts){
      result.add(concept.terms.size()+"\t"+concept.conceptId+"\t"+ontology.getConcept(concept.terms.get(0).term.conceptId[0]).getName());
    } 
    return result;
  }    
  
  //Converts the index results into an XML format
  public static List<String> XML(ConceptPeregrine peregrine){
    List<String> result = new ArrayList<String>();
    Map<ResultTerm, Integer> term2clid = new HashMap<ResultTerm, Integer>();
    for (Integer i = 0; i < peregrine.resultTerms.size(); i++) term2clid.put(peregrine.resultTerms.get(i), i);
    
    Ontology ontology = peregrine.ontology;
    Tokenizer tokenizer = peregrine.tokenizer;
    result.add("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
    if (ontology == null || ontology.getName() == "")
      result.add("<fingerprint>");
    else
      result.add("<fingerprint thesaurus=\""+ontology.getName()+"\">");
    
    result.add("  <concepts count=\""+peregrine.resultConcepts.size()+
        "\" clusters=\""+ peregrine.resultTerms.size()+"\"/>");
    for (ResultConcept concept : peregrine.resultConcepts){
      result.add("  <concept id=\""+concept.conceptId+"\" rank=\""+concept.terms.size()+"\">");
      if (ontology != null){
        Concept conceptstore = ontology.getConcept(concept.conceptId);
        String name = "";
        if (conceptstore.getName().equals("")){ 
          if (conceptstore.getTerms().size() != 0) name = conceptstore.getTerms().get(0).text;
        }else name = conceptstore.getName();
        if (!name.equals(""))
          result.add("    <name>"+name+"</name>");
      }
      for (ResultTerm term : concept.terms){
        int clid = term2clid.get(term);
        for (int word : term.words){
          result.add("    <word clid=\""+Integer.toString(clid+1)+
              "\" pos=\""+ Integer.toString(tokenizer.startpositions.get(word)+1)+
              "\" len=\""+ tokenizer.tokens.get(word).length()+
              "\">"+tokenizer.tokens.get(word)+"</word>");
        }
      }
      result.add("  </concept>");
    } 
    result.add("</fingerprint>");
    return result;
  }  
  
  //Converts the index results into a simple XML format
  public static String simpleXML(ConceptPeregrine peregrine){
    StringBuffer result = new StringBuffer();
    //result.add("<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
    result.append("<fingerprint thesaurus=\""+peregrine.ontologyName+"\">");
    for (ResultConcept concept : peregrine.resultConcepts){
      result.append("<concept id=\""+concept.conceptId+"\" rank=\""+concept.terms.size()+"\"/>");
    } 
    result.append("</fingerprint>");
    return result.toString();
  }
  //Converts the index results into a RobFingerprint for STORING in the Groundhog
  public static ConceptVector convertResult2ConceptVector(AbstractPeregrine peregrine, Ontology ontology){
    ConceptVector conceptVector = new ConceptVector(ontology);
    
    for (ResultConcept concept : peregrine.resultConcepts){
      conceptVector.add(concept.conceptId,concept.terms.size());
    }
    return conceptVector;
  }
}
