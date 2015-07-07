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

public class ChemicalSBDtokenizer  extends Tokenizer implements Serializable{

  boolean removeSuffixes = true; //Release takes longer if true

  public ChemicalSBDtokenizer(){
    super();
  }

  public ChemicalSBDtokenizer(Tokenizer tokenizer) {
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
    potentialEosFlag = false;
    boolean inParenthesis = false;

    int start = 0;
    int i = 0;
    for (; i < string.length(); i++){
      char ch = string.charAt(i);

      if (!Character.isLetterOrDigit(ch) &&
          !(ch == '.') &&
          !(ch == ',') &&
          !(ch == '+') &&
          !(ch == ']') &&
          !(ch == '[') &&
          !(ch == ')') &&
          !(ch == '(') &&
          !(ch == '}') &&
          !(ch == '{') &&
          !(ch == '-') &&
          !(ch == '\'')){
//      !(ch == '\'' && i>0 && Character.isLetter(string.charAt(i-1)) && string.length()-1 > i && string.charAt(i+1) == 's' && (string.length()-2 == i || !Character.isLetterOrDigit(string.charAt(i+2))))){ //leaves ' in possesive pattern    
        if (start != i) {
          AddToken(start, i);        
        }
        if (ch == '(') 
          inParenthesis = true;
        else if (ch == ')')
          inParenthesis = false;

        if (tokens.size() != 0){
          //Detect (potential) End Of Sentence:        
          potentialEosFlag = false;
          if ((int)ch == 10 || ch == '!' || ch == '?'){ //single char unambiguous patterns
            potentialEosFlag = true;
          } else if ((ch == ']' || ch == ')') && i < string.length()-1 && string.charAt(i+1) == '.'){
            potentialEosFlag = true;
            i++;
          } 
          if (!inParenthesis && ch == '.' && i < string.length()-2 && string.charAt(i+1) == ' ') {
            int ord = (int)string.charAt(i+2);
            if (ord<97 || ord>122){ //anything but lowercase    
              potentialEosFlag = true;
              ambiguousEOS.add(endOfSentence.size());        
              i++;
            }
          }
          if (potentialEosFlag) {
            endOfSentence.add(tokens.size());
          }

        }
        start = i+1;
      }
    }
    if (start != i) {
      AddToken(start, i);   
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
      if (tokenIndex > 0 && tokenIndex < tokens.size()-1){
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

  private void AddToken(int start, int end) {
    String word = string.substring(start,end);
    //Start change oct 22, 2008
    if (word.length()>1){
      if (word.startsWith(".") || word.startsWith(",")){
        word = word.substring(1);
        start = start+1;  
      }
      if (word.endsWith(".") || word.endsWith(",")){
        word = word.substring(0, word.length()-1);
        end = end-1;
        if (end==-1) end = 0;
      }
      if (word.startsWith("(") && word.endsWith(")")){
        word = word.substring(1, word.length()-1);
        start = start+1;
        end = end-1;
        if (end==-1) end = 0;
      }
      if (word.startsWith("(") && !word.contains(")")){
        word = word.substring(1, word.length());
        start = start+1;
      }
      if (word.endsWith(")") && !word.contains("(")){
        word = word.substring(0, word.length()-1);
        end = end-1;
        if (end==-1) end = 0;
      }
      if (word.startsWith("[") && word.endsWith("]")){
        word = word.substring(1, word.length()-1);
        start = start+1;
        end = end-1;
        if (end==-1) end = 0;
      }
      if (word.startsWith("[") && !word.contains("]")){
        word = word.substring(1, word.length());
        start = start+1;
      }
      if (word.endsWith("]") && !word.contains("[")){
        word = word.substring(0, word.length()-1);
        end = end-1;
        if (end==-1) end = 0;
      }
      if (word.startsWith("{") && word.endsWith("}")){
        word = word.substring(1, word.length()-1);
        start = start+1;
        end = end-1;
        if (end==-1) end = 0;
      }
      if (word.startsWith("{") && !word.contains("}")){
        word = word.substring(1, word.length());
        start = start+1;
      }
      if (word.endsWith("}") && !word.contains("{")){
        word = word.substring(0, word.length()-1);
        end = end-1;
        if (end==-1) end = 0;
      }      
    }
    if (removeSuffixes){ 
      // This code checks for suffixes at the end of words
      List<String> words = new ArrayList<String>();
      int start1 = -10;
      int end1 = -10;
      int start2 = -10;
      int end2 = -10;
      if (word.length()!=0){
        String suffixPart = "";
        for (String suffix: suffixes){
          if (word.toLowerCase().endsWith(suffix) && !word.toLowerCase().equals(suffix)){          
            suffixPart = suffix;                    
          }
        }

        if (suffixPart.length()!=0){
          String word1 = word.substring(0, word.length() - suffixPart.length());
          start1 = start;
          end1 = end-suffixPart.length();

          String word2 = suffixPart.substring(1);
          start2 = start+word1.length()+2;
          end2 = end;

          words.add(word1);        
          words.add(word2);
        }
      }
      if (!words.isEmpty()){
        int i =1;
        for (String wordPart: words){
          if (i==1){
            start = start1;
            end = end1-1;
          } else if (i==2){
            start = start2;
            end = end2-1;
          }
          String lcword = wordPart.toLowerCase();
          tokens.add(wordPart);
          startpositions.add(start);
          endpositions.add(end);

          //add to word list:
          WordData wordData;
          wordData = word2data.get(lcword);
          if (wordData == null) {
            wordData = new WordData();
            word2data.put(lcword, wordData);
          }

          //check for proper noun:
          if (wordPart.length()==0) System.out.println("Something is wrong "+words.toString());
          int ord = (int)wordPart.charAt(0);
          if (ord<91 && ord>64) { // first char is a capital
            if (!potentialEosFlag && tokens.size() != 0) wordData.isProperNoun = true; 
          } else {
            wordData.isNotProperNoun = true;
          }

          //check for abbreviation:
          if (wordPart.length() > 4 || (end < string.length()-1 && !(string.charAt(end) == '.'))){
            wordData.isNotAbbreviation = true;
          }

          //check for number:
          if (StringUtilities.isNumber(wordPart))
            wordData.isNumber = true;
          i++;
        }
      } else {
        if (word.length()!=0){
//        End change oct 22, 2008
          String lcword = word.toLowerCase();
          tokens.add(word);
          startpositions.add(start);
          endpositions.add(end-1);

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
            if (!potentialEosFlag && tokens.size() != 0) wordData.isProperNoun = true; 
          } else {
            wordData.isNotProperNoun = true;
          }

          //check for abbreviation:
          if (word.length() > 4 || (end < string.length()-1 && !(string.charAt(end) == '.'))){
            wordData.isNotAbbreviation = true;
          }

          //check for number:
          if (StringUtilities.isNumber(word))
            wordData.isNumber = true;
        }
      }
    } else {

      if (word.length()!=0){
        
//      End change oct 22, 2008
        String lcword = word.toLowerCase();
        tokens.add(word);
        startpositions.add(start);
        endpositions.add(end-1);

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
          if (!potentialEosFlag && tokens.size() != 0) wordData.isProperNoun = true; 
        } else {
          wordData.isNotProperNoun = true;
        }

        //check for abbreviation:
        if (word.length() > 4 || (end < string.length()-1 && !(string.charAt(end) == '.'))){
          wordData.isNotAbbreviation = true;
        }

        //check for number:
        if (StringUtilities.isNumber(word))
          wordData.isNumber = true;
      }
    }
  }

  private static Set<String> suffixes = getSuffixes();

  private static Set<String> getSuffixes() {
    Set<String> suffixes = new HashSet<String>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ChemicalSBDtokenizer.class.getResourceAsStream("chemicalSuffixes.txt")));
    try {
      while (bufferedReader.ready()) {
        suffixes.add(bufferedReader.readLine());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return suffixes;
  }

  private class WordData implements Serializable{
    private static final long serialVersionUID = -6853979045253442261L;
    boolean isAbbreviation = false;
    boolean isNotAbbreviation = false;
    boolean isProperNoun = false;
    boolean isNotProperNoun = false;
    boolean isNumber = false;
  }
  private String string;
  private boolean potentialEosFlag;
  private final Map<String, WordData> word2data = new HashMap<String, WordData>();
  private static final long serialVersionUID = 1L;
  private List<Integer> ambiguousEOS = new ArrayList<Integer>();
}
