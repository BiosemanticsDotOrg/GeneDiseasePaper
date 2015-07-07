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

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.math.space.Space;
import org.erasmusmc.math.vector.ArrayVector;
import org.erasmusmc.math.vector.Vector;

public class RowArrayMatrix<R, C> extends ArrayMatrix<R, C> {
  public RowArrayMatrix(Space<R> rowSpace, Space<C> columnSpace) {
    super(rowSpace, columnSpace);
  }

  public RowArrayMatrix(Matrix<R, C> matrix) {
    super(matrix);
  }

  public void set(R row, C column, double value) {
    values[rowSpace.indexOfObject(row)][columnSpace.indexOfObject(column)] = value;
  }

  public double get(R row, C column) {
    return values[rowSpace.indexOfObject(row)][columnSpace.indexOfObject(column)];
  }

  public void set(Matrix<R, C> matrix) {
    int rows = rowSpace.getDimensions();
    int columns = columnSpace.getDimensions();

    for (int i = 0; i < rows; i++) {
      double[] rowValues = values[i];
      Vector<C> rowVector = matrix.getRow(rowSpace.objectForIndex(i));

      for (int j = 0; j < columns; j++)
        rowValues[j] = rowVector.get(columnSpace.objectForIndex(j));
    }
  }

  public void setSpaces(Space<R> rowSpace, Space<C> columnSpace) {
    this.rowSpace = rowSpace;
    this.columnSpace = columnSpace;

    values = new double[rowSpace.getDimensions()][columnSpace.getDimensions()];
  }

  public Vector<C> getRow(R row) {
    return new ArrayVector<C>(getColumnSpace(), values[rowSpace.indexOfObject(row)]);
  }

  public List<Vector<C>> getRows() {
    List<Vector<C>> result = new ArrayList<Vector<C>>();
    ArrayMatrixSecondSpaceCursor arrayMatrixSecondSpaceCursor = new ArrayMatrixSecondSpaceCursor<C, R>(columnSpace, rowSpace);
    while (arrayMatrixSecondSpaceCursor.isValid()) {
      result.add(arrayMatrixSecondSpaceCursor.get());
      arrayMatrixSecondSpaceCursor.next();
    }
    return result;
  }

  public MatrixCursor<R, C> getRowCursor() {
    return new ArrayMatrixSecondSpaceCursor<C, R>(columnSpace, rowSpace);
  }

  public MatrixCursor<C, R> getColumnCursor() {
    return new ArrayMatrixFirstSpaceCursor<C, R>(columnSpace, rowSpace);
  }

  protected double sqr(double a) {
    return a * a;
  }

  protected double pythagoras(double a, double b) {
    double absa, absb;
    absa = Math.abs(a);
    absb = Math.abs(b);

    if (absa > absb)
      return absa * Math.sqrt(1.0 + sqr(absb / absa));
    else
      return absb == 0.0 ? 0.0 : absb * Math.sqrt(1.0 + sqr(absa / absb));
  }

  protected double transferSign(double a, double b) {
    return b >= 0 ? Math.abs(a) : -Math.abs(a);
  }

  public void tridiagonalSymmetric(ArrayVector d, ArrayVector e) {
    int i, j, k, l;
    double scale, hh, h, g, f;

    int rows = getRowSpace().getDimensions();

    for (i = rows - 1; i >= 1; i--) {
      l = i - 1;
      h = scale = 0.0;

      if (l > 0) {
        for (k = 0; k <= l; k++)
          scale += Math.abs(values[i][k]);

        if (scale == 0.0)
          e.values[i] = values[i][l];
        else {
          for (k = 0; k <= l; k++) {
            values[i][k] /= scale;
            h += sqr(values[i][k]);
          }

          f = values[i][l];
          g = f >= 0.0 ? -Math.sqrt(h) : Math.sqrt(h);
          e.values[i] = scale * g;
          h -= f * g;
          values[i][l] = f - g;
          f = 0.0;

          for (j = 0; j <= l; j++) {
            values[j][i] = values[i][j] / h;
            g = 0.0;

            for (k = 0; k <= j; k++)
              g += values[j][k] * values[i][k];

            for (k = j + 1; k <= l; k++)
              g += values[k][j] * values[i][k];

            e.values[j] = g / h;
            f += e.values[j] * values[i][j];
          }

          hh = f / (h + h);

          for (j = 0; j <= l; j++) {
            f = values[i][j];
            g = e.values[j] - hh * f;
            e.values[j] = g;

            for (k = 0; k <= j; k++)
              values[j][k] -= f * e.values[k] + g * values[i][k];
          }
        }
      }
      else
        e.values[i] = values[i][l];

      d.values[i] = h;
    }

    d.values[0] = 0.0;
    e.values[0] = 0.0;

    for (i = 0; i < rows; i++) {
      l = i - 1;

      if (d.values[i] != 0.0) {
        for (j = 0; j <= l; j++) {
          g = 0.0;

          for (k = 0; k <= l; k++)
            g += values[i][k] * values[k][j];

          for (k = 0; k <= l; k++)
            values[k][j] -= g * values[k][i];
        }
      }

      d.values[i] = values[i][i];
      values[i][i] = 1.0;

      for (j = 0; j <= l; j++) {
        values[j][i] = 0;
        values[i][j] = 0;
      }
    }
  }

  public void eigenvectorsQLdiagonal(ArrayVector d, ArrayVector e) {
    int m, l, iter, i, k;
    double s, r, p, g, f, dd, c, b;

    int rows = getRowSpace().getDimensions();

    for (i = 1; i < rows; i++)
      e.values[i - 1] = e.values[i];

    e.values[rows - 1] = 0.0;

    for (l = 0; l < rows; l++) {
      iter = 0;

      do {
        for (m = l; m < rows - 1; m++) {
          dd = Math.abs(d.values[m]) + Math.abs(d.values[m + 1]);
          if ((double) (Math.abs(e.values[m]) + dd) == dd)
            break;
        }

        if (m != l) {
          // if (iter++ == 30)
          iter++;
          g = (d.values[l + 1] - d.values[l]) / (2.0 * e.values[l]);
          r = pythagoras(g, 1.0);
          g = d.values[m] - d.values[l] + e.values[l] / (g + transferSign(r, g));
          s = c = 1.0;
          p = 0.0;

          for (i = m - 1; i >= l; i--) {
            f = s * e.values[i];
            b = c * e.values[i];
            e.values[i + 1] = r = pythagoras(f, g);

            if (r == 0.0) {
              d.values[i + 1] -= p;
              e.values[m] = 0.0;
              break;
            }

            s = f / r;
            c = g / r;
            g = d.values[i + 1] - p;
            r = (d.values[i] - g) * s + 2.0 * c * b;
            d.values[i + 1] = g + (p = s * r);
            g = c * r - b;

            for (k = 0; k < rows; k++) {
              f = values[k][i + 1];
              values[k][i + 1] = s * values[k][i] + c * f;
              values[k][i] = c * values[k][i] - s * f;
            }
          }

          if (r == 0.0 && i >= l)
            continue;
          d.values[l] -= p;
          e.values[l] = g;
          e.values[m] = 0.0;
        }
      } while (m != l);
    }
  }

  public void eigenSort(ArrayVector eigenValues) {
    int i, j, k;
    double p;

    int rows = getRowSpace().getDimensions();

    for (i = 0; i < rows - 1; i++) {
      p = eigenValues.values[k = i];

      for (j = i + 1; j < rows; j++)
        if (eigenValues.values[j] >= p)
          p = eigenValues.values[k = j];

      if (k != i) {
        eigenValues.values[k] = eigenValues.values[i];

        for (j = 0; j < rows; j++) {
          p = values[j][i];
          values[j][i] = values[j][k];
          values[j][k] = p;
        }
      }
    }
  }

  public RowArrayMatrix eigenVectors(ArrayVector eigenValues) {
    RowArrayMatrix result = new RowArrayMatrix(this);

    eigenValues.setSpace(getRowSpace());
    ArrayVector offDiagonal = new ArrayVector(getRowSpace());

    result.tridiagonalSymmetric(eigenValues, offDiagonal);
    result.eigenvectorsQLdiagonal(eigenValues, offDiagonal);
    result.eigenSort(eigenValues);

    return result;
  }

  public void setAboveDiagonal(double value) {
    int rows = getRowSpace().getDimensions();
    int columns = getColumnSpace().getDimensions();

    for (int i = 0; i < rows - 1; i++)
      for (int j = i + 1; j < columns; j++)
        values[i][j] = value;
  }

  public void setBelowDiagonal(double value) {
    int rows = getRowSpace().getDimensions();

    for (int i = 1; i < rows; i++)
      for (int j = 0; j < i; j++)
        values[i][j] = value;
  }

  public void swapRows(int row1, int row2) {
    double swap;
    int columns = getColumnSpace().getDimensions();

    for (int i = 0; i < columns; i++) {
      swap = values[row1][i];
      values[row1][i] = values[row2][i];
      values[row2][i] = swap;
    }
  }
}
