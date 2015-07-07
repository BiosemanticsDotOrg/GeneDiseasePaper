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
import java.util.Iterator;
import java.util.List;

public class Trie<T> {
  private TrieNode root;
  private CharIterator charIterator;
  private int direction;
  public static final int FORWARD = 0;
  public static final int BACKWARD = 1;
  
  public Trie(int direction){
    root = new TrieNode();
    this.direction = direction;
    if (direction == FORWARD)
      charIterator = new ForwardCharIterator();
    else
      charIterator = new BackwardCharIterator();
  }
  
  /**
   * Add a key to the trie, and link it to the value
   * @param key
   * @param value
   */
  public void put(String key, T value){
    TrieNode node = root;
    charIterator.setString(key);
    while (charIterator.hasNext()){
      char ch = charIterator.next();      
      int index = node.binarySearch(ch);
      if (index < node.size() && node.getKeyForIndex(index) == ch){ //Entry with char already exists
        Object object = node.getObjectForIndex(index);
        if (object instanceof TrieNode){ 
          node = (TrieNode) object;
          if (!charIterator.hasNext()) //end of key
            node.value = value;      
        } else {
          if (!charIterator.hasNext()) //end of key
            node.setObject(index, value);
          else {
            TrieNode tempNode = new TrieNode();
            tempNode.value = object;
            node.setObject(index, tempNode);
            node = tempNode;
          }
        }
      } else { //Entry with char does not exist
        if (!charIterator.hasNext()) //end of key
          node.insert(index, ch, value);
        else {
          TrieNode tempNode = new TrieNode();
          node.insert(index, ch, tempNode);
          node = tempNode;
        }
      }
    }
  }
  
  /**
   * Get the longest key found in the string (at the beginning or end)
   * @param string
   * @return
   */
  @SuppressWarnings("unchecked")
  public T getLongest(String string){
    TrieNode node = root;
    charIterator.setString(string);
    T result = null;
    while (charIterator.hasNext()){
      char ch = charIterator.next();
      int index = node.indexOf(ch);
      if (index == -1)
          return result;
      else {
        Object object = node.getObjectForIndex(index);
        if (object instanceof TrieNode){
          node = (TrieNode)object;
          if (node.value != null)
            result = (T)node.value;
        } else
          return (T)object;
      }
    }
    return result;
  }
  
  /**
   * Get all the keys found in the string (at the beginning or end)
   * @param string
   * @return
   */
  @SuppressWarnings("unchecked")
  public List<T> getAll(String string){
    TrieNode node = root;
    charIterator.setString(string);
    List<T> result = new ArrayList<T>();
    while (charIterator.hasNext()){
      char ch = charIterator.next();
      int index = node.indexOf(ch);
      if (index == -1)
          return result;
      else {
        Object object = node.getObjectForIndex(index);
        if (object instanceof TrieNode){
          node = (TrieNode)object;
          if (node.value != null)
            result.add((T)node.value);
        } else {
          result.add((T)object);
          return result;
        }
      }
    }
    return result;
  }
  
  @SuppressWarnings("unchecked")
  public T getFullMatch(String key){
    TrieNode node = root;
    charIterator.setString(key);
    while (charIterator.hasNext()){
      char ch = charIterator.next();
      int index = node.indexOf(ch);
      if (index == -1)
          return null;
      else {
        Object object = node.getObjectForIndex(index);
        if (object instanceof TrieNode){
          node = (TrieNode)object;
          if (!charIterator.hasNext())
            return (T)node.value;
        } else {
          if (!charIterator.hasNext())
            return (T)object;
          return null;
        }
      }
    }
    return null;
  }
  
  public int size(){
    return countValues(root);
  }
  
  private int countValues(TrieNode node) {
    int count = 0;
    if (node.value != null)
      count++;
    for (int i = 0; i < node.size(); i++){
      Object object = node.getObjectForIndex(i);
      if (object instanceof TrieNode)
        count += countValues((TrieNode)object);
      else
        count++;
    }
    return count;
  }
  
  public Iterator<Pair<String,T>>getEntryIterator() {
    return new EntryIterator();
  }
  
  private class EntryIterator implements Iterator<Pair<String,T>>{
    private IntList path;
    private Pair<String,T> buffer;
    public EntryIterator(){
      path = new IntList();
      buffer = findNext();
    }
    
    public boolean hasNext() {
      return buffer != null;
    }

    public Pair<String,T> next() {
      Pair<String,T> result = buffer;
      buffer = findNext();
      return result;
    }

    @SuppressWarnings("unchecked")
    private Pair<String,T> findNext() {
      Object currentObject = traversePath(0);
      int skip = 0;
      if (!(currentObject instanceof TrieNode)){
        skip++;
        currentObject = traversePath(skip);          
      }  
      while (currentObject != null){
        int index;
        if (skip == 0)
          index = 0; //New at this node: start at begin
        else
          index = path.getInt(path.size()-skip) + 1; //One further
        
        if (index == ((TrieNode)currentObject).size()){ //This node is finished
          skip++;
          if (skip > path.size())
            return null;
          currentObject = traversePath(skip);
        } else {
          //Construct the path to the new node:
          path = path.subList(0, path.size()-skip);
          path.add(index);
          skip = 0;
          //Fetch the new node:
          currentObject = ((TrieNode)currentObject).getObjectForIndex(index);
          if (currentObject instanceof TrieNode){
            if (((TrieNode)currentObject).value != null)
              return new Pair<String, T>(reconstructKey(),(T)((TrieNode)currentObject).value);
          } else {
            return new Pair<String, T>(reconstructKey(),(T)currentObject);
          }
        }

      }
      return null;
    }

    private Object traversePath(int skip) {
      Object node = root;
      for (int i = 0; i < path.size()-skip; i++)
        node = ((TrieNode)node).getObjectForIndex(path.getInt(i));
      return node;
    }
    
    private String reconstructKey(){
      TrieNode node = root;
      StringBuilder result = new StringBuilder();
      for (int i = 0; i < path.size(); i++){
        int index = path.getInt(i);
        result.append(node.getKeyForIndex(index));
        if (i < path.size()-1)
          node = (TrieNode)node.getObjectForIndex(index);
      }  
      if (direction == FORWARD)
        return result.toString();
      else
        return result.reverse().toString();
            
    }
    

    public void remove() {
      System.err.println("Calling unimplemented remove method");
    }
    
  }

  private interface CharIterator {
    public boolean hasNext();
    public char next();
    public void setString(String string);
  }
  
  private class ForwardCharIterator implements CharIterator {
    private String string;
    private int index;
    
    public void setString(String string){
      this.string = string;
      index = 0;
    }
    public boolean hasNext() {
      return (index < string.length());
    }
    public char next() {
      return string.charAt(index++);
    }
  }
  
  private class BackwardCharIterator implements CharIterator {
    private String string;
    private int index;
    
    public void setString(String string){
      this.string = string;
      index = string.length()-1;
    }
    public boolean hasNext() {
      return (index > -1);
    }
    public char next() {
      return string.charAt(index--);

    }
  }
  
  

}
