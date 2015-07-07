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

public class RowVectorMatrix<V> extends VectorMatrix<V, Vector<V>, V> {
  public RowVectorMatrix(List<Vector<V>> rowVectors, Space<V> columnSpace) {
    super(rowVectors, columnSpace);
  }
  
  public void set(Vector<V> row, V column, double value) {
    row.set(column, value);
  }

  public double get(Vector<V> row, V column) {
    return row.get(column);
  }

  public Space<Vector<V>> getRowSpace() {
    return listSpace;
  }

  public Space<V> getColumnSpace() {
    return vectorSpace;
  }

  public Vector<V> getRow(Vector<V> row) {
    return row;
  }

  public MatrixCursor<Vector<V>, V> getRowCursor() {
    return new VectorMatrixVectorCursor();
  }

  public MatrixCursor<V, Vector<V>> getColumnCursor() {
    return new VectorMatrixOrthogonalCursor();
  }
}
