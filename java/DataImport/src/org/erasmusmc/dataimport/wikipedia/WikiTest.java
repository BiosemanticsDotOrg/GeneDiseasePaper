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

package org.erasmusmc.dataimport.wikipedia;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.utilities.StringUtilities;

public class WikiTest {

	public static void main(String[] args) {
		System.out.println(StringUtilities.now() +"\tLoading");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadFromPSF(WikiOntology.targetOntology);
    System.out.println(StringUtilities.now() +"\tInitializing");
    int termCount = 0;
    for (Concept concept : loader.ontology){
    	for (TermStore term : concept.getTerms()){
    		term.caseSensitive = false;
    		term.orderSensitive = true;
    		term.normalised = false;
    		termCount++;
    	}
    }
    System.out.println("Concepts: " + loader.ontology.size() + ", terms: " + termCount);
    System.out.println(StringUtilities.now() +"\tReleasing");
    ConceptPeregrine peregrine = new ConceptPeregrine();
    peregrine.destroyOntologyDuringRelease = true;
    peregrine.setOntology(loader.ontology);
    peregrine.release();
    System.out.println(StringUtilities.now() +"\tDone");

	}

}
