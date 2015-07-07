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

package org.erasmusmc.dataimport.UMLS;

import java.util.List;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class ConvertFrequencyCountsFileToCurationFileFormat {
	public static void main(String[] args) {
		WriteTextFile out = new WriteTextFile("/home/khettne/Projects/umls2010ABTermsToRemove_curationFileFormat.txt");	
		for (String line: new ReadTextFile("/home/khettne/Projects/umls2010ABTermsToRemove_JK_KH.txt")){
			if (!line.startsWith("#")) { // check if it is not a comment line!
				List<String> cells = StringUtilities.safeSplit(line, '\t');
				String term = cells.get(1);
				List<String> cuis = StringUtilities.safeSplit(cells.get(2), ';');
				for (String cui: cuis){
					if (!cui.isEmpty()){
						Integer cuiInt = Integer.parseInt(cui);
						String formattedCui = StringUtilities.formatNumber("C0000000", cuiInt);
						StringBuffer stringbuffer = new StringBuffer();
						stringbuffer.append("SUPPRESS");
						stringbuffer.append("|");
						stringbuffer.append("DBLINK");
						stringbuffer.append("|");
						stringbuffer.append("UMLS");
						stringbuffer.append("|");
						stringbuffer.append(formattedCui);
						stringbuffer.append("|");
						stringbuffer.append(term);
						String outputString = stringbuffer.toString();
						out.writeln(outputString);
					}
				}			
			}
		}
		out.close();
	}
}