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

package org.erasmusmc.dataimport.genes.ontologyBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.utilities.ReadTextFile;

public class OMIMParser implements DatabaseParser {

  private GeneList geneList;  
  private Set<Integer> taxonIDs;
  private static final Pattern digitsDotsSlashesPattern = Pattern.compile("^[0-9\\-\\\\/\\.]+$");
  private static final Pattern numberPattern = Pattern.compile("^[0-9]+$");
  

  public static void main(String[] args){
    Set<Integer> taxonIDs = new HashSet<Integer>();
    taxonIDs.add(9606);
    OMIMParser parser = new OMIMParser();
    GeneList geneList = parser.parse("/data/OMIM/genemap.txt" , taxonIDs);
    geneList.printStatistics();
    geneList.saveToSimpleFile("/home/temp/OMIM.txt");
  }
  
  @Override
public String getTag() {
    return "OM";
  }

  @Override
public GeneList parse(String filename, Set<Integer> allowedTaxonIDs) {
    
    geneList = new GeneList();
    this.taxonIDs = allowedTaxonIDs;
    
    processFile(filename);
    
    return geneList;
  }

  private void processFile(String filename) {
    for (String line : new ReadTextFile(filename)){
      Gene gene = new Gene(getTag());
      
      gene.taxonIDs.add(9606);
      
      String[] fields = safeSplit(line);
      if (fields.length < 10)
        continue;
      String omid = fields[9].trim();
      String symbols = fields[5];
      String fullname = fields[7].trim();
      if (numberPattern.matcher(omid).matches()){
      //if (StringUtilities.isNumber(omid)) {
        gene.ids.add(new DatabaseID("OM", omid.trim()));
        if (fullname.length() != 0 && isValidTerm(fullname))
          gene.names.add(fullname);
        for (String symbol : symbols.split(" *[,.] +")) {
          if (symbol.length() != 0 && isValidTerm(symbol))
            gene.symbols.add(symbol);
        }
        if (taxonIDs.contains(gene.taxonIDs.iterator().next()))
          geneList.add(gene);
      }
    }
  }
  
  private String[] safeSplit(String line){
    List<String> result = new ArrayList<String>();
    StringBuilder sb = new StringBuilder();
    int open = 0;
    for (int i = 0; i < line.length(); i++){
      char ch = line.charAt(i);
      if (ch == '(')
        open++;
      else if (ch == ')')
        open--;
      if (ch == '|'){
        if (open == 0){
          result.add(sb.toString());
          sb = new StringBuilder();
        } else 
          sb.append(' ');  
      } else
        sb.append(ch);
    }
    result.add(sb.toString());
    return result.toArray(new String[result.size()]);
  }
       
            
  private boolean isValidTerm(String term) {
    term = term.trim().toLowerCase();
    if ((term.length() < 3) || (digitsDotsSlashesPattern.matcher(term).find())) 
      return false;     
     
//    if (term.contains("similar") || term.contains("putative") || term.contains("hypothetical") || term.contains("predicted") || term.contains("uncharacterized"))
//      return false;
          
    return true;      
  }
}
