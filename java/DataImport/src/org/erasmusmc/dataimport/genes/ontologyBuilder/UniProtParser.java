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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;


public class UniProtParser implements DatabaseParser {

  private GeneList geneList;  
  private Set<Integer> taxonIDs;
  private static final Pattern digitsDotsSlashesPattern = Pattern.compile("^[0-9\\-\\\\/\\.]+$");

  public static void main(String[] args){
    UniProtParser parser = new UniProtParser();
    Set<Integer> taxonIDs = new HashSet<Integer>();
    taxonIDs.add(9606);
    GeneList geneList = parser.parse("/home/data/Swiss-Prot/human.txt", taxonIDs);
    geneList.saveToSimpleFile("/home/temp/uniprot.txt");
  }
  
  @Override
public GeneList parse(String filename, Set<Integer> allowedTaxonIDs) {
    geneList = new GeneList();
    this.taxonIDs = allowedTaxonIDs;
    
    processFile(filename);
    
    return geneList;
  }
  
  private void processFile(String filename) {
    Gene gene = new Gene(getTag());
    for (String line : new ReadTextFile(filename)){
      String prefix = line.substring(0,2);
      String content;
      if (line.length() > 5)
        content = line.substring(5);
      else
        content = "";
      
      if (prefix.equals("ID")){
        String symbol = content.substring(0,content.indexOf('_')).trim();
        if (isValidTerm(symbol)){
          gene.symbols.add(symbol);
          gene.preferredSymbol = symbol;
        }
        
      } else if (prefix.equals("AC")){
        for (String upid : content.split(";"))
          if (upid.length() != 0)
            gene.ids.add(new DatabaseID("UP",upid.trim()));
        
      } else if (prefix.equals("DE")){
        String fullname = StringUtilities.findBetween(content, "Full=", ";").trim();
        if (fullname.length() != 0 && isValidTerm(fullname))
          gene.names.add(fullname);
        
        String symbol = StringUtilities.findBetween(content, "Short=", ";").trim();
        if (symbol.length() != 0 && isValidTerm(symbol))
          gene.symbols.add(symbol);
        
      } else if (prefix.equals("GN")){
        String geneSymbol = StringUtilities.findBetween(content, "Name=", ";").trim();
        if (isValidTerm(geneSymbol)){
          gene.preferredSymbol = geneSymbol; //overrides protein symbol
          gene.symbols.add(geneSymbol);
        }
        String synonyms = StringUtilities.findBetween(content, "Synonyms=", ";");
        for (String synonym : synonyms.split(","))
          if (isValidTerm(synonym))
            gene.symbols.add(synonym.trim());
        
      } else if (prefix.equals("OX")){
        String taxonID = StringUtilities.findBetween(content, "NCBI_TaxID=", ";");
        gene.taxonIDs.add(Integer.parseInt(taxonID));
        
      } else if (prefix.equals("DR")){
        String egID = StringUtilities.findBetween(content, "GeneID; ", ";");
        if (egID.length() != 0)
          gene.ids.add(new DatabaseID("EG", egID));
        
        String hgncID = StringUtilities.findBetween(content, "HGNC; HGNC:", ";");
        if (hgncID.length() != 0)
          gene.ids.add(new DatabaseID("HG", hgncID));
        
        String omimID = StringUtilities.findBetween(content, "MIM; ", ";");
        if (omimID.length() != 0 && content.contains("gene"))
          gene.ids.add(new DatabaseID("OM", omimID));

        String uniGeneID = StringUtilities.findBetween(content, "UniGene; ", ";");
        if (uniGeneID.length() != 0)
          gene.ids.add(new DatabaseID("UG", uniGeneID));
        
        String ecoID = StringUtilities.findBetween(content, "EcoGene; ", ";");
        if (ecoID.length() != 0)
          gene.ids.add(new DatabaseID("ECO", ecoID));
        
        String sgdID = StringUtilities.findBetween(content, "SGD; ", ";");
        if (sgdID.length() != 0)
          gene.ids.add(new DatabaseID("SGD", sgdID));
        
        String mgiID = StringUtilities.findBetween(content, "MGI; MGI:", ";");
        if (mgiID.length() != 0)
          gene.ids.add(new DatabaseID("MGI", mgiID));
        
        String rgdID = StringUtilities.findBetween(content, "RGD; ", ";");
        if (rgdID.length() != 0)
          gene.ids.add(new DatabaseID("RGD", rgdID));
        
        String flybaseID = StringUtilities.findBetween(content, "FlyBase; ", ";");
        if (flybaseID.length() != 0)
          gene.ids.add(new DatabaseID("FB", flybaseID));
        
        String zfinID = StringUtilities.findBetween(content, "ZFIN; ", ";");
        if (zfinID.length() != 0)
          gene.ids.add(new DatabaseID("ZFIN", zfinID));

        String wormbaseID = StringUtilities.findBetween(content, "WormBase; ", ";");
        if (wormbaseID.length() != 0)
          gene.ids.add(new DatabaseID("WB", wormbaseID));
        
      } else if (prefix.equals("//")){
        if (taxonIDs.contains(gene.taxonIDs.iterator().next()))
          geneList.add(gene);
        gene = new Gene(getTag());
      }
    }
  }

  @Override
public String getTag() {
    return "UP";
  }
  
  private boolean isValidTerm(String term) {
    term = term.trim().toLowerCase();
    if ((term.length()<3) || (digitsDotsSlashesPattern.matcher(term).find())) 
      return false;     
 
    if (term.contains("similar") || term.contains("putative") || term.contains("hypothetical") || term.contains("predicted") || term.contains("uncharacterized"))
      return false;
    
    return true;      
  }
}

