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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OneToManySet<K,V> {
  private Map<K,Set<V>> map = new HashMap<K, Set<V>>();
  
  public boolean put(K key, V value){
    Set<V> set = map.get(key);
    if (set == null){
      set = new HashSet<V>();
      map.put(key, set);
    }
    return set.add(value);
  }
  
  public void set(K key, Set<V> set){
  	map.put(key, set);
  }
  
  public Set<V> get(K key){
    Set<V> set = map.get(key);
    if (set == null)
      return Collections.emptySet();
    else
      return set;   
  }
  
  public Set<K> keySet(){
    return map.keySet();
  }
  
  public Collection<Set<V>> values(){
    return map.values();
  }
  
  public Set<Map.Entry<K, Set<V>>> entrySet(){
  	return map.entrySet();
  }
  
  public int size(){
  	return map.size();
  }
  

}

