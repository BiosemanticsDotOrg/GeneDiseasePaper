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

package org.erasmusmc.rmi.ontology.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyClient;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.rmi.ontology.conceptrelations.ConceptRelation;

public class RMIOntologyImplementation extends UnicastRemoteObject implements RMIOntologyInterface {
  private static final long serialVersionUID = -3180335592740263450L;
  private Ontology ontology;
  private String name;
  
  private ReentrantLock lock = new ReentrantLock();

  public RMIOntologyImplementation(String server, String user, String password, String name) throws RemoteException {
    ontology = new OntologyClient(server, user, password, name);
    this.name = name;
  }
  
  public RMIOntologyImplementation(Ontology ontology) throws RemoteException {
    this.ontology = ontology;
    this.name = ontology.getName();
  }

  public RMIOntologyImplementation(String psfFileName) throws RemoteException {
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadDefinitions = true;
    loader.loadHierarchy = true;
    loader.loadFromPSF(psfFileName);
    ontology = loader.ontology;
    this.name = ontology.getName();
  }

  public List<DatabaseID> getDBLinksForConcept(int id) throws RemoteException {
    List<DatabaseID> result = null;
    try {
      lock.lock();
      result = ontology.getDatabaseIDsForConcept(id);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public void setDBLinkForConcept(int id, DatabaseID dblink) throws RemoteException {
    try {
      lock.lock();
      ontology.setDatabaseIDForConcept(id, dblink);
    } finally {
      lock.unlock();
    }

  }

  public Concept getConcept(int id) throws RemoteException {
    Concept result = null;
    try {
      lock.lock();
      result = ontology.getConcept(id);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public Map<Integer, Concept> getConcepts(Collection<Integer> ids) throws RemoteException {
    Map<Integer, Concept> result = null;
    try {
      lock.lock();
      result = ontology.getConcepts(ids);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public Map<Integer, Concept> getConcepts(int offset, int limit) throws RemoteException {
    Map<Integer, Concept> result = null;
    try {
      lock.lock();
      result = ontology.getConceptSubset(offset, limit);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public String getName() {
    return name;
  }

  public List<Relation> getRelations() {
    List<Relation> result = null;
    try {
      lock.lock();
      result = ontology.getRelations();
    } finally {
      lock.unlock();
    }
    return result;
  }

  public List<Relation> getRelationsForConceptAsObject(int id, int relationtype) throws RemoteException {
    List<Relation> result = null;
    try {
      lock.lock();
      result = ontology.getRelationsForConceptAsObject(id, relationtype);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public List<Relation> getRelationsForConceptAsObject(int id) throws RemoteException {
    List<Relation> result = null;
    try {
      lock.lock();
      result = ontology.getRelationsForConceptAsObject(id);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public List<Relation> getRelationsForConceptAsSubject(int id, int relationtype) throws RemoteException {
    List<Relation> result = null;
    try {
      lock.lock();
      result = ontology.getRelationsForConceptAsSubject(id, relationtype);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public List<Relation> getRelationsForConceptAsSubject(int id) throws RemoteException {
    List<Relation> result = null;
    try {
      lock.lock();
      result = ontology.getRelationsForConceptAsSubject(id);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public Set<Integer> getConceptIDs(DatabaseID databaseID) throws RemoteException {
    Set<Integer>  result = null;
    try {
      lock.lock();
      result = ontology.getConceptIDs(databaseID);
    } finally {
      lock.unlock();
    }
    return result;
  }

  public int size() throws RemoteException {
    int result = 0;
    try {
      lock.lock();
      result = ontology.size();
    } finally {
      lock.unlock();
    }
    return result;
  }

  public List<Relation> getRelationsForConceptsAsObject(IntList ids, int relationtype) throws RemoteException {
    List<Relation> result;
    try {
      lock.lock();
      result = new ArrayList<Relation>();
      for (Integer id: ids)
        result.addAll(getRelationsForConceptAsObject(id, relationtype));
    } finally {
      lock.unlock();
    }
    return result;
  }

  public List<Relation> getRelationsForConceptsAsSubject(IntList ids, int relationtype) throws RemoteException {
    List<Relation> result;
    try {
      lock.lock();
      result = new ArrayList<Relation>();
      for (Integer id: ids)
        result.addAll(getRelationsForConceptAsSubject(id, relationtype));
    } finally {
      lock.unlock();
    }
    return result;
  }

  public Set<ConceptRelation> getParentRelationsForConceptSet(IntList ids) throws RemoteException  {
    Set<ConceptRelation>  result = null;
    try {
      lock.lock();
      result = ontology.getParentRelationsForConceptSet(ids);
    } finally {
      lock.unlock();
    }
    return result;
  }

}
