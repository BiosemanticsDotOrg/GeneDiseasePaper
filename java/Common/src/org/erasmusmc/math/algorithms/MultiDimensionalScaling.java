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

package org.erasmusmc.math.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.math.matrix.RowArrayMatrix;
import org.erasmusmc.math.matrix.DistanceMatrix;
import org.erasmusmc.math.matrix.PermutedLUDecomposition;
import org.erasmusmc.math.matrix.Matrix;
import org.erasmusmc.math.matrix.RowVectorMatrix;
import org.erasmusmc.math.space.ListSpace;
import org.erasmusmc.math.space.Space;
import org.erasmusmc.math.vector.ArrayVector;
import org.erasmusmc.math.vector.Vector;

/**
 * <p>Title: Thesaurus Enricher</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class MultiDimensionalScaling {
  protected RowArrayMatrix dissimilarities, weights;
  protected RowArrayMatrix BZ;
  protected PermutedLUDecomposition vPlus;
  protected RowArrayMatrix distances;
  protected List<Vector> positions;
  protected List<Vector> newPositions;
  protected RowVectorMatrix positionMatrix;
  protected RowVectorMatrix newPositionMatrix;
  protected double stressBeforeUpdate, stressAfterUpdate;
  protected int iterations, dimensions;
  protected Space positionSpace, space;

  public void initialize(Space space, Matrix dissimilarities, List<Vector> positions) throws Exception {
    initialize(space, dissimilarities, positions, null);
  }

  public void initialize(Space space, Matrix dissimilarities, List<Vector> positions, Matrix weights) throws Exception {
    this.space = space;
    dimensions = positions.size();
    positionSpace = new ListSpace(positions);
    
    distances = new RowArrayMatrix(positionSpace, positionSpace);
    distances.set(new DistanceMatrix(positions));
    
    this.dissimilarities = new RowArrayMatrix(dissimilarities);
    this.positions = positions;
    
    if (weights != null) 
      this.weights = new RowArrayMatrix(weights);
    else {
      this.weights = new RowArrayMatrix(positionSpace, positionSpace);
      this.weights.ones();
    }
      
    newPositions = new ArrayList<Vector>();
    
    for (int i = 0; i < positions.size(); i++)
      newPositions.add(new ArrayVector(space));

    positionMatrix = new RowVectorMatrix(positions, space);
    newPositionMatrix = new RowVectorMatrix(newPositions, space);
    
    normalizeWeights();

    vPlus = computeVPlus(this.weights);
    stressBeforeUpdate = Double.MAX_VALUE;
    stressAfterUpdate = stress();
    iterations = 0;
    BZ = new RowArrayMatrix(positionSpace, positionSpace);
  }

  private void normalizeWeights() {
    weights.setMainDiagonal(0);
    weights.divide(weights.maximum());
    weights.setMainDiagonal(1);
  }

  private PermutedLUDecomposition computeVPlus(RowArrayMatrix weights) throws Exception {
    RowArrayMatrix V = new RowArrayMatrix(positionSpace, positionSpace);

    for (int i = 0; i < dimensions; i++) {
      for (int j = 0; j < dimensions; j++) {
        if (i == j) {
          double sum = 0;

          for (int k = 0; k < dimensions; k++)
            if (k != i)
              sum += weights.values[i][k];

          V.values[i][j] = sum;
        }
        else
          V.values[i][j] = -weights.values[i][j];
      }
    }
    
    V.add(1);

    return new PermutedLUDecomposition(V);
  }

  protected void computeBofCoordinates() {
    double distance;

    for (int i = 0; i < dimensions; i++) {
      double[] row = BZ.values[i];
      double[] distancesRow = distances.values[i];
      double[] dissimilaritiesRow = dissimilarities.values[i];
      double[] weightsRow = weights.values[i];
      
      for (int j = 0; j < dimensions; j++)
        if (i != j) {
          distance = distancesRow[j];
                    
          if (distance != 0d)
            row[j] = -((dissimilaritiesRow[j] * weightsRow[j]) / distance);
          else
            row[j] = 0;
        }  
    }

    double sum;

    for (int i = 0; i < dimensions; i++) {
      double[] row = BZ.values[i];

      sum = 0;

      for (int j = 0; j < i; j++)
        sum += row[j];

      for (int j = i + 1; j < dimensions; j++)
        sum += row[j];

      row[i] = -sum;
    }
  }

  public void update() {
    stressBeforeUpdate = stressAfterUpdate;
    
    computeBofCoordinates();
    BZ.multiply(newPositionMatrix, positionMatrix);
    positionMatrix.set(newPositionMatrix);
    vPlus.substituteBack(positionMatrix);
    distances.set(new DistanceMatrix(positions));
    stressAfterUpdate = stress();
    
    iterations++;
  }

  protected double stress() {
    double result = 0;

    for (int i = 0; i < dimensions; i++) {
      double[] distancesRow = distances.values[i];
      double[] dissimilaritiesRow = dissimilarities.values[i];
      double[] weightsRow = weights.values[i];

      for (int j = i + 1; j < dimensions; j++) {
        double difference = dissimilaritiesRow[j] - distancesRow[j];
        result += weightsRow[j] * difference * difference;
      }
    }

    //return result;
    return Math.sqrt(result) / (double) (dimensions * dimensions);
  }

  public boolean hasConverged() {
    if (stressBeforeUpdate < stressAfterUpdate) {
      System.err.println("Stress after update higher than stress before update");
      System.err.println("  Before update: " + stressBeforeUpdate);
      System.err.println("  After update:  " + stressAfterUpdate);
      System.err.println("  Difference:    " + (stressAfterUpdate - stressBeforeUpdate));
      return false;
     }
    else
      return stressBeforeUpdate - stressAfterUpdate < 1.0e-15;
  }
  
  public double getStress() {
    return stressAfterUpdate;
  }
  
  public void dump() {
    System.out.println("Distances:");
    distances.dump();
    
    System.out.println("Coordinates:");
    for (Vector coordinate: positions)
      coordinate.dump();
    
    System.out.println("Stress: " + stressAfterUpdate);
  }
}