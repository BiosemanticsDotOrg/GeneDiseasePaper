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

package org.erasmusmc.rmi.conceptmapper.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.erasmusmc.rmi.conceptmapper.server.ConceptMapping;
import org.erasmusmc.rmi.conceptmapper.server.RMIConceptMapperInterface;

public class RMIConceptMapper {
  private RMIConceptMapperInterface proxy;
  
  /**
   * Create a connection to the given server.
   * @param server  The name or IP address of the server running the concept mapper service
   */
  public RMIConceptMapper(String server, int port, String serviceName) throws Exception {
    proxy = (RMIConceptMapperInterface) Naming.lookup("rmi://" + server + ":"+port+"/"+serviceName);
  }
  
  public List<ConceptMapping> getMapping(int conceptID){
    try {
      return proxy.getMapping(conceptID);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;   
  }
  
  public Map<Integer, List<ConceptMapping>> getMappings(Collection<Integer> conceptIDs){
    try {
      return proxy.getMappings(conceptIDs);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    return null;
  }
  
}
