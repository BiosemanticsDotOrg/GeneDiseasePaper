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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.erasmusmc.collections.CountingSet;

public class CID2PMID extends TreeMap<Integer, Set<Integer>>{
  public void put(Integer cid, Integer pmid){
    Set<Integer> pmids = get(cid);
    if (pmids == null){
      pmids = new TreeSet<Integer>();
      put(cid, pmids);
    }
    pmids.add(pmid);
  }
  
  public int filter(int maxCIDsPerPMID){
    //Count number of occurrences of PMIDs:
    CountingSet<Integer> allPMIDs = new CountingSet<Integer>();
    for (Set<Integer> pmids : values())
      for (Integer pmid : pmids)
        allPMIDs.add(pmid);
    
    //Select PMIDs for removal:
    Iterator<Map.Entry<Integer, CountingSet.Count>> iterator = allPMIDs.key2count.entrySet().iterator();
    while (iterator.hasNext())
      if (iterator.next().getValue().count <= maxCIDsPerPMID)
        iterator.remove();
    
    //Remove PMIDs:
    removedRefCount = 0;
    Iterator<Map.Entry<Integer, Set<Integer>>> entryIterator = entrySet().iterator();
    while (entryIterator.hasNext()){
      Set<Integer> pmids = entryIterator.next().getValue();
      Iterator<Integer> refIterator = pmids.iterator();
      while (refIterator.hasNext())
        if (allPMIDs.contains(refIterator.next())){
          refIterator.remove();
          removedRefCount++;
        }  
      if (pmids.size() == 0) entryIterator.remove();
    }    
    return allPMIDs.size();
  }
  
  public int removedRefCount = 0; 
  
  public void saveToFile(String filename){
    try { 
      FileOutputStream PSFFile = new FileOutputStream(filename);
      BufferedWriter bufferedWrite = new BufferedWriter( new OutputStreamWriter(PSFFile),1000000);
      try {
        for (Map.Entry<Integer, Set<Integer>> entry : entrySet()){
          StringBuffer line = new StringBuffer();
          line.append(entry.getKey());
          line.append("\t");
          Iterator<Integer> pmidIterator = entry.getValue().iterator();
          while (pmidIterator.hasNext()){
            line.append(pmidIterator.next());
            if (pmidIterator.hasNext()) line.append("\t");
          }
          
          bufferedWrite.write(line.toString());  
          bufferedWrite.newLine();
        }
        bufferedWrite.flush();
        bufferedWrite.close();
      }catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e){
      e.printStackTrace(); 
    }     
  }
 
  private static final long serialVersionUID = 1L;

}
