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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReadCSVFileWithHeader implements Iterable<Row>{
	private InputStream inputstream;
	
  public ReadCSVFileWithHeader(String filename) {
    try {
    	inputstream = new FileInputStream(filename);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
	
  public ReadCSVFileWithHeader(InputStream inputstream){
  	this.inputstream = inputstream;
  }
  
	@Override
	public Iterator<Row> iterator() {
		return new RowIterator();
	}
	
	public class RowIterator implements Iterator<Row>{

		private Iterator<List<String>> iterator;
		private Map<String, Integer> fieldName2ColumnIndex;
		
		public RowIterator(){
			iterator = new ReadCSVFile(inputstream).iterator();
			fieldName2ColumnIndex = new HashMap<String, Integer>();
			for (String header : iterator.next())
				fieldName2ColumnIndex.put(header, fieldName2ColumnIndex.size());
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Row next() {
			return new Row(iterator.next(),fieldName2ColumnIndex);
		}

		@Override
		public void remove() {
			throw new RuntimeException("Remove not supported");			
		}
		
	}
}
