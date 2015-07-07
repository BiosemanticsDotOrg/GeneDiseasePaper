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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.WriteTextFile;

public class SwissProtOrganismFilter {
  public static void main(String[] args){
    Set<String> taxons = new HashSet<String>();
    taxons.add("9031");
    filterSwissProt("/home/data/Swiss-Prot/uniprot_sprot.dat", "/home/data/Swiss-Prot/worm.txt", taxons);
  }
  
  public static void filterSwissProt(String source, String target, Set<String> taxons){
    ReadTextFile in = new ReadTextFile(source);
    WriteTextFile out = new WriteTextFile(target);
    List<String> record = new ArrayList<String>();
    boolean include = false;
    for (String line : in){
      record.add(line);
      if (line.startsWith("OX   NCBI_TaxID=")){
        String taxID = line.substring(16, line.length()-1);
        include = (taxons.contains(taxID));
      }
      
      if (line.startsWith("//")){
        if (include)
          for (String recordLine: record)
            out.writeln(recordLine);
        record.clear();
        include = false;
      }
    }
    out.close();
  }
}
