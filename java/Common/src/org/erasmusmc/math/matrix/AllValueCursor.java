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

import org.erasmusmc.math.vector.VectorCursor;

public class AllValueCursor<R, C> {
  protected MatrixCursor<R, C> rowCursor;
  protected VectorCursor<C> columnCursor;
  
  public AllValueCursor(Matrix<R, C> matrix) {
    rowCursor = matrix.getRowCursor();
    
    if (rowCursor.isValid())
      columnCursor = rowCursor.get().getCursor();
  }
  
  public boolean isValid() {
    return rowCursor.isValid() && columnCursor.isValid();
  }
  
  public void next() {
    columnCursor.next();
    
    if (!columnCursor.isValid()) {
      rowCursor.next();
      
      if (rowCursor.isValid())
        columnCursor = rowCursor.get().getCursor();
    }
  }
  public R getRowDimension(){
    return rowCursor.dimension();
  }
  public C getColumnDimension(){
    return columnCursor.dimension();
  }
  public double get() {
    return columnCursor.get();
  }
  
  public void set(double value) {
    columnCursor.set(value);
  }
}
