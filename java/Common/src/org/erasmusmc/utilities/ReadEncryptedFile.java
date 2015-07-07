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

package org.erasmusmc.utilities;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;


public class ReadEncryptedFile implements Iterable<String>{
  public String filename;
  protected BufferedReader bufferedReader;
  public boolean EOF = false;
  
  public ReadEncryptedFile(String filename, String keyFilename){
    this(filename, WriteEncryptedFile.loadKey(keyFilename));
  }

  public ReadEncryptedFile(String filename, Key key) {
    try {
      //Step 1: Generate cipher using private key (RSA algorithm):
      Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      rsaCipher.init(Cipher.DECRYPT_MODE, key);

      //Step 2: open file:
      FileInputStream textFileStream = new FileInputStream(filename);
      this.filename = filename;

      //Step 3: read encrypted symmetric key, and decrypt using private key:
      byte[] encKey = new byte[64];
      textFileStream.read(encKey);
      Key aesKey = new SecretKeySpec(rsaCipher.doFinal(encKey), "AES");

      //Step 4: create encryption stream (AES algorithm):
      Cipher aesCipher = Cipher.getInstance("AES");
      aesCipher.init(Cipher.DECRYPT_MODE, aesKey);
      CipherInputStream in = new CipherInputStream(textFileStream, aesCipher);
      bufferedReader = new BufferedReader(new InputStreamReader(in));
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  public Iterator<String> getIterator() {
    return iterator();
  }

  public List<String> loadFromFileInBatches(Integer batchsize) {
    List<String> result = new ArrayList<String>();
    if (!EOF) {
      try {
        int i = 0;
        while (!EOF && i++ < batchsize) {
          String nextLine = bufferedReader.readLine();
          if (nextLine == null)
            EOF = true;
          else
            result.add(nextLine);
        }
        if (EOF) {
          bufferedReader.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  private class TextFileIterator implements Iterator<String> {
    private String buffer;
    
    public TextFileIterator() {
      try {
        buffer = bufferedReader.readLine();
        if(buffer == null) EOF = true;
      }
      catch (IOException e) {
        e.printStackTrace();
      }
      
    }

    public boolean hasNext() {
      return !EOF;
    }

    public String next() {
      String result = buffer;
      try {
        buffer = bufferedReader.readLine();
        if(buffer == null) EOF = true;
      } catch (IOException e) {
        e.printStackTrace();
      }

      return result;
    }

    public void remove() {
      // not implemented
    }

  }

  public Iterator<String> iterator() {
    return new TextFileIterator();
  }
}