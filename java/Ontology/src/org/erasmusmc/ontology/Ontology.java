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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.math.space.Space;
import org.erasmusmc.rmi.ontology.conceptrelations.ConceptRelation;

/**
 * Abstract ontology class
 * @author Schuemie
 *
 */
public abstract class Ontology implements Iterable<Concept>{
  
  /** 
   * Retrieves a concept from the ontology with the given concept ID.
   * @param id  The concept ID
   * @return    The concept. Return null if no concept exists with the give concept ID
   */
  public abstract Concept getConcept(int id);
  
  /**
   * Insert a concept in the ontology. If a concept already exists with the same concept ID, it is overwritten 
   * @param concept
   */
  public abstract void setConcept(Concept concept);
  
  /** 
   * Removed a concept from the ontology
   * @param id  The concept ID of the concept to be removed
   */
  public abstract void removeConcept(int id);
  
  /**
   * Retrieves the name of the ontology
   * @return
   */
  public abstract String getName();
  
  /**
   * Sets the name of the ontology
   * @param name
   */
  public abstract void setName(String name);
  
  /**
   * Retrieves all relations for a concept with the given ID, where the concept acts as object
   * @param id  The concept ID
   * @return
   */
  public abstract List<Relation> getRelationsForConceptAsObject(int id);
  
  /**
   * Retrieves all relations of the given type for a concept with the given ID, where the concept acts as object
   * @param id  The concept ID
   * @param relationtype    The type of relation
   * @return    The list of relations
   */
  public abstract List<Relation> getRelationsForConceptAsObject(int id, int relationtype);
  
  /**
   * Retrieves all relations for a concept with the given ID, where the concept acts as subject
   * @param id  The concept ID
   * @return
   */
  public abstract List<Relation> getRelationsForConceptAsSubject(int id);
  
  /**
   * Retrieves all relations of the given type for a concept with the given ID, where the concept acts as subject
   * @param id  The concept ID
   * @param relationtype    The type of relation
   * @return
   */
  public abstract List<Relation> getRelationsForConceptAsSubject(int id, int relationtype);
  
  /**
   * Returns all the relation in the ontology
   * @return
   */
  public abstract List<Relation> getRelations();
  
  /**
   * Insert a relation in the ontology
   * @param relation
   */
  public abstract void setRelation(Relation relation);
 
  /** 
   * Returns an iterator that can be used to iterate over all concepts in the ontology
   * @return
   */
  public abstract Iterator<Concept> getConceptIterator();
  
  /**
   * Retrieves a list of concepts from the ontology with the given concept IDs. This works faster than
   * retrieving concepts one by one using the getConcept method. If no concept can be found for a concept ID 
   * in the list, it is simply not added to the list of concepts.
   * @param ids A list of conccept IDs
   * @return    The concepts with the given IDs
   */
  public abstract Map<Integer, Concept> getConcepts(Collection<Integer> ids);
  
  /**
   * Returns the size of the ontology.
   * @return    The number of concepts in the ontology
   */
  public abstract int size();
  
  /**
   * Retrieves a subset of the concepts in the ontology.
   * @param offset
   * @param limit
   * @return    A map from concept IDs to concepts
   */
  public abstract Map<Integer, Concept> getConceptSubset(int offset, int limit);
  
  /**
   * Retrieves the (external) database IDs for this concept
   * @param id  The concept ID
   * @return    A list of database IDs
   */
  public abstract List<DatabaseID> getDatabaseIDsForConcept(int id);
  
  /** 
   * Inserts a database ID for a given concept
   * @param id  The concept ID
   * @param databaseID
   */
  public abstract void setDatabaseIDForConcept(int id, DatabaseID databaseID);
  
  /**
   * Retrieves the list of concepts that are associate
   * @param databaseID
   * @return
   */
  public abstract Set<Integer> getConceptIDs(DatabaseID databaseID);

  public Space<Concept> space = new Space<Concept>() {
    public int getDimensions() {
      return size();
    }

    public int indexOfObject(Concept object) {
      return object.getID();
    }

    public Concept objectForIndex(int index) {
      return getConcept(index);
    }

    public Iterator<Concept> iterator() {
      return getConceptIterator();
    }

    public String getDimensionsCaption() {
      return "Concept";
    }

    public String getValuesCaption() {
      return "Weight";
    }

    public void setDimensionsCaption(String dimensionsCaption) {
          
    }
    
  };
  
  /**
   * Retrieves the list of parent relations for the given set of concepts
   * @param List of the concept ids
   * @return
   */
  public abstract Set<ConceptRelation> getParentRelationsForConceptSet(IntList ids);
  
  /**
   * Prints some statistics about the ontology
   */
  public void printStatistics(){
  	int conceptCount = 0;
  	int termCount = 0;
  	for (Concept concept : this){
  		conceptCount++;
  		termCount+= concept.getTerms().size();
  	}
  	System.out.println("Number of concepts: " + conceptCount + ", number of terms: " + termCount);
  }
}
