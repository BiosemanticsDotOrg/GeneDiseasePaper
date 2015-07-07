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

import java.util.Iterator;

import org.erasmusmc.utilities.ReadTextFile;

public class AnalysisStratification {
	public void run(String inFileName) {
		Integer tpIupac = 0;
		Integer tpPart = 0;
		Integer tpSum = 0;
		Integer tpTriv = 0;
		Integer tpAbb = 0;
		Integer tpFam = 0;

		Integer fnIupac = 0;
		Integer fnPart = 0;
		Integer fnSum = 0;
		Integer fnTriv = 0;
		Integer fnAbb = 0;
		Integer fnFam = 0;

		ReadTextFile inFile = new ReadTextFile(inFileName);
		Iterator<String> fileIterator = inFile.iterator();    
		while (fileIterator.hasNext()){
			String line = fileIterator.next();
			String[] columns = line.split("\t");
			if (columns[0].equals("true positive")){
				if (columns[4].equals("|B-IUPAC")) tpIupac++; 
				else if (columns[4].equals("|B-PARTIUPAC")) tpPart++; 
				else if (columns[4].equals("|B-SUM")) tpSum++; 
				else if (columns[4].equals("|B-TRIVIAL") || columns[2].equals("|B-TRIVIALVAR")) tpTriv++; 
				else if (columns[4].equals("|B-ABBREVIATION")) tpAbb++; 
				else if (columns[4].equals("|B-FAMILY")) tpFam++; 
			}else if (columns[0].equals("false negative")){
				if (columns[4].equals("|B-IUPAC")) fnIupac++; 
				else if (columns[4].equals("|B-PARTIUPAC")) fnPart++; 
				else if (columns[4].equals("|B-SUM")) fnSum++; 
				else if (columns[4].equals("|B-TRIVIAL") || columns[2].equals("|B-TRIVIALVAR")) fnTriv++; 
				else if (columns[4].equals("|B-ABBREVIATION")) fnAbb++; 
				else if (columns[4].equals("|B-FAMILY")) fnFam++; 
			}
		}
		Integer temp = tpIupac+fnIupac;
		Double recallIupac = tpIupac.doubleValue()/temp.doubleValue();
		System.out.println("Recall IUPAC: "+"\t"+recallIupac);
		Integer temp2 = tpPart+fnPart;
		Double recallPart = tpPart.doubleValue()/temp2.doubleValue();
		System.out.println("Recall PART: "+"\t"+recallPart);
		Integer temp3 = tpSum+fnSum;
		Double recallSum = tpSum.doubleValue()/temp3.doubleValue();
		System.out.println("Recall SUM: "+"\t"+recallSum);
		Integer temp4 = tpTriv+fnTriv;
		Double recallTriv = tpTriv.doubleValue()/temp4.doubleValue();
		System.out.println("Recall TRIV: "+"\t"+recallTriv);
		Integer temp5 = tpAbb+fnAbb;
		Double recallAbb = tpAbb.doubleValue()/temp5.doubleValue();
		System.out.println("Recall ABB: "+"\t"+recallAbb);
		Integer temp6 = tpFam+fnFam;
		Double recallFam = tpFam.doubleValue()/temp6.doubleValue();
		System.out.println("Recall FAM: "+"\t"+recallFam);
	}
}
