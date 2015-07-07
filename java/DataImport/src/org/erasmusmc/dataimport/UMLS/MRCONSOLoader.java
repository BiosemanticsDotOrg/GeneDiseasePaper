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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.ReadTextFile;

public class MRCONSOLoader {

  public static void loadFromMRCONSO(Ontology ontology, String filename, List<String> log_output, String abbreviationsFile) {

    int cuiCol = 0;
    int termIDCol = 5;
    int termCol = 14;
    int vocCol = 11;
    int GOCol = 13;

    int cui = -1;
    int prevCui = -1;
    int termID = -1;
    int prevTermID = -1;
    Map<String, Integer> vocabularies = new HashMap<String, Integer>();

    Set<Integer> foundVocsForConcept = null;
    Concept concept = null;

    // Get list of abbreviations and acronyms
    List<String> listOfAbbreviationsOrAcronyms = UMLSFiltersBeforeOntologyCreation.getAbbreviationsAndAcronyms(abbreviationsFile);

    ReadTextFile textFile = new ReadTextFile(filename);
    Iterator<String> fileIterator = textFile.getIterator();
    int lineCount = 0;
    while (fileIterator.hasNext()) {
      lineCount++;
      if (lineCount % 100000 == 0)
        System.out.println(lineCount);
      String line = fileIterator.next();
      if (line.length() != 0) {
        // Check filters
        String[] columns = line.split("\\|");
        if (UMLSFiltersBeforeOntologyCreation.isSuppressable(columns)) {
          log_output.add("TERM MARKED AS SUPPRESSED IN MRCONSO.RRF|" + line);
        }
        else if (UMLSFiltersBeforeOntologyCreation.isMoreThan255(columns)) {
          log_output.add("TERM FIELD MORE THAN 255 IN MRCONSO.RRF|" + line);
        }
        else if (UMLSFiltersBeforeOntologyCreation.notRightLanguage(columns)) {
          
        }
        else if (UMLSFiltersBeforeOntologyCreation.isFromBadVocabulary(columns)) {
          log_output.add("FILTERED OUT DUE TO BAD VOC IN MRCONSO.RRF|" + line);
        }
        else {
          // Process concept information line

          // Save the cui and TermID as Integers
          cui = Integer.parseInt(columns[cuiCol].trim().substring(1, columns[cuiCol].length()));
          termID = Integer.parseInt(columns[termIDCol].trim().substring(1, columns[termIDCol].length()));
          // If we encounter a new concept identifier in the file: create a new
          // concept
          if (prevCui != cui) {
            if (concept != null) {
              if (concept.getTerms().size() != 0)
                concept.setName(concept.getTerms().get(0).text);
              ontology.setConcept(concept);
            }
            concept = new Concept(cui);
            foundVocsForConcept = new TreeSet<Integer>();
            prevCui = cui;
          }
          // Add GO-identifier
          String voc = columns[vocCol].trim();
          if (voc.equals("GO")) {
            String GOstring = columns[GOCol].trim();
            DatabaseID databaseID = new DatabaseID("GO", GOstring);
            ontology.setDatabaseIDForConcept(concept.getID(), databaseID);
          }
          // If we have not encountered the term before, add it to the concept
          if (prevTermID != termID) {
            String term = columns[termCol].trim();            
            // If the term in not an abbreviation or acronym, convert to lower
            // case
            // if it is from a vocabulary which has only upper case terms
            String checkedTerm = UMLSFiltersBeforeOntologyCreation.convertToLowerCaseIfWordsMoreThan2AndCharactersMoreThan10AndNotAbbreviationOrAcronym(term, voc, listOfAbbreviationsOrAcronyms);
            if (!checkedTerm.equals(term)) {
              log_output.add("TERM HAS BEEN CONVERTED TO LOWERCASE IN ONTOLOGY|" + line);
            }
            term = checkedTerm;
            List<TermStore> terms = concept.getTerms();
            terms.add(new TermStore(term));
            concept.setTerms(terms);
            prevTermID = termID;

            // Add vocabulary
            String vocstring = columns[vocCol].trim();
            Integer vocID = vocabularies.get(vocstring);
            if (vocID == null) {
              vocID = -1000 - vocabularies.size();
              vocabularies.put(vocstring, vocID);
              Concept vocabulary = new Concept(vocID);
              vocabulary.setName(vocstring);
              ontology.setConcept(vocabulary);
            }
            // Set the vocabulary if it has not been set before for the concept
            if (!foundVocsForConcept.contains(vocID)) {
              Relation relation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, vocID);
              ontology.setRelation(relation);
              foundVocsForConcept.add(vocID);
            }           
          }
        }
      }
    }
    if (concept != null) {
      if (concept.getTerms().size() != 0)
        concept.setName(concept.getTerms().get(0).text);
      ontology.setConcept(concept);
    }
  }
}
