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

package org.erasmusmc.math;

/**
 * Calculates the CRC32 - 32 bit Cyclical Redundancy Check
 * 
 * When calculating the CRC32 over a number of strings or byte arrays
 * the previously calculated CRC is passed to the next call.  In this
 * way the CRC is built up over a number of items, including a mix of
 * strings and byte arrays.
 */

public class CRC32
{
  public CRC32() {
    buildCRCTable();     
  }

  public int crc32(String buffer)
  {
    return crc32(buffer.getBytes(), 0xFFFFFFFF);
  }
  
  public int crc32(byte buffer[])
  {
    return crc32(buffer, 0xFFFFFFFF);
  }
  
  public int crc32(String buffer, int crc)
  {
    return crc32(buffer.getBytes(), crc);
  }
  
  public int crc32(byte buffer[], int crc)
  {
    return crc32(buffer, 0, buffer.length, crc);
  }
  
  public int crc32(byte buffer[], int start, int count, int crc)
  {
    int temp1, temp2;
    int i = start;
    
    while (count-- != 0)
    {
      temp1 = crc >>> 8;
      temp2 = CRCTable[(crc ^ buffer[i++]) & 0xFF];
      crc = temp1 ^ temp2;
    }
    
    return crc;
  }
  
  private void buildCRCTable()
  {
    final int CRC32_POLYNOMIAL = 0xEDB88320;
    
    int i, j;
    int crc;
    
    CRCTable = new int[256];
    
    for (i = 0; i <= 255; i++)
    {
      crc = i;
      for (j = 8; j > 0; j--)
        if ((crc & 1) == 1)
          crc = (crc >>> 1) ^ CRC32_POLYNOMIAL;
        else
          crc >>>= 1;
          CRCTable[i] = crc;
    }
  }
  
  private int CRCTable[];   // CRC Lookup table
}

