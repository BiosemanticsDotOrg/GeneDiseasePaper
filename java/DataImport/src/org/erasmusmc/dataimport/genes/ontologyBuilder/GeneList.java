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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.collections.CountingSet;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.WriteTextFile;

public class GeneList extends ArrayList<Gene>{
  
  private static final long serialVersionUID = 2929874217342122759L;
  private Map<Integer, String> taxonID2name = initTaxonNames();
  private Map<Integer, Integer> taxonID2CID = initTaxonCIDs();
  
  public void printStatistics(){
    int symbolCount = 0;
    int nameCount = 0;
    int idCount = 0;
    CountingSet<Integer> taxonCounts = new CountingSet<Integer>();
    for (Gene gene : this){
      symbolCount += gene.symbols.size();
      nameCount += gene.names.size();
      idCount += gene.ids.size();
      taxonCounts.addAll(gene.taxonIDs);
    }  
    System.out.println("TaxonIDs:");
    taxonCounts.printCounts();
    System.out.println("genes: " + this.size() + " symbols: " + symbolCount + " names: " + nameCount + " databaseIDs: " + idCount); 
  }
  
  private Map<Integer, Integer> initTaxonCIDs() {
    Map<Integer,Integer> map = new HashMap<Integer, Integer>();
    map.put(9606, -1001);
    map.put(10090, -1002);
    map.put(10116, -1003);
    map.put(83333, -1004);
    map.put(4932, -1005);
    map.put(7955, -1006);
    map.put(6239, -1007);
    map.put(5833, -1008);    
    map.put(9031, -1009);
    map.put(7227, -1010); 
    return map;
  }

  private Map<Integer, String> initTaxonNames() {
    Map<Integer,String> map = new HashMap<Integer, String>();
    map.put(9606, "HSAPIENS");
    map.put(10090, "MMUSCULUS");
    map.put(10116, "RNORVEGICUS");
    map.put(83333, "ECOLI");
    map.put(4932, "SCEREVISIAE");
    map.put(7955, "DRERIO");
    map.put(6239, "CELEGANS");
    map.put(5833, "FPLASMODIUM");
    map.put(9031, "GGALLUS");
    map.put(7227, "DMELANOGASTER");
    return map;
  }

  public void saveToSimpleFile(String filename){
    WriteTextFile out = new WriteTextFile(filename);
    for (Gene geneInfo : this){
      StringBuilder sb = new StringBuilder();
      if (geneInfo.preferredSymbol != null)
        sb.append(geneInfo.preferredSymbol);
      sb.append("\t");
      sb.append(join(geneInfo.symbols));
      sb.append("\t");
      sb.append(join(geneInfo.names));
      sb.append("\t");
      sb.append(join(geneInfo.ids));
      sb.append("\t");
      sb.append(join(geneInfo.taxonIDs));
      sb.append("\t");
      sb.append(geneInfo.source);
      out.writeln(sb.toString());
    }
    out.close();
  }
  
  private static String join(Collection<?> items){
  	StringBuilder sb = new StringBuilder();
  	for (Object item : items){
  		if (sb.length() != 0)
  			sb.append(";");
  		sb.append(item.toString().replace(";", " "));
  	}
  	return sb.toString();
  }
  
  public static GeneList loadFromSimpleFile(String filename){
  	GeneList geneList = new GeneList();
  	for (String line : new ReadTextFile(filename)){
  		String[] cols = line.split("\t");
  		Gene gene = new Gene(cols[5]);
  		if (cols[0].length() != 0)
  		  gene.preferredSymbol = cols[0];
  		for (String symbol : cols[1].split(";"))
  			gene.symbols.add(symbol);
  		for (String name : cols[2].split(";"))
  			gene.names.add(name);
  		for (String id : cols[3].split(";")) {
  			String[] parts = id.split("_");
  			gene.ids.add(new DatabaseID(parts[0],parts[1]));
  		}
  		for (String taxon : cols[4].split(";"))
  			gene.taxonIDs.add(Integer.parseInt(taxon));
  		geneList.add(gene);
  	}
  	return geneList;
  }
  
  public OntologyStore convertToOntologyStore(int startCID){
    OntologyStore ontology = new OntologyStore();
    Concept semtype = new Concept(-116);
    semtype.setName("Amino Acid, Peptide, or Protein");
    ontology.setConcept(semtype);
    Concept voc = new Concept(-1000);
    voc.setName("GENE");
    ontology.setConcept(voc);
    
    for (Gene gene : this){
      Concept concept = new Concept(startCID++);
      List<TermStore> terms = new ArrayList<TermStore>();
      if (gene.preferredSymbol != null)
        terms.add(new TermStore(gene.preferredSymbol));
      
      for (String symbol : gene.symbols)
        if (gene.preferredSymbol == null || !gene.preferredSymbol.equals(symbol))
          terms.add(new TermStore(symbol));
      
      for (String name : gene.names)
        terms.add(new TermStore(name));
      
      for (DatabaseID id : gene.ids)
          ontology.setDatabaseIDForConcept(concept.getID(), id);
      
      concept.setTerms(terms);
      ontology.setConcept(concept);
      
      addVocsAndSemTypes(ontology, gene, concept.getID());
    }
    return ontology;
  }

  private void addVocsAndSemTypes(Ontology ontology, Gene gene, Integer cid) {
    ontology.setRelation(new Relation(cid, DefaultTypes.fromVocabulary, -1000));
    for (Integer taxonID : gene.taxonIDs){
      int vocCID = getVocCID(ontology, taxonID);
      ontology.setRelation(new Relation(cid, DefaultTypes.fromVocabulary, vocCID));
    }
    ontology.setRelation(new Relation(cid, DefaultTypes.isOfSemanticType, -116));
    
  }

  private int getVocCID(Ontology ontology, Integer taxonID) {
    int cid = taxonID2CID.get(taxonID);
    if (ontology.getConcept(cid) == null){
      Concept concept = new Concept(cid);
      concept.setName(taxonID2name.get(taxonID));
      ontology.setConcept(concept);
    }
    return cid;
  }
}
