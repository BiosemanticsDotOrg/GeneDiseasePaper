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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.CountingSet;
import org.erasmusmc.collections.OneToManyList;
import org.erasmusmc.collections.OneToManySet;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class HomologeneMerger {
  private String homologeneFilename;
  private Set<Integer> taxonIDs;
  private int recipCount = 0;
  private int overlapCount = 0;
  
  public static void main(String[] args){
  	Set<Integer> allowedTaxonIDs = new HashSet<Integer>();
    allowedTaxonIDs.add(9606); // H sapiens
    allowedTaxonIDs.add(10090); // M musculus
    allowedTaxonIDs.add(10116); // R norvegicus
    allowedTaxonIDs.add(83333); //E coli
    allowedTaxonIDs.add(4932); // S cerevisiae
    allowedTaxonIDs.add(7227); // D melanogaster
    allowedTaxonIDs.add(7955); // D rerio
    allowedTaxonIDs.add(6239); // C elegans
    allowedTaxonIDs.add(9031); // G gallus
  	new HomologeneMerger("/data/Homologene/homologene.xml", allowedTaxonIDs);
  }
  
  public HomologeneMerger(String homologeneFilename, Set<Integer> taxonIDs){
    this.homologeneFilename = homologeneFilename;
    this.taxonIDs = taxonIDs;
  }
  
  public GeneList merge(GeneList geneList){
  	Map<String, Gene> eg2gene = createEG2GeneMap(geneList);
  	OneToManySet<String, String> eg2egs = generateMapping(eg2gene);
  	filterMultiPerOrganismLinks(eg2egs, eg2gene);
  	Map<String, Integer> eg2clusterID = convertToClusters(eg2egs);
  	
  	//Go through genes. If gene doesn't belong to cluster, add it to the new genelist:
    GeneList newGeneList = new GeneList();
    OneToManyList<Integer, Gene> cluster2Genes = new OneToManyList<Integer, Gene>();
    for (Gene gene : geneList){
    	boolean inCluster = false;
      for (DatabaseID databaseID : gene.ids){
        if (databaseID.database.equals("EG")){
          Integer cluster = eg2clusterID.get(databaseID.ID);
          if (cluster != null){ 
          	cluster2Genes.put(cluster, gene);
          	inCluster = true;
          	break;          	
          }
        }   
      }
      if (!inCluster)
        newGeneList.add(gene);
    }
    
    // Merge clusters:
    int count = 0;
    int clusterCount = 0;
    for (Integer cluster : cluster2Genes.keySet()){
      Gene homoloGene = new Gene("Homologene");
      List<Gene> homologs = cluster2Genes.get(cluster);

      //Preferred symbol of human has preference, else pick first:
      for (Gene gene : homologs)
        if (gene.taxonIDs.contains(9606))
        		homoloGene.preferredSymbol = gene.preferredSymbol;
      if (homoloGene.preferredSymbol == null)
      	homoloGene.preferredSymbol = homologs.get(0).preferredSymbol;
        
      //Merge homologs:
      for (Gene gene : homologs)
        homoloGene.merge(gene);
      if (homologs.size() > 1){
        count += homologs.size();
        clusterCount++;
      }
      newGeneList.add(homoloGene);
    }
    System.out.println("Merged " + count + " genes into "+ clusterCount + " clusters");
    return newGeneList;
  }
  
  private Map<String, Gene> createEG2GeneMap(GeneList geneList) {
  	Map<String, Gene> eg2gene = new HashMap<String, Gene>();
  	for (Gene gene : geneList){
  		for (DatabaseID dbID : gene.ids)
  			if (dbID.database.equals("EG"))
  				eg2gene.put(dbID.ID, gene);
  	}
  	return eg2gene;
	}

	private OneToManySet<String, String> generateMapping(Map<String, Gene> eg2gene){
  	System.out.println("Loading pairs from homologene");
  	Map<String, String> gi2egID = new HashMap<String, String>();
  	String entrezGeneID = null;
  	String gi1 = null;
  	String gi2 = null;
  	OneToManySet<String, String> eg2egs = new OneToManySet<String, String>();
  	
  	for (String line : new ReadTextFile(homologeneFilename)){
  		String trimLine = line.trim();
  		if (trimLine.startsWith("<HG-Gene_geneid>"))
  			entrezGeneID = StringUtilities.findBetween(trimLine, "<HG-Gene_geneid>", "</HG-Gene_geneid>");
  		else if (trimLine.startsWith("<HG-Gene_taxid>")){
  			Integer taxonID = Integer.parseInt(StringUtilities.findBetween(trimLine, "<HG-Gene_taxid>", "</HG-Gene_taxid>"));
  			if (!taxonIDs.contains(taxonID))
  				entrezGeneID = null;
  		} else if (trimLine.startsWith("<HG-Gene_prot-gi>")){
  			if (entrezGeneID != null){
  			  String gi = StringUtilities.findBetween(trimLine, "<HG-Gene_prot-gi>", "</HG-Gene_prot-gi>");
  			  gi2egID.put(gi, entrezGeneID);
  			}
  		} else if (trimLine.equals("</HG-Gene>")) 
  			entrezGeneID = null;
  		else if (trimLine.startsWith("<HG-Stats_gi1>"))
  			gi1 = StringUtilities.findBetween(trimLine, "<HG-Stats_gi1>", "</HG-Stats_gi1>"); 	
  		else if (trimLine.startsWith("<HG-Stats_gi2>"))
  			gi2 = StringUtilities.findBetween(trimLine, "<HG-Stats_gi2>", "</HG-Stats_gi2>"); 	
  		else if (trimLine.startsWith("<HG-Stats_recip-best")) {
  			String recip = StringUtilities.findBetween(trimLine, "<HG-Stats_recip-best value=\"", "\"/>");
				String eg1 = gi2egID.get(gi1);
				String eg2 = gi2egID.get(gi2);
				if (eg1 != null && eg2 != null){
			    Gene gene1 = eg2gene.get(eg1);
			    Gene gene2 = eg2gene.get(eg2);
		  	  if (gene1 != null && gene2 != null){
			      if (areOrthologs(gene1, gene2, recip.equals("true")))	{ 
  					  eg2egs.put(eg1, eg2);
  					  eg2egs.put(eg2, eg1);
      			}
  				}
  			}
  		} else if (trimLine.startsWith("</HG-Stats>")) {
  			gi1 = null;
  			gi2 = null;
  		} else if (trimLine.startsWith("</HG-Entry>")) {
  			gi2egID.clear();
  		}
  	}
  	System.out.println("Created " + recipCount + " pairs due to reciprocity, " + overlapCount + " pairs due to overlap");
  	return eg2egs;
	}
	
	private void filterMultiPerOrganismLinks(OneToManySet<String, String> eg2egs, Map<String,Gene> eg2gene){  	
  	System.out.println("Filtering multiple-per-organism links");

		int originalCount = 0;
  	int deletedCount = 0;
  	for (Map.Entry<String, Set<String>> entry : eg2egs.entrySet()){
  		originalCount += entry.getValue().size();
  		CountingSet<Integer> taxonCounts = new CountingSet<Integer>();
  		taxonCounts.add(eg2gene.get(entry.getKey()).taxonIDs.iterator().next());
  		for (String eg : entry.getValue())
  			taxonCounts.add(eg2gene.get(eg).taxonIDs.iterator().next());
  		Iterator<String> iterator = entry.getValue().iterator();
  		while (iterator.hasNext()){
  			Integer taxonID = eg2gene.get(iterator.next()).taxonIDs.iterator().next();
  			if (taxonCounts.getCount(taxonID) != 1){
  				iterator.remove();
  				deletedCount++;
  			}
  		}
  	}
  	for (Map.Entry<String, Set<String>> entry : eg2egs.entrySet()){
  		Iterator<String> iterator = entry.getValue().iterator();
  		while (iterator.hasNext()){
  			String eg = iterator.next();
  			if (!eg2egs.get(eg).contains(entry.getKey())){
  				iterator.remove();
  			  deletedCount++;
  			}
  		}
  	}
  	System.out.println("Filtering removed " + deletedCount + " of " + originalCount + " pairs");
  }	
	
	private boolean areOrthologs(Gene gene1, Gene gene2, boolean reciprocity) {
  	if (reciprocity){
  		recipCount++;
  		return true;
  	} 
  	if (calculateOverlapScore(gene1, gene2) >= 50){
  		overlapCount++;
  		return true;
  	}
  	return false;
	}

	private int calculateOverlapScore(Gene gene1, Gene gene2) {
		int score = 0;
		if (samePreferredSymbol(gene1, gene2))
			score += 15;
		for (String symbol1 : gene1.symbols){
			String lcSymbol1 = symbol1.toLowerCase();
			for (String symbol2 : gene2.symbols){
				String lcSymbol2 = symbol2.toLowerCase();
				if (lcSymbol1.equals(lcSymbol2))
					if (StringUtilities.containsNumber(symbol1))
						score += 20;
					else
						score += 10;
			}
		}
		for (String name1 : gene1.names){
			String lcName11 = name1.toLowerCase();
			for (String name2 : gene2.names){
				String lcName2 = name2.toLowerCase();
				if (lcName11.equals(lcName2))
					if (OntologyUtilities.isGeneSymbol(name1))
					  score += 15;
					else
						score += 25;
			}
		}
		return score;
	}

	private boolean samePreferredSymbol(Gene gene1, Gene gene2) {
		if (gene1.preferredSymbol == null || gene2.preferredSymbol == null)
		  return false;
		else 
			return (gene1.preferredSymbol.toLowerCase().equals(gene2.preferredSymbol.toLowerCase()));
	}

	private Map<String, Integer> convertToClusters(OneToManySet<String, String> eg2egs){
  	Map<String, Integer> eg2clusterID = new HashMap<String, Integer>();
    int nextCluster = 0;
  	for (Map.Entry<String, Set<String>> entry : eg2egs.entrySet()){
  		Integer cluster = eg2clusterID.get(entry.getKey());
  		Iterator<String> iterator = entry.getValue().iterator();
  		while (cluster == null && iterator.hasNext())
  			cluster = eg2clusterID.get(iterator.next());
  		if (cluster == null)
  			cluster = nextCluster++;
  		eg2clusterID.put(entry.getKey(), cluster);
  		for (String eg : entry.getValue())
  			eg2clusterID.put(eg, cluster);
  	}		
  	System.out.println(nextCluster + " clusters with an average of " + (eg2clusterID.size() / (double)nextCluster) + " genes");
  	return eg2clusterID;
  }
}

