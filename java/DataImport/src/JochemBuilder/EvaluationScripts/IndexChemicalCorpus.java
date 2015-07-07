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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ReleasedTerm;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.UMLSGeneChemTokenizer;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails;
import org.erasmusmc.peregrine.disambiguator.DisambiguatorRuleRegistry;
import org.erasmusmc.peregrine.disambiguator.GeneDisambiguator;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails.EvaluationResult.ExtraData;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class IndexChemicalCorpus {
	public boolean disambiguate = true;
	private GeneDisambiguator geneDisambiguator;
	public static String corpusFile = "/home/khettne/Projects/Jochem/Indexing/chemicals-test-corpus-01-05-2008.iob";

	public void run(String home, String fileWithfilesToIndex, String indexresultFileName, String falsePositivePositionsFileName, String falseNegativePositionsFileName, String truePositivePositionsFileName, OntologyStore ontology){
		Integer truePos = 0;
		Integer falsePos = 0;
		Integer falseNeg = 0;

		WriteTextFile falsePosPositionsFile = new WriteTextFile(falsePositivePositionsFileName);
		WriteTextFile falseNegPositionsFile = new WriteTextFile(falseNegativePositionsFileName);
		WriteTextFile truePosPositionsFile = new WriteTextFile(truePositivePositionsFileName);

		indexer.tokenizer = new UMLSGeneChemTokenizer();
		indexer.biggestMatchOnly = true;
		indexer.destroyOntologyDuringRelease = false;
		indexer.setOntology(ontology);

		System.out.println("Releasing thesaurus. "+StringUtilities.now());
		indexer.release(); 
		if (disambiguate){
			geneDisambiguator = new GeneDisambiguator(indexer, 4000000, Integer.MAX_VALUE);
		}

		System.out.println("Indexing texts. "+StringUtilities.now());
		List<String> files = TextFileUtilities.loadFromFile(fileWithfilesToIndex);
		System.out.println("No of keys: " +positions.size());
		for (String file : files){
			List<String> textPairs = new ArrayList<String>();
			List<String> kolarikPairs = positions.get(file.substring(0, file.indexOf(".txt")));
			falsePosPositionsFile.writeln("### "+file.substring(0, file.indexOf(".txt")));
			truePosPositionsFile.writeln("### "+file.substring(0, file.indexOf(".txt")));
			falseNegPositionsFile.writeln("### "+file.substring(0, file.indexOf(".txt")));
			List<String> text = TextFileUtilities.loadFromFile(home+file);
			StringBuffer buffer = new StringBuffer();
			for (String textline: text){
				buffer.append(textline);
				buffer.append(" ");
			}
			String textString = buffer.toString().trim();
			indexer.index(textString);
			if (disambiguate){
				geneDisambiguator.disambiguate(indexer);
			}
//			DisambiguationDetails details = disambiguator.disambiguateWithDetails(indexer);
//			outputDetails(details);
			List<Integer> start = indexer.tokenizer.startpositions;
			List<Integer> end = indexer.tokenizer.endpositions;
			for (ResultConcept concept: indexer.resultConcepts){
				boolean chemVoc = false;
				boolean geneVoc = false;
				for (Relation relation: ontology.getRelationsForConceptAsSubject(concept.conceptId, DefaultTypes.fromVocabulary)) {
					if (ontology.getConcept(relation.object).getName().equals("CHEMICAL"))
						chemVoc = true;
					if (ontology.getConcept(relation.object).getName().equals("GENE"))
						geneVoc = true;
				}
				if (chemVoc && !geneVoc){
//					if (chemVoc){
					List<ResultTerm> terms = concept.terms;
					for (ResultTerm term: terms){
						Integer first = term.words[0];
						Integer last = term.words[term.words.length-1];
						String startPos = start.get(first).toString();
						Integer endP = end.get(last)+1;
						String endPos = endP.toString();
						String startAndEnd = startPos+"\t"+endPos;
						if (!textPairs.contains(startAndEnd)){
							textPairs.add(startAndEnd); 
						}
						Count count = releasedTerm2Count.get(term.term);
						if (count == null) {
							count = new Count();
							releasedTerm2Count.put(term.term, count);
						}
						count.count++;
					}
				}
			}
			if (kolarikPairs!=null){        
				boolean found = false;
				for (String textPair: textPairs){
					for (String kolarikPair: kolarikPairs){
						if (kolairkPairMatches(textPair, kolarikPair)){
							found = true;
							/** Corrects for irregularities in corpus*/
							if (!textPair.equals(kolarikPair)){
								textPair = kolarikPair;                
							}
						}
					}
					if (found){
						truePos++; 
						truePosPositionsFile.writeln(textPair);
					} else {
						falsePos++;
						falsePosPositionsFile.writeln(textPair);
					}
					found = false;
				}
				found = false;
				for (String kolarikPair: kolarikPairs){          
					for (String textPair: textPairs){
						if (kolairkPairMatches(kolarikPair, textPair)){
							found = true;
						}
					}
					if (!found){
						falseNeg++; 
						falseNegPositionsFile.writeln(kolarikPair);
					}
					found = false;
				}        
			} else if (!textPairs.isEmpty()){
				for (String textPair: textPairs){
					falsePos++;
					falsePosPositionsFile.writeln(textPair);
				}
			}
		}
		generateResults(indexresultFileName, ontology);
		Integer temp = truePos+falsePos;
		Double precision = truePos.doubleValue()/temp.doubleValue();
		temp = truePos+falseNeg;
		Double recall = truePos.doubleValue()/temp.doubleValue();
		System.out.println("True positives: "+truePos);
		System.out.println("False positives: "+falsePos);
		System.out.println("False negatives: "+falseNeg);
		System.out.println("Precision: "+precision);
		System.out.println("Recall: "+recall); 
		falsePosPositionsFile.close();
		falseNegPositionsFile.close();
		truePosPositionsFile.close();
	}

	public static boolean kolairkPairMatches(String textPair, String kolarikPair){
		String columns[] = textPair.split("\t");
		Integer a = Integer.parseInt(columns[0]);
		Integer b = Integer.parseInt(columns[1]);
		String cols[] = kolarikPair.split("\t");
		Integer c = Integer.parseInt(cols[0]);
		Integer d = Integer.parseInt(cols[1]);
		if ((a.intValue()==c.intValue() && b.intValue()==d.intValue()) || (a.intValue()+1==c.intValue() && b.intValue()+1==d.intValue()) || (a.intValue()-1==c.intValue() && b.intValue()-1==d.intValue())){
			return true;
		}
		return false;
	}

	private void generateResults(String filename, Ontology ontology) {
		try {
			FileOutputStream PSFFile = new FileOutputStream(filename);
			BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(PSFFile), 1000000);
			try {
				for (Entry<ReleasedTerm, Count> entry: releasedTerm2Count.entrySet()) {
					ReleasedTerm term = entry.getKey();
					StringBuffer line = new StringBuffer();
					line.append(entry.getValue().count);
					line.append("\t");
					int id = term.conceptId[0];
					int tid = term.termId[0];
					line.append(ontology.getConcept(id).getTerms().get(tid).text);
					line.append("\t");
					for (int cid: term.conceptId) {
						line.append(cid);
						line.append(";");
					}
					bufferedWrite.write(line.toString());
					bufferedWrite.newLine();
				}
				bufferedWrite.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private class Count {
		int count = 0;
	}
	protected class ReleasedTermComparator implements Comparator<ReleasedTerm> {
		@Override
		public int compare(ReleasedTerm arg0, ReleasedTerm arg1) {
			int result = arg0.conceptId[0] - arg1.conceptId[0];
			if (result == 0)
				result = arg0.termId[0] - arg1.termId[0];
			return result;
		}
	}

	private Map<ReleasedTerm, Count> releasedTerm2Count = new TreeMap<ReleasedTerm, Count>(new ReleasedTermComparator());  
	private ConceptPeregrine indexer = new ConceptPeregrine();

	private Map<String,List<String>> positions = getKolarikPositions(corpusFile);
	public static Map<String,List<String>> getKolarikPositions(String corpusFile){
		Map<String,List<String>> positions = new HashMap<String,List<String>>();
		int tokenClassIndex = 4;
		String tokenClass = "";
		int startIndex = 1;
		String start = "";
		int endIndex = 2;
		String end = "";
		String pmid = "";
		int entities = 0;
		ReadTextFile textFile = new ReadTextFile(corpusFile);
		Iterator<String> fileIterator = textFile.getIterator();
		boolean inEntity = false;
		while (fileIterator.hasNext()) {
			String line = fileIterator.next();
			String pair = "";
			if (line.startsWith("###")){
				pmid = line.substring(4);
			}
			else if (line.length()!=0){        
				String[] columns = line.split("\t");
				tokenClass = columns[tokenClassIndex];
				if (inEntity && (tokenClass.contains("|B-IUPAC") || tokenClass.contains("|B-TRIVIAL") || tokenClass.contains("|B-TRIVIALVAR") || tokenClass.contains("|B-FAMILY") || tokenClass.contains("|B-ABBREVIATION") || tokenClass.contains("|B-PARTIUPAC") || tokenClass.contains("|B-SUM"))){
					pair = start+"\t"+end;
					List<String> pairs = positions.get(pmid);
					if (pairs==null){
						pairs = new ArrayList<String>();
					}          
					pairs.add(pair);
					positions.put(pmid, pairs);
					entities++;
					start = columns[startIndex];
					end = columns[endIndex];
				}
				else if (!inEntity && (tokenClass.contains("|B-IUPAC") || tokenClass.contains("|B-TRIVIAL") || tokenClass.contains("|B-TRIVIALVAR") || tokenClass.contains("|B-FAMILY") || tokenClass.contains("|B-ABBREVIATION") || tokenClass.contains("|B-PARTIUPAC") || tokenClass.contains("|B-SUM"))){
					start = columns[startIndex];
					end = columns[endIndex];
					inEntity = true;          
				}
				else if (inEntity && (tokenClass.contains("|O") || tokenClass.contains("|B-MODIFIER"))){            
					pair = start+"\t"+end;
					List<String> pairs = positions.get(pmid);
					if (pairs==null){
						pairs = new ArrayList<String>();
					}
					pairs.add(pair);
					positions.put(pmid, pairs);
					entities++;
					inEntity = false;
				}                
				if (tokenClass.contains("|I-IUPAC") || tokenClass.contains("|I-TRIVIAL") || tokenClass.contains("|I-TRIVIALVAR") || tokenClass.contains("|I-FAMILY") || tokenClass.contains("|I-ABBREVIATION") || tokenClass.contains("|I-PARTIUPAC") || tokenClass.contains("|I-SUM")){
					end = columns[endIndex];
				}
			}
		}    
		System.out.println("Number of entities: "+entities);
		return positions;
	}
	private void outputDetails(DisambiguationDetails details) {    
		for (Map.Entry<Integer, List<EvaluationResult>> entry : details.conceptID2EvaluationResult.entrySet()){
			System.out.println("Evaluating concept: " + entry.getKey());
			for (EvaluationResult evaluationResult : entry.getValue()) {
				String ruleName = DisambiguatorRuleRegistry.getRuleName(evaluationResult.ruleID);
				System.out.println(ruleName + " (result: " + evaluationResult.result + ")");
				if (evaluationResult.extraDatas != null)
					for (ExtraData extraData : evaluationResult.extraDatas){
						String typeString = ExtraData.typeStrings[extraData.type];
						System.out.println("- " + typeString + ": " + extraData.value);
					}
			}
		}
	}
}
