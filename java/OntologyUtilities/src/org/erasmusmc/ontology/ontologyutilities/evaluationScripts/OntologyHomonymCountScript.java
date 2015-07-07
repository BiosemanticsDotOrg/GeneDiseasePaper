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

package org.erasmusmc.ontology.ontologyutilities.evaluationScripts;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.ontologyutilities.HomonymAnalyzer;
import org.erasmusmc.utilities.StringUtilities;

public class OntologyHomonymCountScript{

	public static String outputFile = "/home/khettne/UMLS/2008AB/homonyms_umls2008AB.txt";
	public static String ontologyPath = "/home/khettne/UMLS/2008AB/UMLS2008AB_060209.ontology";

	public static String normaliserCacheFile = "/home/public/Peregrine/standardNormCache2006.bin";

	public static void main(String[] args){
		System.out.println("Starting script. " + StringUtilities.now());
		System.out.println("Loading ontology. " + StringUtilities.now());
		
		OntologyFileLoader loader = new OntologyFileLoader();
		Ontology ontology = loader.load(ontologyPath);

		System.out.println("Analyzing homonyms. " + StringUtilities.now());
		HomonymAnalyzer homcount = new HomonymAnalyzer();
		homcount.destroyOntologyDuringRelease = false;
		homcount.normaliser.loadCacheBinary(normaliserCacheFile);
		homcount.setOntology(ontology);
		homcount.countHomonyms(outputFile);
		System.out.println("Done. " + StringUtilities.now());
	}
}
