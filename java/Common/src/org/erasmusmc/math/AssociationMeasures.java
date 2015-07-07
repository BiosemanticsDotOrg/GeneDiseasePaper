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


public class AssociationMeasures {
  
  public static double cramersV(int[][] contingencyTableValues, int numberOfFieldsVar1, int numberOfFieldsVar2) {
    int[] sumFieldsVar1 = new int[numberOfFieldsVar1];
    int[] sumFieldsVar2 = new int[numberOfFieldsVar2];
    
    int numberOfNonZeroFieldsVar1 = numberOfFieldsVar1;
    int numberOfNonZeroFieldsVar2 = numberOfFieldsVar2;
    int totalSum = 0;
    for (int i = 0; i < numberOfFieldsVar1; i++) {
      sumFieldsVar1[i] = 0;
      for (int j = 0; j < numberOfFieldsVar2; j++) {
        sumFieldsVar1[i] += contingencyTableValues[i][j];
        totalSum += contingencyTableValues[i][j];
      }
      if (sumFieldsVar1[i] == 0) {
        numberOfNonZeroFieldsVar1--;
      }
    }
    for (int j = 0; j < numberOfFieldsVar2; j++) {
      sumFieldsVar2[j] = 0;
      for (int i = 0; i < numberOfFieldsVar1; i++) {
        sumFieldsVar2[j] += contingencyTableValues[i][j];
      }
      if (sumFieldsVar2[j] == 0) {
        numberOfNonZeroFieldsVar2--;
      }
    }
    // int degreesOfFreedom = numberOfNonZeroFieldsVar1 *
    // numberOfNonZeroFieldsVar2 - numberOfNonZeroFieldsVar1 -
    // numberOfNonZeroFieldsVar2 +1;
    double chiSquareCoefficient = 0d;
    for (int i = 0; i < numberOfFieldsVar1; i++) {
      for (int j = 0; j < numberOfFieldsVar2; j++) {
        double expected = (double) sumFieldsVar2[j] * (double) sumFieldsVar1[i] / (double) totalSum;
        double temp = (double) contingencyTableValues[i][j] - expected;
        chiSquareCoefficient += temp * temp / (expected + 1.0e-30);

      }
    }
    // double probability =
    // ChiSquaredProbabilityFunction.chiSquaredProbabilityFunction(chiSquareCoefficient,degreesOfFreedom);
    int minimumOfVar1OrVar2Fields = numberOfNonZeroFieldsVar1;
    if (numberOfNonZeroFieldsVar2 < numberOfNonZeroFieldsVar1) {
      minimumOfVar1OrVar2Fields = numberOfNonZeroFieldsVar2;
    }
    minimumOfVar1OrVar2Fields--;
    double result = Math.sqrt(chiSquareCoefficient / ((double) totalSum * (double) minimumOfVar1OrVar2Fields));
    if (result>1d&&result<1.000001){
      result = 1d;
    }
    return result;
  }

  public static double logLikelihoodRatio(int A, int B, int C, int D) {
    // entries A, B , C and D represent the cells of a contingency table A =
    // cell 0,0 B = cell 0,1 etc
    // rows first and rows represent free variable, e.g. the occurrence of a
    // term, column the expermental condition.
    double result = 0;
    if (D != 0) {
      double chanceInSelection = (double) A / (double) (C + A);
      double chanceNotInSelection = (double) B / (double) (D + B);
      double chanceOverall = (double) (A + B) / (double) (A + B + C + D);
      result = A * Math.log(chanceOverall) + C * Math.log(1 - chanceOverall);
      result += B * Math.log(chanceOverall) + D * Math.log(1 - chanceOverall);
      if (C > 0) {
        result -= A * Math.log(chanceInSelection) + C * Math.log(1 - chanceInSelection);
      }
      if (B > 0) {
        result -= B * Math.log(chanceNotInSelection) + D * Math.log(1 - chanceNotInSelection);
      }
    }
    return result;
  }

  public static double symmetricUncertaintyCoefficient(int A, int B, int C, int D) {
    double tiny = 1e-30;
    int rowTotal1 = A + B;
    int rowTotal2 = C + D;
    int columnTotal1 = A + C;
    int columnTotal2 = B + D;
    int total = A + B + C + D;

    double entropyVar1 = -nlogn((double) rowTotal1 / (double) total) - nlogn((double) rowTotal2 / (double) total);
    double entropyVar2 = -nlogn((double) columnTotal1 / (double) total) - nlogn((double) columnTotal2 / (double) total);
    double totalEntropy = -nlogn((double) A / (double) total) - nlogn((double) B / (double) total) - nlogn((double) C / (double) total) - nlogn((double) D / (double) total);
    // double entropyVar1GivenVar2 = totalEntropy-entropyVar2;
    // double entropyVar2GivenVar1 = totalEntropy-entropyVar1;
    // double uncertaintyCoefficientVar1GivenVar2 =
    // (entropyVar1-entropyVar1GivenVar2)/(entropyVar1+tiny);
    // double uncertaintyCoefficientVar2GivenVar1 =
    // (entropyVar2-entropyVar2GivenVar1)/(entropyVar2+tiny);
    double uncertaintyCoefficientVar1Var2 = 2d * (entropyVar1 + entropyVar2 - totalEntropy) / (entropyVar1 + entropyVar2 + tiny);
    return uncertaintyCoefficientVar1Var2;
  }

  public static double asymmetricUncertaintyCoefficient(int A, int B, int C, int D) {
    double tiny = 1e-30;
    int rowTotal1 = A + B;
    int rowTotal2 = C + D;
    int columnTotal1 = A + C;
    int columnTotal2 = B + D;
    int total = A + B + C + D;

    double entropyVar1 = -nlogn((double) rowTotal1 / (double) total) - nlogn((double) rowTotal2 / (double) total);
    double entropyVar2 = -nlogn((double) columnTotal1 / (double) total) - nlogn((double) columnTotal2 / (double) total);
    double totalEntropy = -nlogn((double) A / (double) total) - nlogn((double) B / (double) total) - nlogn((double) C / (double) total) - nlogn((double) D / (double) total);
    double entropyVar1GivenVar2 = totalEntropy - entropyVar2;
    double uncertaintyCoefficientVar1GivenVar2 = (entropyVar1 - entropyVar1GivenVar2) / (entropyVar1 + tiny);
    return uncertaintyCoefficientVar1GivenVar2;
  }

  public static double nlogn(double n) {
    if (n != 0) {
      return n * Math.log(n);
    }
    else {
      return 0;
    }
  }
  /**
   * Based on http://math.hws.edu/javamath/ryan/ChiSquare.html
   * @param a
   * @param b
   * @param c
   * @param d
   * @return
   */
  public static double chiSquare(int a, int b, int c, int d){
    double da = a;
    double db = b;
    double dc = c;
    double dd = d;
    double denominator = (da+db)*(dc+dd)*(db+dd)*(da+dc);
    double x = (da*dd-db*dc);
    double numerator = x*x*(da+db+dc+dd); 
    return numerator/denominator;
  }
  
  public static double chiSquareToP(double chiSquare){
    return ChiSquaredProbabilityFunction.chiSquaredProbabilityFunction(chiSquare, 1);
  }
 }
