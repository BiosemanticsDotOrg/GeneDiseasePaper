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

import org.erasmusmc.math.space.ListSpace;
import org.erasmusmc.math.space.Space;
import org.erasmusmc.math.vector.Vector;

public class DistanceMatrix extends Matrix {
  protected ListSpace positionSpace;
  protected List<Vector> positions;
  
  public DistanceMatrix(List<Vector> positions) {
    positionSpace = new ListSpace(positions);
    this.positions = positions;
  }
  
  public void set(Object row, Object column, double value) {
  }

  public double get(Object row, Object column) {
    return ((Vector) row).distanceTo((Vector) column);
  }

  public Space getRowSpace() {
    return positionSpace;
  }

  public Space getColumnSpace() {
    return positionSpace;
  }

  public void setSpaces(Space rowSpace, Space columnSpace) {
  }
  
  public MatrixCursor getRowCursor() {
    return new DistanceMatrixCursor();
  }

  public MatrixCursor getColumnCursor() {
    return new DistanceMatrixCursor();
  }

  protected class DistanceMatrixHandle implements MatrixHandle {
    protected int index = 0;
    
    public Object dimension() {
      return positions.get(index);
    }

    public int index() {
      return index;
    }

    public Vector get() {
      return positions.get(index);
    }
  }
  
  protected class DistanceMatrixCursor extends DistanceMatrixHandle implements MatrixCursor {
    public boolean isValid() {
      return index < positions.size();
    }

    public void next() {
      index++;
    }
  }
}
