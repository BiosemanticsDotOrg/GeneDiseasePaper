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

import java.util.List;

import org.erasmusmc.math.space.Space;
import org.erasmusmc.math.vector.Vector;

public class ColumnVectorMatrix<V> extends VectorMatrix<V, V, Vector<V>> {
  public ColumnVectorMatrix(List<Vector<V>> columnVectors, Space<V> rowSpace) {
    super(columnVectors, rowSpace);
  }
  
  public void set(V row, Vector<V> column, double value) {
    column.set(row, value);
  }

  public double get(V row, Vector<V> column) {
    return column.get(row);
  }

  public Space<V> getRowSpace() {
    return vectorSpace;
  }

  public Space<Vector<V>> getColumnSpace() {
    return listSpace;
  }

  public Vector<V> getColumn(Vector<V> column) {
    return column;
  }

  public MatrixCursor<V, Vector<V>> getRowCursor() {
    return new VectorMatrixOrthogonalCursor();
  }

  public MatrixCursor<Vector<V>, V> getColumnCursor() {
    return new VectorMatrixVectorCursor();
  }
}
