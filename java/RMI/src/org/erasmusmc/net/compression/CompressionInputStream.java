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

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class CompressionInputStream extends FilterInputStream
    implements CompressionConstants, Serializable
{
  private static final long serialVersionUID = -2111728416598238619L;

    /*
     * Constructor calls constructor of superclass
     */
    public CompressionInputStream(InputStream in) {
        super(in);
    }
 
    /* 
     * Buffer of unpacked 6-bit codes 
     * from last 32 bits read.
     */
    int buf[] = new int[5];
 
    /*
     * Position of next code to read in buffer (5 signifies end). 
     */ 
    int bufPos = 5;
 
    /*
     * Reads in format code and decompresses character accordingly.
     */

    public int read() throws IOException {
        try {
            int code;

            // Read in and ignore empty bytes (NOP's) as long as they
            // arrive. 
            do {
          code = readCode();
        } while (code == NOP);      
 
            if (code >= BASE) {
                // Retrieve index of character in codeTable if the
                // code is in the correct range.
                return codeTable.charAt(code - BASE);
            } else if (code == RAW) {
                // read in the lower 4 bits and the higher 4 bits,
                // and return the reconstructed character
                int high = readCode();
                int low = readCode();
                return (high << 4) | low;
            } else 
                throw new IOException("unknown compression code: " + code);
        } catch (EOFException e) {
            // Return the end of file code
            return -1;
        }
    }
 
    /* 
     * This method reads up to len bytes from the input stream. 
     * Returns if read blocks before len bytes are read.
     */ 
    public int read(byte b[], int off, int len) throws IOException {

    if (len <= 0) {
        return 0;
    }

    int c = read();
    if (c == -1) {
        return -1;
    }
    b[off] = (byte)c;

    int i = 1;
        // Try to read up to len bytes or until no
        // more bytes can be read without blocking.
        try {
        for (; (i < len) && (in.available() > 0); i++) {
        c = read();
        if (c == -1) {
            break;
        }
        if (b != null) {
            b[off + i] = (byte)c;
        }
        }
    } catch (IOException ee) {
    }
    return i;
    }

    /*
     * If there is no more data to decode left in buf, read the
     * next four bytes from the wire. Then store each group of 6
     * bits in an element of buf.  Return one element of buf.
     */
    private int readCode() throws IOException {
        // As soon as all the data in buf has been read
        // (when bufPos == 5) read in another four bytes.
        if (bufPos == 5) {
            int b1 = in.read();
            int b2 = in.read();
            int b3 = in.read();
            int b4 = in.read();

            // make sure none of the bytes signify the
            // end of the data in the stream
            if ((b1 | b2 | b3 | b4) < 0) {
                throw new EOFException();
            }
            // Assign each group of 6 bits to an element of
            // buf
            int pack = (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
            buf[0] = (pack >>> 24) & 0x3F;
            buf[1] = (pack >>> 18) & 0x3F;
            buf[2] = (pack >>> 12) & 0x3F;
            buf[3] = (pack >>>  6) & 0x3F;
            buf[4] = (pack >>>  0) & 0x3F;
            bufPos = 0;
        }
        return buf[bufPos++];
    }
}
