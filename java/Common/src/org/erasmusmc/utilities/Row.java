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

package org.erasmusmc.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Row {
	private List<String> cells;
	private Map<String, Integer> fieldName2ColumnIndex;

	public Row(){
		fieldName2ColumnIndex = new HashMap<String, Integer>();
		cells = new ArrayList<String>();
	}
	
	protected Row(List<String> cells, Map<String, Integer> fieldName2ColumnIndex){
		this.cells = cells;
		this.fieldName2ColumnIndex = fieldName2ColumnIndex;
	}

	public Row(Row row) {
		cells = new ArrayList<String>(row.cells);
		fieldName2ColumnIndex = new HashMap<String, Integer>(row.fieldName2ColumnIndex);
	}

	public String get(String fieldName){
		int index;
		try {
			index = fieldName2ColumnIndex.get(fieldName);
		} catch(NullPointerException e){
			throw new RuntimeException("Field \"" + fieldName + "\" not found");
		}
		if (cells.size() <= index)
			return null;
		else
			return cells.get(index);
	}
	
	public Set<String> getFieldNames(){
		return fieldName2ColumnIndex.keySet();
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
	
	public void add(String fieldName, String value){
		fieldName2ColumnIndex.put(fieldName, cells.size());
		cells.add(value);
	}
	
	public void add(String fieldName, int value){
		add(fieldName, Integer.toString(value));
	}
	
	public void add(String fieldName, boolean value){
		add(fieldName, Boolean.toString(value));
	}
	
	public void add(String fieldName, double value){
		add(fieldName, Double.toString(value));
	}
	
	public void add(String fieldName, long value){
		add(fieldName, Long.toString(value));
	}
	
	public void set(String fieldName, String value){
		cells.set(fieldName2ColumnIndex.get(fieldName),value);
	}
	
	public void set(String fieldName, int value){
		set(fieldName, Integer.toString(value));
	}
	
	public void set(String fieldName, long value){
		set(fieldName, Long.toString(value));
	}
	
	public void set(String fieldName, double value){
		set(fieldName, Double.toString(value));
	}

	public List<String> getCells(){
		return cells;
	}
	
	protected Map<String,Integer> getfieldName2ColumnIndex(){
		return fieldName2ColumnIndex;
	}
	
	public String toString(){
		List<String> data = new ArrayList<String>(cells);
		for (String fieldName : fieldName2ColumnIndex.keySet()){
			int index = fieldName2ColumnIndex.get(fieldName);
			if (data.size() > index)
			  data.set(index, "["+fieldName + ": "+data.get(index)+"]");
		}
		return StringUtilities.join(data, ",");	
	}
}
