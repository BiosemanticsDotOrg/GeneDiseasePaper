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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BinaryFileUtilities {
  
  public static void saveObject(Object object, String filename){
    try { 
      FileOutputStream binFile = new FileOutputStream(filename);
      try {  
        ObjectOutputStream out = new ObjectOutputStream(binFile);
        out.writeObject(object);
      }catch (IOException e) {
        e.printStackTrace();
      }        
    } catch (FileNotFoundException e){
      e.printStackTrace();           
    }
  }
  
  public static Object loadObject(String filename){
    Object result = null;
    try { 
      FileInputStream binFile = new FileInputStream(filename);
      try {  
        ObjectInputStream inp = new ObjectInputStream(binFile);
        try{
          result = inp.readObject();
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }        
      }catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e){
      e.printStackTrace();           
    }
    return result;
  }
  
}
