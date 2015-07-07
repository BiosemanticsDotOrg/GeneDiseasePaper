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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

public class ReadCSVFile implements Iterable<List<String>>{
  protected BufferedReader bufferedReader;
  public boolean EOF = false;
  private char delimiter = ',';

  public ReadCSVFile(String filename) {
    try {
      FileInputStream textFileStream = new FileInputStream(filename);
      bufferedReader = new BufferedReader(new InputStreamReader(textFileStream, "UTF-8"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
  }
  
  public ReadCSVFile(InputStream inputstream){
  	try {
			bufferedReader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
  }
  
  public Iterator<List<String>> getIterator() {
    return iterator();
  }

  private class CSVFileIterator implements Iterator<List<String>> {
    private String buffer;
    
    public CSVFileIterator() {
      try {
        buffer = bufferedReader.readLine();
        if (buffer == null){
          EOF = true;
          bufferedReader.close();
        }
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      
    }

    public boolean hasNext() {
      return !EOF;
    }

    public List<String> next() {
      String result = buffer;
      try {
        buffer = bufferedReader.readLine();
        if (buffer == null){
          EOF = true;
          bufferedReader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      return line2columns(result);
    }

    public void remove() {
      System.err.println("Unimplemented method 'remove' called");
    }
  }

  public Iterator<List<String>> iterator() {
    return new CSVFileIterator();
  }
  
  private List<String> line2columns(String line){
    List<String> columns = StringUtilities.safeSplit(line, delimiter);
    for (int i = 0; i < columns.size(); i++){
      String column = columns.get(i);
      if (column.startsWith("\"") && column.endsWith("\"") && column.length() > 1)
        column = column.substring(1, column.length()-1);
      column = column.replace("\\\"", "\"");
      columns.set(i, column);
    }
    return columns;
  }

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public char getDelimiter() {
		return delimiter;
	}
}
