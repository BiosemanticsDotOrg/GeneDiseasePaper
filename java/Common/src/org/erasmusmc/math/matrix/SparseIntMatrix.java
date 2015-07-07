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

package org.erasmusmc.math.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.erasmusmc.collections.IntList;

public class SparseIntMatrix {
  public static void main(String[] args){
    SparseIntMatrix matrix = new SparseIntMatrix();
    Integer[][] fullMatrix = new Integer[100][100];
    for (int x = 0; x < 100; x++)
      for (int y = 0; y < 100; y++)
        fullMatrix[x][y] = 0;
    Random random = new Random();
    for (int i = 0; i < 1000; i++){
      int x = random.nextInt(100);
      int y = random.nextInt(100);
      int value = random.nextInt();
      matrix.set(x, y, value);
      fullMatrix[x][y] = value;
    }
    for (int x = 0; x < 100; x++){
      for (int y = 0; y < 100; y++){
        int value = fullMatrix[x][y];
        if (matrix.get(x, y) != value)
          System.err.println("Bad!");
      }
    }
  }

  private List<Row> rows = new ArrayList<Row>();
  
  public void set(int columnIndex, int rowIndex, int value){
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
        row.setInt(index, value);
      } else {
        row.columnIndex.add(index, columnIndex);
        row.add(index, value);
      }
    }
  }
  
  public int get(int columnIndex, int rowIndex){
    int index = binarySearchRows(rowIndex);
    Row row;
    if (index > rows.size()-1 )
      return 0;
    else {
      row = rows.get(index);
      if (row.rowIndex != rowIndex)
        return 0;
    }

    index = binarySearchColumns(columnIndex, row.columnIndex);
    if (index > row.size()-1)
      return 0;
    else {
      if (row.columnIndex.get(index) == columnIndex)
        return row.get(index);
      else 
        return 0;
    }   
  }
  
  public void add(int columnIndex, int rowIndex, int value){
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
        row.setInt(index, row.get(index)+value);
      } else {
        row.columnIndex.add(index, columnIndex);
        row.add(index, value);
      }
        
    }
  }
  
  private class Row extends IntList{
    private static final long serialVersionUID = 633475150212969144L;
    IntList columnIndex = new IntList();
    int rowIndex;
  }
  
  protected int binarySearchRows(int value) {
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
  
  protected int binarySearchColumns(int value, IntList columnIndex) {
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
}
