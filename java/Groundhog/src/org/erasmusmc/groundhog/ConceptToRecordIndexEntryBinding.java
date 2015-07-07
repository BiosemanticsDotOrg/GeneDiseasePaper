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

import org.erasmusmc.collections.IntList;
import org.erasmusmc.collections.SortedIntListSet;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class ConceptToRecordIndexEntryBinding extends TupleBinding {
  public Comparator<Integer> comparator;
  public ConceptToRecordIndexEntryBinding(Comparator<Integer> comparator){
    this.comparator=comparator;
  }
  @Override
  public Object entryToObject(TupleInput ti) {
    ConceptToConceptVectorRecordIndexEntry conceptToRecordIndexEntry = new ConceptToConceptVectorRecordIndexEntry();
    conceptToRecordIndexEntry.sumOfValuesInRecords = ti.readFloat();
    int numberofentries = ti.readInt();
    IntList list = new IntList(numberofentries);
    
    for (int i=0; i<numberofentries;i++){
      list.add(ti.readInt());
    }
    conceptToRecordIndexEntry.conceptVectorRecordIDs = new SortedIntListSet(list);
    
    return conceptToRecordIndexEntry;
  }

  @Override
  public void objectToEntry(Object object, TupleOutput to) {
    ConceptToConceptVectorRecordIndexEntry conceptToRecordIndexEntry = (ConceptToConceptVectorRecordIndexEntry) object;
    to.writeFloat(conceptToRecordIndexEntry.sumOfValuesInRecords);
    int numberofentries = conceptToRecordIndexEntry.conceptVectorRecordIDs.size();
    to.writeInt(numberofentries);
    Iterator<Integer> iterator = conceptToRecordIndexEntry.conceptVectorRecordIDs.iterator();
    while (iterator.hasNext()){
      to.writeInt(iterator.next());
    }

  }

}
