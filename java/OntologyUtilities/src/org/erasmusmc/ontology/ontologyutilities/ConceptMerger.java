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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.math.vector.SparseVectorInt2Float;
import org.erasmusmc.math.vector.VectorCursor;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.SetUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

public class ConceptMerger {
  // Merged concepts start from:
  public static int startConceptNumber = 4000000;
  // rules
  public static boolean mergeIdentical = true;
  public static boolean mergeWhenSubSet = true;
  public static double minDiceForMergeWhenPreferredTermsMatch = 0.35;
  public static double minDiceForMerge = 0.4d;
  public static double baseScore = 0.01;
  /**
   * this number of terms should overlap or more.
   */
  public static int minimumOverlapForDiceMerge = 2;

  public static Map<Integer, Set<Integer>> conceptMerge(Ontology ontology, String logfile,boolean conservative) {
    Map<Integer, Set<Integer>> mappings = conceptMerge(ontology, conservative);
   
    List<String> lines = new ArrayList<String>();

    for (Entry<Integer, Set<Integer>> mapping: mappings.entrySet()) {
      lines.add(mapping.getKey() + ": " + mapping.getValue().toString());
    }
    TextFileUtilities.saveToFile(lines, logfile);
    return mappings;
  }
  public static Map<Integer, Set<Integer>> conceptMerge(Ontology ontology, boolean conservativeMergeAlgorithm) {
    Map<Integer, Set<Integer>> mappings;
    if(conservativeMergeAlgorithm)
      mappings=conservativeConceptMerge(ontology);
    else
      mappings= greedyConceptMerge(ontology);
    return mappings;
    
  }
  private static Map<Integer, SparseVectorInt2Float> getOverlapMap(Ontology ontology) {
    HomonymAnalyzer homonymAnalyzer = new HomonymAnalyzer();
    homonymAnalyzer.setOntology(ontology);
    Map<Integer, SparseVectorInt2Float> overlap = homonymAnalyzer.compareConceptsLight();
    return overlap;
  }

  public static void addRelation(int id1, int id2, Map<Integer, Set<Integer>> mapping) {
    Set<Integer> mapping1 = mapping.get(id1);
    if (mapping1 == null) {
      mapping1 = new SortedIntListSet();
      mapping.put(id1, mapping1);
    }
    Set<Integer> mapping2 = mapping.get(id2);
    if (mapping2 == null) {
      mapping2 = new SortedIntListSet();
      mapping.put(id2, mapping2);
    }
    mapping1.add(id2);
    mapping2.add(id1);
  }

  private static Map<Integer, Set<Integer>> getMappings(Ontology ontology, Map<Integer, SparseVectorInt2Float> overlapMapping) {
    Map<Integer, Set<Integer>> mappings = new TreeMap<Integer, Set<Integer>>();

    for (Entry<Integer, SparseVectorInt2Float> entry: overlapMapping.entrySet()) {
      Concept concept1 = ontology.getConcept(entry.getKey());
      SparseVectorInt2Float homonyms = entry.getValue();
      VectorCursor<Integer> vc = homonyms.getNonzeroCursor();
      while (vc.isValid()) {
        int value = (int) Math.round(vc.get());
        Concept concept2 = ontology.getConcept(vc.dimension());
        if (shouldBeMerged(concept1, concept2, value)) {
          addRelation(concept1.getID(), concept2.getID(), mappings);
        }
        vc.next();
      }
    }
    return mappings;
  }

  public static Map<Integer, Set<Integer>> conservativeConceptMerge(Ontology ontology) {
    Integer numberOfMappings;
    Map<Integer, Set<Integer>> out = new TreeMap<Integer, Set<Integer>>();
    do {
      Set<Set<Integer>> uniqueMappings = new HashSet<Set<Integer>>();
      Map<Integer, SparseVectorInt2Float> overlap = getOverlapMap(ontology);
      Map<Integer, Set<Integer>> pairwiseMapping = getMappings(ontology, overlap);
      List<Integer> ids = new ArrayList<Integer>(pairwiseMapping.keySet());
      numberOfMappings = 0;
      for (Integer id: ids) {
        Set<Integer> mapClique = pruneCliqueForConcept(id, pairwiseMapping, overlap, ontology);
        removeCliqueFromMapping(mapClique, pairwiseMapping);
        if (mapClique.size() != 0) {
          uniqueMappings.add(mapClique);
          numberOfMappings += mapClique.size();
        }
      }
      out.putAll(performMappings(uniqueMappings, ontology));
      System.out.println("generated " + numberOfMappings.toString() + " mappings");
    } while (numberOfMappings > 0);
    return out;
  }

  private static Map<Integer, Set<Integer>> performMappings(Set<Set<Integer>> uniqueMappings, Ontology ontology) {
    Map<Integer, Set<Integer>> out = new TreeMap<Integer, Set<Integer>>();
    for (Set<Integer> mapping: uniqueMappings) {
      Concept newConcept = new Concept(startConceptNumber);
      out.put(startConceptNumber, mapping);
      ontology.setConcept(newConcept);
      startConceptNumber++;
      for (Integer fromid: mapping) {
        OntologyUtilities.mergeConcepts(ontology, fromid, newConcept.getID());
      }
    }
    return out;
  }

  private static void removeCliqueFromMapping(Set<Integer> mapClique, Map<Integer, Set<Integer>> mapping) {
    for (Integer id: mapClique) {
      Set<Integer> targets = mapping.remove(id);
      for (Integer target: targets) {
        Set<Integer> targetSet = mapping.get(target);
        if (targetSet != null) {
          targetSet.remove(id);
        }
      }
    }

  }

  private static Set<Integer> pruneCliqueForConcept(Integer id, Map<Integer, Set<Integer>> mapping, Map<Integer, SparseVectorInt2Float> overlap, Ontology ontology) {
    Set<Integer> cli = getPotentialClique(id, mapping);
    while (!trueClique(cli, mapping)) {
      pruneClique(cli, overlap, ontology);
    }

    return cli;
  }

  private static void pruneClique(Set<Integer> cli, Map<Integer, SparseVectorInt2Float> overlapMap, Ontology ontology) {
    Integer lowest = null;
    Float lowestScore = Float.MAX_VALUE;
    //List<Integer> ids = new ArrayList<Integer>(cli);
    for (Integer id: cli) {
      double score = 1d;
      for (Integer id2: cli) {
        double overlap;
        if (id != id2) {
          if (id > id2) {
            overlap = overlapMap.get(id2).get(id);
          }
          else {
            overlap = overlapMap.get(id).get(id2);
          }

          double inbetween = score(ontology.getConcept(id), ontology.getConcept(id2), overlap);
          if (inbetween == 0) {
            inbetween = baseScore;
          }
          score *= inbetween;
        }
      }
      if (score < lowestScore) {
        lowest = id;
      }

    }
    cli.remove(lowest);

  }

  private static double score(Concept concept1, Concept concept2, double overlap) {
    if (shouldBeMerged(concept1, concept2, (int) Math.round(overlap))) {
      return 1d;
    }
    Double dice = 2d * overlap / (double) (concept1.getTerms().size() + concept2.getTerms().size());
    if (doPreferredTermsMatch(concept1, concept2)) {
      double bonus = minDiceForMergeWhenPreferredTermsMatch - minDiceForMerge;
      return (dice + bonus) <= 1 ? dice + bonus : dice;
    }
    return dice;
  }

  @SuppressWarnings("unchecked")
  public static boolean trueClique(Set<Integer> cli, Map<Integer, Set<Integer>> mapping) {
    if (cli.size() == 0) {
      return true;
    }
    if (cli.size() == 1) {
      return true;
    }
    for (Integer id: cli) {
      Set<Integer> map = mapping.get(id);
      if (map != null) {
        Set<Integer> remainder = SetUtilities.substraction(cli, map);
        if (remainder.size() > 1 || (remainder.size() == 1 && !remainder.contains(id))) {
          return false;
        }
      }
      else {
        return false;
      }
    }

    return true;
  }

  public static Set<Integer> getPotentialClique(Integer id, Map<Integer, Set<Integer>> mapping) {
    Set<Integer> result = new HashSet<Integer>();
    addNewConcepts(id, result, mapping);
    return result;
  }

  private static void addNewConcepts(Integer id, Set<Integer> result, Map<Integer, Set<Integer>> mapping) {

    if (!result.contains(id)) {
      Set<Integer> maps = mapping.get(id);
      if (maps != null) {
        result.add(id);
        for (Integer id2: maps) {
          addNewConcepts(id2, result, mapping);
        }
      }
    }

  }

  public static Map<Integer, Set<Integer>> greedyConceptMerge(Ontology ontology) {
    Map<Integer, SparseVectorInt2Float> overlap = getOverlapMap(ontology);
    Map<Integer, Set<Integer>> mappings = new TreeMap<Integer, Set<Integer>>();

    for (Entry<Integer, SparseVectorInt2Float> entry: overlap.entrySet()) {
      Concept concept1 = ontology.getConcept(entry.getKey());
      SparseVectorInt2Float homonyms = entry.getValue();
      VectorCursor<Integer> vc = homonyms.getNonzeroCursor();
      while (vc.isValid()) {
        int value = (int) Math.round(vc.get());
        Concept concept2 = ontology.getConcept(vc.dimension());
        if (shouldBeMerged(concept1, concept2, value)) {

          Set<Integer> mapping1 = mappings.get(concept1.getID());
          if (mapping1 == null) {
            mapping1 = new SortedIntListSet();
            mapping1.add(concept1.getID());
          }
          Set<Integer> mapping2 = mappings.get(concept2.getID());
          if (mapping2 == null) {
            mapping2 = new SortedIntListSet();
            mapping2.add(concept2.getID());
          }
          mapping1.addAll(mapping2);
          for (Integer id: mapping1) {
            mappings.put(id, mapping1);
          }

        }
        vc.next();
      }
    }
    Integer currentNewCUI = startConceptNumber;

    Set<Set<Integer>> UniqueMappings = new HashSet<Set<Integer>>(mappings.values());
    mappings = null;
    Map<Integer, Set<Integer>> out = new TreeMap<Integer, Set<Integer>>();
    int count = 0;
    for (Set<Integer> mapping: UniqueMappings) {
      Concept newConcept = new Concept(currentNewCUI);
      out.put(currentNewCUI, mapping);
      ontology.setConcept(newConcept);
      currentNewCUI++;
      for (Integer fromid: mapping) 
        OntologyUtilities.mergeConcepts(ontology, fromid, newConcept.getID());
      count += mapping.size();
    }
    System.out.println("Greedy concept merge merged " + count + " concepts");
    return out;
  }

  protected static boolean isSubset(Integer numberOfHits, Concept concept1, Concept concept2) {
    if (numberOfHits == concept1.getTerms().size() || numberOfHits == concept2.getTerms().size()) {
      return true;
    }
    return false;
  }

  protected static boolean shouldBeMerged(Concept concept1, Concept concept2, int numberofhits) {
    boolean preferredTermsMatch = doPreferredTermsMatch(concept1, concept2);
    boolean subSet = isSubset(numberofhits, concept1, concept2);
    Double dice = (double) 2d * numberofhits / (double) (concept1.getTerms().size() + concept2.getTerms().size());
    return shouldBeMerged(preferredTermsMatch, subSet, dice, numberofhits);
  }

  protected static boolean shouldBeMerged(boolean preferredTermsMatch, boolean subSet, double dice, int numberofhits) {
    if (dice == 1d && mergeIdentical) {
      return true;
    }
    if (subSet && mergeWhenSubSet) {
      return true;
    }
    if (preferredTermsMatch && dice >= minDiceForMergeWhenPreferredTermsMatch && numberofhits >= minimumOverlapForDiceMerge) {
      return true;
    }
    if (dice >= minDiceForMerge && numberofhits >= minimumOverlapForDiceMerge) {
      return true;
    }
    return false;
  }

  protected static boolean doPreferredTermsMatch(Concept concept1, Concept concept2) {
    String pref1 = getPreferredTerm(concept1);
    String pref2 = getPreferredTerm(concept2);
    if (!pref1.equals("") && !pref2.equals("")) {
      if (pref1.compareToIgnoreCase(pref2) == 0) {
        return true;
      }
    }
    return false;
  }

  protected static String getPreferredTerm(Concept concept) {
    List<TermStore> terms = concept.getTerms();
    String preferredterm = "";
    if (terms.size() > 0) {
      preferredterm = terms.get(0).text;
    }
    return preferredterm;
  }

  

}
