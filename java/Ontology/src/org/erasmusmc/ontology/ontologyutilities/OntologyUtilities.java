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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.peregrine.SimpleTokenizer;
import org.erasmusmc.peregrine.Tokenizer;
import org.erasmusmc.peregrine.UMLSGeneChemTokenizer;
import org.erasmusmc.utilities.StringUtilities;

public class OntologyUtilities {
	public static Set<String> stopwordsForFiltering = getDefaultStopWordsForFiltering();
	public static Set<String> stopwordsForIndexing = getDefaultStopWordsForIndexing();
  // Specifies the maximum amount of characters allowed for a gene symbol.
  public static Set<Integer> chemicalSemanticTypes = getChemicalSemanticTypes();
  public static int maxGeneSymbolLength = 6;
  public static String geneVocabulary = "GENE";
  public static String chemVocabulary = "CHEMICAL";
  public static int minChemID = 4000000;
  public static Tokenizer tokenizer = new SimpleTokenizer();
  //public static Tokenizer tokenizer = new UMLSGeneChemTokenizer();
  /**
   * Terms will not be tossed out if they have more than the specified number of
   * tokens. <br>
   * <br>
   * The default value is 7
   */
  public static int minTokenNumberForNoFilter = 7;

  /**
   * The minimum number of characters for a word to be considered non-ambiguous
   * for the filter. <br>
   * <br>
   * The default value is 2.
   */
  public static int minWordSize = 2;

  /**
   * Terms with less characters will be removed by the filter. <br>
   * <br>
   * The default value is 3.
   */
  public static int minTermSize = 3;

  /**
   * Terms consisting of more tokens will always be matched order-sensitive
   * (default = 7)
   */
  public static int maxTermLengthForOrderInsensitivity = 7;
  /**
   * If one of the tokens consists of the specified number of characters or
   * less, the term will always be matched order-sensitive. The assumtion is
   * that it is likely a systematic name such as a chemical formula, for which
   * order is important. The default value is 3.
   */
  public static int minTokenLengthForOrderInsensitivity = 3;

  public static boolean hasGeneVoc(Concept concept, Ontology ontology) {
    if (geneVocabulary.equals(""))
      return true;
    for (Relation relation: ontology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.fromVocabulary)) {

      if (ontology.getConcept(relation.object).getName().equals(geneVocabulary))
        return true;
    }
    return false;
  }
  
  public static boolean hasChemVoc(Concept concept, Ontology ontology) {
	    if (chemVocabulary.equals(""))
	      return true;
	    for (Relation relation: ontology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.fromVocabulary)) {

	      if (ontology.getConcept(relation.object).getName().equals(chemVocabulary))
	        return true;
	    }
	    return false;
	  }

  public static boolean isChemical(Integer conceptID, Ontology ontology) {
    List<Relation> relations = ontology.getRelationsForConceptAsSubject(conceptID, DefaultTypes.isOfSemanticType);
    for (Relation relation: relations) {
      if (chemicalSemanticTypes.contains(relation.object))
        return true;
    }
    return false;
  }

  public static void setMatchingFlagsForOntology(Ontology ontology) {
    Iterator<Concept> conceptIterator = ontology.getConceptIterator();

    while (conceptIterator.hasNext()) {
      Concept concept = conceptIterator.next();
      if (hasGeneVoc(concept, ontology)) {
        for (TermStore term: concept.getTerms())
          setGeneChemMatchingFlags(term);
      }
      else {
        for (TermStore term: concept.getTerms())
          setDefaultMatchingFlags(term);
      }
    }
  }

  public static void mergeConceptsIntoNew(Ontology ontology, int id1, int id2, int newConceptID) {
    Concept newConcept = new Concept(newConceptID);
    ontology.setConcept(newConcept);
    mergeConcepts(ontology, id1, newConceptID);
    mergeConcepts(ontology, id2, newConceptID);
  }

  public static void mergeConcepts(Ontology ontology, int fromCUI, int toCUI) {
    mergeConcepts(ontology, fromCUI, toCUI, true);
  }

  public static void mergeConcepts(Ontology ontology, int fromCUI, int toCUI, boolean removeFromConcept) {
    if (fromCUI == toCUI) {
      System.out.println("ERROR: attempted to merge " + fromCUI + " to itself!");
    }
    else {
      Concept fromConcept = ontology.getConcept(fromCUI);
      Concept toConcept = ontology.getConcept(toCUI);
      if (fromConcept != null && toConcept != null) {
        List<TermStore> termsfromConcept = fromConcept.getTerms();
        List<Relation> fromRelationsSub = ontology.getRelationsForConceptAsSubject(fromCUI);
        List<Relation> fromRelationsObj = ontology.getRelationsForConceptAsObject(fromCUI);
        List<DatabaseID> fromDbIDs = ontology.getDatabaseIDsForConcept(fromCUI);

        List<TermStore> toTerms = toConcept.getTerms();
        Set<String> toTermSet = getTermsAsSet(toTerms);
        for (TermStore term: termsfromConcept) {
          if (!toTermSet.contains(term.text)) {
            toTerms.add(term);
          }
        }
        toConcept.setTerms(toTerms);
        if (!fromConcept.getDefinition().equals("")) {
          String def = fromConcept.getDefinition();
          if (!toConcept.getDefinition().equals("")) {
            def = toConcept.getDefinition() + ";" + def;
          }
          toConcept.setDefinition(def);
        }
        for (Relation relation: fromRelationsObj) {
          relation.object = toCUI;
          ontology.setRelation(relation);
        }
        for (Relation relation: fromRelationsSub) {
          relation.subject = toCUI;
          ontology.setRelation(relation);
        }
        for (DatabaseID databaseID: fromDbIDs) {
          ontology.setDatabaseIDForConcept(toCUI, databaseID);
        }
        if (removeFromConcept)
          ontology.removeConcept(fromCUI);
      }
      else {
        System.out.println("Attempted merge with a non existing Concept: either " + toCUI + " and/or " + fromCUI);
      }
    }
  }
  
  

  public static void setDefaultMatchingFlags(TermStore term) {
    term.caseSensitive = false;
    term.normalised = true;
    term.orderSensitive = true;
/*
    tokenizer.tokenize(term.text);
    if (tokenizer.tokens.size() <= maxTermLengthForOrderInsensitivity) {
      term.orderSensitive = false;
      for (String token: tokenizer.tokens) {
        if (token.length() < minTokenLengthForOrderInsensitivity || StringUtilities.containsNumber(token)) {
          term.orderSensitive = true;
          break;
        }
      }
    }
    */
  }

  public static boolean isGeneSymbol(String string) {
    return !((string.contains(" ") || !StringUtilities.isAbbr(string)) && string.length() > maxGeneSymbolLength);
  }

  public static String tokenizeAndRemoveStopwordsFromString(String term, Set<String> stopwords) {
	//tokenizer = new SimpleTokenizer();
    String word;
    tokenizer.tokenize(term);
    for (int i = tokenizer.tokens.size() - 1; i > -1; i--) {
      word = tokenizer.tokens.get(i);
      if (!StringUtilities.isAbbr(word) && stopwords.contains(word.toLowerCase())) {
        tokenizer.removeToken(i);
      }
    }
    return StringUtilities.join(tokenizer.tokens, " ");
  }

  public static void setGeneChemMatchingFlags(TermStore term) {
    term.orderSensitive = true;
    term.caseSensitive = false;
    term.normalised = false;
    if (isGeneSymbol(term.text)) { // gene symbol
        if (StringUtilities.containsNumber(term.text) && !MartijnsFilterRule(term.text.toLowerCase(), stopwordsForFiltering)) { // symbol with number
          term.caseSensitive = false;
        }
        else { // symbol without number
          term.caseSensitive = true;
        }
      }
	}
	
  public static boolean hasChemicalConceptID (Concept concept){
    if (concept.getID()>=minChemID)
      return true;
    return false;
  }

  /**
   * Removes terms that consist only of ambiguous words and/or numbers.
   * 
   * @param ontology
   *            Ontology to be filtered.
   */
  public static void filterOntology(Ontology ontology, Set<String> stopwordsForFiltering) {
    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
    while (conceptIterator.hasNext()) {
      Concept concept = conceptIterator.next();
      Iterator<TermStore> termIterator = concept.getTerms().iterator();
      Set<String> previousTerms = new HashSet<String>();
      while (termIterator.hasNext()) {
        TermStore term = termIterator.next();
        if (previousTerms.contains(term.text) || MartijnsFilterRule(term.text, stopwordsForFiltering) || term.text.length() < minTermSize) {
          termIterator.remove();
        }
        else {
          previousTerms.add(term.text);
        }
      }
    }
  }

  public static boolean MartijnsFilterRule(String term, Set<String> stopwordsForFiltering) {
    tokenizer.tokenize(term);
    if (tokenizer.tokens.size() >= minTokenNumberForNoFilter)
      return false;

    for (String token: tokenizer.tokens) {
      if (token.length() >= minWordSize && !StringUtilities.isNumber(token) && !StringUtilities.isRomanNumeral(token) && (StringUtilities.isAbbr(token) || !stopwordsForFiltering.contains(token.toLowerCase()))) {

        return false;
      }
    }
    return true;
  }

  public static void removeDuplicateTerms(List<TermStore> terms) {
    Set<String> previousTerms = new HashSet<String>();
    Iterator<TermStore> iterator = terms.iterator();
    while (iterator.hasNext()) {
      TermStore term = iterator.next();
      if (previousTerms.contains(term.text)) {
        iterator.remove();
      }
      else {
        previousTerms.add(term.text);
      }
    }
  }

  public static Set<String> getTermsAsSet(List<TermStore> terms) {
    Set<String> result = new HashSet<String>();
    for (TermStore term: terms) {
      result.add(term.text);
    }
    return result;
  }

  public static Set<Integer> getChemicalSemanticTypes() {
    Set<Integer> result = new TreeSet<Integer>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(OntologyUtilities.class.getResourceAsStream("Chemicals semantic types.txt")));
    try {
      while (bufferedReader.ready()) {
        result.add(-Integer.parseInt(bufferedReader.readLine()));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;

  }

  /**
   * This function filters an ontology by semantictype(s). The
   * parameter inclusive indicates whether the set of semantic types should be
   * considered and inclusive or exclusive filter. The parameter strict
   * specifies whether the filter includes/excludes concepts that have only
   * (true) or any (false) of the specified semantic types.
   * 
   * @param ontology
   * @param semanticTypeFilter
   * @param inclusive
   * @param strict
   */
  public static void filterOntologyBySemanticTypes(Ontology ontology, Set<Integer> semanticTypeFilter, boolean inclusive, boolean strict) {
    Set<Integer> selection;
    if (strict)
      selection = getSemanticFilter(ontology, semanticTypeFilter);
    else
      selection = getSemanticSelection(ontology, semanticTypeFilter);
    Iterator<Concept> it = ontology.getConceptIterator();
    if(inclusive){
      while(it.hasNext()){
        if( ! selection.contains(it.next().getID())){
          it.remove();
        }
      }
    }
    else{
      while(it.hasNext()){
        if(selection.contains(it.next().getID())){
          it.remove();
        }
      }
    }
  }

  /**
   * This function selects all concepts that are only of the given semantic
   * types.
   * 
   */
  public static Set<Integer> getSemanticFilter(Ontology ontology, Set<Integer> semanticTypes) {

    Set<Integer> result = getSemanticSelection(ontology, semanticTypes);
    Iterator<Integer> iterator = result.iterator();
    while (iterator.hasNext()) {
      Integer cui = iterator.next();
      List<Relation> relations = ontology.getRelationsForConceptAsSubject(cui, DefaultTypes.isOfSemanticType);
      int i = 0;
      while (i < relations.size()) {
        Relation relation = relations.get(i++);
        if (!semanticTypes.contains(relation.object)) {
          i = relations.size();
          iterator.remove();
        }
      }
    }
    return result;
  }

  /**
   * This function selects all concepts that are of one of the given semantic
   * types.
   * 
   */
  public static Set<Integer> getSemanticSelection(Ontology ontology, Set<Integer> semanticTypes) {
    Set<Integer> result = new HashSet<Integer>();
    for (Integer semantictype: semanticTypes) {
      List<Relation> relations = ontology.getRelationsForConceptAsObject(semantictype, DefaultTypes.isOfSemanticType);
      for (Relation relation: relations)
        result.add(relation.subject);
    }
    return result;
  }

  /** Removes the terms specified in the removeTerms list from the ontology.
   * @@param ontology    The ontology to be filtered.
   * @@param removeTerms The list of terms that will be removed. */
  public static void removeTerms(Ontology ontology, Collection<String> removeTerms){ //removes the specified terms
    System.out.println("Removing " + removeTerms.size() + " terms");
    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
    while (conceptIterator.hasNext()){
      Concept concept = conceptIterator.next();
      if (hasGeneVoc(concept, ontology)){
        Iterator<TermStore> termIterator = concept.getTerms().iterator();
        while (termIterator.hasNext()){
          TermStore term = termIterator.next();
          if (removeTerms.contains(term.text)) 
            termIterator.remove();
        }
      }
    }  
  }
  
  public static Set<String> getDefaultStopWordsForFiltering() {
		Set<String> result = new TreeSet<String>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(OntologyCurator.class.getResourceAsStream("DefaultStopwordsForFiltering.txt")));
		try {
			while (bufferedReader.ready()) {
				result.add(bufferedReader.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	private static Set<String> getDefaultStopWordsForIndexing() {
		Set<String> result = new TreeSet<String>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(OntologyCurator.class.getResourceAsStream("DefaultStopwordsForIndexing.txt")));
		try {
			while (bufferedReader.ready()) {
				result.add(bufferedReader.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
