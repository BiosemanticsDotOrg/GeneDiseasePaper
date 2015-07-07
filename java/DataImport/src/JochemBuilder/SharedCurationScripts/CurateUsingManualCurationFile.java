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

import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class CurateUsingManualCurationFile {

	  public static boolean chemicalMiscTermRule = true;

	  boolean suppressRules = true;

	  public OntologyStore run(OntologyStore ontology, String logfilePath, String termsToRemove) {

	    System.out.println("Starting script: "+StringUtilities.now());

	    /** Create log */
	    WriteTextFile logFile = new WriteTextFile(logfilePath);

	    int suppressedTermsCount = 0;

	    System.out.println("Suppressing... ");
	    
	    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
	    int lineCount = 0;
	    while (conceptIterator.hasNext()) {
	      lineCount++;
	      if (lineCount % 10000 == 0)
	        System.out.println(lineCount);
	      Concept concept = conceptIterator.next();
	      if (concept.getID() > 0) {
	        List<TermStore> terms = concept.getTerms();
	        Iterator<TermStore> termIterator = terms.iterator();
	        while (termIterator.hasNext()) {
	          TermStore term = termIterator.next();
	          if(suppressRules){
	            if(applySuppressRules(term.text, termsToRemove)){
	              logFile.writeln(term.text + "|"+concept.getName() +"|"+ concept.getID());
	              termIterator.remove();
	              suppressedTermsCount++;
	            }
	          }        
	        }
	        OntologyUtilities.removeDuplicateTerms(concept.getTerms());
	      }
	    }
	    logFile.close();
	    return ontology;
	  }

	  public static boolean applySuppressRules(String term, String termsToRemove){
	    if(chemicalMiscTermRule){
	      if(JochemCurator.findAndSuppressChemicalMisc(term, termsToRemove)) return true;
	    }
	    return false;
	  }

}
