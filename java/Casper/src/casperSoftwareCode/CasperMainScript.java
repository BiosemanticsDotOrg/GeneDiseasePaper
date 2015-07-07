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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class CasperMainScript {

  public static boolean dosagesRule; //= true;
  public static boolean atsignRule; //= true;
  public static boolean shortTokenRule; //= true;
  public static boolean necRule; //= true;
  public static boolean nosRule; //= true;
  public static boolean ecNumbersRule; //= true;
  public static boolean miscRule; //= true;
  public static boolean wordsMoreThanFiveRule; //= false;

  String mrconsoPath;
  String mrstyPath;
  String logfilePath;
  String ontologyPath;

  boolean suppressRules; //= true;
  boolean rewriteRules; //= true;

  boolean syntacticInvRule; //= true;
  boolean possessivesRule; //= true;
  boolean shortformlongformRule; //= true;
  boolean angularBracketsRule; //= true;
  boolean semanticTypesRule; //= true;
  boolean beginParenthesesRule; //= false;
  boolean endParenthesesRule; //= false;
  boolean beginBracketsRule; //= false;
  boolean endBracketsRule; //= false;

  public static boolean chemical = false;
  
  public void run(String filename) {

    System.out.println("Starting script: "+StringUtilities.now());

    /** Set program variables */
    String iniFilePath = filename;
    ReadTextFile iniFile = new ReadTextFile(iniFilePath);
    Iterator<String> iniFileIterator = iniFile.getIterator();
    while (iniFileIterator.hasNext()) {
      String line = iniFileIterator.next();
      if (line.length() != 0 && !line.startsWith("#")) {
        String[] columns = line.split("=");
        String variable = columns[0].trim();
        /** Set paths */
        if(variable.equals("MRCONSO")) mrconsoPath = columns[1].trim();
        if(variable.equals("MRSTY")) mrstyPath = columns[1].trim();
        if(variable.equals("log")) logfilePath = columns[1].trim();
        if(variable.equals("MRCONSO rewritten and suppressed")) ontologyPath = columns[1].trim();
        /** Set variables */
        if(variable.equals("Apply suppressrules")){
          String value = columns[1].trim();
          if (value.equals("on"))
            suppressRules = true;
          else suppressRules = false;
        }
        if(variable.equals("Apply rewriterules")){
          String value = columns[1].trim();
          if (value.equals("on"))
            rewriteRules = true;
          else rewriteRules = false;
        }
        if(variable.equals("Syntactic inversion")){
          String value = columns[1].trim();
          if (value.equals("on"))
            syntacticInvRule = true;
          else syntacticInvRule = false;
        }
        if(variable.equals("Possessives")){
          String value = columns[1].trim();
          if (value.equals("on"))
            possessivesRule = true;
          else possessivesRule = false;
        }
        if(variable.equals("Short Form And Long Form")){
          String value = columns[1].trim();
          if (value.equals("on"))
            shortformlongformRule = true;
          else shortformlongformRule = false;
        }
        if(variable.equals("Angular Brackets")){
          String value = columns[1].trim();
          if (value.equals("on"))
            angularBracketsRule = true;
          else angularBracketsRule = false;
        }
        if(variable.equals("Semantic Type")){
          String value = columns[1].trim();
          if (value.equals("on"))
            semanticTypesRule = true;
          else semanticTypesRule = false;
        }
        if(variable.equals("Begin Parenthesis")){
          String value = columns[1].trim();
          if (value.equals("on"))
            beginParenthesesRule = true;
          else beginParenthesesRule = false;
        }
        if(variable.equals("End Parenthesis")){
          String value = columns[1].trim();
          if (value.equals("on"))
            endParenthesesRule = true;
          else endParenthesesRule = false;
        }
        if(variable.equals("Begin Brackets")){
          String value = columns[1].trim();
          if (value.equals("on"))
            beginBracketsRule = true;
          else beginBracketsRule = false;
        }
        if(variable.equals("End Brackets")){
          String value = columns[1].trim();
          if (value.equals("on"))
            endBracketsRule = true;
          else endBracketsRule = false;
        }
        if(variable.equals("Dosages")){
          String value = columns[1].trim();
          if (value.equals("on"))
            dosagesRule = true;
          else dosagesRule = false;
        }
        if(variable.equals("At-sign")){
          String value = columns[1].trim();
          if (value.equals("on"))
            atsignRule = true;
          else atsignRule = false;
        }
        if(variable.equals("Short token")){
          String value = columns[1].trim();
          if (value.equals("on"))
            shortTokenRule = true;
          else shortTokenRule = false;
        }
        if(variable.equals("Any classification")){
          String value = columns[1].trim();
          if (value.equals("on"))
            necRule = true;
          else necRule = false;
        }
        if(variable.equals("Any underspecification")){
          String value = columns[1].trim();
          if (value.equals("on"))
            nosRule = true;
          else nosRule = false;
        }
        if(variable.equals("EC numbers")){
          String value = columns[1].trim();
          if (value.equals("on"))
            ecNumbersRule = true;
          else ecNumbersRule = false;
        }
        if(variable.equals("Miscellaneous")){
          String value = columns[1].trim();
          if (value.equals("on"))
            miscRule = true;
          else miscRule = false;
        }
        if(variable.equals("More than five words in term")){
          String value = columns[1].trim();
          if (value.equals("on"))
            wordsMoreThanFiveRule = true;
          else wordsMoreThanFiveRule = false;
        }
      }
    }

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
    int cuiCol = 0;
    int suiCol = 5;
    int termTextCol = 14;
    int cui = -1;
    int prevCui =-1;
    int sui = -1;
    int prevSui = -1;
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
        if (CasperFilters.isMoreThan255(columns)) {
          logFile.writeln("TERM FIELD MORE THAN 255 CHARACTERS|" + line);
          moreThan255count++;
        }
        else if (CasperFilters.notRightLanguage(columns)) {
          logFile.writeln("TERMS NOT ENGLISH LANGUAGE|" + line);
          nonEnglishTermsCount++;
        }
        else if (CasperFilters.isSuppressable(columns)) {
          logFile.writeln("TERMS MARKED AS SUPPRESSABLE BY NLM|" + line);
          suppressableTermsCount++;
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
              }
            }
            if (!suppressed){
              newOntologyFile.writeln(columns[0]+"|"+columns[1]+"|"+columns[2]+"|"+columns[3]+"|"+columns[4]+"|"+columns[5]+"|"+columns[6]+"|"+columns[7]+"|"+columns[8]+"|"+columns[9]+"|"+columns[10]+"|"+columns[11]+"|"+columns[12]+"|"+columns[13]+"|"+columns[14]+"|"+columns[15]+"|"+columns[16]+"|");
            }
            prevSui = sui;
            chemical = false;
          }
        }
      }
    }

    /**  Save to ontologyfile and log */
    System.out.println("Closing logfile and saving to new MRCONSO file: "+StringUtilities.now());
    logFile.close();
    newOntologyFile.close();
    System.out.println(moreThan255count+ " terms were removed due to length > 255 characters");
    System.out.println(nonEnglishTermsCount+ " non-english terms were removed");
    System.out.println(suppressableTermsCount+ " terms marked as suppressable by NLM were removed");
    System.out.println(suppressedTermsCount+ " terms were removed by suppress rules");
    System.out.println(rewrittenTermsCount+ " terms were rewritten and added");

  }

  public static boolean applySuppressRules(CasperConcept concept){
    if(dosagesRule){      
      if(RulesCombination.applyDosagesRule(concept)) return true;
    }
    if(atsignRule){
      if(RulesCombination.applyAtSignRule(concept)) return true;
    }
    if(shortTokenRule && !chemical){
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
    return false;
  }

}
