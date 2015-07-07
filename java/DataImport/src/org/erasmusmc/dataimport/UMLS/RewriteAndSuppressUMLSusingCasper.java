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

package org.erasmusmc.dataimport.UMLS;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

import casperSoftwareCode.AssignSemanticTypeToCui;
import casperSoftwareCode.CasperConcept;
import casperSoftwareCode.CasperFilters;
import casperSoftwareCode.ExtractCUIsAndTermsFromMRCONSO;
import casperSoftwareCode.RulesCombination;


public class RewriteAndSuppressUMLSusingCasper {
  public static boolean dosagesRule = true;
  public static boolean atsignRule = true;
  public static boolean shortTokenRule = true;
  public static boolean necRule = true;
  public static boolean nosRule = true;
  public static boolean ecNumbersRule = true;
  public static boolean miscRule = true;
  public static boolean wordsMoreThanFiveRule = false;

  String mrconsoPath;// = "/home/khettne/UMLS2008/2008AA/META/MRCONSO.RRF";
  String mrstyPath;// = "/home/khettne/UMLS2008/2008AA/META/MRSTY.RRF";
  String logfilePath;// = "/home/khettne/UMLS2008/UMLS_rewriting_log.log";
  String ontologyPath;// = "/home/khettne/UMLS2008/MRCONSO2008AA_rewrittenAndSuppressed.RRF";

  boolean suppressRules = true;
  boolean rewriteRules = true;

  boolean syntacticInvRule = true;
  boolean possessivesRule = true;
  boolean shortformlongformRule = true;
  boolean angularBracketsRule = true;
  boolean semanticTypesRule = true;
  boolean beginParenthesesRule = false;
  boolean endParenthesesRule = false;
  boolean beginBracketsRule = false;
  boolean endBracketsRule = false;

  public static boolean chemical = false;
  
  /** These variables are required for the non-Casper rules: */
  
  //Patterns for suppress rules
  public static Pattern Retiredpattern = Pattern.compile("retired code", Pattern.CASE_INSENSITIVE);
  public static Pattern CurlyParenthesispattern = Pattern.compile("\\{.*\\}");
  public static Pattern xxxPattern = Pattern.compile("xxx", Pattern.CASE_INSENSITIVE);
  public static Pattern proteinWeightPattern = Pattern.compile("^[0-9]+ ?[kK][dD][aA]?$");
  public static int maxtermsize = 100;
  public static int mintermsize = 3;
  public static boolean retiredPatternRule = true;
  public static boolean curlyParenthesesRule = true;
  public static boolean xxxPatternRule = true;
  public static boolean proteinWeightPatternRule = true;
  public static boolean maxTermSize100Rule = true;
  public static boolean minTermSize3Rule = true; 
  public static Set<Integer> filteredSemanticTypes = getSemanticTypesForFiltering();
  public static Set<Integer> filteredSemanticTypesNotMesh = getSemanticTypesForFilteringNotMesh();

  // Variables for rewrite rules 
  
  public boolean nonEssentialParentheticalsRule = true;
  public boolean endParenthesesContainsFilteredWordRule = true;
  
  public void run(String mrconsoPath, String mrstyPath, String ontologyPath, String logFilePath) {
    this.mrconsoPath = mrconsoPath;
    this.mrstyPath = mrstyPath;
    this.ontologyPath = ontologyPath;
    this.logfilePath = logFilePath;
    
    /** Create log */
    WriteTextFile logFile = new WriteTextFile(logfilePath);

    /** Create datatypes for homonym checks */
    System.out.println("Creating datatypes for homonym checks...");
    Map<Integer, Set<String>> cuisWithTerms = ExtractCUIsAndTermsFromMRCONSO.extractCuisAndTermsAsMap(mrconsoPath);
    Collection<Set<String>> allTermsInUMLS = cuisWithTerms.values();
    Set<String> allTerms = new HashSet<String>();
    for (Set<String> valueSet: allTermsInUMLS){
      for(String value: valueSet){
        allTerms.add(value);        
      }
    }
    allTermsInUMLS = null;

    /** Add semantic types from MRSTY */
    System.out.println("Adding semantic types to concepts...");
    Map<Integer, Set<Integer>> conceptsWithSemTypes = AssignSemanticTypeToCui.getCuisWithSemanticTypes(mrstyPath);

    /** Set ontology variables*/
    WriteTextFile newOntologyFile = new WriteTextFile(ontologyPath);
    RulesCombination rulesClass = new RulesCombination(cuisWithTerms, allTerms);    
    int vocCol = 11;
    int cuiCol = 0;
    int suiCol = 5;
    int termTextCol = 14;
    int cui = -1;
    int prevCui =-1;
    int sui = -1;
    int prevSui = -1;
    int removedDueToBadSemTypeCount = 0;
    int moreThan255count = 0;
    int nonEnglishTermsCount = 0;
    int suppressableTermsCount = 0;
    int rewrittenTermsCount = 0;
    int suppressedTermsCount = 0;
    CasperConcept concept = null;

    /** Read from MRCONSO */
    System.out.println("Reading from MRCONSO.RRF... ");
    ReadTextFile textFile = new ReadTextFile(mrconsoPath);
    Iterator<String> fileIterator = textFile.getIterator();
    int lineCount = 0;
    while (fileIterator.hasNext()) {
      lineCount++;
      if (lineCount % 100000 == 0)
        System.out.println(lineCount+" lines processed from MRCONSO.RRF");
      String line = fileIterator.next();
      if (line.length() != 0) {
        String[] columns = line.split("\\|");
        String voc = columns[vocCol].trim();
        cui = Integer.parseInt(columns[cuiCol].trim().substring(1, columns[cuiCol].length()));
        Set<Integer> semtypes = conceptsWithSemTypes.get(cui);
        if (CasperFilters.notRightLanguage(columns)) {
          logFile.writeln("TERM NOT IN ENGLISH LANGUAGE|" + line);
          nonEnglishTermsCount++;
        }
        else if (CasperFilters.isMoreThan255(columns)) {
          logFile.writeln("TERM FIELD MORE THAN 255 CHARACTERS|" + line);
          moreThan255count++;
        }
        else if (CasperFilters.isSuppressable(columns)) {
          logFile.writeln("TERM MARKED AS SUPPRESSABLE BY NLM|" + line);
          suppressableTermsCount++;
        }
        else if (semanticFilter(voc, semtypes)){
          logFile.writeln("TERM REMOVED DUE TO BAD SEMANTIC TYPE|" + line);
          removedDueToBadSemTypeCount++;
        }
        else {
          cui = Integer.parseInt(columns[cuiCol].trim().substring(1, columns[cuiCol].length()));
          sui = Integer.parseInt(columns[suiCol].trim().substring(1, columns[suiCol].length()));
          String term = columns[termTextCol].trim();
          if (prevCui != cui) {
            RulesCombination.cuisWithRuleNo.clear();
          }
          prevCui = cui;
          if (prevSui != sui) {
            concept = new CasperConcept();            
            concept.setCUI(cui);
            concept.setSUI(sui);
            concept.setTermText(term);
            concept.setSemType(conceptsWithSemTypes.get(concept.getCUI()));
            boolean suppressed = false;
            if (rulesClass.conceptHasChemicalSemanticType(concept)) chemical = true;
            if(suppressRules){
              if(applySuppressRules(concept)){
                logFile.writeln("TERM REMOVED DUE TO SUPPRESS RULE|" + line);
                suppressed = true;
                suppressedTermsCount++;
              }
            }
            if (rewriteRules){
              if (!suppressed){
                if(syntacticInvRule){
                  CasperConcept rewrittenConcept = rulesClass.applySyntacticInversionRule(concept);
                  if(rewrittenConcept!=null){
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+SYN"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");                     
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+SYN"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
                if(possessivesRule){
                  CasperConcept rewrittenConcept = rulesClass.applyPossessiveRule(concept);        
                  if(rewrittenConcept!=null){
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+POS"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+POS"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
                if(shortformlongformRule){
                  List<CasperConcept> rewrittenConceptList = rulesClass.applyShortformLongformRule(concept);        
                  if(!rewrittenConceptList.isEmpty()){
//                    RulesCombination.rewrittenAndSuppressed = true;
                    for(CasperConcept rewrittenConcept: rewrittenConceptList){
                      if(suppressRules){
                        if(!applySuppressRules(rewrittenConcept)){
                          rewrittenTermsCount++;
                          newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+SFLF"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");                  
                        }
                      }else {
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+SFLF"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                      }
                    }
                  }
                }
                if(angularBracketsRule){
                  CasperConcept rewrittenConcept = rulesClass.applyAngluarBracketsRule(concept);        
                  if(rewrittenConcept!=null){
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+ANG"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");                  
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+ANG"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
                if(semanticTypesRule){
                  CasperConcept rewrittenConcept = rulesClass.applySemanticTypesRule(concept);        
                  if(rewrittenConcept!=null){
//                    RulesCombination.rewrittenAndSuppressed = true;
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+SEM"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+SEM"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
                if(beginParenthesesRule && !chemical){
                  CasperConcept rewrittenConcept = rulesClass.applyLeftSideParenthesesRule(concept);        
                  if(rewrittenConcept!=null){
//                    RulesCombination.rewrittenAndSuppressed = true;
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+BPA"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+BPA"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
                if(endParenthesesRule && !chemical){
                  CasperConcept rewrittenConcept = rulesClass.applyRightSideParenthesesRule(concept);
                  if(rewrittenConcept!=null){
//                   RulesCombination.rewrittenAndSuppressed = true;
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+EPA"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+EPA"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
                if(beginBracketsRule && !chemical){
                  CasperConcept rewrittenConcept = rulesClass.applyLeftSideBracketsRule(concept);
                  if(rewrittenConcept!=null){
 //                   RulesCombination.rewrittenAndSuppressed = true;
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+BBR"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+BBR"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
                if(endBracketsRule && !chemical){
                  CasperConcept rewrittenConcept = rulesClass.applyRightSideBracketsRule(concept);
                  if(rewrittenConcept!=null){
 //                   RulesCombination.rewrittenAndSuppressed = true;
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+EBR"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+EBR"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
                if(nonEssentialParentheticalsRule){
                  CasperConcept rewrittenConcept = rulesClass.applyNonEssentialParantheticalsRule(concept);
                  if(rewrittenConcept!=null){
  //                  RulesCombination.rewrittenAndSuppressed = true;
                    if(suppressRules){
                      if(!applySuppressRules(rewrittenConcept)){
                        rewrittenTermsCount++;
                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+NON"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                      }
                    }else {
                      rewrittenTermsCount++;
                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+NON"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
                    }
                  }
                }
//                if(endParenthesesContainsFilteredWordRule){
//                  CasperConcept rewrittenConcept = rulesClass.applyEndParenthesesContainsFilteredWordRule(concept);
//                  if(rewrittenConcept!=null){
//                    if(suppressRules){
//                      if(!applySuppressRules(rewrittenConcept)){
//                        rewrittenTermsCount++;
//                        newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+DIS"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
//                      }
//                    }else {
//                      rewrittenTermsCount++;
//                      newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"+DIS"+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+rewrittenConcept.getTermText()+"|"+columns[15]+"|"+columns[16]+"|");
//                    }
//                  }
//                }
              }
            }
            if (!suppressed){
              newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+columns[14]+"|"+columns[15]+"|"+columns[16]+"|");
            }
            prevSui = sui;
            chemical = false;
//            RulesCombination.rewrittenAndSuppressed = false;
          }
        }
      }
    }

    /**  Save to ontologyfile and log */
    System.out.println("Closing logfile and saving to new MRCONSO file: "+StringUtilities.now());
    logFile.close();
    newOntologyFile.close();
    System.out.println(removedDueToBadSemTypeCount+ " terms were removed due to bad semantic type");
    System.out.println(moreThan255count+ " terms were removed due to length > 255 characters");
    System.out.println(nonEnglishTermsCount+ " non-english terms were removed");
    System.out.println(suppressableTermsCount+ " terms marked as suppressable by NLM were removed");
    System.out.println(suppressedTermsCount+ " terms were removed by suppress rules");
    System.out.println(rewrittenTermsCount+ " terms were rewritten and added");

  }

  public static boolean applySuppressRules(CasperConcept concept){
    //Suppressrules from Casper
    if(dosagesRule){      
      if(RulesCombination.applyDosagesRule(concept)) return true;
    }
    if(atsignRule){
      if(RulesCombination.applyAtSignRule(concept)) return true;
    }
    if(shortTokenRule){
      if(RulesCombination.applyMartijnsRule(concept)) return true;
    }
    if(ecNumbersRule){
      if(RulesCombination.applyECrule(concept)) return true;
    }
    if(necRule){
      if(RulesCombination.applyNECrule(concept)) return true;
    }
    if(nosRule){
      if(RulesCombination.applyNOSrule(concept)) return true;
    }
    if(miscRule){
      if(RulesCombination.applyMiscRule(concept)) return true;
    }
    if(wordsMoreThanFiveRule && !chemical){
      if(RulesCombination.applyNoOfWordsMoreThanFiveRule(concept)) return true;
    }   
    
    //Other suppress rules
    String t = concept.getTermText();
    if(maxTermSize100Rule && !chemical){
      if (t.length() > maxtermsize) return true;
    }
    if(minTermSize3Rule){
        if (t.length() < mintermsize) return true;
    }
    if(curlyParenthesesRule){
      if (CurlyParenthesispattern.matcher(t).find() && !chemical) return true;
    }
    if(retiredPatternRule){
      if (Retiredpattern.matcher(t).find()) return true;
    }
    if(xxxPatternRule){
      if (xxxPattern.matcher(t).find()) return true;
    }
    if(proteinWeightPatternRule){
      if (proteinWeightPattern.matcher(t).matches()) return true;
    }
    
    return false;
  }
  
  private static boolean semanticFilter(String voc, Set<Integer> semtypes) {
      Iterator<Integer> semIterator = semtypes.iterator();
      while (semIterator.hasNext()){
        Integer semID = semIterator.next();
        if (filteredSemanticTypes.contains(-semID)){
          return true;
        }else if (!voc.equals("MSH") && filteredSemanticTypesNotMesh.contains(-semID)) {
          return true;
        }
      }
    return false;
  }
  private static Set<Integer> getSemanticTypesForFiltering() {
    Set<Integer> result = new TreeSet<Integer>();
    result.add(-71);
    result.add(-185);
    result.add(-78);
    result.add(-171);
    result.add(-122);
    return result;
  }

  private static Set<Integer> getSemanticTypesForFilteringNotMesh() {
    Set<Integer> result = new TreeSet<Integer>();
    result.add(-201);
    result.add(-200);
    result.add(-170);
    result.add(-97);
    result.add(-73);
    result.add(-74);
    result.add(-203);
    result.add(-79);
    result.add(-80);
    result.add(-81);
    result.add(-82);
    result.add(-83);
    result.add(-169);
    result.add(-77);
    result.add(-92);
    result.add(-93);
    result.add(-94);
    return result;
  }
}
