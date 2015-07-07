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

import org.erasmusmc.peregrine.ConceptKeywords;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult.ExtraData;

public class HasKeywordsRule {
  public static final int ruleID = 3;
  private ConceptKeywords conceptKeywords;
  
  public HasKeywordsRule(ConceptPeregrine peregrine, int minConceptID, int maxConceptID){
    this.conceptKeywords = new ConceptKeywords(peregrine, minConceptID, maxConceptID);
  }
  
  public boolean evaluate(ResultConcept concept, List<EvaluationResult> evaluationResults) {
    if (evaluationResults != null){
      String keyword = conceptKeywords.findKeyword(concept);
      if (keyword == null){
        evaluationResults.add(new EvaluationResult(ruleID, false));
        return false;
      } else {
        List<ExtraData> extraDatas = new ArrayList<ExtraData>();
        extraDatas.add(new ExtraData(ExtraData.KEYWORD, keyword));
        evaluationResults.add(new EvaluationResult(ruleID, true, extraDatas));
        return true;
      }
    } else {
      return conceptKeywords.hasKeyword(concept);
    }

  }
}

