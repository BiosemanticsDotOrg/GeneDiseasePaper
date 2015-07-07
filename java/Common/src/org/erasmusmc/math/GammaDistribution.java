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

import java.util.Random;

public class GammaDistribution implements ProbablityDistribution, RealRootFunction {

  private double locationParameter = 0.0D;
  private double scaleParameter = 0.0D;
  private double shapeParameter = 0.0D;
  public Random rr = new Random();
  private double cfd = rr.nextDouble();

  // tolerance used in terminating series in Incomplete Gamma function;
  private static double incompleteGammaFunctionSeriesTerminationCutoff = 1e-8;
  // Maximum number of iterations allowed in Incomplete Gamma Function
  // calculations
  private static int incompleteGammaFunctionIterationFrequency = 1000;
  // A small number close to the smallest representable floating point number
  public static final double FPMIN = 1e-300;

  public GammaDistribution(double locationParameter, double scaleParameter, double shapeParameter) {

    if (scaleParameter <= 0.0D)
      throw new IllegalArgumentException("The scale parameter, " + scaleParameter + "must be greater than zero");
    if (shapeParameter <= 0.0D)
      throw new IllegalArgumentException("The shape parameter, " + shapeParameter + "must be greater than zero");

    this.locationParameter = locationParameter;
    this.scaleParameter = scaleParameter;
    this.shapeParameter = shapeParameter;
  }

  /**
   * default locationParameter = 0;
   */
  public GammaDistribution(double scaleParameter, double shapeParameter) {
    this.scaleParameter = scaleParameter;
    this.shapeParameter = shapeParameter;
  }

  public double function(double x) {
    return cfd - gammaCDF(x);
  }

  // Gamma distribution - three parameter
  // cumulative distribution function
  public double gammaCDF(double upperLimit) {
    if (upperLimit < locationParameter)
      throw new IllegalArgumentException("The upper limit, " + upperLimit + "must be equal to or greater than the location parameter, " + locationParameter);
    double xx = (upperLimit - locationParameter) / scaleParameter;
    return regularisedGammaFunction(shapeParameter, xx);
  }

  // Regularised Incomplete Gamma Function P(a,x) = integral from zero to x of
  // (exp(-t)t^(a-1))dt
  public static double regularisedGammaFunction(double a, double x) {
    if (a < 0.0D || x < 0.0D)
      throw new IllegalArgumentException("\nFunction defined only for a >= 0 and x>=0");
    double igf = 0.0D;

    if (x < a + 1.0D) {
      // Series representation
      igf = incompleteGammaSer(a, x);
    }
    else {
      // Continued fraction representation
      igf = incompleteGammaFract(a, x);
    }
    return igf;
  }

  public double gammaPDF(double x) {
    double xx = (x - locationParameter) / scaleParameter;
    return Math.pow(xx, shapeParameter - 1) * Math.exp(-xx) / (scaleParameter * SpecialFunctions.gammaFunction(shapeParameter));
  }

  public double mean() {
    return shapeParameter * scaleParameter - locationParameter;
  }

  public double mode() {
    double mode = Double.NaN;
    if (shapeParameter >= 1.0D)
      mode = (shapeParameter - 1.0D) * scaleParameter - locationParameter;
    return mode;
  }

  public double standDev() {
    return Math.sqrt(shapeParameter) * scaleParameter;
  }

  public double nextDouble() {
    if (scaleParameter <= 0.0D)
      throw new IllegalArgumentException("The scale parameter, " + scaleParameter + "must be greater than zero");
    if (shapeParameter <= 0.0D)
      throw new IllegalArgumentException("The shape parameter, " + shapeParameter + "must be greater than zero");

    // Set initial range for search
    double range = Math.sqrt(shapeParameter) * scaleParameter;

    // required tolerance
    double tolerance = 1e-10;

    double lowerBound = locationParameter;
    // upper bound
    double upperBound = locationParameter + 5.0D * range;
    if (upperBound <= lowerBound)
      upperBound += 5.0D * range;

    // set functioncfd variables
    cfd = rr.nextDouble();

    // Create instance of RealRoot
    RealRoot realR = new RealRoot();

    // Set extension limits
    realR.noLowerBoundExtension();

    // Set tolerance
    realR.setTolerance(tolerance);

    // call root searching method, bisectNewtonRaphson
    return realR.falsePosition(this, lowerBound, upperBound);
  }

  // generate an array of Gamma random deviates
  public double[] getArray(int n) {
    double[] ran = new double[n];
    for (int i = 0; i < n; i++) {
      ran[i] = nextDouble();
    }
    return ran;
  }

  public double pValueBiggerThanX(double x) {
    return gammaCDF(x);
  }

  public double pValueSmallerThanX(double x) {
    return 1-gammaCDF(x);
  }

  // Regularised Incomplete Gamma Function P(a,x) = integral from zero to x of
  // (exp(-t)t^(a-1))dt
  // Series representation of the function - valid for x < a + 1
  public static double incompleteGammaSer(double a, double x) {
    if (a < 0.0D || x < 0.0D)
      throw new IllegalArgumentException("\nFunction defined only for a >= 0 and x>=0");
    if (x >= a + 1)
      throw new IllegalArgumentException("\nx >= a+1   use Continued Fraction Representation");

    int i = 0;
    double igf = 0.0D;
    boolean check = true;

    double acopy = a;
    double sum = 1.0 / a;
    double incr = sum;
    double loggamma = SpecialFunctions.lnOfGammaFunction(a);

    while (check) {
      ++i;
      ++a;
      incr *= x / a;
      sum += incr;
      if (Math.abs(incr) < Math.abs(sum) * incompleteGammaFunctionSeriesTerminationCutoff) {
        igf = sum * Math.exp(-x + acopy * Math.log(x) - loggamma);
        check = false;
      }
      if (i >= incompleteGammaFunctionIterationFrequency) {
        check = false;
        igf = sum * Math.exp(-x + acopy * Math.log(x) - loggamma);
        System.out.println("\nMaximum number of iterations were exceeded in Stat.incompleteGammaSer().\nCurrent value returned.\nIncrement = " + String.valueOf(incr) + ".\nSum = " + String.valueOf(sum) + ".\nTolerance =  " + String.valueOf(incompleteGammaFunctionSeriesTerminationCutoff));
      }
    }
    return igf;
  }

  // Regularised Incomplete Gamma Function P(a,x) = integral from zero to x of
  // (exp(-t)t^(a-1))dt
  // Continued Fraction representation of the function - valid for x >= a + 1
  // This method follows the general procedure used in Numerical Recipes for C,
  // The Art of Scientific Computing
  // by W H Press, S A Teukolsky, W T Vetterling & B P Flannery
  // Cambridge University Press, http://www.nr.com/
  public static double incompleteGammaFract(double a, double x) {
    if (a < 0.0D || x < 0.0D)
      throw new IllegalArgumentException("\nFunction defined only for a >= 0 and x>=0");
    if (x < a + 1)
      throw new IllegalArgumentException("\nx < a+1   Use Series Representation");

    int i = 0;
    double ii = 0;
    double igf = 0.0D;
    boolean check = true;

    double loggamma = SpecialFunctions.lnOfGammaFunction(a);
    double numer = 0.0D;
    double incr = 0.0D;
    double denom = x - a + 1.0D;
    double first = 1.0D / denom;
    double term = 1.0D / FPMIN;
    double prod = first;

    while (check) {
      ++i;
      ii = (double) i;
      numer = -ii * (ii - a);
      denom += 2.0D;
      first = numer * first + denom;
      if (Math.abs(first) < FPMIN) {
        first = FPMIN;
      }
      term = denom + numer / term;
      if (Math.abs(term) < FPMIN) {
        term = FPMIN;
      }
      first = 1.0D / first;
      incr = first * term;
      prod *= incr;
      if (Math.abs(incr - 1.0D) < incompleteGammaFunctionSeriesTerminationCutoff)
        check = false;
      if (i >= incompleteGammaFunctionIterationFrequency) {
        check = false;
        System.out.println("\nMaximum number of iterations were exceeded in Stat.incompleteGammaFract().\nCurrent value returned.\nIncrement - 1 = " + String.valueOf(incr - 1) + ".\nTolerance =  " + String.valueOf(incompleteGammaFunctionSeriesTerminationCutoff));
      }
    }
    igf = 1.0D - Math.exp(-x + a * Math.log(x) - loggamma) * prod;
    return igf;
  }

}
