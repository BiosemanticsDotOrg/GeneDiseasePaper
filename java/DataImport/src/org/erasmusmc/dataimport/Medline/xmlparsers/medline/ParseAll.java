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

/*
 * @author Ariel Schwartz
 * @author Gaurav Bhalotia
 */

package org.erasmusmc.dataimport.Medline.xmlparsers.medline;

import java.io.BufferedOutputStream;
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
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.erasmusmc.dataimport.Medline.xmlparsers.GenericXMLParser;

/**
 * This class the parses a medline document, extends the generic xml parser
 */
public class ParseAll extends GenericXMLParser {

  private static String dir = null;
  public static String server;
  public static String database;
  public static String port;
  public static String username;
  public static String password;

  protected static void usage() {
    System.err.println("Usage: [-server <servername>][-database <databasename>][-port <portnumber>][-user <username>][-password <password>] <dir containing medline.xml files>");
    System.err.println("\tIf the extension is .gz it will unzip the file, afterwards deletes the unzipped file.");
    System.err.println("\tAlso check out the README.txt in the org.erasmusmc.dataimport.Medline package");
    System.err.println("\t-usage or -help displays this message");
    System.exit(1);
  }

  static public void main(String[] args) throws Exception {
    readParameters(args);
      File directory = new File(dir);
      List<File> fileList = getFileListing(directory);
      boolean cont = false;
      Iterator<File> filesIter = fileList.iterator();
      while (filesIter.hasNext()) {
        String fn = filesIter.next().getAbsolutePath();
        //Use these lines if you don't want to start at the beginning:
        //if (fn.contains("0769"))
         // cont = true;
        //if (cont)
        if (fn.endsWith(".gz")) {
          copyStream(new GZIPInputStream(new FileInputStream(fn)), new FileOutputStream(fn.substring(0, fn.length() - 3)));
          MedlineParser.main(new String[] { "-validate", fn.substring(0, fn.length() - 3) });
          File del = new File(fn.substring(0, fn.length() - 3));
          del.delete();
        }
      }
    
  }

  private static void readParameters(String[] args) {
    int i = 0;
    while (i < args.length){
      if (args[i].equals("-usage")) {
        usage();
      } else if (args[i].equals("-help")) {
        usage();
      } else if (args[i].equals("-server")) {
        i++;
        server = args[i];
      } else if (args[i].equals("-database")) {
        i++;
        database = args[i];
      } else if (args[i].equals("-port")) {
        i++;
        port = args[i];
      } else if (args[i].equals("-user")) {
        i++;
        username = args[i];
      } else if (args[i].equals("-password")) {
        i++;
        password = args[i];
      } else {
        dir = args[i];
        if (i != args.length - 1) {
          usage();
        }
      }
      i++;
    }
    if (dir == null) {
      usage();
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

  /**
   * Directory is valid if it exists, does not represent a file, and can be read.
   */
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

  public static final void copyInputStream(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int len;

    while ((len = in.read(buffer)) >= 0)
      out.write(buffer, 0, len);

    in.close();
    out.close();
  }

  static private String[] Unzip(String zippedfile) throws IOException {
    Enumeration entries;
    ZipFile zipFile;
    zipFile = new ZipFile(zippedfile);
    String[] result = new String[] {};

    entries = zipFile.entries();

    while (entries.hasMoreElements()) {
      ZipEntry entry = (ZipEntry) entries.nextElement();

      if (entry.isDirectory()) {
        continue;
      }

      System.err.println("Extracting file: " + entry.getName());
      copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(dir + "/" + entry.getName())));
      String file = entry.getName();
      File delfile = new File(file);
      delfile.deleteOnExit();
      String[] newResult = new String[result.length + 1];
      System.arraycopy(result, 0, newResult, 0, result.length);
      newResult[result.length] = dir + "/" + file;
      result = newResult;
    }
    return result;
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
