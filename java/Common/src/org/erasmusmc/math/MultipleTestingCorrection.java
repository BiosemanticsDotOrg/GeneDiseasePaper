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

package org.erasmusmc.math;

import java.util.List;

import org.erasmusmc.math.vector.ObjectAndDoubleEntry;
import org.erasmusmc.math.vector.SparseVector;
import org.erasmusmc.math.vector.Vector;

public class MultipleTestingCorrection {
/**
 * benjamini hochberg fdr
 * @param p_values
 * @return
 */
  @SuppressWarnings("unchecked")
  static public SparseVector benjaminiHochberg(Vector p_values) {
    List<ObjectAndDoubleEntry> sorted = p_values.getNonZeroEntriesSortedOnDescendingValue();
    SparseVector vector = new SparseVector(p_values);
    double n = sorted.size();
    Double min = 1d;
    for (int i = 0; i < sorted.size(); i++) {
      Double padj = sorted.get(i).value * (n / (double) (n - i));
      if (padj > min) {
        vector.set(sorted.get(i).key, min.floatValue());
      }
      else {
        min = padj;
        vector.set(sorted.get(i).key, padj.floatValue());
      }
    }
    return vector;
  }
}
