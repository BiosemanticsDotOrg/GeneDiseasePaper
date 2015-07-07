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

package org.erasmusmc.peregrine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.SparseMatrix;
import org.erasmusmc.peregrine.ConceptPeregrine.TermLink;
import org.erasmusmc.peregrine.ConceptPeregrine.TokenPair;
/**
 * Internal class for Peregrine
 * @author martijn
 *
 */
public class TokenPairToTermLinksMap extends SparseMatrix<List<TermLink>> implements Map<TokenPair, List<TermLink>>{

  public void clear() {
    super.clear();    
  }

  public boolean containsKey(Object key) {
    System.err.println("Calling unimplemented method");
    return false;
  }

  public boolean containsValue(Object value) {
    System.err.println("Calling unimplemented method");
    return false;
  }

  public Set<java.util.Map.Entry<TokenPair, List<TermLink>>> entrySet() {
    return new EntrySet();
  }

  public List<TermLink> get(Object key) {
    return get(((TokenPair)key).token1, ((TokenPair)key).token2);
  }

  public boolean isEmpty() {
    System.err.println("Calling unimplemented method");
    return false;
  }

  public Set<TokenPair> keySet() {
    System.err.println("Calling unimplemented method");
    return null;
  }

  public List<TermLink> put(TokenPair key, List<TermLink> value) {
    set(key.token1, key.token2, value);
    return value;
  }

  public void putAll(Map<? extends TokenPair, ? extends List<TermLink>> m) {
    System.err.println("Calling unimplemented method");
    
  }

  public List<TermLink> remove(Object key) {
    System.err.println("Calling unimplemented method");
    return null;
  }
  
  public int size(){
    return super.size();
  }

  public Collection<List<TermLink>> values() {
    List<List<TermLink>> result = new ArrayList<List<TermLink>>();
    for (Row row : rows){
      result.addAll(row);
    }
    return result;
  }
  
  private Iterator<SparseMatrix<List<TermLink>>.Entry<List<TermLink>>> matrixIterator(){
    return super.iterator();
  }

  private class EntrySet implements Set<java.util.Map.Entry<TokenPair, List<TermLink>>>{

    public boolean add(java.util.Map.Entry<TokenPair, List<TermLink>> arg0) {
      System.err.println("Calling unimplemented method");
      return false;
    }

    public boolean addAll(Collection<? extends java.util.Map.Entry<TokenPair, List<TermLink>>> arg0) {
      System.err.println("Calling unimplemented method");
      return false;
    }

    public void clear() {
      System.err.println("Calling unimplemented method");
      
    }

    public boolean contains(Object arg0) {
      System.err.println("Calling unimplemented method");
      return false;
    }

    public boolean containsAll(Collection<?> arg0) {
      System.err.println("Calling unimplemented method");
      return false;
    }

    public boolean isEmpty() {
      System.err.println("Calling unimplemented method");
      return false;
    }

    public Iterator<java.util.Map.Entry<TokenPair, List<TermLink>>> iterator() {
      return new CustomIterator(matrixIterator());
    }

    public boolean remove(Object arg0) {
      System.err.println("Calling unimplemented method");
      return false;
    }

    public boolean removeAll(Collection<?> arg0) {
      System.err.println("Calling unimplemented method");
      return false;
    }

    public boolean retainAll(Collection<?> arg0) {
      System.err.println("Calling unimplemented method");
      return false;
    }

    public int size() {
      System.err.println("Calling unimplemented method");
      return 0;
    }

    public Object[] toArray() {
      System.err.println("Calling unimplemented method");
      return null;
    }

    public <T> T[] toArray(T[] arg0) {
      System.err.println("Calling unimplemented method");
      return null;
    }
    
    
  }
  
  private class CustomIterator implements Iterator<java.util.Map.Entry<TokenPair, List<TermLink>>>{

    Iterator<SparseMatrix<List<TermLink>>.Entry<List<TermLink>>> iterator;
    
    public CustomIterator(Iterator<SparseMatrix<List<TermLink>>.Entry<List<TermLink>>> iterator){
      this.iterator = iterator;
    }
    public boolean hasNext() {
      return iterator.hasNext();
    }

    public java.util.Map.Entry<TokenPair, List<TermLink>> next() {
      SparseMatrix<List<TermLink>>.Entry<List<TermLink>> entry = iterator.next();
      MapEntry mapEntry = new MapEntry();
      mapEntry.key = new TokenPair(entry.column, entry.row);
      mapEntry.value = entry.value;
      return mapEntry;
    }

    public void remove() {
      System.err.println("Calling unimplemented method");
    }
    
    private class MapEntry implements java.util.Map.Entry<TokenPair, List<TermLink>>{
      TokenPair key;
      List<TermLink> value;
      
      public TokenPair getKey() {
        return key;
      }

      public List<TermLink> getValue() {
        return value;
      }

      public List<TermLink> setValue(List<TermLink> arg0) {
        return null;
      }
      
    }
    
  }


}
