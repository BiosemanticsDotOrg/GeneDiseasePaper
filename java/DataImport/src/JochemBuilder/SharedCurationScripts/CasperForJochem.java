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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.collections.Pair;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

import casperSoftwareCode.Rules;

public class CasperForJochem {

	  public static Set<String> stopwordsForFiltering = OntologyUtilities.stopwordsForFiltering;

	  public static boolean chemicalShortTokenRule = true;
	  public static boolean shortTokenRule = false;  
	  public static boolean dosagesRule = true;
	  public static boolean atsignRule = true;
	  public static boolean necRule = true;
	  public static boolean nosRule = true;
	  public static boolean ecNumbersRule = true;
	  public static boolean miscRule = true;

	  boolean suppressRules = true;
	  boolean rewriteRules = true;

	  boolean semanticTypesRule = true;
	  boolean syntacticInvRule = true;
	  boolean possessivesRule = true;
	  boolean shortformlongformRule = true;

	  public OntologyStore run(OntologyStore originalOntology, String logfilePath) {

	    System.out.println("Starting script: "+StringUtilities.now());

	    /** Create log */
	    WriteTextFile logFile = new WriteTextFile(logfilePath);

	    OntologyStore newOntology = new OntologyStore();
	    
	    Set<Integer> includedCUIs = new HashSet<Integer>();

	    /** Create datatypes for homonym checks */
	        System.out.println("Creating datatypes for homonym checks...");
	    Set<String> allTerms = new HashSet<String>();
	    Iterator<Concept> conceptIteratorForHomonyms = originalOntology.getConceptIterator();
	    while (conceptIteratorForHomonyms.hasNext()) {
	      Concept conceptForHomonyms = conceptIteratorForHomonyms.next();
	      if (conceptForHomonyms.getID() > 0) {        
	        Iterator<TermStore> termIteratorForHomonyms = conceptForHomonyms.getTerms().iterator();
	        while (termIteratorForHomonyms.hasNext()) {
	          TermStore term = termIteratorForHomonyms.next();
	          allTerms.add(term.text.toLowerCase());
	        }
	      }
	    }

	    /** Set ontology variables*/
	    int rewrittenTermsCount = 0;
	    int suppressedTermsCount = 0;
	    Concept concept = null;

	    System.out.println("Rewriting... ");

	    Iterator<Concept> conceptIterator = originalOntology.getConceptIterator();
	    int lineCount = 0;
	    while (conceptIterator.hasNext()) {
	      lineCount++;
	      if (lineCount % 10000 == 0)
	        System.out.println(lineCount);
	      concept = conceptIterator.next();
	      if (concept.getID() > 0) {
	        List<TermStore> terms = concept.getTerms();
	        List<TermStore> termsToAdd = new ArrayList<TermStore>();
	        Iterator<TermStore> termIterator = terms.iterator();
	        while (termIterator.hasNext()) {
	          TermStore term = termIterator.next();

	          boolean suppressed = false;            
	          if(suppressRules){
	            String termToRewrite = term.text;
	            if(applySuppressRules(termToRewrite)){
	              logFile.writeln("TERM REMOVED DUE TO SUPPRESS RULE|" + termToRewrite + "|"+concept.getName() +"|"+ concept.getID());
	              termIterator.remove();
	              suppressed = true;
	              suppressedTermsCount++;
	            }
	          }
	          if (rewriteRules){
	            if (!suppressed){
	              if(semanticTypesRule){
	                String old = term.text;
	                String rewrittenTermText = RewriteRules.findAndRewriteParenthesesWithSemanticType(term.text);
	                if(!rewrittenTermText.equals("")){
	                  if(suppressRules){
	                    if(!applySuppressRules(rewrittenTermText) && !allTerms.contains(rewrittenTermText.toLowerCase())){
	                      rewrittenTermsCount++;
	                      term.text = rewrittenTermText;
	                      logFile.writeln("REWRITTEN DUE TO SEMANTIC TYPE|"+term.text+"|"+ old + "|"+concept.getID());
	                    }
	                  }else if(!allTerms.contains(rewrittenTermText.toLowerCase())) {
	                    rewrittenTermsCount++;
	                    term.text = rewrittenTermText;
	                    logFile.writeln("REWRITTEN DUE TO SEMANTIC TYPE|"+term.text+"|"+ old + "|"+concept.getID());
	                  }
	                }
	              }
	              if(syntacticInvRule){
	                String termToRewrite = term.text;
	                String rewrittenTermText = RewriteRules.findAndRewriteSyntacticUniversion(termToRewrite);
	                if(!rewrittenTermText.equals("")){
	                  if(suppressRules){
	                    if(!applySuppressRules(rewrittenTermText)  && !allTerms.contains(rewrittenTermText.toLowerCase())){
	                      rewrittenTermsCount++;
	                      TermStore rewrittenTerm = new TermStore(rewrittenTermText);
	                      termsToAdd.add(rewrittenTerm);
	                      logFile.writeln("ADDED DUE TO SYNTACTIC INVERSION|"+rewrittenTermText+"|"+ termToRewrite + "|"+concept.getID());
	                    }
	                  }else if(!allTerms.contains(rewrittenTermText.toLowerCase())) {
	                    rewrittenTermsCount++;
	                    TermStore rewrittenTerm = new TermStore(rewrittenTermText);
	                    termsToAdd.add(rewrittenTerm);
	                    logFile.writeln("ADDED DUE TO SYNTACTIC INVERSION|"+rewrittenTermText+"|"+ termToRewrite + "|"+concept.getID());
	                  }
	                }
	              }
	              if(possessivesRule){
	                String rewrittenTermText = RewriteRules.findAndRewritePossessive(term.text);
	                if(!rewrittenTermText.equals("")){
	                  if(suppressRules){
	                    if(!applySuppressRules(rewrittenTermText) && !allTerms.contains(rewrittenTermText.toLowerCase())){
	                      rewrittenTermsCount++;
	                      TermStore rewrittenTerm = new TermStore(rewrittenTermText);
	                      termsToAdd.add(rewrittenTerm);
	                      logFile.writeln("ADDED DUE TO POSSESSIVE|"+rewrittenTermText+"|"+ term.text + "|"+concept.getID());
	                    }
	                  }else if(!allTerms.contains(rewrittenTermText.toLowerCase())){
	                    rewrittenTermsCount++;
	                    TermStore rewrittenTerm = new TermStore(rewrittenTermText);
	                    termsToAdd.add(rewrittenTerm);
	                    logFile.writeln("ADDED DUE TO POSSESSIVE|"+rewrittenTermText+"|"+ term.text + "|"+concept.getID());
	                  }
	                }
	              }
	              if(shortformlongformRule){
	                String termToRewrite = term.text;
	                List<Pair<String, String>> sflf = RewriteRules.findShortformLongformPattern(termToRewrite);
	                if (sflf!=null){
	                  List<String> rewrittenTermList  = new ArrayList<String>();
	                  rewrittenTermList.add(sflf.get(0).object1.trim());
	                  rewrittenTermList.add(sflf.get(0).object2.trim());
	                  for(String rewrittenTerm: rewrittenTermList){
	                    if(suppressRules){
	                      if(!applySuppressRules(rewrittenTerm) && !allTerms.contains(rewrittenTerm.toLowerCase())){
	                        rewrittenTermsCount++;
	                        TermStore rewrittenTermStore = new TermStore(rewrittenTerm);
	                        termsToAdd.add(rewrittenTermStore);
	                        logFile.writeln("ADDED DUE TO SHORT FORM OR LONG FORM|"+rewrittenTerm+"|"+ termToRewrite + "|"+concept.getID());
	                      }
	                    }else if(!allTerms.contains(rewrittenTerm.toLowerCase())){
	                      rewrittenTermsCount++;
	                      TermStore rewrittenTermStore = new TermStore(rewrittenTerm);
	                      termsToAdd.add(rewrittenTermStore); 
	                      logFile.writeln("ADDED DUE TO SHORT FORM OR LONG FORM|"+rewrittenTerm+"|"+ termToRewrite + "|"+concept.getID());
	                    }
	                  }
	                }
	              }
	            }
	          }
	        }
	        if (!termsToAdd.isEmpty()){
	          terms.addAll(termsToAdd);
	        }
	        concept.setTerms(terms);
	        OntologyUtilities.removeDuplicateTerms(concept.getTerms());
	      }
	      if (!concept.getTerms().isEmpty() || concept.getID() < 0) {
	        includedCUIs.add(concept.getID());
	        newOntology.setConcept(concept);
	      }
	    }
//	  Copy relationships:
	    List<Relation> relations = originalOntology.getRelations();
	    for (Relation relation: relations)
	      if (includedCUIs.contains(relation.subject) && includedCUIs.contains(relation.object))
	        newOntology.setRelation(relation);

	    // Copy databaseIDs:
	    List<DatabaseID> databaseIDs;
	    for (int cui: includedCUIs) {
	      databaseIDs = originalOntology.getDatabaseIDsForConcept(cui);
	      if (databaseIDs != null)
	        for (DatabaseID databaseID: databaseIDs)
	          newOntology.setDatabaseIDForConcept(cui, databaseID);
	    }
	    /**  Save to ontologyfile and log */
	    System.out.println("Closing logfile and saving new ontology: "+StringUtilities.now());
	    logFile.close();
	    System.out.println(suppressedTermsCount+ " terms were removed by suppress rules");
	    System.out.println(rewrittenTermsCount+ " terms were rewritten");
	    return newOntology;
	  }

	  public static boolean applySuppressRules(String term){
	    if(chemicalShortTokenRule){
	      if(JochemCurator.kristinasChemicalShortTokenFilterRule(term, stopwordsForFiltering)) return true;
	    }    
	    if(shortTokenRule){
	        if(Rules.MartijnsFilterRule(term, stopwordsForFiltering)) return true;
	      }  
	    if(dosagesRule){      
	      if(RewriteRules.findAndSuppressDosages(term)) return true;
	    }
	    if(atsignRule){
	      if(RewriteRules.findAndSuppressAtSign(term)) return true;
	    }
	    if(ecNumbersRule){
	      if(RewriteRules.findAndSuppressECnumbers(term)) return true;
	    }
	    if(necRule){
	      if(RewriteRules.findAndSuppressNEC(term)) return true;
	    }
	    if(nosRule){
	      if(RewriteRules.findAndSuppressNOS(term)) return true;
	    }
	    if(miscRule){
	      if(RewriteRules.findAndSuppressMisc(term)) return true;
	    }
	    return false;
	  }

}
