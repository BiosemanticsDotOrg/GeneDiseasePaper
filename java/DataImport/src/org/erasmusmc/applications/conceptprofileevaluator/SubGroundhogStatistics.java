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

package org.erasmusmc.applications.conceptprofileevaluator;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.erasmusmc.groundhog.ConceptStatistic;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.math.vector.VectorCursor;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.ConceptVectorRecord;


public class SubGroundhogStatistics {
  public Map<Integer, ConceptStatistic> conceptStatistics;
  public Collection<Integer> documentIDs;
  public int allConceptOccurrences;

  public SubGroundhogStatistics(Groundhog sourceGroundhog, Collection<Integer> docIDs) {
    conceptStatistics = new HashMap<Integer, ConceptStatistic>();
    documentIDs = docIDs;
    extractStatisticsFromSubCollexion(sourceGroundhog);
  }
  
  public SubGroundhogStatistics(){}
  
  public void saveToFile(String filename){
    try {
      FileOutputStream file = new FileOutputStream(filename);
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(file);
      objectOutputStream.writeObject(conceptStatistics);
      objectOutputStream.writeObject(documentIDs);
      objectOutputStream.writeInt(allConceptOccurrences);
      objectOutputStream.flush();      
      file.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public static SubGroundhogStatistics loadFromFile(String filename){
    SubGroundhogStatistics result = new SubGroundhogStatistics();
    try {
      FileInputStream file = new FileInputStream(filename);
      ObjectInputStream objectInputStream = new ObjectInputStream(file);
      result.conceptStatistics = (Map<Integer, ConceptStatistic>)objectInputStream.readObject();
      result.documentIDs = (Collection<Integer>)objectInputStream.readObject();
      result.allConceptOccurrences = objectInputStream.readInt();
      file.close();
    } catch (Exception e) {
      e.printStackTrace();
    }    
    return result;
  }
  
  public SubGroundhogStatistics(Collection<ConceptVector> vectors){
    int size = vectors.size();
    if (size*50>10000){
      size = 10000;
    }
    else 
       size = size*50;
    conceptStatistics = new HashMap<Integer, ConceptStatistic>(Math.round(3f/2f*size));
    extractStatisticsFromSet(vectors);    
  }

  private void extractStatisticsFromSet(Collection<ConceptVector> vectors) {
    for (ConceptVector vector: vectors) {
      if (vector != null) {
        Double maxvalue = vector.max();
        VectorCursor<Integer> cursor = vector.getNonzeroCursor();
        while (cursor.isValid()) {
          int conceptID = cursor.dimension();
          ConceptStatistic conceptCollexionStatistic = conceptStatistics.get(cursor.dimension());
          if (conceptCollexionStatistic == null) {
            conceptCollexionStatistic = new ConceptStatistic();
            //conceptCollexionStatistic.conceptID = (cursor.dimension());
            conceptStatistics.put(conceptID, conceptCollexionStatistic);
          }
          //conceptCollexionStatistic.summedWeight += cursor.get() / maxvalue;
          conceptCollexionStatistic.termFrequency += cursor.get();
          allConceptOccurrences += cursor.get();
          conceptCollexionStatistic.docFrequency++;
          cursor.next();
        }
      }
    }
  }

  protected void extractStatisticsFromSubCollexion(Groundhog sourceGroundhog) {
    Map<Integer, ConceptVectorRecord> records = sourceGroundhog.getSubMap(documentIDs);
    for (ConceptVectorRecord record: records.values()) {
      if (record != null) {
        VectorCursor<Integer> cursor = record.getConceptVector().getNonzeroCursor();
        while (cursor.isValid()) {
          int conceptID = cursor.dimension();
          ConceptStatistic conceptCollexionStatistic = conceptStatistics.get(conceptID);
          if (conceptCollexionStatistic == null) {
            conceptCollexionStatistic = new ConceptStatistic();
            conceptStatistics.put(conceptID, conceptCollexionStatistic);
          }
          conceptCollexionStatistic.termFrequency += cursor.get();
          allConceptOccurrences += cursor.get();
          conceptCollexionStatistic.docFrequency++;
          cursor.next();
        }
      }
    }
  }
}
