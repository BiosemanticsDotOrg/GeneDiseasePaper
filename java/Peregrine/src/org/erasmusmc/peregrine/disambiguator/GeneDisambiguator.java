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
public class GeneDisambiguator extends AbstractDisambiguator {
  private ApplyGeneDisambiguatorRule applyGeneDisambiguatorRule;
  private IsHomonymRule isHomonymRule;
  private IsPreferredTermRule isPreferredTermRule;
  private IsComplexRule isComplexRule;
  private HasSynonymRule hasSynonymRule;
  private HasKeywordsRule hasKeywordsRule;
  
  /**
   * The gene disambiguator should be initialised using a released ontology before
   * disambiguation.
   * 
   * @param peregrine
   *            Specifies the ConceptPeregrine that should be used for
   *            initalisation.
   * @param minConceptID
   *            The lower end of the range of conceptIDs to which the disambiguator applies.
   * @param maxConceptID
   *            The upper end (exclusive) of the range of conceptIDs.
   */
  public GeneDisambiguator(ConceptPeregrine peregrine, int minConceptID, int maxConceptID){
    applyGeneDisambiguatorRule = new ApplyGeneDisambiguatorRule(minConceptID, maxConceptID);
    isHomonymRule = new IsHomonymRule();
    isPreferredTermRule = new IsPreferredTermRule();
    isComplexRule = new IsComplexRule(peregrine, minConceptID, maxConceptID);
    hasSynonymRule = new HasSynonymRule();
    hasKeywordsRule = new HasKeywordsRule(peregrine, minConceptID, maxConceptID);
  }

  protected boolean removeConcept(ConceptPeregrine peregrine, ResultConcept concept, List<EvaluationResult> evaluationResults){
    if (applyGeneDisambiguatorRule.evaluate(concept, evaluationResults)){
      ResultTerm term = concept.terms.get(0);
      if (!isHomonymRule.evaluate(term, concept, evaluationResults) || 
          isPreferredTermRule.evaluate(term, concept, evaluationResults)) 
        if (isComplexRule.evaluate(term, evaluationResults))
          return false;

      if (hasSynonymRule.evaluate(concept, evaluationResults))
        return false;

      if (hasKeywordsRule.evaluate(concept, evaluationResults))
        return false;

      return true;  
    }
    return false;
  }
}
