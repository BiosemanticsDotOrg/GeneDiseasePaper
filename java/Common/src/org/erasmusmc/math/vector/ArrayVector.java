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

import org.erasmusmc.math.space.Space;

public class ArrayVector<D> extends IndexedVector<D> {
  public double[] values;
  public Space<D> space;

  public ArrayVector(Space<D> space) {
    super();
    
    setSpace(space);
  }
  
  public ArrayVector(Vector<D> vector) {
    super(vector);
  }
  
  public ArrayVector(Space<D> space, double[] array) {
    this.space = space;
    values = array;
  }
  
  public void setByIndex(int index, double value) {
    values[index] = value;
  }
  
  public double getByIndex(int index) {
    return values[index];
  }

  public Space<D> getSpace() {
    return space;
  }

  public void setSpace(Space<D> space) {
    this.space = space;
    values = new double[space.getDimensions()];
  }
}
