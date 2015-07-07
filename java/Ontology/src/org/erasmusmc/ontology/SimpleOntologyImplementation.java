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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.rmi.ontology.conceptrelations.ConceptRelation;
import org.erasmusmc.storecaching.StoreMapCaching;

public class SimpleOntologyImplementation extends Ontology implements Serializable{
  private static final long serialVersionUID = 7546288672266505163L;
  public String name = "";
  protected ConceptMapStore conceptMapStore;

  public SimpleOntologyImplementation() {
    conceptMapStore = new ConceptMapStore();
  }

  public Concept getConcept(int id) {

    return conceptMapStore.get(id);
  }

  public void setConcept(Concept concept) {
    conceptMapStore.set(concept.getID(), concept);
  }

  public String getName() {
    return name;
  }

  public List<Relation> getRelationsForConcept(int id) {
    return null;
  }

  @Override
  public Iterator<Concept> getConceptIterator() {
    return null;
  }

  @Override
  public void setName(String name) {
    this.name = name;

  }

  @Override
  public List<Relation> getRelationsForConceptAsObject(int id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Relation> getRelationsForConceptAsObject(int id, int relationtype) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Relation> getRelationsForConceptAsSubject(int id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Relation> getRelationsForConceptAsSubject(int id, int relationtype) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Relation> getRelations() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setRelation(Relation relation) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeConcept(int id) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Map<Integer, Concept> getConcepts(Collection<Integer> ids) {
    Map<Integer, Concept> map = new HashMap<Integer, Concept>();
    for(int i: ids) {
      map.put(i, getConcept(i));
    }
    return map;
  }

  @Override
  public Map<Integer, Concept> getConceptSubset(int offset, int limit) {
    System.out.println("calling SimpleOntology.getConceptSubset(int offset, in limit) is not implemented");
    return null;
  }

  @Override
  public int size() {
    System.out.println("calling SimpleOntology.size() is useless...");
    return conceptMapStore.size();
  }

  @Override
  public List<DatabaseID> getDatabaseIDsForConcept(int id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setDatabaseIDForConcept(int id, DatabaseID databaseID) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Set<Integer> getConceptIDs(DatabaseID databaseID) {
    // TODO Auto-generated method stub
    return null;
  }
  private class ConceptMapStore extends StoreMapCaching<Integer,Concept> implements Serializable {
    private static final long serialVersionUID = -6433371120598729352L;

    protected Concept getEntryFromStoreWithID(Integer id) {
    
      return new Concept(id);
    }

    protected Map<Integer, Concept> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
      return null;
    }

    public int size() {
      return 0;
    }

    protected void setEntryInStore(Integer id, Concept value) {
      
    }
  }
  @Override
  public Set<ConceptRelation> getParentRelationsForConceptSet(IntList ids) {
    // TODO Auto-generated method stub
    return null;
  }

  public Iterator<Concept> iterator() {
    return getConceptIterator();
  }
  
  
}
