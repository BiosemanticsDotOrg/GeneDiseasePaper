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

import java.util.List;

import org.erasmusmc.ontology.ConceptVectorRecord;



public class ConceptVectorRecordBuffer {
  private List<ConceptVectorRecord> records;
  public int batchSize= 1000;
  
  //private Map<Integer,ConceptToConceptVectorRecordIndexEntry> map;
  
  public ConceptVectorRecordBuffer(int batchSize){
    this.batchSize = batchSize;
  }

  
  public synchronized void updateBuffer(List<ConceptVectorRecord> newRecords) {
      
    if (records != null){
      try {
        wait();
      } catch (InterruptedException e) {
        System.out.println("Thread interrupted!");
      }
    }
  
    this.records=newRecords;
//    System.out.println("Records set!");
    notify();
   }
   
  
  public synchronized  List<ConceptVectorRecord> getRecords() {
    
    if (records == null ){
      try {
        wait();
      } catch (InterruptedException e) {
        System.out.println("Thread interrupted!");
      }
    }
 //   System.out.println("Records given!");
    List<ConceptVectorRecord> localRecords = this.records;
    this.records = null;
    notify();
    return localRecords;
  }
}
