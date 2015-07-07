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

/**
 * Class for storing a high dimensional sparse data as a tree
 * @author schuemie
 *
 * @param <T>
 */
public class SparseHighDimensionalMatrix<T> {

	private Row rootRow = new Row();
	private int dimensions;
	
	public static void main(String[] args){
		SparseHighDimensionalMatrix<String> matrix = new SparseHighDimensionalMatrix<String>(3);
		matrix.set(new int[]{-1,2,3}, "-1-2-3");
		matrix.set(new int[]{1,2,5}, "1-2-5");
		matrix.set(new int[]{1,2,1}, "1-2-1");
		matrix.set(new int[]{3,2,1}, "3-2-1");
		matrix.set(new int[]{2,2,1}, "2-2-1");

		System.out.println(matrix.get(new int[]{-1,2,3}));
		System.out.println(matrix.get(new int[]{1,2,5}));
		System.out.println(matrix.get(new int[]{1,2,1}));
		System.out.println(matrix.dimensions());
		System.out.println(matrix.size());
		
		Iterator<Entry<String>> iterator = matrix.iterator();
		while (iterator.hasNext()){
			Entry<String> entry = iterator.next();
			System.out.println(toString(entry.indices)+ "\t" + entry.value);
			
		}
	}
	

	private static String toString(int[] indices) {
		StringBuilder sb = new StringBuilder();
		for (int i : indices){
			sb.append(i);
			sb.append(",");
		}
		return sb.toString();
	}
	
	public SparseHighDimensionalMatrix(int dimensions){
		this.dimensions = dimensions;		
	}

	public void set(int[] indices, T value){
		if (indices.length != dimensions)
			throw new RuntimeException("Wrong number of dimensions: " + indices.length + " instead of " + dimensions);
		Row row = rootRow;

		for (int i = 0; i < indices.length-1; i++){
			int index = indices[i];
			int internalIndex = binarySearch(index,row);
			Row nextRow;
			if (internalIndex >= row.size()){
				nextRow = new Row();
				row.add(nextRow);
				row.index.add(index);
			} else {
				if (row.index.get(internalIndex) == index){
					nextRow = (Row)row.get(internalIndex);
				} else {
					nextRow = new Row();
					row.add(internalIndex,nextRow);
					row.index.add(internalIndex,index);
				}
			}
			row = nextRow;
		}
		int index = indices[indices.length-1];
		int internalIndex = binarySearch(index, row);
		if (internalIndex >= row.size()){
			row.add(value);
			row.index.add(index);
		} else {
			if (row.index.get(internalIndex) == index){
				row.set(internalIndex, value);    	 
			} else {
				row.add(internalIndex,value);
				row.index.add(internalIndex,index);

			}
		}
	}

	@SuppressWarnings("unchecked")
	public T get(int[] indices){
		Object o = rootRow;
		for (int i = 0; i < indices.length; i++){
			int index = indices[i];
			Row row = (Row)o;
			int rowIndex = binarySearch(index, row);
			if (rowIndex>=row.size())
				return null;
			else if (row.index.get(rowIndex) != index)
				return null;
			else 
				o = row.get(rowIndex);
		}
		return (T)o;
	}

	public void trimToSize(){
		trimToSize(rootRow);
	}


	private void trimToSize(Row row) {
		row.trimToSize();
		for (Object o : row)
			if (o instanceof Row)
				trimToSize((Row)o);
	}


	private static class Row extends ArrayList<Object>{
		private static final long serialVersionUID = 633475150212969144L;
		IntList index = new IntList();
		public Row(){
			super(1);
		}
		
		public void clear(){
			super.clear();
			index.clear();
		}
	}

	private int binarySearch(int value, Row row) {
		int low = 0, middle, high = row.size();
		while (low < high) {
			middle = (low + high) / 2;

			if (row.index.get(middle) < value) 
				low = middle + 1;
			else
				high = middle;
		}
		return low;
	}

	public int size(){
		return size(rootRow);
	}
	
	public int dimensions(){
		return dimensions;
	}

	private int size(Row row) {
		int result = 0;
		for (Object o : row)
			if (o instanceof Row)
				result += size((Row)o);
			else
				result++;
		return result;
	}


	public static class Entry<T> {
		public int[] indices;
		public T value;
	}

	public Iterator<Entry<T>> iterator() {
		return new MatrixIterator();
		}

	public void clear(){
		rootRow.clear();
	}

	private class MatrixIterator implements Iterator<Entry<T>>{
		private int[] currentIndices;
		private boolean hasNext = true;
		
		public MatrixIterator(){
			currentIndices = new int[dimensions];
			for (int i = 0; i < dimensions; i++)
				currentIndices[i] = 0;
			if (rootRow.size() == 0)
				hasNext = false;
		}
		
		public boolean hasNext() {
			return hasNext;
		}


		@SuppressWarnings("unchecked")
		public Entry<T> next() {
			//Get entry at current index:
			int[] indices = new int[dimensions];
			Object o = rootRow;
			for (int i = 0; i < dimensions; i++){
				int internalIndex = currentIndices[i];
				indices[i] = ((Row)o).index.get(internalIndex);
				o = ((Row)o).get(internalIndex);
			}
			Entry<T> entry = new Entry<T>();
			entry.value = (T)o;
			entry.indices = indices;
			
			hasNext = moveToNext(rootRow, 0);
			
			return entry;
		}
		
		private boolean moveToNext(Row row, int level){
			Object o = row.get(currentIndices[level]);
		  if (o instanceof Row){
		   if (moveToNext((Row)o, level+1))
		  	 return true;
		   else
		  	 currentIndices[level+1] = 0; 
		  }
		  currentIndices[level]++;
		  return (currentIndices[level] < row.size());  	
		}

		public void remove() {
			System.err.println("Remove method not implemented");

		}


	}


}
