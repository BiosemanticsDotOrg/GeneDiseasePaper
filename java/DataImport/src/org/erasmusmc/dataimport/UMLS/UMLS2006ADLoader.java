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
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

public class UMLS2006ADLoader {
  
  public static void main(String[] args) {
    
    System.out.println("Starting script: "+StringUtilities.now());
     
    //This file contains concept IDs, terms, and vocabularies
    String MRCONSOfile = "/home/public/thesauri/UMLS2006AD/META/MRCONSO.RRF";
    
    //This file contains semantic types for a concept
    String MRSTYfile = "/home/public/thesauri/UMLS2006AD/META/MRSTY.RRF";
    String SRDEFfile = "/home/public/thesauri/UMLS2006AD/NET/SRDEF";
    
    //This file contains abbreviations
    String LRABRfile = "/home/public/thesauri/UMLS2006AD/LEX/LRABR";
    
    //This file contains the concept definitions
    String MRDEFfile = "/home/public/thesauri/UMLS2006AD/META/MRDEF.RRF";
    
    //Create log
    List<String> log_output = new ArrayList<String>();
   // String logname = "/home/khettne/Toxicogenomics/Data/Indexing/UMLS_thesaurus_building/UMLS_loading_log.log";
    String logname = "/home/public/thesauri/UMLS2006AD/UMLS_loading_log.log";
    
    //Name of the database
    String dbname = "UMLSupdate";
    //System.out.println("Creating MySQL database "+StringUtilities.now());
    
    //Create new ontology
    //OntologyManager manager = new OntologyManager();
    
    //manager.deleteOntology(dbname);
    //manager.createOntology(dbname);
    //Ontology newOntology = manager.fetchClient(dbname);
    Ontology newOntology = new OntologyStore();
    newOntology.setName(dbname);    
    
    //Fill the ontology
    System.out.println("Executing MRCONSOLoader... "+StringUtilities.now());
    MRCONSOLoader.loadFromMRCONSO(newOntology, MRCONSOfile, log_output, LRABRfile);
    
    System.out.println("Executing MRSTYLoader... "+StringUtilities.now());
    MRSTYLoader.addSemanticType(newOntology, MRSTYfile, SRDEFfile);
       
    System.out.println("Executing MRDEFLoader... "+StringUtilities.now());
    MRDEFLoader.addDefinition(newOntology, MRDEFfile, log_output);

//  Save to log
    System.out.println("Saving to logfile "+StringUtilities.now());
    TextFileUtilities.saveToFile(log_output, logname);
    
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = (OntologyStore)newOntology;
    //loader.SaveToPSF("/home/khettne/Toxicogenomics/Data/Indexing/UMLS_thesaurus_building/UMLS_2006AD_beforefiltering.psf");
    loader.saveToPSF("/home/public/thesauri/UMLS2006AD/UMLS_2006AD_beforefiltering.psf");
  }
}

