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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.rmi.ontology.conceptrelations.ConceptRelation;

/**
 * Class for storing an ontology in memory
 * 
 * @author Schuemie
 * 
 */
public class OntologyStore extends Ontology {

  private Map<Integer, Concept> id2concept = new TreeMap<Integer, Concept>();
  private String name = "";
  private Map<Integer, List<Relation>> id2subjectrelations = new HashMap<Integer, List<Relation>>();
  private Map<Integer, List<Relation>> id2objectrelations = new HashMap<Integer, List<Relation>>();
  private Map<Integer, List<DatabaseID>> id2DatabaseID = new HashMap<Integer, List<DatabaseID>>();
  private Map<DatabaseID, Set<Integer>> databaseID2id = null;

  /**
   * Creates an index from database IDs to concepts. Will drastically speed up
   * the getConceptIDs method, but will cost more memory.
   */
  public void createIndexForDatabaseIDs() {
    databaseID2id = new HashMap<DatabaseID, Set<Integer>>();
    for (Map.Entry<Integer, List<DatabaseID>> entry: id2DatabaseID.entrySet())
      for (DatabaseID databaseID: entry.getValue()) {
        Set<Integer> conceptIDs = databaseID2id.get(databaseID);
        if (conceptIDs == null) {
          conceptIDs = new HashSet<Integer>();
          databaseID2id.put(databaseID, conceptIDs);
        }
        conceptIDs.add(entry.getKey());
      }
  }

  /**
   * Removes the index created with the createIndexForDatabaseIDs method and
   * frees the memory used.
   */
  public void deleteIndexForDatabaseIDs() {
    databaseID2id = null;
    System.gc();
  }

  public Map<Integer, Concept> getConcepts(Collection<Integer> ids) {
    Map<Integer, Concept> map = new HashMap<Integer, Concept>();
    for (int i: ids) {
      map.put(i, getConcept(i));
    }
    return map;
  }

  public Map<Integer, Concept> getConceptSubset(int offset, int limit) {
    Map<Integer, Concept> map = new HashMap<Integer, Concept>();
    Object[] collection = id2concept.values().toArray();
    for (int i = offset; i < offset + limit; i++) {
      map.put(i, (Concept) collection[i]);
    }
    return map;
  }

  public int size() {
    return id2concept.size();
  }

  @Override
  public Concept getConcept(int id) {
    return id2concept.get(id);
  }

  @Override
  public void setConcept(Concept concept) {
    id2concept.put(concept.getID(), concept);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Iterator<Concept> getConceptIterator() {
    return new ConceptIterator();
  }

  public class ConceptIterator implements Iterator<Concept> {
    List<Integer> ids;
    Integer last = null;
    Iterator<Integer> it;

    public ConceptIterator() {
      ids = new ArrayList<Integer>(id2concept.keySet());
      it = ids.iterator();
    }

    public boolean hasNext() {
      return it.hasNext();
    }

    public Concept next() {
      last = it.next();
      return getConcept(last);
    }

    public void remove() {
      it.remove();
      removeConcept(last);
    }
  }

  @Override
  public void setRelation(Relation relation) {
    if (!existsRelation(relation)) {
      List<Relation> subjectrelations = id2subjectrelations.get(relation.subject);
      if (subjectrelations == null) {
        subjectrelations = new ArrayList<Relation>();
        id2subjectrelations.put(relation.subject, subjectrelations);
      }
      subjectrelations.add(relation);

      List<Relation> objectrelations = id2objectrelations.get(relation.object);
      if (objectrelations == null) {
        objectrelations = new ArrayList<Relation>();
        id2objectrelations.put(relation.object, objectrelations);
      }
      objectrelations.add(relation);
    }
  }
  
  public void removeRelation(Relation relation){
  	List<Relation> subjectrelations = id2subjectrelations.get(relation.subject);
  	subjectrelations.remove(relation);
  	if (subjectrelations.size() == 0)
  		id2subjectrelations.remove(relation.subject);
  	List<Relation> objectrelations = id2objectrelations.get(relation.object);
  	objectrelations.remove(relation);
  	if (objectrelations.size() == 0)
  		id2objectrelations.remove(relation.object);  	
  }

  public boolean existsRelation(Relation relation) {
    List<Relation> subjectrelations = id2subjectrelations.get(relation.subject);
    List<Relation> objectrelations = id2objectrelations.get(relation.object);

    if (subjectrelations != null && objectrelations != null) {
      if (subjectrelations.size() < objectrelations.size()) {
        for (Relation testRelation: subjectrelations) {
          if (testRelation.equals(relation)) {
            return true;
          }
        }
      }
      else {
        for (Relation testRelation: objectrelations) {
          if (testRelation.equals(relation)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public List<Relation> getRelationsForConceptAsObject(int id) {
    List<Relation> relations = id2objectrelations.get(id);
    if (relations == null)
      return new ArrayList<Relation>();
    return relations;
  }

  @Override
  public List<Relation> getRelationsForConceptAsObject(int id, int relationtype) {
    List<Relation> relations = id2objectrelations.get(id);
    if (relations == null)
      return new ArrayList<Relation>();
    List<Relation> result = new ArrayList<Relation>();
    for (Relation relation: relations)
      if (relation.predicate == relationtype)
        result.add(relation);
    return result;
  }

  @Override
  public List<Relation> getRelationsForConceptAsSubject(int id) {
    List<Relation> relations = id2subjectrelations.get(id);
    if (relations == null)
      return new ArrayList<Relation>();
    return relations;
  }

  @Override
  public List<Relation> getRelationsForConceptAsSubject(int id, int relationtype) {
    List<Relation> relations = id2subjectrelations.get(id);
    if (relations == null)
      return new ArrayList<Relation>();
    List<Relation> result = new ArrayList<Relation>();
    for (Relation relation: relations)
      if (relation.predicate == relationtype)
        result.add(relation);
    return result;
  }

  @Override
  public List<Relation> getRelations() {
    List<Relation> result = new ArrayList<Relation>();
    for (List<Relation> relations: id2subjectrelations.values())
      result.addAll(relations);
    return result;
  }

  @Override
  public void removeConcept(int id) {
    id2concept.remove(id);
    List<Relation> relations = id2subjectrelations.remove(id);
    if (relations != null) {
      for (Relation relation: relations) {
        List<Relation> checkrelations = id2objectrelations.get(relation.object);
        Iterator<Relation> relationIterator = checkrelations.iterator();
        while (relationIterator.hasNext()) {
          Relation checkRelation = relationIterator.next();
          if (checkRelation.subject == id) {
            relationIterator.remove();
          }
        }
      }
    }
    List<Relation> objectRelations = id2objectrelations.remove(id);
    if (objectRelations != null) {
      for (Relation relation: objectRelations) {
        List<Relation> checkrelations = id2subjectrelations.get(relation.subject);
        Iterator<Relation> relationIterator = checkrelations.iterator();
        while (relationIterator.hasNext()) {
          Relation checkRelation = relationIterator.next();
          if (checkRelation.object == id) {
            relationIterator.remove();
          }
        }
      }
    }
    id2DatabaseID.remove(id);
  }

  @Override
  public List<DatabaseID> getDatabaseIDsForConcept(int id) {
    List<DatabaseID> dbids = id2DatabaseID.get(id);
    if (dbids == null) {
      dbids = new ArrayList<DatabaseID>();
    }
    return dbids;
  }

  @Override
  public void setDatabaseIDForConcept(int id, DatabaseID databaseID) {
    List<DatabaseID> databaseIDs = id2DatabaseID.get(id);
    if (databaseIDs == null) {
      databaseIDs = new ArrayList<DatabaseID>();
      id2DatabaseID.put(id, databaseIDs);
    }
    boolean present = false;
    for (DatabaseID databaseID2: databaseIDs) {
      if (databaseID2.equals(databaseID)) {
        present = true;
      }
    }
    if (!present) {
      databaseIDs.add(databaseID);

      // add to index if exists:
      if (databaseID2id != null) {
        Set<Integer> conceptIDs = databaseID2id.get(databaseID);
        if (conceptIDs == null) {
          conceptIDs = new HashSet<Integer>();
          databaseID2id.put(databaseID, conceptIDs);
        }
        conceptIDs.add(id);
      }
    }
  }

  @Override
  public Set<Integer> getConceptIDs(DatabaseID databaseID) {
    // first check if index exists:
    if (databaseID2id != null) {
      Set<Integer> result = databaseID2id.get(databaseID);
      if (result == null)
        return new HashSet<Integer>();
      else
        return result;
    }
    else { // Run through the whole thesaurus
      Set<Integer> result = new HashSet<Integer>();
      for (Map.Entry<Integer, List<DatabaseID>> entry: id2DatabaseID.entrySet()) {
        for (DatabaseID otherDatabaseID: entry.getValue())
          if (otherDatabaseID.equals(databaseID))
            result.add(entry.getKey());
      }
      return result;
    }
  }

  @Override
  public Set<ConceptRelation> getParentRelationsForConceptSet(IntList ids) {
    Set<ConceptRelation> set = new HashSet<ConceptRelation>();
    Set<Integer> seenSet = new HashSet<Integer>();
    for (int id: ids) {
      getParentRelationsForConcept(id, set, seenSet);
    }
    return set;
  }

  private void getParentRelationsForConcept(int conceptid, Set<ConceptRelation> set, Set<Integer> seenSet) {
    List<Relation> parentlist = getRelationsForConceptAsObject(conceptid, DefaultTypes.isParentOf);
    seenSet.add(conceptid);
    for (Relation relation: parentlist) {
      if (set.add(new ConceptRelation(relation.subject, conceptid))) {
        // Avoid circulair references : If a parent is already seen, do not
        // traverse up anymore.
        if (!seenSet.contains(relation.subject))
          getParentRelationsForConcept(relation.subject, set, seenSet);
      }
    }
  }

  public Iterator<Concept> iterator() {
    return getConceptIterator();
  }
}
