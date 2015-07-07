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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
public class Tree<K> extends Node<K> {
  private Map<K, Set<K>> parentMap = new HashMap<K, Set<K>>();
  private boolean redundancyremoved = true;

  public Tree(K root) {
    super(root);
  }
  
 
  public void addParentChildRelation(K parent, K child) {
    redundancyremoved = false;
    Node<K> parentNode = this.children.get(parent);
    Node<K> childNode = this.children.get(child);
    
    
    if(parentNode == null) 
      parentNode = this.add(parent);

    if(childNode == null) 
      childNode = this.add(child);
    
    parentNode.add(childNode);
    
  }
  
  public Node<K> getRootNode() {
    Set<K> children = new HashSet<K>();
    for (Node<K> node : this.children.values()){
      for (Node<K> child : node.children.values()){
        children.add(child.id);
      }
    }
    for (K child : children)
      this.removeChild(child);
    
    if(!redundancyremoved) this.removeReduncancy();
    return this;
  }
  
  public Set<K> getParents(K child) {
    if(!redundancyremoved) removeReduncancy();
    return parentMap.get(child);
  }
  
  public void removeReduncancy() {
    super.removeReduncancy();
    this.redundancyremoved = true;
    buildParentsMap(this);
  }

  private void buildParentsMap(Node<K> parent) {
    Map<K, Node<K>> childrenMap = parent.getChildrenTree();
    for(Node<K> child: childrenMap.values()) {
      Set<K> parentList = parentMap.get(child.id);
      if(parentList == null) {
        parentList = new HashSet<K>();
        parentMap.put(child.id, parentList);
      }
      parentList.add(parent.id);
      buildParentsMap(child);
    }
  }
}
