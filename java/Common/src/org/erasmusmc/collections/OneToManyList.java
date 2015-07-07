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

package org.erasmusmc.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OneToManyList<K,V> {
  private Map<K,List<V>> map = new HashMap<K, List<V>>();
  
  public void put(K key, V value){
    List<V> list = map.get(key);
    if (list == null){
      list = new ArrayList<V>();
      map.put(key, list);
    }
    list.add(value);
  }
  
  public void putAll(OneToManyList<K,V> other){
  	for (Map.Entry<K, List<V>> entry : other.map.entrySet())
  		for (V value : entry.getValue())
  		  put(entry.getKey(), value);
  }
  
  public List<V> get(K key){
    List<V> list = map.get(key);
    if (list == null)
      return Collections.emptyList();
    else
      return list;   
  }
  
  public Set<K> keySet(){
    return map.keySet();
  }
  
  public Collection<List<V>> values(){
    return map.values();
  }
  
  public List<V> remove(K key){
  	return map.remove(key);
  }

}
