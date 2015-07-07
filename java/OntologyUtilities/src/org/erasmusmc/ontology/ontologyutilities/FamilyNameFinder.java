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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.ListTree;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.peregrine.SimpleTokenizer;
import org.erasmusmc.peregrine.Tokenizer;
import org.erasmusmc.utilities.StringUtilities;

public class FamilyNameFinder {
  public String geneVoc = "GENE";
  public int minFamilyNameLength = 5;

  public Map<String, Integer> findFamilyNames(Ontology ontology) {
    Map<String, Integer> result = new HashMap<String, Integer>();

    // Generate set of terms:
    Set<String> termset = new HashSet<String>();
    // CountingSet<String> termset = new CountingSet<String>();

    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
    while (conceptIterator.hasNext()) {
      Concept concept = conceptIterator.next();
      if (hasGeneVoc(concept, ontology))
        for (TermStore term: concept.getTerms()) {
          termset.add(term.text);
        }
    }
    // Build tree:
    ListTree<String, List<String>> listTree = new ListTree<String, List<String>>();
    // ListTree<String, List<String>> reverseListTree = new ListTree<String,
    // List<String>>();
    Tokenizer tokenizer = new SimpleTokenizer();
    for (String term: termset) {
      tokenizer.tokenize(term);
      tokenizer.tokens = toLowercase(tokenizer.tokens);
      addToListTree(tokenizer.tokens, term, listTree);

    }
    for (ListTree<String, List<String>> terminator: listTree.terminatorSet())
      for (Map.Entry<String, ListTree<String, List<String>>> nextNode: terminator.subTree.entrySet())
        if (isMemberIndicator(nextNode.getKey()))
          for (String term: terminator.value)
            if (term.length() > minFamilyNameLength && !StringUtilities.isAbbr(term)) {
              Integer count = result.get(term);
              if (count == null) {
                count = 0;
              }
              result.put(term, ++count);
            }

    return result;
  }

  public static List<String> findFamilyNamesListOutput(Ontology ontology) {
    FamilyNameFinder finder = new FamilyNameFinder();
    return new ArrayList<String>(finder.findFamilyNames(ontology).keySet());
  }

  private static boolean isMemberIndicator(String key) {
    return (StringUtilities.isNumber(key) || StringUtilities.isRomanNumeral(key) || StringUtilities.isGreekLetter(key));
  }

  private static void addToListTree(List<String> tokens, String term, ListTree<String, List<String>> listTree) {
    ListTree<String, List<String>> terminator = listTree.get(tokens);
    List<String> identicalTerms;
    if (terminator == null || terminator.value == null) {
      identicalTerms = new ArrayList<String>();
      listTree.put(tokens, identicalTerms);
    }
    else
      identicalTerms = terminator.value;
    identicalTerms.add(term);
  }

  private static List<String> toLowercase(List<String> tokens) {
    List<String> result = new ArrayList<String>(tokens.size());
    for (String token: tokens) {
      if (StringUtilities.isAbbr(token))
        result.add(token);
      else
        result.add(token.toLowerCase());
    }
    return result;
  }

  private int fromVocabulary = DefaultTypes.fromVocabulary;

  private boolean hasGeneVoc(Concept concept, Ontology ontology) {
    List<Relation> relations = ontology.getRelationsForConceptAsSubject(concept.getID(), fromVocabulary);
    if (geneVoc.equals("") && relations.size() == 0)
      return true;
    for (Relation relation: relations) {
      if (ontology.getConcept(relation.object).getName().equals(geneVoc))
        return true;
    }
    return false;
  }
  /**
   * System.out.println("Counting substrings " + StringUtilities.now()); List<String>
   * lines = new ArrayList<String>(); Map<List<String>, Integer> term2count =
   * new HashMap<List<String>, Integer>(); Set<List<String>> typicalTerms =
   * new HashSet<List<String>>(); for (ListTree<String, List<String>>
   * terminator : listTree.terminatorSet()){ int superSets =
   * terminator.terminatorSet().size()-1; if (superSets > 0)
   * term2count.put(terminator.value, superSets); } for (ListTree<String, List<String>>
   * terminator : reverseListTree.terminatorSet()){ int superSets =
   * terminator.terminatorSet().size()-1; if (superSets > 0){ Integer count =
   * term2count.get(terminator.value); if (count == null) count = 0; for (String
   * term : terminator.value){ lines.add((count + superSets) + "\t" + count +
   * "\t" + superSets + "\t" + term); }
   *  } }
   */

}
