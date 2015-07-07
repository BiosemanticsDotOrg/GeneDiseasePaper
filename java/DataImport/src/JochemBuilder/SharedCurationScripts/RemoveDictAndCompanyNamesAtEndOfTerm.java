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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class RemoveDictAndCompanyNamesAtEndOfTerm {
	  boolean rewriteRules = true;

	  boolean pharmaceuticalCompanies = true;
	  boolean dictionariesRule = true;

	  public OntologyStore run(OntologyStore originalOntology, String logfilePath) {

	    System.out.println("Starting script: "+StringUtilities.now());

	    /** Create log */
	    WriteTextFile logFile = new WriteTextFile(logfilePath);

	    OntologyStore newOntology = new OntologyStore();

	    Set<Integer> includedCUIs = new HashSet<Integer>();

	    /** Set ontology variables*/
	    int rewrittenTermsCount = 0;
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
	        Iterator<TermStore> termIterator = terms.iterator();
	        while (termIterator.hasNext()) {
	          TermStore term = termIterator.next();          
	          if (rewriteRules){
	            if(dictionariesRule){
	              String old = term.text;
	              String rewrittenTerm = JochemCurator.rewriteNameForDictionaries(term.text);
	              if(!rewrittenTerm.equals("")){
	                rewrittenTermsCount++;
	                term.text = rewrittenTerm;
	                logFile.writeln("REWRITTEN DUE TO DICTIONARY|"+term.text+"|"+ old + "|"+concept.getID());
	              }
	            }
	            if(pharmaceuticalCompanies){
	              String old = term.text;
	              String rewrittenTerm = JochemCurator.rewriteNameForPharmas(term.text);
	              if(!rewrittenTerm.equals("")){
	                term.text = rewrittenTerm;
	                logFile.writeln("REWRITTEN DUE TO PHARMA|"+term.text+"|"+ old + "|"+concept.getID());
	              }
	            }

	          }
	        }
	      }
	 //     if (!concept.getTerms().isEmpty()) {
	        includedCUIs.add(concept.getID());
	        newOntology.setConcept(concept);
//	      }
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
	    System.out.println(rewrittenTermsCount+ " terms were rewritten");
	    return newOntology;
	  }

}
