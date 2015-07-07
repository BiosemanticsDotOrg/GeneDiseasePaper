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

package org.erasmusmc.userinterface.components;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData;

public class Occurrence {
  public int start, end, cui;
  public Concept concept;

  public Occurrence(){}
      
  public Occurrence(ConceptOccurrenceData conceptOccurrence) {
    cui = conceptOccurrence.getConceptID();
    start = conceptOccurrence.getStartOffset();
    end = conceptOccurrence.getEndOffset();
  }

  public int length() {
    return end - start + 1;
  }

  public boolean contains(int i) {
    return (i >= start) && (i <= end);
  }

  public void shiftTowardsEnd(int charactersToShift) {
    start += charactersToShift;
    end += charactersToShift;
  }
}
