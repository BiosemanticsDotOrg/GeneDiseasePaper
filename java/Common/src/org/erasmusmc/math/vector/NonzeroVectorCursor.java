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

public class NonzeroVectorCursor<D> implements VectorCursor<D> {
  protected VectorCursor<D> vectorCursor;
  
  public NonzeroVectorCursor(VectorCursor<D> vectorCursor) {
    this.vectorCursor = vectorCursor;
    
    while (vectorCursor.isValid() && vectorCursor.get() == 0)
      vectorCursor.next();
  }
  
  public boolean isValid() {
    return vectorCursor.isValid();
  }

  public void next() {
    do
      vectorCursor.next();
    while (vectorCursor.isValid() && vectorCursor.get() == 0);
  }

  public D dimension() {
    return vectorCursor.dimension();
  }

  public int index() {
    return vectorCursor.index();
  }

  public double get() {
    return vectorCursor.get();
  }

  public void set(double value) {
  }
}
