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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.rmi.ontology.conceptrelations.ConceptRelation;

public interface RMIOntologyInterface extends Remote {
  public Concept getConcept(int id) throws RemoteException;
  public Map<Integer, Concept> getConcepts(Collection<Integer> ids) throws RemoteException;
  public Map<Integer, Concept> getConcepts(int offset, int limit) throws RemoteException;
  public String getName() throws RemoteException;
  public List<Relation> getRelationsForConceptAsObject(int id) throws RemoteException;
  public List<Relation> getRelationsForConceptAsObject(int id, int relationtype) throws RemoteException;
  public List<Relation> getRelationsForConceptsAsObject(IntList ids, int relationtype) throws RemoteException;
  public List<Relation> getRelationsForConceptAsSubject(int id) throws RemoteException;
  public List<Relation> getRelationsForConceptAsSubject(int id, int relationtype) throws RemoteException;
  public List<Relation> getRelationsForConceptsAsSubject(IntList ids, int relationtype) throws RemoteException;
  public List<Relation> getRelations() throws RemoteException;
  public int size () throws RemoteException;
  public List<DatabaseID> getDBLinksForConcept(int id) throws RemoteException;
  public void setDBLinkForConcept(int id, DatabaseID dblink) throws RemoteException;
  public Set<Integer> getConceptIDs(DatabaseID databaseID) throws RemoteException;
  
  public Set<ConceptRelation> getParentRelationsForConceptSet(IntList ids) throws RemoteException;
}
