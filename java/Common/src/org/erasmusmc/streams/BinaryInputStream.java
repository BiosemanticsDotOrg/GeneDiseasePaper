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

package org.erasmusmc.streams;

import java.io.*;


/**
 * <p>Title: ACS Viewer</p>
 * <p>Description: A viewer to visualize Associative Concept Spaces</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC, Medical Informatics</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class BinaryInputStream extends InputStream {
  private InputStream stream;

  public BinaryInputStream(InputStream stream) {
    this.stream = stream;
  }

  public int read() throws java.io.IOException {
    return stream.read();
  }

  public boolean markSupported() {
    return stream.markSupported();
  }

  public long skip(long n) throws java.io.IOException {
    return stream.skip(n);
  }

  public synchronized void mark(int readlimit) {
    stream.mark(readlimit);
  }

  public int available() throws java.io.IOException {
    return stream.available();
  }

  public void close() throws java.io.IOException {
    stream.close();
  }

  public synchronized void reset() throws java.io.IOException {
    stream.reset();
  }

  public int readInteger() throws java.io.IOException {
    int a = read(), b = read(), c = read(), d = read();

    return ((d & 0xff) << 24) | ((c & 0xff) << 16) | ((b & 0xff) << 8) | (a & 0xff);
  }

  public long readLong() throws java.io.IOException {
    long a = read(), b = read(), c = read(), d = read(),
         e = read(), f = read(), g = read(), h = read();

    return ((h & 0xff) << 56) | ((g & 0xff) << 48) |
           ((f & 0xff) << 40) | ((e & 0xff) << 32) |
           ((d & 0xff) << 24) | ((c & 0xff) << 16) |
           ((b & 0xff) <<  8) | ((a & 0xff));
  }

  public double readDouble() throws java.io.IOException {
    return Double.longBitsToDouble(readLong());
  }

  public String readString() throws java.io.IOException {
    StringBuffer result = new StringBuffer();
    byte[] byteString = new byte[readInteger()];
    stream.read(byteString);

    for (int i = 0; i < byteString.length; i++)
      result.append((char) byteString[i]);

    return result.toString();
  }

  public String readEncodedString() throws java.io.IOException {
    StringBuffer result = new StringBuffer();
    byte[] byteString = new byte[readInteger()];
    stream.read(byteString);

    for (int i = 0; i < byteString.length; i++)
      result.append((char) (byteString[i] ^ (byte) 0xFF));

    return result.toString();
  }

  public String[] readEncodedStringArray() throws java.io.IOException {
    String[] result = new String[readInteger()];

    for (int i = 0; i < result.length; i++)
      result[i] = readEncodedString();

    return result;
  }

  public byte readByte() throws java.io.IOException {
    return (byte) read();
  }
}