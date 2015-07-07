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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Node<K> {
  protected K id;
  protected Map<K, Node<K>> children;
  protected Comparator<K> comparator;
  protected List<K> sortedChildren;
  protected boolean needsToSort;

  public Node(K id) {
    this.id = id;
    children = new HashMap<K, Node<K>>();
  }

  public K getID() {
    return id;
  }
  public Set<K> getAllNodesBelowNode(){
    Set<K> result = getChildren();
    for (Node<K> child: children.values()) {
      result.addAll(child.getAllNodesBelowNode());
    }
    return result;

  }

  public List<K> asList() {
    return new ArrayList<K>(children.keySet());
  }

  public Set<K> getChildren() {
    return children.keySet();
  }
  
  public List<K> getChildrenAsSortedList() {
    if(needsToSort) {
      sortedChildren = new ArrayList<K>(getChildren());
      if(comparator != null) {
        Collections.sort(sortedChildren, comparator);
        needsToSort = false;
      }
    }
    return sortedChildren;
  }
  
  public List<K> getChildrenAsSortedList(Comparator<K> comparator) {
    if(this.comparator != comparator) { 
      this.comparator = comparator;
      needsToSort = true;
    }
    return getChildrenAsSortedList();
  }

  public Map<K, Node<K>> getChildrenTree() {
    return children;
  }

  /** Check if the tree contains the node */
  
  public Node<K> getNode(K object) {
    if (object.equals(this.id)) {
      return this;
    }
    else {
      for (Node<K> child: children.values()) {
        Node<K> result = child.getNode(object);
        if (result != null && result.id.equals(object)) {
          return result;
        }
      }
    }
    return null;
  }
  

  public boolean containsNode(K node) {
    if (node.equals(this.id)) {
      return true;
    }
    else {
      for (Node<K> child: children.values()) {
        if (child.containsNode(node)) {
          return true;
        }
      }
    }
    return false;
  }

  public Node<K> add(K newChild) {
    needsToSort = true;
    if (id.equals(newChild)) {
      //System.out.println("circular reference attempted with: " + newChild.toString() + " in tree of " + id.toString());
      return null;
    }
    else if(children.containsKey(newChild)){
      //System.out.println("child : " + newChild.toString() + " already exists in tree of " + id.toString());
      return null;
    }
    else {
      Node<K> childNode = new Node<K>(newChild);
      children.put(newChild, childNode);
      return childNode;
      
      
    }

  }
  public Node<K> set(K newChild) {
    removeChild(newChild);
    return add(newChild);
  }
   
  public Node<K> set(Node<K> newChild) {
    removeChild(newChild.id);
    return add(newChild);
  }
  
  
  public Node<K> add(Node<K> newChild) {
    needsToSort = true;
    if (newChild.containsNode(id)) {
      //System.out.println("circular reference attempted: attempt to place  " + newChild.id.toString() + " in tree of " + id.toString());
      return null;
    }
    else if(children.containsKey(newChild.id)){
      //System.out.println("child : " + newChild.id.toString() + " already exists in tree of " + id.toString());
      return null;
    }
    else {
      children.put(newChild.id, newChild);
      return newChild;
    }
  }

  public int countChildren() {
    return children.size();
  }

  public Node<K> removeChild(K child) {
    return children.remove(child);
  }

  public String toString() {
    String result = "Tree of " + id.toString() + "{\n";
    result = result + childrentoString(2) + "}";
    return result;

  }

  private String childrentoString(int counter) {
    String result = "";
    String spaces = "";
    for (int i = 0; i < counter; i++)
      spaces += " ";
    for (Node<K> child: children.values()) {
      result = result + spaces + child.id.toString() + "\n";
      result = result + child.childrentoString(counter + 2);
    }
    return result;
  }
  /**
   * this function rewrites redundancy of the following type:
   * node A is parent of B and C, B is a parent of C. The algorithm removes A is parent of C.
   * i.e. avoid repetition and prefer the longest tree  
   */
  public void removeReduncancy(){
    Set<K> remove = new HashSet<K>();
    for (K child: children.keySet()){
      for(Node<K> childTree: children.values()){
        if(!child.equals(childTree.id)){
          if(childTree.containsNode(child)){
            remove.add(child);
          }
        }
      }
    }
    for(K child:remove){
      //System.out.println("remove redundancy: node " + child.toString() + " occurs at different layers");
      removeChild(child);
    }
    for(Node<K> childTree: children.values()){
      childTree.removeReduncancy();
    }
  }
}
