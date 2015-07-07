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

package org.erasmusmc.databases.integersetstore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.collections.SortedListSet;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class IntegerToSetOfIntegersBinding extends TupleBinding {

  public IntegerToSetOfIntegersBinding() {

  }

  @Override
  public Object entryToObject(TupleInput ti) {

    int numberofentries = ti.readInt();
    IntList list = new IntList(numberofentries);

    for (int i = 0; i < numberofentries; i++) {

      list.add(ti.readInt());
    }
    SortedIntListSet set = new SortedIntListSet(list);

    return set;
  }

  @Override
  public void objectToEntry(Object arg0, TupleOutput to) {
    SortedIntListSet set = (SortedIntListSet) arg0;
    int numberofentries = set.size();
    to.writeInt(numberofentries);
    Iterator<Integer> iterator = set.iterator();
    while (iterator.hasNext()) {
      to.writeInt(iterator.next());
    }
  }

}
