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

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

import org.erasmusmc.collections.SortedListMap;
import org.erasmusmc.math.space.Space;

public class SparseVector<D> extends Vector<D> implements Serializable {
  private static final long serialVersionUID = 7973211925353447858L;
  public Space<D> space;
  public SortedListMap<D, Double> values;
  
  public SparseVector(Space<D> space) {
    this.space = space;    
    values = new SortedListMap<D, Double>(new SparseVectorKeyComparator());
  }
  
  public SparseVector(){
    values = new SortedListMap<D, Double>(new SparseVectorKeyComparator());
  }

  public SparseVector(Vector<D> vector) {
    space = vector.getSpace();
    values = new SortedListMap<D, Double>(new SparseVectorKeyComparator());
    set(vector);
  }
  
  
  public void set(D index, double value) {
    if (value != 0d)
      values.put(index, value);
    else
      values.remove(index);
  }

  public double get(D index) {
    Double value = values.get(index);
    
    if (value != null)
      return value;
    else
      return 0;
  }
  
  public void set(Vector<D> vector) {
    values.clear();
    VectorCursor<D> cursor = vector.getNonzeroCursor();
    
    while (cursor.isValid()) {
      set(cursor.dimension(), cursor.get());
      cursor.next();
    }
  }
    
  public Space<D> getSpace() {
    return space;
  }

  public void setSpace(Space<D> space) {
    this.space = space;
  }
  
  public String toString() {
    return "Sparse vector";
  }
  
  public VectorCursor<D> getCursor() {
    return new SparseVectorCursor();
  }
  
  public VectorCursor<D> getNonzeroCursor() {
    return new SparseVectorNonzeroCursor();
  }

  public VectorSlaveCursor<D> getSlaveCursor() {
    return new SparseVectorSlaveCursor();
  }
  
  public int getStoredValueCount() {
    return values.size();
  }

  protected class SparseVectorHandle implements VectorHandle<D> {
    D dimension;
    
    public D dimension() {
      return dimension;
    }

    public int index() {
      return space.indexOfObject(dimension);
    }

    public double get() {
      Double value = values.get(dimension);
      
      if (value != null)
        return value;
      else
        return 0;
    }

    public void set(double value) {
      if (value != 0d)
        values.put(dimension, value);
      else
        values.remove(dimension);
    }
  }
  
  protected class SparseVectorSlaveCursor extends SparseVectorHandle implements VectorSlaveCursor<D> {
    public void synchronize(VectorHandle<D> vectorHandle) {
      dimension = vectorHandle.dimension();
    }
  }
  
  protected class SparseVectorCursor extends SparseVectorHandle implements VectorCursor<D> {
    protected Iterator<D> iterator;
    
    public SparseVectorCursor() {
      iterator = space.iterator();
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

  protected class SparseVectorNonzeroCursor extends SparseVectorHandle implements VectorCursor<D>, Serializable {
    private static final long serialVersionUID = 2287253547250643918L;
    protected Iterator<D> iterator;
    
    public SparseVectorNonzeroCursor() {
      iterator = values.keyIterator();
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
  protected class SparseVectorKeyComparator implements Serializable, Comparator<D> {
    private static final long serialVersionUID = 1481833931150717597L;

    public int compare(D o1, D o2) {
      return space.indexOfObject(o1) - space.indexOfObject(o2);
    }
    
  }
}
