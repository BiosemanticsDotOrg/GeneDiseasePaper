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

import static KnowledgeTransfer.PathConfigs.CONCEPT_PROFILES_DIR;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.erasmusmc.utilities.ReadCSVFile;
import org.erasmusmc.utilities.TextFileUtilities;

public class ConceptProfileUtil {

	public static HashMap<Integer,Integer> loadConceptFrequencies(String filename){
		HashMap<Integer,Integer> cid2count = new HashMap<Integer, Integer>();
		List<String> in = TextFileUtilities.loadFromFile(filename);
		for(String row:in){
			int cid = Integer.parseInt(row.split("\t")[0]);
			int count = Integer.parseInt(row.split("\t")[1]);
			cid2count.put(cid, count);
		}
		return cid2count;
	}
	
	public static Map<Integer, Double> readConceptProfile(String filename) {
		Map<Integer, Double> cp = new HashMap<Integer, Double>();
		
		ReadCSVFile input = new ReadCSVFile(filename);
		Iterator<List<String>> it = input.iterator();
		
		while(it.hasNext()) {
			List<String> fields = it.next();
                        if (fields.size() == 1) {
                            System.out.println(":-)");
                        }
			Integer concept = Integer.parseInt(fields.get(0));
			Double score = Double.parseDouble(fields.get(1));
			cp.put(concept, score);
                        //System.out.println(cp.size());
                    
		}
		return cp;
	}
	
	public static Map<Integer, Map<Integer, Double>> readConceptProfiles(String directory) {
		Map<Integer, Map<Integer, Double>> result = new HashMap<Integer, Map<Integer, Double>>();
		
		File myDir = new File(directory);
		for (File file: myDir.listFiles()) {
			result.put(Integer.parseInt(file.getName()), readConceptProfile(file.getAbsolutePath()));
		}
		
		return result;
	}
	
	public static Map<Integer, Map<Integer, Double>> readConceptProfiles(
			List<Integer> allConceptIDS) {
		Map<Integer, Map<Integer, Double>> conceptProfiles = new HashMap<Integer, Map<Integer, Double>>();

		for (Integer cid : allConceptIDS) {
			String cpFileName = CONCEPT_PROFILES_DIR + cid;
			conceptProfiles.put(cid,
					ConceptProfileUtil.readConceptProfile(cpFileName));

		}

		return conceptProfiles;
	}

	public static Map<Integer, Map<Integer, Double>> readConceptProfilesByID(List<Integer> allConceptIDS, String directory) {
		Map<Integer, Map<Integer, Double>> conceptProfiles = new HashMap<Integer, Map<Integer, Double>>();

		for (Integer cid : allConceptIDS)
		{
			String cpFileName = directory + cid;
			File file = new File(cpFileName);

			if(file.exists()) {
				conceptProfiles.put(cid, readConceptProfile(cpFileName));
			}
		}

		return conceptProfiles;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Map<Integer, Map<Integer, Double>> cp = readConceptProfiles("/tmp/profiles/");

	}

	public static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e1.getValue().compareTo(e2.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}

	public static List<Integer> readCidFile(String filename) {
		List<Integer> result = new ArrayList<Integer>();
		BufferedReader in = null;
		
		try {
			in = new BufferedReader(new FileReader(filename));
			
			String line;
			while( (line = in.readLine()) != null ) {
				result.add(Integer.parseInt(line.trim()));
			}
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}

}
