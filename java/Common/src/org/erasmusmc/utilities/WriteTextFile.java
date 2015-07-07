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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class WriteTextFile {
  
  public WriteTextFile(String filename){
    FileOutputStream PSFFile;
    try {
      PSFFile = new FileOutputStream(filename);
      bufferedWrite = new BufferedWriter( new OutputStreamWriter(PSFFile, "UTF-8"),10000);      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      System.err.println("Computer does not support UTF-8 encoding");
      e.printStackTrace();
    }
  }
  
  public void writeln(String string){
    try {
      bufferedWrite.write(string);
      bufferedWrite.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void writeln(int integer){
  	writeln(Integer.toString(integer));
  }
  
  public void writeln(Object object){
  	writeln(object.toString());
  }
  
  public void flush(){
    try {
      bufferedWrite.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void close() {
    try {
      bufferedWrite.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private BufferedWriter bufferedWrite;
}
