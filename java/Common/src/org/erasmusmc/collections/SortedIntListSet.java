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
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class SortedIntListSet implements Set<Integer>, Serializable {
  private static final long serialVersionUID = 7178025949709731944L;
  protected IntList entries;

  public SortedIntListSet() {
    entries = new IntList();
  }

  public SortedIntListSet(int initialCapacity) {
    entries = new IntList(initialCapacity);
  }

  public SortedIntListSet(IntList setEntries) {
    // if you use this constructor: know what you are doing!
    // the list is expected to be correcly sorted with only unique entries
    this.entries = setEntries;
  }

  public String toString() {
    StringBuffer result = new StringBuffer("[");
    Iterator<Integer> iter = iterator();
    if (iter.hasNext()) {
      result.append(iter.next().toString());
    }
    while (iter.hasNext()) {
      result.append(",");
      result.append(iter.next().toString());
    }
    result.append("]");
    return result.toString();
  }

  public boolean add(Integer key) {
    int index = binarySearch(key);

    if (index < entries.size()) {
      int k = entries.getInt(index);

      if (key.intValue() == k) {
        return false;

      }
      else
        entries.add(index, key);
    }
    else
      entries.add(index, key);

    return true;
  }

  public boolean guidedContains(int key, int low, int high) {
    int index = binarySearch(key, low, high);

    if (index < entries.size()) {

      if (key == entries.getInt(index))
        return true;
    }
    return false;
  }
  
  public int indexOf(int key) {
    int index = binarySearch(key, 0, entries.size());

    if (index < entries.size()) {

      if (key == entries.getInt(index))
        return index;
    }
    return -1;
  }

  public boolean contains(int key) {
    return guidedContains(key, 0, entries.size());
  }

  protected int binarySearch(int key, int low, int high) {
    int middle;

    while (low < high) {
      middle = (low + high) / 2;

      if (key > entries.getInt(middle))
        low = middle + 1;
      else
        high = middle;
    }

    return low;
  }

  protected int binarySearch(int key) {
    int low = 0, high = entries.size();
    return binarySearch(key, low, high);
  }

  public boolean addAll(Collection<? extends Integer> c) {
    Iterator<? extends Integer> iterator = c.iterator();
    boolean check = false;
    while (iterator.hasNext()) {
      if (add(iterator.next())) {
        check = true;
      }
    }
    return check;
  }

  public void clear() {
    entries.clear();
  }

  public boolean contains(Object o) {
    if (o instanceof Integer) {
      Integer id = (Integer) o;
      return contains(id.intValue());
    }
    return false;
  }

  public boolean containsAll(Collection<?> c) {
    Iterator<?> iterator = c.iterator();
    while (iterator.hasNext()) {
      Object o = iterator.next();
      if (o instanceof Integer) {
        Integer id = (Integer) o;
        if (!contains(id.intValue())) {
          return false;
        }
      }
      else
        return false;
    }
    return true;
  }

  /**
   * This is a special function if you know FOR SURE that you have a properly
   * sorted intlist with only unique entries, and you would like to set it as
   * the list underlying the set. Note that the old list is lost.
   */
  public void setSortedList(IntList list) {
    entries = list;
  }

  /** This returns the precious sorted list: Don't fuck it up! (please)
   *  You can do all that you want now that it is a copy..... GO YOUR GANG !
   * */
  public IntList getSortedList() {
    return new IntList(entries);
  }

  public boolean isEmpty() {
    if (entries.size() == 0)
      return true;
    else
      return false;
  }

  public Iterator<Integer> iterator() {

    return entries.iterator();
  }

  public boolean remove(Object o) {
    if (o instanceof Integer) {
      Integer key = (Integer) o;
      int index = binarySearch(key);
      if (index < entries.size()) {
        if (key == entries.getInt(index)) {
          entries.remove(index);
          return true;
        }
      }
    }
    return false;
  }

  public boolean removeAll(Collection<?> c) {
    Iterator<?> it = c.iterator();
    boolean check = false;
    while (it.hasNext()) {
      if (remove(it.next())) {
        check = true;
      }
    }
    return check;
  }

  public boolean retainAll(Collection<?> c) {

    Iterator<Integer> it = iterator();
    boolean check = false;
    while (it.hasNext()) {
      Integer current = it.next();
      if (!c.contains(current)) {
        check = true;
        it.remove();
      }
    }
    return check;
  }

  public int size() {
    return entries.size();
  }

  public Object[] toArray() {
    // TODO Auto-generated method stub
    return null;
  }

  public <T> T[] toArray(T[] a) {
    // TODO Auto-generated method stub
    return null;
  }
public SortedIntListSet getSubstraction (SortedIntListSet set2){
  //hurryhurry, not perfectly optimized
  
  IntList resultList = new IntList(size());
  SortedIntListSet intersect = getIntersection(set2);
  for (int i=0;i<entries.size();i++){
    int key = entries.getInt(i);
    if (!intersect.contains(key)){
      resultList.add(key);
    }
  }
  SortedIntListSet result = new SortedIntListSet(resultList);
  return result;
}
  public SortedIntListSet getIntersection(SortedIntListSet set2) {
    SortedIntListSet shorter = set2;
    SortedIntListSet longer = this;
    if (shorter.size() > longer.size()) {
      shorter = longer;
      longer = set2;
    }
    IntList result = new IntList(shorter.size());
    IntList top = new IntList(shorter.size() / 2);
    int longerlowestIndex = 0;
    int longerhighestIndex = longer.size() - 1;
    int longerlowest = longer.getKeyForIndex(longerlowestIndex);
    int longerhighest = longer.getKeyForIndex(longerhighestIndex);
    int shorterlowestIndex = 0;
    int shorterhighestIndex = shorter.size() - 1;
    while (shorterlowestIndex <= shorterhighestIndex) {
      int key = shorter.getKeyForIndex(shorterlowestIndex);
      if (key >= longerlowest) {
        int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
        if (index < longer.size()) {
          longerlowestIndex = index;
          if (longer.getKeyForIndex(index) == key) {
            result.add(key);
          }
          longerlowest = longer.getKeyForIndex(longerlowestIndex);
        }
      }
      shorterlowestIndex++;
      if (shorterlowestIndex < shorterhighestIndex) {
        key = shorter.getKeyForIndex(shorterhighestIndex);
        if (key <= longerhighest) {
          int index = longer.guidedGetIndexForKey(key, longerlowestIndex, longerhighestIndex + 1);
          if (index < longer.size()) {
            longerhighestIndex = index;
            if (longer.getKeyForIndex(index) == key) {
              top.add(key);
            }
            longerhighest = longer.getKeyForIndex(longerhighestIndex);
          }
        }
        shorterhighestIndex--;
      }
    }
    for (int i = top.size() - 1; i >= 0; i--)
      result.add(top.getInt(i));
    result.trimToSize();
    return new SortedIntListSet(result);
  }

  private int guidedGetIndexForKey(int key, int low, int high) {
    return binarySearch(key, low, high);
  }

  public int getKeyForIndex(int index) {
    return entries.getInt(index);
  }
  
  public SortedIntListSet overlap(SortedIntListSet other){
    IntList result = new IntList();
    int i1 = 0;
    int i2 = 0;
    while (i1 < entries.size() && i2 < other.entries.size()){
      int val1 = entries.getInt(i1);
      int val2 = other.entries.getInt(i2);

      if (val1 == val2) {
        result.add(entries.getInt(i1));
        i1++;
        i2++;
      } else if (val1 > val2){
        i2++;
      } else
        i1++;
    }
    return new SortedIntListSet(result);
  }
  
  public int overlapCount(SortedIntListSet other){
    int result = 0;
    int i1 = 0;
    int i2 = 0;
    while (i1 < entries.size() && i2 < other.entries.size()){
      int val1 = entries.getInt(i1);
      int val2 = other.entries.getInt(i2);

      if (val1 == val2) {
        result++;
        i1++;
        i2++;
      } else if (val1 > val2){
        i2++;
      } else
        i1++;
    }
    return result;
  }
  
  public IntList getEntries(){
    return entries;
  }
}
