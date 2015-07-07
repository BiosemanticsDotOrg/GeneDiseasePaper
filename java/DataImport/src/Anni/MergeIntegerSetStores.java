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

package Anni;

import java.io.File;
import java.util.Iterator;

import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.databases.integersetstore.Integer2IntegerSet;
import org.erasmusmc.databases.integersetstore.IntegerSetStore;

public class MergeIntegerSetStores {
  private IntegerSetStore targetStore;
  public int sizeUpperCutoff = 10000;
  public int sizeLowerCutoff = 5;
  
  public MergeIntegerSetStores(String filename){
    targetStore = new IntegerSetStore(filename);
  }
  
  public void addStore(String filename){
    IntegerSetStore sourceStore = new IntegerSetStore(new File(filename));
    Iterator<Integer2IntegerSet> iterator = sourceStore.iterator();
    while (iterator.hasNext()) {
      Integer2IntegerSet entry = iterator.next();
      SortedIntListSet entrySet = entry.setofIntegers;
      SortedIntListSet exists = targetStore.get(entry.id);
      if (exists != null) {
        exists.addAll(entrySet);
        entrySet = exists;
      }

      if (entrySet.size() >= sizeLowerCutoff) {
        if (entrySet.size() > sizeUpperCutoff)
          truncate(entrySet);
        targetStore.set(entry.id, entrySet);
      }
    }
  }
  private void truncate(SortedIntListSet set) {
    set.setSortedList(set.getSortedList().subList(set.size() - sizeUpperCutoff, set.size()));
  }
}
