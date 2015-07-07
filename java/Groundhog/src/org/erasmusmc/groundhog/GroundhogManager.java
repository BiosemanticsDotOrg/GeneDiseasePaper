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


package org.erasmusmc.groundhog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.utilities.DirectoryUtilities;

import com.sleepycat.je.DatabaseException;


public class GroundhogManager {
  public String baseDirectoryPath = "../Data/Databases/Groundhogs/";
  public File baseDir;
  private int defaultCacheSize = 302400000;
  public GroundhogManager(String baseDirectoryPath){
    this.baseDirectoryPath = baseDirectoryPath;
    try {
      baseDir = new File(baseDirectoryPath);
    }
    catch (NullPointerException npe) {
      System.out.println(baseDirectoryPath + " not found...");
    }
  }
  public GroundhogManager(){
    try {
      baseDir = new File(baseDirectoryPath);
    }
    catch (NullPointerException npe) {
      System.out.println(baseDirectoryPath + " not found...");
    }
  }
  
  public List<String> getDatabaseListing(){
  if (!baseDir.exists())
    baseDir.mkdir();
  String[] arrayOfDatabases=baseDir.list();
  List<String> result = new ArrayList<String>();
  for (String file: arrayOfDatabases){
    File entry = new File(baseDirectoryPath + file);
    if (entry.isDirectory()){
      result.add(file);
    }
  }
  return result;
  }
  public Groundhog getGroundhog(String groundhogName){
        
    Groundhog groundhog = null;
    File file = new File(baseDirectoryPath + groundhogName);

    if( file.isDirectory() ){
      
        try {
          groundhog = new Groundhog(file, defaultCacheSize);
          return groundhog;
        } catch (DatabaseException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        
    }
    return groundhog;
  }
  public Groundhog createNewGroundhog(String groundhogName) throws Exception{
    Groundhog groundhog = null;
    File file = new File(baseDirectoryPath + groundhogName);
    String path = ("path: " + baseDirectoryPath + "/" + groundhogName );
    if( !file.exists()){
        System.out.println("Groundhog directory " + path + " doesn't exist yet");
      if (file.mkdir()){
        groundhog = getGroundhog(groundhogName);
      } else {
          throw new Exception("Can't create groundhog directory " + path);
      }
    } else {
      throw new Exception("Groundhog directory " + path + " exists already, failing");
    }
   return groundhog;
  }
  public boolean removeGroundhog(String groundhogName){
    File file = new File(baseDirectoryPath + groundhogName);
    boolean result = false;
    if (file.exists() && file.isDirectory()){
      result = DirectoryUtilities.deleteDir(file);
     }
    return result;
  }
  
  public void setDefaultCacheSize(int size){
    defaultCacheSize = size;
  }
  
  public int getDefaultCacheSize(){
    return defaultCacheSize;
  }
  
}
