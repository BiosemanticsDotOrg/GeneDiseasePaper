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

public class RowVector<R, C> extends Vector<C> {
  protected Matrix<R, C> matrix;
  protected R row;
  
  public RowVector(Matrix<R, C> matrix, R row) {
    this.matrix = matrix;
    this.row = row;
  }
  
  public void set(C index, double value) {
    matrix.set(row, index, value);
  }

  public double get(C index) {
    return matrix.get(row, index);
  }

  public Space<C> getSpace() {
    return matrix.getColumnSpace();
  }

  public void setSpace(Space<C> space) {
  }
  
  public VectorCursor<C> getCursor() {
    return new RowVectorCursor();
  }

  public VectorCursor<C> getNonzeroCursor() {
    return new NonzeroVectorCursor<C>(new RowVectorCursor());
  }
  
  public VectorSlaveCursor<C> getSlaveCursor() {
    return new RowVectorSlaveCursor();
  }

  public int getStoredValueCount() {
    return getSpace().getDimensions();
  }

  protected class RowVectorHandle implements VectorHandle<C> {
    protected C dimension;
    
    public C dimension() {
      return dimension;
    }

    public int index() {
      return getSpace().indexOfObject(dimension);
    }

    public double get() {
      return matrix.get(row, dimension);
    }

    public void set(double value) {
      matrix.set(row, dimension, value);
    }
  }
  
  protected class RowVectorSlaveCursor extends RowVectorHandle implements VectorSlaveCursor<C> {
    public void synchronize(VectorHandle<C> vectorHandle) {
      dimension = vectorHandle.dimension();
    }
  }
  
  protected class RowVectorCursor extends RowVectorHandle implements VectorCursor<C> {
    protected Iterator<C> iterator;
    
    public RowVectorCursor() {
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

  public void set(Vector vector) {
  }
}
