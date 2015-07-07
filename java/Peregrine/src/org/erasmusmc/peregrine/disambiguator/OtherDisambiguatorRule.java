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

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult.ExtraData;

public class OtherDisambiguatorRule {
  public static final int ruleID = 7;
  private int minConceptID;
  private int maxConceptID;

  public OtherDisambiguatorRule(int minConceptID, int maxConceptID) {
    this.minConceptID = minConceptID;
    this.maxConceptID = maxConceptID;
  }
  
  public boolean evaluate(ResultTerm term, ResultConcept concept, List<EvaluationResult> evaluationResults) {
    for (int conceptID : term.term.conceptId)
      if (conceptID != concept.conceptId && (conceptID < minConceptID || conceptID > maxConceptID)) {
        if (evaluationResults != null) {
          List<ExtraData> extraDatas = new ArrayList<ExtraData>();
          extraDatas.add(new ExtraData(ExtraData.OTHER_CONCEPT, Integer.toString(conceptID)));
          evaluationResults.add(new EvaluationResult(ruleID, true, extraDatas));
        }
        return true;
      } 
    
    if (evaluationResults != null) 
      evaluationResults.add(new EvaluationResult(ruleID, false));
    return false;
  }
}
