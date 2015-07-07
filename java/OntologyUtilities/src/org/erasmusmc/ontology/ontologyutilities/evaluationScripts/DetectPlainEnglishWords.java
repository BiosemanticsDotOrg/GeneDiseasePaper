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

import java.util.HashSet;
import java.util.Set;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyClient;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.peregrine.SimpleTokenizer;
import org.erasmusmc.peregrine.Tokenizer;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class DetectPlainEnglishWords {

	public static final int UKWORDS = 0;
	public static final int WORDNET = 1;
	public static int wordlist = UKWORDS;
	public static int maxWordCount = 2;
	public static String wordNetOntologyName = "Wordnet3_0";
	public static String defaultWordListFile = "/home/data/EnglishWords/ukwords.txt";
	private Set<String> words;
	private Tokenizer tokenizer = new SimpleTokenizer(); 

	/**
	 * Detects terms in the ontology that are also plain english words.
	 * @param ontology	
	 * @param wordListFile	null loads default word list
	 * @param ouputFile
	 */
	public DetectPlainEnglishWords(Ontology ontology, String wordListFile, String ouputFile){
		if (wordListFile == null){
			wordListFile = defaultWordListFile;
		}
		if (wordlist == UKWORDS)
			loadEnglishWords(wordListFile);
		else
	    loadWordnetWords();
	  
	  compare(ontology, ouputFile);
	}

	private void loadEnglishWords(String wordListFile) {
		words = new HashSet<String>();
		for (String word : new ReadTextFile(wordListFile))
		  words.add(word);
	}

	private void compare(Ontology ontology, String filename) {
		WriteTextFile out = new WriteTextFile(filename);
		for (Concept concept : ontology)
			for (TermStore term : concept.getTerms()){
				if (!StringUtilities.isAbbr(term.text)){
					if (words.contains(term.text.toLowerCase()))
						out.writeln(term.text);
					else {
						tokenizer.tokenize(term.text);
						if (tokenizer.tokens.size() <= maxWordCount){
							boolean ambiguous = true;
							for (String token : tokenizer.tokens)
								if (StringUtilities.isAbbr(token) || !words.contains(token.toLowerCase()))
									ambiguous = false;
							if (ambiguous)
								out.writeln(term.text);
						}
					}
				}
			}
		out.close();
	}

	private void loadWordnetWords() {
		OntologyManager manager = new OntologyManager();
		OntologyClient ontology = manager.fetchClient(wordNetOntologyName);
		words = new HashSet<String>();
		for (Concept concept : ontology){
			for (TermStore term : concept.getTerms())
				words.add(term.text.toLowerCase());
		}
		words.remove("i");
		words.remove("ii");
		words.remove("iii");
		words.remove("iv");
		words.remove("v");
		words.remove("vi");
		words.remove("vii");
		words.remove("viii");
		words.remove("ix");
		words.remove("x");
	}
}
