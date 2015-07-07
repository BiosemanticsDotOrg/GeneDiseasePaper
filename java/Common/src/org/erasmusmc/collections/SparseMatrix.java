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
import java.util.Iterator;


public class SparseMatrix<T> implements Iterable<SparseMatrix<T>.Entry<T>> {

  protected ArrayList<Row> rows = new ArrayList<Row>();

  public void set(int columnIndex, int rowIndex, T value){
    int index = binarySearchRows(rowIndex);
    Row row;
    if (index > rows.size()-1 ){
      row = new Row();
      row.rowIndex = rowIndex;
      rows.add(index, row);
    } else {
      row = rows.get(index);
      if (row.rowIndex != rowIndex){
        row = new Row();
        row.rowIndex = rowIndex;
        rows.add(index, row);
      }
    }

    index = binarySearchColumns(columnIndex, row.columnIndex);
    if (index > row.size()-1){
      row.columnIndex.add(columnIndex);
      row.add(value);
    } else {
      if (row.columnIndex.get(index) == columnIndex){
        row.set(index, value);
      } else {
        row.columnIndex.add(index, columnIndex);
        row.add(index, value);
      }
    }
  }

  public T get(int columnIndex, int rowIndex){
    int index = binarySearchRows(rowIndex);
    Row row;
    if (index > rows.size()-1 )
      return null;
    else {
      row = rows.get(index);
      if (row.rowIndex != rowIndex)
        return null;
    }

    index = binarySearchColumns(columnIndex, row.columnIndex);
    if (index > row.size()-1)
      return null;
    else {
      if (row.columnIndex.get(index) == columnIndex)
        return row.get(index);
      else 
        return null;
    }   
  }
  
  public void trimToSize(){
    rows.trimToSize();
    for (Row row : rows){
      row.trimToSize();
      row.columnIndex.trimToSize();
    }
  }

  protected class Row extends ArrayList<T>{
    private static final long serialVersionUID = 633475150212969144L;
    IntList columnIndex = new IntList();
    int rowIndex;
    public Row(){
      super(1);
    }
  }

  private int binarySearchRows(int value) {
    int low = 0, middle, high = rows.size();
    while (low < high) {
      middle = (low + high) / 2;

      if (rows.get(middle).rowIndex < value) 
        low = middle + 1;
      else
        high = middle;
    }
    return low;
  }

  private int binarySearchColumns(int value, IntList columnIndex) {
    int low = 0, middle, high = columnIndex.size();
    while (low < high) {
      middle = (low + high) / 2;

      if (columnIndex.get(middle) < value) 
        low = middle + 1;
      else
        high = middle;
    }
    return low;
  }  
  
  public int size(){
    int result = 0;
    for (Row row : rows)
      result += row.size();
    return result;
  }

  public class Entry<V> {
    public int column;
    public int row;
    public V value;
  }

  public Iterator<Entry<T>> iterator() {
    return new MatrixIterator();
  }
  
  public void clear(){
    rows.clear();
  }
  
  private class MatrixIterator implements Iterator<Entry<T>>{
    private int rowIndex = 0;
    private int columnIndex = 0;
    public boolean hasNext() {
      return (rowIndex != rows.size());
    }

    public Entry<T> next() {
      Row row = rows.get(rowIndex);
      Entry<T> entry = new Entry<T>();
      entry.row = row.rowIndex;
      entry.column = row.columnIndex.getInt(columnIndex);
      entry.value = row.get(columnIndex);
      columnIndex++;
      if (columnIndex == row.size()){
        rowIndex++;
        columnIndex = 0;
      }
      return entry;
    }

    public void remove() {
      System.err.println("Remove method not implemented");
      
    }
    
    
  }
  
  
}
