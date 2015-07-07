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

public class HyperGeometricDistribution {
  
  /**When drawing from a set of N object of which D are of a special kind,
  this function returns the chance of finding k objects of 
  the special kind in a sample of size n.
  <br><br>Author: da Robje
  */
  public static double hyperGeometricDistribution(int k, int N, int D, int n){
   
    
    
    double firstBinomial = SpecialFunctions.LNofBinomialCoefficient(D,k);
    double secondBinomial = SpecialFunctions.LNofBinomialCoefficient(N-D,n-k);
    double thirdBinomial = SpecialFunctions.LNofBinomialCoefficient(N,n);
    double intermediate =firstBinomial + secondBinomial - thirdBinomial; 
    return Math.exp(intermediate);
    
  }
  public static double hGDMoreThanK(int k, int N, int D, int n){
    return 1d-hGD_KorLess(k,N,D,n);
  }
  public static double hGD_KorLess(int k, int N, int D, int n){
    double result = 0d;
    for(int i=0;i<=k; i++){
      result +=hyperGeometricDistribution(i,N,D,n);
    }
    if (result<1d){
      return result;
    }
    else{
      return 1d- 1e-30;
    }
  }
}
