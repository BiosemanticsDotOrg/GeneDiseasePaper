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
import java.util.List;
import java.util.Map;

public class WriteCSVFileWithHeader {
	
	private WriteCSVFile out;
	private boolean headerWritten;
	
	public WriteCSVFileWithHeader(String filename){
		out = new WriteCSVFile(filename);
		headerWritten = false;
	}
	
	public void write(Row row){
		if (!headerWritten)
			writeHeader(row);
		out.write(row.getCells());
	}
	
	public void close(){
		out.close();
	}
	
	public void flush(){
		out.flush();
	}
	
	private void writeHeader(Row row) {
		headerWritten = true;
		Map<String, Integer> fieldName2ColumnIndex = row.getfieldName2ColumnIndex();
		int size = fieldName2ColumnIndex.size();
		List<String> header = new ArrayList<String>(size);
		for (int i = 0; i < size; i++)
			header.add(null);
		for (Map.Entry<String, Integer> entry : fieldName2ColumnIndex.entrySet())
			header.set(entry.getValue(), entry.getKey());
		out.write(header);		
	}
}