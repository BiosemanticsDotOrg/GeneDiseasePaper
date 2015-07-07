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

package org.erasmusmc.rmi.peregrine.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.Tokenizer;

public class RMIKnewcoPeregrineTest {
  public static void main(String[] args) throws Exception {
    RMIPeregrine peregrine = new RMIPeregrine("mojojojo.biosemantics.org", 1011, "RMIPeregrineServerService");
    //RMIPeregrine peregrine = new RMIPeregrine("69.64.173.228", 1099, "KnewcoPeregrine");
    peregrine.setDisambiguate(false);
    
    //peregrine.setDisambiguate(true);
    for(int i=0; i<1;i++) {
      String input = "KLK3";
      peregrine.index( input );
      
      System.out.println( XML(input, peregrine) );
      //List<ResultTerm> resultTerms = peregrine.resultTerms;
      List<ResultConcept>  resultConcepts = peregrine.resultConcepts;
      //Tokenizer tokenizer = peregrine.tokenizer;
      if (i % 100 == 0){
        for(ResultConcept result: resultConcepts) {
          System.out.println(result.conceptId);
        }
        System.out.println("***"+i);
        
        for(ResultTerm result: peregrine.resultTerms) {
          System.out.println(result.term.termId);
          for(String str: peregrine.tokenizer.tokens) {
            System.out.println(str);
          }
        }
      }
    }
  }
  
  public static String XML(String input, RMIPeregrine peregrine){
    String result         = "";
    Integer NrOfTerms     = 0;
    Integer EndOfSentence = 0;
    Double MaxNrOfTerms   = 0.0;
    Double Rank           = 0.0;
    Map<ResultTerm, Integer> term2clid = new HashMap<ResultTerm, Integer>();
    
    if ( peregrine == null ){
      return result;
    }
    
    for (Integer i = 0; i < peregrine.resultTerms.size(); i++){ 
      term2clid.put(peregrine.resultTerms.get(i), i);
    }
    
    Tokenizer tokenizer = peregrine.tokenizer;
    result += "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>";
    result += "<fingerprint>";
    
    result += "<concepts count=\""+peregrine.resultConcepts.size()+ "\" clusters=\""+ peregrine.resultTerms.size()+"\"/>";
    
    Integer LineCount = peregrine.tokenizer.endOfSentence.size();
    result += "<lineslist count=\"" + LineCount + "\">";
    Integer StartPos = 0;
    Integer LineLength = 0;
    for (Integer i = 0 ; i < LineCount ; i++ ){
      EndOfSentence = peregrine.tokenizer.endOfSentence.get(i);
      if ( EndOfSentence > 0 ){
        LineLength = peregrine.tokenizer.endpositions.get(EndOfSentence-1) - StartPos + 1;

        int e = input.length();
        if ( (StartPos+LineLength) < e ){
          char ch = input.charAt(StartPos + LineLength);
          while (  ( (StartPos+LineLength) < e ) && ch != '.' && (int)ch != 10 && ch != '!' && ch != '?' && ch != ';'  ){
            LineLength++;
            ch = input.charAt(StartPos + LineLength);
          }
        }
      }
      else {
        LineLength = 0;
      }
      result += "<line startpos=\"" + (StartPos+1) + "\" length=\"" + LineLength + "\"/>";
      StartPos = StartPos + LineLength + 1;
    }
    result += "</lineslist>";

    for (ResultConcept concept : peregrine.resultConcepts){
      NrOfTerms = concept.terms.size();
      if ( NrOfTerms > MaxNrOfTerms ){
          MaxNrOfTerms = NrOfTerms.doubleValue();
      }
    }

    for (ResultConcept concept : peregrine.resultConcepts){
      Rank = concept.terms.size()/MaxNrOfTerms;
      result += "<concept id=\""+concept.conceptId+"\" rank=\""+Rank+"\" freq=\""+concept.terms.size()+"\">";
      for (ResultTerm term : concept.terms){
        int clid = term2clid.get(term);
        for (int word : term.words){
          result += "<word clid=\""+Integer.toString(clid+1)+
              "\" pos=\""+ Integer.toString(tokenizer.startpositions.get(word)+1)+
              "\" len=\""+ tokenizer.tokens.get(word).length()+
              "\">"+tokenizer.tokens.get(word)+"</word>";
        }
      }
      result += "</concept>";
    } 
    result += "</fingerprint>";
    
    return result;
  }  

}
