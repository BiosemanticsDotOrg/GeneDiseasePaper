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
import java.util.List;
import java.util.Map;

public class ListSpace<D> extends DefaultSpace<D> {
  
  public List<D> list;  
  protected Map<D, Integer> index = new HashMap<D, Integer>();

  public ListSpace(List<D> list) {
    this.list = new ArrayList<D>(list);
    int i = 0;
    
    for (D object: list)
      index.put(object, i++);
  }
  public ListSpace(Collection<D> collection){
    this(new ArrayList<D>(collection));
  }
  
  public int getDimensions() {
    return list.size();
  }

  public int indexOfObject(D object) {
    Integer result = index.get(object);
    if (result == null)
      return -1;
    else
      return result;
  }

  public D objectForIndex(int index) {
    return list.get(index);
  }

  public Iterator<D> iterator() {
    return list.iterator();
  }
}
