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

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.HomonymAnalyzer;
import org.erasmusmc.utilities.StringUtilities;

public class ChemicalOntologyHomonymCount {
	
	public static String ontologyFile = "/home/khettne/ChEBI/ChEBICAS_curated_110809.ontology";
	public static String output = "/home/khettne/ChEBI/chebi_homonyms.txt";
		
	  public static void main(String[] args){
		    System.out.println("Starting script. " + StringUtilities.now());
		    OntologyFileLoader loader = new OntologyFileLoader();
		    OntologyStore ontology = new OntologyStore();
		    ontology = loader.load(ontologyFile);   		    
		    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		    int lineCount = 0;
		    while (conceptIterator.hasNext()) {
		      lineCount++;
		      if (lineCount % 100000 == 0)
		        System.out.println(lineCount);
		      Concept concept = conceptIterator.next();
		      if (concept.getID() > 0) {
		        Iterator<TermStore> termIterator = concept.getTerms().iterator();
		        while (termIterator.hasNext()) {
		          TermStore term = termIterator.next();
		          term.orderSensitive = true;
		          term.caseSensitive = false;
		          term.normalised = false;
		        }
		      }
		    }
		     
		    System.out.println("Analyzing homonyms. " + StringUtilities.now());
		    HomonymAnalyzer homcount = new HomonymAnalyzer();
		    
		    homcount.setOntology(ontology);
		    homcount.countHomonyms(output);
		    System.out.println("Done. " + StringUtilities.now());
		  }
}
