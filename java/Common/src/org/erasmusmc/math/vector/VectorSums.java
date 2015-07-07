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

public class VectorSums<D> {
  public double sumX, sumY, sumXY, sumXX, sumYY;
  public int nX, nY, nXY, N;
  public VectorSums(Vector<D> vector){
    VectorCursor<D> cursor = vector.getNonzeroCursor();
    
    while (cursor.isValid()) {
      double value = cursor.get();
      sumX += value;
      sumXX += value * value;
      nX++;
      cursor.next();
    }
    nY=nX;
    nXY=nX;
    N=nX;
    sumY=sumX;
    sumYY=sumX;
    sumXY=sumX;
  }
  public VectorSums(Vector<D> lhs, Vector<D> rhs) {
    if (rhs.getStoredValueCount() <= lhs.getStoredValueCount())
      calculate(lhs, rhs);
    else {
      calculate(rhs, lhs);
      double swap = sumX;
      sumX = sumY;
      sumY = swap;
      
      swap = sumXX;
      sumXX = sumYY;
      sumYY = swap;
      
      int intSwap;
      intSwap = nX;
      nX = nY;
      nY = intSwap;
    }
  }
  public double getMeanX(){
    return sumX/(double)nX;
  }
  public double getMeanY(){
    return sumY/(double)nY;
  }
  public double getStdDevX(){
    double meanX = getMeanX();
    return Math.sqrt(sumXX/(double)nX-meanX*meanX);
  }
  public double getStdDevY(){
    double meanY = getMeanY();
    return Math.sqrt(sumYY/(double)nY-meanY*meanY);
  }
  protected void calculate(Vector<D> lhs, Vector<D> rhs) {
    VectorCursor<D> cursor = lhs.getNonzeroCursor();
    
    while (cursor.isValid()) {
      double value = cursor.get();
      sumX += value;
      sumXX += value * value;
      nX++;
      cursor.next();
    }
    
    ParallelVectorCursor<D> parallelCursor = new ParallelVectorCursor<D>(rhs.getNonzeroCursor(), lhs.getSlaveCursor(), false);
    
    while (parallelCursor.isValid()) {
      double X = parallelCursor.slaveCursor.get();
      double Y = parallelCursor.masterCursor.get();
      
      sumY += Y;
      sumYY += Y * Y;
      nY++;
      
      if (X != 0d) {
        sumXY += X * Y;
        nXY++;
      }
      
      parallelCursor.next();
    }
    
    N = nX + nY - nXY;
  }
}
