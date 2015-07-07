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

package JochemBuilder.MergeOntologies;

import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;

public class DumpJochemInDatabase {
	public static String ontologyName = "JochemV1_5";
	public static String ontologyPath = "/home/khettne/Projects/Jochem/Jochem_V1_5.ontology";

	public static void main(String[] args) {

		System.out.println("Loading ontology: "+StringUtilities.now());
		OntologyFileLoader loader = new OntologyFileLoader();
		OntologyStore ontology = loader.load(ontologyPath);
		
		System.out.println("Dumping into database: "+StringUtilities.now());

		OntologyManager ontologyManager = new OntologyManager();
		ontologyManager.dumpStoreInDatabase(ontology);  
		System.out.println("Done: "+StringUtilities.now());
	}
}
