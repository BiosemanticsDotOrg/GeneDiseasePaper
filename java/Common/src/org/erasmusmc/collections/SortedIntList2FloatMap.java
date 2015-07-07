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

import java.io.Serializable;
import java.util.Iterator;

public class SortedIntList2FloatMap implements Serializable {
  private static final long serialVersionUID = -2857486206204805801L;
  private IntList keys;
  private FloatList values;

  // make supersleak cursor! for high speed reading
  public SortedIntList2FloatMap() {
    keys = new IntList();
    values = new FloatList();
  }

  public SortedIntList2FloatMap(int initialCapacity) {
    keys = new IntList(initialCapacity);
    values = new FloatList(initialCapacity);
  }

  public SortedIntListSet getKeySet() {
    return new SortedIntListSet(keys);
  }
  
  public IntList keys(){
  	return keys;
  }
  
  public FloatList values(){
    return values;
  }
  /**
   * special function for efficiency if you add an entry to end by bypassing
   * binarysearch
   */
  public void addEntry(int key, float value) {
    keys.add(key);
    values.add(value);
  }

  public int getIndexForKey(int key) {
    return binarySearch(key);
  }

  public Integer guidedGetIndexForKey(int key, int low, int high) {
    return binarySearch(key, low, high);
  }

  public void put(int key, float element) {
    int index = binarySearch(key);

    if (index < keys.size()) {

      if (key == keys.getInt(index))
        values.set(index, element);
      else {
        keys.add(index, key);
        values.add(index, element);
      }
    }
    else {
      keys.add(index, key);
      values.add(index, element);
    }

  }

  public boolean containsKey(int key) {
    float v = get(key);
    if (Float.isNaN(v)) {
      return false;
    }
    else {
      return true;
    }
  }

  public void putAll(SortedIntList2FloatMap map) {
    Iterator<Integer> iterator = map.keyIterator();
    while (iterator.hasNext()) {
      int k = iterator.next();
      put(k, map.get(k));
    }
  }

  public float guidedGet(int key, int low, int high) {
    int index = binarySearch(key, low, high);

    if (index < keys.size()) {

      if (key == keys.getInt(index))
        return values.get(index);
    }

    return Float.NaN;
  }

  public float get(int key) {
    return guidedGet(key, 0, keys.size());
  }

  public int getKey(int index) {
    return keys.getInt(index);
  }

  public float getValue(int index) {
    return values.get(index);
  }

  public float remove(int key) {
    int index = binarySearch(key);

    if (index < keys.size()) {
      if (keys.getInt(index) == key) {
        keys.remove(index);
        float value = values.get(index);
        values.remove(index);
        return value;
      }
    }
    return Float.NaN;
  }

  protected int binarySearch(int key, int low, int high) {
    int middle;

    while (low < high) {
      middle = (low + high) / 2;

      if (key > keys.getInt(middle))
        low = middle + 1;
      else
        high = middle;
    }

    return low;
  }

  protected int binarySearch(int key) {
    int low = 0, high = keys.size();
    return binarySearch(key, low, high);
  }

  public void pack() {
    keys.trimToSize();
    values.trimToSize();
  }

  public int size() {
    return keys.size();
  }

  public void clear() {
    keys.clear();
    values.clear();
  }

  public Iterator<Integer> keyIterator() {
    return new KeyIterator();
  }

  public Iterator<MapEntry> entryIterator() {
    return new EntryIterator();
  }

  private class KeyIterator implements Iterator<Integer> {
    private int currentIndex = 0;

    public boolean hasNext() {
      return currentIndex < keys.size();
    }

    public Integer next() {
      return keys.getInt(currentIndex++);
    }

    public void remove() {
    }

  };

  public class MapEntry {
    int key;
    float value;

    public MapEntry(int key, float value) {
      this.key = key;
      this.value = value;
    }

    public int getKey() {
      return key;
    }

    public float getValue() {
      return value;
    }
  }

  public MapCursor<Integer, Float> getEntryCursor() {
    return new EntryCursor();
  }

  protected class EntryCursor implements MapCursor<Integer, Float> {
    private int index = 0;
    private boolean removeCheck = false;

    public boolean isValid() {
      return index < keys.size() && !removeCheck;
    }

    public Integer key() {
      if (isValid())
        return keys.getInt(index);
      else
        return null;
    }

    public void next() {
      if (removeCheck)
        removeCheck = false;
      index++;
    }

    public void setValue(Float value) {
      if (isValid())
        values.set(index, value);
    }

    public Float value() {
      if (isValid())
        return values.get(index);
      else
        return null;
    }

    public Float remove() {
      if (isValid()) {
        Float value = values.get(index);
        values.remove(index);
        keys.remove(index);
        removeCheck = true;
        index--;
        return value;
      }
      else {
        return null;
      }
    }
  }

  protected class EntryIterator implements Iterator<MapEntry> {
    private int index = 0;

    public boolean hasNext() {
      return index < keys.size();
    }

    public MapEntry next() {
      return new MapEntry(keys.getInt(index), values.get(index++));
    }

    public void remove() {
      int removeindex = index - 1;
      if (removeindex > 0) {
        keys.remove(removeindex);
        values.remove(removeindex);
        index = removeindex;
      }

    }

  }

}
