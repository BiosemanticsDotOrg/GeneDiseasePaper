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

package org.erasmusmc.dataimport.Medline;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class GeneratePMID2XMLFile{

  public static String dir = "/home/data/Medline/2010/";
  public static String outputFile = "/home/temp/pmid2xml.txt";
  public static int maxRetries = 3;
  
  public static void main (String[] args) throws Exception {
    new GeneratePMID2XMLFile(args);
  }
  
  public GeneratePMID2XMLFile(String[] args)  throws Exception {
    /*if (args.length == 0){
      System.out.println("usage: arg1 = folder, arg2 = outputfile");
      return;
    } else {
      dir = args[0];
      outputFile = args[1];
    }*/
    File directory = new File(dir);
    List<File> fileList = getFileListing(directory);
    WriteTextFile out = new WriteTextFile(outputFile);
    boolean cont = false;
    Iterator<File> filesIter = fileList.iterator();
    while (filesIter.hasNext()) {
      String fn = filesIter.next().getAbsolutePath();
     //Use these lines if you don't want to start at the beginning:
      //if (fn.contains("0700"))
      //  cont = true;
      //if (cont)
      if (fn.endsWith(".gz")) {
        GZIPInputStream input = new GZIPInputStream(new FileInputStream(fn));
        FileOutputStream output = new FileOutputStream(fn.substring(0, fn.length() - 3));
        copyStream(input,output);
        output.close();
        File del = new File(fn.substring(0, fn.length() - 3));
        process(out, del.getAbsolutePath(), del.getName());
        
        int retries = 0;
        while (!del.delete() && retries < maxRetries){
          System.err.println("Unable to delete " + del.getAbsolutePath() + ", retrying....");
          Thread.sleep(1000);
          retries++;
        }
      }
    }
    out.close();
  }
  
  private static void process(WriteTextFile out, String fullpath, String filename) {
    System.out.println("Processing " + fullpath);
    ReadTextFile in = new ReadTextFile(fullpath);
    for (String line : in){
      String pmid = StringUtilities.findBetween(line, "<PMID>", "</PMID>");
      if (!pmid.equals(""))
        out.writeln(pmid + "\t" + filename);  
    }
    
  }

  static private List<File> getFileListing(File aStartingDir) throws FileNotFoundException {
    validateDirectory(aStartingDir);
    List<File> result = new ArrayList<File>();

    File[] filesAndDirs = aStartingDir.listFiles();
    List<File> filesDirs = Arrays.asList(filesAndDirs);
    Iterator<File> filesIter = filesDirs.iterator();
    File file = null;
    while (filesIter.hasNext()) {
      file = filesIter.next();
      result.add(file); //always add, even if directory
      if (!file.isFile()) {
        //must be a directory
        //recursive call!
        List<File> deeperList = getFileListing(file);
        result.addAll(deeperList);
      }

    }
    Collections.sort(result);
    return result;
  }
  
  static private void validateDirectory(File aDirectory) throws FileNotFoundException {
    if (aDirectory == null) {
      throw new IllegalArgumentException("Directory should not be null.");
    }
    if (!aDirectory.exists()) {
      throw new FileNotFoundException("Directory does not exist: " + aDirectory);
    }
    if (!aDirectory.isDirectory()) {
      throw new IllegalArgumentException("Is not a directory: " + aDirectory);
    }
    if (!aDirectory.canRead()) {
      throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
    }
  }
  
  private static final void copyStream(InputStream source, OutputStream dest){
    int bufferSize = 1024;
    int bytes;
    byte[] buffer;
    buffer = new byte[bufferSize];
    try {  
      while ((bytes = source.read(buffer)) != -1) {
        if (bytes == 0) {
          bytes = source.read();
          if (bytes < 0)
            break;
          dest.write(bytes);
          dest.flush();
          continue;
        }
        dest.write(buffer, 0, bytes);
        dest.flush();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }  
  }
}

