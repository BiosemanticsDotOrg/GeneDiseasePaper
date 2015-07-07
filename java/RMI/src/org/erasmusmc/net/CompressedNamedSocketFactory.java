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

package org.erasmusmc.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import org.erasmusmc.net.compression.CompressionServerSocket;
import org.erasmusmc.net.compression.CompressionSocket;

public class CompressedNamedSocketFactory extends java.rmi.server.RMISocketFactory implements Serializable {
  private static final long serialVersionUID = 1889388927914512176L;

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
                new CompressedNamedSocketFactory(port));
    }

    protected CompressedNamedSocketFactory(int defaultPort)
    {
        this.defaultPort = defaultPort;
        nextFactory = java.rmi.server.RMISocketFactory.getSocketFactory();
        if (nextFactory == null)
            nextFactory = new CompressedVanillaSocketFactory();
    }

    // override RMISocketFactory.createServerSocket
    public java.net.ServerSocket createServerSocket(int port)
    throws java.io.IOException
    {
        // if asked for the "default" port, then port==0, but select it
        // ourselves instead of letting the kernel decide
        return nextFactory.createServerSocket(port == 0 ? defaultPort : port);
    }

    // override RMISocketFactory.createSocket
    public java.net.Socket createSocket(String host, int port)
    throws java.io.IOException
    {
        // same as before
        return nextFactory.createSocket(host, port);
    }

    private class CompressedVanillaSocketFactory
    extends java.rmi.server.RMISocketFactory
    {
//      override RMISocketFactory.createServerSocket
        public ServerSocket createServerSocket(int port)
        throws IOException
        {
            return new CompressionServerSocket(port);
        }

//      override RMISocketFactory.createSocket
        public java.net.Socket createSocket(String host, int port)
        throws java.io.IOException
        {
            return new CompressionSocket(host, port);
        }
    }
 
  
}

