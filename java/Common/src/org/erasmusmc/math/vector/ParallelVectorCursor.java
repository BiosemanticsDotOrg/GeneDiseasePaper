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

package org.erasmusmc.math.vector;

public class ParallelVectorCursor<D> {
  protected VectorCursor<D> masterCursor;
  protected VectorSlaveCursor<D> slaveCursor;
  public VectorHandle<D> lhs, rhs;
  
  public ParallelVectorCursor(VectorCursor<D> masterCursor, VectorSlaveCursor<D> slaveCursor, boolean swap) {
    this.masterCursor = masterCursor;
    this.slaveCursor = slaveCursor;
    
    if (swap) {
      lhs = slaveCursor;
      rhs = masterCursor;
    }
    else {
      lhs = masterCursor;
      rhs = slaveCursor;
    }
    if (masterCursor.isValid())
      slaveCursor.synchronize(masterCursor);
  }
  
  public boolean isValid() {
    return masterCursor.isValid();
  }
  
  public void next() {
    masterCursor.next();
    
    if (masterCursor.isValid())
      slaveCursor.synchronize(masterCursor);
  }
}
