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

package org.erasmusmc.rmi.registry.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipSocket extends Socket implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 4239927856884979122L;
  private InputStream in;
  private OutputStream out;
  
  public ZipSocket() { super(); }
  public ZipSocket(String host, int port) 
      throws IOException {
          super(host, port);
  }
  
  public InputStream getInputStream() 
      throws IOException {
          if (in == null) {
              in = new ZipInputStream(super.getInputStream());
          }
return in;
  }

  public OutputStream getOutputStream() 
      throws IOException {
          if (out == null) {
              out = new ZipOutputStream(super.getOutputStream());
          }
          return out;
  }
  
  public synchronized void close() throws IOException {
      OutputStream o = getOutputStream();
      o.flush();
      super.close();
  }
}
