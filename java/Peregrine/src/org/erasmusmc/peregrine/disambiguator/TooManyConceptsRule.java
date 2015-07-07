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
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ReleasedTerm;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult.ExtraData;

public class TooManyConceptsRule {
  public static final int ruleID = 8;
  private int maxDubiousConcepts;
  
  public TooManyConceptsRule(int maxDubiousConcepts){
    this.maxDubiousConcepts = maxDubiousConcepts;
  }
  
  public void evaluate(ConceptPeregrine peregrine, DisambiguationDetails details, Set<ResultConcept> dubiousConcepts){
    Set<Integer> dubiousConceptIDs = concepts2ConceptIDs(dubiousConcepts);
    Map<ReleasedTerm, Set<Integer>> term2concepts = createTermToRemainingConceptsMap(peregrine);
    for (Set<Integer> conceptIDs : term2concepts.values()){
      Set<Integer> localDubious = new HashSet<Integer>();
      for (Integer conceptID : conceptIDs)
        if (dubiousConceptIDs.contains(conceptID))
          localDubious.add(conceptID);
      if (localDubious.size() > maxDubiousConcepts){
        removeDubiousConcepts(localDubious, peregrine, details);
      }  
    }
    addEvaluationResultForRemainingDubious(peregrine, details, dubiousConceptIDs);
  }
  
  private void addEvaluationResultForRemainingDubious(ConceptPeregrine peregrine, DisambiguationDetails details, Set<Integer> dubiousConceptIDs) {
    if (details != null)
      for (ResultConcept concept : peregrine.resultConcepts)
        if (dubiousConceptIDs.contains(concept.conceptId)){
          List<EvaluationResult> evaluationResults = details.conceptID2EvaluationResult.get(concept.conceptId); 
          evaluationResults.add(new EvaluationResult(ruleID, false));
        }
  }

  private void removeDubiousConcepts(Set<Integer> localDubious, ConceptPeregrine peregrine, DisambiguationDetails details) {
    Iterator<ResultConcept> iterator = peregrine.resultConcepts.iterator();
    while (iterator.hasNext()){
      ResultConcept concept = iterator.next();
      if (localDubious.contains(concept.conceptId)){
        iterator.remove();
        if (details != null){
          details.removedConcepts.add(concept);
          List<EvaluationResult> evaluationResults = details.conceptID2EvaluationResult.get(concept.conceptId);
          addPositiveEvaluation(evaluationResults, localDubious, concept.conceptId);
        }
      }
    }  
  }

  private void addPositiveEvaluation(List<EvaluationResult> evaluationResults, Set<Integer> localDubious, int conceptID) {
    List<ExtraData> extraDatas = new ArrayList<ExtraData>();
    for (int dubiousID : localDubious)
      if (dubiousID != conceptID)
      extraDatas.add(new ExtraData(ExtraData.OTHER_CONCEPT, Integer.toString(dubiousID)));
    evaluationResults.add(new EvaluationResult(ruleID, true, extraDatas));
  }

  private Set<Integer> concepts2ConceptIDs(Set<ResultConcept> concepts) {
    Set<Integer> conceptIDs = new HashSet<Integer>();
    for (ResultConcept concept : concepts)
      conceptIDs.add(concept.conceptId);
    return conceptIDs;
  }

  private Map<ReleasedTerm, Set<Integer>> createTermToRemainingConceptsMap(ConceptPeregrine peregrine){
    Map<ReleasedTerm, Set<Integer>> term2concept = new IdentityHashMap<ReleasedTerm, Set<Integer>>();
    for (ResultConcept concept : peregrine.resultConcepts)
      for (ResultTerm term : concept.terms){
          Set<Integer> cuis = term2concept.get(term.term);
          if (cuis == null){cuis = new HashSet<Integer>(); term2concept.put(term.term, cuis);}
          cuis.add(concept.conceptId);
      }
    return term2concept;
  }
}
