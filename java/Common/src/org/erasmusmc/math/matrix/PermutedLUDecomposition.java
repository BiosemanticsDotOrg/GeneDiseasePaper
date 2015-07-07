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

package org.erasmusmc.math.matrix;

import org.erasmusmc.math.space.Space;
import org.erasmusmc.math.vector.Vector;

/**
 * <p>Title: ACS Viewer</p>
 * <p>Description: A viewer to visualize Associative Concept Spaces</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC, Medical Informatics</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */
/** Determines a decomposition of a matrix A into a lower triangular matrix L 
 * and an upper triangular matrix U such that L*U is a permutation of A.
 * This decomposition can be used to solve systems of linear equations
 * and to determine the inverse of a matrix.  
 */
public class PermutedLUDecomposition<R, C> extends RowArrayMatrix<R, C> {
 
  
  protected Object[] indices;
  protected double d;

  public PermutedLUDecomposition(Matrix<R, C> matrix) throws Exception {
    super(matrix);

    // From Numerical Recipes, function ludcmp  
    Space<R> rowSpace = getRowSpace();
    int rows = rowSpace.getDimensions();
    int columns = getColumnSpace().getDimensions();
    
    double[] implicitScalings = new double[rows];
    indices = new Object[rows];
    d = 1;

    for (int i = 0; i < rows; i++) {
      double big = 0;

      for (int j = 0; j < columns; j++)
        big = Math.max(big, Math.abs(values[i][j]));

      if (big == 0)
        throw new Exception("Singular matrix in LUDecomposition");
      else
        implicitScalings[i] = 1d / big;
    }

    int imax = 0;

    for (int j = 0; j < columns; j++) {
      double sum;

      for (int i = 0; i < j; i++) {
        sum = values[i][j];

        for (int k = 0; k < i; k++)
          sum -= values[i][k] * values[k][j];

        values[i][j] = sum;
      }

      double maximum = 0;

      for (int i = j; i < rows; i++) {
        sum = values[i][j];

        for (int k = 0; k < j; k++)
          sum -= values[i][k] * values[k][j];

        values[i][j] = sum;

        double figureOfMerit = implicitScalings[i] * Math.abs(sum);

        if (figureOfMerit >= maximum) {
          maximum = figureOfMerit;
          imax = i;
        }
      }

      if (j != imax) {
        swapRows(j, imax);
        d = -d;
        implicitScalings[imax] = implicitScalings[j];
      }

      indices[j] = rowSpace.objectForIndex(imax);

      if (values[j][j] == 0d)
        throw new Exception("Singular matrix in LUDecomposition");

      if (j != rows - 1) {
        double factor = 1d / values[j][j];

        for (int i = j + 1; i < rows; i++)
          values[i][j] *= factor;
      }
    }
  }

  public Matrix<R, C> getL() {
    RowArrayMatrix<R, C> result = new RowArrayMatrix<R, C>(getRowSpace(), getColumnSpace());
    int rows = getRowSpace().getDimensions();
    
    for (int i = 1; i < rows; i++)
      for (int j = 0; j < i; j++)
        result.values[i][j] = values[i][j];

    result.setMainDiagonal(1);
    result.setAboveDiagonal(0);

    return result;
  }

  public Matrix<R, C> getU() {
    RowArrayMatrix<R, C> result = new RowArrayMatrix<R, C>(getRowSpace(), getColumnSpace());
    int rows = getRowSpace().getDimensions();
    int columns= getColumnSpace().getDimensions();
    
    for (int i = 0; i < rows; i++)
      for (int j = i; j < columns; j++)
        result.values[i][j] = values[i][j];

    result.setBelowDiagonal(0);

    return result;
  }

  public void substituteBack(Vector b) {
    // From Numerical Recipes, function lubksb
    double sum;
    int ii = -1;

    Space rowSpace = b.getSpace();
    int rows = rowSpace.getDimensions();
    
    for (int i = 0; i < rows; i++) {
      Object row = rowSpace.objectForIndex(i);
      Object ip = indices[i];
      sum = b.get(ip);
      b.set(ip, b.get(row));

      if (ii != -1)
        for (int j = ii; j <= i - 1; j++)
          sum -= values[i][j] * b.get(rowSpace.objectForIndex(j));
      else if (sum != 0d)
        ii = i;

      b.set(row, sum);
    }
    
    for (int i = rows - 1; i >= 0; i--) {
      Object row = rowSpace.objectForIndex(i);
      sum = b.get(row);

      for (int j = i + 1; j < rows; j++)
        sum -= values[i][j] * b.get(rowSpace.objectForIndex(j));

      b.set(row, sum / values[i][i]);
    }
  }

  public void substituteBack(Matrix matrix) {
   for (Object column: matrix.getColumnSpace())
      substituteBack(matrix.getColumn(column));
  }
  
  public int[] getPermutation() {
    int[] result = new int[indices.length];
    
    for (int i = 0; i < result.length; i++)
      result[i] = i;
    
    for (int i = 0; i < result.length; i++) {
      int row = rowSpace.indexOfObject((R) indices[i]);
      int swap = result[row];
      result[row] = result[i];
      result[i] = swap;
    }
          
    return result;
  }
}