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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.utilities.StringUtilities;

public class SortedListSet<K> implements Set<K>,Serializable {

  private static final long serialVersionUID = -3522054629441473877L;
  protected List<K> setEntries;
  protected Comparator<K> comparator;
  public String toString(){
    StringBuffer result = new StringBuffer("[");
    result.append(StringUtilities.join(setEntries,","));
    result.append("]");
    return result.toString();
      }
    
  public SortedListSet(Comparator<K> comparator) {
    this.comparator = comparator;
    setEntries =  new ArrayList<K>();
  }
  public SortedListSet(Comparator<K> comparator,List<K> setEntries) {
    //if you use this constructor: know what you are doing!
    //the list is expected to be sorted exactly as would be done by the
    //given comparator!
    this.comparator = comparator;
    this.setEntries =  setEntries;
  }  
  public Comparator<K> getComparator(){
    return comparator;
  }
  public Iterator<K> iterator() {
    return new Iterator<K> () {
      int index = 0;
      
      public boolean hasNext() {
        return index < setEntries.size();
      }

      public K next() {
        return setEntries.get(index++);
      }

      public void remove() {
      }
    };
  }
  
  public boolean add(K key) {

    
    int index = binarySearch(key);
    
    if (index < setEntries.size()) {
      K k = setEntries.get(index);
      
      if (comparator.compare(key, k) == 0){
        setEntries.set(index,key);
        
      }
      else
        setEntries.add(index, key);
    }
    else
      setEntries.add(index, key);
   
    //err... always true?
    return true;
  }
  
 
  
  
  protected int binarySearch(K key) {
    int low = 0, middle, high = setEntries.size();
    
    while (low < high) {
      middle = (low + high) / 2;
      
      if (comparator.compare(key, setEntries.get(middle)) > 0) 
        low = middle + 1;
      else
        high = middle;
    }
    
    return low;
  }
  
  public int size() {
    return setEntries.size();
  }
  
  public void clear() {
    setEntries.clear();
  }
  
  

  public boolean isEmpty() {
    if (setEntries.size()==0){
      return true;
    }
    else{
      return false;
    }
  }

  @SuppressWarnings("unchecked")
  public boolean contains(Object key) {
    //listen, if you're going to be so stupid to put 
    //an object in here which is not of type K, all weird shit is going to happen.
    //Don't look at me then!
    //love Rob

    int index = binarySearch((K)key);

    if (index < setEntries.size()) {
      K k = setEntries.get(index);

      if (comparator.compare((K)key, k) == 0){
        return true;
      }
      else
        return false;
    }
    else
      return false;
  }

  /**This is a special function if you know FOR SURE that you have a list sorted
  *according to how the Comparator would sort, and you would like to set it
  * as the list underlying the set. Note that the old list is lost.*/
  public void setSortedList(List<K> list){
    setEntries = list;
  }
  
/**This returns the precious sorted list: Don't fuck it up! (please)*/
  public List<K> getSortedList(){
    
    return setEntries;
   
  }
  public Object[] toArray() {
    
    return setEntries.toArray();
  }

  @SuppressWarnings("unchecked")
  public Object[] toArray(Object[] arg0) {
    
    return setEntries.toArray(arg0);
  }

 

  @SuppressWarnings("unchecked")
  public boolean remove(Object key) {
    int index = binarySearch((K)key);
    
    if (index < setEntries.size()) {
      K k = setEntries.get(index);
      
      if (comparator.compare((K)key, k) == 0){
        setEntries.remove(index);
        return true;
      }
      else
        return false;
    }
    else
      return false;
  }

  
  public boolean containsAll(Collection<?> arg0) {
    Iterator<?> iterator = arg0.iterator();
    while (iterator.hasNext()){
      if(!contains(iterator.next())){
        return false;
      }
    }
    return true;
  }

  public boolean addAll(Collection<? extends K> arg0) {
    Iterator<? extends K> iterator = arg0.iterator();
    while (iterator.hasNext()){
      add(iterator.next());
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  public boolean retainAll(Collection<?> arg0) {
    //not very efficient!!
    List<K> temp = new ArrayList<K>();
    Iterator<?> iterator = arg0.iterator();
    while (iterator.hasNext()){
      Object object = iterator.next();
      if (contains(object)){
        temp.add((K)object);
      }
    }
    setEntries = temp;
    return true;
  }

  public boolean removeAll(Collection<?> arg0) {
    Iterator<?> iterator = arg0.iterator();
    while (iterator.hasNext()){
      remove(iterator.next());
    }
    return true;
    
  }

 
  public static Comparator<Integer> getAscendingIntegerComparator(){
    return new Comparator<Integer>(){
  

    public int compare(Integer arg0, Integer arg1) {

      return arg0-arg1;
    }
    
  };
  }
  }
