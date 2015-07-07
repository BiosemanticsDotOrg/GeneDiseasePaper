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

package org.erasmusmc.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TabDelimitedReader {
  protected BufferedReader bufferedReader;
  protected String[] columns;
  protected int currentColumn;
  
  public TabDelimitedReader(InputStream inputStream) {
    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
  }
  
  public boolean hasNextColumn() {
    return currentColumn < columns.length;
  }
  public int getNumberOfColumns(){
    return columns.length;
  }
  
  public String getNextColumn() {
    return columns[currentColumn++];
  }
  
  public boolean proceedToNextRow() throws IOException {
    if (bufferedReader.ready()) {
      columns = bufferedReader.readLine().split("\t");
      currentColumn = 0;
      return true;
    }
    else
      return false;
  }
  public void close(){
    try {
      bufferedReader.close();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
}
