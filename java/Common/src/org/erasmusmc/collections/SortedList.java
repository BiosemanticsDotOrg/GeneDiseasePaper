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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class SortedList<E> implements Iterable<E>{
  protected List<E> elements = new ArrayList<E>();
  protected Comparator<E> comparator;
  
  public SortedList(Comparator<E> comparator) {
    this.comparator = comparator;
  }
  
  protected int binarySearch(E element) {
    int low = 0, middle, high = elements.size();
    
    while (low < high) {
      middle = low + (high - low) / 2;
      
      if (comparator.compare(element, elements.get(middle)) > 0)
        low = middle + 1;
      else
        high = middle;
    }
    
    return low;
  }
  
  public boolean add(E element) {
    int index = binarySearch(element);
    elements.add(index, element);
    return true;
  }
  
  public E get(int index) {
    return elements.get(index);
  }
  
  public int remove(E element) {
    int index = binarySearch(element);
    
    if (index < elements.size() && elements.get(index) == element) {
      elements.remove(index);
      return index;
    }
    else
      return -1;
  }
  
  public void clear() {
    elements.clear();
  }
  
  
  public int indexOf(E element) {
    int index = binarySearch(element);
    
    if (index < elements.size() && elements.get(index) == element)
      return index;
    else
      return -1;
  }
  
  public int size() {
    return elements.size();
  }

  public Iterator<E> iterator() {
    
    return elements.listIterator();
  }


}
