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

import java.util.Map;


public class ProcessedConceptVectorRecordsBuffer {
  public boolean finishedUpdating=false;
  private Map<Integer,ConceptToConceptVectorRecordIndexEntry> map;


  
  public synchronized void updateBuffer(Map<Integer,ConceptToConceptVectorRecordIndexEntry> newMap,boolean lastupdate) {
      
    if (map != null){
      try {
        wait();
      } catch (InterruptedException e) {
        System.out.println("Thread interrupted!");
      }
    }
    finishedUpdating = lastupdate;
    this.map=newMap;
//    System.out.println("Processed Records Map set!");
    notify();
   }
   
  
  public synchronized  Map<Integer,ConceptToConceptVectorRecordIndexEntry>  getProcessedRecordsBuffer() {
    
    if (map == null ){
      try {
        wait();
      } catch (InterruptedException e) {
        System.out.println("Thread interrupted!");
      }
    }
  //  System.out.println("Processed Records Map given!");
    Map<Integer,ConceptToConceptVectorRecordIndexEntry> localmap = this.map;
    this.map = null;
    notify();
    return localmap;
  }
  
  
}
