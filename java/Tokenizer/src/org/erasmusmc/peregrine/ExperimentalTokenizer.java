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

package org.erasmusmc.peregrine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.utilities.StringUtilities;

public class ExperimentalTokenizer extends Tokenizer implements Serializable{

	public boolean removeSuffixes = true; //Release takes longer if true
	private Set<String> suffixes = getDefaultSuffixes();
	private String string;
	private boolean potentialEosFlag;
	private final Map<String, WordData> word2data = new HashMap<String, WordData>();
	private static final long serialVersionUID = 1L;
	private List<Integer> ambiguousEOS = new ArrayList<Integer>();

	public static void main(String[] args){
		ExperimentalTokenizer tokenizer = new ExperimentalTokenizer();
		 
		//String text = "A John. A dr. John. a-backed a,abc-def a, a.bcd ab(cd). Abc) abc. Abc- (abc. def). A-b-c-backed dr. John";
		String text = "MMP (8%). Once upon a time, there was a 5,3-monoxoy-something. Alzheimer's disease. Disease, Alzheimer's. One [3,5] blaat [blaat]. One (3,5) blaat (blaat)";
		System.out.println(text);
		//tokenizer.tokenize("a-backed abc-def a, John a.bcd ab(cd). Abc) abc. Abc- (abc. def). A-b-c-backed dr. John");
		tokenizer.tokenize(text);
		int start = 0;

		for (int eos : tokenizer.endOfSentence){
			System.out.println(StringUtilities.join(tokenizer.tokens.subList(start, eos)," "));
			start = eos;
		}
	}

	public ExperimentalTokenizer(){
		super();
	}

	public ExperimentalTokenizer(Tokenizer tokenizer) {
		super(tokenizer);
	}

	public void tokenize(String string){
		this.string = string;
		word2data.clear();
		tokens.clear(); 
		startpositions.clear(); 
		endpositions.clear();  
		endOfSentence.clear();
		ambiguousEOS.clear();
		potentialEosFlag = true;
		boolean inParenthesis = false;

		int start = 0;
		int i = 0;
		int lastHyphen = -1;
		for (; i < string.length(); i++){
			char ch = string.charAt(i);
			if (isWordSeperator(ch))
				if (ch == '-')
					lastHyphen = i;
				else if (ch == '.'){
					if(!inParenthesis && i < string.length()-2 && string.charAt(i+1) == ' ') {
						int ord = (int)string.charAt(i+2);
						if (ord<97 || ord>122){ //anything but lowercase   
							trimSplitAndAddToken(new TokenRef(start, i), lastHyphen); 
							ambiguousEOS.add(endOfSentence.size());      
							endOfSentence.add(tokens.size()); 
							potentialEosFlag = true;
							i++;
							start = i+1;
						}
					}
				} else {  
					trimSplitAndAddToken(new TokenRef(start, i), lastHyphen);        

					if (ch == '(') 
						inParenthesis = true;
					else if (ch == ')')
						inParenthesis = false;

					if (tokens.size() != 0){ //Detect End Of Sentence:  
						potentialEosFlag = false;
						if ((int)ch == 10 || ch == '!' || ch == '?') //single char unambiguous patterns
							potentialEosFlag = true;
						else if ((ch == ']' || ch == ')') && i < string.length()-1 && string.charAt(i+1) == '.'){
							potentialEosFlag = true;
							if (ch == ')'){
								addToken(new TokenRef(i,i+1));
								ch = '@';
							}
							i++;
						}
						if (potentialEosFlag)
						  endOfSentence.add(tokens.size());
					}
					start = i+1;
				}
			if (ch == ',' || ch == '(' || ch == ')')
				addToken(new TokenRef(i,i+1));
		}
		if (start != i) {
			trimSplitAndAddToken(new TokenRef(start, i), lastHyphen);   
		} 
		if (ambiguousEOS.size() != 0){
			checkForMoreAbbreviations();
			disambiguateEOS();
		}
		//Add end of sentence at end of document:
		endOfSentence.add(tokens.size());

		removeDuplicates(endOfSentence);
	}

	private void removeDuplicates(List<Integer> endOfSentence){
		Iterator<Integer> eosIterator = endOfSentence.iterator();
		int previous = -1;
		while (eosIterator.hasNext()){
			int eos = eosIterator.next();
			if (eos == previous)
				eosIterator.remove();
			else
				previous = eos;
		}
	}

	private void disambiguateEOS() {
		for (int i = ambiguousEOS.size()-1; i >= 0; i--){
			int eosIndex = ambiguousEOS.get(i);
			int tokenIndex = endOfSentence.get(eosIndex);
			if (tokenIndex > 0 && tokenIndex < tokens.size()){
				WordData precedingWordData = word2data.get(tokens.get(tokenIndex-1).toLowerCase());
				WordData nextWordData = word2data.get(tokens.get(tokenIndex).toLowerCase());
				if (!(nextWordData.isNotProperNoun || 
						(precedingWordData.isNotAbbreviation) ||
						(!precedingWordData.isAbbreviation && !nextWordData.isProperNoun)) ||
						(!precedingWordData.isNotAbbreviation && nextWordData.isNumber )){
					endOfSentence.remove(eosIndex);
				} 
			}   
		}    
	}

	private void checkForMoreAbbreviations() {
		String word;
		WordData wordData;
		for (Map.Entry<String, WordData> entry : word2data.entrySet()){
			wordData = entry.getValue();
			if (!wordData.isNotAbbreviation){
				word = entry.getKey();
				if (StringUtilities.containsNumber(word))
					wordData.isNotAbbreviation = true; 
				else {
					if (word.length() == 1 || noVowels(word)) 
						wordData.isAbbreviation = true; 
				}
			}
		}
	}

	private boolean noVowels(String word) {
		for (char c : word.toCharArray()){
			if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y'){
				return false;
			}
		}
		return true;
	}

	private void trimSplitAndAddToken(TokenRef tokenRef, int lastHyphen) {
		tokenRef = trim(tokenRef);

		if (tokenRef.length() == 0)
			return;

		if (removeSuffixes && lastHyphen > tokenRef.start && lastHyphen < tokenRef.end-1){ 
			String suffix = string.substring(lastHyphen+1, tokenRef.end);
			if (suffixes.contains(suffix.toLowerCase())){// || StringUtilities.isNumber(suffix) || StringUtilities.isRomanNumeral(suffix)){
				addToken(new TokenRef(tokenRef.start, lastHyphen));
				addToken(new TokenRef(lastHyphen + 1, tokenRef.end));
				return;
			}
		}  
		addToken(tokenRef);
	}


	private TokenRef trim(TokenRef tokenRef) {
		if (tokenRef.length() < 2)
			return tokenRef;

		char startChar = string.charAt(tokenRef.start);
		char endChar = string.charAt(tokenRef.end - 1);

		if (startChar == '.')
			tokenRef.start++;
		else if (endChar == '.')
			tokenRef.end--;
		else if (startChar == '[' && endChar == ']') {
			tokenRef.start++;
			tokenRef.end--;
		} else if (startChar == '{' && endChar == '}') {
			tokenRef.start++;
			tokenRef.end--;
		}else if (startChar == '[' && !contains(tokenRef, ']'))
			tokenRef.start++;
		else if (endChar == ']' && !contains(tokenRef, '['))
			tokenRef.end--;
		else if (startChar == '{' && !contains(tokenRef, '}'))
			tokenRef.start++;
		else if (endChar == '}' && !contains(tokenRef, '{'))
			tokenRef.end--;   
		else 
			return tokenRef;
		return trim(tokenRef);
	}

	private boolean contains(TokenRef tokenPos, char ch) {
		for (int i = tokenPos.start; i < tokenPos.end; i++)
			if (string.charAt(i) == ch)
				return true;
		return false;
	}

	private void addToken(TokenRef tokenRef){
		String word = tokenRef.toString();

		String lcword = word.toLowerCase();
		tokens.add(word);
		startpositions.add(tokenRef.start);
		endpositions.add(tokenRef.end-1);

		//add to word list:
		WordData wordData;
		wordData = word2data.get(lcword);
		if (wordData == null) {
			wordData = new WordData();
			word2data.put(lcword, wordData);
		}

		//check for proper noun:
		int ord = (int)word.charAt(0);
		if (ord<91 && ord>64) { // first char is a capital
			if (!potentialEosFlag) 
				wordData.isProperNoun = true; 
		} else {
			wordData.isNotProperNoun = true;
		}

		//check for abbreviation:
		if (word.length() > 4 || (tokenRef.end < string.length()-1 && !(string.charAt(tokenRef.end) == '.'))){
			wordData.isNotAbbreviation = true;
		}

		//check for number:
		if (StringUtilities.isNumber(word))
			wordData.isNumber = true;
	}

	private boolean isWordSeperator(char ch){
		return (!Character.isLetterOrDigit(ch) &&
				!(ch == '+') &&
				!(ch == ']') &&
				!(ch == '[') &&
				//!(ch == ')') &&
				//!(ch == '(') &&
				!(ch == '}') &&
				!(ch == '{') &&
				!(ch == '\''));
	}  

	private static Set<String>  getDefaultSuffixes() {
		Set<String>  suffixes = new HashSet<String>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ChemicalSBDtokenizer.class.getResourceAsStream("chemicalSuffixes.txt")));
		try {
			while (bufferedReader.ready()) {
				String suffix = bufferedReader.readLine();
				if (suffix.startsWith("-"))
					suffix = suffix.substring(1);
				suffixes.add(suffix);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return suffixes;
	}
	
	public Set<String> getSuffixes(){
		return suffixes;
	}
	
	public void setSuffixes(Set<String> suffixes){
		this.suffixes = suffixes;
	}

	private class WordData implements Serializable{
		private static final long serialVersionUID = -6853979045253442261L;
		boolean isAbbreviation = false;
		boolean isNotAbbreviation = false;
		boolean isProperNoun = false;
		boolean isNotProperNoun = false;
		boolean isNumber = false;
	}

	private class TokenRef {
		int start; //inclusive
		int end; //exclusive
		public TokenRef(int start, int end){
			this.start = start;
			this.end = end;
		}

		public String toString(){
			return string.substring(start, end);
		}

		public int length(){
			return end-start;
		}
	}
}
