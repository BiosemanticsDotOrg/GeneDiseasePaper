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

package JochemBuilder.SharedCurationScripts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.erasmusmc.collections.Pair;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.ShortFormLongFormMatcher;
import org.erasmusmc.utilities.StringUtilities;

public class RewriteRules {
	  public static final String allEndBracketsNotGreedyPattern = "\\[[^]]*\\]$";
	  public static final String allBeginBracketsNotGreedyPattern = "^\\[[^]]*\\]";
	  public static final String allBeginParenthesisNotGreedyPattern = "^\\([^)]*\\)";
	  public static final String allEndParenthesisNotGreedyPattern = "\\([^)]*\\)$";
	  public static final String syntacticUniversionPattern = ",\\s";
	  public static final String possessivePattern = "'s\\s";
	  public static final String dosagePattern = "(\\s\\d[\\d.]*\\s?((g )|(ug)|(mg)|(ml)|%)|(\\(ml\\))|(\\(mg\\))|(\\(gm\\))|(\\(ug\\)))";
	  public static final String atSignPattern = "@";
	  public static final String ECpattern = "^EC\\s[0-9]+\\.";
	  public static final String necPatternCombined = "(,\\snec$)|(\\s\\(nec\\)$)|(\\s\\[nec\\]$)|(not elsewhere classified)|(unclassified)|(without mention)";
	  public static final String nosPatternCombined = "(,\\snos$)|(\\s\\(nos\\)$)|(\\s\\[nos\\]$)|(not otherwise specified)|(not specified)|(unspecified)";
	  public static final String miscPatternCombined = "(^|\\s)other(\\s|$)|(deprecated)|(unknown)|(obsolete)|(^no\\s+)|(miscellaneous)|(\\(MMHCC\\))";
	  public static final String angularBrackets = "<[^<]*>";

	  public static Set<String> semanticTypesAsText = getSemanticTypesAsText();

	  public static String makeLowerCaseAndRemoveEos(String term){
	    String tempTerm = term.toLowerCase();
	    OntologyUtilities.tokenizer.tokenize(tempTerm);
	    String tokenizedTerm = "";
	    for (String token: OntologyUtilities.tokenizer.tokens) {
	      tokenizedTerm = tokenizedTerm.concat(token+" ");
	    }
	    return tokenizedTerm.trim();
	  }
	  
	  public static String firstLetterToLowerCaseAndRemoveEos(String term){
	    OntologyUtilities.tokenizer.tokenize(term);
	    String tokenizedTerm = "";
	    for (String token: OntologyUtilities.tokenizer.tokens) {
	      String tempToken = StringUtilities.firstLetterToLowerCase(token);        
	      tokenizedTerm = tokenizedTerm.concat(tempToken+" ");
	    }
	    return tokenizedTerm.trim();
	  }
	  
	  public static String removeEos(String term){
	    OntologyUtilities.tokenizer.tokenize(term);
	    String tokenizedTerm = "";
	    for (String token: OntologyUtilities.tokenizer.tokens) {
	      tokenizedTerm = tokenizedTerm.concat(token+" ");
	    }
	    return tokenizedTerm.trim();
	  }

	  public static String findAndRewriteParenthesesOrBrackets(String term){
	    String rewrittenTerm = "";
	    rewrittenTerm = findAndRewriteBeginParenthesesOrBrackets((term));
	    rewrittenTerm = findAndRewriteEndParenthesesOrBrackets((rewrittenTerm));
	    return rewrittenTerm;
	  }

	  public static String findAndRewriteEndParenthesesOrBrackets(String term){
	    String endparentheses = findAndRewriteEndParentheses(term);
	    String endbrackets = findAndRewriteEndBrackets(term);
	    if (!endparentheses.equals("")){
	      term = endparentheses;
	    }
	    if (!endbrackets.equals("")){
	      term = endbrackets;
	    }
	    return term;
	  }

	  public static String findAndRewriteBeginParenthesesOrBrackets(String term){
	    String beginparentheses = findAndRewriteBeginParentheses(term);
	    String beginbrackets = findAndRewriteBeginBrackets(term);
	    if (!beginparentheses.equals("")){
	      term = beginparentheses;
	    }
	    if (!beginbrackets.equals("")){
	      term = beginbrackets;
	    }
	    return term;
	  }

	  public static String findAndRewriteBeginParentheses(String term) {
	    String rewrittenTerm = "";
	    Pattern p = Pattern.compile(allBeginParenthesisNotGreedyPattern);
	    Matcher m = p.matcher(term);
	    int count = 0;
	    while(m.find()) {
	      term = m.replaceAll(" ").trim();
	      count++;
	    }
	    if (count > 0){
	      rewrittenTerm = term;
	    }
	    return rewrittenTerm;
	  }

	  public static String findAndRewriteEndParentheses(String term) {
	    String rewrittenTerm = "";
	    Pattern p = Pattern.compile(allEndParenthesisNotGreedyPattern);
	    Matcher m = p.matcher(term);
	    int count = 0;
	    while(m.find()) {
	      term = m.replaceAll(" ").trim();
	      count++;
	    }
	    if (count > 0){
	      rewrittenTerm = term;
	    }
	    return rewrittenTerm;
	  }

	  public static String findAndRewriteBeginBrackets(String term) {
	    String rewrittenTerm = "";
	    Pattern p = Pattern.compile(allBeginBracketsNotGreedyPattern);
	    Matcher m = p.matcher(term);
	    int count = 0;
	    while(m.find()) {
	      term = m.replaceAll(" ").trim();
	      count++;
	    }
	    if (count > 0){
	      rewrittenTerm = term;
	    }
	    return rewrittenTerm;
	  }

	  public static String findAndRewriteEndBrackets(String term) {
	    String rewrittenTerm = "";
	    Pattern p = Pattern.compile(allEndBracketsNotGreedyPattern);
	    Matcher m = p.matcher(term);
	    int count = 0;
	    while(m.find()) {
	      term = m.replaceAll(" ").trim();
	      count++;
	    }
	    if (count > 0){
	      rewrittenTerm = term;
	    }
	    return rewrittenTerm;
	  }

	  public static String findAndRewriteParenthesesWithSemanticType(String term){
	    String semanticTerm = "";
	    Pattern p = Pattern.compile(allEndParenthesisNotGreedyPattern);
	    Matcher m = p.matcher(term);
	    while(m.find()) {
	      String match = m.group().substring(1, m.group().length()-1).toLowerCase();
	      if (RewriteRules.semanticTypesAsText.contains(match)){
	        semanticTerm = m.replaceAll("").trim();
	      } 
	    }
	    return semanticTerm;
	  }

	  public static String findAndRewriteAngularBrackets(String term) {
	    String angularTerm = "";
	    Pattern p = Pattern.compile(angularBrackets);
	    Matcher m = p.matcher(term);
	    int count = 0;
	    while(m.find()) {
	      term = m.replaceAll("").trim();
	      count++;
	    }
	    if (count > 0){
	      angularTerm = term;
	    }
	    return angularTerm;
	  }

	  public static List<Pair<String, String>> findShortformLongformPattern(String term){
	    List<Pair<String, String>> sflfToBeReturned = null;
	    ShortFormLongFormMatcher sflf = new ShortFormLongFormMatcher();
	    List<Pair<String, String>> form = sflf.extractSFLFmatches(term);      
	    if (!form.isEmpty()){
	      String sf = form.get(0).object1;
	      if (term.endsWith("("+sf+")")){
	        String first = sf.substring(0, 1);
	        if (term.startsWith(first)){
	          sflfToBeReturned = form;
	        }
	      } 
	    }
	    return sflfToBeReturned;
	  }

	  public static String findAndRewriteSyntacticUniversion(String term){
	    String inversedTerm = "";
	    Pattern p = Pattern.compile(RewriteRules.syntacticUniversionPattern);
	    Matcher m = p.matcher(term);
	    int count = 0;
	    while(m.find()) {
	      count++;
	    }
	    if (count == 1){
	      boolean found = checkForPrepOrCon(term);      
	      if (!found){
	        int commaPos = term.indexOf(", ");
	        String sub1 = term.substring(commaPos+2);
	        String sub2 = term.substring(0, commaPos);
	        if (sub1.substring(sub1.length()-1, sub1.length()).trim().equals("-")){
	          inversedTerm = sub1.trim()+sub2.trim();
	        }else inversedTerm = sub1.trim()+" "+sub2.trim();
	      }
	    }
	    return inversedTerm;
	  }

	  public static String findAndRewritePossessive(String term) {
	    String possessiveTerm = "";
	    Pattern p = Pattern.compile(possessivePattern,Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(term);
	    int count = 0;
	    while(m.find()) {
	      term = m.replaceAll(" ").trim();
	      count++;
	    }
	    if (count > 0){
	      possessiveTerm = term;
	    }
	    return possessiveTerm;
	  }

	  public static boolean findAndSuppressDosages(String term) {
	    Pattern p = Pattern.compile(dosagePattern,Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(term);
	    if(m.find()) {
	      return true;
	    }else
	      return false;
	  }

	  public static boolean findAndSuppressAtSign(String term) {
	    Pattern p = Pattern.compile(atSignPattern);
	    Matcher m = p.matcher(term);
	    if(m.find()) {
	      return true;
	    }else
	      return false;
	  }

	  public static boolean findAndSuppressECnumbers(String term) {
	    Pattern p = Pattern.compile(ECpattern);
	    Matcher m = p.matcher(term);
	    if(m.find()) {
	      return true;
	    }else
	      return false;
	  }

	  public static boolean findAndSuppressNEC(String term) {
	    Pattern p = Pattern.compile(necPatternCombined,Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(term);
	    if(m.find()) {
	      return true;
	    }else
	      return false;
	  }

	  public static boolean findAndSuppressNOS(String term) {
	    Pattern p = Pattern.compile(nosPatternCombined,Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(term);
	    if(m.find()) {
	      return true;
	    }else
	      return false;
	  }

	  public static boolean findAndSuppressMisc(String term) {
	    Pattern p = Pattern.compile(miscPatternCombined,Pattern.CASE_INSENSITIVE);
	    Matcher m = p.matcher(term);
	    if(m.find()) {
	      return true;
	    }else
	      return false;
	  }

	  public static boolean checkForPrepOrCon(String term) {
	    Set<String> prepOrCon = RewriteRules.isPrepositionOrConjunction();
	    OntologyUtilities.tokenizer.tokenize(term);
	    for (String token: OntologyUtilities.tokenizer.tokens) {
	      if (prepOrCon.contains(token.toLowerCase())) {
	        return true;
	      }
	    }return false;
	  }

	  public static Set<String> isPrepositionOrConjunction(){
	    Set<String> result = new TreeSet<String>();
	    result.add("about");
	    result.add("above");
	    result.add("across");
	    result.add("after");
	    result.add("against");
	    result.add("along");
	    result.add("among");
	    result.add("around");
	    result.add("at");
	    result.add("before");
	    result.add("behind");
	    result.add("below");
	    result.add("beneath");
	    result.add("beside");
	    result.add("between");
	    result.add("beyond");
	    result.add("but");
	    result.add("by");
	    result.add("despite");
	    result.add("down");
	    result.add("during");
	    result.add("except");
	    result.add("for");
	    result.add("from");
	    result.add("in");
	    result.add("inside");
	    result.add("into");
	    result.add("like");
	    result.add("near");
	    result.add("of");
	    result.add("off");
	    result.add("on");
	    result.add("onto");
	    result.add("out");
	    result.add("outside");
	    result.add("over");
	    result.add("past");
	    result.add("since");
	    result.add("through");
	    result.add("throughout");
	    result.add("till");
	    result.add("to");
	    result.add("toward");
	    result.add("under");
	    result.add("underneath");
	    result.add("until");
	    result.add("up");
	    result.add("upon");
	    result.add("with");
	    result.add("within");
	    result.add("without");
	    result.add("and");
	    result.add("but");
	    result.add("or");
	    result.add("nor");
	    result.add("for");
	    result.add("so");
	    result.add("yet");
	    result.add("after");
	    result.add("although");
	    result.add("as");
	    result.add("because");
	    result.add("before");
	    result.add("how");
	    result.add("if");
	    result.add("once");
	    result.add("since");
	    result.add("than");
	    result.add("that");
	    result.add("though");
	    result.add("till");
	    result.add("until");
	    result.add("when");
	    result.add("where");
	    result.add("whether");
	    result.add("while");
	    return result;
	  }

	  public static Set<String> getSemanticTypesAsText(){
		    Set<String> result = new TreeSet<String>();
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(RewriteRules.class.getResourceAsStream("semanticTypesAsText.txt")));
		    try {
		      while (bufferedReader.ready()) {
		        result.add(bufferedReader.readLine().trim().toLowerCase());
		      }
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    return result;
		  }
}
