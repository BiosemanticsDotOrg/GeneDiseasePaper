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

import java.util.Set;

import org.erasmusmc.applications.conceptprofileevaluator.SubGroundhogStatistics;
import org.erasmusmc.groundhog.GroundhogStatistics;
import org.erasmusmc.ontology.ConceptProfile;

public abstract class ConceptProfileGenerator {
  protected Set<Integer> conceptsToBeFiltered;
  protected GroundhogStatistics groundhogStatistics;
  public int maxNumberOfConceptsPerProfile = 1000;
  public ConceptProfileGenerator(GroundhogStatistics wholeGroundhogStatistics,Set<Integer> conceptsForFiltering){
    this.groundhogStatistics = wholeGroundhogStatistics;
    this.conceptsToBeFiltered  = conceptsForFiltering;
  }
 
  public abstract ConceptProfile generateConceptProfile(SubGroundhogStatistics statistics,Integer ownerConcept);
}
