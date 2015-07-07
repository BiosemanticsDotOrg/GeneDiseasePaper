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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SparseHighDimensionalMap<K,V> {
	
	private SparseHighDimensionalMatrix<V> data;
	private List<Map<K, Integer>> dim2key2index;
	
	public SparseHighDimensionalMap(int numberOfDimensions){
	  data = new SparseHighDimensionalMatrix<V>(numberOfDimensions);
	  dim2key2index = new ArrayList<Map<K,Integer>>(numberOfDimensions);
	  for (int i = 0; i < numberOfDimensions; i++)
	  	dim2key2index.add(new HashMap<K, Integer>());
	}
	
	public void put(V value, K...keys){
		data.set(convertToIndicesAddIfNeeded(keys),value);
	}
	
	public V get(K...keys){
		int[] indices = convertToIndices(keys);
		if (indices == null)
			return null;
		else		
		  return data.get(indices);
	}
	
	private int[] convertToIndices(K...keys){
		int[] indices = new int[data.dimensions()];
		for (int i = 0; i < keys.length; i++){
			Integer index = dim2key2index.get(i).get(keys[i]);
			if (index == null)
				return null;
			indices[i] = index;
		}
		return indices;
	}
	
	public Set<K> keySet(int dimension){
		return dim2key2index.get(dimension).keySet();
	}
	
	private int[] convertToIndicesAddIfNeeded(K...keys){
		int[] indices = new int[data.dimensions()];
		for (int i = 0; i < keys.length; i++){
			Map<K,Integer> key2index = dim2key2index.get(i);
			Integer index = key2index.get(keys[i]);
			if (index == null){
				index = key2index.size();
				key2index.put(keys[i], index);
			}
			indices[i] = index;
		}
		return indices;
	}
	
  public static class Entry<K,V>{
  	public List<K> keys;
  	public V value;
  }
  
  public Iterator<Entry<K,V>> iterator(){
  	return new EntryIterator();
  }
  
  private class EntryIterator implements Iterator<Entry<K,V>>{

  	private Iterator<SparseHighDimensionalMatrix.Entry<V>> iterator;
  	
  	public EntryIterator(){
  		iterator = data.iterator();
  	}
  	
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Entry<K, V> next() {
			Entry<K, V> entry = new Entry<K, V>();
			
			entry.keys = new ArrayList<K>(data.dimensions());
			SparseHighDimensionalMatrix.Entry<V> shdmEntry = iterator.next();
			for (int i = 0; i < shdmEntry.indices.length; i++)
				entry.keys.add(findKey(i,shdmEntry.indices[i]));
			entry.value = shdmEntry.value;
			return entry;
		}

		private K findKey(int dim, int index) {
			for (Map.Entry<K, Integer> entry : dim2key2index.get(dim).entrySet())
				if (entry.getValue().equals(index))
					return entry.getKey();
			throw new RuntimeException("Value not found in map");
		}

		@Override
		public void remove() {
			System.err.println("Remove method not implemented");
		}
  }
}
