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

public class TrieNode implements Serializable {
  private static final long serialVersionUID = 717802594709731944L;
  private char[] chars = new char[0];
  private Object[] objects = new Object[0];
  public Object value;
  
  public void insert(int index, char key, Object object) {
    insertChar(index, key);
    insertObject(index, object);
  }
  
  public void setObject(int index, Object object){
    objects[index] = object;
  }

  public int indexOf(char key) {
    int index = binarySearch(key, 0, chars.length);
    if (index < chars.length) {
      if (key == chars[index])
        return index;
    }
    return -1;
  }

  private int binarySearch(char key, int low, int high) {
    int middle;
    while (low < high) {
      middle = (low + high) / 2;
      if (key > chars[middle])
        low = middle + 1;
      else
        high = middle;
    }
    return low;
  }
 
  public int binarySearch(char key) {
    int low = 0;
    int high = chars.length;
    return binarySearch(key, low, high);
  }

  public int size() {
    return chars.length;
  }
  
  public char getKeyForIndex(int index) {
    return chars[index];
  }
  
  public Object getObjectForIndex(int index) {
    return objects[index];
  }
  
  private void insertChar(int index, char element) {
    char[] newChars;
    if (chars == null){
      newChars = new char[1];
    } else {
      newChars = new char[chars.length+1];
      System.arraycopy(chars, 0, newChars, 0, index);
      System.arraycopy(chars, index, newChars, index + 1, chars.length - index);
    }
    newChars[index] = element;
    chars = newChars;
  }
  
  private void insertObject(int index, Object element) {
    Object[] newObjects;
    if (objects == null){
      newObjects = new Object[1];
    } else {
      newObjects = new Object[objects.length+1];
      System.arraycopy(objects, 0, newObjects, 0, index);
      System.arraycopy(objects, index, newObjects, index + 1, objects.length - index);
    }
    newObjects[index] = element;
    objects = newObjects;
  }
  
}