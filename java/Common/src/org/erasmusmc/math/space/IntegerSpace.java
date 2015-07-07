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

package org.erasmusmc.math.space;

import java.io.Serializable;
import java.util.Iterator;

public class IntegerSpace extends DefaultSpace<Integer> implements Serializable {
  private static final long serialVersionUID = -5957501459753317173L;
  public int dimensions;
  
  public IntegerSpace(int dimensions) {
    this.dimensions = dimensions;
  }
  public IntegerSpace(){
    this.dimensions= Integer.MAX_VALUE;
  }
  
  public int getDimensions() {
    return dimensions;
  }

  public int indexOfObject(Integer object) {
    return (Integer) object;
  }

  public Integer objectForIndex(int index) {
    return index;
  }

  public Iterator<Integer> iterator() {
    return new IntegerIterator(dimensions);
  }
}
