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

public interface Space<D> extends Iterable<D> {
  public int getDimensions();
  public int indexOfObject(D object);
  public D objectForIndex(int index);
  public String getDimensionsCaption();
  public void setDimensionsCaption(String dimensionsCaption);
  public String getValuesCaption();
  
  public static Space<Integer> twoD = new IntegerSpace(2);
  public static Space<Integer> threeD = new IntegerSpace(3);
}
