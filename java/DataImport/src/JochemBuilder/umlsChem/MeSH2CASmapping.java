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

import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.WriteTextFile;

public class MeSH2CASmapping {
	public void getMeSH2CASmappings(String outfile, String meshhOntology, String meshsOntology) {
		WriteTextFile writeFile = new WriteTextFile(outfile);
		OntologyStore ontology = new OntologyStore();
		OntologyFileLoader loader = new OntologyFileLoader();
		ontology = loader.load(meshhOntology);
		Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		while (conceptIterator.hasNext()){
			StringBuffer out = new StringBuffer();
			Concept concept = conceptIterator.next();
			List<DatabaseID> dbIds = ontology.getDatabaseIDsForConcept(concept.getID());
			if (dbIds.size()>1){
				for (DatabaseID id: dbIds){
					String db = id.database;
					String dbid = id.ID;
					if (db.equals("CAS")){
						out.append(dbid+"|");
					} else if (db.equals("MESH")){
						out.append(dbid);
					}
				}
				writeFile.writeln(out.toString());
			}
		}
		OntologyStore ontology2 = new OntologyStore();
		OntologyFileLoader loader2 = new OntologyFileLoader();
		ontology2 = loader2.load(meshsOntology);
		Iterator<Concept> conceptIterator2 = ontology2.getConceptIterator();
		while (conceptIterator2.hasNext()){
			StringBuffer out = new StringBuffer();
			Concept concept = conceptIterator2.next();
			List<DatabaseID> dbIds = ontology2.getDatabaseIDsForConcept(concept.getID());
			if (dbIds.size()>1){
				for (DatabaseID id: dbIds){
					String db = id.database;
					String dbid = id.ID;
					if (db.equals("CAS")){
						out.append(dbid+"|");
					} else if (db.equals("MESH")){
						out.append(dbid);
					}
				}
				writeFile.writeln(out.toString());
			}
		}    
		writeFile.close();
	}
}
