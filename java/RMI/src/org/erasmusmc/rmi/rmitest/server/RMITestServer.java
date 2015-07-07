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

package org.erasmusmc.rmi.rmitest.server;

import java.rmi.registry.Registry;
import java.util.Properties;

import org.erasmusmc.rmi.RMIProperties;
import org.erasmusmc.rmi.registry.NamedSocketFactory;
import org.erasmusmc.rmi.registry.RMIMasterServer;

public class RMITestServer {
    public RMITestServer() {
        try {
          RMITestInterface rmiTest = new RMITestImplementation();
          Properties defaultProps = RMIProperties.getProperties();
          String servername = defaultProps.getProperty("org.erasmusmc.rmi.rmiproperties.rmitest.servername");
          String servicename = defaultProps.getProperty("org.erasmusmc.rmi.rmiproperties.rmitest.servicename");
          int serverport = Integer.parseInt(defaultProps.getProperty("org.erasmusmc.rmi.rmiproperties.rmitest.serverport"));
          int objectport = Integer.parseInt(defaultProps.getProperty("org.erasmusmc.rmi.rmiproperties.rmitest.objectport"));
          Registry registry = RMIMasterServer.getRegistry(serverport, objectport, servername);
          registry.rebind(servicename, rmiTest);
        } catch (Exception e) {
            System.out.println("Trouble: " + e);
        }
    }

    public static void main(String args[]) {
        new RMITestServer();
    }
}
