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

public class ChiSquaredProbabilityFunction {
  
  static public double chiSquaredProbabilityFunction(double chiSquared, int degreesOfFreedom){
 
     double factor;   // factor which multiplies sum of series
     double g;   // lngamma(k1+1)
     double k1;   // adjusted degrees of freedom
     double sum;   // temporary storage for partial sums
     double term;   // term of series
     double x1;   // adjusted argument of funtion
     double chi2prob; // chi-squared probability
  
       // the distribution function of the chi-squared distribution based on k d.f.
      if ((chiSquared < 0.01) || (chiSquared > 1000.0)){
        if (chiSquared < 0.01){
          chi2prob = 0.0001;
        }
        else {
          chi2prob = 0.999999;
        }
      }
       else{
         x1 = 0.5 * chiSquared;
         k1 = 0.5 * degreesOfFreedom;
         g = gammaLN(k1 + 1);
         factor = Math.exp(k1 * Math.log(x1) - g - x1);
        sum = 0.0;
        if (factor > 0) {
            term = 1.0;
                sum = 1.0;
                while ((term / sum) > 0.000001) 
                {
                       k1 = k1 + 1;
                       term  = term * (x1 / k1);
                       sum = sum + term;
                }
        }
        chi2prob = sum * factor;
       } //end if .. else
       return (1-chi2prob);
  }

  protected static double gammaLN(double input){
     double X, tmp, ser;
     double[] cof = new double[6];
     
     int j;
       cof[0] = 76.18009173;
      cof[1] = -86.50532033;
      cof[2] = 24.01409822;
      cof[3] = -1.231739516;
      cof[4] = 0.00120858003;
      cof[5] = -0.00000536382;

      X = input - 1.0;
      tmp = X + 5.5;
      tmp = tmp - ((X + 0.5) * Math.log(tmp));
      ser = 1.0;
      for (j = 0;j<=5;j++){
          X = X + 1.0;
          ser = ser + cof[j] / X;
      }
      return ( -tmp + Math.log(2.50662827465 * ser) );
  }

}
