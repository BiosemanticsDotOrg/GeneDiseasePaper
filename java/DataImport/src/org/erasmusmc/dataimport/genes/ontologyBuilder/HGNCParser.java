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

public class HGNCParser implements DatabaseParser {

  private GeneList geneList;  
  private Set<Integer> taxonIDs;
  private static final Pattern digitsDotsSlashesPattern = Pattern.compile("^[0-9\\-\\\\/\\.]+$");
  private static final Pattern numberPattern = Pattern.compile("^[0-9]+$");


  public static void main(String[] args){
    Set<Integer> taxonIDs = new HashSet<Integer>();
    taxonIDs.add(9606);
    HGNCParser parser = new HGNCParser();
    GeneList geneList = parser.parse("/data/HUGO/alldata.txt" , taxonIDs);
    geneList.printStatistics();
    geneList.saveToSimpleFile("/home/temp/HGNC.txt");
  }

  @Override
public String getTag() {
    return "HG";
  }

  @Override
public GeneList parse(String filename, Set<Integer> allowedTaxonIDs) {

    geneList = new GeneList();
    this.taxonIDs = allowedTaxonIDs;

    processFile(filename);

    return geneList;
  }

  private void processFile(String filename) {
    
    String id;
    Boolean firstLine = true;
    for (String line : new ReadTextFile(filename)){
      if (firstLine) {
        firstLine = false;
        continue;                   // skip header line
      }
      Gene gene = new Gene(getTag());

      gene.taxonIDs.add(9606);

      String[] fields = line.split("\t");
      String status = fields[3];
      if (fields.length < 35 || !status.equalsIgnoreCase("approved"))
        continue;

      String hgid = fields[0].trim();
      String preferredSymbol = fields[1].trim();
      String preferredName = fields[2].trim();
      String symbols = fields[7].trim();
      String names = fields[8].trim();
      String gdbid = fields[31].trim();
      String egid = fields[32].trim();
      String omid = fields[33].trim();
      String upid = fields[34].trim();

      if (isValidTerm(preferredSymbol)) {
        gene.preferredSymbol = preferredSymbol;
        gene.symbols.add(preferredSymbol);
      }

      for (String symbol : safeSplit(symbols)) {
        if (isValidTerm(symbol) && !symbol.startsWith("LOC"))
          gene.symbols.add(symbol);
      }
      if (isValidTerm(preferredName))
        gene.names.add(preferredName);
      for (String name : safeSplit(names)) {
        if (isValidTerm(name))
          gene.names.add(name);
      }
      
      id = hgid.replaceFirst("HGNC:", "");
      if (numberPattern.matcher(id).matches()) 
        gene.ids.add(new DatabaseID("HG", id));
      if (numberPattern.matcher(egid).matches()) 
        gene.ids.add(new DatabaseID("EG", egid));
      id = gdbid.replaceFirst("GDB:", "");
      if (numberPattern.matcher(id).matches()) 
        gene.ids.add(new DatabaseID("GD", id));
      if (numberPattern.matcher(omid).matches()) 
        gene.ids.add(new DatabaseID("OM", omid));
      if (upid.length() != 0) 
        gene.ids.add(new DatabaseID("UP", upid));
      
      if (taxonIDs.contains(gene.taxonIDs.iterator().next()))
        geneList.add(gene);
    }
  }

  private String[] safeSplit(String line){
    List<String> result = new ArrayList<String>();
    StringBuilder sb = new StringBuilder();
    int open = 0;
    for (int i = 0; i < line.length(); i++){
      char ch = line.charAt(i);
      if (ch == '"')
        open = (open == 0 ? open++ : open--);
      else if (ch == ',') {
        if (open == 0){
          result.add(sb.toString().trim());
          sb = new StringBuilder();
        }
        else 
          sb.append(ch);  
      } 
      else
        sb.append(ch);
    }
    result.add(sb.toString().trim());
    return result.toArray(new String[result.size()]);
  }
  
  private boolean isValidTerm(String term) {
    term = term.trim().toLowerCase();
    if ((term.length() < 3) || (digitsDotsSlashesPattern.matcher(term).find())) 
      return false;     

//  if (term.contains("similar") || term.contains("putative") || term.contains("hypothetical") || term.contains("predicted") || term.contains("uncharacterized"))
//  return false;

    return true;      
  }
}

