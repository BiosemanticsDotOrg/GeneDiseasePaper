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

public class IsPreferredTermRule {
  public static final int ruleID = 4;
  
  public boolean evaluate(ResultTerm term, ResultConcept concept, List<EvaluationResult> evaluationResults) {
    int termID = -1;
    for (int i = 0; i < term.term.conceptId.length; i++)
      if (term.term.conceptId[i] == concept.conceptId) {
        termID = term.term.termId[i];
        break;
      }
    boolean result = (termID == 0);
    if (evaluationResults != null) {
      List<ExtraData> extraDatas = new ArrayList<ExtraData>();
      extraDatas.add(new ExtraData(ExtraData.TERM_ID, Integer.toString(termID)));
      evaluationResults.add(new EvaluationResult(ruleID, result, extraDatas));
    }
    return result;
  }

}
