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

public class Kappa {
  public int[][] ratingMatrix;
  public double kappa;
  public double variance;
  public int numberOfObjects;
  public int numberOfCategories;
  public int numberOfClassifiers;
  /**
   * zScore only valid with larger N (about >20)
   */
  public double zScore;

  /**
   * This implementation expects a rating Matrix, where the rows (first index)
   * represent the objects to be classified and the columns (second index) the
   * classes. For every row the number of columns should be equal. The sum of
   * the row values should be equal to the number of classifiers k for every
   * row. If you do not know what the beforementioned means: RAFM on the kappa
   * statistic, thank you. Cheers Rob.
   */
  public Kappa(int[][] ratingMatrix) {

    numberOfCategories = ratingMatrix[0].length;
    int total = 0;

    int summedSquaredCellValues = 0;
    numberOfClassifiers = 0;
    numberOfObjects = ratingMatrix.length;
    int[] categories = new int[numberOfCategories];
    for (int i = 0; i < ratingMatrix.length; i++) {
      for (int j = 0; j < numberOfCategories; j++) {
        int cellValue = ratingMatrix[i][j];
        categories[j] += cellValue;
        summedSquaredCellValues += cellValue * cellValue;
      }
    }

    for (int j = 0; j < numberOfCategories; j++) {
      total += categories[j];
      numberOfClassifiers += ratingMatrix[0][j];
    }
    double expectedAgreement = 0d;
    double pj3 = 0d;
    for (int j = 0; j < numberOfCategories; j++) {
      Double chanceForCategoryJ = ((double) categories[j]) / ((double) total);
      expectedAgreement += chanceForCategoryJ * chanceForCategoryJ;
      pj3 += Math.pow(chanceForCategoryJ, 3d);
    }

    Double proportionOfAgreement = -1d / (double) (numberOfClassifiers - 1) + (double) summedSquaredCellValues / (double) (total * (numberOfClassifiers - 1));
    if (total == numberOfClassifiers * ratingMatrix.length) {
      kappa = (proportionOfAgreement - expectedAgreement) / (1d - expectedAgreement);
      variance = 2d / (double) (numberOfObjects * numberOfClassifiers * (numberOfClassifiers - 1));
      variance *= expectedAgreement - (2d * numberOfClassifiers - 3d) * expectedAgreement * expectedAgreement + 2 * (numberOfClassifiers - 2) * pj3;
      variance /= (1d - expectedAgreement) * (1d - expectedAgreement);
      zScore = kappa / Math.sqrt(variance);
    }
    else {
      kappa = -1d;
    }
  }
}