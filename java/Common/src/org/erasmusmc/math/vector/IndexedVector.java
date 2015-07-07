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

public abstract class IndexedVector<D> extends Vector<D> {
  public IndexedVector() {
    super();
  }
  
  public IndexedVector(Vector<D> vector) {
    super(vector);
  }
  
  public abstract double getByIndex(int index);
  public abstract void setByIndex(int index, double value);
  
  public void set(D object, double value) {
    setByIndex(getSpace().indexOfObject(object), value);
  }
  
  public void set(Vector<D> vector) {
    VectorCursor<D> cursor = vector.getNonzeroCursor();
    int index = 0;
    
    while (cursor.isValid()) {
      int cursorIndex = cursor.index();
      
      while (index < cursorIndex) {
        setByIndex(index, 0);
        index++;
      }
      
      setByIndex(cursorIndex, cursor.get());
      cursor.next();
      index++;
    }
  }
  
  public double get(D object) {
    return getByIndex(getSpace().indexOfObject(object));
  }
  
  public VectorCursor<D> getCursor() {
    return new IndexedVectorCursor();
  }
  
  public VectorCursor<D> getNonzeroCursor() {
    return new IndexedVectorNonzeroCursor();
  }

  public VectorSlaveCursor<D> getSlaveCursor() {
    return new IndexedVectorSlaveCursor();
  }

  public int getStoredValueCount() {
    return getSpace().getDimensions();
  }

  protected class IndexedVectorHandle implements VectorHandle<D> {
    protected int index = 0;
    
    public D dimension() {
      return getSpace().objectForIndex(index);
    }

    public int index() {
      return index;
    }

    public double get() {
      return getByIndex(index);
    }

    public void set(double value) {
      setByIndex(index, value);
    }
  }
  
  protected class IndexedVectorCursor extends IndexedVectorHandle implements VectorCursor<D> {
    public boolean isValid() {
      return index < getSpace().getDimensions();
    }

    public void next() {
      index++;
    }
  }
  
  protected class IndexedVectorNonzeroCursor extends IndexedVectorHandle implements VectorCursor<D> {
    public boolean isValid() {
      return index < getSpace().getDimensions();
    }

    public void next() {
      do 
        index++;
      while (index < getSpace().getDimensions() && getByIndex(index) == 0);
    }
  }
  
  protected class IndexedVectorSlaveCursor extends IndexedVectorHandle implements VectorSlaveCursor<D> {
    public void synchronize(VectorHandle<D> vectorHandle) {
      index = vectorHandle.index();
    }
  }
}

