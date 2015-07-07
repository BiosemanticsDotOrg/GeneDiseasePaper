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
 * Adds affymetrix IDs to an ontology
 */
package org.erasmusmc.dataimport.genes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.utilities.StringUtilities;

public class Affymetrix {
  public static String libraryFolder = "/home/public/thesauri/affymetrix/";
  
  public Affymetrix(Ontology ontology){   
    fetchAffyIDs();
    insertAffyIDs(ontology);
  }

  private void insertAffyIDs(Ontology ontology) {
    System.out.println(StringUtilities.now()+"\tInserting Affymetrix IDs");
    int count = 0;
    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
    while (conceptIterator.hasNext()){
      int conceptID = conceptIterator.next().getID();
      List<DatabaseID> dblinks = ontology.getDatabaseIDsForConcept(conceptID);
      if (dblinks != null){
        Set<DatabaseID> affyIDs = new HashSet<DatabaseID>();
        for (DatabaseID dblink : dblinks){
          Set<DatabaseID> affyID = dblink2affys.get(dblink);
          if (affyID != null) 
             affyIDs.addAll(affyID);
        }
        for (DatabaseID affyID : affyIDs)
          ontology.setDatabaseIDForConcept(conceptID, affyID);
        count += affyIDs.size();
      }
    }
    System.out.println(StringUtilities.now()+"\tInserted number of ids: "+count);
  }


  private void fetchAffyIDs() {
    File f = new File(libraryFolder);
    String[] filenames = f.list();
    for (String filename : filenames)
      if (filename.toLowerCase().endsWith(".gin")){
        processFile(filename);
      }
    System.out.println("Number of links found: "+dblink2affys.size());
    
  }

  private void processFile(String filename) {
    System.out.println(StringUtilities.now()+"\tNow reading file: "+filename);
    try {
      FileInputStream PSFFile = new FileInputStream(libraryFolder+filename);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(PSFFile),1000000);
      try {
        while (bufferedReader.ready() && !bufferedReader.readLine().startsWith("1")){} //Skip header
          
        while (bufferedReader.ready()){
          processLine(bufferedReader.readLine());
        }
        bufferedReader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }      
    } catch (FileNotFoundException e){
      e.printStackTrace();
    }        
  }

  private void processLine(String string) {
    String affyID = getAffyID(string);
    List<DatabaseID> dblinks = getDatabaseIDs(string);
    if (dblinks.size() != 0){
      DatabaseID affyLink = new DatabaseID("AF", affyID);
      for (DatabaseID dblink : dblinks) {
        Set<DatabaseID> affyIDs = dblink2affys.get(dblink);
        if (affyIDs == null){
          affyIDs = new HashSet<DatabaseID>();
          dblink2affys.put(dblink, affyIDs);
        }
        affyIDs.add(affyLink);
      }
    }
  }

  private List<DatabaseID> getDatabaseIDs(String string) {
    List<DatabaseID> result = new ArrayList<DatabaseID>();
    String UG = getDBID(string, "/UG=");
    if (!UG.equals("")) result.add(new DatabaseID("UG", UG));

    String EG = getDBID(string, "/LL=");
    if (!EG.equals("")) result.add(new DatabaseID("EG", EG));
   
    return result;
  }

  private String getDBID(String string, String prefix) {
    int start = string.indexOf(prefix);
    if (start != -1){
      start += prefix.length();
      int i = start;
      while (i < string.length() && !Character.isWhitespace(string.charAt(i))) i++;
      return string.substring(start, i);
    }
    return "";   
  }

  private String getAffyID(String string) {
    int start = string.indexOf("\t\t\t");
    if (start != -1){
      int end = string.indexOf("\t", start+4);
      return string.substring(start+3, end);
    }
    return "";
  }
  
  //private Map<DatabaseID, DatabaseID> dblink2affy = new HashMap<DatabaseID, DatabaseID>();
  private Map<DatabaseID, Set<DatabaseID>> dblink2affys = new HashMap<DatabaseID, Set<DatabaseID>>();
}
