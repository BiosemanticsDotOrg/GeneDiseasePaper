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

package org.erasmusmc.math;

import org.erasmusmc.math.matrix.Matrix;
import org.erasmusmc.math.matrix.MatrixCursor;
import org.erasmusmc.math.vector.VectorCursor;

public class ClusterComparison {
  public static double adjustedRandIndex(Matrix contingencyTable){
    double sumOfBinomialCoefficientsij =0;
    double sumOfBinomialCoefficientsi=0;
    double sumOfBinomialCoefficientsj=0;
    int numberOfEntries=0;
    
    int[] sumForColumnsj=new int[contingencyTable.getColumnSpace().getDimensions()];
    MatrixCursor rowcursor = contingencyTable.getRowCursor();
    while (rowcursor.isValid()){
      //for (int i =0 ; i<contingencyTable.getRow().length;i++){
      int sumForCurrentRow=0;
      VectorCursor columncursor =  rowcursor.get().getCursor();
      int j =0;
      while (columncursor.isValid()){
        double value = contingencyTable.get(rowcursor.dimension(),columncursor.dimension());
        sumForColumnsj[j++]+=value;
      //for (int j = 0 ; j<contingencyTable[i].length;j++){
        
        sumOfBinomialCoefficientsij+=SpecialFunctions.BinomialCoefficient((int) value,2);
        sumForCurrentRow+=value;
        columncursor.next();
      }
      numberOfEntries+=sumForCurrentRow;
      sumOfBinomialCoefficientsi+=SpecialFunctions.BinomialCoefficient(sumForCurrentRow,2);
      rowcursor.next();
    }
    for (Integer sumj:sumForColumnsj){
      sumOfBinomialCoefficientsj+=SpecialFunctions.BinomialCoefficient(sumj,2);
    }
    double binomialCoefficientN= SpecialFunctions.BinomialCoefficient(numberOfEntries,2);
    double inbetween = sumOfBinomialCoefficientsi*sumOfBinomialCoefficientsj/binomialCoefficientN;
    return (sumOfBinomialCoefficientsij-inbetween)/(0.5*(sumOfBinomialCoefficientsi+sumOfBinomialCoefficientsj)-inbetween);
  }

}
