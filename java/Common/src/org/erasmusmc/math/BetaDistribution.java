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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.collections.IntList;

public class BetaDistribution implements ProbablityDistribution {
  // NOTE THAT THE BETADISTRIBUTION LIES BETWEEN 0 AND 1, OTHERWISE IT NEEDS TO
  // BE BOUNDED
  private double alpha;
  private double beta;
  private double lowerbound = 0d;
  private double upperbound = 1d;

  public BetaDistribution(double alpha, double beta) {
    this.alpha = alpha;
    this.beta = beta;
  }

  public BetaDistribution(double alpha, double beta, double lowerbound, double upperbound) {
    // note that alpha and beta are for scaled (valid) distribution between 0
    // and 1
    this.alpha = alpha;
    this.beta = beta;
    this.lowerbound = lowerbound;
    this.upperbound = upperbound;
  }

  /*
   * use this distribution for values bounded between 0 and 1
   */
  public BetaDistribution(Collection<Double> values) {
    estimateParameters(values);
  }

  public BetaDistribution(Collection<Double> values, double lowerbound, double upperbound) {
    this.lowerbound = lowerbound;
    this.upperbound = upperbound;
    estimateParameters(transformValues(values));

  }
public double getAlpha(){
  return alpha;
}
public double getBeta(){
  return beta;
}
  private Collection<Double> transformValues(Collection<Double> values) {
    List<Double> array = new ArrayList<Double>();
    Iterator<Double> it = values.iterator();
    while (it.hasNext())
      array.add((it.next() - lowerbound) / upperbound);
    return array;
  }

  private void estimateParameters(Collection<Double> values) {
    Iterator<Double> it = values.iterator();
    double average = 0d;
    while (it.hasNext()) {
      average += it.next();
    }
    average /= (double) values.size();
    it = values.iterator();
    double sd = 0d;
    while (it.hasNext()) {
      sd += Math.pow(it.next() - average, 2);
    }
    sd /= (double) values.size();
    double inbetween = ((average * (1 - average) / sd) - 1);
    alpha = average * inbetween;
    beta = (1 - average) * inbetween;

  }

  public double pValueBiggerThanX(double x) {
    double scaledX = (x - lowerbound) / upperbound;
    try {
      return 1 - SpecialFunctions.regularizedIncompleteBetaFunction(alpha, beta, scaledX);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1d;
  }

  public double pValueSmallerThanX(double x) {
    double scaledX = (x - lowerbound) / upperbound;
    try {
      return SpecialFunctions.regularizedIncompleteBetaFunction(alpha, beta, scaledX);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1d;
  }

  public double getLowerbound() {
    return lowerbound;
  }

  public void setLowerbound(double lowerbound) {
    this.lowerbound = lowerbound;
  }

  public double getUpperbound() {
    return upperbound;
  }

  public void setUpperbound(double upperbound) {
    this.upperbound = upperbound;
  }

}
