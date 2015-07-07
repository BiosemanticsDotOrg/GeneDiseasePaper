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

package org.erasmusmc.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.math.GammaDistribution;
import org.erasmusmc.math.vector.SparseVectorInt2Float;

public class RandomUtilities {
  private static Random random = new Random();

  public static SparseVectorInt2Float getGammaDistributionVector(Integer size,Double scale, Double shape){
    GammaDistribution gammaDistribution = new GammaDistribution(scale,shape);
    SparseVectorInt2Float result = new SparseVectorInt2Float();
    for (int i = 1; i <= size; i++) {
      Double value = gammaDistribution.nextDouble();
      result.values.addEntry(i, value.floatValue());
    }
    return result;
  }
  public static SparseVectorInt2Float getGammaDistributionVector(Set<Integer> entries, Double scale, Double shape) {
    GammaDistribution gammaDistribution = new GammaDistribution(scale,shape);
    SparseVectorInt2Float result = new SparseVectorInt2Float();
    Iterator<Integer> it = entries.iterator();
    while (it.hasNext()) {
      Double value = gammaDistribution.nextDouble();
      result.set(it.next(), value);
    }
    return result;
  }

  public static SparseVectorInt2Float getNormalDistributionVector(Integer size, Double standardDeviation, Double mean) {
    SparseVectorInt2Float result = new SparseVectorInt2Float();
    for (int i = 1; i <= size; i++) {
      Double value = random.nextGaussian() * standardDeviation + mean;
      result.values.addEntry(i, value.floatValue());
    }
    return result;

  }

  public static SparseVectorInt2Float getNormalDistributionVector(Set<Integer> entries, Double standardDeviation, Double mean) {
    SparseVectorInt2Float result = new SparseVectorInt2Float();
    Iterator<Integer> it = entries.iterator();
    while (it.hasNext()) {
      double value = random.nextGaussian() * standardDeviation + mean;
      result.set(it.next(), value);
    }
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<?> randomSelector(Collection<?> input, Integer numberOfSelected) {
    // zonder teruglegging!

    List result = new ArrayList();

    try {

      if (numberOfSelected >= input.size()) {

        throw new Exception("Illegal number of Selected requested in randomIdSelector");
      }
      else {

        Object[] objects = input.toArray();
        Set<Integer> temp = new TreeSet<Integer>();
        while (temp.size() < numberOfSelected) {
          temp.add(random.nextInt(input.size()));
        }
        for (Integer id: temp) {
          result.add(objects[id]);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }
}
