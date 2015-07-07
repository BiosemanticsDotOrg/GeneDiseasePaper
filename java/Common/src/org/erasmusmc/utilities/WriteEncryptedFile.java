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

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class WriteEncryptedFile {

  public static KeyPair generateKeyPair(){
    KeyPair result = null;
    KeyPairGenerator keygen;
    try {
      keygen = KeyPairGenerator.getInstance("RSA");
      keygen.initialize(512);
      result = keygen.generateKeyPair();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return result;
  }
  
  public static void saveKey(Key key, String filename){
    saveRawKey(filename, key);
  }  
  
  public static Key loadKey(String filename){
    return loadRawKey(filename);
  }
   
  private static Key loadRawKey(String filename) {
    Key result = null;
    try {
      FileInputStream binFile = new FileInputStream(filename);
      try {
        ObjectInputStream inp = new ObjectInputStream(binFile);
        try {
          result = (Key)inp.readObject();   
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return result;
  }
  
  private static void saveRawKey(String filename, Key key) {
    try {
      FileOutputStream binFile = new FileOutputStream(filename);
      try {
        ObjectOutputStream out = new ObjectOutputStream(binFile);
        out.writeObject(key);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
   public WriteEncryptedFile(String filename, String keyFilename){
    this(filename, loadKey(keyFilename));
  }
  
  public WriteEncryptedFile(String filename, Key publicKey){
    try{
      //Step 1: generate random symmetric key (AES algorithm):
      KeyGenerator kgen = KeyGenerator.getInstance("AES");
      kgen.init(128); 
      SecretKey aesKey = kgen.generateKey();

      //Step 2: Create encoding cipher using public key (RSA algorithm):
      Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
      rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

      //Step 3: Open file stream: 
      FileOutputStream file = new FileOutputStream(filename);

      //Step 4: Encode symmetric key using encoding cipher, and write to file:
      file.write(rsaCipher.doFinal(aesKey.getEncoded()));

      //Step 5: Open encrypted stream using symmetric key (AES algorithm):
      Cipher cipher = Cipher.getInstance("AES");
      cipher.init(Cipher.ENCRYPT_MODE, aesKey);
      CipherOutputStream out = new CipherOutputStream(file, cipher);
      bufferedWrite = new BufferedWriter(new OutputStreamWriter(out));
    } catch (Exception e){
      e.printStackTrace();
    }
  }
  
  public void writeln(String string){
    try {
      bufferedWrite.write(string);
      bufferedWrite.newLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void flush(){
    try {
      bufferedWrite.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    try {
      bufferedWrite.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private BufferedWriter bufferedWrite;
}