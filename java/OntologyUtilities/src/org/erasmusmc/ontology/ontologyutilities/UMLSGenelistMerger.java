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

package org.erasmusmc.ontology.ontologyutilities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyConstructors.OCGenelistHumanV240;
import org.erasmusmc.ontology.ontologyConstructors.OCHomologeneV2;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;


public class UMLSGenelistMerger {
  public String ontologyName;
  public Integer geneVocIDLimit = 3000000; // Set to -1 to use GENE voc to
                                            // identify gene ontology instead
                                            // (much slower)
  public Pattern pLetterWordPattern = Pattern.compile("\\W[pP]\\d+\\W");
  public Pattern recombinantPattern = Pattern.compile("^recombinant \\w+", Pattern.CASE_INSENSITIVE);
  public Pattern wtAllelePattern = Pattern.compile(" wt Allele$");
  private List<Pattern> organisms;

  public static String umlsOntologyFilePath = "/home/khettne/Projects/UMLS/2010AB/UMLS2010AB_180211_medlinefilter.ontology";
  public static String geneListOntologyFilePath = "/home/khettne/Projects/GeneList/GeneListHumanMouseRatV6_0.ontology";
  public static String mergedOntologyName = "UMLS2010ABHomologeneV5_1";
  
  public static String tempPath = "/home/khettne/temp/";
  
  public static String normCacheFileName = "/home/public/Peregrine/standardNormCache2006.bin";

  public static void main(String[] args) {
    System.out.println(StringUtilities.now() + "\tLoading ontologies");

    OntologyFileLoader loader = new OntologyFileLoader();
    Ontology umls = loader.load(umlsOntologyFilePath);
    
    OntologyFileLoader fileLoader = new OntologyFileLoader();
    Ontology genelist = fileLoader.load(geneListOntologyFilePath);
    new UMLSGenelistMerger(umls, genelist, mergedOntologyName);
  }

  public UMLSGenelistMerger(Ontology umls, Ontology genelist, String name) {
    organisms = getOrganismPatterns(umls);
    ontologyName = name;
    concatenate(umls, genelist);
    System.gc();

    mergedOntology = ontologyManager.fetchClient(ontologyName);

    List<Integer> removelist = findGenesInUMLS();
    System.gc();

    remove(removelist);
    System.out.println(StringUtilities.now() + "\tDone");
  }

  private void remove(List<Integer> removelist) {
    System.out.println(StringUtilities.now() + "\tRemoving " + removelist.size() + " concepts");
    for (Integer cui: removelist) {
      mergedOntology.removeConcept(cui);
    }
  }

  private List<Integer> findGenesInUMLS() {
    List<Integer> removelist = new ArrayList<Integer>();
    List<String> logfile = new ArrayList<String>();
    List<Concept> checkForOverlap = new ArrayList<Concept>();
    OntologyStore tempThesaurus = new OntologyStore();

    System.gc();

    // Evaluate UMLS concepts:
    System.out.println(StringUtilities.now() + "\tEvaluating UMLS concepts");
    Iterator<Concept> conceptIterator = mergedOntology.getConceptIterator();
    while (conceptIterator.hasNext()) {
      Concept concept = conceptIterator.next();
      if (geneVocIDLimit.equals(-1) || concept.getID() < geneVocIDLimit) {

        // Check semantic types
        boolean geneOrProtein = false;
        boolean potentialGeneOrProtein = false;
        for (Relation relation: mergedOntology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.isOfSemanticType)) {
          if (relation.object == -116)
            geneOrProtein = true;
          if (relation.object == -28 || relation.object == -126 || relation.object == -192)
            potentialGeneOrProtein = true;
        }
        if (geneOrProtein)
          potentialGeneOrProtein = false; // if positively identified as gene,
                                          // it is no longer potential!

        // Check vocabularies
        boolean geneVoc = false;
        boolean HUGO = false;
        if (potentialGeneOrProtein || geneOrProtein) {
          for (Relation relation: mergedOntology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.fromVocabulary)) {
            if (mergedOntology.getConcept(relation.object).getName().equals("HUGO"))
              HUGO = true;
            if (mergedOntology.getConcept(relation.object).getName().equals("GENE"))
              geneVoc = true;
          }
        }

        // Check whether concept should be removed
        if (geneVoc) { // ignore gene vocabulary completely
          tempThesaurus.setConcept(concept);
        }
        else {
          if (HUGO) { // Must remove it
            removelist.add(concept.getID());
            logfile.add("REMOVE " + concept.getID() + " BECAUSE FROM HUGO (" + concept.getName() + ")");
          }
          else if (geneOrProtein || potentialGeneOrProtein) {
            if (containswtAllelePattern(concept.getTerms())) {
              removelist.add(concept.getID());
              logfile.add("REMOVE " + concept.getID() + " BECAUSE OF wtAllele pattern (" + concept.getName() + ")");
            }
            else if (geneOrProtein && remove116(concept)) { // Sem type 116:
                                                            // remove if fails
                                                            // tests
              removelist.add(concept.getID());
              logfile.add("REMOVE " + concept.getID() + " BECAUSE FROM SEM TYPE 116 (" + concept.getName() + ")");
            }
            else {
              tempThesaurus.setConcept(concept);
              checkForOverlap.add(concept);
            }
          }
        }
      }
      else {
        tempThesaurus.setConcept(concept);
      } // Its geneVoc concept, so add to tempthesaurus for overlap testing
    }
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = tempThesaurus;
    loader.saveToPSF(tempPath+"overlapTest.psf");

    // Do the check for overlap:
    Map<Integer, Map<Integer, List<String>>> cui2cuis = fetchOverlap(tempThesaurus);
    for (Concept concept: checkForOverlap) {
      String report = overlapsWithGene(concept, cui2cuis);
      if (report != null) {
        removelist.add(concept.getID());
        logfile.add("REMOVE " + concept.getID() + " BECAUSE " + report);
      }
    }

    TextFileUtilities.saveToFile(logfile, tempPath+"mergelog.txt");
    return removelist;
  }

  private boolean containswtAllelePattern(List<TermStore> terms) {
    for (TermStore ts: terms) {
      if (wtAllelePattern.matcher(ts.text).find()) {
        return true;
      }

    }
    return false;
  }

  // Check whether concept of semantic type 116 (protein) can be removed
  private boolean remove116(Concept concept) {
     return (
         concept.getName().toLowerCase().contains(" protein, ") ||
         StringUtilities.isPlural(concept.getName()) ||
         containsorganism(concept.getName()) ||
         containsPword(concept.getTerms()) ||
         containsRecombinant(concept.getTerms()));
  }

  private boolean containsRecombinant(List<TermStore> terms) {
    for (TermStore term: terms) {
      if (recombinantPattern.matcher(term.text).find()) {
        return true;
      }
    }

    return false;
  }

  private boolean containsPword(List<TermStore> terms) {
    for (TermStore term: terms) {
      if (pLetterWordPattern.matcher(term.text).find()) {
        return true;
      }
    }
    return false;
  }

 
  private boolean containsorganism(String name) {
    for (Pattern organism: organisms) {
      if (organism.matcher(name).find())
        return true;
    }
    return false;
  }

  private List<Pattern> getOrganismPatterns(Ontology ontology) {
    System.out.println("Detecting organisms names");
    Set<String> organisms = new HashSet<String>();
    for (Relation relation : ontology.getRelationsForConceptAsObject(-116, DefaultTypes.isOfSemanticType)){
      Concept concept = ontology.getConcept(relation.subject);
      if (concept.getName().toLowerCase().contains(" protein, ")) {
         String name = concept.getName(); int i = name.toLowerCase().indexOf("protein, "); 
         organisms.add(name.substring(i+10, name.length()));
         
      }
    }
    List<Pattern> result = new ArrayList<Pattern>();
    for (String organism : organisms){
      result.add(Pattern.compile("\\W" + organism + "\\W"));
    }
    System.out.println("Found " + result.size() + " organism names");
    return result;
  }

  // Check whether the string ends on a letter or number or mix
  public boolean endsWithID(String name) {
    int tokenstart = -1;
    for (int i = name.length() - 2; i > 0; i--)
      if (!Character.isLetterOrDigit(name.charAt(i))) {
        tokenstart = i + 1;
        break;
      }
    if (tokenstart != -1)
      if (tokenstart == name.length() - 1 || name.substring(tokenstart, name.length()).equals(name.substring(tokenstart, name.length()).toUpperCase()))
        return true;

    return false;
  }

  // Check whether the terms of the concept overlap sufficiently with a gene
  private String overlapsWithGene(Concept concept, Map<Integer, Map<Integer, List<String>>> cui2cuis) {
    Map<Integer, List<String>> id2overlap = cui2cuis.get(concept.getID());
    if (id2overlap == null)
      return null;
    int maxOverlap = 0;
    int maxLFOverlap = 0;
    int maxOverlapConcept = 0;
    for (Entry<Integer, List<String>> entry: id2overlap.entrySet()) {
      boolean geneVoc = false;
      if (geneVocIDLimit.equals(-1)) {
        for (Relation relation: mergedOntology.getRelationsForConceptAsSubject(entry.getKey(), DefaultTypes.fromVocabulary))
          if (mergedOntology.getConcept(relation.object).getName().equals("GENE"))
            geneVoc = true;
      }
      else if (entry.getKey() > geneVocIDLimit)
        geneVoc = true;
      if (geneVoc) { // it is another gene: look at overlap
        int overlap = entry.getValue().size();
        if (overlap >= maxOverlap) {
          int lfOverlap = 0;
          for (String term: entry.getValue())
            if (!OntologyUtilities.isGeneSymbol(term))
              lfOverlap++;
          if (overlap > maxOverlap || lfOverlap > maxLFOverlap) {
            maxOverlap = overlap;
            maxLFOverlap = lfOverlap;
            maxOverlapConcept = entry.getKey();
          }
        }
      }
    }

    if (maxOverlap > 0) {
      int termcount = concept.getTerms().size();
      if (maxOverlap >= termcount / 2 || (maxLFOverlap > 0)) {
        StringBuffer report = new StringBuffer();
        report.append("OVERLAP WITH CONCEPT " + maxOverlapConcept + " (");
        for (String term: id2overlap.get(maxOverlapConcept)) {
          report.append(term);
          report.append(";");
        }
        report.append(")");
        return report.toString();
      }
    }
    return null;
  }

  private Map<Integer, Map<Integer, List<String>>> fetchOverlap(OntologyStore tempThesaurus) {
    System.out.println(StringUtilities.now() + "\tExamining overlap");
    HomonymAnalyzer analyzer = new HomonymAnalyzer();
    analyzer.normaliser.loadCacheBinary(normCacheFileName);
    analyzer.stopwords = HomonymAnalyzer.getDefaultStopWordsForIndexing();
    analyzer.stopwords.add("human");
    analyzer.stopwords.add("protein");
    analyzer.stopwords.add("gene");
    analyzer.stopwords.add("antigen");
    analyzer.stopwords.add("product");
    Iterator<Concept> iterator = tempThesaurus.getConceptIterator();
    while (iterator.hasNext())
      for (TermStore term: iterator.next().getTerms()) {
        term.text = OntologyUtilities.tokenizeAndRemoveStopwordsFromString(term.text, analyzer.stopwords);
        OntologyUtilities.setGeneChemMatchingFlags(term);
    }
    analyzer.setOntology(tempThesaurus);
    return analyzer.compareConcepts();
  }

  private void concatenate(Ontology umls, Ontology genelist) {
    // Concatenate and dump into one ontology(client):
    System.out.println(StringUtilities.now() + "\tConcatening ontologies");
    umls.setName(ontologyName);
    //ontologyManager.deleteOntology(ontologyName);
    // ontologyManager.dumpStoreInDatabase(umls);
    // mergedOntology = ontologyManager.fetchClient(umls.getName());
    mergedOntology = umls;

    Iterator<Concept> conceptIterator = genelist.getConceptIterator();
    while (conceptIterator.hasNext()) {
      Concept concept = conceptIterator.next();
      if (concept.getID() < -999) {// vocabulary ID for genelist: make sure no
                                    // overlap with umls vocs
        Concept newConcept = new Concept(concept.getID() - 1000);
        newConcept.setDefinition(concept.getDefinition());
        newConcept.setName(concept.getName());
        newConcept.setTerms(concept.getTerms());
        concept = newConcept;
      }

      mergedOntology.setConcept(concept);
      geneCUIs.add(concept.getID());

      List<DatabaseID> databaseIDs = genelist.getDatabaseIDsForConcept(concept.getID());
      if (databaseIDs != null)
        for (DatabaseID databaseID: databaseIDs)
          mergedOntology.setDatabaseIDForConcept(concept.getID(), databaseID);
    }
    for (Relation relation: genelist.getRelations()) {
      if (relation.object < -999)
        relation.object -= 1000;
      if (relation.subject < -999)
        relation.subject -= 1000;
      mergedOntology.setRelation(relation);
    }
    ontologyManager.dumpStoreInDatabase((OntologyStore) mergedOntology);
  }

  private Ontology mergedOntology;
  private Set<Integer> geneCUIs = new HashSet<Integer>();
  private static OntologyManager ontologyManager = new OntologyManager();
}
