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

public class HilbertMatrix extends Matrix {
  protected Space rowSpace, columnSpace;
  
  public HilbertMatrix(Space rowSpace, Space columnSpace) {
    setSpaces(rowSpace, columnSpace);
  }
  
  public void set(Object row, Object column, double value) {
  }

  public double get(Object row, Object column) {
    return 1.0d / (rowSpace.indexOfObject(row) + columnSpace.indexOfObject(column) + 1.0d);
  }

  public Space getRowSpace() {
    return rowSpace;
  }

  public Space getColumnSpace() {
    return columnSpace;
  }

  public void setSpaces(Space rowSpace, Space columnSpace) {
    this.rowSpace = rowSpace;
    this.columnSpace = columnSpace;
  }

  public MatrixCursor getRowCursor() {
    return null;
  }

  public MatrixCursor getColumnCursor() {
    return null;
  }
}
