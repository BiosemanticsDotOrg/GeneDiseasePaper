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

package org.erasmusmc.ontology;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.utilities.StringUtilities;

/**
 * Use this class to load an OntologyStore from a PSF file
 * 
 * @author Schuemie
 * 
 */
public class OntologyPSFLoader {
  /**
   * After loading, the OntologyStore can be found here. Before saving, set the
   * OntologyStore here
   */
  public OntologyStore ontology;

  /**
   * Determines whether the definitions are loaded from the PSF file (uses more
   * memory). <br>
   * <br>
   * The default value is true.
   */
  public boolean loadDefinitions = true;

  /**
   * Determines whether the concept hierarchy is loaded from the PSF file (uses
   * more memory). <br>
   * <br>
   * The default value is false.
   */
  public boolean loadHierarchy = false;

  /**
   * Start reading the PSF file at this line <br>
   * <br>
   * The default value is 0.
   */
  public int offset = 0; // start reading at this line
  /**
   * This variable formalizes the assumption that semantic types have negative
   * concept ids; NOTE this is a partial solution as it is not taken into
   * account that vocs have to have a value of less then -1000 :<
   */
  public static boolean semanticTypesNegative = true;
  /**
   * Read this number of lines from the PSF file. A value of -1 indicates all
   * lines will be read. <br>
   * <br>
   * The default value is -1.
   */
  public int length = -1; // read this many lines (-1 indicates all remaining)

  /**
   * Load the ontology from the given file. After this method, the OntologyStore
   * can be retrieved from the ontology property.
   * 
   * @param filename
   *            The complete path and filename of the file
   */
  public void loadFromPSF(String filename) {
	try {
		loadFromPSF(new FileInputStream(filename));
	} catch (FileNotFoundException e) {
        e.printStackTrace();
	}
  }

  /**
   * Load the ontology from the given file. After this method, the OntologyStore
   * can be retrieved from the ontology property.
   * 
   * @param filename
   *            The complete path and filename of the file
   */
  public void loadFromPSF(InputStream is) {
    ontology = new OntologyStore();
    hasHierarchy = false;
    level = -1;
    sem = -1;
    terms = -1;
    voc = -1;
    cui = -1;
    autoCID = 0;
    vocabularies.clear();
    int count = -1;
    boolean First = true;
    BufferedReader bufferedReader = null;
    try {
      bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 1000000);

      while (bufferedReader.ready()) {
        String line = bufferedReader.readLine();
        if (!line.equals("")) {
          count++;
          if (First) {
            First = false;
            if (line.substring(0, 5).equalsIgnoreCase("level")) {
              extractColumns(line);
            }
            else {// assume default columns:
              level = 0;
              terms = 1;
              cui = 2;
            }
          }
          else if (count > offset && (length == -1 || count <= offset + length))
            if (line.length() != 0) {
              addToOntology(line);
            }
        }
      }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
      if (bufferedReader != null)
		try {
			bufferedReader.close();
		} catch (IOException e) {
	        e.printStackTrace();
		}
    }
  }

  /**
   * Save the OntologyStore defined in the ontology properly to the specified
   * file location.
   * 
   * @param filename
   */
  public void saveToPSF(String filename) {
    boolean doVoc = hasVoc();
    boolean doSem = hasSem();
    try {
      FileOutputStream PSFFile = new FileOutputStream(filename);
      BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(PSFFile, "UTF-8"), 1000000);
      StringBuffer firstline = new StringBuffer();
      firstline.append("LEVEL|");
      if (doVoc) {
        firstline.append("VOC|");
      }
      if (doSem) {
        firstline.append("SEM|");
      }
      firstline.append("DEFAULT|0");
      bufferedWrite.write(firstline.toString());
      bufferedWrite.newLine();

      // Add concepts
      Iterator<Concept> values = ontology.getConceptIterator();
      while (values.hasNext()) {
        Concept concept = values.next();
        // if (concept.ID >= 0){
        StringBuffer line = new StringBuffer();
        line.append(0 + "|");
        if (doVoc) {
          line.append(relatedToString(concept.getID(), DefaultTypes.fromVocabulary) + "|");
        }
        if (doSem) {
          line.append(semrelatedToString(concept.getID(), DefaultTypes.isOfSemanticType) + "|");
        }

        if (concept.terms != null) {
          if (concept.terms.size() != 0) {
            line.append(StringUtilities.escape(concept.terms.get(0).text));
          }
          for (int j = 1; j < concept.terms.size(); j++) {
            line.append(";" + StringUtilities.escape(concept.terms.get(j).text));
          }
        }
        else {
          line.append(StringUtilities.escape(concept.getName()));
        }
        boolean hasDef = false;
        if (!((concept.definition == null) || concept.definition.equals(""))) {
          line.append("?" + StringUtilities.escape(concept.definition));
          hasDef = true;
        }
        // Append the database identifiers (if any)
        List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
        if (databaseIDs != null) {
          boolean first = true;
          for (DatabaseID databaseID: databaseIDs) {
            if (first && !hasDef) {
              first = false;
              line.append("?");
            } else
              line.append("\\;");
            line.append(databaseID.database);
            line.append("_");
            line.append(StringUtilities.escape(databaseID.ID));
          }
        }

        line.append("|" + Integer.toString(concept.ID));
        bufferedWrite.write(line.toString());
        bufferedWrite.newLine();
      }

      // Add hierarchy:
      Iterator<Concept> conceptIterator = ontology.getConceptIterator();
      while (conceptIterator.hasNext()) {
        Concept concept = conceptIterator.next();
        List<Relation> relations = ontology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.isParentOf);
        if (relations != null && relations.size() != 0) {
          StringBuffer line = new StringBuffer();
          line.append("H|");
          Iterator<Relation> relationIterator = relations.iterator();
          while (relationIterator.hasNext()) {
            Relation relation = relationIterator.next();
            line.append(relation.object);
            if (relationIterator.hasNext())
              line.append(";");
          }
          line.append("|");
          line.append(concept.getID());
          bufferedWrite.write(line.toString());
          bufferedWrite.newLine();
        }
      }
      bufferedWrite.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private String relatedToString(int conceptID, int relationType) {
    List<Relation> vocs = ontology.getRelationsForConceptAsSubject(conceptID, relationType);

    StringBuffer buffer = new StringBuffer();
    if (vocs.size() != 0)
      buffer.append(ontology.getConcept(vocs.get(0).object).getName());
    for (int i = 1; i < vocs.size(); i++) {
      buffer.append(";");
      buffer.append(ontology.getConcept(vocs.get(i).object).getName());
    }
    return buffer.toString();
  }

  private String semrelatedToString(int conceptID, int relationType) {
    List<Relation> sems = ontology.getRelationsForConceptAsSubject(conceptID, relationType);

    StringBuffer buffer = new StringBuffer();
    if (sems.size() != 0 && ontology.getConcept(sems.get(0).object) == null)
      System.out.println(sems.get(0).toString());

    if (sems.size() != 0) {
      Integer id = ontology.getConcept(sems.get(0).object).getID();
      if (semanticTypesNegative)
        id = -id;
      buffer.append(id);
    }
    for (int i = 1; i < sems.size(); i++) {
      buffer.append(";");
      Integer id = ontology.getConcept(sems.get(i).object).getID();
      if (semanticTypesNegative)
        id = -id;
      buffer.append(id);
    }
    return buffer.toString();
  }

  private int level = -1;
  private int sem = -1;
  private int terms = -1;
  private int voc = -1;
  private int cui = -1;
  private int autoCID = 0;
  private boolean hasHierarchy = false;
  private Map<String, Integer> vocabularies = new HashMap<String, Integer>();

  // Finds which colums in the file contains what type of info:
  private void extractColumns(String line) {
    String[] columns = line.split("\\|");

    for (int i = 0; i < columns.length; i++) {
      if (columns[i].equalsIgnoreCase("level")) {
        level = i;
      }
      ;
      if (columns[i].equalsIgnoreCase("sem")) {
        sem = i;
      }
      ;
      if (columns[i].equalsIgnoreCase("voc")) {
        voc = i;
      }
      ;
      if (columns[i].equalsIgnoreCase("default")) {
        terms = i;
      }
      ;
      if (columns[i].equalsIgnoreCase("cui") || StringUtilities.isNumber(columns[i].trim())) {
        cui = i;
      }
      ;
    }
  }

  // Parses the main lines and insert the info in the thesaurus:
  private void addToOntology(String line) {
    List<String> columns = StringUtilities.safeSplit(line, '|');
    if (columns.get(0).equals("H")) {
      if (loadHierarchy) {
        if (!hasHierarchy) {
          hasHierarchy = true;
        }
        int parent = Integer.parseInt(columns.get(2));
        List<String> childrenStrings = StringUtilities.safeSplit(columns.get(1), ';');
        for (String childString: childrenStrings) {
          Relation relation = new Relation(parent, DefaultTypes.isParentOf, Integer.parseInt(childString));
          ontology.setRelation(relation);
        }
      }
    }
    else {// Process concept information line// plugin rob//heee we cant have a
      // cui of -1? hooray for text files! Bugs Galore!
      Concept newConcept;
      if (columns.size() > cui && cui != -1) {
        if (columns.get(cui).trim().equals("")){
          System.err.println("Missing cui in PSF file line: \"" + line + "\"");
          newConcept = new Concept(autoCID++);
        } else {
          newConcept = new Concept(Integer.parseInt(columns.get(cui).trim()));
          autoCID = newConcept.getID() + 1;
        }
      }
      else {
        newConcept = new Concept(autoCID++);
      }
      if (newConcept.getID() < 0) {
        // semantic types and vocabularies
        List<String> subs = StringUtilities.safeSplit(columns.get(terms), '?');
        List<String> termlist = StringUtilities.safeSplit(subs.get(0).trim(), ';');
        if (newConcept.getID() <= -1000) {// It's a vocabulary!
          vocabularies.put(termlist.get(0), newConcept.getID());
          newConcept.setName(termlist.get(0));
          ontology.setConcept(newConcept);
        }
        else {// It's a semantic type (of course)
          newConcept.setName(termlist.get(0));
          if (loadDefinitions && subs.size() == 2) {
            newConcept.setDefinition(StringUtilities.unescape(subs.get(1).trim()));
          }
          ontology.setConcept(newConcept);
        }
      }
      else {
        for (int column = 0; column < columns.size(); column++) {
          if (column == level) {
            // Ignored
          }
          else if (column == sem) {
            List<String> semtypes = StringUtilities.safeSplit(columns.get(column), ';');
            for (String type: semtypes) {
              if (!type.equals("")) {
                int id = Integer.parseInt(type);
                if (semanticTypesNegative)
                  id = -id;
                // check if already in semantic network of thesaurus:
                Concept semtype = ontology.getConcept(id);
                if (semtype == null) {
                  semtype = new Concept(id);
                  semtype.setName(type);
                  ontology.setConcept(semtype);
                }
                Relation relation = new Relation(newConcept.getID(), DefaultTypes.isOfSemanticType, semtype.getID());
                ontology.setRelation(relation);
              }
            }
          }
          else if (column == voc) {
            List<String> vocs = StringUtilities.safeSplit(columns.get(column), ';');
            for (String vocstring: vocs) {
              if (!vocs.equals("")) {
                Integer vocID = vocabularies.get(vocstring);
                if (vocID == null) {
                  if(StringUtilities.isInteger(vocstring)){
                    vocID = Integer.parseInt(vocstring);
                  }
                  else{
                    vocID = makeVocID();
                    Concept vocabulary = new Concept(vocID);
                    vocabulary.setName(vocstring);
                    ontology.setConcept(vocabulary);
                  }
                  vocabularies.put(vocstring, vocID);
                }
                Relation relation = new Relation(newConcept.getID(), DefaultTypes.fromVocabulary, vocID);
                ontology.setRelation(relation);
              }
            }
          }
          else if (column == terms) {
            List<String> subs = StringUtilities.safeSplit(columns.get(column), '?');
            List<String> terms = StringUtilities.safeSplit(subs.get(0).trim(), ';');
            if (terms.size() != 0) {
              newConcept.terms = new ArrayList<TermStore>(terms.size());
            }
            for (int i = 0; i < terms.size(); i++) {
              String text = StringUtilities.unescape(terms.get(i));
              if (text.length() < 256)
                newConcept.terms.add(new TermStore(text));
            }
            if (loadDefinitions && subs.size() == 2) {
              String definition = StringUtilities.unescape(subs.get(1).trim());
              definition = addDatabaseIDs(definition, newConcept);
              newConcept.definition = definition;
            }
          }
        }
        if (ontology.getConcept(newConcept.ID) == null) // not already in
          // thesaurus
          ontology.setConcept(newConcept);
      }
    }
  }

  private Integer makeVocID() {
    if (vocabularies.size() > 0) {
      ArrayList<Integer> list = new ArrayList<Integer>(vocabularies.values());
      Collections.sort(list);
      return list.get(0) - 1;
    }
    else {
      return -1000;
    }

  }

  // Checks whether there are database identifiers in the definition. If so, it
  // adds them to the ontology
  private String addDatabaseIDs(String definition, Concept concept) {
    if (definition.contains("_")) {
      boolean hasIDs = false;
      int minStart = 9999;
      int maxEnd = 0;
      for (String databaseID: DatabaseID.enumerateDatabases().keySet()) {
        String prefix = databaseID + "_";
        int start = 0;
        while (start != -1) {
          start = definition.indexOf(prefix, start);

          // Check whether there is a letter directly before the prefix. If so,
          // search for next:
          while (start > 0 && Character.isLetter(definition.charAt(start - 1)))
            start = definition.indexOf(prefix, start + 1);

          if (start != -1) {
            minStart = Math.min(start, minStart);
            int end = definition.indexOf(';', start);
            int endExclamation = definition.indexOf('!', start);
            if (endExclamation != -1 && (endExclamation < end || end == -1))
              end = endExclamation;

            if (end == -1)
              end = definition.length();
            maxEnd = Math.max(end, maxEnd);
            ontology.setDatabaseIDForConcept(concept.getID(), new DatabaseID(databaseID, definition.substring(start + prefix.length(), end)));
            start = end;
            hasIDs = true;
          }
        }
      }
      if (hasIDs)
        return definition.substring(0, minStart) + definition.substring(maxEnd, definition.length());
    }
    return definition;
  }

  private boolean hasVoc() {
    for (Relation relation: ontology.getRelations())
      if (relation.predicate == DefaultTypes.fromVocabulary)
        return true;
    return false;
  }

  private boolean hasSem() {
    for (Relation relation: ontology.getRelations())
      if (relation.predicate == DefaultTypes.isOfSemanticType)
        return true;
    return false;
  }

}