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

package org.erasmusmc.collections;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;

public class ComparatorFactory {

  public static Comparator<Integer> getAscendingIntegerComparator() {
    return new IntegerAscendingComparator();
  }

  public static Comparator<Integer> getDescendingIntegerComparator() {
    return new IntegerDescendingComparator();
  }

  public static Comparator<Double> getAscendingDoubleComparator() {
    return new DoubleAscendingComparator();
  }

  public static Comparator<Double> getDescendingDoubleComparator() {
    return new DoubleDescendingComparator();
  }

  public static Comparator<String> getDescendingStringComparator() {
    return new StringDescendingComparator();
  }

  public static Comparator<String> getAscendingStringComparator() {
    return new StringAscendingComparator();
  }
  public static Comparator<File> getAscendingFileComparator() {
    return new FileAscendingComparator();
  }

  public static class IntegerAscendingComparator implements Comparator<Integer>, Serializable {
    private static final long serialVersionUID = -476978425831389381L;

    public int compare(Integer arg0, Integer arg1) {
      if (arg0 > arg1)
        return 1;
      if (arg0 < arg1)
        return -1;
      return 0;
    }
  }
  
  public static class LongAscendingComparator implements Comparator<Long>, Serializable {
    private static final long serialVersionUID = -476978425831389381L;

    public int compare(Long arg0, Long arg1) {
      if (arg0 > arg1)
        return 1;
      if (arg0 < arg1)
        return -1;
      return 0;
    }
  }

  public static class DoubleAscendingComparator implements Comparator<Double>, Serializable {
    private static final long serialVersionUID = -476978425831389381L;

    public int compare(Double arg0, Double arg1) {
      if (arg0 > arg1)
        return 1;
      if (arg0 < arg1)
        return -1;
      return 0;
    }
  }

  public static class DoubleDescendingComparator implements Comparator<Double>, Serializable {
    private static final long serialVersionUID = -476978425831389381L;

    public int compare(Double arg0, Double arg1) {
      if (arg0 > arg1)
        return -1;
      if (arg0 < arg1)
        return 1;
      return 0;
    }
  }

  public static class IntegerDescendingComparator implements Comparator<Integer>, Serializable {
    private static final long serialVersionUID = -476978425831389381L;

    public int compare(Integer arg0, Integer arg1) {
      if (arg0 > arg1)
        return -1;
      if (arg0 < arg1)
        return 1;
      return 0;
    }
  }
  public static class StringDescendingComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 4502103115822498558L;

    public int compare(String arg0, String arg1) {
      return (arg1.compareTo(arg0));
    }
  }
  public static class StringAscendingComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 4502103115822498558L;

    public int compare(String arg0, String arg1) {
      return (arg0.compareTo(arg1));
    }
  }
  public static class FileAscendingComparator implements Comparator<File>, Serializable {
    private static final long serialVersionUID = 4502103115822498558L;

    public int compare(File arg0, File arg1) {
      return (arg0.getAbsolutePath().compareTo(arg1.getAbsolutePath()));
    }
  }

}
