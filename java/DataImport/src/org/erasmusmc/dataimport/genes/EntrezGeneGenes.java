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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.textMining.LVG.LVGNormaliser;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

/**
 * Extracts information from an Entrez-Gene XML file. 
 * 
 * @author Schuemie
 *
 */public class EntrezGeneGenes {
  public boolean filterBadTypes = true;
  
  public static void main(String[] args){
    EntrezGeneGenes egg = new EntrezGeneGenes();
    egg.run();
  }
  
  public void run(){  
    System.out.println(StringUtilities.now()+"\tLoading normaliser cache");
    normaliser.loadCacheBinary("/home/public/Peregrine/standardNormCache2006.bin");
    
    System.out.println(StringUtilities.now()+"\tProcessing file");    

    processFile("/home/data/Entrez-gene/Homo_sapiens.xml");
    
    System.out.println(StringUtilities.now()+"\tDumping output");
    //dumpOutputToPSFFormat("/home/schuemie/EG_Rat.psf");
    dumpOutputToJansFormat("/home/data/geneprotein_EG_norm.txt");
    //dumpOutputToSimpleFormat("/home/schuemie/EntrezGeneSimple.txt");
    //TextFileUtilities.saveToFile(discontinued, "/home/schuemie/WithdrawnIDs_Fly.txt");
    //System.out.println(discontinued.size() + " genes written to " + "/home/schuemie/WithdrawnIDs.txt");
  }
  
  private void dumpOutputToPSFFormat(String filename) {
    OntologyStore ontology = new OntologyStore();
    for (GeneInfo geneInfo : geneInfos){
      Concept concept = new Concept(Integer.parseInt(getID(geneInfo, "LL_")));
      List<TermStore> terms = new ArrayList<TermStore>();
      for (String symbol : geneInfo.symbols){
        terms.add(new TermStore(symbol));
      }
      for (String name : geneInfo.names){
        terms.add(new TermStore(name));
      }
      for (String id : geneInfo.ids){
        String[] idparts = id.split("_");
        if (idparts.length < 2)
          System.out.println(id);
        else
          ontology.setDatabaseIDForConcept(concept.getID(), new DatabaseID(idparts[0], idparts[1]));
      }
      concept.setTerms(terms);
      ontology.setConcept(concept);
    }
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = ontology;
    loader.saveToPSF(filename);
    
  }

  private void dumpOutputToSimpleFormat(String filename) {
    try { 
      FileOutputStream PSFFile = new FileOutputStream(filename);
      BufferedWriter bufferedWrite = new BufferedWriter( new OutputStreamWriter(PSFFile),1000000);
      try {
        for (GeneInfo geneInfo : geneInfos){
          bufferedWrite.write(info2simpleline(geneInfo));  
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

  private String info2simpleline(GeneInfo geneInfo) {
    StringBuffer line = new StringBuffer();
    line.append(geneInfo.symbols.get(0)); // Preferred symbol
    
    for (int i = 1; i < geneInfo.symbols.size(); i++){ //Other symbols
      line.append(";");
      line.append(geneInfo.symbols.get(i));
    }      
    line.append("\t");
    boolean first = true;
    for (String name : geneInfo.names){ //Gene names
      if (first) first = false; else line.append(";");
      line.append(name);
    }      
    line.append("\t");
    line.append(getID(geneInfo, "LL_"));
    return line.toString();
  }

  private void dumpOutputToJansFormat(String filename) {
    try { 
      FileOutputStream PSFFile = new FileOutputStream(filename);
      BufferedWriter bufferedWrite = new BufferedWriter( new OutputStreamWriter(PSFFile),1000000);
      try {
        for (GeneInfo geneInfo : geneInfos){
          bufferedWrite.write(info2line(geneInfo));  
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
    System.out.println(geneInfos.size() + " genes written to " + filename);
  }

  private String info2line(GeneInfo geneInfo) {
    StringBuffer line = new StringBuffer();
    line.append(getID (geneInfo, "OM_")); line.append("|");
    line.append(getID (geneInfo, "GD_")); line.append("|");
    line.append(getID (geneInfo, "LL_")); line.append("|");
    line.append(getID (geneInfo, "RQ_")); line.append("|");
    line.append(getID (geneInfo, "UG_")); line.append("|");
    line.append(getID (geneInfo, "SP_")); line.append("|");
    line.append(getID (geneInfo, "HG_")); line.append("|");
    line.append(geneInfo.symbols.get(0)); line.append("|"); // Preferred symbol
    
    for (int i = 1; i < geneInfo.symbols.size(); i++){ //Other symbols
      if (i != 1) line.append("\t");
      line.append(geneInfo.symbols.get(i));
    }  
    line.append("|");
    boolean first = true;
    for (String name : geneInfo.names){ //Normalised gene names
      if (first) first = false; else line.append("\t");
      line.append(normaliser.normalise(name));
    }  
    line.append("|");   
    
    first = true;
    for (String name : geneInfo.names){ //Gene names
      if (first) first = false; else line.append("\t");
      line.append(name);
    }  
    
    return line.toString();
  }
  
  private String getID(GeneInfo geneInfo, String prefix){
    StringBuffer result = new StringBuffer();
    Set<String> unique = new HashSet<String>();
    for (String id : geneInfo.ids)
      if (id.startsWith(prefix)) unique.add(id);
    
    for (String id : unique)  {
      if (result.length() != 0) result.append("\t");
      result.append(id.replace(prefix,""));
    }
    
    return result.toString();
  }

  private void processFile(String filename) {
    ReadTextFile file = new ReadTextFile(filename);
    Iterator<String> iterator = file.getIterator();
    while(iterator.hasNext()){
      processLine(iterator.next());
    }
  }
  
  private boolean live = false;
  private boolean genetypeOk = false;
  private GeneInfo currentGeneInfo = new GeneInfo();
  private String dbTag = "";
  private String llid = "";
  private void processLine(String line) {
    if (line.toLowerCase().contains("</entrezgene>")) {
      count++;
      if (count % 100 == 0) System.out.println(count + " genes analysed.");
    }    
    
    
    
    String trimline = line.trim();
    if (trimline.startsWith("<Gene-track_geneid>")) {
      llid = getValue(trimline);
      currentGeneInfo.ids.add("LL_"+getValue(trimline));
    }
    if (trimline.startsWith("<Gene-track_status value=") && trimline.contains(("live"))) live = true;
    if (trimline.startsWith("<Entrezgene_type value=") && (trimline.contains(("protein-coding")) || trimline.contains(("unknown")) || trimline.contains(("other")))) genetypeOk = true;
    
   
    if (trimline.startsWith("<Gene-ref_locus>")) currentGeneInfo.symbols.add(getValue(trimline));
    if (trimline.startsWith("<Gene-ref_desc>")) currentGeneInfo.names.add(getValue(trimline));
    if (trimline.startsWith("<Gene-ref_syn_E>")) currentGeneInfo.symbols.add(getValue(trimline));
    if (trimline.startsWith("<Prot-ref_name_E>")) currentGeneInfo.names.add(getValue(trimline));    
    
    String id = "";
    if (trimline.startsWith("<Gene-ref_locus-tag>")) id = getValue(trimline);
    if (trimline.startsWith("<Dbtag_db>")) dbTag = getValue(trimline);
    if (trimline.startsWith("<Object-id_id>")) id = dbTag + ":" + getValue(trimline);
    if (trimline.startsWith("<Object-id_str>")) id = dbTag + ":" + getValue(trimline);    
    
    if (!id.equals("")){
      if (id.startsWith("MIM")) currentGeneInfo.ids.add(id.replace("MIM:", "OM_"));
      if (id.startsWith("HGNC")) currentGeneInfo.ids.add(id.replace("HGNC:", "HG_"));
      if (id.startsWith("GDB")) currentGeneInfo.ids.add(id.replace("GDB:GDB:", "GD_"));
      if (id.startsWith("UniGene")) currentGeneInfo.ids.add(id.replace("UniGene:", "UG_"));
      if (id.startsWith("MGI")) currentGeneInfo.ids.add(id.replace("MGI:", "MGI_"));
      if (id.startsWith("SGD")) currentGeneInfo.ids.add(id.replace("SGD:", "SGD_"));
      if (id.startsWith("FLYBASE")) currentGeneInfo.ids.add(id.replace("FLYBASE:", "FB_"));      
      if (id.startsWith("UniProt:")) currentGeneInfo.ids.add(id.replace("UniProt:", "SP_"));  
      if (id.startsWith("UniProtKB/Swiss-Prot:")) currentGeneInfo.ids.add(id.replace("UniProtKB/Swiss-Prot:", "SP_"));
      if (id.startsWith("WormBase")) currentGeneInfo.ids.add(id.replace("WormBase:", "WB_"));
      if (id.startsWith("RATMAP")) currentGeneInfo.ids.add(id.replace("RATMAP:", "RM_"));
      if (id.startsWith("RGD")) currentGeneInfo.ids.add(id.replace("RGD:", "RGD_"));
    }

    if (trimline.startsWith("</Entrezgene>")) {
      if (filterBadTypes) filterBadTerms(currentGeneInfo);
      if (live && (genetypeOk || !filterBadTypes) && (currentGeneInfo.symbols.size() != 0)) 
        geneInfos.add(currentGeneInfo);
      else
        if (!live)
          discontinued.add(llid);
        
      live = false; genetypeOk = false;
      currentGeneInfo = new GeneInfo();
    }
  }
  private void filterBadTerms(GeneInfo geneInfo) {
    Iterator<String> symbolIterator = geneInfo.symbols.iterator();
    boolean hasGoodSymbol = false;
    while (symbolIterator.hasNext()){
      String symbol = symbolIterator.next();
      if (!symbol.startsWith("LOC")) hasGoodSymbol = true;
    }
    Iterator<String> nameIterator = geneInfo.names.iterator();
    while (nameIterator.hasNext()){
      String name = nameIterator.next().toLowerCase();
      if (name.contains("similar") || name.contains("putative") || name.contains("hypothetical"))
        nameIterator.remove();
    }
    if (!hasGoodSymbol && geneInfo.names.size() == 0)
      geneInfo.symbols.clear();    
    
  }

  private int count = 0;
  private String getValue(String line) {
    int x = line.indexOf(">");
    int y = line.lastIndexOf("<");
    return line.substring(x+1,y);
  }

  /*
  //Process line for table file
  private void processLine(String line){
    String[] cols = line.split("\t");
    if (cols[0].equals(taxonomyID)){
      GeneInfo geneInfo = new GeneInfo(); 
      
      geneInfo.symbols.add(cols[2]); //Preferred symbol
      
      String[] symbols = cols[4].split("[|]");
      for (String symbol : symbols) geneInfo.symbols.add(symbol);
      
      geneInfo.ids.add("LL_"+cols[1]);
      String[] ids = cols[5].split("[|]");
      for (String id : ids){
        if (id.startsWith("MIM")) geneInfo.ids.add(id.replace("MIM:", "OM_"));
        if (id.startsWith("HGNC")) geneInfo.ids.add(id.replace("HGNC:", "HG_"));
        if (id.startsWith("GDB")) geneInfo.ids.add(id.replace("GDB:", "GD_"));
        if (id.startsWith("UNIGENE")) geneInfo.ids.add(id.replace("UNIGENE:", "UG_"));
        if (id.startsWith("MGI")) geneInfo.ids.add(id.replace("MGI:", "MGI_"));
        if (id.startsWith("SGD")) geneInfo.ids.add(id.replace("SGD:", "SGD_"));
        if (id.startsWith("FLYBASE")) geneInfo.ids.add(id.replace("FLYBASE:", "FB_"));
      }
      
      if (!cols[8].equals("") && !geneInfo.symbols.contains(cols[8]))
        geneInfo.names.add(cols[8]);
      
      if (!cols[10].equals("") && !geneInfo.symbols.contains(cols[10])&& !geneInfo.names.contains(cols[10]))
        geneInfo.names.add(cols[10]);
      
      geneInfos.add(geneInfo);
    }
  }
  */
  
  private class GeneInfo{
    List<String> ids = new ArrayList<String>();
    List<String> symbols = new ArrayList<String>();
    Set<String> names = new HashSet<String>();
  }
  private List<GeneInfo> geneInfos = new ArrayList<GeneInfo>();
  private List<String> discontinued = new ArrayList<String>(); 
  private LVGNormaliser normaliser = new LVGNormaliser();
}
