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

import java.util.Comparator;

import org.erasmusmc.collections.SortedList;
import org.erasmusmc.math.vector.ObjectAndDoubleEntry;

public class ClassificationResultEvaluator {
  
  SortedList<ObjectAndDoubleEntry<Boolean>> classificationOutcome;
  public int positives;
  public int seedID;
  public double AUC;
  public double MAP;
  public void evaluate(){
    calculateAUC();
    calculateMAP();
  }
  public int getNumberOfMatches(){
    return classificationOutcome.size();
  }
  public ClassificationResultEvaluator(SortedList<ObjectAndDoubleEntry<Boolean>> classificationOutcome){
    this.classificationOutcome=classificationOutcome;
    positives = -1;
    AUC = -1;
    MAP = -1;
  }
  public void findPositives(){
    positives = 0;
    for (ObjectAndDoubleEntry<Boolean> vectorEntry: classificationOutcome  ){
      if (vectorEntry.key)
        positives++;
    }
  }
  public int getNumberOfPositives(){
    if (positives<0){
      findPositives();
    }
    return positives;
  }
  public void calculateMAP(){
    
    MAP=0;
    if (positives>0){
      double falsePositives = 0;
      double truePositives = 0;
      
      int i =0 ;
      while (i<classificationOutcome.size() && truePositives<positives){
      
        if (classificationOutcome.get(i).key){
          truePositives++;
          MAP = MAP + truePositives/(truePositives+falsePositives);
        }
        else 
          falsePositives++;
        i++;
      }
      MAP=MAP/(double)positives;
    }
    
  }
  private double trapArea(double x1, double x2, double y1,double y2){
    //this function calculates the area of an trapezoid whose corners
    //lie at given x and y values; 
    double base = x1-x2;
    double avheight = (y1+y2)/2d;
    return base*avheight;
  }
  public double calculateAUC(){
    if (positives<0){
      findPositives();
    }
    AUC=0;
    if (positives>0){
      int falsePositives = 0;
      int previousFalsePositives = falsePositives;
      //int falseNegatives = positives;
      int truePositives = 0;
      int previousTruePositives = truePositives;
      //int trueNegatives=classificationOutcome.size()-positives;
      double previousValue=Double.NEGATIVE_INFINITY;
      for (ObjectAndDoubleEntry<Boolean> entry: classificationOutcome  ){
        if(entry.value!=previousValue){
         AUC+=trapArea(falsePositives,previousFalsePositives,truePositives,previousTruePositives);
          previousValue = entry.value;
          previousFalsePositives = falsePositives;
          previousTruePositives = truePositives;
        }
        if (entry.key){
          truePositives++;
        }
        else {
          falsePositives++;
         }
       
      }
      AUC+=trapArea(falsePositives,previousFalsePositives,truePositives,previousTruePositives);
      AUC/=(double)(positives*(getNumberOfMatches()-positives));
      
    }
 
    return AUC;
  }
  public static Comparator <ObjectAndDoubleEntry<Boolean>> classificationResultComparatorDescending() {
    return new Comparator<ObjectAndDoubleEntry<Boolean>>() {
      public int compare(ObjectAndDoubleEntry object1, ObjectAndDoubleEntry object2) {
        if (object1.value < object2.value){
          return 1;
        }
        else if (object1.value == object2.value){
          return 0;
        }
        else {
          return -1;
        }
      }
    };
  }
  public static Comparator <ObjectAndDoubleEntry<Boolean>> classificationResultComparatorAscending() {
    return new Comparator<ObjectAndDoubleEntry<Boolean>>() {
      public int compare(ObjectAndDoubleEntry object1, ObjectAndDoubleEntry object2) {
        if (object1.value > object2.value){
          return 1;
        }
        else if (object1.value == object2.value){
          return 0;
        }
        else {
          return -1;
        }
      }
    };
  }

}