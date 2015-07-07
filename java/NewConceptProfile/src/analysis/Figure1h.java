/*
 * Concept profile generation and analysis for Gene-Disease paper
 * Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
 *  Leiden, The Netherlands
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

package analysis;

import static KnowledgeTransfer.PathConfigs.MATCH_SCORE_FILENAME;
import static KnowledgeTransfer.PathConfigs.RESULTS_BASE_DIR;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.utilities.ReadCSVFile;

import com.google.common.collect.Sets;

public class Figure1h {

	private static final String OUTPUT = RESULTS_BASE_DIR + "Figure1h.txt";
	private static final int BUCKET_DECIMAL_PLACE = 1;
	//private static final int NUM_EXPECTED_MATCHSCORES = 1899; // on inputs with 10 genes, diseases
	private static final int NUM_EXPECTED_MATCHSCORES = 204072353; // on full input creating ~400M pairs: also 23 negative infinite
	private static int lineCount = 0;

	public static void main(String[] args) {
		ReadCSVFile input = new ReadCSVFile(MATCH_SCORE_FILENAME + "-coOcc.txt");
		Iterator<List<String>> it = input.iterator();

		Map<Double, Integer> explicitHist = new HashMap<Double, Integer>();
		Map<Double, Integer> implicitHist = new HashMap<Double, Integer>();

		Integer implicitCnt = 0;
		Integer explicitCnt = 0;
		
		/*
		 * Collect histogram data
		 */
		while(it.hasNext()) {
			List<String> fields = it.next();
			Integer conceptA = Integer.parseInt(fields.get(0));
			Integer conceptB = Integer.parseInt(fields.get(1));
			Double matchScore = Double.parseDouble(fields.get(2));
			String coOccurrences = fields.get(3);
			if( lineCount % 1000000 == 0 ) {
				System.out.println("Processed: " + lineCount);
			}
			lineCount++;
			
			if(!matchScore.isNaN()) { // null if one concept has no profile
				if(coOccurrences.equals("[]")) { // implicit link
					implicitCnt += addMatchScoreToHistogram(implicitHist, matchScore);
				} else { // explicit link
					explicitCnt += addMatchScoreToHistogram(explicitHist, matchScore);
				}
			}
		}
		
		/*
		 * Print histograms
		 */
		//printHistogram(implicitHist);
		//printHistogram(explicitHist);
	    relativeHistogram(explicitHist, implicitHist);
		System.out.println("Number of concept pairs with match score: " + (implicitCnt + explicitCnt));
	    System.out.println("Number of expected concept pairs used for calculating percentile (first column): " + NUM_EXPECTED_MATCHSCORES);
	    System.out.println("If the former number does not match the latter, then percentile score is incorrect (use different constant for NUM_EXPECTED_MATCHSCORES or check for unexpected infinite valued match scores).");
	}
	
	public static void printHistogram(Map<Double, Integer> histogram) {
		Double max = Collections.max(histogram.keySet());
		Double min = Collections.min(histogram.keySet());
		Integer foundInBuckets = 0;
		
		for(Double i=max; i>=min; i-=1.0/Math.pow(10,BUCKET_DECIMAL_PLACE)) {
			Double bucket = (new BigDecimal(i)).setScale(BUCKET_DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP).doubleValue();
			Integer cnt = null;
			if(histogram.containsKey(bucket)) {
				cnt = histogram.get(bucket);
			} else {
				cnt = 0;
			}
			foundInBuckets += cnt;
			System.out.println(bucket + "\t" + cnt + "\t" + foundInBuckets);
		}
	}
	
	/*
	 * print the relative, normalized fraction of explicit to implicit associations
	 */
	public static Map<Double, Integer> relativeHistogram(Map<Double, Integer> explicitHist, Map<Double, Integer> implicitHist) {
		Map<Double, Integer> result = new HashMap<Double, Integer>();
		
		Set<Double> allIndices = Sets.union(explicitHist.keySet(), implicitHist.keySet());
		Double max = Collections.max(allIndices);
		Double min = Collections.min(allIndices);
		
		Integer explCumul = 0;
		Integer implCumul = 0;
		
		System.out.println("perc\tscore\t#impl\t#expl\t#implC\t#explC\t%impl");

		for(Double i=max; i>=min; i-=1.0/Math.pow(10,BUCKET_DECIMAL_PLACE)) {
			Double bucket = (new BigDecimal(i)).setScale(BUCKET_DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP).doubleValue();
			Integer explCnt = 0;
			Integer implCnt = 0;
			if(explicitHist.containsKey(bucket))
				explCnt = explicitHist.get(bucket);
			if(implicitHist.containsKey(bucket))
				implCnt = implicitHist.get(bucket);

			implCumul += implCnt;
			explCumul += explCnt;
			
			//TODO: add %expl
			Double percentile = (1 - (((double) implCumul+explCumul) / NUM_EXPECTED_MATCHSCORES)) * 100;
			System.out.printf("%.3f\t" + bucket + "\t" + implCnt + "\t" + explCnt + "\t" +
			                 implCumul + "\t" + explCumul + "\t", percentile);
			if(explCumul+implCumul > 0) {
				System.out.printf("%.3f\n", (double)implCumul/(explCumul+implCumul));
			} else {
				System.out.println("-");
			}
		}
		
		return result;
	}
	
	public static Integer addMatchScoreToHistogram(Map<Double, Integer> histogram, Double matchScore) {
		assert matchScore <= 1  && matchScore >= 0 ;
		Double bucket = (double) Math.log10(matchScore);
		assert bucket <= 0;
		//TODO: count number of infinite values, since this influences NUM_EXPECTED_MATCHSCORES
		if( bucket.isNaN() || bucket.isInfinite() ) {
			System.out.println("NaN or inifinite matchscore in input line " + lineCount + " (skipping..)");
			return 0;
		}
		BigDecimal bd = (new BigDecimal(bucket)).setScale(BUCKET_DECIMAL_PLACE, BigDecimal.ROUND_HALF_UP);
		bucket = bd.doubleValue();

		if(!histogram.containsKey(bucket)) {
			histogram.put(bucket, 1);
		} else {
			histogram.put(bucket, histogram.get(bucket)+1);
		}
		
		return 1;
	}
}
