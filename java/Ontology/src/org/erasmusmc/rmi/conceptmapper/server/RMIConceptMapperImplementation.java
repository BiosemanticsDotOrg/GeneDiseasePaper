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

package org.erasmusmc.rmi.conceptmapper.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.utilities.ReadTextFile;

public class RMIConceptMapperImplementation extends UnicastRemoteObject implements RMIConceptMapperInterface {
  private static final long serialVersionUID = -6638799498443801109L;
  private Map<Integer, List<ConceptMapping>> cid2mappings = new HashMap<Integer, List<ConceptMapping>>();
  
  protected RMIConceptMapperImplementation(String filename) throws RemoteException {
    super();
    ReadTextFile in = new ReadTextFile(filename);
    for (String line : in){
      String[] cols = line.split("\t");
      Integer cid = Integer.parseInt(cols[0]);
      ConceptMapping mapping = new ConceptMapping();
      mapping.identifier = cols[1];
      mapping.name = cols[2];
      addToMapping(cid, mapping);
    }
  }

  private void addToMapping(Integer cid, ConceptMapping mapping) {
    List<ConceptMapping> mappings = cid2mappings.get(cid);
    if (mappings == null){
      mappings = new ArrayList<ConceptMapping>(1);
      cid2mappings.put(cid, mappings);
    }
    mappings.add(mapping);
  }

  public List<ConceptMapping> getMapping(int conceptID) {
    return cid2mappings.get(conceptID);
  }
  
  public Map<Integer, List<ConceptMapping>> getMappings(Collection<Integer> conceptIDs){
    Map<Integer, List<ConceptMapping>> result = new HashMap<Integer, List<ConceptMapping>>(conceptIDs.size());
    for (Integer conceptID : conceptIDs) {
      List<ConceptMapping> mappings = cid2mappings.get(conceptID);
      if (mappings != null)
        result.put(conceptID, mappings);
    }
    return result;
  }

}
