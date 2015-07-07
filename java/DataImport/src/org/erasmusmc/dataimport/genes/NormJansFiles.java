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

package org.erasmusmc.dataimport.genes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.erasmusmc.textMining.LVG.LVGNormaliser;

public class NormJansFiles {
  public static void main(String[] args){
    String filename = "/home/schuemie/GeneList/geneprotein_OM.txt";
    String newfilename = "/home/schuemie/GeneList/geneprotein_OM_norm.txt";
    
    LVGNormaliser normaliser = new LVGNormaliser();
    normaliser.loadCacheBinary("/home/public/Peregrine/standardNormCache2006.bin");
    try {
      FileInputStream PSFFile = new FileInputStream(filename);
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(PSFFile),1000000);
      
      FileOutputStream OutFile = new FileOutputStream(newfilename);
      BufferedWriter bufferedWrite = new BufferedWriter( new OutputStreamWriter(OutFile),1000000);      
      try {
        while (bufferedReader.ready()){
          bufferedWrite.write(processLine(bufferedReader.readLine(), normaliser));
          bufferedWrite.newLine();
        }
        bufferedReader.close();
        bufferedWrite.close();
      } catch (IOException e) {
        e.printStackTrace();
      }      
    } catch (FileNotFoundException e){
      e.printStackTrace();
    }    
  }

  private static String processLine(String string, LVGNormaliser normaliser) {
    String[] cols = string.split("[|]");
    if (cols.length < 10) return string;
    String[] lfs = cols[9].split("\t");
    StringBuffer newLine = new StringBuffer();
    for (int i = 0; i< cols.length; i++){
      if (i != 0) newLine.append("|");
      if (i == 9){
        for (int j = 0; j < lfs.length; j++){
          if (j != 0) newLine.append("\t");
          newLine.append(normaliser.normalise(lfs[j]));
        }
        newLine.append("|");
      }
      newLine.append(cols[i]);
    }
    return newLine.toString();
  }

}
