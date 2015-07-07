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

import java.util.Comparator;
import java.util.Iterator;

import org.erasmusmc.collections.SortedIntListSet;

public class ConceptToConceptVectorRecordIndexEntry {
  public Integer key;
  public SortedIntListSet conceptVectorRecordIDs;
  public float sumOfValuesInRecords=0f;

  public ConceptToConceptVectorRecordIndexEntry() {
    conceptVectorRecordIDs = new SortedIntListSet(); 
  }
  public SortedIntListSet getConceptVectorRecordIDs() {
    //return new HashSet<Integer>(conceptVectorRecordIDs.getSortedList());
    return conceptVectorRecordIDs;
  }
  public ConceptToConceptVectorRecordIndexEntry(Integer recordID,float value ){
    conceptVectorRecordIDs = new SortedIntListSet();       
    conceptVectorRecordIDs.add(recordID);
    sumOfValuesInRecords += value;
  }
  public ConceptToConceptVectorRecordIndexEntry(Integer recordID,float value,Comparator<Integer> comparator ){
    conceptVectorRecordIDs = new SortedIntListSet();
    conceptVectorRecordIDs.add(recordID);
    sumOfValuesInRecords += value;
  }
  public ConceptToConceptVectorRecordIndexEntry(SortedIntListSet conceptVectorIDs, float sumofValuesInRecords) {

    this.conceptVectorRecordIDs = conceptVectorIDs;
    this.sumOfValuesInRecords = sumofValuesInRecords;

  }
  public void addRecordData(Integer recordID,float value){
    conceptVectorRecordIDs.add(recordID);
    sumOfValuesInRecords += value;
  }
  public void appendConsecutiveRecordToList(Integer recordID,float value){
    conceptVectorRecordIDs.add(recordID);
    sumOfValuesInRecords+=value;
  }
  protected void removeRecordData(Integer recordID,float value){
    conceptVectorRecordIDs.remove(recordID);
    sumOfValuesInRecords -= value; 
  }
  public String toPrint(){
    StringBuffer result = new StringBuffer();
    result.append("sumOfValuesInRecords: " + sumOfValuesInRecords + "\n");
    Iterator<Integer> iterator = conceptVectorRecordIDs.iterator();
    while (iterator.hasNext()){
      result.append(iterator.next() + "\t");
    }
    return result.toString();
  }
}
