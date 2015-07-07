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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.erasmusmc.peregrine.ComplexTerms.TokenInfo;
import org.erasmusmc.peregrine.ConceptPeregrine.TermLink;

public class ConceptKeywords {

  /**
   * Words that appear less than this number in the thesaurus are considered
   * keywords <br>
   * <br>
   * The default value is 1000.
   */
  public int maxKeywordUsage = 1000;
  
  /**
   * Remove keywords that occurr for more than one meaning of a term <br>
   * <br>
   * The default value is True.
   */
  public static boolean removeNonDistinguishing = true;

  private int minConceptID;
  private int maxConceptID;
  private ConceptPeregrine peregrine;

  /**
   * The disambiguator should be initialised using a released ontology before
   * disambiguation.
   * 
   * @param peregrine
   *          Specifies the ConceptPeregrine that should be used for
   *          initalisation.
   */
  public ConceptKeywords(ConceptPeregrine peregrine, int minConceptID, int maxConceptID) {
    this.minConceptID = minConceptID;
    this.maxConceptID = maxConceptID;
    this.peregrine = peregrine;
    
    Map<Integer, TokenInfo> token2info = new HashMap<Integer, TokenInfo>();
    ComplexTerms.analyseTokens(peregrine.words, token2info);
    ComplexTerms.analyseTokens(peregrine.lcwords, token2info);
    ComplexTerms.analyseTokens(peregrine.normwords, token2info);

    // Build set of keywords:
    Set<Integer> keywords = new TreeSet<Integer>();
    for (Entry<Integer, ConceptPeregrine.Count> entry: peregrine.token2count.entrySet())
      if (entry.getValue().value < maxKeywordUsage) // Filter for very common
                                                    // words
        keywords.add(entry.getKey());

    // Remove non-complex keywords:
    removeNonComplexKeywords(keywords, token2info);

    // Add keywords to concepts:
    concept2keywords = new TreeMap<Integer, TreeSet<Integer>>();
    for (Entry<ConceptPeregrine.TokenPair, List<ConceptPeregrine.TermLink>> entry: peregrine.pair2Termlinks.entrySet()) {
      ConceptPeregrine.TokenPair pair = entry.getKey();
      if (keywords.contains(pair.token1))
        addKeywordToTerm(pair.token1, entry.getValue(), peregrine);
      if (keywords.contains(pair.token2))
        addKeywordToTerm(pair.token2, entry.getValue(), peregrine);
    }
      
    if (removeNonDistinguishing)
      removeNondistinguishingKeywords(peregrine);
  }

  private void removeNondistinguishingKeywords(ConceptPeregrine peregrine) {
    Set<Integer> uniquekeywords = new TreeSet<Integer>();
    Set<Integer> duplicateKeywords = new TreeSet<Integer>();

    for (ReleasedTerm term: peregrine.terms) {
      uniquekeywords.clear();
      duplicateKeywords.clear();
      for (int conceptID: term.conceptId) {
        Set<Integer> keywords = concept2keywords.get(conceptID);
        if (keywords != null) {
          for (int keyword: keywords) {
            if (!uniquekeywords.add(keyword))
              duplicateKeywords.add(keyword);
          }
        }
      }

      for (int conceptID: term.conceptId) {
        Set<Integer> keywords = concept2keywords.get(conceptID);
        if (keywords != null)
          keywords.removeAll(duplicateKeywords);
      }
    }
  }

  private void removeNonComplexKeywords(Set<Integer> keywords, Map<Integer, TokenInfo> token2info) {
    Iterator<Integer> keywordIterator = keywords.iterator();
    while (keywordIterator.hasNext()) {
      Integer keyword = keywordIterator.next();
      TokenInfo tokenInfo = token2info.get(keyword);
      if (!ComplexTerms.isComplex(tokenInfo))
        keywordIterator.remove();
    }
  }

  private void addKeywordToTerm(Integer keyword, List<TermLink> termlinks, ConceptPeregrine peregrine) {
    for (TermLink termlink: termlinks) {
      for (Integer conceptID: termlink.term.conceptId) {
        if (conceptID > minConceptID && conceptID < maxConceptID) {
          if (!isSingleWordTerm(keyword, conceptID, peregrine)) {
            TreeSet<Integer> keywords = concept2keywords.get(conceptID);
            if (keywords == null) {
              keywords = new TreeSet<Integer>();
              concept2keywords.put(conceptID, keywords);
            }
            keywords.add(keyword);
          }
        }
      }
    }
  }

  private boolean isSingleWordTerm(Integer keyword, Integer conceptID, ConceptPeregrine peregrine) {
    ReleasedTerm term = peregrine.token2Term.get(keyword);
    if (term == null) //|| !term.conceptId.contains(conceptID))
      return false;
    else {
      for (int id : term.conceptId)
        if (id == conceptID)
          return true;
      return false;
    }
  }

  private Map<Integer, TreeSet<Integer>> concept2keywords;
  
  /*
  private static void DisplayTerm(ConceptPeregrine indexer, ResultTerm resultTerm) {
    StringBuffer term = new StringBuffer();
    for (Integer word: resultTerm.words) {
      term.append(indexer.tokenizer.tokens.get(word));
      term.append(" ");
    }
    StringBuilder termIds = new StringBuilder();
    for (int termId : resultTerm.term.termId)
      termIds.append(termId + ",");
    System.out.print(term.toString() + " termid:" + termIds.toString());
  }
  */
  
  public boolean hasKeyword(ResultConcept concept) {
    TreeSet<Integer> keywords = concept2keywords.get(concept.conceptId);
    if (keywords == null) 
      return false; //No keywords for this concept
    

    // Determine words that are already part of found terms:
    Set<Integer> ignoreTokens = new TreeSet<Integer>();
    for (ResultTerm term: concept.terms)
      for (int word: term.words)
        for (int[] tokenIDs: peregrine.tokenIDslist)
          ignoreTokens.add(tokenIDs[word]);

    for (int[] tokenIDs: peregrine.tokenIDslist) {
      for (int i = 0; i < tokenIDs.length; i++) {
        if (keywords.contains(tokenIDs[i]) && !ignoreTokens.contains(tokenIDs[i])) 
          return true;
      }
    }
    return false;
  }
  
  public String findKeyword(ResultConcept concept) {
    TreeSet<Integer> keywords = concept2keywords.get(concept.conceptId);
    if (keywords == null) 
      return null; //No keywords for this concept
    

    // Determine words that are already part of found terms:
    Set<Integer> ignoreTokens = new TreeSet<Integer>();
    for (ResultTerm term: concept.terms)
      for (int word: term.words)
        for (int[] tokenIDs: peregrine.tokenIDslist)
          ignoreTokens.add(tokenIDs[word]);

    for (int[] tokenIDs: peregrine.tokenIDslist) {
      for (int i = 0; i < tokenIDs.length; i++) {
        if (keywords.contains(tokenIDs[i]) && !ignoreTokens.contains(tokenIDs[i])) 
          return getToken(tokenIDs[i]);
      }
    }
    return null;
  }
  
  private String getToken(int tokenID) {
    for (Entry<String, Integer> entry: peregrine.normwords.entrySet())
      if (entry.getValue() == tokenID) 
        return entry.getKey();


    for (Entry<String, Integer> entry: peregrine.words.entrySet())
      if (entry.getValue() == tokenID) 
        return entry.getKey();


    for (Entry<String, Integer> entry: peregrine.lcwords.entrySet())
      if (entry.getValue() == tokenID) 
        return entry.getKey();

    return null;
  }
}
