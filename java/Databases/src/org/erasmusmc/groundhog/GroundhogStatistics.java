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

package org.erasmusmc.groundhog;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class GroundhogStatistics {
  public Map<Integer,ConceptStatistic> conceptStatistics;
  public int allConceptOccurrences;
  public int totalNumberOfDocuments;
  public GroundhogStatistics(){
    conceptStatistics=new HashMap<Integer, ConceptStatistic>();
  }
  
  public void loadGroundhogStatisticsFromFile(InputStream inputStream){
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream),1000000);
    try {
      allConceptOccurrences = Integer.parseInt(bufferedReader.readLine());
      totalNumberOfDocuments= Integer.parseInt(bufferedReader.readLine());
      while (bufferedReader.ready()){
        String[] entry = bufferedReader.readLine().split("\t");
        ConceptStatistic conceptCollexionStatistic= new ConceptStatistic();
        int cid = Integer.parseInt(entry[0]);
        conceptCollexionStatistic.termFrequency=Integer.parseInt(entry[1]);
        conceptCollexionStatistic.docFrequency =Integer.parseInt(entry[2]);
        conceptStatistics.put(cid,conceptCollexionStatistic);      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void saveGroundhogStatisticsToFile(FileOutputStream fileOutputStream){
    try {
      String firstlines= allConceptOccurrences + "\n" + totalNumberOfDocuments + "\n";
      fileOutputStream.write(firstlines.getBytes());
      for (Map.Entry<Integer, ConceptStatistic> entry : conceptStatistics.entrySet()){
        ConceptStatistic statistic = entry.getValue();
        String outputline = entry.getKey() + "\t" + statistic.termFrequency + "\t" + statistic.docFrequency + "\n";
        fileOutputStream.write(outputline.getBytes());
      }
      fileOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
 
  }
}
