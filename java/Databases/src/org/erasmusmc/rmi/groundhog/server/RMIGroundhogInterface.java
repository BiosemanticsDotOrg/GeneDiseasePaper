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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.ontology.ConceptVectorRecord;

public interface RMIGroundhogInterface extends Remote {
    public ConceptVectorRecord getEntryFromStoreWithID(Integer id) throws RemoteException;
    public Map<Integer, ConceptVectorRecord> getEntriesFromStoreWithIDs(Collection<Integer> ids) throws RemoteException;
    public Set<Integer> getRecordIDsForConcept(Integer conceptID) throws RemoteException;
    public boolean hasEntry(Integer conceptID) throws RemoteException;
    public Set<Integer> checkForEntries(Collection<Integer> ids) throws RemoteException;
    public void setConceptVectorRecord(ConceptVectorRecord conceptVectorRecord) throws RemoteException;
    public void toggleBulkImportMode(boolean toggle) throws RemoteException;
    public int size() throws RemoteException;
    public Set<Integer> getEntryIDs() throws RemoteException;
    public void setReindexBatchSize(int size) throws RemoteException;
}

