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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.erasmusmc.math.space.Space;
import org.erasmusmc.streams.BinaryInputStream;



public abstract class Vector<D> {
  protected Double squarednorm = -1d;
  public Vector() {
  }
  
  public Vector(Vector<D> source) {
    setSpace(source.getSpace());
    set(source);
  }
  
  public Vector(Space<D> space) {
    setSpace(space);
  }
    
  public abstract Space<D> getSpace();
  public abstract void setSpace(Space<D> space);
  public abstract void set(D object, double value);
  public abstract double get(D object);
  public abstract int getStoredValueCount();
  public abstract VectorCursor<D> getCursor();
  public abstract VectorCursor<D> getNonzeroCursor();
  public abstract VectorSlaveCursor<D> getSlaveCursor();
  public abstract void set(Vector<D> vector);
  
  public static Vector<Integer> point2D(double x, double y) {
    Vector<Integer> result = new ArrayVector<Integer>(Space.twoD);
    result.set(0, x);
    result.set(1, y);
    
    return result;
  }
  
  public void add(D index, double value) {
    set(index, get(index) + value);
    squarednorm=-1d;
  }

  public void subtract(D index, double value) {
    set(index, get(index) - value);
    squarednorm=-1d;
  }

  public void multiply(D index, double value) {
    set(index, get(index) * value);
    squarednorm=-1d;
  }

  public void divide(D index, double value) {
    set(index, get(index) / value);
    squarednorm=-1d;
  }

  public void readFromStream(BinaryInputStream is) throws java.io.IOException  {
    is.readInteger();

    VectorCursor<D> cursor = getCursor();
    
    while (cursor.isValid()) {
      cursor.set(is.readDouble());
      cursor.next();
    }
  }

  public double distanceTo(Vector<D> vector) {
    return Math.sqrt(squaredDistanceTo(vector));
  }
  
  public double squaredDistanceTo(Vector<D> vector) {
    /*double result = 0;
    double distance;

    for (Object dimension: getSpace()) {
      distance = get(dimension) - vector.get(dimension);
      result += distance * distance;
    }

    return result;*/
    
    // Numerically unstable
    // Should be fixed!
    
    VectorSums<D> vectorSums = new VectorSums<D>(this, vector);
    double result = vectorSums.sumXX - 2 * vectorSums.sumXY + vectorSums.sumYY;
    
    if (result < 0d) // Catch unstability: in theory 'result' should be nonnegative 
      return 0d;
    else
      return result;
  }
public boolean isSquaredNormCalculated(){
  if(squarednorm==-1d){
    return false;
  }
  else {
    return true;
  }
}
public void setSquaredNorm(Double squaredNorm){
  this.squarednorm = squaredNorm;
}
  public Double getSquaredNorm() {
    if (squarednorm==-1d){
      squarednorm=0d;
      VectorCursor<D> cursor = getNonzeroCursor();
      while (cursor.isValid()) {
        double value = cursor.get();
        squarednorm+= value * value;
        cursor.next();
      }
    }
          
    return squarednorm;
  }
  
  public double norm() {
    return Math.sqrt(getSquaredNorm());
  }
  
  public void normalize() {
    divide(norm());
    squarednorm=1d;
  }
  
  /**
   * Adjusts the length of the vector in such a way that the maximum value is 1.
   */
  public void unify() {
    divide(max());
    squarednorm=-1d;
  }
  
  /**
   * Finds the maximum value in a vector.
   */
  public double max() {
    VectorCursor<D> cursor = getNonzeroCursor();
    
    double maximumValue;
    
    if (getStoredValueCount() == getSpace().getDimensions()) 
      maximumValue = Double.NEGATIVE_INFINITY;
    else
      maximumValue = 0;
    
    while (cursor.isValid()) {
      maximumValue = Math.max(maximumValue, cursor.get());
      cursor.next();
    }

    return maximumValue;
  }

  public static <D> SparseVector<D> pairwiseElementwiseProduct(List<? extends Vector<D>> vectors) {
    SparseVector<D> result = new SparseVector<D>(vectors.get(0).getSpace());
    
    for (int i = 0; i < vectors.size() - 1; i++) 
      for (int j = i + 1; j < vectors.size(); j++ )
        result.add(elementwiseProduct(vectors.get(i), vectors.get(j)));

    return result;
  }
  
  public double innerProduct(Vector<D> vector) {
    double result = 0;
    
    ParallelVectorCursor<D> cursor = getParallelNonzeroCursor(vector);

    while (cursor.isValid()) {
      result += cursor.lhs.get() * cursor.rhs.get();
      cursor.next();
    }
          
    return result;
  }
  
  public double cosine(Vector<D> vector) {
    double result = 0;
    double denominator = norm() * vector.norm();
    
    if (denominator > 0)
      result = innerProduct(vector) / denominator;

    return result;
  }
  
  public ParallelVectorCursor<D> getParallelCursor(Vector<D> vector) {
    return new ParallelVectorCursor<D>(getCursor(), vector.getSlaveCursor(), false);
  }
  
  public ParallelVectorCursor<D> getParallelNonzeroCursor(Vector<D> vector) {
    if (getStoredValueCount() <= vector.getStoredValueCount())
      return new ParallelVectorCursor<D>(getNonzeroCursor(), vector.getSlaveCursor(), false);
    else
      return new ParallelVectorCursor<D>(vector.getNonzeroCursor(), getSlaveCursor(), true);
  }

  public void dump() {
    VectorCursor<D> cursor = getCursor();
    
    while (cursor.isValid()) {
      System.out.print(cursor.get() + " | ");      
      cursor.next();
    }
    
    System.out.println("");
  }
  
  public String getNonzerosString() {
    StringBuffer stringBuffer = new StringBuffer();
    VectorCursor<D> cursor = getNonzeroCursor();
    
    while (cursor.isValid()) {
      stringBuffer.append(cursor.dimension() + ": " + cursor.get() + "\n");
      cursor.next();
    }
    
    return stringBuffer.toString();
  }
  
  public void dumpNonzeros() {
    VectorCursor<D> cursor = getNonzeroCursor();
    
    while (cursor.isValid()) {
      System.out.println(cursor.dimension() + ":\t" + cursor.get());
      cursor.next();
    }
  }

  public void zeroes() {
    VectorCursor<D> cursor = getNonzeroCursor();
    
    while (cursor.isValid()) { 
      cursor.set(0);
      cursor.next();
    }
  }

  public void unit(D index) {
    zeroes();
    set(index, 1);
  }
  
  public void constants(double value) {
    VectorCursor<D> cursor = getCursor();
    
    while (cursor.isValid()) {
      cursor.set(value);
      cursor.next();
    }
  }
  public void add(Double value){
    VectorCursor<D> cursor = getNonzeroCursor();
    
    while (cursor.isValid()){
      cursor.set(cursor.get() + value);
      cursor.next();
    }
  }
  public void add(Vector<D> vector) {
    ParallelVectorCursor<D> cursor = new ParallelVectorCursor<D>(vector.getNonzeroCursor(), getSlaveCursor(), false);
    
    while (cursor.isValid()) {
      cursor.slaveCursor.set(cursor.slaveCursor.get() + cursor.masterCursor.get());
      cursor.next();
    }
    squarednorm=-1d;
  }

  public void subtract(Vector<D> vector) {
    ParallelVectorCursor<D> cursor = new ParallelVectorCursor<D>(vector.getNonzeroCursor(), getSlaveCursor(), false);
    
    while (cursor.isValid()) {
      cursor.slaveCursor.set(cursor.slaveCursor.get() - cursor.masterCursor.get());
      cursor.next();
    }
    squarednorm=-1d;
  }
  
  public void divide(double value) {
    VectorCursor<D> cursor = getNonzeroCursor();
    
    while (cursor.isValid()){
      cursor.set(cursor.get() / value);
      cursor.next();
    }
    squarednorm=-1d;
  }
  
  public void multiply(double value) {
    VectorCursor<D> cursor = getNonzeroCursor();
    
    while (cursor.isValid()){
      cursor.set(cursor.get() * value);
      cursor.next();
    }
    squarednorm=-1d;
  }
  
  public void random(Random random) {
    VectorCursor<D> cursor = getCursor();
    
    while (cursor.isValid()) {
      cursor.set(random.nextDouble());
      cursor.next();
    }
  }
  
  public void random() {
    random(new Random());
  }
  
  public static <D> void random(Collection<Vector<D>> vectors) {
    Random random = new Random();
    
    for (Vector<D> vector: vectors) 
      vector.random(random);
  }
  
  public static <D> void meanVector(Vector<D> result, Collection<? extends Vector<D>> vectors) {
    for (Vector<D> vector: vectors) {
      ParallelVectorCursor<D> cursor = new ParallelVectorCursor<D>(vector.getNonzeroCursor(), result.getSlaveCursor(), false);

      while (cursor.isValid()) {
        cursor.slaveCursor.set(cursor.slaveCursor.get() + cursor.masterCursor.get());
        cursor.next();
      }
    }
    
    result.divide(vectors.size());        
  }
  public void standardize(){
    VectorSums<D> sums = new VectorSums<D>(this);
    add(-sums.getMeanX());
    divide(sums.getStdDevX());
  }
  public void meanCenter(){
    VectorSums<D> sums = new VectorSums<D>(this);
    add(-sums.getMeanX());
  }
  
  /**
   * Calculates Pearson's correlation. 
   * 
   * Note: Is only based on vector components where one or both vectors have a non-zero value!
   * @param Other vector
   * @return Pearson's correlation
   */
  public double pearsonCorrelation(Vector<D> vector) {
    VectorSums<D> vectorSums = new VectorSums<D>(this, vector);
    double doubleN = vectorSums.N;    
    double inbetween =Math.sqrt((vectorSums.sumXX - (vectorSums.sumX * vectorSums.sumX) / doubleN) * ((vectorSums.sumYY - (vectorSums.sumY * vectorSums.sumY) / doubleN)));
    
    if (inbetween==0d){
      return 0d;
    }
    
    return      (vectorSums.sumXY - (vectorSums.sumX * vectorSums.sumY) / doubleN) /inbetween; 
      
  }
  /**
   * Calculates Pearson's correlation. 
   * 
   * Note: Uses vector space to determine number of dimensions
   * @param Other vector
   * @return Pearson's correlation
   */  
  public double pearsonCorrelationCorrectN(Vector<D> vector) {
    VectorSums<D> vectorSums = new VectorSums<D>(this, vector);
    double doubleN = vector.getSpace().getDimensions();  
    double inbetween =Math.sqrt((vectorSums.sumXX - (vectorSums.sumX * vectorSums.sumX) / doubleN) * ((vectorSums.sumYY - (vectorSums.sumY * vectorSums.sumY) / doubleN)));
    
    if (inbetween==0d){
      return 0d;
    }
    
    return      (vectorSums.sumXY - (vectorSums.sumX * vectorSums.sumY) / doubleN) /inbetween; 
      
  }
  
  public double kendallsTau(Vector<D> vector){
    VectorCursor<D> cursor1 = getNonzeroCursor();
    VectorCursor<D> cursor2 = vector.getNonzeroCursor();
    int is=0;
    int n1=0;
    int n2=0;
    
    //SparseVector keys = new SparseVector(this.getSpace());
    Set<D> keySet = new HashSet<D>();
    Map<D, Double> map1 = new HashMap<D, Double>();
    Map<D, Double> map2 = new HashMap<D, Double>();
    while (cursor1.isValid()) {
      keySet.add(cursor1.dimension());
      map1.put(cursor1.dimension(),cursor1.get());
      map2.put(cursor1.dimension(),0d);
      cursor1.next();
    }
    while (cursor2.isValid()) {
      keySet.add(cursor2.dimension());
      map2.put(cursor2.dimension(),cursor2.get());
      map1.put(cursor2.dimension(),get(cursor2.dimension()));
      cursor2.next();
    }  
    
    List<D> keys = new ArrayList<D>(keySet);
    
    for (int i = 0; (i+1)<keys.size();i++){
      D object1 = keys.get(i);
      double valueA1 = map1.get(object1);
      double valueB1 = map2.get(object1);
      for (int j = i+1; j<keys.size();j++){
        D object2 = keys.get(j); 
        double valueA2 = map1.get(object2);
        double valueB2 = map2.get(object2);
        double a1 = valueA1-valueA2;
        double a2 = valueB1-valueB2;
        double aa = a1*a2;
        if (aa!=0){
          n1++;
          n2++;
          if (aa>0)
            is++;
          else
            is--;
        }
        else {
          if (a1!=0){
            n1++;}
          if (a2!=0){
            n2++;}
        }
      }
    }
    double result = (double) is / (Math.sqrt((double)n1)*Math.sqrt((double)n2));
    return result;
  }
  
public double spearmanRankCorrelation(Vector<D> vector){
  //Note: this correlation factor should only be used when there are not a lot of ties! 
 
    
  ParallelVectorCursor<D> cursor1 = getParallelNonzeroCursor(vector);
  ParallelVectorCursor<D> cursor2 = vector.getParallelNonzeroCursor(this);
 // VectorCursor cursorlhs = vector.getNonzeroCursor();
 // VectorCursor cursorrhs = this.getNonzeroCursor();
  List<ObjectAndDoubleEntry<D>> master=new ArrayList<ObjectAndDoubleEntry<D>>();
  List<ObjectAndDoubleEntry<D>> slave=new ArrayList<ObjectAndDoubleEntry<D>>();
  while (cursor1.isValid()) {
    master.add(new ObjectAndDoubleEntry<D>(cursor1.masterCursor.dimension(),cursor1.masterCursor.get()));
    slave.add(new ObjectAndDoubleEntry<D>(cursor1.slaveCursor.dimension(),cursor1.slaveCursor.get()));
    cursor1.next();
  }
  int check=0;
  while (cursor2.isValid()) {
    if (cursor2.slaveCursor.get() == 0){
      slave.add(new ObjectAndDoubleEntry<D>(cursor2.masterCursor.dimension(),cursor2.masterCursor.get()));
      master.add(new ObjectAndDoubleEntry<D>(cursor2.slaveCursor.dimension(),cursor2.slaveCursor.get()));
    }
    else {
      check++;
    }
    cursor2.next();
  }  
  //if (check > 0){
    Collections.sort(master,ObjectAndDoubleEntry.mapEntryComparatorDescending());
    Collections.sort(slave,ObjectAndDoubleEntry.mapEntryComparatorDescending());
    Vector<D> resultLhs = new SparseVector<D>(vector.getSpace());
    Vector<D> resultRhs = new SparseVector<D>(vector.getSpace());
    //calculating ranks taking ties into account (averaging ranks when ties occur)  
    //once
    int i = 0;
    int sum0fTies3MinusTies1 = 0;
    while (i<master.size()){
      double rank = (double)i+1d;
      int j =i+1;
      int number =1;
      while (j<master.size() && (master.get(i).value == master.get(j).value)){
        rank += (double)j + 1d;
        number++;
        j++;
      }
      if (number > 1)
         sum0fTies3MinusTies1+=(number)*(number)*(number)-(number);
      
      rank = rank / (double)number;
      
      for (j = i; j< ( i + number ); j++){
        resultLhs.set(master.get(j).key,rank);     
      }
      i += number;
    }
    //calculating ranks taking ties into account (averaging ranks when ties occur)
    //twice (will make function when I use it again ;) cheers RJ
    i = 0;
    int sum0fTies3MinusTies2 = 0;
    while (i<slave.size()){
      double rank = (double)i + 1d;
      int j =i+1;
      int number = 1;
      while (j<slave.size() && (slave.get(i).value == slave.get(j).value)){
        rank += (double)j + 1d;
        number++;
        j++;
      }
      if (number > 1)
        sum0fTies3MinusTies2+=(number)*(number)*(number)-(number);
      
      rank = rank / (double)number;
      
      for (j = i; j< ( i + number ); j++){
        resultRhs.set(slave.get(j).key,rank);       
      }
      i += number;
    }
    
    int N = slave.size();
    int N3minusN=N*N*N-N;
    double fac = (1-(double)sum0fTies3MinusTies1/(double)N3minusN)*(1-(double)sum0fTies3MinusTies2/(double)N3minusN);
    ParallelVectorCursor<D> cursor = resultLhs.getParallelNonzeroCursor(resultRhs);
    double sumOfSquaredDifferenceInRanks =0d ;
    while (cursor.isValid()){
      double difference=cursor.masterCursor.get() - cursor.slaveCursor.get();
      sumOfSquaredDifferenceInRanks += difference*difference;
      cursor.next();
    }
    return (1-(6/(double)N3minusN)*(sumOfSquaredDifferenceInRanks+((double)sum0fTies3MinusTies1+(double)sum0fTies3MinusTies2)/12d)) / 
             Math.sqrt(fac);
  }
 // else{
 //   return 0;
 // }
  
//}
    
  public String toString() {
    return "Vector";
  }
  
  public static <D> SparseVector<D> elementwiseProduct(Vector<D> lhs, Vector<D> rhs) {
    ParallelVectorCursor<D> cursor = lhs.getParallelNonzeroCursor(rhs);
    SparseVector<D> result = new SparseVector<D>(lhs.getSpace());
    
    while (cursor.isValid()){
      double product = cursor.lhs.get() * cursor.rhs.get();
      result.set(cursor.lhs.dimension(),product);
      cursor.next();
    }
    
    return result;
  }
  
  public double sum() {
    double result = 0;

    VectorCursor<D> cursor = getNonzeroCursor();
    
    while(cursor.isValid()){
      result += cursor.get();
      cursor.next();
    }
    
    return result;
  }
  
  public String getNonZeroDimensionText() {
    String result = "";
    VectorCursor<D> cursor = getNonzeroCursor();
    
    if (cursor.isValid()) {
      result += "\"" + cursor.dimension() + "\"";
      cursor.next();
      
      while (cursor.isValid()) {
        result += ", \"" + cursor.dimension() + "\"";
        cursor.next();
      }   
    }
    
    return result;
  }
  private List<ObjectAndDoubleEntry<D>> getNonzeroEntrieslist(){
    List<ObjectAndDoubleEntry<D>> values = new ArrayList<ObjectAndDoubleEntry<D>>();
    VectorCursor<D> vectorcursor = this.getNonzeroCursor();
    while (vectorcursor.isValid()){
      values.add(new ObjectAndDoubleEntry<D>(vectorcursor.dimension(),vectorcursor.get()));
      vectorcursor.next();
    }
   return values;
   
  }
  private List<ObjectAndDoubleEntry<D>> getAllEntrieslist(){
    List<ObjectAndDoubleEntry<D>> values = new ArrayList<ObjectAndDoubleEntry<D>>();
    VectorCursor<D> vectorcursor = this.getCursor();
    while (vectorcursor.isValid()){
      values.add(new ObjectAndDoubleEntry<D>(vectorcursor.dimension(),vectorcursor.get()));
      vectorcursor.next();
    }
   return values;
   
  }
  public List<ObjectAndDoubleEntry<D>> getNonZeroEntriesSortedOnAscendingValue(){
    List<ObjectAndDoubleEntry<D>> values = getNonzeroEntrieslist();
    Collections.sort(values, ObjectAndDoubleEntry.mapEntryComparatorAscending());
    return values;
  }
  public List<ObjectAndDoubleEntry<D>> getNonZeroEntriesSortedOnDescendingValue(){
    List<ObjectAndDoubleEntry<D>> values = getNonzeroEntrieslist();
    Collections.sort(values, ObjectAndDoubleEntry.mapEntryComparatorDescending());
    return values;
  }
  public List<ObjectAndDoubleEntry<D>> getAllEntriesSortedOnAscendingValue(){
    List<ObjectAndDoubleEntry<D>> values = getAllEntrieslist();
    Collections.sort(values, ObjectAndDoubleEntry.mapEntryComparatorAscending());
    return values;
  }
  public List<ObjectAndDoubleEntry<D>> getAllEntriesSortedOnDescendingValue(){
    List<ObjectAndDoubleEntry<D>> values = getAllEntrieslist();
    Collections.sort(values, ObjectAndDoubleEntry.mapEntryComparatorDescending());
    return values;
  }
  public String getEntriesSortedOnValueString(){
   List<ObjectAndDoubleEntry<D>> values = getNonZeroEntriesSortedOnAscendingValue();
   StringBuffer result = new StringBuffer();
   for (ObjectAndDoubleEntry<D> entry: values){
     result.append(entry.toString()+"\n");
  }
   return result.toString();
  }
  
}