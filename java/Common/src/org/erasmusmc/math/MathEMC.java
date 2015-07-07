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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.erasmusmc.math.space.Space;
import org.erasmusmc.math.vector.ArrayVector;
import org.erasmusmc.math.vector.Vector;

/**
 * <p>
 * Title: ACS Viewer
 * </p>
 * <p>
 * Description: A viewer to visualize Associative Concept Spaces
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Erasmus MC, Medical Informatics
 * </p>
 * 
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class MathEMC {
  public static double median(List<Double> m) {
    Collections.sort(m);
    int middle = m.size() / 2; // subscript of middle element
    if (m.size() % 2 == 1) {
      // Odd number of elements -- return the middle one.
      return m.get(middle);
    }
    else {
      return (m.get(middle - 1) + m.get(middle)) / 2.0;
    }
  }
  public static double mean(List<Double> m) {
    double result =0;
    double factor = 1d/(double)m.size();
    for(double entry:m){
      result+=factor*entry;
    }
    return result;
  }
  public static double mean(double[] m) {
    double result =0;
    double factor = 1d/(double)m.length;
    for(double entry:m){
      result+=factor*entry;
    }
    return result;
  }
  public static double median(double[] m) {
    Arrays.sort(m);
    int middle = m.length / 2; // subscript of middle element
    if (m.length % 2 == 1) {
      // Odd number of elements -- return the middle one.
      return m[middle];
    }
    else {
      return (m[middle - 1] + m[middle]) / 2.0;
    }
  }
  public static double distancePointToLineSegment(Vector point, Vector lineStart, Vector lineEnd) {
    double lineMagnitude = lineStart.squaredDistanceTo(lineEnd);

    if (lineMagnitude == 0.0d) {
      return point.distanceTo(lineStart);
    }
    else {
      double u = ((point.get(0) - lineStart.get(0)) * (lineEnd.get(0) - lineStart.get(0)) + (point.get(1) - lineStart.get(1)) * (lineEnd.get(1) - lineStart.get(1))) / lineMagnitude;

      if (u >= 0.0d && u <= 1.0d) {
        Vector intersection = new ArrayVector(Space.twoD);

        intersection.set(0, lineStart.get(0) + u * (lineEnd.get(0) - lineStart.get(0)));
        intersection.set(1, lineStart.get(1) + u * (lineEnd.get(1) - lineStart.get(1)));

        return point.distanceTo(intersection);
      }
      else {
        return Double.POSITIVE_INFINITY;
      }
    }
  }
}