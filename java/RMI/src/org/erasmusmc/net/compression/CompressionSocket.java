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

package org.erasmusmc.net.compression;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class CompressionSocket extends Socket implements Serializable {
  private static final long serialVersionUID = -1953028387522285599L;
  /* InputStream used by socket */
  private InputStream in;
  /* OutputStream used by socket */
  private OutputStream out;

  /* 
   * No-arg constructor for class CompressionSocket  
   */
  public CompressionSocket() { super(); }

  /* 
   * Constructor for class CompressionSocket 
   */
  public CompressionSocket(String host, int port) throws IOException {
      super(host, port);
  }

  /* 
   * Returns a stream of type CompressionInputStream 
   */
  public InputStream getInputStream() throws IOException {
      if (in == null) {
          in = new CompressionInputStream(super.getInputStream());
      }
      return in;
  }

  /* 
   * Returns a stream of type CompressionOutputStream 
   */
  public OutputStream getOutputStream() throws IOException {
      if (out == null) {
          out = new CompressionOutputStream(super.getOutputStream());
      }
      return out;
  }

  /*
   * Flush the CompressionOutputStream before 
   * closing the socket.
   */
  public synchronized void close() throws IOException {
      OutputStream o = getOutputStream();
      o.flush();
  super.close();
  }
}
