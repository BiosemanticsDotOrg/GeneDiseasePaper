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

import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.StringUtilities;

public class UMLS2008Curator {

  /**
   * Perform curation according to Rob's and Kristina's script and manual analysis files
   */
  public static void main(String[] args) {
    System.out.println("Starting script "+StringUtilities.now());
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadFromPSF("/home/khettne/UMLS/2008AB/UMLS2008AB_060209.psf");
    //loader.loadFromPSF("/home/khettne/UMLS/2008AB/AllLevels/UMLS2008AB_allLevels_disease_300309.psf");
   OntologyStore newOntology = loader.ontology;
    OntologyCurator curator = new OntologyCurator("/home/public/thesauri/UMLS2008AB/UMLS_curation_file_updatedFor2008AB.txt");
    //OntologyCurator curator = new OntologyCurator("/home/public/thesauri/UMLS2008ABMDR/UMLS_curation_file_updatedFor2008AB_EU-ADR_Extended.txt");
    curator.curateAndPrepare(newOntology);
    loader.ontology = newOntology;
    loader.saveToPSF("/home/khettne/UMLS/2008AB/UMLS2008AB_curated_060209.psf");
    //loader.SaveToPSF("/home/khettne/UMLS/2008AB/AllLevels/UMLS2008AB_allLevels_disease_curated_300309.psf");
//    System.out.println("Dumping to database... " + StringUtilities.now());
//    newOntology.setName("UMLS2008AB_EUADR_disesases_300309");
//    OntologyManager ontologyManager = new OntologyManager();
//    ontologyManager.deleteOntology("UMLS2008AB_EUADR_disesases_300309");
//    ontologyManager.dumpStoreInDatabase(newOntology);  
    System.out.println("Done! "+StringUtilities.now());
  }

}
