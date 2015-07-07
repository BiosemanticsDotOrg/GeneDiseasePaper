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

package org.erasmusmc.rmi.groundhog.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.ontology.ConceptVectorRecord;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.rmi.groundhog.server.RMIGroundhogInterface;
import org.erasmusmc.storecaching.StoreMapCaching;

public class RMIGroundhog extends StoreMapCaching<Integer, ConceptVectorRecord> {
  private RMIGroundhogInterface rmiGroundhogServer;
  private Ontology ontology;

  public RMIGroundhog() {
    try {
      rmiGroundhogServer = (RMIGroundhogInterface) Naming.lookup("rmi://mi-bios2.erasmusmc.nl:111/RMIGroundhogServerService");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Ontology getOntology() {
    return this.ontology;
  }

  public void setOntology(Ontology ontology) {
    this.ontology = ontology;
  }

  public Set<Integer> getRecordIDsForConcept(Integer conceptID) {
    try {
      return rmiGroundhogServer.getRecordIDsForConcept(conceptID);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public int size() {
    int i = 0;
    try {
      i = rmiGroundhogServer.size();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return i;
 }

  @Override
  protected Map<Integer, ConceptVectorRecord> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
    try {
      return rmiGroundhogServer.getEntriesFromStoreWithIDs(ids);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected ConceptVectorRecord getEntryFromStoreWithID(Integer id) {
    try {
      ConceptVectorRecord cvr = rmiGroundhogServer.getEntryFromStoreWithID(id);
      if (cvr != null)
        cvr.getConceptVector().ontology = ontology;
      return cvr;
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }
  public void saveEntryNoCaching(ConceptVectorRecord value){
    try {
      rmiGroundhogServer.setConceptVectorRecord(value);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
  @Override
  protected void setEntryInStore(Integer id, ConceptVectorRecord value) {
    saveEntryNoCaching(value);
  }
  public void setBulkImportMode(boolean toggle){
    try {
      rmiGroundhogServer.toggleBulkImportMode(toggle);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }
  public void setReindexBatchSize(int size){
    try {
      rmiGroundhogServer.setReindexBatchSize(size);
    } catch (RemoteException e) {
        e.printStackTrace();
    }
  }
  public Map<Integer, Boolean> checkForEntries(Set<Integer> ids) {
    Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
    Set<Integer> set = null;
    try {
      set = rmiGroundhogServer.checkForEntries(ids);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    for(Integer i : ids) {
      if(set.contains(i)) map.put(i, true);
      else map.put(i, false);
    }
    return map;
    
  }

  public boolean hasEntry(Integer conceptID) {
    boolean result = false;
    try {
      result = rmiGroundhogServer.hasEntry(conceptID);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return result;
  }


}
