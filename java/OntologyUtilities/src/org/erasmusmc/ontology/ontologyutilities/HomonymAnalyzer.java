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

package org.erasmusmc.ontology.ontologyutilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.math.vector.SparseVectorInt2Float;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ReleasedTerm;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

public class HomonymAnalyzer extends ConceptPeregrine {
  public void countHomonyms(String filename) {
    System.out.println("Releasing thesaurus");
    release();
    System.out.println("Saving homonyms");
    List<String> lines = new ArrayList<String>();
    for (ReleasedTerm term: terms) {
      Set<Integer> uniqueCIDs = new TreeSet<Integer>();
      for (int cid : term.conceptId)
        uniqueCIDs.add(cid);
      if (uniqueCIDs.size() > 1) {
        StringBuffer line = new StringBuffer();
        line.append(uniqueCIDs.size());
        line.append("\t");
        line.append(ontology.getConcept(term.conceptId[0]).getTerms().get(term.termId[0]).text);
        line.append("\t");
        for (Integer conceptId: uniqueCIDs) {
          line.append(conceptId);
          line.append(";");
        }
        lines.add(line.toString());
      }
    }
    TextFileUtilities.saveToFile(lines, filename);
    System.out.println("Done");
  }

  
  //Returns a map from concept ID to a map containing concept IDs, and the strings (terms) that they have in common.
  public Map<Integer,Map<Integer,List<String>>> compareConcepts() {
    System.out.println("Releasing thesaurus");
    release();
    System.out.println("Analyzing terms");
    Map<Integer, Map<Integer, List<String>>> overlap = new TreeMap<Integer, Map<Integer, List<String>>>();
    for (ReleasedTerm term: terms) {
      Set<Integer> uniqueCIDs = new TreeSet<Integer>();
      for (int cid : term.conceptId)
        uniqueCIDs.add(cid);
      if (uniqueCIDs.size() > 1) {
        List<Integer> array = new IntList();
        array.addAll(uniqueCIDs);
        String termString = ontology.getConcept(term.conceptId[0]).getTerms().get(term.termId[0]).text;
        System.out.println(array);
        //Collections.sort(array);
        for (int i = 0; i < array.size(); i++) {
          for (int j = 0 ; j < array.size(); j++) 
          if (i != j){
            Map<Integer, List<String>> map = overlap.get(array.get(i));
            if (map == null) {
              map = new TreeMap<Integer, List<String>>();
              overlap.put(array.get(i), map);
            }
            List<String> overlapStrings = map.get(array.get(j));
            if (overlapStrings == null) {
              overlapStrings = new ArrayList<String>();
              map.put(array.get(j), overlapStrings);
            }
            overlapStrings.add(termString);
          }
        }
      }
    }
    return overlap;
  }
  //Returns a map from concept ID to a vector containing concept IDs, and the number of terms that they have in common.
  public Map<Integer,SparseVectorInt2Float> compareConceptsLight() {
    System.out.println("Releasing thesaurus");
    release();
    System.out.println("Analyzing terms");
    Map<Integer, SparseVectorInt2Float> overlap = new TreeMap<Integer, SparseVectorInt2Float>();
    for (ReleasedTerm term: terms) {
      Set<Integer> uniqueCIDs = new TreeSet<Integer>();
      for (int cid : term.conceptId)
        uniqueCIDs.add(cid);
      if (uniqueCIDs.size() > 1) {
        List<Integer> array = new ArrayList<Integer>(uniqueCIDs);
     //   String termString = ontology.getConcept(term.conceptId[0]).getTerms().get(term.termId[0]).text;
        Collections.sort(array);
        for (int i = 0; i < (array.size() - 1); i++) {
          for (int j = i + 1; j < array.size(); j++) {
            SparseVectorInt2Float map = overlap.get(array.get(i));
            if (map == null) {
              map = new SparseVectorInt2Float();
              overlap.put(array.get(i), map);
            }
            Double overlapStrings = map.get(array.get(j));
           overlapStrings++;
           map.set(array.get(j),overlapStrings);
          }
        }
      }
    }
    return overlap;
  }
  
  public void compareConceptsAndPrint2File(String filename){
    Map<Integer, Map<Integer, List<String>>> overlap = compareConcepts(); 
    List<String> lines = new ArrayList<String>();
    for (Entry<Integer, Map<Integer, List<String>>> entry: overlap.entrySet()) {
      Concept concept1 = ontology.getConcept(entry.getKey());
      Map<Integer, List<String>> strings = entry.getValue();
      for (Entry<Integer, List<String>> entry2: strings.entrySet()) {
        List<String> value = entry2.getValue();
        Concept concept2 = ontology.getConcept(entry2.getKey());

        if (value.size() > 1) {
          String line = concept1.getID() + "\t" + concept2.getID() + "\t" + value.size() + "\t" + concept1.getTerms().size() + "\t" + concept2.getTerms().size();
          if (value.size() == concept1.getTerms().size() || value.size() == concept1.getTerms().size()){
            line += "\t1";
          }
          else
            line+="\t0";
          Double dice = (double)2d* value.size() / (double) (concept1.getTerms().size() + concept2.getTerms().size());
          line += "\t" + dice;
          String terms = StringUtilities.join(value, ";");
          line += "\t" + terms;
          lines.add(line);
        }
      }
    }
    TextFileUtilities.saveToFile(lines, filename);
    System.out.println("Done");
  }
}
