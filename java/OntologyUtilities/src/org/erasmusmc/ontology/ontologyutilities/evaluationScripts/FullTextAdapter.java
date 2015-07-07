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

package org.erasmusmc.ontology.ontologyutilities.evaluationScripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.Tokenizer;
import org.erasmusmc.peregrine.disambiguator.GeneDisambiguator;

public class FullTextAdapter {

	public static Map<Integer, Context> process(List<String> lines, ConceptPeregrine peregrine, GeneDisambiguator disambiguator){
		List<String> sections = toSections(lines);
		return index(sections, peregrine, disambiguator);
	}

	private static Map<Integer, Context> index(List<String> sections, ConceptPeregrine peregrine, GeneDisambiguator disambiguator) {
		Map<Integer, Context> id2context = new HashMap<Integer, Context>();
		List<String> tokens = new ArrayList<String>();
		Map<Integer, ResultConcept> id2concept = new HashMap<Integer, ResultConcept>();
		for (String section : sections){
			peregrine.index(section);
			if (disambiguator != null)
				disambiguator.disambiguate(peregrine);
			removeNonCooccurring(peregrine, id2context);

			//Change word ids:
			for (ResultTerm term : peregrine.resultTerms)
				for (int i = 0; i < term.words.length; i++)
					term.words[i] = term.words[i] + tokens.size();

			//Store found terms:
			for (ResultConcept concept : peregrine.resultConcepts){
				ResultConcept existingConcept = id2concept.get(concept.conceptId);
				if (existingConcept == null)
					id2concept.put(concept.conceptId, concept);
				else
					existingConcept.terms.addAll(concept.terms);
			}
			tokens.addAll(peregrine.tokenizer.tokens);	
		}
		peregrine.resultConcepts = new ArrayList<ResultConcept>(id2concept.values());
		peregrine.tokenizer.tokens = tokens;
		return id2context;
	}

	private static void removeNonCooccurring(ConceptPeregrine peregrine, Map<Integer,Context> cid2context) {
		Set<Integer> conceptWords = new HashSet<Integer>();
		Map<Integer, Set<Integer>> cid2sentenceIDs = new HashMap<Integer, Set<Integer>>();
		Set<Integer> singleSentences = new HashSet<Integer>();
		Set<Integer> cooccurringSentences = new HashSet<Integer>();

		for (ResultConcept concept : peregrine.resultConcepts){
			Set<Integer> sentenceIDs = new HashSet<Integer>();
			cid2sentenceIDs.put(concept.conceptId, sentenceIDs);
			for (ResultTerm term : concept.terms){
				int sentenceID = getSentenceID(peregrine.tokenizer, term);
				if (sentenceIDs.add(sentenceID))
					if (!singleSentences.add(sentenceID))
						cooccurringSentences.add(sentenceID);
				for (int word : term.words)
					conceptWords.add(word);
			}
		}	

		List<String> lines = filterLines(peregrine.tokenizer, conceptWords);

		//remove all non cooccurring concepts:
		Iterator<ResultConcept> conceptIterator = peregrine.resultConcepts.iterator();
		while (conceptIterator.hasNext()){
			ResultConcept concept = conceptIterator.next();
			boolean cooccurrence = false;
			for (Integer sentenceID : cid2sentenceIDs.get(concept.conceptId))
				if (cooccurringSentences.contains(sentenceID)){
					cooccurrence = true;
					break;
				}
			if (!cooccurrence)
				conceptIterator.remove();				
		}		

		//Build contexts:
		for (ResultConcept concept : peregrine.resultConcepts){
			Context context = cid2context.get(concept.conceptId);
			if (context == null){
				context = new Context();
				cid2context.put(concept.conceptId, context);
			}
			for (Integer sentenceID : cid2sentenceIDs.get(concept.conceptId)){
				String sentence = lines.get(sentenceID);
				context.all.append(sentence);
				if (cooccurringSentences.contains(sentenceID))
					context.cooccurring.append(sentence);
			}
			cid2context.put(concept.conceptId, context);
		}
	}

	public static class Context {
		public StringBuilder cooccurring = new StringBuilder();
		public StringBuilder all = new StringBuilder();
	}
	private static List<String> filterLines(Tokenizer tokenizer,	Set<Integer> conceptWords) {
		List<String> lines = new ArrayList<String>();
		int start = 0;
		for (int eos : tokenizer.endOfSentence){
			StringBuilder sb = new StringBuilder();
			for (int i = start; i < eos; i++)
				if (!conceptWords.contains(i)){
					sb.append(tokenizer.tokens.get(i));
					sb.append(' ');
				} else 
					sb.append("proteinX ");
			sb.append("\n");
			lines.add(sb.toString());
			start = eos;	
		}
		return lines;
	}

	private static int getSentenceID(Tokenizer tokenizer, ResultTerm term) {
		int start = 0;
		int termStart = term.words[0];
		for (int i = 0; i < tokenizer.endOfSentence.size(); i++){
			int eos = tokenizer.endOfSentence.get(i);
			if (termStart >= start && termStart < eos)
				return i;
			start = eos;	
		}
		return -1;
	}

	public static List<String> toSections(List<String> lines) {
		String buffer = "";
		List<String> sections = new ArrayList<String>();
		for (String line : lines){
			if (line.length() == 0){
				sections.add(buffer);
				buffer = "";
			} else
				buffer = buffer + "\n" + line;
		}
		return sections;
	}

	public static String getSentence(String text, Tokenizer tokenizer, ResultTerm term) {
		int termStart = term.words[0];
		int termEnd = term.words[term.words.length-1];
		int termStartPos = tokenizer.startpositions.get(termStart);
		int termEndPos = tokenizer.endpositions.get(termEnd);
		int sos = 0;
		StringBuilder sentence = new StringBuilder();
		for (int eos : tokenizer.endOfSentence){
			if (termStart >= sos && termStart < eos){
				if (termStart != sos){
					sentence.append(text.substring(tokenizer.startpositions.get(sos), termStartPos));
					if (termEnd != eos-1)
						sentence.append(" and ");
				}
				if (termEnd != eos-1)
					sentence.append(text.substring(termEndPos+1,tokenizer.endpositions.get(eos-1)));
				sentence.append(". ");
			}
			sos = eos;
		}
		return sentence.toString();
	}
}
