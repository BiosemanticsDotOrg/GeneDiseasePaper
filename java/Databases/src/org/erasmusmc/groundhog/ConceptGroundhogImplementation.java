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

package org.erasmusmc.groundhog;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.ComparatorFactory;
import org.erasmusmc.collections.SortedListSet;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.ConceptProfile;
import org.erasmusmc.ontology.ConceptVectorRecord;
import org.erasmusmc.ontology.Ontology;

public class ConceptGroundhogImplementation implements ConceptGroundhog {
  private Groundhog groundHog;
  private SortedListSet<Integer> groundhogEntries=null;

  
  public Set<Integer> getCUIswithConceptProfiles(Collection<Integer> ids) {
    Map<Integer, ConceptProfile> submap = getSubMap(ids);
    return submap.keySet();
  }

  public boolean hasConceptProfile(Integer conceptID) {
    return (groundHog.get(conceptID) != null);
  }
 
  public ConceptGroundhogImplementation(Groundhog groundhog) {
   
    this.groundHog = groundhog;
  }
  public Map<Integer, ConceptProfile> getSubMap(Collection<Integer> keys){
    return ConceptProfile.makeConceptProfilesFromRecords(groundHog.getSubMap(keys));
  }
  public Groundhog getGroundHog(){
    return groundHog;
  }
  public ConceptProfile getConceptProfile(Integer cui) {
    ConceptVectorRecord conceptVectorRecord = groundHog.get(cui);
    if (conceptVectorRecord!=null){
    return new ConceptProfile(conceptVectorRecord);
    }
    else {
      return null;
    }
  }

  public ConceptProfile getConceptProfile(Concept concept) {
    return getConceptProfile(concept.getID());
  }

  public Ontology getOntology() {
    return groundHog.ontology;
  }
  public void setOntology(Ontology ontology){
    groundHog.setOntology(ontology);
  }
  public Map<Integer, ConceptProfile> getConceptProfiles(Collection<Integer> ids) {
    
    return getSubMap(ids);
  }

  public void setConceptProfile(ConceptProfile conceptprofile) {
    groundHog.set(conceptprofile.cui, conceptprofile.conceptProfileToRecord());
  }

  public Set<Integer> getAllEntryIDs() {
    if(groundhogEntries==null){
      initializeGroundhogEntriesSet();
    }
    return groundhogEntries;
  }
  public void initializeGroundhogEntriesSet() {
    // groundhogEntries = new
    // HashSet<Integer>((int)Math.round(3d/2d*(double)groundhog.size()));
    groundhogEntries = new SortedListSet<Integer>(ComparatorFactory.getAscendingIntegerComparator());
    Iterator<ConceptVectorRecord> iterator = groundHog.getIterator();
    while (iterator.hasNext()) {
      groundhogEntries.add(iterator.next().getID());
    }
  }

  
}
