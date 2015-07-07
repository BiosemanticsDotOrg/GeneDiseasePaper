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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Anni;

import static Anni.LiteratureUpdateMasterScript.cooccurrenceDBName;
import static Anni.LiteratureUpdateMasterScript.genegroundhogName;
import static Anni.LiteratureUpdateMasterScript.groundhogName;
import static Anni.LiteratureUpdateMasterScript.groundhogRoot;
import static Anni.LiteratureUpdateMasterScript.indexMedline;
import static Anni.LiteratureUpdateMasterScript.ontologyName;
import static Anni.LiteratureUpdateMasterScript.temp;
import static Anni.LiteratureUpdateMasterScript.titlesDatabase;
import org.erasmusmc.databases.cooccurrenceDataBase.CooccurrenceDatabase;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.utilities.StringUtilities;

/**
 *
 * @author reinout
 */
public class ContinueFromCoOccurrenceDB extends LiteratureUpdateMasterScript {
    public static void main (String args[]) {
        System.out.println("Try to regenerate groundhog without reloading everything from web:");
        OntologyManager ontologyManager = new OntologyManager();
		Ontology ontology = ontologyManager.fetchClient(ontologyName);
        indexMedline(groundhogName, ontology, temp+"Restricted1980tillnow.PMIDs");
        
        System.out.println(StringUtilities.now() + "\tCreate cooccurrence database");
		CooccurrenceDatabase cooccurrenceDB = new CooccurrenceDatabase(groundhogRoot + cooccurrenceDBName);
		GroundhogManager groundhogManager = new GroundhogManager(groundhogRoot);
        System.out.println("GroundhogManager db listing: " + groundhogManager.getDatabaseListing());
		Groundhog groundhog = groundhogManager.getGroundhog(groundhogName);
        System.out.println("Got groundhog: " + groundhog.getName() + ", size " + groundhog.size());
		cooccurrenceDB.makeFromGroundhog(groundhog);

//		System.out.println(StringUtilities.now() + "\tCreate medline titles database");
//		CreateTitlesDatabase.create(titlesDatabase, temp+"Restricted1980tillnow.PMIDs");

		System.out.println(StringUtilities.now() + "\tDone!");
    }
}
