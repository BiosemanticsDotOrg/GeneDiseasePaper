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

package org.erasmusmc.rmi.groundhog.server;

import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.ComparatorFactory;
import org.erasmusmc.collections.SortedListSet;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.ontology.ConceptVectorRecord;

public class RMIGroundhogImplementation extends UnicastRemoteObject implements RMIGroundhogInterface {
  private static final long serialVersionUID = 5686029925410426146L;
  private GroundhogManager manager;
  private Groundhog groundhog;
  private boolean allowSavingOfRecords = false;
  private Set<Integer> groundhogEntries = null;
  private int size = -1;

  public Set<Integer> checkForEntries(Collection<Integer> ids) throws RemoteException {
    if (groundhogEntries != null) {
      SortedListSet<Integer> result = new SortedListSet<Integer>(ComparatorFactory.getAscendingIntegerComparator());
      for (int id: ids) {
        if (groundhogEntries.contains(id))
          result.add(id);
      }
      return result;
    }
    else {
      return new HashSet<Integer>(groundhog.checkForEntries(ids));
    }
  }

  public boolean hasEntry(Integer conceptID) throws RemoteException {
    if (groundhogEntries != null) {
      return groundhogEntries.contains(conceptID);
    }
    else
      return groundhog.hasEntry(conceptID);
  }

  public void initializeGroundhogEntriesSet() {
    System.out.println("Groundhog server initializing");
    // groundhogEntries = new
    // HashSet<Integer>((int)Math.round(3d/2d*(double)groundhog.size()));
    groundhogEntries = new SortedListSet<Integer>(ComparatorFactory.getAscendingIntegerComparator());
    Iterator<ConceptVectorRecord> iterator = groundhog.getIterator();
    while (iterator.hasNext()) {
      groundhogEntries.add(iterator.next().getID());
    }
    System.out.println("Groundhog server ready");
  }

  protected RMIGroundhogImplementation(String groundhogName, Boolean allowSavingofRecords, Boolean initialize) throws RemoteException {
    this(groundhogName);
    this.allowSavingOfRecords = allowSavingofRecords;
    if (initialize)
      initializeGroundhogEntriesSet();
  }

  protected RMIGroundhogImplementation(String groundhogName, boolean allowSavingofRecords) throws RemoteException {
    this(groundhogName);
    this.allowSavingOfRecords = allowSavingofRecords;
    initializeGroundhogEntriesSet();
  }

  protected RMIGroundhogImplementation(String groundhogName) throws RemoteException {
    super();
    File groundhogdir = new File(groundhogName);
    if (!groundhogdir.isDirectory()) {
      System.err.println("No such directory " + groundhogdir.getAbsolutePath());
      System.exit(1);
    }
    manager = new GroundhogManager("");
    groundhog = manager.getGroundhog(groundhogName);
    if (groundhog != null)
      System.out.println("Connected to Groundhog: " + groundhogName);
    else
      System.out.println("Error connecting to Groundhog: " + groundhogName);
  }

  public Map<Integer, ConceptVectorRecord> getEntriesFromStoreWithIDs(Collection<Integer> ids) throws RemoteException {
    return groundhog.getSubMap(ids);
  }

  public ConceptVectorRecord getEntryFromStoreWithID(Integer id) throws RemoteException {
    return groundhog.getEntryFromStoreWithID(id);
  }

  public Set<Integer> getRecordIDsForConcept(Integer conceptID) throws RemoteException {
    Set<Integer> set = groundhog.getRecordIDsForConcept(conceptID);
    return set;
  }

  public int size() throws RemoteException {
    if (allowSavingOfRecords || size == -1)
      size = groundhog.size();
    return size;
  }

  public void setConceptVectorRecord(ConceptVectorRecord conceptVectorRecord) throws RemoteException {
    if (allowSavingOfRecords)
      groundhog.saveConceptVectorRecord(conceptVectorRecord);
    if(groundhogEntries!=null)
      groundhogEntries.add(conceptVectorRecord.getID());
  }

  public void toggleBulkImportMode(boolean toggle) throws RemoteException {
    if (allowSavingOfRecords)
      groundhog.setBulkImportMode(toggle);
  }

  public void setReindexBatchSize(int size) throws RemoteException {
    if (allowSavingOfRecords)
      groundhog.setReindexBatchSize(size);
  }

  public Set<Integer> getEntryIDs() throws RemoteException {
    if (groundhogEntries == null)
      initializeGroundhogEntriesSet();
    return groundhogEntries;
  }
}
