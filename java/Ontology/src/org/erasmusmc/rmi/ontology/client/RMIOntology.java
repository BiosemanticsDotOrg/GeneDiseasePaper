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

package org.erasmusmc.rmi.ontology.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.rmi.ontology.conceptrelations.ConceptRelation;
import org.erasmusmc.rmi.ontology.server.RMIOntologyInterface;
import org.erasmusmc.storecaching.StoreMapCaching;

/** 
 * RMI interface for accessing an ontology on a remote computer
 * @author Schuemie
 *
 */
public class RMIOntology extends Ontology {
  protected RMIOntologyInterface rmiOntologyServer;

  @Override
  public Set<Integer> getConceptIDs(DatabaseID databaseID) {
    Set<Integer> result = null;
    try {
      result = rmiOntologyServer.getConceptIDs(databaseID);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return result;
  }

  protected ConceptCache conceptCache;
  private String name;
  private boolean makeEmptyConcepts=true;
  private String emptyConceptString = "Concept not found in Ontology";

  public RMIOntology() throws Exception {
      this("mojojojo.biosemantics.org", 1011, "RMIOntologyServerService");
  }

  /**
   * Create a connection to the given server.
   * @param server  The name or IP address of the server running the ontology service
   */
  public RMIOntology(String server, int port, String serviceName) throws Exception {
    conceptCache = new ConceptCache(this);
    rmiOntologyServer = (RMIOntologyInterface) Naming.lookup("rmi://" + server + ":"+port+"/"+serviceName);
  }

  @Override
  public List<DatabaseID> getDatabaseIDsForConcept(int id) {
    try {
      return rmiOntologyServer.getDBLinksForConcept(id);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void setDatabaseIDForConcept(int id, DatabaseID dblink) {
    try {
      rmiOntologyServer.setDBLinkForConcept(id, dblink);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Concept getConcept(int id) {
    return conceptCache.get(id);
  }

  @Override
  public Map<Integer, Concept> getConcepts(Collection<Integer> ids) {
    return conceptCache.getSubMap(ids);
  }

  @Override
  public int size() {
    try {
      return rmiOntologyServer.size();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  public Map<Integer, Concept> getConceptSubset(int offset, int limit) {
    Map<Integer, Concept> result = null;
    try {
      result = rmiOntologyServer.getConcepts(offset, limit);
    } catch (RemoteException e) {
      e.printStackTrace();
    }  
    return result;
  }

  public Iterator<Concept> getConceptIterator() {
    return new ConceptIterator(this);
  }

  public String getName() {
    if (name != null)
      return name;
    try {
      this.name = rmiOntologyServer.getName();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return name;
  }

  public List<Relation> getRelations() {
    try {
      return rmiOntologyServer.getRelations();
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<Relation> getRelationsForConceptAsObject(int id, int relationtype) {
    try {
      return rmiOntologyServer.getRelationsForConceptAsObject(id, relationtype);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }
  public List<Relation> getRelationsForConceptsAsObject(IntList ids, int relationtype) {
    try {
      return rmiOntologyServer.getRelationsForConceptsAsObject(ids, relationtype);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<Relation> getRelationsForConceptAsObject(int id) {
    try {
      return rmiOntologyServer.getRelationsForConceptAsObject(id);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<Relation> getRelationsForConceptAsSubject(int id, int relationtype) {
    try {
      return rmiOntologyServer.getRelationsForConceptAsSubject(id, relationtype);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }
  public List<Relation> getRelationsForConceptsAsSubject(IntList ids, int relationtype) {
    try {
      return rmiOntologyServer.getRelationsForConceptsAsSubject(ids, relationtype);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<Relation> getRelationsForConceptAsSubject(int id) {
    try {
      return rmiOntologyServer.getRelationsForConceptAsSubject(id);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void removeConcept(int id) {
  }

  public void setConcept(Concept concept) {
    conceptCache.set(concept.getID(), concept);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setRelation(Relation relation) {
  }

  
  public Set<ConceptRelation> getParentRelationsForConceptSet(IntList ids) {
    try {
      return rmiOntologyServer.getParentRelationsForConceptSet(ids);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }

  private class ConceptIterator implements Iterator<Concept> {
    private int offset = 0;
    private boolean eof = false;
    private Collection<Concept> miniCache;
    private Iterator<Concept> cacheIterator;
    private RMIOntology ontology;

    public ConceptIterator(RMIOntology rmiOntology) {
      this.ontology = rmiOntology;
      this.miniCache = new ArrayList<Concept>();
    }

    public boolean hasNext() {
      if (cacheIterator != null && cacheIterator.hasNext())
        return true;
      else {
        getNextBatch();
        return !eof;
      }
    }

    public Concept next() {
      while (cacheIterator == null || !cacheIterator.hasNext())
        getNextBatch();
      return cacheIterator.next();
    }

    public void remove() {
      System.out.println("Remove is not implemented for OntologyClient iterator!");
    }

    private void getNextBatch() {
      int batchsize = 10000;
      eof = true;
      Map<Integer, Concept> conceptMap = ontology.getConceptSubset(offset, batchsize);
      offset += batchsize;
      miniCache = conceptMap.values();
      cacheIterator = miniCache.iterator();
      eof = (offset > ontology.size());
    }
  }

  private class ConceptCache extends StoreMapCaching<Integer, Concept> {
    private RMIOntology ontology;

    public ConceptCache(RMIOntology ontology) {
      this.ontology = ontology;
    }

    protected Map<Integer, Concept> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
      Map<Integer, Concept> conceptMap = null;
      try {
        conceptMap = rmiOntologyServer.getConcepts(ids);
        for (Integer id: ids) {
          if (!conceptMap.containsKey(id) && makeEmptyConcepts) {
            Concept concept = new Concept(id);
            concept.setName(emptyConceptString);
            conceptMap.put(id, concept);
          }
        }
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      return conceptMap;
    }

    /*
    protected Map<Integer, Concept> getEntriesFromStoreWithIDs(int offset, int limit) {
      Map<Integer, Concept> conceptMap = null;
      try {
        conceptMap = rmiOntologyServer.getConcepts(offset, limit);
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      return conceptMap;
    }
*/
    protected Concept getEntryFromStoreWithID(Integer id) {
      Concept concept = null;
      try {
        concept = rmiOntologyServer.getConcept(id);
        if (concept == null && makeEmptyConcepts) {
          concept = new Concept(id);
          concept.setName(emptyConceptString);
        }
      } catch (RemoteException e) {
        e.printStackTrace();
      }
      return concept;
    }

    protected void setEntryInStore(Integer id, Concept value) {
      ontology.setConcept(value);
    }

    public int size() {
      return 0;
    }
    
  }

  public boolean isMakeEmptyConcepts() {
    return makeEmptyConcepts;
  }

  public void setMakeEmptyConcepts(boolean makeEmptyConcepts) {
    this.makeEmptyConcepts = makeEmptyConcepts;
  }

  public Iterator<Concept> iterator() {
    return getConceptIterator();
  }
}
