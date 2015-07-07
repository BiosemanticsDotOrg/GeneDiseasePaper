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

package org.erasmusmc.math.space;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CollectionSpace<D> extends DefaultSpace<D> {
  public Collection<D> collection;
  protected ArrayList<D> list; 
  protected Map<D, Integer> index; 
  public CollectionSpace(Collection<D> collection) {
    this.collection = collection;
    list = new ArrayList<D>(collection.size());
    index = new HashMap<D, Integer>();
    int i = 0;
    Iterator<D> iterator = collection.iterator();
    
    while (iterator.hasNext()){
      D d =iterator.next();
      
      if (!index.containsKey(d)){
        index.put(d, i++);
        list.add(d);
      }
    }
    list.trimToSize();
  }
  
  public int getDimensions() {
    
    return index.size();
  }

  public int indexOfObject(D object) {
    
    return index.get(object);
  }

  public D objectForIndex(int index) {

    return list.get(index);
  }

  public Iterator<D> iterator() {
   
    return list.iterator();
  }

}
