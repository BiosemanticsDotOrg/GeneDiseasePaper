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

package org.erasmusmc.collections.datasets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.collections.OneToManyList;
import org.erasmusmc.utilities.ReadCSVFile;
import org.erasmusmc.utilities.WriteCSVFile;

public class DataSetInMemory implements DataSet {
	private List<List<String>> data;
	private Map<String, Integer> fieldName2ColumnIndex;

  public static DataSetInMemory loadFromCSV(String filename){
  	DataSetInMemory dataSet = new DataSetInMemory();
  	Iterator<List<String>> iterator = new ReadCSVFile(filename).iterator();
  	//Parse header:
  	dataSet.fieldName2ColumnIndex = new HashMap<String, Integer>();
  	for (String header : iterator.next())
  		dataSet.fieldName2ColumnIndex.put(header, dataSet.fieldName2ColumnIndex.size());
  	
  	dataSet.data = new ArrayList<List<String>>();
  	//Load data:
  	while (iterator.hasNext())
  		dataSet.data.add(iterator.next());
  	
  	return dataSet;
  }
  
  public void writeToCSV(String filename){
  	WriteCSVFile out = new WriteCSVFile(filename);
  	out.write(getFieldNames());
  	for (List<String> line : data)
  		out.write(line);
  	out.close();
  }

	@Override
	public Iterator<DataLine> iterator() {
		return new DataIterator();
	}
	
	public DataSet innerJoin(DataSet other, String byField){
		return innerJoin(other, new String[]{byField});
	}
  
	public DataSet innerJoin(DataSet other, String[] byFields) {
		//Build index:
		OneToManyList<String, List<String>> key2dataLine = new OneToManyList<String, List<String>>();
		int[] indices = new int[byFields.length];
		for (int i = 0; i < byFields.length; i++){
		  Integer index = fieldName2ColumnIndex.get(byFields[i]);	
			if (index == null)
				throw new RuntimeException("Field name not found: " + byFields[i]);		
			indices[i] = index;
		}
		for (List<String> line : data){
			StringBuilder key = new StringBuilder();
			for (int index : indices){
				if (index >= line.size())
					throw new RuntimeException("Row " + data.indexOf(line) + " does not have enough columns (expected "+fieldName2ColumnIndex.size()+", found " + line.size()+")");
				key.append(line.get(index));
				key.append('\n');
			}
			key2dataLine.put(key.toString(), line);
		}
		
		//Create new dataset, and merge field names:
		DataSetInMemory merged = new DataSetInMemory();
		merged.data = new ArrayList<List<String>>();
		merged.fieldName2ColumnIndex = new HashMap<String, Integer>(fieldName2ColumnIndex);
		List<String> otherFieldNames = other.getFieldNames();
		for (String byField : byFields)
			otherFieldNames.remove(byField);
		List<String> fieldsToCopy = new ArrayList<String>();
		for (String field : otherFieldNames){
			if (fieldName2ColumnIndex.containsKey(field))
				System.err.println("Warning: duplicate field name found: " + field + ", ignoring one field");
			else {
				merged.fieldName2ColumnIndex.put(field, merged.fieldName2ColumnIndex.size());
				fieldsToCopy.add(field);
			}
		}
		
		//Merge data:
		for (DataLine otherLine : other){
			StringBuilder key = new StringBuilder();
			for (String byField : byFields){
				key.append(otherLine.get(byField));
				key.append('\n');
			}
			List<List<String>> thisLines = key2dataLine.get(key.toString());
			for (List<String> thisLine : thisLines){
				List<String> newLine = new ArrayList<String>(thisLine);
				for (String field : fieldsToCopy)
					newLine.add(otherLine.get(field));
				merged.data.add(newLine);
			}
		}
		return merged;
	}

	@Override
	public List<String> getFieldNames() {
		int size = fieldName2ColumnIndex.size();
		List<String> fieldNames = new ArrayList<String>(size);
		for (int i = 0; i < size; i++)
			fieldNames.add(null);
		for (Map.Entry<String, Integer> entry : fieldName2ColumnIndex.entrySet())
			fieldNames.set(entry.getValue(), entry.getKey());
		return fieldNames;
	}

	
	private class DataIterator implements Iterator<DataLine>{
    private Iterator<List<String>> iterator;
    
    public DataIterator(){
    	iterator = data.iterator();
    }
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public DataLine next() {
			return new DataLineImplementation(iterator.next());
		}

		@Override
		public void remove() {
			iterator.remove();
		}
	}
	
	private class DataLineImplementation implements DataLine {
    private List<String> dataLine;
		
    public DataLineImplementation(List<String> dataLine){
    	this.dataLine = dataLine;
    }
    
		public String get(String field) {
			Integer index = fieldName2ColumnIndex.get(field);
			if (index == null)
				throw new RuntimeException("Field name not found: " + field);
			if (index >= dataLine.size())
				throw new RuntimeException("Row " + data.indexOf(dataLine) + " does not have enough columns (expected "+fieldName2ColumnIndex.size()+", found " + dataLine.size()+")");
			return dataLine.get(index);
		}
		
		public int getInt(String fieldName){
			return Integer.parseInt(get(fieldName));
		}
		
		public long getLong(String fieldName){
			return Long.parseLong(get(fieldName));
		}
		
		public double getDouble(String fieldName){
			return Double.parseDouble(get(fieldName));
		}
	}

	@Override
	public void renameField(String oldName, String newName) {
		Integer index = fieldName2ColumnIndex.remove(oldName);
		fieldName2ColumnIndex.put(newName, index);
	}
}
