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

import java.util.Iterator;
import java.util.List;

import org.erasmusmc.math.space.ListSpace;
import org.erasmusmc.math.space.Space;
import org.erasmusmc.math.vector.ArrayVector;
import org.erasmusmc.math.vector.DeepListVector;
import org.erasmusmc.math.vector.Vector;

public abstract class VectorMatrix<V, R, C> extends Matrix<R, C> {
  protected Space<Vector<V>> listSpace;
  protected Space<V> vectorSpace;
  protected List<Vector<V>> vectors;
   
  public VectorMatrix(List<Vector<V>> vectors, Space<V> vectorSpace) {
    this.vectors = vectors;
    this.vectorSpace = vectorSpace;
    this.listSpace = new ListSpace<Vector<V>>(vectors);
  }
  
  public void setSpaces(Space<R> rowSpace, Space<C> columnSpace) {
  }

  public RowArrayMatrix<Vector<V>, Vector<V>> covarianceCoordinates() {
    RowArrayMatrix<Vector<V>, Vector<V>> result = new RowArrayMatrix<Vector<V>, Vector<V>>(listSpace, listSpace);
    result.zeroes();

    ArrayVector<V> mean = new ArrayVector<V>(vectorSpace);
    Vector.meanVector(mean, vectors);

    int rows = listSpace.getDimensions();
    int columns = vectorSpace.getDimensions();

    for (V column: vectorSpace) {
      for (int i = 0; i < rows; i++)
        for (int j = i; j < rows; j++)
          result.values[i][j] += (vectors.get(i).get(column) - mean.values[i]) * (vectors.get(j).get(column) - mean.values[j]);
    }

    for (int i = 0; i < rows; i++)
      for (int j = i; j < rows; j++) {
        result.values[i][j] /= (double) columns;
        result.values[j][i] = result.values[i][j];
      }

    return result;
  }
  
  protected class VectorMatrixVectorHandle implements MatrixHandle<Vector<V>, V> {
    protected int index = 0;
    
    public Vector<V> dimension() {
      return listSpace.objectForIndex(index);
    }

    public int index() {
      return index;
    }

    public Vector<V> get() {
      return vectors.get(index);
    }
  }
  
  protected class VectorMatrixVectorCursor extends VectorMatrixVectorHandle implements MatrixCursor<Vector<V>, V> {
    public boolean isValid() {
      return index < listSpace.getDimensions();
    }

    public void next() {
      index++;
    }
  }
  
  protected class VectorMatrixOrthogonalHandle implements MatrixHandle<V, Vector<V>> {
    protected V dimension;
    
    public V dimension() {
      return dimension;
    }

    public int index() {
      return vectorSpace.indexOfObject(dimension);
    }

    public Vector<Vector<V>> get() {
      return new DeepListVector<V>(listSpace, vectors, dimension);
    }
  }
  
  protected class VectorMatrixOrthogonalCursor extends VectorMatrixOrthogonalHandle implements MatrixCursor<V, Vector<V>> {
    protected Iterator<V> iterator;
    
    public VectorMatrixOrthogonalCursor() {
      iterator = vectorSpace.iterator();
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
}
