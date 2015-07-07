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

package casperSoftwareCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.collections.Pair;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;

public class RulesCombination {

  public Map<Integer, Set<String>> cuisWithTerms;
  public Set<String> allTermsInUMLS;


  public RulesCombination(Map<Integer, Set<String>> cuisWithTerms, Set<String> allTermsInUMLS){
    this.cuisWithTerms = cuisWithTerms;
    this.allTermsInUMLS = allTermsInUMLS;
  }

  public static int syntacticInversionRule = 1;
  public static int possessiveRule = 2;
  public static int shortFormLongFormRule = 3;
  public static int angularBracketsRule = 4;
  public static int parenthesesWithSemanticTypeRule = 5;
  public static int leftParenthesisRule = 6;
  public static int rightParenthesisRule = 7;
  public static int leftBracketsRule = 8;
  public static int rightBracketsRule = 9;
  public static int NonEssentialParentheticalsRule = 10;
  public static int endParenthesesContainsFilteredWordRule = 11;
    
  public static Set<Integer> chemicalSemanticTypes = OntologyUtilities.getChemicalSemanticTypes();  
  public static Set<String> stopwordsForFiltering = getMedlineStopWordsForFiltering();
  public static Map<Integer, Map<String, Set<Integer>>> cuisWithRuleNo = new HashMap<Integer, Map<String, Set<Integer>>>();

  //Rewrite rules

  public CasperConcept applySyntacticInversionRule(CasperConcept concept){    
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = "";
    rewrittenTermText = Rules.findAndRewriteSyntacticUniversion(concept.getTermText());    
    if (!rewrittenTermText.equals("")){ 
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, syntacticInversionRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, syntacticInversionRule);                      
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }

  public CasperConcept applyPossessiveRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewritePossessive(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, possessiveRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, possessiveRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }

  public List<CasperConcept> applyShortformLongformRule(CasperConcept concept){
    List<CasperConcept> conceptsToReturn = new ArrayList<CasperConcept>();
    List<Pair<String, String>> rewrittenToShortFormAndLongForm = Rules.findShortformLongformPattern(concept.getTermText());
    CasperConcept shortformRewrittenConcept = null;
    CasperConcept longformRewrittenConcept = null;
    if (rewrittenToShortFormAndLongForm != null){      
      String shortForm = rewrittenToShortFormAndLongForm.get(0).object1.trim();      
      String longForm = rewrittenToShortFormAndLongForm.get(0).object2.trim();    
      if (newTermUniqueForConceptCaseInsensitive(concept, shortForm)){
        if (newTermUniqueForConceptAndRule(concept, shortForm, shortFormLongFormRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(shortForm)){
            shortformRewrittenConcept = setRewriteConcept(concept, shortformRewrittenConcept, shortForm, shortFormLongFormRule);          
            conceptsToReturn.add(shortformRewrittenConcept);
            addTermToUniqueCuiTermRuleCombination(shortformRewrittenConcept);
          }
        }
      }
      if (newTermUniqueForConceptCaseInsensitive(concept, longForm)){
        if (newTermUniqueForConceptAndRule(concept, longForm, shortFormLongFormRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(longForm)){
            longformRewrittenConcept = setRewriteConcept(concept, longformRewrittenConcept, longForm, shortFormLongFormRule);
            conceptsToReturn.add(longformRewrittenConcept);
            addTermToUniqueCuiTermRuleCombination(longformRewrittenConcept);
          }
        }
      }
    }
    return conceptsToReturn;
  }

  public CasperConcept applyAngluarBracketsRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewriteAngularBrackets(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, angularBracketsRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, angularBracketsRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }

  public CasperConcept applySemanticTypesRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewriteParenthesesWithSemanticType(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, parenthesesWithSemanticTypeRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, parenthesesWithSemanticTypeRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      } 
    }
    return rewrittenConcept;
  }


  public CasperConcept applyLeftSideParenthesesRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewriteBeginParentheses(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, leftParenthesisRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){            
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, leftParenthesisRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }

  public CasperConcept applyRightSideParenthesesRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewriteEndParentheses(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, rightParenthesisRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, rightParenthesisRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }

  public CasperConcept applyLeftSideBracketsRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewriteBeginBrackets(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, leftBracketsRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, leftBracketsRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }

  public CasperConcept applyRightSideBracketsRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewriteEndBrackets(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, rightBracketsRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, rightBracketsRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }

  public CasperConcept applyNonEssentialParantheticalsRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewriteNonEssentialParentheticals(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, NonEssentialParentheticalsRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, NonEssentialParentheticalsRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }

/**  public CasperConcept applyEndParenthesesContainsFilteredWordRule(CasperConcept concept){
    CasperConcept rewrittenConcept = null;
    String rewrittenTermText = Rules.findAndRewriteEndParenthesesContainsFilteredWordPattern(concept.getTermText());
    if (!rewrittenTermText.equals("")){
      if (newTermUniqueForConceptCaseInsensitive(concept, rewrittenTermText)){
        if (newTermUniqueForConceptAndRule(concept, rewrittenTermText, endParenthesesContainsFilteredWordRule)){
          if (rewrittenTermIsNotCaseInsensitiveHomonym(rewrittenTermText.trim())){
            rewrittenConcept = setRewriteConcept(concept, rewrittenConcept, rewrittenTermText, endParenthesesContainsFilteredWordRule);
            addTermToUniqueCuiTermRuleCombination(rewrittenConcept);
          }
        }
      }
    }
    return rewrittenConcept;
  }
*/

  //Suppress rules

  public static boolean applyDosagesRule(CasperConcept concept){
    if (Rules.findAndSuppressDosages(concept.getTermText())){
      return true;
    }
    return false;
  }

  public static boolean applyAtSignRule(CasperConcept concept){    
    if (Rules.findAndSuppressAtSign(concept.getTermText())){
      return true;
    }
    return false;
  }

  public static boolean applyMartijnsRule(CasperConcept concept){    
    if (Rules.MartijnsFilterRule(concept.getTermText(), stopwordsForFiltering)){
      return true;
    }
    return false;
  }

  public static boolean applyECrule(CasperConcept concept){    
    if (Rules.findAndSuppressECnumbers(concept.getTermText())){
      return true;
    }
    return false;
  }

  public static boolean applyNECrule(CasperConcept concept){    
    if (Rules.findAndSuppressNEC(concept.getTermText())){
      return true;  
    }
    return false;
  }

  public static boolean applyNOSrule(CasperConcept concept){    
    if (Rules.findAndSuppressNOS(concept.getTermText())){
      return true;
    }
    return false;
  }

  public static boolean applyMiscRule(CasperConcept concept){    
    if (Rules.findAndSuppressMisc(concept.getTermText())){
      return true;
    }
    return false;
  }

  public static boolean applyNoOfWordsMoreThanFiveRule(CasperConcept concept){
    if (Rules.findAndSuppressWordsMoreThanFiveWords(concept)){
      return true;
    }
    return false;
  }

  public boolean conceptHasChemicalSemanticType(CasperConcept concept){
    Set<Integer> semsForConcept = concept.getSemType();    
    Iterator<Integer> semIterator = semsForConcept.iterator();
    while (semIterator.hasNext()){
      Integer semID = semIterator.next();
      if (chemicalSemanticTypes.contains(-semID)){
        return true;
      }
    }
    return false;
  }

  public static Set<String> getMedlineStopWordsForFiltering() {
    Set<String> result = new TreeSet<String>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(RulesCombination.class.getResourceAsStream("stopWordsMedline.txt")));
    try {
      while (bufferedReader.ready()) {
        result.add(bufferedReader.readLine());
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public boolean newTermUniqueForConceptCaseInsensitive(CasperConcept concept, String newTermText){
    String newText = Rules.makeLowerCaseAndRemoveEos(newTermText);
    Set<String> TempTerms = cuisWithTerms.get(concept.getCUI());
    Iterator<String> setIt = TempTerms.iterator();
    while (setIt.hasNext()){
      String item = setIt.next();
      if (newText.equals(item)) return false;
    }
    return true;
  }

  public boolean rewrittenTermIsNotCaseInsensitiveHomonym(String text){
    String newText = Rules.makeLowerCaseAndRemoveEos(text);
    if (allTermsInUMLS.contains(newText)) return false;
    allTermsInUMLS.add(newText);
    return true;
  }

  public boolean newTermUniqueForConceptAndRule(CasperConcept concept, String newTermText, Integer rewriteRule){
    if (cuisWithRuleNo.get(concept.getCUI())!= null){
      String text = Rules.makeLowerCaseAndRemoveEos(newTermText);
      Map<String, Set<Integer>> tempMap = cuisWithRuleNo.get(concept.getCUI());
      if (tempMap.get(text)!= null){
        Set<Integer> tempRules = tempMap.get(text);
        if (tempRules.contains(rewriteRule)) return false;      
      }
    }
    return true;
  }

  public CasperConcept setRewriteConcept(CasperConcept concept, CasperConcept rewrittenConcept, String rewrittenTermText, int rule){
    rewrittenConcept = new CasperConcept();
    rewrittenConcept.setTermText(rewrittenTermText.trim());
    rewrittenConcept.setCUI(concept.getCUI());
    rewrittenConcept.setSUI(concept.getSUI());
    rewrittenConcept.setRewriteRuleFlag(rule);
    rewrittenConcept.setSemType(concept.getSemType());
    return rewrittenConcept;    
  }

  public void addTermToUniqueCuiTermRuleCombination(CasperConcept rewrittenConcept){
    String text = Rules.makeLowerCaseAndRemoveEos(rewrittenConcept.getTermText());
    Map<String, Set<Integer>> tempMap = cuisWithRuleNo.get(rewrittenConcept.getCUI());
    if (tempMap==null){
      Set<Integer> rules = new HashSet<Integer>();
      Map<String, Set<Integer>> map = new HashMap<String, Set<Integer>>(); 
      rules.add(rewrittenConcept.getRewriteRuleFlag());
      map.put(text, rules);
      cuisWithRuleNo.put(rewrittenConcept.getCUI(), map);
    }else {
      Set<Integer> tempRules = tempMap.get(text);
      if (tempRules==null){
        Set<Integer> secondTempRules = new HashSet<Integer>();
        secondTempRules.add(rewrittenConcept.getRewriteRuleFlag());
        tempMap.put(text, secondTempRules);
        cuisWithRuleNo.put(rewrittenConcept.getCUI(), tempMap);
      }else {
        tempRules.add(rewrittenConcept.getRewriteRuleFlag());
        tempMap.put(text, tempRules);
        cuisWithRuleNo.put(rewrittenConcept.getCUI(), tempMap);
      }
    }
  }

}
