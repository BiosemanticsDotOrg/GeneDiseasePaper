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


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class ClassifierEvaluator {
	public static int width = 500;
	public static int height = 500;
	public static int borderWidth = 50;
	public static int ticSize = 5;
	public static boolean includeAuCinROC = true;
	public static boolean outputROCcsv = true;


	public static void main(String[] args){
		ClassifierOutput output = new ClassifierOutput();
		/*output.scoreLabelPairs.add(new ScoreLabelPair(5, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(0, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(9, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(8, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(5, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(0, false));
		output.scoreLabelPairs.add(new ScoreLabelPair(0, false));
		output.scoreLabelPairs.add(new ScoreLabelPair(0, false));
		output.scoreLabelPairs.add(new ScoreLabelPair(5, false));
		 */
		output.scoreLabelPairs.add(new ScoreLabelPair(5, false));
		output.scoreLabelPairs.add(new ScoreLabelPair(4, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(3, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(2, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(1, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(ScoreLabelPair.LOW_NUMBER, false));
		output.scoreLabelPairs.add(new ScoreLabelPair(ScoreLabelPair.LOW_NUMBER, false));
		output.scoreLabelPairs.add(new ScoreLabelPair(ScoreLabelPair.LOW_NUMBER, false));
		output.scoreLabelPairs.add(new ScoreLabelPair(ScoreLabelPair.LOW_NUMBER, true));
		output.scoreLabelPairs.add(new ScoreLabelPair(ScoreLabelPair.LOW_NUMBER, true));

		System.out.println(ClassifierEvaluator.areaUnderCurve(output));
		System.out.println(ClassifierEvaluator.areaUnderCurveConfidenceInterval(output).pointEstimate);
		System.out.println(ClassifierEvaluator.areaUnderCurveConfidenceInterval(output).lowerBound);
		System.out.println(ClassifierEvaluator.areaUnderCurveConfidenceInterval(output).upperBound);
		System.out.println(ClassifierEvaluator.bpref(output));
		
	}

	public static void createROC(ClassifierOutput classifierOutput, String filename){
		WriteTextFile out = null;
		if (outputROCcsv){
			out = new WriteTextFile(filename.replace(".gif", ".csv"));
			out.writeln("FPR,TPR");
		}
		BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)img.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		renderAxes(g2d, 0.2f,1f);
		//renderDiagonal(g2d);
		renderROC(g2d,classifierOutput,out);

		if (includeAuCinROC){
			double auc = ClassifierEvaluator.areaUnderCurveConfidenceInterval(classifierOutput).pointEstimate;
			g2d.setColor(Color.black);
			g2d.setFont(new Font("Arial",Font.PLAIN,50));
			FontMetrics metrics = g2d.getFontMetrics();
			String text = "AUC = " + StringUtilities.formatNumber("0.00", auc);
			int textWidth = metrics.stringWidth(text);
			int textHeight = metrics.getHeight();
			g2d.drawString(text, Math.round(width-textWidth-borderWidth), Math.round(height-textHeight)+7);
		}
		try {
			ImageIO.write(img, "GIF", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (out != null)
			out.close();
	}

	public static void createFPCurve(ClassifierOutput classifierOutput, String filename, int count){
		BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D)img.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, width, height);

		renderAxes(g2d, 10,count);
		renderDiagonal(g2d);
		renderFPCurve(g2d,classifierOutput,count);
		try {
			ImageIO.write(img, "GIF", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void renderDiagonal(Graphics2D g2d) {
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.draw(new Line2D.Float(borderWidth, height-borderWidth, width-borderWidth, borderWidth));

	}

	public static double averagePrecision(ClassifierOutput classifierOutput){
		Collections.sort(classifierOutput.scoreLabelPairs);
		int tpCount = 0;
		double score = -1;
		double sumP = 0;
		int startInterval = 1;
		int tpCountInterval = 0;
		for (int i = 1; i <= classifierOutput.scoreLabelPairs.size(); i++){
			ScoreLabelPair pair = classifierOutput.scoreLabelPairs.get(i-1);

			if (pair.score != score) {// In case of a tie, just make a linear interpolation
				if (tpCountInterval != 0){
					int intervalSize = (i-startInterval);
					double step = (intervalSize+1) / (double)(tpCountInterval + 1);
					for (int j = 1; j <= tpCountInterval; j++){
						double index = startInterval-1 + j*step;
						double precision = (tpCount + j) / index;
						sumP += precision;
					}
					tpCount += tpCountInterval;
				}
				score = pair.score;
				startInterval = i;

				tpCountInterval = 0;
			} 
			if (pair.label){
				tpCountInterval++;
			}
		}
		int i = classifierOutput.scoreLabelPairs.size()+1;
		if (tpCountInterval != 0){
			int intervalSize = (i-startInterval);
			double step = (intervalSize+1) / (double)(tpCountInterval + 1);
			for (int j = 1; j <= tpCountInterval; j++){
				double index = startInterval-1 + j*step;
				double precision = (tpCount + j) / index;
				sumP += precision;
			}
			tpCount += tpCountInterval;
		}
		return sumP/(double)tpCount;
	}

	/**
	 * Calculates the MAP according to TREC. Assumes a score of 0 means not ranked by the system!
	 * @param classifierOutput
	 * @return
	 */
	public static double trecMAP(ClassifierOutput classifierOutput){
		Collections.sort(classifierOutput.scoreLabelPairs);
		int tpCount = 0;
		int pCount = 0;
		double sumP = 0;
		for (int i = 1; i <= classifierOutput.scoreLabelPairs.size(); i++){
			ScoreLabelPair pair = classifierOutput.scoreLabelPairs.get(i-1);
			if (pair.label){
				pCount++;
				if (pair.score != Integer.MIN_VALUE){
					tpCount++;
					sumP += tpCount/(double)i;
				}
			}
		}
		return sumP/(double)pCount;
	}


	public static double omopMAP(ClassifierOutput classifierOutput){
		Collections.sort(classifierOutput.scoreLabelPairs);
		/*Collections.sort(classifierOutput.scoreLabelPairs, new Comparator<ScoreLabelPair>(){
			@Override
			public int compare(ScoreLabelPair arg0, ScoreLabelPair arg1) {
				int result = Double.compare(arg1.score, arg0.score);
				if (result == 0)
					if (arg0.label == arg1.label)
						return 0;
					else if (arg0.label && !arg1.label)
						return 1;
					else
						return -1;
				return result;
			}});
		 */
		int tpCount = 0;
		int tpTieCount = 0;
		double score = -1;
		double sumP = 0;
		for (int i = 1; i <= classifierOutput.scoreLabelPairs.size(); i++){
			ScoreLabelPair pair = classifierOutput.scoreLabelPairs.get(i-1);
			if (pair.score != score){
				if (tpTieCount != 0){
					tpCount += tpTieCount;
					double precision = tpCount / (double)(i-1);
					sumP += tpTieCount * precision;
					tpTieCount = 0;
				}
			} 
			if (pair.label)
				tpTieCount++;
			score = pair.score;
		}
		if (tpTieCount != 0){
			tpCount += tpTieCount;
			double precision = tpCount / (double)classifierOutput.scoreLabelPairs.size();
			sumP += tpTieCount * precision;
			tpTieCount = 0;
		}

		return sumP/(double)tpCount;
	}

	public static double p10(ClassifierOutput classifierOutput){
		if (classifierOutput.scoreLabelPairs.size() < 10)
			return Double.NaN;
		Collections.sort(classifierOutput.scoreLabelPairs);
		int pos = 0;
		for (int i = 0; i < 10; i++)
			if (classifierOutput.scoreLabelPairs.get(i).label)
				pos++;
		return pos / 10d;
	}

	public static double bpref(ClassifierOutput classifierOutput){
		Collections.sort(classifierOutput.scoreLabelPairs);
		int R = 0;
		int N = 0;
		
		for (ScoreLabelPair pair : classifierOutput.scoreLabelPairs)
			if (pair.label)
				R++;
			else
				N++;
		int minNR = Math.min(R,N);
		double bpref = 0;
		int neg = 0;
		for (int i = 0; i < classifierOutput.scoreLabelPairs.size(); i++){
			ScoreLabelPair pair = classifierOutput.scoreLabelPairs.get(i);
			if (pair.label){
				if (pair.score != ScoreLabelPair.LOW_NUMBER){
					bpref += (N-neg)/(double)minNR;
				}
			} else
				neg++;
		}
		return bpref/R;
	}


	public static double areaUnderCurve(ClassifierOutput classifierOutput){
		if (classifierOutput.scoreLabelPairs.size() == 0)
			return Double.NaN;
		Collections.sort(classifierOutput.scoreLabelPairs);
		int postiveCount = countPositives(classifierOutput);
		int tpCount = 0;
		int fpCount = 0;
		float fpr = 0;
		float tpr = 0;
		double score = classifierOutput.scoreLabelPairs.get(0).score;
		double auc = 0;
		for (int i = 0; i < classifierOutput.scoreLabelPairs.size(); i++){
			ScoreLabelPair pair = classifierOutput.scoreLabelPairs.get(i);
			if (pair.label)
				tpCount++;
			else
				fpCount++;
			float newFpr = (float)fpCount / (float)(classifierOutput.scoreLabelPairs.size()-postiveCount);
			float newTpr = (float)tpCount / (float)postiveCount;

			if (pair.score != score || i == classifierOutput.scoreLabelPairs.size()-1) {// In case of a tie, just make a linear interpolation
				auc += (newFpr-fpr)*tpr + 0.5*((newFpr-fpr)*(newTpr-tpr));
				tpr = newTpr;
				fpr = newFpr;
				score = pair.score;
			}
		}
		return auc;
	}
	
	public static double omopAUC(ClassifierOutput classifierOutput){
		if (classifierOutput.scoreLabelPairs.size() == 0)
			return Double.NaN;
		Collections.sort(classifierOutput.scoreLabelPairs);
		int postiveCount = countPositives(classifierOutput);
		int tpCount = 0;
		int fpCount = 0;
		float fpr = 0;
		float tpr = 0;
		double score = classifierOutput.scoreLabelPairs.get(0).score;
		double auc = 0;
		for (int i = 0; i < classifierOutput.scoreLabelPairs.size(); i++){
			ScoreLabelPair pair = classifierOutput.scoreLabelPairs.get(i);
			if (pair.label)
				tpCount++;
			else
				fpCount++;
			float newFpr = (float)fpCount / (float)(classifierOutput.scoreLabelPairs.size()-postiveCount);
			float newTpr = (float)tpCount / (float)postiveCount;

			if (pair.score != score || i == classifierOutput.scoreLabelPairs.size()-1) {// In case of a tie, take worst case scenario:
				auc += (newFpr-fpr)*tpr ;
				tpr = newTpr;
				fpr = newFpr;
				score = pair.score;
			}
		}
		return auc;
	}


	/**
	 * Calculates AuC with CI using DeLong method (DeLong et al., 1988). Based on R pROC package.
	 * @param classifierOutput
	 * @return
	 */
	public static ConfidenceInterval areaUnderCurveConfidenceInterval(ClassifierOutput classifierOutput){
		List<Double> cases = new ArrayList<Double>();
		List<Double> controls = new ArrayList<Double>();
		for (ScoreLabelPair pair : classifierOutput.scoreLabelPairs)
			if (pair.label)
				cases.add(pair.score);
			else
				controls.add(pair.score);
		int m = cases.size();
		int n = controls.size();
		int mn = m*n;
		double[][] mwMatrix = new double[m][n];
		double mean = 0;
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++){
				double mw = mannWhitneyKernel(cases.get(i), controls.get(j));
				mwMatrix[i][j] = mw;
				mean += mw;
			}
		mean /= (double)mn;
		double vr10[] = new double[m];
		for (int i = 0; i < m; i++){
			double sum = 0;
			for (int j = 0; j < n; j++)
				sum += mwMatrix[i][j];
			vr10[i] = sum/(double)n;
		}

		double vr01[] = new double[n];
		for (int i = 0; i < n; i++){
			double sum = 0;
			for (int j = 0; j < m; j++)
				sum += mwMatrix[j][i];
			vr01[i] = sum/(double)m;
		}  	

		double s10 = 0;
		for (double vr : vr10)
			s10 += (vr-mean)*(vr-mean);
		s10 /= (double)(m-1);

		double s01 = 0;
		for (double vr : vr01)
			s01 += (vr-mean)*(vr-mean);
		s01 /= (double)(n-1);

		double s = s10/(double)m + s01/(double)n;
		double sd = Math.sqrt(s);
		ConfidenceInterval ci = new ConfidenceInterval();
		ci.pointEstimate = mean;
		ci.lowerBound = mean - (1.96*sd);
		ci.upperBound = mean + (1.96*sd);
		return ci;
	}

	private static double mannWhitneyKernel(double x, double y){
		if (y < x) 
			return 1;
		if (y == x)
			return 0.5;
		return -0;
	}

	private static void renderROC(Graphics2D g2d,	ClassifierOutput classifierOutput,WriteTextFile out) {
		Collections.sort(classifierOutput.scoreLabelPairs);
		int postiveCount = countPositives(classifierOutput);
		g2d.setColor(new Color(200,0,0));
		g2d.setStroke(new BasicStroke(4));
		float areaWidth = width - 2*borderWidth;
		float areaHeight = height - 2*borderWidth;
		float x = borderWidth;
		float y = height-borderWidth;
		float previousX = x;
		float previousY = y;
		double score = classifierOutput.scoreLabelPairs.get(0).score;
		int fpCount = 0;
		int tpCount = 0;
		for (ScoreLabelPair pair : classifierOutput.scoreLabelPairs){
			if (pair.score != score) {// In case of a tie, just make a linear interpolation
				g2d.draw(new Line2D.Float(previousX,previousY,x,y));
				previousX = x;
				previousY = y;
				score = pair.score;
			}

			if (pair.label)
				tpCount++;
			else
				fpCount++;
			float fpr = (float)fpCount / (float)(classifierOutput.scoreLabelPairs.size()-postiveCount);
			float tpr = (float)tpCount / (float)postiveCount;
			if (out != null)
				out.writeln(Float.toString(fpr)+"," +Float.toString(tpr));
			x = borderWidth + fpr * areaWidth;
			y = height-borderWidth - tpr * areaHeight;
		}
		g2d.draw(new Line2D.Float(previousX,previousY,x,y));
		if (out != null)
			out.writeln("1,1");

	}

	private static void renderFPCurve(Graphics2D g2d,	ClassifierOutput classifierOutput, int count) {
		Collections.sort(classifierOutput.scoreLabelPairs);
		g2d.setColor(Color.RED);
		float areaWidth = width - 2*borderWidth;
		float areaHeight = height - 2*borderWidth;
		float x = borderWidth;
		float y = height-borderWidth;
		int fpCount = 0;
		for (int i = 0; i < count; i++){
			if (!classifierOutput.scoreLabelPairs.get(i).label)
				fpCount++;
			float fracX = (i+1)/(float)count;
			float fracY = fpCount/(float)count;
			float newX = borderWidth + fracX * areaWidth;
			float newY = height-borderWidth - fracY * areaHeight;
			g2d.draw(new Line2D.Float(x,y,newX,newY));
			x = newX;
			y = newY;
		}

	}

	private static int countPositives(ClassifierOutput classifierOutput) {
		int postiveCount = 0;
		for (ScoreLabelPair pair : classifierOutput.scoreLabelPairs)
			if (pair.label)
				postiveCount++;
		return postiveCount;
	}

	private static void renderAxes(Graphics2D g2d, float tic, float max) {
		/*
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(3));
		g2d.setFont(new Font("Arial",Font.PLAIN,20));
		int areaWidth = width - 2*borderWidth;
		int areaHeight = height - 2*borderWidth;
		g2d.drawLine(borderWidth, borderWidth, borderWidth, height-borderWidth);
		g2d.drawLine(borderWidth, height-borderWidth, width-borderWidth, height-borderWidth);
		int fontHeight = g2d.getFontMetrics().getHeight();
		int fontWidth = g2d.getFontMetrics().stringWidth("0.0");
		for (float f = 0; f <= max; f+= tic){
			int ticY = Math.round((height-borderWidth) - (f/max) * (areaHeight));
			g2d.drawString(StringUtilities.formatNumber("0.0", f), borderWidth-ticSize-fontWidth, fontHeight / 2 + ticY);
			g2d.drawLine(borderWidth - ticSize, ticY, borderWidth, ticY);

			int ticX = Math.round(borderWidth + (f/max) * (areaWidth));
			g2d.drawString(StringUtilities.formatNumber("0.0", f), ticX - fontWidth/2,height-borderWidth+ticSize+fontHeight);
			g2d.drawLine(ticX, height-borderWidth, ticX, height-borderWidth+ticSize);
		}
		*/
		g2d.setStroke(new BasicStroke(1));
		g2d.setFont(new Font("Arial",Font.PLAIN,30));
		int areaWidth = width - 2*borderWidth;
		int areaHeight = height - 2*borderWidth;
		g2d.setColor(new Color(238, 238, 238));
		g2d.fillRect(borderWidth, borderWidth, areaWidth, areaHeight);
		int fontHeight = g2d.getFontMetrics().getHeight();
		int fontWidth = g2d.getFontMetrics().stringWidth("0.0");
		
		for (float f = 0; f <= max; f+= tic){
			int ticY = Math.round((height-borderWidth) - (f/max) * (areaHeight));
			g2d.setColor(Color.BLACK);
			g2d.drawString(StringUtilities.formatNumber("0.0", f), borderWidth-ticSize-fontWidth-3, ticY+(fontHeight/2)-5);
			g2d.setColor(new Color(170, 170, 170));
			if (f != 0 && f != max)
			  g2d.drawLine(borderWidth, ticY, width-borderWidth, ticY);

			int ticX = Math.round(borderWidth + (f/max) * (areaWidth));
			g2d.setColor(Color.BLACK);
			g2d.drawString(StringUtilities.formatNumber("0.0", f), ticX - fontWidth/2,height-borderWidth+ticSize+fontHeight);
			g2d.setColor(new Color(170, 170, 170));
			if (f != 0 && f != max)
			  g2d.drawLine(ticX, borderWidth, ticX, height-borderWidth);
		}		
	}

	public static class ConfidenceInterval {
		public double lowerBound;
		public double upperBound;
		public double pointEstimate;
	}

	public static String printStats(ClassifierOutput output) {
		int pos = 0;
		int neg = 0;
		for (ScoreLabelPair pair : output.scoreLabelPairs)
			if (pair.label)
				pos++;
			else
				neg++;
		
			
		return "Postives: " + pos + ", negatives: " + neg;
	}
}
