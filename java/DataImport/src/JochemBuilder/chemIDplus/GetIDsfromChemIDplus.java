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

package JochemBuilder.chemIDplus;

import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class GetIDsfromChemIDplus {
	public void run(OntologyStore ontology, String outfile){
		System.out.println("Starting script: "+StringUtilities.now());
		WriteTextFile write = new WriteTextFile(outfile);
		Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		while (conceptIterator.hasNext()){
			Concept concept = conceptIterator.next();      
			List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
			for (DatabaseID databaseID: databaseIDs){
				String db = databaseID.database;
				if (db.equals("CHID")){
					write.writeln(databaseID.ID);
				}
			}
		}
		write.close();
	}
}
