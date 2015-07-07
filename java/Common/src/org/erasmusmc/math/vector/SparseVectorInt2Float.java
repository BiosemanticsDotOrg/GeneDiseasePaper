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
import java.util.Iterator;

import org.erasmusmc.collections.MapCursor;
import org.erasmusmc.collections.SortedIntList2FloatMap;
import org.erasmusmc.collections.SortedIntList2FloatMap.MapEntry;
import org.erasmusmc.math.space.IntegerSpace;
import org.erasmusmc.math.space.Space;

public class SparseVectorInt2Float extends Vector<Integer> implements Serializable {
  private static final long serialVersionUID = 2830857831592496295L;
  transient public SortedIntList2FloatMap values;

  public SparseVectorInt2Float() {
    values = new SortedIntList2FloatMap();
  }

  public SparseVectorInt2Float(SortedIntList2FloatMap map) {
    values = map;
  }

  public SparseVectorInt2Float(Vector<Integer> vector) {
    values = new SortedIntList2FloatMap();
    set(vector);
  }
 

  public SparseVectorInt2Float sparseElementWiseSparseInnerProduct(SparseVectorInt2Float other) {

    SortedIntList2FloatMap shorter = other.values;
    SortedIntList2FloatMap longer = values;
    SortedIntList2FloatMap resultmap = new SortedIntList2FloatMap(shorter.size() + longer.size());
    if (shorter.size() != 0 && longer.size() != 0){
      if (shorter.size() > longer.size()) {
        shorter = longer;
        longer = other.values;
      }
      int longerlowestIndex = 0;
      int longerhighestIndex = longer.size() - 1;
      int longerlowest = longer.getKey(longerlowestIndex);
      int shorterlowestIndex = 0;
      int shorterhighestIndex = shorter.size() - 1;
      while (shorterlowestIndex <= shorterhighestIndex) {
        int key = shorter.getKey(shorterlowestIndex);
        if (key >= longerlowest) {
          int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
          if (index < longer.size()) {
            longerlowestIndex = index;
            if (longer.getKey(index) == key) {
              float product = longer.getValue(index) * shorter.getValue(shorterlowestIndex);
              resultmap.addEntry(key, product);
              if (longerlowestIndex < longerhighestIndex - 1)
                longerlowestIndex++;
            }
            longerlowest = longer.getKey(longerlowestIndex);
          }
        }
        shorterlowestIndex++;
      }
    }
    return new SparseVectorInt2Float(resultmap);
  }
  
  /**
   * Calculates cityblock metric (sum of absolute differences between components of the vectors)
   * @param other
   * @return	Cityblock
   */
  public double cityBlock(SparseVectorInt2Float other) {
    Iterator<MapEntry> iterator1 = values.entryIterator();
    Iterator<MapEntry> iterator2 = other.values.entryIterator();
    double score = 0;
    MapEntry buffer1 = null;
    MapEntry buffer2 = null;
    if (iterator1.hasNext())
    	buffer1 = iterator1.next();
    if (iterator2.hasNext())
    	buffer2 = iterator2.next();
    
    while (buffer1 != null && buffer2 != null){
    	if (buffer1.getKey() == buffer2.getKey()){
    		score += Math.abs(buffer1.getValue() - buffer2.getValue());
    	  if (iterator1.hasNext())
      	  buffer1 = iterator1.next();
        else
      	  buffer1 = null;
      
        if (iterator2.hasNext())
      	  buffer2 = iterator2.next();
        else
      	  buffer2 = null;    
    	} else if (buffer1.getKey() > buffer2.getKey()) {
    		score += Math.abs(buffer2.getValue());
        if (iterator2.hasNext())
      	  buffer2 = iterator2.next();
        else
      	  buffer2 = null; 
    	} else {
    		score += Math.abs(buffer1.getValue());
    	  if (iterator1.hasNext())
      	  buffer1 = iterator1.next();
        else
      	  buffer1 = null;
    	}
    }
    if (buffer1 != null){
    	score += Math.abs(buffer1.getValue());
    	while (iterator1.hasNext())
    		score += Math.abs(iterator1.next().getValue());
    }
    if (buffer2 != null){
    	score += Math.abs(buffer2.getValue());
    	while (iterator2.hasNext())
    		score += Math.abs(iterator2.next().getValue());
    }
    return score;    
  }

  public double sparseInnerProduct(SparseVectorInt2Float other) {
    SortedIntList2FloatMap shorter = other.values;
    SortedIntList2FloatMap longer = values;
    if (shorter.size() > longer.size()) {
      shorter = longer;
      longer = other.values;
    }
    double innerproduct = 0f;
    if (shorter.size() > 0) {
      int longerlowestIndex = 0;
      int longerhighestIndex = longer.size() - 1;
      int longerlowest = longer.getKey(longerlowestIndex);
      int longerhighest = longer.getKey(longerhighestIndex);
      int shorterlowestIndex = 0;
      int shorterhighestIndex = shorter.size() - 1;
      while (shorterlowestIndex <= shorterhighestIndex) {
        int key = shorter.getKey(shorterlowestIndex);
        if (key >= longerlowest) {
          int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
          if (index < longer.size()) {
            longerlowestIndex = index;
            if (longer.getKey(index) == key) {
              innerproduct += longer.getValue(index) * shorter.getValue(shorterlowestIndex);
              if (longerlowestIndex < longerhighestIndex - 1)
                longerlowestIndex++;
            }
            longerlowest = longer.getKey(longerlowestIndex);
          }
        }
        shorterlowestIndex++;
        if (shorterlowestIndex < shorterhighestIndex) {
          key = shorter.getKey(shorterhighestIndex);
          if (key <= longerhighest) {
            int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
            if (index < longer.size()) {
              longerhighestIndex = index;
              if (longer.getKey(index) == key) {
                innerproduct += longer.getValue(index) * shorter.getValue(shorterhighestIndex);
                if (longerlowestIndex < longerhighestIndex - 1)
                  longerhighestIndex--;
              }
              longerhighest = longer.getKey(longerhighestIndex);
            }
          }
          shorterhighestIndex--;
        }
      }
    }
    return innerproduct;
  }

  /**
   * superfancy high performance cosine function. Whips Any ass up to now.
   * 
   * @param other
   * @return
   */
  public double sparseCosine(SparseVectorInt2Float other) {
    double result = 0;
    double denominator = norm() * other.norm();
    if (denominator > 0) {
      double innerproduct = sparseInnerProduct(other);
      result = innerproduct / denominator;
    }
    return result;

  }

  public double jaccard(SparseVectorInt2Float other) {
    double result = 0;
    double innerproduct = sparseInnerProduct(other);
    double denominator = getSquaredNorm() + other.getSquaredNorm() - innerproduct;
    if (denominator > 0) {
      result = innerproduct / denominator;
    }
    return result;
  }

  public double dice(SparseVectorInt2Float other) {
    double result = 0;
    double innerproduct = sparseInnerProduct(other);
    double denominator = getSquaredNorm() + other.getSquaredNorm();
    if (denominator > 0) {
      result = 2d * innerproduct / denominator;
    }
    return result;
  }

  @Override
  public double get(Integer object) {
    float value = values.get(object.intValue());

    if (!Float.isNaN(value))
      return new Float(value).doubleValue();
    else
      return 0f;
  }

  @Override
  public Space<Integer> getSpace() {
    return new IntegerSpace();
  }

  @Override
  public int getStoredValueCount() {
    return values.size();
  }

  @Override
  public void set(Integer index, double value) {
    if (value != 0d)
      values.put(index, new Double(value).floatValue());
    else
      values.remove(index);
  }

  public void setFloat(Integer index, float value) {
    if (value != 0d)
      values.put(index, value);
    else
      values.remove(index);
  }

  public float getFloat(Integer index) {
    float value = values.get(index);

    if (!Float.isNaN(value))
      return value;
    else
      return 0;

  }

  @Override
  public void set(Vector<Integer> vector) {
    values.clear();
    VectorCursor<Integer> cursor = vector.getNonzeroCursor();

    while (cursor.isValid()) {
      set(cursor.dimension(), cursor.get());
      cursor.next();
    }
  }

  @Override
  public void setSpace(Space<Integer> space) {
  }

  public VectorCursor<Integer> getCursor() {
    return new SparseVectorNonzeroCursor();
  }

  public VectorCursor<Integer> getNonzeroCursor() {
    return new SparseVectorNonzeroCursor();
  }

  public VectorSlaveCursor<Integer> getSlaveCursor() {
    return new SparseVectorSlaveCursor();
  }

  public Iterator<MapEntry> entryIterator() {
    return values.entryIterator();
  }

  protected class SparseVectorHandle implements VectorHandle<Integer> {
    Integer dimension;

    public Integer dimension() {
      return dimension;
    }

    public int index() {
      return dimension;
    }

    public double get() {
      float value = values.get(dimension);

      if (!Float.isNaN(value))
        return value;
      else
        return 0;
    }

    public void set(double value) {
      if (value != 0d)
        values.put(dimension, new Double(value).floatValue());
      else
        values.remove(dimension);
    }
  }

  protected class SparseVectorSlaveCursor extends SparseVectorHandle implements VectorSlaveCursor<Integer> {
    public void synchronize(VectorHandle<Integer> vectorHandle) {
      dimension = vectorHandle.dimension();
    }
  }

  protected class SparseVectorNonzeroCursor extends SparseVectorHandle implements VectorCursor<Integer>, Serializable {
    private static final long serialVersionUID = 2287253547250643918L;
    MapCursor<Integer, Float> cursor;

    public SparseVectorNonzeroCursor() {
      cursor = values.getEntryCursor();
    }

    public boolean isValid() {
      return cursor.isValid();
    }

    public void next() {
      cursor.next();
    }

    @Override
    public Integer dimension() {
      return cursor.key();
    }

    @Override
    public double get() {
      return cursor.value();
    }

    @Override
    public int index() {
      return values.getIndexForKey(dimension());
    }

    @Override
    public void set(double value) {
      if (value != 0d)
        cursor.setValue(new Double(value).floatValue());
      else
        cursor.remove();
      
    }
  }
}
