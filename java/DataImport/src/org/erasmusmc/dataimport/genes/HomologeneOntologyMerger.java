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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.ConceptMerger;
import org.erasmusmc.ontology.ontologyutilities.GeneTermVariantGenerator;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

public class HomologeneOntologyMerger {
  public static Integer numberAddedToHomologeneID = 3000000;
  public static String[] organisms = {"9606", "10090", "10116"};    
  public static String curationfile = "/home/public/Thesauri/homologene/GeneThesaurusCurationFile_may2007.txt";
  
  public static void main(String[] args) {
    String dir = "/home/public/Thesauri/homologene/";
    /*
    String homologenefile = "homologene.data";
    String mousePSF = "MGD_may2007.psf";
    String ratPSF = "RGD_may2007.psf";
    String humanPSF = "GenelistHuman_v2.4.0.psf";
    String dbname = "Homologene_newRule";
    */
    String homologenefile = "homologene_may2007.data";
    String mousePSF = "MGD_may2007.psf";
    String ratPSF = "RGD_may2007.psf";
    String humanPSF = "GenelistHuman_v2.4.0.psf";
    String dbname = "Homologene_curated_may2007";    

    System.out.println(StringUtilities.now() + "\tInitializing"); 
    OntologyManager ontologyManager = new OntologyManager();
    Map<Integer, List<Concept>> conceptsToBeMapped = new TreeMap<Integer, List<Concept>>();
    ontologyManager.deleteOntology(dbname);
    OntologyStore target = new OntologyStore();
    target.setName(dbname);
    intializeTargetOntology(target); //Sets vocs & semtypes
    Map<Integer, Integer> mapEntrezGene2Homologene = loadHomologeneFile(dir + homologenefile); //Loads mapping for all species
    
    System.out.println(StringUtilities.now() + "\tLoading source thesauri"); 
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadFromPSF(dir + mousePSF);
    Ontology mouse = loader.ontology;
    loader.loadFromPSF(dir + ratPSF);
    Ontology rat = loader.ontology;
    loader.loadFromPSF(dir + humanPSF);
    Ontology human = loader.ontology;

    System.out.println(StringUtilities.now() + "\tMapping concepts based on homologene file"); 
    //The following procedures check for each gene whether there is a mention in homologene.
    //If not, the gene is added to the target ontology, else, it is added to conceptsToBeMapped 
    //(DatabaseIDs are already added to the target ontology)
    checkIfIDsMapped(mouse, target, mapEntrezGene2Homologene, conceptsToBeMapped, -1002);
    checkIfIDsMapped(human, target, mapEntrezGene2Homologene, conceptsToBeMapped, -1001);
    checkIfIDsMapped(rat, target, mapEntrezGene2Homologene, conceptsToBeMapped, -1003);

    //Merge concepts in conceptsToBeMapped, and add them to the target ontology:
    for (Integer homologene: conceptsToBeMapped.keySet()) {
      Concept newconcept = new Concept(homologene + numberAddedToHomologeneID);
      List<Concept> concepts = conceptsToBeMapped.get(homologene);
      Set<String> newterms = new HashSet<String>();
      String name = "";
      Integer id = 3000000;
      for (Concept concept: concepts) {
        if (concept.getID() < id) { //use concept with lowest ID to give name
          id = concept.getID(); 
          name = concept.getName();
        }
        List<TermStore> terms = concept.getTerms();
        for (TermStore term: terms) {
          newterms.add(term.text);
        }

      }
      newconcept.setName(name);
      List<TermStore> newtermlist = new ArrayList<TermStore>();
      newtermlist.add(new TermStore(name));
      for (String term: newterms) {
        if (!term.equals(name)) {
          TermStore newterm = new TermStore(term);
          newtermlist.add(newterm);
        }
      }
      newconcept.setTerms(newtermlist);
      target.setRelation(new Relation(newconcept.getID(), DefaultTypes.fromVocabulary, -1000));
      target.setRelation(new Relation(newconcept.getID(), DefaultTypes.fromVocabulary, -1004));
      target.setRelation(new Relation(newconcept.getID(), DefaultTypes.isOfSemanticType, -116));
      DatabaseID databaseID = new DatabaseID("HO", homologene.toString());
      target.setDatabaseIDForConcept(newconcept.getID(), databaseID);
      target.setConcept(newconcept);
    }
    
    AddLLIDsFromHomologene(target, mapEntrezGene2Homologene);
    System.out.println(StringUtilities.now() + "\tNumber of concepts mapped due to homologene: " +conceptsToBeMapped.size());
    
    System.out.println(StringUtilities.now() + "\tCurating ontology"); 
    CurateOntology(target);
    
    System.out.println(StringUtilities.now() + "\tMapping concepts based on overlapping terms");
    MergeIdenticalConcepts(target);
    
    System.out.println(StringUtilities.now() + "\tGenerating spelling variants");
    GenerateSpellingVariants(target);
    
    System.out.println(StringUtilities.now() + "\tSaving ontology to database");
    ontologyManager.dumpStoreInDatabase(target);
    loader.ontology = target;
    loader.saveToPSF("/temp/Homologen.psf");
    
    System.out.println(StringUtilities.now() + "\tDone.");
    
  }

  private static void AddLLIDsFromHomologene(OntologyStore target, Map<Integer, Integer> mapEntrezGene2Homologene) {
    for (Map.Entry<Integer, Integer> entry : mapEntrezGene2Homologene.entrySet()){
      //Check if homologene concept exists:
      if (target.getConcept(entry.getValue()+numberAddedToHomologeneID) != null)
        target.setDatabaseIDForConcept(entry.getValue()+numberAddedToHomologeneID, new DatabaseID("LL", entry.getKey().toString()));      
    }
    
  }

  private static Map<Integer, Integer> loadHomologeneFile(String filename) {
    List<String> homologenelines = TextFileUtilities.loadFromFile(filename);
    Set<String> orgs = new TreeSet<String>();
    for (String organism : organisms)
      orgs.add(organism);
    Map<Integer, Integer> mapEntrezGene2Homologene = new TreeMap<Integer, Integer>();
    for (String line: homologenelines) {
      String[] cells = line.split("\t");
      if (orgs.contains(cells[1]))
        mapEntrezGene2Homologene.put(Integer.parseInt(cells[2]), Integer.parseInt(cells[0]));
    }
    return mapEntrezGene2Homologene;
  }

  public static void intializeTargetOntology(Ontology target) {

    Concept voc = new Concept(-1000);
    voc.setName("GENE");
    target.setConcept(voc);
    Concept voc2 = new Concept(-1001);
    voc2.setName("HUMAN");
    target.setConcept(voc2);
    Concept voc3 = new Concept(-1002);
    voc3.setName("MOUSE");
    target.setConcept(voc3);
    Concept voc4 = new Concept(-1003);
    voc4.setName("RAT");
    target.setConcept(voc4);
    Concept voc5 = new Concept(-1004);
    voc5.setName("HOMOLOGENE");
    target.setConcept(voc5);

    Concept semtype = new Concept(-116);
    semtype.setName("Amino Acid, Peptide, or Protein");
    target.setConcept(semtype);
  }

  public static void CurateOntology(OntologyStore ontology) {
    OntologyCurator ontologyCurator = new OntologyCurator(curationfile);
    ontologyCurator.curateAndPrepare(ontology);
  }   
   
  public static void MergeIdenticalConcepts(OntologyStore ontology) {    
    ConceptMerger.mergeIdentical=true;
    ConceptMerger.mergeWhenSubSet=true;
    ConceptMerger.minDiceForMerge=0.4;
    ConceptMerger.minDiceForMergeWhenPreferredTermsMatch=0.35;
    ConceptMerger.greedyConceptMerge(ontology);
  }
  
  public static void GenerateSpellingVariants(OntologyStore ontology) {  
    GeneTermVariantGenerator.generateVariants(ontology);
    String curationfile = "/home/public/Thesauri/homologene/GeneThesaurusCurationFile.txt";
    OntologyCurator ontologyCurator = new OntologyCurator(curationfile);  
    ontologyCurator.curateAndPrepare(ontology);
  }    

  public static void checkIfIDsMapped(Ontology checkontology, Ontology targetOntology, Map<Integer, Integer> homologeneMapping, Map<Integer, List<Concept>> conceptsToBeMapped, Integer sourceVoc) {

    Iterator<Concept> iterator = checkontology.getConceptIterator();
    while (iterator.hasNext()) {
      Concept concept = iterator.next();
      if (concept.getID() > 0){
        boolean commit = true;
        List<DatabaseID> dbids = checkontology.getDatabaseIDsForConcept(concept.getID());
        if (dbids != null) {
          for (DatabaseID databaseID: dbids) {
            if (databaseID.database == "LL") {
              Integer entrezGeneID = Integer.parseInt(databaseID.ID);
              Integer homologeneID = homologeneMapping.get(entrezGeneID);
              if (homologeneID != null) {
                commit = false;
                List<Concept> concepts = conceptsToBeMapped.get(homologeneID);
                if (concepts == null) {
                  concepts = new ArrayList<Concept>();
                  conceptsToBeMapped.put(homologeneID, concepts);
                }
                concepts.add(concept);

                for (DatabaseID dbid: dbids) {
                  targetOntology.setDatabaseIDForConcept(homologeneID + numberAddedToHomologeneID, dbid);
                }
              }
            }
          }
        }

        if (commit) {

          targetOntology.setConcept(concept);
          if (dbids != null) {
            for (DatabaseID dbid: dbids) {
              targetOntology.setDatabaseIDForConcept(concept.getID(), dbid);
            }
          }

          targetOntology.setRelation(new Relation(concept.getID(), DefaultTypes.fromVocabulary, -1000));
          targetOntology.setRelation(new Relation(concept.getID(), DefaultTypes.fromVocabulary, sourceVoc));
          targetOntology.setRelation(new Relation(concept.getID(), DefaultTypes.isOfSemanticType, -116));
        }
      }
    }
  }
  
  /*public static void addHomologene2LLlinks(Ontology ontology, String linkFile){
    List<String> lines = TextFileUtilities.loadFromFile(linkFile);
    
    for (String line: lines){
      String[] cells = line.split("\t");
      Set<Integer> entries = ontology.getConceptIDs(new DatabaseID("HO",cells[0]));
      if (entries != null){
        for(Integer id: entries){
          ontology.setDatabaseIDForConcept(id,new DatabaseID("LL",cells[1]));
        }
      }
    }
  }*/
}
