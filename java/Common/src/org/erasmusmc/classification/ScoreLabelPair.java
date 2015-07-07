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

package org.erasmusmc.classification;


public class ScoreLabelPair implements Comparable<ScoreLabelPair>{
	public static double LOW_NUMBER = -9999999d;
	/**
	 * Classifier score. Higher means more likely to be positive (according to the classifier)
	 */
  public double score;
  
  /**
   * true is positive (according to gold standard)
   */
  public boolean label;
  
  public ScoreLabelPair(double score, boolean label){
  	this.score = score;
  	this.label = label;
  }
	@Override
	public int compareTo(ScoreLabelPair arg0) {
		return Double.compare(arg0.score, score);
	}
}
