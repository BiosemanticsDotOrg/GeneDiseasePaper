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

package org.erasmusmc.peregrine.disambiguator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.erasmusmc.peregrine.ReleasedTerm;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;

public class HasSynonymRule {
  public static final int ruleID = 2;
  
  public boolean evaluate(ResultConcept concept, List<EvaluationResult> evaluationResults) {
    boolean result = hasSynonyms(concept); 
    if (evaluationResults != null)
      evaluationResults.add(new EvaluationResult(ruleID, result));
    return result;
  }
  
  private boolean hasSynonyms(ResultConcept concept){
    Set<ReleasedTerm> uniqueTerms = new HashSet<ReleasedTerm>();
    for (ResultTerm term: concept.terms) {
      if (uniqueTerms.add(term.term) && uniqueTerms.size() != 1) {
        return true;
      }
    }
    return false;
  }

}
