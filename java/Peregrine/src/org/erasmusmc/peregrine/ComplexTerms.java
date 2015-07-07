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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.erasmusmc.utilities.StringUtilities;

public class ComplexTerms {
  /**
   * Terms are considered complex if equal or longer than this size (or have
   * numbers) <br>
   * <br>
   * The default value is 6.
   */
  public static int minTermLength = 6;
  private int minConceptID;
  private int maxConceptID;
  private Set<ReleasedTerm> complexTerms;
  
  public ComplexTerms(ConceptPeregrine peregrine, int minConceptID, int maxConceptID) {
    this.minConceptID = minConceptID;
    this.maxConceptID = maxConceptID;
    Map<Integer, TokenInfo> token2info = new HashMap<Integer, TokenInfo>();
    analyseTokens(peregrine.words, token2info);
    analyseTokens(peregrine.lcwords, token2info);
    analyseTokens(peregrine.normwords, token2info);
    
    // Analyse terms:
    Map<ReleasedTerm, TokenInfo> term2tempInfo = new HashMap<ReleasedTerm, TokenInfo>();

    for (Map.Entry<Integer, ReleasedTerm> entry: peregrine.token2Term.entrySet())
      if (hasValidConceptID(entry.getValue()))
        term2tempInfo.put(entry.getValue(), token2info.get(entry.getKey()));

    for (Entry<ConceptPeregrine.TokenPair, List<ConceptPeregrine.TermLink>> entry: peregrine.pair2Termlinks.entrySet()) {
      ConceptPeregrine.TokenPair pair = entry.getKey();
      for (ConceptPeregrine.TermLink termLink: entry.getValue()) {
        if (hasValidConceptID(termLink.term)) {

          TokenInfo tempInfo = term2tempInfo.get(termLink.term);
          if (tempInfo == null) {
            tempInfo = new TokenInfo();
            term2tempInfo.put(termLink.term, tempInfo);
          }
          // Beware: for terms with more than 2 tokens, or with order
          // insensitivity, length will be overestimated!
          tempInfo.combine(token2info.get(pair.token1));
          tempInfo.combine(token2info.get(pair.token2));
        }
      }
    }

    complexTerms = new HashSet<ReleasedTerm>();
    for (Map.Entry<ReleasedTerm, TokenInfo> entry: term2tempInfo.entrySet()) {
      TokenInfo tokenInfo = entry.getValue();
      if (isComplex(tokenInfo))
        complexTerms.add(entry.getKey());
    }
  }
  
  protected static void analyseTokens(Map<String, Integer> words, Map<Integer, TokenInfo> token2info) {
    for (Map.Entry<String, Integer> entry: words.entrySet()) {
      TokenInfo tokenInfo = new TokenInfo();
      String token = entry.getKey();
      tokenInfo.letters = StringUtilities.countLetters(token);
      tokenInfo.numbers = StringUtilities.countNumbers(token);
      tokenInfo.length = token.length();
      token2info.put(entry.getValue(), tokenInfo);
    }
  }
  
  private boolean hasValidConceptID(ReleasedTerm term) {
    for (Integer conceptID: term.conceptId)
      if (conceptID > minConceptID && conceptID < maxConceptID)
        return true;
    return false;
  }
  
  protected static boolean isComplex(TokenInfo tokenInfo) {
    if(tokenInfo.length >= minTermLength){
      return true;
    }
    else if(tokenInfo.numbers>0&&tokenInfo.letters>0&&tokenInfo.length>2){
      return true;
    }
    else{
      return false;
    }
  }
  
  protected static class TokenInfo {
    int numbers = 0;
    int letters = 0;
    int length = 0;

    public void combine(TokenInfo tokenInfo) {
      numbers += tokenInfo.numbers;
      letters += tokenInfo.letters;
      length += tokenInfo.length;
    }
  }
  
  public boolean isComplex(ReleasedTerm term){
    return complexTerms.contains(term);
  }  
}
