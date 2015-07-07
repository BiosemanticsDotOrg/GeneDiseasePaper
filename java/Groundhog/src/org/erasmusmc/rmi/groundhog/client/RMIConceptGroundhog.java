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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.ComparatorFactory;
import org.erasmusmc.collections.SortedListSet;
import org.erasmusmc.groundhog.ConceptGroundhog;
import org.erasmusmc.ontology.ConceptProfile;
import org.erasmusmc.ontology.ConceptVectorRecord;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.rmi.groundhog.server.RMIGroundhogInterface;
import org.erasmusmc.storecaching.StoreMapCaching;

public class RMIConceptGroundhog extends StoreMapCaching<Integer, ConceptProfile> implements ConceptGroundhog {
  private static final long serialVersionUID = 941715750363144985L;
  private RMIGroundhogInterface rmiGroundhogServer;
  private Set<Integer> entryIDs = null;
  private Ontology ontology;
  private int batchSize = 500;

  public RMIConceptGroundhog(String server, int port, String serviceName) throws Exception {
      rmiGroundhogServer = (RMIGroundhogInterface) Naming.lookup("rmi://" + server + ":" + port + "/" + serviceName);
  }

  public void initializeEntryIndex(){
	  try {
	    entryIDs=rmiGroundhogServer.getEntryIDs();
	  } catch (RemoteException e) {
	    e.printStackTrace();
	  }
  }

  public Set<Integer> getAllEntryIDs(){
	  if(entryIDs==null)
	    initializeEntryIndex();
	  return entryIDs;
	}

  protected ConceptProfile getEntryFromStoreWithID(Integer id) {
    try {
      ConceptVectorRecord cvr = rmiGroundhogServer.getEntryFromStoreWithID(id);
      if (cvr == null)
        return null;
      cvr.getConceptVector().ontology = ontology;
      return new ConceptProfile(cvr);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
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
  public Map<Integer, ConceptProfile> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
    Map<Integer, ConceptProfile> result = new HashMap<Integer, ConceptProfile>();
    try {
      List<ConceptVectorRecord> records = new ArrayList<ConceptVectorRecord>();
      if (ids.size() < batchSize)
        records.addAll(rmiGroundhogServer.getEntriesFromStoreWithIDs(ids).values());
      else{
        Iterator<Integer> idit=ids.iterator();
        for (int i = 0; i < ids.size(); i += batchSize) {
          List<Integer> batch = new ArrayList<Integer>();
          int sublist = i + batchSize;
          if (sublist > ids.size())
            sublist = ids.size();
          for(int j = i; j<sublist;j++)
            batch.add(idit.next());
          records.addAll(rmiGroundhogServer.getEntriesFromStoreWithIDs(batch).values());
        }
      }
      for (ConceptVectorRecord record: records) {
        record.getConceptVector().ontology = getOntology();
        result.put(record.getID(), new ConceptProfile(record));
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return result;
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
  protected void setEntryInStore(Integer id, ConceptProfile value) {
    // not implemented for a reason ...
  }

  public ConceptProfile getConceptProfile(Integer cui) {
    return get(cui);
  }

  public Map<Integer, ConceptProfile> getConceptProfiles(Collection<Integer> ids) {
    return getSubMap(ids);
  }

  public Set<Integer> getCUIswithConceptProfiles(Collection<Integer> ids) {
    if (entryIDs != null) {
      SortedListSet<Integer> result = new SortedListSet<Integer>(ComparatorFactory.getAscendingIntegerComparator());
      for (int id: ids) {
        if (entryIDs.contains(id))
          result.add(id);
      }
      return result;
    }
    else {
      Set<Integer> set = null;
      try {
        set = rmiGroundhogServer.checkForEntries(ids);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      return set;
    }
  }

  public boolean hasConceptProfile(Integer conceptID) {

    if (entryIDs != null)
      return entryIDs.contains(conceptID);
    else {
      boolean result = false;

      try {
        result = rmiGroundhogServer.hasEntry(conceptID);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      return result;
    }
  }

  public void setConceptProfile(ConceptProfile conceptprofile) {
    try {
      rmiGroundhogServer.setConceptVectorRecord(conceptprofile.conceptProfileToRecord());
      if (entryIDs!=null){
        entryIDs.add(conceptprofile.cui);
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

}
