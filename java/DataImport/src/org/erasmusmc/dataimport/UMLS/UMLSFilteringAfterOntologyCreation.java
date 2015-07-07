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

package org.erasmusmc.dataimport.UMLS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

public class UMLSFilteringAfterOntologyCreation {
  public static int MESHVOC = -1000;
  public static int maxtermsize = 100;
  public static Pattern Retiredpattern = Pattern.compile("retired code", Pattern.CASE_INSENSITIVE);
  public static Pattern CurlyParenthesispattern = Pattern.compile("\\{.*\\}");
  public static Pattern SquarebracketsCodingPattern = Pattern.compile("^\\[\\w{2}\\d{3}\\]");
  public static Pattern AtsignPattern = Pattern.compile("@");
  public static Pattern AtsignGeneClusterPattern = Pattern.compile("@ gene cluster");
  public static Pattern NonEssentialParentheticals = Pattern.compile("(\\[X\\])|(\\[V\\])|\\[D\\]|\\[M\\]|\\[EDTA\\]|\\[SO\\]|\\[Q\\]");
  public static Pattern DisorderPattern = Pattern.compile("\\(disorder\\)");
  public static Pattern FindingPattern = Pattern.compile("\\(finding\\)");
  public static Pattern xxxPattern = Pattern.compile("xxx", Pattern.CASE_INSENSITIVE);
  public static Pattern ECPattern = Pattern.compile("^EC\\s[0-9]+\\.", Pattern.CASE_INSENSITIVE);
  public static Pattern proteinWeightPattern = Pattern.compile("^[0-9]+ ?[kK][dD][aA]?$");

  /**Patterns from UMLS rewrite rules project:*/
  public static Pattern DosagePattern = Pattern.compile("(\\s\\d[\\d.]*\\s?((g )|(ug)|(mg)|(ml)|%)|(\\(ml\\))|(\\(mg\\))|(\\(gm\\))|(\\(ug\\)))", Pattern.CASE_INSENSITIVE);
  public static Pattern necPatternCombined = Pattern.compile("(,\\snec$)|(\\s\\(nec\\)$)|(\\s\\[nec\\]$)|(not elsewhere classified)|(unclassified)|(without mention)", Pattern.CASE_INSENSITIVE);
  public static Pattern nosPatternCombined = Pattern.compile("(,\\snos$)|(\\s\\(nos\\)$)|(\\s\\[nos\\]$)|(not otherwise specified)|(not specified)|(unspecified)", Pattern.CASE_INSENSITIVE);
  public static Pattern miscPatternCombined = Pattern.compile("(^|\\s)other(\\s|$)|(deprecated)|(unknown)|(obsolete)|(^no\\s+)|(miscellaneous)|(\\(MMHCC\\))", Pattern.CASE_INSENSITIVE);


  public static Set<Integer> filteredSemanticTypes = getSemanticTypesForFiltering();
  public static Set<Integer> filteredSemanticTypesNotMesh = getSemanticTypesForFilteringNotMesh();
  public static Set<String> stopwordsForFiltering = OntologyUtilities.stopwordsForFiltering;

  public static void main(String[] args) {
    String nameOfNewDatabase = "UMLS2006AD_AfterFiltering_031207";
    // String nameOfNewDatabase = "test_AfterFiltering";

    // Create log
    List<String> log_output = new ArrayList<String>();
    // String logname = "/home/khettne/Toxicogenomics/Data/Indexing/UMLS_thesaurus_building/UMLS_filtering_log.log";
    String logname = "/home/public/Thesauri/UMLS2006AD/UMLS_filtering_log_031207.log";

    System.out.println("Loading original ontology " + StringUtilities.now());

    OntologyPSFLoader loader = new OntologyPSFLoader();
    //loader.loadFromPSF("/home/khettne/Toxicogenomics/Data/Indexing/UMLS_thesaurus_building/UMLS_2006AD_beforefiltering.psf");
    loader.loadFromPSF("/home/public/Thesauri/UMLS2006AD/UMLS_2006AD_beforefiltering.psf");
    //loader.loadFromPSF("/home/public/Thesauri/UMLS2006AD/test.psf");
    OntologyStore originalOntology = loader.ontology;

    System.out.println("Creating new ontology " + StringUtilities.now());

    Ontology newOntology = new OntologyStore();
    newOntology.setName(nameOfNewDatabase);

    Set<Integer> includedCUIs = new HashSet<Integer>();

    System.out.println("Checking rules " + StringUtilities.now());

    Iterator<Concept> conceptIterator = originalOntology.getConceptIterator();
    int lineCount = 0;
    while (conceptIterator.hasNext()) {
      lineCount++;
      if (lineCount % 1000 == 0)
        System.out.println(lineCount);
      Concept concept = conceptIterator.next();
      if (concept.getID() > 0) {
        if (semanticFilter(concept, originalOntology)) {
          log_output.add("FILTERED OUT DUE TO BAD SEMANTIC TYPE|" + concept.getName() + "|" + concept.getID());
          concept.setTerms(new ArrayList<TermStore>());
        }
        /*if (VOCFilter(concept, originalOntology)) {
          log_output.add("FILTERED OUT DUE TO BAD VOC|" + concept.getName() + "|" + concept.getID());
          concept.setTerms(new ArrayList<TermStore>());
        }*/
        Iterator<TermStore> termIterator = concept.getTerms().iterator();
        while (termIterator.hasNext()) {
          TermStore term = termIterator.next();          
          // Check if the term satisfies Martijns rule or the other filter rules, and remove it if it does
          if (OntologyUtilities.MartijnsFilterRule(term.text, stopwordsForFiltering)) {
            termIterator.remove();
            log_output.add("SATISFIES MARTIJNS RULE|" + term.text + "|" + concept.getID());
          }
          else if (satisfiesUMLSfilterRules(concept.getID(), originalOntology, term)) {
            termIterator.remove();
            log_output.add("FILTERED OUT DUE TO PATTERN MATCHING|" + term.text + "|" + concept.getID());
          }
        }
        OntologyUtilities.removeDuplicateTerms(concept.getTerms());
      }
      if (!concept.getTerms().isEmpty() || concept.getID() < 0) {

        includedCUIs.add(concept.getID());
        newOntology.setConcept(concept);
      }
    }
    // Copy relationships:
    List<Relation> relations = originalOntology.getRelations();
    for (Relation relation: relations)
      if (includedCUIs.contains(relation.subject)&& includedCUIs.contains(relation.object))
        newOntology.setRelation(relation);

    // Copy databaseIDs:
    List<DatabaseID> databaseIDs;
    for (int cui: includedCUIs) {
      databaseIDs = originalOntology.getDatabaseIDsForConcept(cui);
      if (databaseIDs != null)
        for (DatabaseID databaseID: databaseIDs)
          newOntology.setDatabaseIDForConcept(cui, databaseID);
    }
    System.out.println("Writing logfile " + StringUtilities.now());
    TextFileUtilities.saveToFile(log_output, logname);

    OntologyCurator curator = new OntologyCurator("/home/public/thesauri/UMLS2006AD/UMLS_curation_file.txt");
    curator.curateAndPrepare(newOntology);
    loader.ontology = (OntologyStore) newOntology;
    //loader.SaveToPSF("/home/khettne/Toxicogenomics/Data/Indexing/UMLS_thesaurus_building/UMLS_2006AD_filtered.psf");
    loader.saveToPSF("/home/public/Thesauri/UMLS2006AD/UMLS_2006AD_filtered_031207.psf");
  }

  /*private static boolean VOCFilter(Concept concept, OntologyStore originalOntology) {

    return originalOntology.existsRelation(new Relation(concept.getID(), DefaultTypes.fromVocabulary, NCICTCAEVOC));
  }*/

  private static boolean semanticFilter(Concept concept, OntologyStore originalOntology) {
    List<Relation> relations = originalOntology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.isOfSemanticType);

    boolean isMESH = originalOntology.existsRelation(new Relation(concept.getID(), DefaultTypes.fromVocabulary, MESHVOC));

    for (Relation relation: relations) {
      boolean filter = false;
      if (filteredSemanticTypes.contains(relation.object)) {
        filter = true;
      }
      else if (!isMESH && filteredSemanticTypesNotMesh.contains(relation.object)) {
        filter = true;
      }
      if (!filter) {
        return false;
      }
    }
    return true;
  }

  private static Set<Integer> getSemanticTypesForFiltering() {
    Set<Integer> result = new TreeSet<Integer>();
    result.add(-71);
    result.add(-185);
    result.add(-78);
    result.add(-171);
    result.add(-122);
    return result;
  }

  private static Set<Integer> getSemanticTypesForFilteringNotMesh() {
    Set<Integer> result = new TreeSet<Integer>();
    result.add(-201);
    result.add(-200);
    result.add(-170);
    result.add(-97);
    result.add(-73);
    result.add(-74);
    result.add(-203);
    result.add(-79);
    result.add(-80);
    result.add(-81);
    result.add(-82);
    result.add(-83);
    result.add(-169);
    result.add(-77);
    result.add(-92);
    result.add(-93);
    result.add(-94);
    return result;
  }


  public static boolean satisfiesUMLSfilterRules(Integer conceptID, Ontology ontology, TermStore term) {
    String t = term.text;
    if (t.length() > maxtermsize && !(OntologyUtilities.isChemical(conceptID, ontology)))
      return true;
    if (DosagePattern.matcher(t).find())
      return true;
    if (necPatternCombined.matcher(t).find())
      return true;
    if (nosPatternCombined.matcher(t).find())
      return true;
    if (miscPatternCombined.matcher(t).find())
      return true;
    if (CurlyParenthesispattern.matcher(t).find() && !(OntologyUtilities.isChemical(conceptID, ontology)))
      return true;
    if (AtsignPattern.matcher(t).find() && !(AtsignGeneClusterPattern.matcher(t).find()))
      return true;
    if (Retiredpattern.matcher(t).find())
      return true;
    if (xxxPattern.matcher(t).find())
      return true;
    if (ECPattern.matcher(t).find())
      return true;
    if (proteinWeightPattern.matcher(t).matches())
      return true;
    return false;
  }
}