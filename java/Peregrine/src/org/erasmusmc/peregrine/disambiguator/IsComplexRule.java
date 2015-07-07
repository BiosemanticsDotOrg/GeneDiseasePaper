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

import org.erasmusmc.peregrine.ComplexTerms;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;

public class IsComplexRule {
  public static final int ruleID = 5;
  private ComplexTerms complexTerms;
  
  public IsComplexRule(ConceptPeregrine peregrine, int minConceptID, int maxConceptID){
    this.complexTerms =  new ComplexTerms(peregrine, minConceptID, maxConceptID);
  }
  
  public boolean evaluate(ResultTerm term, List<EvaluationResult> evaluationResults) {
    boolean result = complexTerms.isComplex(term.term); 
    if (evaluationResults != null)
      evaluationResults.add(new EvaluationResult(ruleID, result));
    return result;
  }

}
