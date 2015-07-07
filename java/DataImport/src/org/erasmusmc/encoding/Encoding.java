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

/*
 * Created on Dec 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.erasmusmc.encoding;

/**
 * @author pjroes
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Encoding {
  public final static String base64Encode(String str) {
    byte[] Base64EncMap =

  {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
   'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
   'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
   'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
   '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    byte data[] = str.getBytes();
        
    int sidx, didx;
    byte dest[] = new byte[((data.length+2)/3)*4];
    
    // 3-byte to 4-byte conversion + 0-63 to ascii printable conversion
    for (sidx=0, didx=0; sidx < data.length-2; sidx += 3) {
      dest[didx++] = Base64EncMap[(data[sidx] >>> 2) & 077];
      dest[didx++] = Base64EncMap[(data[sidx+1] >>> 4) & 017 | (data[sidx] << 4) & 077];
      dest[didx++] = Base64EncMap[(data[sidx+2] >>> 6) & 003 | (data[sidx+1] << 2) & 077];
      dest[didx++] = Base64EncMap[data[sidx+2] & 077];
    }
    
    if (sidx < data.length) {
      dest[didx++] = Base64EncMap[(data[sidx] >>> 2) & 077];
      
      if (sidx < data.length-1) {
        dest[didx++] = Base64EncMap[(data[sidx+1] >>> 4) & 017 | (data[sidx] << 4) & 077];
        dest[didx++] = Base64EncMap[(data[sidx+1] << 2) & 077];
      }
      else
        dest[didx++] = Base64EncMap[(data[sidx] << 4) & 077];
    }
    
    // add padding
    for ( ; didx < dest.length; didx++)
      dest[didx] = '=';
    
    return new String(dest);
  }
}
