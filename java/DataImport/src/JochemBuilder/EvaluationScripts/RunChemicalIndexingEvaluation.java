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

package JochemBuilder.EvaluationScripts;

public class RunChemicalIndexingEvaluation {
	public static String home = "/home/khettne/Projects/Jochem/Indexing/";

	public static String falsePositivePositionsInFileName = home+"falsePosPositions.txt";
	public static String falseNegativePositionsInFileName = home+"falseNegPositions.txt";
	public static String truePositivePositionsInFileName = home+"truePosPositions.txt";
	public static String analysisResultOutFileName = home+"analysis_results.txt";    
	public static String corpusFile = "chemicals-test-corpus-01-05-2008.iob";

	public static void main(String[] args) {
		GetResultsFromChemicalCorpusIndexation analysis = new GetResultsFromChemicalCorpusIndexation();
		analysis.run(home, corpusFile, falsePositivePositionsInFileName, falseNegativePositionsInFileName, truePositivePositionsInFileName, analysisResultOutFileName);

		AnalysisStratification stratify = new AnalysisStratification();
		stratify.run(analysisResultOutFileName);
	}
}
