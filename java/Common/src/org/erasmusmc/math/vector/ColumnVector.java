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

package org.erasmusmc.math.vector;

import java.util.Iterator;

import org.erasmusmc.math.matrix.Matrix;
import org.erasmusmc.math.space.Space;

public class ColumnVector<R, C> extends Vector<R> {
  protected Matrix<R, C> matrix;
  protected C column;
  
  public ColumnVector(Matrix<R, C> matrix, C column) {
    this.matrix = matrix;
    this.column = column;
  }
  
  public void set(R index, double value) {
    matrix.set(index, column, value);
  }

  public double get(R index) {
    return matrix.get(index, column);
  }

  public Space<R> getSpace() {
    return matrix.getRowSpace();
  }

  public void setSpace(Space<R> space) {
  }
  
  public VectorCursor<R> getCursor() {
    return new ColumnVectorCursor();
  }

  public VectorCursor<R> getNonzeroCursor() {
    return new NonzeroVectorCursor<R>(new ColumnVectorCursor());
  }
  
  public VectorSlaveCursor<R> getSlaveCursor() {
    return new ColumnVectorSlaveCursor();
  }

  public int getStoredValueCount() {
    return getSpace().getDimensions();
  }

  protected class ColumnVectorHandle implements VectorHandle<R> {
    protected R dimension;
    
    public R dimension() {
      return dimension;
    }

    public int index() {
      return getSpace().indexOfObject(dimension);
    }

    public double get() {
      return matrix.get(dimension, column);
    }

    public void set(double value) {
      matrix.set(dimension, column, value);
    }
  }
  
  protected class ColumnVectorSlaveCursor extends ColumnVectorHandle implements VectorSlaveCursor<R> {
    public void synchronize(VectorHandle<R> vectorHandle) {
      dimension = vectorHandle.dimension();
    }
  }
  
  protected class ColumnVectorCursor extends ColumnVectorHandle implements VectorCursor<R> {
    protected Iterator<R> iterator;
    
    public ColumnVectorCursor() {
      iterator = getSpace().iterator();
      next();
    }
    
    public boolean isValid() {
      return dimension != null;
    }

    public void next() {
      if (iterator.hasNext())
        dimension = iterator.next();
      else
        dimension = null;
    }
  }

  public void set(Vector<R> vector) {
  }
}
