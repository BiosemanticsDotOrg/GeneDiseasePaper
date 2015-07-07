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
import org.erasmusmc.math.vector.DeepArrayVector;
import org.erasmusmc.math.vector.Vector;

public abstract class ArrayMatrix<R, C> extends Matrix<R, C> {
  public double[][] values;
  protected Space<R> rowSpace;
  protected Space<C> columnSpace;

  public ArrayMatrix(Space<R> rowSpace, Space<C> columnSpace) {
    super(rowSpace, columnSpace);
  }
  
  public ArrayMatrix(Matrix<R, C> matrix) {
    super(matrix);
  }
  
  public Space<R> getRowSpace() {
    return rowSpace;
  }

  public Space<C> getColumnSpace() {
    return columnSpace;
  }
  
  public void constants(double value) {
    for (int i = 0; i < values.length; i++) {
      double[] valueAxis = values[i];
      
      for (int j = 0; j < valueAxis.length; j++)
        valueAxis[j] = value;
    }
  }
  
  protected class ArrayMatrixFirstSpaceHandle<S1, S2> implements MatrixHandle<S1, S2> {
    protected int index = 0;
    protected Space<S1> firstSpace;
    protected Space<S2> secondSpace;
    
    public ArrayMatrixFirstSpaceHandle(Space<S1> firstSpace, Space<S2> secondSpace) {
      this.firstSpace = firstSpace;
      this.secondSpace = secondSpace;
    }
    
    public S1 dimension() {
      return firstSpace.objectForIndex(index);
    }

    public int index() {
      return index;
    }

    public Vector<S2> get() {
      return new DeepArrayVector<S2>(secondSpace, values, index);
    }  
  }
  
  protected class ArrayMatrixFirstSpaceCursor<S1, S2> extends ArrayMatrixFirstSpaceHandle<S1, S2> implements MatrixCursor<S1, S2> {
    public ArrayMatrixFirstSpaceCursor(Space<S1> firstSpace, Space<S2> secondSpace) {
      super(firstSpace, secondSpace);
    }

    public boolean isValid() {
      return index < firstSpace.getDimensions();
    }

    public void next() {
      index++;
    }
  }
  
  protected class ArrayMatrixSecondSpaceHandle<S1, S2> implements MatrixHandle<S2, S1> {
    protected int index = 0;
    protected Space<S1> firstSpace;
    protected Space<S2> secondSpace;
    
    public ArrayMatrixSecondSpaceHandle(Space<S1> firstSpace, Space<S2> secondSpace) {
      this.firstSpace = firstSpace;
      this.secondSpace = secondSpace;
    }
    
    public S2 dimension() {
      return secondSpace.objectForIndex(index);
    }

    public int index() {
      return index;
    }

    public Vector<S1> get() {
      return new ArrayVector<S1>(firstSpace, values[index]);
    }
  }
  
  protected class ArrayMatrixSecondSpaceCursor<S1, S2> extends ArrayMatrixSecondSpaceHandle<S1, S2> implements MatrixCursor<S2, S1> {
    public ArrayMatrixSecondSpaceCursor(Space<S1> firstSpace, Space<S2> secondSpace) {
      super(firstSpace, secondSpace);
    }

    public boolean isValid() {
      return index < values.length;
    }

    public void next() {
      index++;
    }
  }
}
