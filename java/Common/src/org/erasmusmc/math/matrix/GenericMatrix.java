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
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.math.space.ListSpace;
import org.erasmusmc.math.space.Space;

public class GenericMatrix<R,C,V> {//implements Visitable{
  public ArrayList<ArrayList<V>> values;
  protected Space<R> rowSpace;
  protected Space<C> columnSpace;
  public String name = "";
  public GenericMatrix(){
    
  }
  
  public GenericMatrix(Space<R> rowSpace, Space<C> columnSpace) {
    setSpaces(rowSpace, columnSpace);
  }
  public Space<R> getRowSpace() {
    return rowSpace;
  }

  public Space<C> getColumnSpace() {
    return columnSpace;
  }
  public void setSpaces(Space<R> rowSpace, Space<C> columnSpace) {
    this.rowSpace = rowSpace;
    this.columnSpace = columnSpace;

    values = new ArrayList<ArrayList<V>>();
    
    for (int i =0; i<rowSpace.getDimensions();i++){
      values.add(new ArrayList<V>());
    }
  }
  public void set(R row, C column, V value) {
    int rownum =rowSpace.indexOfObject(row);
    int colnum = columnSpace.indexOfObject(column);
    if (values.get(rownum).size()>colnum){
      values.get(rownum).set(colnum,value);
    }
    else if (values.get(rownum).size()==colnum){
      values.get(rownum).add(value);  
    }
    else {
      while (values.get(rownum).size()<colnum){
        values.add(null);
      }
      values.get(rownum).add(value); 
    }
  }
  
  public V get(R row, C column) {
    return values.get(rowSpace.indexOfObject(row)).get(columnSpace.indexOfObject(column));
  }
  public GenericMatrix<R,C,V> getSubMatrixByRowList(List<R> rowList){
    Space<R> newRowSpace= new ListSpace<R>(rowList);
    newRowSpace.setDimensionsCaption(rowSpace.getDimensionsCaption());
    GenericMatrix<R,C,V> result = new GenericMatrix<R,C,V>(newRowSpace,columnSpace);
    
    
    for(R r: rowList){
      Iterator<C> iterator =columnSpace.iterator();
      while(iterator.hasNext()){
        C c = iterator.next();
        result.set(r,c,get(r,c));
      }
    }
    return result;
    
  }

  /*public Object accept(Visitor visitor) {
    
    return visitor.visitGenericMatrix(this);
  }*/
  public String toString(){
    if (name!=""){
      return name;
    }
    return "GenericMatrix";
  }
}
