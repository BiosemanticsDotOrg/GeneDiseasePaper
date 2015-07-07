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

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;

public class ConvertUMLSToOntology {
	public static String base = "/Volumes/laMulligen/Projects/Mantra/data/Terminology";
	public static String version = "2012AA";
	public static String timestamp = "091012";
	public static String baseName = "MSH_MDR_SNOMEDCT_DUT";
	public static String rewrittenMRCONSOfile = base + "/MRCONSO_" + baseName + ".RRF";
	public static String MRSTYfile = base +"/MRSTY.RRF";
	public static String SRDEFfile = base + "/SRDEF";
	public static String LRABRfile = base + "/LRABR";
	public static String MRDEFfile = base +"/MRDEF.RRF";
	public static List<String> log_output = new ArrayList<String>();
	public static String ontologyName = "UMLS" + version + "_" + baseName + "_" + timestamp;
	public static String ontologyPath = base + "/UMLS"+version + "_" + baseName + "_" + timestamp + ".ontology";

	public static void main(String[] args) {

		System.out.println("Starting script: " + StringUtilities.now() );

		Ontology newOntology = new OntologyStore();
		newOntology.setName(ontologyName);    

		//Fill the ontology
		System.out.println("Executing MRCONSOLoader... "+StringUtilities.now());
		RewrittenMRCONSOLoader.loadFromRewrittenMRCONSO(newOntology, rewrittenMRCONSOfile, log_output, LRABRfile);

		System.out.println("Executing MRSTYLoader... "+StringUtilities.now());
		MRSTYLoader.addSemanticType(newOntology, MRSTYfile, SRDEFfile);

		System.out.println("Executing MRDEFLoader... "+StringUtilities.now());
		MRDEFLoader.addDefinition(newOntology, MRDEFfile, log_output);

		OntologyFileLoader loader = new OntologyFileLoader();
		loader.save((OntologyStore)newOntology, ontologyPath);
	}

}
