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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.WriteTextFile;

public class GetResultsFromChemicalCorpusIndexation {
	public void run(String home, String corpusFile, String falsePosPositionsFileName, String falseNegPositionsFileName, String truePosPositionsFileName, String analysisResultFileName){
		WriteTextFile outPutFile = new WriteTextFile(analysisResultFileName);

		ReadTextFile corpus = new ReadTextFile(home+corpusFile);
		Map<String,List<String>> corpusMap = getKolarikAbstractsAsMap(corpus);

		ReadTextFile truePosResultsfile = new ReadTextFile(truePosPositionsFileName);
		HashMap<String,List<String>> truePosMap = getPositionsAsMap(truePosResultsfile);
		checkPositions("true positive", corpusMap, truePosMap, outPutFile);

		ReadTextFile falsePosResultsfile = new ReadTextFile(falsePosPositionsFileName);
		HashMap<String,List<String>> falsePosMap = getPositionsAsMap(falsePosResultsfile);
		checkPositions("false positive", corpusMap, falsePosMap, outPutFile);

		ReadTextFile falseNegResultsfile = new ReadTextFile(falseNegPositionsFileName);
		HashMap<String,List<String>> falseNegMap = getPositionsAsMap(falseNegResultsfile);
		checkPositions("false negative", corpusMap, falseNegMap, outPutFile);

		outPutFile.close();
	}

	private HashMap<String,List<String>> getPositionsAsMap(ReadTextFile file){
		String pmid = "";    
		HashMap<String,List<String>> posMap = new HashMap<String,List<String>>();
		Iterator<String> Iterator = file.getIterator();
		while (Iterator.hasNext()) {
			String line = Iterator.next();      
			if (line.startsWith("###")){
				pmid = line.substring(4);
			} else if (line.length()!=0){ 
				List<String> positions = posMap.get(pmid);
				if (positions==null){
					positions = new ArrayList<String>();
				}
				positions.add(line);
				posMap.put(pmid, positions);
			}
		}
		return posMap;

	}

	private Map<String,List<String>> getKolarikAbstractsAsMap(ReadTextFile corpus){
		Map<String,List<String>> abstracts = new HashMap<String,List<String>>();
		String corpusPmid = "";
		Iterator<String> fileIterator = corpus.getIterator();
		while (fileIterator.hasNext()) {
			String line = fileIterator.next();
			if (line.startsWith("###")){
				corpusPmid = line.substring(4);
			}
			else if (line.length()!=0){
				List<String> abs = abstracts.get(corpusPmid);
				if (abs==null){
					abs = new ArrayList<String>();
				}
				abs.add(line);
				abstracts.put(corpusPmid, abs);
			}
		}
		return abstracts;
	}

	private void checkPositions(String type, Map<String, List<String>> corpusMap, Map<String,List<String>> positionsMap, WriteTextFile outPutFile){
		int no = 0;
		String pmid = "";
		Iterator it = corpusMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry)it.next();
			pmid = pairs.getKey().toString();
			if (positionsMap.containsKey(pmid)){
				boolean inEntity = false;
				String tokenClass = "";
				String entity = "";
				String prevEnd = "";
				List<String> corpusLines = (List<String>) pairs.getValue();
				List<String> positions = positionsMap.get(pmid);
				for (String pair: positions){
					String[] startAndEnd = pair.split("\t");
					Integer startPos = Integer.parseInt(startAndEnd[0]);
					Integer endPos = Integer.parseInt(startAndEnd[1]);          
					for (String line: corpusLines){            
						String[] columns = line.split("\t");
						Integer start = Integer.parseInt(columns[1]);
						Integer end = Integer.parseInt(columns[2]);
						if (inEntity){
							if (endPos.equals(end)){
								if (start.equals(prevEnd))
									entity = entity+columns[0];
								else entity = entity+" "+columns[0];
								no++;
								outPutFile.writeln(type+"\t"+entity+"\t"+startPos+"\t"+endPos+"\t"+tokenClass+"\t"+pmid);
								inEntity = false;
							} else {
								if (start.equals(prevEnd))
									entity = entity+columns[0];
								else entity = entity+" "+columns[0];
							}
						}
						else if (startPos.equals(start)){
							inEntity = true;
							entity = columns[0];
							tokenClass = columns[4];
							if (endPos.equals(end)){
								no++;
								outPutFile.writeln(type+"\t"+entity+"\t"+startPos+"\t"+endPos+"\t"+tokenClass+"\t"+pmid);
								inEntity = false;
							}
						}
						prevEnd = end.toString();
					}          
				}
			} 
		}
		System.out.println("Number of "+type+" entities: "+no);    
	}  
}
