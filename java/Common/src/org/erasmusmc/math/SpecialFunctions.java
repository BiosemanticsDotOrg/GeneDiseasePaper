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

public class SpecialFunctions {
  public static double BinomialCoefficient(int n, int k) {

    double value = -1;
    if (n < k) {
      return 0d;
    }
    else {
      try {
        value = Math.floor(0.5 + Math.exp(lnFactorial(n) - lnFactorial(k) - lnFactorial(n - k)));
      } catch (Exception e) {

        e.printStackTrace();
      }
      ;
      return value;
    }
  }

  public static double LNofBinomialCoefficient(int n, int k) {

    double value = -1;
    if (n < k) {
      return 0d;
    }
    else {
      try {
        value = (lnFactorial(n) - lnFactorial(k) - lnFactorial(n - k));
      } catch (Exception e) {

        e.printStackTrace();
      }
      ;
      return value;
    }
  }

  public static double lnFactorial(int n) throws Exception {
    // returns ln(n!);
    double[] values = { 0, 0, 0.693147181, 1.791759469, 3.17805383, 4.787491743, 6.579251212, 8.525161361, 10.6046029, 12.80182748, 15.10441257, 17.50230785, 19.9872145 };

    if (n < 0) {
      throw new Exception("Negative factorial in routine lnFactorial");
    }
    if (n <= 1)
      return 0d;
    if (n <= 12) {
      return values[n];
    }
    else {
      return lnOfGammaFunction((double) n + 1d);
    }
  }

  public static double lnOfGammaFunction(double xx) {
    // courtesy to numerical recipes
    double[] cof = { 76.18009172947146, -86.50532032941677, 24.01409824083091, -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5 };
    double y = xx;
    double x = xx;
    double temp = x + 5.5;
    temp -= (x + 0.5) * Math.log(temp);
    double ser = 1.000000000190015;
    for (int j = 0; j <= 5; j++) {
      ser += cof[j] / ++y;
    }
    return -temp + Math.log(2.5066282746310005 * ser / x);
  }

  // Gamma function
  // Lanczos approximation (6 terms)
  public static double gammaFunction(double x) {
    // Lanczos Gamma Function approximation - small gamma
    double lgfGamma = 5.0;
    // Lanczos Gamma Function approximation - Coefficients
    double[] lgfCoeff = { 1.000000000190015, 76.18009172947146, -86.50532032941677, 24.01409824083091, -1.231739572450155, 0.1208650973866179E-2, -0.5395239384953E-5 };
    //  Lanczos Gamma Function approximation - N (number of coefficients -1)
    int lgfN = 6;
    
    double xcopy = x;
    double first = x + lgfGamma + 0.5;
    double second = lgfCoeff[0];
    double fg = 0.0D;

    if (x >= 0.0) {
      if (x >= 1.0D && x - (int) x == 0.0D) {
        try {
          fg = Math.exp(lnFactorial((int)x)) / x;
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      else {
        first = Math.pow(first, x + 0.5) * Math.exp(-first);
        for (int i = 1; i <= lgfN; i++)
          second += lgfCoeff[i] / ++xcopy;
        fg = first * Math.sqrt(2.0 * Math.PI) * second / x;
      }
    }
    else {
      fg = -Math.PI / (x * gammaFunction(-x) * Math.sin(Math.PI * x));
    }
    return fg;
  }

  public static double regularizedIncompleteBetaFunction(double alpha, double beta, double x) throws Exception {
    if (x < 0d || x > 1d)
      throw new Exception("X in IncompleteBetaFunction is out of Range!");
    double valofBetafunction;
    if (x == 0d || x == 1d) {
      valofBetafunction = 0d;
    }
    else {
      double first = lnOfGammaFunction(alpha + beta) - lnOfGammaFunction(alpha) - lnOfGammaFunction(beta);
      double second = alpha * Math.log(x) + beta * Math.log(1 - x);
      valofBetafunction = Math.exp(first + second);
    }
    if (x < (alpha + 1.0) / (alpha + beta + 2.0)) {
      return valofBetafunction * betaContinuedFracion(alpha, beta, x) / alpha;
    }
    else
      return 1 - valofBetafunction * betaContinuedFracion(beta, alpha, 1 - x) / beta;
  }

  private static double betaContinuedFracion(double alpha, double beta, double x) throws Exception {
    int maxit = 500;
    double eps = 3E-7;
    double fpmin = 1E-30;
    double qab = alpha + beta;
    double qap = alpha + 1;
    double qam = alpha - 1;
    double c = 1.0;
    double d = 1.0 - (qab * x / qap);
    if (Math.abs(d) < fpmin)
      d = fpmin;
    d = 1d / d;
    double h = d;
    int m = 1;
    double del = 10;

    while (m < maxit && !(Math.abs(del - 1) < eps)) {
      int m2 = 2 * m;
      double aa = m * (beta - m) * x / ((qam + m2) * (alpha + m2));
      d = 1 + aa * d;
      if (Math.abs(d) < fpmin)
        d = fpmin;
      c = 1 + aa / c;
      if (Math.abs(c) < fpmin)
        c = fpmin;
      d = 1d / d;
      h *= d * c;
      aa = -(alpha + m) * (qab + m) * x / ((alpha + m2) * (qap + m2));
      d = 1 + aa * d;
      if (Math.abs(d) < fpmin)
        d = fpmin;
      c = 1 + aa / c;
      if (Math.abs(c) < fpmin)
        c = fpmin;
      d = 1d / d;
      del = d * c;
      h *= del;
      m++;
    }
    if (m == maxit) {
      throw new Exception("alpha or beta too big, or MAXIT too small in betaContinuedFracion");
    }
    return h;
  }
}
