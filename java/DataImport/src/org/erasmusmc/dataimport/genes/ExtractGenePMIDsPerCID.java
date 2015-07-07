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

package org.erasmusmc.dataimport.genes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;

public class ExtractGenePMIDsPerCID {
  
  public static void main(String[] args){
    new ExtractGenePMIDsPerCID();
  }
  
  public ExtractGenePMIDsPerCID(){
    System.out.println(StringUtilities.now()+"\tFetching ontology"); 
    OntologyManager manager = new OntologyManager();
    ontology =  manager.fetchStoreFromDatabase("Homologene_curated_min3_090107");
    
    System.out.println(StringUtilities.now()+"\tCreating databaseID index"); 
    ((OntologyStore) ontology).createIndexForDatabaseIDs();
    
    System.out.println(StringUtilities.now()+"\tProcessing Entrez-Gene PMID file");    
    processEGFile("C:/Data/Entrez-Genes/gene2pubmed");

    System.out.println(StringUtilities.now()+"\tProcessing other PMID files");    
    //processFile("C:/home/public/Thesauri/GenesNonHuman/PMID2RGD.txt");
    //processFile("C:/home/public/Thesauri/GenesNonHuman/PMID2MGD.txt");
    
    System.out.println(StringUtilities.now()+"\tFiltering");
    System.out.println("Removed "+cid2PMID.filter(25)+" PMIDs");
    System.out.println("Removed "+cid2PMID.removedRefCount+" reference");
    
    System.out.println(StringUtilities.now()+"\tSaving to file");
    cid2PMID.saveToFile("/temp/cid2pmid.txt");
  }
  
  private void processEGFile(String filename) {
    int count = 0;
    try {
      FileInputStream PSFFile = new FileInputStream(filename);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(PSFFile),1000000);
      try {
        bufferedReader.readLine(); //skip first line
        while (bufferedReader.ready()){
          processEGLine(bufferedReader.readLine());
          count++;
          if (count % 100000 == 0) System.out.println(count);
        }
        bufferedReader.close();
      } catch (IOException e) {
        e.printStackTrace();
      }      
    } catch (FileNotFoundException e){
      e.printStackTrace();
    }    
  }
  
  private void processEGLine(String string) {
    String[] cols = string.split("\t");
    //if (cols[0].equals("9606") || cols[0].equals("10090") ||cols[0].equals("10116")){
      DatabaseID databaseID = new DatabaseID("LL", cols[1]);
      Set<Integer> cids = ontology.getConceptIDs(databaseID);
      if (cids != null)
        for (Integer cid : cids)
          cid2PMID.put(cid, Integer.parseInt(cols[2]));
    //}
  }
  
  private void processFile(String filename) {
    System.out.println(StringUtilities.now()+"\tNow processing " + filename);   
    int count = 0;
    try {
      FileInputStream PSFFile = new FileInputStream(filename);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(PSFFile),1000000);
      try {
        while (bufferedReader.ready()){
          processLine(bufferedReader.readLine());
          count++;
          if (count % 10000 == 0) System.out.println(count);          
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
    String[] cols = string.split("=");
    String[] components = cols[1].split("_");
    DatabaseID databaseID = new DatabaseID(components[0], components[1]);
    Set<Integer> cids = ontology.getConceptIDs(databaseID);
    if (cids != null)
      for (Integer cid : cids)
        cid2PMID.put(cid, Integer.parseInt(cols[0]));     
  }  
  
  private CID2PMID cid2PMID = new CID2PMID();
  private Ontology ontology;
}