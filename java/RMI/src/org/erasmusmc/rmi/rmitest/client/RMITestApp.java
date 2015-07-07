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

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.util.ArrayList;
import java.util.Properties;
import org.erasmusmc.rmi.RMIProperties;
import org.erasmusmc.rmi.rmitest.server.RMITestInterface;
import sun.rmi.transport.proxy.RMIHttpToCGISocketFactory;

public class RMITestApp {
    public static void main(String[] args) {
      RMITestApp app = new RMITestApp();
      RMITestInterface server = app.connect();
      try {
        app.go(server);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    private RMITestInterface connect() {
      RMITestInterface server = null;
      try {
        server = (RMITestInterface) Naming.lookup("rmi://mojojojo.biosemantics.org:1111/RMITestServerService");
      }
      catch (Exception e) {
       try {
          System.out.println("Direct conncetion failed!, falling back to HTTPtoCGISockets, can lead to performance Issues");
          RMISocketFactory.setSocketFactory(new RMIHttpToCGISocketFactory());
          server = (RMITestInterface) Naming.lookup("rmi://mojojojo.biosemantics.org:1111/RMITestServerService");
        } catch (MalformedURLException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (RemoteException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (NotBoundException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        } catch (IOException ee) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
      return server;
    }
    
    private void go(RMITestInterface server) throws Exception {
      System.out.println(server.testConnection());
      ArrayList<String> list = new ArrayList<String>(server.getList());
      for(String string: list) {
        System.out.println(string);
      }           

    }
}
