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

package org.erasmusmc.collections;

import java.util.Collection;
import java.util.Iterator;

public class FloatList {
  private float[] array;
  private int defaultCapacity = 8;
  private int size = 0;

  public FloatList() {
    array = new float[defaultCapacity];
  }

  public FloatList(int initialCapacity) {
    array = new float[initialCapacity];
  }

  public boolean add(float i) {
    if (size >= array.length)
      grow();
    array[size] = i;
    size++;
    return true;
  }

  public void add(int index, float element) {
    if (index < size) {
      if (size + 1 >= array.length)
        grow();

      System.arraycopy(array, index, array, index + 1, size - index);
      array[index] = element;
      size++;
    }
    else if (index == size) {
      add(element);
    }
    else {
      throw new IndexOutOfBoundsException();
    }

  }

  public float set(int index, float element) {
    if (index < size) {
      float current = array[index];
      array[index] = element;
      return current;
    }
    else {
      throw new IndexOutOfBoundsException();
    }
  }

  public boolean addAll(Collection<Integer> collection) {
    if (size + collection.size() > array.length)
      setCapacity(size + collection.size());
    Iterator<Integer> iterator = collection.iterator();
    while (iterator.hasNext()) {
      array[size] = iterator.next();
      size++;
    }
    return true;
  }

  public boolean remove(int index) throws ArrayIndexOutOfBoundsException {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException("list[" + index + "] is out of bounds (max " + (size - 1) + ")");
    System.arraycopy(array, index + 1, array, index, size - index - 1);
    size--;
    return true;
  }

  public float[] toArray() {
    return array;
  }

  public void clear() {
    size = 0;
  }

  public float get(int index) throws ArrayIndexOutOfBoundsException {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException("list[" + index + "] is out of bounds (max " + (size - 1) + ")");
    return array[index];
  }

  public int size() {
    return size;
  }

  public void trimToSize() {
    setCapacity(size);
  }

  private void grow() {
    int delta;
    if (array.length > 64)
      delta = array.length / 4;
    else
      delta = 16;
    setCapacity(array.length + delta);
  }

  private void setCapacity(int newCapacity) {
    float[] newArray = new float[newCapacity];
    System.arraycopy(array, 0, newArray, 0, size);
    array = newArray;
  }

}
