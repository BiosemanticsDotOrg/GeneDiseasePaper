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
import java.util.Collections;
import java.util.List;

public class WeibullDistribution implements ProbablityDistribution {
  /**
   * location parameter
   */
  private double omega;
  /**
   * shape parameter
   */
  private double kappa; 
/**
 * scale parameter
 */
  private double lambda;
  
  public WeibullDistribution(double lambda,double kappa){
    this.omega = 0d;
    this.kappa= kappa;
    this.lambda = lambda;
  }
  public WeibullDistribution(double lambda,double kappa,double omega){
    this.omega = omega;
    this.kappa = kappa;
    this.lambda = lambda;
  }
  /**
   * estimate parameters from values
   * @param values
   */
  public WeibullDistribution(Collection<Double> values){
    estimateParameters(values);
  }
  public double getLocationParameter(){
    return omega;
  }
  public double getShapeParameter(){
    return kappa;
  }
  public double getScaleParameter(){
    return lambda;
  }
  public double pValueBiggerThanX(double x){
    if (x>0)
      return Math.exp(-Math.pow((x-omega)/lambda,kappa));
      else
        return 1;
  }
  public double pValueSmallerThanX(double x){
    if (x>0)
    return 1d-Math.exp(-Math.pow((x-omega)/lambda,kappa));
    else
      return 0;
  }
  /**
   * Least squared estimation of parameters: simplest but not necessarily best! Consider method of moments for better precision (but at lower speed); 
   *Note this function only matches beta and lambda, so assumes omega is zero!!!! 
   *Note 2: Do not put negative or zero values in here. For the same reason as above. There is no location estimation implemented and please take Weibull bounds into account(!). 
   */
  private void estimateParameters(Collection<Double> values){
    List<Double> sortedValues = new ArrayList<Double>(values);
    Collections.sort(sortedValues);
    
    double sumOfRankScore = 0d;
    double sumlnval = 0d;
    double y = 0d;
    double squaredLNvalSum = 0d;
    int n= values.size();
    for (int i = 1; i<=n;i++){
      double value =sortedValues.get(i-1);
      double rankscore = rankscore(n, i);
      double lnx =Math.log(value); 
      sumOfRankScore+=rankscore;
      sumlnval+=lnx;
      y+=lnx*rankscore;
      squaredLNvalSum+=lnx*lnx;
      
    }
    double avRankScore = sumOfRankScore/(double)n;
    double avlnval=sumlnval/(double)n;
    double nominator = y - sumOfRankScore * sumlnval/(double)n;
    double denominator = squaredLNvalSum - sumlnval*sumlnval/(double) n;
    kappa = nominator/ denominator;
    lambda = Math.exp(avlnval-(avRankScore/kappa));
  }
  
  private double rankscore(int n, int i){
    double medianRank = ((double)i-0.3)/(double)(n+0.4);
    return Math.log(-Math.log(1d-medianRank));
  }
  
}
