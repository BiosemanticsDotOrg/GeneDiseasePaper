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

import java.util.List;

import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;

public class ApplyUMLSDisambiguatorRule {
  public static final int ruleID = 6;
  private int minConceptID;
  private int maxConceptID;

  public ApplyUMLSDisambiguatorRule(int minConceptID, int maxConceptID) {
    this.minConceptID = minConceptID;
    this.maxConceptID = maxConceptID;
  }
  public boolean evaluate(ResultConcept resultConcept, List<EvaluationResult> evaluationResults) {
    if (resultConcept.conceptId >= minConceptID && resultConcept.conceptId < maxConceptID){
      if (evaluationResults != null)
        evaluationResults.add(new EvaluationResult(ruleID, true));
      return true;
    } else
      return false;
  }
}