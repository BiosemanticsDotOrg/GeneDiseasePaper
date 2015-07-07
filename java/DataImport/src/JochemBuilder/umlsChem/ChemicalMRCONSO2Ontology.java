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

package JochemBuilder.umlsChem;

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.dataimport.UMLS.MRDEFLoader;
import org.erasmusmc.dataimport.UMLS.MRSTYLoader;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

public class ChemicalMRCONSO2Ontology {
	public void convertChemicalMRCONSO2Ontology(String MRCONSOfile, String MRSTYfile, String SRDEFfile, String LRABRfile, String MRDEFfile, String logname, String ontologyPath) {

	 System.out.println("Starting script: "+StringUtilities.now());
     
	    //Create loading log
	    List<String> log_output = new ArrayList<String>();
	   
	    Ontology newOntology = new OntologyStore();
	    
	    //Fill the ontology
	    System.out.println("Executing ChemicalFilteredMRCONSOLoader... "+StringUtilities.now());
	    ChemicalFilteredMRCONSOLoader.loadFromChemicalFilteredMRCONSO(newOntology, MRCONSOfile, log_output, LRABRfile);
	    
	    System.out.println("Executing MRSTYLoader... "+StringUtilities.now());
	    MRSTYLoader.addSemanticType(newOntology, MRSTYfile, SRDEFfile);
	       
	    System.out.println("Executing MRDEFLoader... "+StringUtilities.now());
	    MRDEFLoader.addDefinition(newOntology, MRDEFfile, log_output);

//	  Save to log
	    System.out.println("Saving to logfile "+StringUtilities.now());
	    TextFileUtilities.saveToFile(log_output, logname);
	    
	    OntologyFileLoader loader = new OntologyFileLoader();
	    loader.save((OntologyStore)newOntology, ontologyPath);
	  }

}
