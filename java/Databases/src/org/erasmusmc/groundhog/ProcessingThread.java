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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.math.vector.VectorCursor;
import org.erasmusmc.ontology.ConceptVectorRecord;


public class ProcessingThread implements Runnable {
  public ConceptVectorRecordBuffer recordBuffer;
  
  public ProcessedConceptVectorRecordsBuffer processedRecordsBuffer;
  public boolean terminated = false;
  public Thread thread;
  
  public ProcessingThread(ConceptVectorRecordBuffer recordbuffer,ProcessedConceptVectorRecordsBuffer processedRecordsBuffer){
     thread = new Thread(this, "processingThread");
     this.recordBuffer = recordbuffer;   
     this.processedRecordsBuffer = processedRecordsBuffer;
     thread.start();
  }
  public void run() {
    while (!terminated){
      List<ConceptVectorRecord> records = recordBuffer.getRecords();
  //    System.out.println("Got Records!");
      if (records.size()<recordBuffer.batchSize){
        terminated = true;
        System.out.println("processingThread Terminates!");
      }
      processedRecordsBuffer.updateBuffer( process(records),terminated);
      
    }

  }
  
  protected Map<Integer,ConceptToConceptVectorRecordIndexEntry> process(List<ConceptVectorRecord> records){
    Map<Integer,ConceptToConceptVectorRecordIndexEntry> index = new HashMap<Integer, ConceptToConceptVectorRecordIndexEntry>();
    for(ConceptVectorRecord record: records){
      VectorCursor<Integer> cursor = record.getConceptVector().getNonzeroCursor();
      while (cursor.isValid()) {
        ConceptToConceptVectorRecordIndexEntry entry = index.get(cursor.dimension());
        if (entry == null) {
          entry = new ConceptToConceptVectorRecordIndexEntry(record.getID(),cursor.get());
        }
        else {
            entry.addRecordData(record.getID(),cursor.get());
        }
        index.put(cursor.dimension(),entry);
        cursor.next();
      }
    }
    return index;
    
  }

}
