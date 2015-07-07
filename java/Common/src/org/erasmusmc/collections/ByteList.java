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
import java.util.List;
import java.util.ListIterator;

public class ByteList implements List<Byte>, Iterable<Byte>, Serializable {
  private static final long serialVersionUID = 1109817464341796563L;
  private byte[] array;
  private static int defaultCapacity = 8;
  private int size = 0;

  public ByteList() {
    array = new byte[defaultCapacity];
  }
  

  public ByteList(ByteList copy) {
    this(copy.size);
    System.arraycopy(copy.array, 0, this.array , 0, copy.size);
    this.size = this.array.length;
  }

  public ByteList(int initialCapacity) {
    array = new byte[initialCapacity];
  }
public ByteList(byte[] array){
  this.array = array;
  size = array.length;
}
public ByteList(Collection<Byte> collection) {
  this(collection.size());
  Iterator<Byte> it = collection.iterator();
  int x = 0;
  while(it.hasNext()) {
    Byte i = it.next();
    this.array[x++] = i;
  }
  this.size = this.array.length;
}
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer("[");
    if (size > 0)
      buffer.append(Byte.toString(array[0]));
    for (int i = 1; i < size; i++) {
      buffer.append(", ");
      buffer.append(Byte.toString(array[i]));
    }
    buffer.append("]");
    return buffer.toString();
  }

  public void add(int index, byte element) {
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

  public boolean add(byte i) {
    if (size >= array.length)
      grow();
    array[size] = i;
    size++;
    return true;
  }

  public byte set(int index, byte element) {
    if (index < size) {
      byte current = array[index];
      array[index] = element;
      return current;
    }
    else {
      throw new IndexOutOfBoundsException();
    }
  }
  public boolean addAll(int index, Collection<? extends Byte> collection) {
    int newsize =index + collection.size();
    if (newsize  > array.length)
      setCapacity(newsize);
    size = newsize;
    Iterator<? extends Byte> iterator = collection.iterator();
    
    while (iterator.hasNext()) {
      array[index] = iterator.next();
      index++;
    }
    return false;
  }
  public boolean addAll(Collection<? extends Byte> collection) {
    if (size + collection.size() > array.length)
      setCapacity(size + collection.size());
    Iterator<? extends Byte> iterator = collection.iterator();
    while (iterator.hasNext()) {
      array[size] = iterator.next();
      size++;
    }
    return true;
  }

  public Byte remove(int index) throws ArrayIndexOutOfBoundsException {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException("list[" + index + "] is out of bounds (max " + (size - 1) + ")");
    byte returnval = array[index];
    System.arraycopy(array, index + 1, array, index, size - index - 1);
    size--;
    return returnval;
  }

  public byte[] toIntArray() {
    return array;
  }

  public void clear() {
    size = 0;
  }

  public byte getByte(int index) throws ArrayIndexOutOfBoundsException {
    if (index < 0 || index >= size)
      throw new ArrayIndexOutOfBoundsException("list[" + index + "] is out of bounds (max " + (size - 1) + ")");
    return array[index];
  }
  public Byte get(int index) throws ArrayIndexOutOfBoundsException {
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
    byte[] newArray = new byte[newCapacity];
    System.arraycopy(array, 0, newArray, 0, size);
    array = newArray;
    // System.gc(); //Nice idea, but it costs a lot of performance!
  }

  public Iterator<Byte> iterator() {

    return new ByteIterator();
  }

  private class ByteIterator implements Iterator<Byte> {
    private int index = 0;

    public boolean hasNext() {

      return index < size;
    }

    public Byte next() {
      byte val = array[index];
      index++;
      return val;
    }

    public void remove() {
      if (index > 0) {
        ByteList.this.remove(index - 1);
        index--;
      }
    }

  }

  public ByteList subList(int lowerBound, int upperbound) {
    if (lowerBound < 0 || lowerBound > size ||upperbound < 0 || upperbound > size)
      throw new ArrayIndexOutOfBoundsException("lowerbound: " + lowerBound + " or upperbound: " + upperbound + "  is out of bounds (max " + (size - 1) + ")");
    byte[] newArray = new byte[upperbound-lowerBound];
    System.arraycopy(array, lowerBound, newArray, 0, upperbound-lowerBound);
    ByteList result = new ByteList(newArray);
    return result;
  }

  public boolean add(Byte o) {
    return add(o.byteValue());
  }

  public void add(int index, Byte element) {
    add(index,element.byteValue());
  }


  public boolean contains(Object o) {
    System.out.println("Function not implemented!");
    return false;
  }

  public boolean containsAll(Collection<?> c) {
    System.out.println("Function not implemented!");
    return false;
  }

  public int indexOf(Object o) {
    System.out.println("Function not implemented!");
    return 0;
  }

  public boolean isEmpty() {
    System.out.println("Function not implemented!");
    return false;
  }

  public int lastIndexOf(Object o) {
    System.out.println("Function not implemented!");
    return 0;
  }

  public ListIterator<Byte> listIterator() {
    System.out.println("Function not implemented!");
    return null;
  }

  public ListIterator<Byte> listIterator(int index) {
    System.out.println("Function not implemented!");
    return null;
  }

  public boolean remove(Object o) {
    System.out.println("Function not implemented!");
    return false;
  }

  public boolean removeAll(Collection<?> c) {
    System.out.println("Function not implemented!");
    return false;
  }

  public boolean retainAll(Collection<?> c) {
    System.out.println("Function not implemented!");
    return false;
  }

  public Byte set(int index, Byte element) {
    return set(index,element.byteValue());
  }

  public <T> T[] toArray(T[] a) {
    System.out.println("Function not implemented!");
    return null;
  }

  public Object[] toArray() {
    
    return null;
  }

  
}
