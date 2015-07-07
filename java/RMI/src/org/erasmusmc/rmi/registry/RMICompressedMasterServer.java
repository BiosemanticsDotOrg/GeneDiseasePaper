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

package org.erasmusmc.rmi.registry;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.erasmusmc.net.CompressedNamedSocketFactory;

public class RMICompressedMasterServer implements Serializable {
    private static final long serialVersionUID = -2010500347433682781L;

    public static Registry getRegistry(int serverport, int objectport, String servername) throws RemoteException {
      try {
          CompressedNamedSocketFactory.install(objectport);
      } catch (IOException e) {
          e.printStackTrace();
      }
      try {
          System.setProperty("java.rmi.server.hostname", servername);
          return LocateRegistry.createRegistry(serverport);
      } catch (RemoteException exists) {
          System.setProperty("java.rmi.server.hostname", servername);
          return LocateRegistry.getRegistry(servername, serverport);
      }
  }

}
