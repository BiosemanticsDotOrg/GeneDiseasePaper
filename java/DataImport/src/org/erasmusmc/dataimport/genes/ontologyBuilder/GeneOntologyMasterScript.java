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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.erasmusmc.collections.CountingSet;
import org.erasmusmc.dataimport.genes.Affymetrix;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.FamilyNameFinder;
import org.erasmusmc.ontology.ontologyutilities.GeneTermVariantGenerator;
import org.erasmusmc.ontology.ontologyutilities.HomonymAnalyzer;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.ontology.ontologyutilities.evaluationScripts.DetectPlainEnglishWords;
import org.erasmusmc.ontology.ontologyutilities.evaluationScripts.OntologyFrequencyCount;
import org.erasmusmc.utilities.StringUtilities;

public class GeneOntologyMasterScript {
	
	public static boolean mergeHomologs = true;
	public static boolean generateCurationInformation = true;
  
  public static Set<Integer> allowedTaxonIDs = new HashSet<Integer>();
  public static String homologeneFile = "/home/khettne/Projects/GeneList/Homologene/homologene.xml";
  public static String entrezGeneFolder = "/home/khettne/Projects/GeneList/Entrez-Gene/";
  public static String uniprotFile = "/home/khettne/Projects/GeneList/Swiss-Prot/uniprot_sprot.dat";
  public static String omimFile = "/home/khettne/Projects/GeneList/OMIM/genemap";
  public static String hugoFile = "/home/khettne/Projects/GeneList/HUGO/alldata.txt";
  public static String affymetrixFolder = "/home/khettne/Projects/GeneList/Affymetrix/";
	public static String wordListFilename = "/home/khettne/Projects/GeneList/EnglishWords/ukwords.txt";
  public static String curationFile = "/home/khettne/Projects/GeneList/GeneThesaurusCurationFile.txt";
  public static String tempFolder = "/home/khettne/Projects/GeneList/tempDEBUG/";
  public static String normaliserCacheFile = "/home/public/Peregrine/standardNormCache2006.bin";
  public static String randomPMIDSampleFile = "/home/public/PMIDs/Random100.000.PMIDs";
  public static String outputFile = "/home/khettne/Projects/GeneList/GeneListHumanMouseRatV6_0.ontology";
  
  public static void main(String[] args) {
    allowedTaxonIDs.add(9606); // H sapiens
    allowedTaxonIDs.add(10090); // M musculus
    allowedTaxonIDs.add(10116); // R norvegicus
    /*allowedTaxonIDs.add(83333); //E coli
    allowedTaxonIDs.add(4932); // S cerevisiae
    allowedTaxonIDs.add(7227); // D melanogaster
    allowedTaxonIDs.add(7955); // D rerio
    allowedTaxonIDs.add(6239); // C elegans
    allowedTaxonIDs.add(9031); // G gallus
    */
    GeneList geneList = extractAndMergeGeneLists();
    System.out.println("Merged:");
    geneList.printStatistics();
    
    OntologyStore ontology;
    if (mergeHomologs)
    	ontology= mergeHomologs(geneList);
    else 
      ontology = geneList.convertToOntologyStore(3000000);
    expandAndFilter(ontology);
    addExtraIdentifiers(ontology);
    saveOntology(ontology, outputFile);
    
    if (generateCurationInformation)
    	generateCurationInformation(ontology);
  }

  private static void addExtraIdentifiers(OntologyStore ontology) {
    Affymetrix.libraryFolder = affymetrixFolder;
    new Affymetrix(ontology);
  }

  private static void saveOntology(OntologyStore ontology, String filename) {
  	OntologyFileLoader loader = new OntologyFileLoader();
  	loader.save(ontology, filename);
  }

  private static void generateCurationInformation(OntologyStore ontology) {
  	System.out.println(StringUtilities.now() + "\tDetecting plain english words");
  	new DetectPlainEnglishWords(ontology, wordListFilename, tempFolder+"englishWords.txt");
  	
    System.out.println(StringUtilities.now() + "\tAnalyzing homonyms");
    HomonymAnalyzer homcount = new HomonymAnalyzer();
    homcount.destroyOntologyDuringRelease = false;
    homcount.normaliser.loadCacheBinary(normaliserCacheFile);
    homcount.setOntology(ontology);
    homcount.countHomonyms(tempFolder+"homonyms.txt");
    
    System.out.println(StringUtilities.now() + "\tCounting frequencies");
    OntologyFrequencyCount.disambiguate = false;
    OntologyFrequencyCount.pmidsFile = randomPMIDSampleFile;
    OntologyFrequencyCount.outputFile = tempFolder+"frequencyCounts.txt";
    new OntologyFrequencyCount(ontology);
  }

  private static OntologyStore mergeHomologs(GeneList geneList) {
    System.out.println(StringUtilities.now() + "\tMerging using Homologene");
    HomologeneMerger merger = new HomologeneMerger(homologeneFile, allowedTaxonIDs);
    GeneList mergedGeneList = merger.merge(geneList);
    OntologyStore ontology = mergedGeneList.convertToOntologyStore(3000000);
    
    saveOntology(ontology, tempFolder + "merged.psf");
    
    return ontology;
  }

  private static void expandAndFilter(OntologyStore ontology) {
  	addIDsAsTerms(ontology);
  	
    System.out.println(StringUtilities.now() + "\tGenerating spelling variations");
    GeneTermVariantGenerator.generateVariants(ontology);
    
    System.out.println(StringUtilities.now() + "\tApplying generic filter");
    OntologyUtilities.filterOntology(ontology, OntologyUtilities.stopwordsForFiltering);
    
    System.out.println(StringUtilities.now() + "\tApplying family name filter");
    Set<String> familyNames = new HashSet<String>(FamilyNameFinder.findFamilyNamesListOutput(ontology));
    OntologyUtilities.geneVocabulary = ""; // no need for voc lookup in removeterms
    OntologyUtilities.removeTerms(ontology, familyNames);
    
    System.out.println(StringUtilities.now() + "\tCuration");
    OntologyCurator curator;
    if (curationFile == null)
      curator = new OntologyCurator();
    else
      curator = new OntologyCurator(curationFile);
    
    curator.curateAndPrepare(ontology);
  }

  private static void addIDsAsTerms(OntologyStore ontology) {
  	CountingSet<String> dbCounts = new CountingSet<String>();
		for (Concept concept : ontology){
			List<DatabaseID> dbIDs = ontology.getDatabaseIDsForConcept(concept.getID());
			for (DatabaseID dbID : dbIDs){
				String id = dbID.ID;
				if (StringUtilities.containsLetter(id) && StringUtilities.containsNumber(id)){
					TermStore term = new TermStore(id);
					concept.getTerms().add(term);
					dbCounts.add(dbID.database);
				}	
			}
		}
		System.out.println("Database IDs added as terms:");
		dbCounts.printCounts();
		
	}

	private static GeneList extractAndMergeGeneLists() {
    List<GeneList> geneLists = new ArrayList<GeneList>();
    
    System.out.println(StringUtilities.now() + "\tExtracting from Entrez-Gene");
    EntrezGeneParser entrezGeneParser = new EntrezGeneParser();
    String firstOrganismFile = "Homo_sapiens.xgs";
    //String firstOrganismFile = "Rattus_norvegicus.xgs";
    //String firstOrganismFile = "Mus_musculus.xgs";
    //String firstOrganismFile = "Bacteria.xgs";
    //String firstOrganismFile = "Saccharomyces_cerevisiae.xgs";
    //String firstOrganismFile = "Drosophila_melanogaster.xgs";
    //String firstOrganismFile = "Danio_rerio.xgs";
    //String firstOrganismFile = "Caenorhabditis_elegans.xgs";
    //String firstOrganismFile = "Gallus_gallus.xgs";
    
    System.out.println("Processing " + firstOrganismFile);
    GeneList humanGenes = entrezGeneParser.parse(entrezGeneFolder+firstOrganismFile, allowedTaxonIDs);
    humanGenes.printStatistics();
    humanGenes.saveToSimpleFile(tempFolder+firstOrganismFile.replace(".xgs", ".txt"));
    geneLists.add(humanGenes);
    
    
    File folder = new File(entrezGeneFolder);
    for (File file : folder.listFiles())
      if (file.getName().endsWith(".xgs") && !file.getName().equals(firstOrganismFile)) {
        System.out.println("Processing " + file.getName());
        GeneList entrezGeneGenes = entrezGeneParser.parse(file.getAbsolutePath(), allowedTaxonIDs);
        entrezGeneGenes.printStatistics();
        entrezGeneGenes.saveToSimpleFile(tempFolder+file.getName().replace(".xgs", ".txt"));
        geneLists.add(entrezGeneGenes);
      }
    
    System.out.println(StringUtilities.now() + "\tExtracting from HUGO");
    HGNCParser hgncParser = new HGNCParser();
    GeneList hgncGenes = hgncParser.parse(hugoFile, allowedTaxonIDs);
    System.out.println("HUGO:");
    hgncGenes.printStatistics();
    hgncGenes.saveToSimpleFile(tempFolder+"HGNC.txt");
    geneLists.add(hgncGenes);
  
    System.out.println(StringUtilities.now() + "\tExtracting from OMIM");
    OMIMParser omimParser = new OMIMParser();
    GeneList omimGenes = omimParser.parse(omimFile, allowedTaxonIDs);
    System.out.println("OMIM:");
    omimGenes.printStatistics();
    omimGenes.saveToSimpleFile(tempFolder+"OMIM.txt");
    geneLists.add(omimGenes);
  
    System.out.println(StringUtilities.now() + "\tExtracting from UniProt");
    UniProtParser uniProtParser = new UniProtParser();
    GeneList uniProtGenes = uniProtParser.parse(uniprotFile, allowedTaxonIDs);
    System.out.println("UniProt:");
    uniProtGenes.printStatistics();
    uniProtGenes.saveToSimpleFile(tempFolder+"UniProt.txt");
    geneLists.add(uniProtGenes);
  
    System.out.println(StringUtilities.now() + "\tMerging databases");
    DatabaseMerger merger = new DatabaseMerger();
    for (GeneList geneList : geneLists)
      merger.merge(geneList);
    
    GeneList mergedGenes = merger.getMergedGeneList();
    mergedGenes.saveToSimpleFile(tempFolder+"merged.txt");
    
    return mergedGenes;
  }

}
