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
import java.util.ArrayList;
import java.util.List;

public class TextFileUtilities {
  
  public static List<String> loadFromFile(String filename){
    List<String> result = new ArrayList<String>();
    for (String line : new ReadTextFile(filename))
    	result.add(line);
    return result;
  }
  
  
  public static void saveToFile(List<String> lines, String filename){
  	WriteTextFile out = new WriteTextFile(filename);
  	for (String line : lines)
  		out.writeln(line);
  	out.close();
  }
  
  public static void appendToFile(String text, String fileName) {                 
    try {
      FileOutputStream file = new FileOutputStream(fileName,true);
      BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(file),1000000);
      try {
        bufferedWrite.write(text);  
        bufferedWrite.newLine();
        bufferedWrite.flush();
        bufferedWrite.close();
      }catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
  }
}  
