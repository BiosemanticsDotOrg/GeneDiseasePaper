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

package org.erasmusmc.peregrine;

import org.erasmusmc.peregrine.ConceptPeregrine.TermLink;

/**
 * Internal class for Peregrine
 * @author martijn
 *
 */
public class IndexTerm {
  public int[] checkedWordPos;
  public byte checkedCount = 0;
  public int lastChecked = 0;
  
  public final void insert(int w){
    checkedWordPos[0] = w;
  }
  
  public final void insertFirst(TermLink termlink, int w1, int w2){
    checkedWordPos[termlink.wordPos1] = w1;
    checkedWordPos[termlink.wordPos2] = w2;
    checkedCount = 2;
    lastChecked = w2;
  }
  
  public final void insert(TermLink termlink, int w1, int w2){
    checkedWordPos[termlink.wordPos1] = w1;
    checkedWordPos[termlink.wordPos2] = w2;
    checkedCount++;
    lastChecked = w2;
  }
  
  public final void clear(){
    checkedCount = 0;
    lastChecked = 0;
    for (int i = 0; i < checkedWordPos.length; i++){
      checkedWordPos[i] = -1;
    }
  }
  
  
}
