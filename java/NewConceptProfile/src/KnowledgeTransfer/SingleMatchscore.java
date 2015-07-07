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

package KnowledgeTransfer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.utilities.TextFileUtilities;
import com.google.common.collect.Sets;

public class SingleMatchscore {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		HashMap<Integer,Double> cp1 = loadConceptProfile("/home/hvanhaagen/conceptprofile_HIP1.txt");
		HashMap<Integer,Double> cp2 = loadConceptProfile("/home/hvanhaagen/conceptprofile_huntingtondisease.txt");

		Double ip = InnerProduct(cp1, cp2);
		
		System.out.println("matchscore is: "+ip);
	}

	public static double InnerProduct(Map<Integer, Double> cp1, Map<Integer, Double> cp2){
		if(cp1==null || cp2==null)
			return Double.NaN;
		double ip = 0.0;
		 Collection<Integer> sharedkeys = Sets.intersection(cp1.keySet(),cp2.keySet());
		 for(Object key:sharedkeys){
			 Integer concept = (Integer)key;
			 double value1 = cp1.get(concept);
			 double value2 = cp2.get(concept);
			
				 ip += value1*value2;
			
		 }
		 return ip;
	}
	
	public static HashMap<Integer,Double> loadConceptProfile(String filename){
		HashMap<Integer,Double> cp = new HashMap<Integer, Double>();
		List<String> in = TextFileUtilities.loadFromFile(filename);
		for(String row:in){
			int conceptid = Integer.parseInt(row.split("\t")[1]);
			double score = Double.parseDouble(row.split("\t")[2]);
			cp.put(conceptid, score);
		}
		return cp;
	}
}
