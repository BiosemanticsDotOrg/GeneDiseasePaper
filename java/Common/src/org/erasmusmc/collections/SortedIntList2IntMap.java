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

public class SortedIntList2IntMap implements Serializable {

  private static final long serialVersionUID = -285748620620480541L;
  private IntList keys;
  private IntList values;

  public SortedIntList2IntMap() {
    keys = new IntList();
    values = new IntList();
  }

  public SortedIntList2IntMap(int initialCapacity) {
    keys = new IntList(initialCapacity);
    values = new IntList(initialCapacity);
  }

  /**
   * special function for efficiency if you add an entry to end by bypassing
   * binarysearch
   */
  public void addEntry(int key, int value) {
    keys.add(key);
    values.add(value);
  }

  public int getIndexForKey(int key) {
    return binarySearch(key);
  }

  public Integer guidedGetIndexForKey(int key, int low, int high) {
    return binarySearch(key, low, high);
  }

  public void put(int key, int element) {
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
  
  /**
   * Adds the increment to the value in the map. If the key and value do not yet exist they are created,
   * and the value is set to the increment
   * @param key
   * @param increment
   */
  public void add(int key, int increment){
    int index = binarySearch(key);

    if (index < keys.size()) {

      if (key == keys.getInt(index))
        values.set(index, values.get(index) + increment);
      else {
        keys.add(index, key);
        values.add(index, increment);
      }
    }
    else {
      keys.add(index, key);
      values.add(index, increment);
    }
  	
  }

  public boolean containsKey(int key) {
    Integer v = get(key);
    if (v == null) {
      return false;
    }
    else {
      return true;
    }
  }

  public void putAll(SortedIntList2IntMap map) {
    Iterator<MapEntry> iterator = map.entryIterator();
    while (iterator.hasNext()) {
      MapEntry k = iterator.next();
      put(k.key, k.value);
    }
  }

  public Integer guidedGet(int key, int low, int high) {
    int index = binarySearch(key, low, high);

    if (index < keys.size()) {

      if (key == keys.getInt(index))
        return values.getInt(index);
    }

    return null;
  }

  public Integer get(int key) {
    return guidedGet(key, 0, keys.size());
  }

  public int getKey(int index) {
    return keys.getInt(index);
  }

  public int getValue(int index) {
    return values.getInt(index);
  }

  public Integer remove(int key) {
    int index = binarySearch(key);

    if (index < keys.size()) {
      if (keys.getInt(index) == key) {
        keys.remove(index);
        int value = values.getInt(index);
        values.remove(index);
        return value;
      }
    }
    return null;
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
    int value;

    public MapEntry(int key, int value) {
      this.key = key;
      this.value = value;
    }

    public int getKey() {
      return key;
    }

    public int getValue() {
      return value;
    }
  }

  public MapCursor<Integer, Integer> getEntryCursor() {
    return new EntryCursor();
  }

  protected class EntryCursor implements MapCursor<Integer, Integer> {
    private int index = 0;
    private boolean removeCheck;

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
        removeCheck=false;
      index++;
    }

    public void setValue(Integer value) {
      if (isValid())
        values.set(index, value);
    }

    public Integer value() {
      if (isValid())
        return values.getInt(index);
      else
        return null;
    }

    public Integer remove() {
      keys.remove(index);
      Integer returnValue =values.remove(index);
      index--;
      removeCheck=true;
      return returnValue;
    }
  }

  protected class EntryIterator implements Iterator<MapEntry> {
    private int index = 0;

    public boolean hasNext() {
      return index < keys.size();
    }

    public MapEntry next() {
      return new MapEntry(keys.getInt(index), values.getInt(index++));
    }

    public void remove() {
    }

  }

}
