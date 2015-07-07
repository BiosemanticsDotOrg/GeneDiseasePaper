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


public class BitStack {
  private IntList data;
  private int pointer = maxdatabits-1;
  private static int maxdatabits = 32; // an int has 32 bits
  private int currentInt = 0;
  
  public BitStack(){
    data = new IntList();
  }
  
  public BitStack(int initialCapacity){
    data = new IntList((initialCapacity / maxdatabits)+1);
  }
  
  public void push(int number, int bits){
    for (int i = bits-1; i >= 0; i--){
      int nextbit = (number >>> i)& 1;
      push(nextbit);
    }
  }

  public void push(int bit){
    pointer--;
    if (pointer < 0){
      pointer = maxdatabits-1;
      data.add(currentInt);
      currentInt = 0;
    }
    currentInt |= bit << pointer;
  }
  
  public int pop(int bits){
    int result = 0;
    for (int i = 0; i < bits; i++){
      result |= pop() << i;
    }
    return result;
  }
  
  public int pop(){
    int result = (currentInt >>> pointer) & 1;
    pointer++;
    if (pointer >= maxdatabits){
      pointer = 0;
      currentInt = data.remove(data.size()-1);
    }
    return result;
  }
  
  public void trimToSize(){
    data.trimToSize();
  }

}
