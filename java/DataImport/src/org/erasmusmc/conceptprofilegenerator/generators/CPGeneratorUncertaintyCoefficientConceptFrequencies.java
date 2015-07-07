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

package org.erasmusmc.conceptprofilegenerator.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.applications.conceptprofileevaluator.SubGroundhogStatistics;
import org.erasmusmc.collections.SortedIntList2FloatMap;
import org.erasmusmc.groundhog.ConceptStatistic;
import org.erasmusmc.groundhog.GroundhogStatistics;
import org.erasmusmc.math.AssociationMeasures;
import org.erasmusmc.ontology.ConceptProfile;
import org.erasmusmc.ontology.ConceptVector;

public class CPGeneratorUncertaintyCoefficientConceptFrequencies extends ConceptProfileGenerator {
  public double cutoff =0d;
  

  public CPGeneratorUncertaintyCoefficientConceptFrequencies(GroundhogStatistics wholeGroundhogStatistics, Set<Integer> conceptsForFiltering) {
    super(wholeGroundhogStatistics, conceptsForFiltering);
  }

  /*
   * public Map<Double, ConceptProfile> generateConceptProfiles(Map<Double,
   * Double> cutoffvalues) { Map<Double, ConceptProfile> result = new HashMap<Double,
   * ConceptProfile>(); for (Double key: cutoffvalues.keySet()) {
   * result.put(key, generateConceptProfile(cutoffvalues.get(key))); }
   * 
   * return result; }
   */
  @Override
public ConceptProfile generateConceptProfile(SubGroundhogStatistics subGroundhogStatistics, Integer ownerConcept) {
    List<ConceptVectorEntry> values = new ArrayList<ConceptVectorEntry>(subGroundhogStatistics.conceptStatistics.values().size());
    for (Map.Entry<Integer, ConceptStatistic> entry : subGroundhogStatistics.conceptStatistics.entrySet()){
      int concept = entry.getKey();
      ConceptStatistic conceptStatistic = entry.getValue();
      double weight = 0;

      if (conceptsToBeFiltered == null || !conceptsToBeFiltered.contains(concept))
        weight = getWeight(concept, conceptStatistic, subGroundhogStatistics.allConceptOccurrences);

      if (weight > cutoff) {
        values.add(new ConceptVectorEntry(concept, weight));
      }
    }
    Collections.sort(values, ConceptVectorEntry.fingerprintEntryComparator());
    int i = 0;
    int size;
    if (values.size()>maxNumberOfConceptsPerProfile)size=maxNumberOfConceptsPerProfile;
    else size = values.size();
    SortedIntList2FloatMap map = new SortedIntList2FloatMap(size);
    while (i < size) {
      ConceptVectorEntry mapentry = values.get(i);
      map.put(mapentry.key, new Double(mapentry.value).floatValue());
      i++;
    }
    ConceptVector cv =new ConceptVector(null,map);
    ConceptProfile result = new ConceptProfile(ownerConcept, cv);
    return result;
  }

  protected double getWeight(int cid, ConceptStatistic conceptStatistic, Integer allConceptOccurrencesInSubset) {
    int A = conceptStatistic.termFrequency;
    int B;
    ConceptStatistic mainCollectionStatistic = groundhogStatistics.conceptStatistics.get(cid);
    if (mainCollectionStatistic == null){
      B = - A;
    } else {
      B = mainCollectionStatistic.termFrequency - A;
    }  
    int C = allConceptOccurrencesInSubset - A;
    int D = groundhogStatistics.allConceptOccurrences - A - B - C;
    return AssociationMeasures.symmetricUncertaintyCoefficient(A, B, C, D)  ;
//    return AssociationMeasures.asymmetricUncertaintyCoefficient(A, C, B, D)  ;
  }

}
