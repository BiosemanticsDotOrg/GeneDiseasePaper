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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;

public abstract class AbstractDisambiguator {
  private Set<ResultConcept> dubiousConcepts = new HashSet<ResultConcept>();
  protected TooManyConceptsRule tooManyConceptsRule;
  
  /**
   * Remove highly ambiguous concepts from the resultConcept list of the
   * specified Peregrine. Apply this method after indexation.
   * 
   * @param peregrine
   *          Specifies the ConceptPeregrine of which the resultConcepts should
   *          be disambiguated
   */
  public void disambiguate(ConceptPeregrine peregrine) {
    disambiguate(peregrine, null);
  }
  
  /**
   * Remove highly ambiguous concepts from the resultConcept list of the
   * specified Peregrine. Apply this method after indexation.
   * 
   * @param peregrine   
   *          Specifies the ConceptPeregrine of which the resultConcepts should
   *          be disambiguated
   * 
   * @return  
   *          Returns details about the disambiguation  
   */
  public DisambiguationDetails disambiguateWithDetails(ConceptPeregrine peregrine) {
    DisambiguationDetails details = new DisambiguationDetails();
    disambiguate(peregrine, details);
    return details;
  }
  
  private void disambiguate(ConceptPeregrine peregrine, DisambiguationDetails details){
    dubiousConcepts.clear();
    Iterator<ResultConcept> conceptIterator = peregrine.resultConcepts.iterator();
    while (conceptIterator.hasNext()) {
      ResultConcept concept = conceptIterator.next();
      List<EvaluationResult> evaluationResults = null;
      if (details != null)
        evaluationResults = new ArrayList<EvaluationResult>();
      
      if (removeConcept(peregrine, concept, evaluationResults)){
        if (details != null)
          details.removedConcepts.add(concept);
        conceptIterator.remove();
      }
      
      if (details != null && evaluationResults.size() != 0)
        details.conceptID2EvaluationResult.put(concept.conceptId, evaluationResults);
    }
    if (!dubiousConcepts.isEmpty())
      tooManyConceptsRule.evaluate(peregrine, details, dubiousConcepts);
  }

  protected void reportDubious(ResultConcept concept){
    dubiousConcepts.add(concept);
  }
  
  protected abstract boolean removeConcept(ConceptPeregrine peregrine, ResultConcept concept, List<EvaluationResult> evaluationResults);
}
