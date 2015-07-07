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

package org.erasmusmc.ontology.ontologyutilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.StringUtilities;

public class GeneTermVariantGenerator {
  /** Generates spelling variants of gene and protein names using a set of rules.   * 
   * @param ontology    The ontology in which the spelling variants will be inserted. */
  public static void generateVariants(Ontology ontology){ //Generates spelling variations
    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
    while (conceptIterator.hasNext()){
      Concept concept = conceptIterator.next();
      if (OntologyUtilities.hasGeneVoc(concept, ontology))
        ProcessConcept(concept);
    }
  }
  private static void ProcessConcept(Concept concept) {
    List<TermStore> terms = concept.getTerms();
    int termCount = terms.size();
    for (int i = 0; i < termCount; i++){
      TermStore term = terms.get(i);
      TermStore newTerm;

      newTerm = DoRomanNumerals(term);
      if (newTerm != null) 
        terms.add(newTerm); 

      newTerm = DoDelimiter(term);
      if (newTerm != null) 
        terms.add(newTerm);
      
      newTerm = DoHyphen(term);
      if (newTerm != null) 
        terms.add(newTerm);

      //DoOpticalGapExpander(term, concept);

    }
  }
  enum State {lowercase, uppercase, number, delimiter};

  /**
   * Optical Gap expander
   * Splits terms based on transitions from lowercase to uppercase, letters to numbers and numbers to letters
   * 
   * Warning: generates a lot of spelling variations
   * 
   * Use instead of DoDelimiter
   * @param term
   * @param concept
   */
  public static void DoOpticalGapExpander(TermStore term, Concept concept) {
    String text = term.text;
    if (text.length() > 15) return;
    List<String> variations  = new ArrayList<String>();
    variations.add("");

    State state = State.delimiter;  
    for (int i = 0; i < text.length(); i++){
      char ch = text.charAt(i);
      State newState;
      if (Character.isLowerCase(ch))
        newState = State.lowercase;
      else if (Character.isUpperCase(ch))
        newState = State.uppercase;
      else if (Character.isDigit(ch))
        newState = State.number;
      else 
        newState = State.delimiter;

      if (!state.equals(newState) && !state.equals(State.delimiter) && !newState.equals(State.delimiter) &&
          !(state.equals(State.uppercase) && newState.equals(State.lowercase))){
        //Create new variations where a hyphen is added at this location:
        int varsize = variations.size();
        for (int j = 0; j < varsize; j++)
          variations.add(variations.get(j) + "-");     
      }

      //Add the character to all variations:
      for (int j = 0; j < variations.size(); j++)
        variations.set(j, variations.get(j) + ch);

      state = newState;

    }
    System.out.print(term.text + "\t");
    for (int i = 1; i < variations.size(); i++){
      TermStore newTerm = term.copy();
      newTerm.text = variations.get(i);
      System.out.print(newTerm.text + "\t");
      concept.getTerms().add(newTerm);
    }
    System.out.println("");
  }
  private static TermStore DoDelimiter(TermStore term) {
    //if (isSymbol(term.text)){
    String text = term.text;
    Boolean number = false;
    for (int i = text.length()-1; i > 0; i--){
      char ch = text.charAt(i);
      if (ch < 58 && ch > 47){ //Is a number
        number = true;
      } else {
        if (number){
          if (Character.isLetter(text.charAt(i))) { //No delimiter before number: add space
            return new TermStore(text.substring(0, i+1)+"-"+text.substring(i+1, text.length()));
          } else //Delimiter before number 
            if (!Character.isDigit(text.charAt(i-1))) //No number before the delimiter: 
              return new TermStore(text.substring(0, i)+text.substring(i+1, text.length()));
        }
        break;
      }
    }
    //}
    return null;
  }

  private static TermStore DoHyphen(TermStore term) {
	  String[] tokens = term.text.split(" ");
	  if (tokens.length > 1){
		  String lastToken = tokens[tokens.length-1]; 
		  if (StringUtilities.isNumber(lastToken) || StringUtilities.isRomanNumeral(lastToken))
			  return new TermStore(term.text.substring(0,term.text.length() - lastToken.length()-1) + "-" + lastToken);

	  }
	  tokens = term.text.split("-");
	  if (tokens.length > 1){
		  String lastToken = tokens[tokens.length-1]; 
		  if (StringUtilities.isNumber(lastToken) || StringUtilities.isRomanNumeral(lastToken))
			  return new TermStore(term.text.substring(0,term.text.length() - lastToken.length()-1) + " " + lastToken);	
	  }

	  return null;
  }

  private static TermStore DoRomanNumerals(TermStore term) {
    String text = term.text;
    if (text.length() > 1){

      //Replace number with roman numeral:
      char ch = text.charAt(term.text.length()-1);
      if (ch < 58 && ch > 48){ //Last char is a number
        char previousCh = text.charAt(term.text.length()-2);
        if (previousCh < 48 || previousCh > 57){ //Previous char is not a number
          StringBuffer newText = new StringBuffer();
          newText.append(text.substring(0,text.length()-1));
          if (Character.isLetter(previousCh)) newText.append("-");
          switch (ch) {
            case '1': newText.append("I"); break;
            case '2': newText.append("II"); break;
            case '3': newText.append("III"); break;
            case '4': newText.append("IV"); break;
            case '5': newText.append("V"); break;
            case '6': newText.append("VI"); break;
            case '7': newText.append("VII"); break;
            case '8': newText.append("VIII"); break;
            case '9': newText.append("IX"); break;            
          }
          return new TermStore(newText.toString());
        }
      }

      //Replace roman numeral with number:
      for (int i = text.length()-2; i > 0; i--){
        if (!Character.isLetterOrDigit(text.charAt(i))){       
          String lastPart = text.substring(i+1, text.length());
          String number = "";
          if (lastPart.equals("I")) number = "1"; else
            if (lastPart.equals("II")) number = "2"; else
              if (lastPart.equals("III")) number = "3"; else
                if (lastPart.equals("IV")) number = "4"; else
                  if (lastPart.equals("V")) number = "5"; else
                    if (lastPart.equals("VI")) number = "6"; else
                      if (lastPart.equals("VII")) number = "7"; else
                        if (lastPart.equals("VIII")) number = "8"; else
                          if (lastPart.equals("IX")) number = "9";
          if (!number.equals("")){
            return new TermStore(text.substring(0, i+1)+number);
          }
          break;
        }
      }
    }
    return null;
  }

}
