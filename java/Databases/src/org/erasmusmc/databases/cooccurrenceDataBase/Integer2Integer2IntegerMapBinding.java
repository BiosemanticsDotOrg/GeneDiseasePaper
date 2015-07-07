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

package org.erasmusmc.databases.cooccurrenceDataBase;

import java.util.Iterator;

import org.erasmusmc.collections.SortedIntList2IntMap;
import org.erasmusmc.collections.SortedIntList2IntMap.MapEntry;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.DatabaseEntry;

public class Integer2Integer2IntegerMapBinding extends TupleBinding {

  @Override
  public Object entryToObject(TupleInput ti) {
    int size = ti.readInt();
    SortedIntList2IntMap result  = new SortedIntList2IntMap(size);
    for (int i=0;i<size;i++){
      result.addEntry(ti.readInt(), ti.readInt());
    }
    return result;
  }

  @Override
  public void objectToEntry(Object obj, TupleOutput to) {
    SortedIntList2IntMap map = (SortedIntList2IntMap)obj;
    to.writeInt(map.size());
    Iterator<MapEntry> it = map.entryIterator();
    while(it.hasNext()){
      MapEntry me = it.next();
      to.writeInt(me.getKey());
      to.writeInt(me.getValue());
    }
  }

}
