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

package org.erasmusmc.acs;

import java.util.Collection;
import java.util.Iterator;

import org.erasmusmc.webservice.biosemantics.ACSNetworkData;

/**
 * <p>Title: ACS Viewer</p>
 * <p>Description: A viewer to visualize Associative Concept Spaces</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC, Medical Informatics</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class RemoteACSNetwork {
  public String ID;
  public String name;
  public int length;
  public long lastModified;

  public void setData(ACSNetworkData acsNetworkData) {
    ID = acsNetworkData.getID();
    name = acsNetworkData.getName();
    length = acsNetworkData.getLength();
    lastModified = acsNetworkData.getLastModified();
  }

  @Override
public String toString() {
    return name;
  }
  
  public static RemoteACSNetwork findWithName(Collection<? extends RemoteACSNetwork> remoteACSNetworks, String name) {
    RemoteACSNetwork result = null;
    Iterator<? extends RemoteACSNetwork> iterator = remoteACSNetworks.iterator();
    
    while (result == null && iterator.hasNext()) {
      RemoteACSNetwork remoteACSNetwork = iterator.next();
      
      if (remoteACSNetwork.name.equalsIgnoreCase(name))
        result = remoteACSNetwork;
    }
    
    return result;
  }
}