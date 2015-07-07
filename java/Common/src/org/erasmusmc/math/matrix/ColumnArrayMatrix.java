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
import org.erasmusmc.math.vector.ArrayVector;
import org.erasmusmc.math.vector.Vector;

public class ColumnArrayMatrix<R, C> extends ArrayMatrix<R, C> {
  public ColumnArrayMatrix(Space<R> rowSpace, Space<C> columnSpace) {
    super(rowSpace, columnSpace);
  }
  
  public ColumnArrayMatrix(Matrix<R, C> matrix) {
    super(matrix);
  }

  public void set(R row, C column, double value) {
    values[columnSpace.indexOfObject(column)][rowSpace.indexOfObject(row)] = value;
  }

  public double get(R row, C column) {
    int i=columnSpace.indexOfObject(column);
    int j = rowSpace.indexOfObject(row);
    return values[columnSpace.indexOfObject(column)][rowSpace.indexOfObject(row)];
  }
  
  public void setSpaces(Space<R> rowSpace, Space<C> columnSpace) {
    this.rowSpace = rowSpace;
    this.columnSpace = columnSpace;

    values = new double[columnSpace.getDimensions()][rowSpace.getDimensions()];
  }
  
  public Vector<R> getColumn(C column) {
    return new ArrayVector<R>(getRowSpace(), values[columnSpace.indexOfObject(column)]);
  }

  public MatrixCursor<R, C> getRowCursor() {
    return new ArrayMatrixFirstSpaceCursor<R, C>(rowSpace, columnSpace);
  }

  public MatrixCursor<C, R> getColumnCursor() {
    return new ArrayMatrixSecondSpaceCursor<R, C>(rowSpace, columnSpace);
  }

  
  public double getByIndex(Integer row, Integer column) {
    
    return values[row][column];
  }
}
