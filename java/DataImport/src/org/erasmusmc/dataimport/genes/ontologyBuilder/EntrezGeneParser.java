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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class EntrezGeneParser implements DatabaseParser {
  public static boolean filterBadTypes = true;
  private boolean live;
  private boolean genetypeOk;
  private Gene gene ;
  private String dbTag;
  private String llid;
  private GeneList geneList;
  private List<String> discontinued; 
  private Set<Integer> allowedTaxonIDs;
  private boolean geneOrProtein;
  
  @Override
public GeneList parse(String filename, Set<Integer> allowedTaxonIDs) {
    this.allowedTaxonIDs = allowedTaxonIDs;
    geneList = new GeneList();
    live = false;
    genetypeOk = false;
    gene = new Gene(getTag());
    dbTag = "";
    llid  = "";
    discontinued = new ArrayList<String>();
    geneOrProtein = false;
    
    ReadTextFile file = new ReadTextFile(filename);
    Iterator<String> iterator = file.getIterator();
    while(iterator.hasNext()){
      processLine(iterator.next());
    }
    return geneList;
  }


  private void processLine(String line) {
    String trimline = line.trim();
    if (trimline.startsWith("<Gene-track_geneid>")) {
      llid = getValue(trimline);
      gene.ids.add(new DatabaseID("EG",getValue(trimline)));
    }
    if (trimline.startsWith("<Gene-track_status value=") && trimline.contains(("live"))) live = true;
    if (trimline.startsWith("<Entrezgene_type value=") && (trimline.contains(("protein-coding")) || trimline.contains(("unknown")) || trimline.contains(("other")))) genetypeOk = true;
    if (dbTag.equals("taxon") && trimline.startsWith("<Object-id_id>")){
      dbTag = "";
      String taxon = getValue(trimline);
      gene.taxonIDs.add(Integer.parseInt(taxon));
    }
    if (trimline.startsWith("<Dbtag_db>")) dbTag = getValue(trimline);
    
    if (geneOrProtein) {
      if (trimline.startsWith("</Entrezgene_gene>") || trimline.startsWith("</Entrezgene_prot>"))
        geneOrProtein = false;
      else
        extractGeneOrProteinInfo(trimline);
    }
    
    if (trimline.startsWith("<Entrezgene_gene>") || trimline.startsWith("<Entrezgene_prot>"))
      geneOrProtein = true;
 
    if (trimline.startsWith("</Entrezgene>")) {
      if (allowedTaxonIDs.contains(gene.taxonIDs.iterator().next())){
        if (filterBadTypes) filterBadTerms(gene);
        if (live && (genetypeOk || !filterBadTypes) && (gene.symbols.size() != 0)) 
          geneList.add(gene);
        else
          if (!live)
            discontinued.add(llid);
      }
      live = false; 
      genetypeOk = false;
      gene = new Gene(getTag());
    }
  }
  
  private void extractGeneOrProteinInfo(String trimline) {
    if (trimline.startsWith("<Gene-ref_locus>")) {
      gene.preferredSymbol = getValue(trimline);
      gene.symbols.add(getValue(trimline));
    }
    
    if (trimline.startsWith("<Gene-ref_desc>"))
    	if (!(gene.taxonIDs.contains(10090) && StringUtilities.mapToWords(getValue(trimline)).size() < 3) && !gene.taxonIDs.contains(6239))
    	  gene.names.add(getValue(trimline));
    
    if (trimline.startsWith("<Gene-ref_syn_E>")) gene.symbols.add(getValue(trimline));
    if (trimline.startsWith("<Prot-ref_name_E>")) 
    	if (gene.taxonIDs.contains(4932) && StringUtilities.mapToWords(getValue(trimline)).size() > 3)
    		System.out.println("Prot name ignored: " + getValue(trimline)); // In yeast, field is abused for descriptions
    	else
    	  gene.names.add(getValue(trimline));    

    String id = null;
    //if (trimline.startsWith("<Gene-ref_locus-tag>")) id = getValue(trimline);
    if (trimline.startsWith("<Object-id_id>")) id = getValue(trimline);
    if (trimline.startsWith("<Object-id_str>")) id = getValue(trimline);    

    if (id != null){
      if (dbTag.equals("MIM")) gene.ids.add(new DatabaseID("OM", id));
      if (dbTag.equals("HGNC")) gene.ids.add(new DatabaseID("HG", id));
      if (dbTag.equals("GDB")) gene.ids.add(new DatabaseID("GD", id.substring("GDB:".length())));
      if (dbTag.equals("UniGene")) gene.ids.add(new DatabaseID("UG", id));
      if (dbTag.equals("MGI")) gene.ids.add(new DatabaseID("MGI", id));
      if (dbTag.equals("SGD")) gene.ids.add(new DatabaseID("SGD", id));
      if (dbTag.equals("FLYBASE")) gene.ids.add(new DatabaseID("FB", id));
      if (dbTag.equals("UniProt:")) gene.ids.add(new DatabaseID("UP", id));
      if (dbTag.equals("UniProtKB/Swiss-Prot:")) gene.ids.add(new DatabaseID("UP", id));
      if (dbTag.equals("WormBase")) gene.ids.add(new DatabaseID("WB", id));
      if (dbTag.equals("RATMAP")) gene.ids.add(new DatabaseID("RM", id));
      if (dbTag.equals("RGD")) gene.ids.add(new DatabaseID("RGD", id));
      if (dbTag.equals("EcoGene")) gene.ids.add(new DatabaseID("ECO", id));
      if (dbTag.equals("ZFIN")) gene.ids.add(new DatabaseID("ZFIN", id));
      if (dbTag.equals("WormBase")) gene.ids.add(new DatabaseID("WB", id));
    }
  }


  private void filterBadTerms(Gene geneInfo) {
    Iterator<String> symbolIterator = geneInfo.symbols.iterator();
    boolean hasGoodSymbol = false;
    while (symbolIterator.hasNext()){
      String symbol = symbolIterator.next();
      if (!symbol.startsWith("LOC")) hasGoodSymbol = true;
    }
    Iterator<String> nameIterator = geneInfo.names.iterator();
    while (nameIterator.hasNext()){
      String name = nameIterator.next().toLowerCase();
      if (name.contains("similar") || name.contains("putative") || name.contains("hypothetical") || name.contains("predicted") || name.contains("uncharacterized") || name.contains("conserved") || name.contains("expressed") || name.contains("deletion") || name.contains("duplication"))
        nameIterator.remove();
    }
    if (!hasGoodSymbol && geneInfo.names.size() == 0)
      geneInfo.symbols.clear();    
  }


  private String getValue(String line) {
    int x = line.indexOf(">");
    int y = line.lastIndexOf("<");
    return line.substring(x+1,y);
  }
  @Override
public String getTag() {
    return "EG";
  }
}
