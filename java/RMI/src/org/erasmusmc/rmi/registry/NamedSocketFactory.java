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
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

import org.erasmusmc.rmi.registry.zip.ZipServerSocket;
import org.erasmusmc.rmi.registry.zip.ZipSocket;

public class NamedSocketFactory extends java.rmi.server.RMISocketFactory implements Serializable {
  private static final long serialVersionUID = -5887713375593670454L;

    /** the port number that normal objects should be exported on */
    private int defaultPort;

    /** the real socket factory that was there when we were installed */
    private java.rmi.server.RMISocketFactory nextFactory;

    /** Install a NamedSocketFactory for a particular port.
     * If installed before any remote objects are exported (and before
     * any UnicastRemoteObjects are created), then all remote objects
     * will be exported on the given port, instead of a random port.
     */
    public static void install(int port)
    throws java.io.IOException
    {
        java.rmi.server.RMISocketFactory.setSocketFactory(
                new NamedSocketFactory(port));
    }

    protected NamedSocketFactory(int defaultPort)
    {
        this.defaultPort = defaultPort;
        nextFactory = java.rmi.server.RMISocketFactory.getSocketFactory();
        if (nextFactory == null)
            nextFactory = new VanillaSocketFactory();
    }

    // override RMISocketFactory.createServerSocket
    public ServerSocket createServerSocket(int port)
    throws IOException
    {
        // if asked for the "default" port, then port==0, but select it
        // ourselves instead of letting the kernel decide
        return nextFactory.createServerSocket(port == 0 ? defaultPort : port);
    }

    // override RMISocketFactory.createSocket
    public java.net.Socket createSocket(String host, int port) throws java.io.IOException {
        // same as before
        return nextFactory.createSocket(host, port);
    }

    private class VanillaSocketFactory extends RMISocketFactory implements Serializable {
      private static final long serialVersionUID = -273952477278567712L;
      private boolean useCompression = false;
      
      public VanillaSocketFactory() {
        super();
      }
      /*
      public VanillaSocketFactory(boolean useCompression) {
        this();
        this.useCompression = useCompression;
      }
      */
        //      override RMISocketFactory.createServerSocket
        public ServerSocket createServerSocket(int port) throws IOException {
          if(useCompression) return new ZipServerSocket(port);
          return new ServerSocket(port);
        }

//      override RMISocketFactory.createSocket
        public Socket createSocket(String host, int port) throws IOException {
          if(useCompression) return new ZipSocket(host, port);
          return new Socket(host, port);
        }
    }
}

