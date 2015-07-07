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

import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;

/** Removes highly ambiguous results from a ConceptPeregrine indexation */
public class UMLSDisambiguator extends AbstractDisambiguator {

  /**
   * The normal behavior of the disambiguator is to, when in doubt, assign all possible meanings. If 
   * the number of meanings is higher than the value specified here, it will assign none of the
   * possible meanings.
   * <br>
   * <br>
   * The default value is 3.
   */
  public int maxMeaningsForAutoAssign = 3;
 
  /**
   * If true, concepts with IDs higher than the maxConceptID take precedence over concepts with lower IDs.
   * In other words, if a term is ambiguous with such a concept, only that meaning is assigned.
   * <br>
   * <br>
   * The default value = true.
   */
  public boolean otherDisambiguatorPrecedence = true;
  
  private ApplyUMLSDisambiguatorRule applyUMLSDisambiguatorRule;
  private IsHomonymRule isHomonymRule;
  private IsPreferredTermRule isPreferredTermRule;
  private HasSynonymRule hasSynonymRule;
  private OtherDisambiguatorRule otherDisambiguatorRule;
  
  /**
   * Constructor
   * 
   * @param minConceptID
   *            The lower end of the range of conceptIDs to which the disambiguator applies.
   * @param maxConceptID
   *            The upper end (exclusive) of the range of conceptIDs.
   */
  public UMLSDisambiguator(int minConceptID, int maxConceptID){
    tooManyConceptsRule = new TooManyConceptsRule(maxMeaningsForAutoAssign);
    applyUMLSDisambiguatorRule = new ApplyUMLSDisambiguatorRule(minConceptID, maxConceptID);
    isHomonymRule = new IsHomonymRule();
    isPreferredTermRule = new IsPreferredTermRule();
    hasSynonymRule = new HasSynonymRule();
    otherDisambiguatorRule = new OtherDisambiguatorRule(minConceptID, maxConceptID);
  }
  
  @Override
  protected boolean removeConcept(ConceptPeregrine peregrine, ResultConcept concept, List<EvaluationResult> evaluationResults) {
    if (applyUMLSDisambiguatorRule.evaluate(concept, evaluationResults)){
      ResultTerm term = concept.terms.get(0);
      if (!isHomonymRule.evaluate(term, concept, evaluationResults))
        return false;

      if (otherDisambiguatorPrecedence && otherDisambiguatorRule.evaluate(term, concept, evaluationResults))
        return true;

      if (isPreferredTermRule.evaluate(term, concept, evaluationResults))
        return false;

      if (hasSynonymRule.evaluate(concept, evaluationResults))
        return false;

      reportDubious(concept);
    }
    return false;
  }
}
