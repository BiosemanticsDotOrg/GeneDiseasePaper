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

package org.erasmusmc.rmi.rmitest.client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.erasmusmc.rmi.rmitest.server.RMITestInterface;

public class RMITestAppCGI {
    public static void main(String[] args) {
      System.setProperty("useProxy", "true");
      System.setProperty("http.proxyHost", "blwsdfglasfgtasf");
      System.setProperty("http.proxyPort", "10235");
        //Properties defaultProps = RMIProperties.getProperties();
        try {
            //RMITestInterface server = (RMITestInterface) RMIMasterServer.getRegistry().lookup(defaultProps.getProperty("RMITestServer"));
            RMITestInterface server = (RMITestInterface) Naming.lookup("rmi://mi-bios4.erasmusmc.nl:4567/RMITestServerService");
            System.out.println(server.testConnection());
            ArrayList<String> list = server.getList();
            for(String string: list) {
                System.out.println(string);
            }           
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
    }
}