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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class MapUnigene2LLID {
  public static void main(String[] args){
    new MapUnigene2LLID();
  }
  
  public MapUnigene2LLID(){
    System.out.println(StringUtilities.now() + "\tLoading dataset");
    //loadData("/home/schuemie/leiden/Human_genes.txt");
    //loadData("/home/schuemie/leiden/Mouse_genes.txt");
    loadData("/home/schuemie/leiden/top1007targets.txt");
    
    System.out.println(StringUtilities.now() + "\tRetrieving LLIDs");
    loadMapping("/data/UniGene/Hs.data");
    //loadMapping("/data/UniGene/Mm.data");
    
    System.out.println(StringUtilities.now() + "\tMerging");
    //mergeData("/home/schuemie/leiden/Human_genes.txt" , "/home/schuemie/leiden/Human_genes_LLIDs.txt");
    //mergeData("/home/schuemie/leiden/Mouse_genes.txt" , "/home/schuemie/leiden/Mouse_genes_LLIDs.txt");
    mergeData("/home/schuemie/leiden/top1007targets.txt" , "/home/schuemie/leiden/top1007targets_LLIDs.txt");
  }

  private void loadData(String filename) {
    ReadTextFile file = new ReadTextFile(filename);
    Iterator<String> iterator = file.getIterator();
    
    while (iterator.hasNext()){
      String line = iterator.next();
      relevantIDs.add(line.split("\t")[0]); //First column is assumed to be Unigene ID
    }  
  }

  private void loadMapping(String filename) {
    ReadTextFile file = new ReadTextFile(filename);
    Iterator<String> iterator = file.getIterator();
    
    String llid = "";
    while (iterator.hasNext()){
      String line = iterator.next();
      if (line.equals("//"))
        llid = "";
      else if (line.startsWith("LOCUSLINK")){
        llid = line.substring(12);
      } else if (line.startsWith("SEQUENCE    ACC=")){
        String acc = StringUtilities.findBetween(line, "ACC=", ".");
        if (relevantIDs.contains(acc))
          acs2llid.put(acc, llid);
      }
    }
  }
  
  private void mergeData(String filename, String outfilename) {
    ReadTextFile file = new ReadTextFile(filename);
    WriteTextFile outfile = new WriteTextFile(outfilename);
    Iterator<String> iterator = file.getIterator();
    
    while (iterator.hasNext()){
      String line = iterator.next();
      String id = line.split("\t")[0]; //First column is assumed to be Unigene ID
      String llid = acs2llid.get(id);
      if (llid == null)
        llid = "";
      outfile.writeln(llid + "\t" + line); //LLID appended before original string
    }  
    outfile.close();
  }  

  private Map<String, String> acs2llid = new HashMap<String, String>();
  private Set<String> relevantIDs = new HashSet<String>();
}
