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

package JochemBuilder.KEGGdrug;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.ReadTextFile;

public class MapKEGGd2InChI {
	  public OntologyStore map(OntologyStore ontology, String mapfile){
		    System.out.println("Loading ontology... ");
		    int longInchi = 0;
		    ReadTextFile mapFile = new ReadTextFile(mapfile);
		    Map<String, String> idToInchi = new HashMap<String, String>();
		    Iterator<String> iterator = mapFile.getIterator();
		    while(iterator.hasNext()){
		      String line = iterator.next();
		      String[] columns = line.split("\t");
		      String key = columns[0];
		      String value = columns[1];
		      idToInchi.put(key, value);
		    }
		    Concept concept = null;
		    Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		    while (conceptIterator.hasNext()) {
		      DatabaseID inchid = null;
		      concept = conceptIterator.next();
		      List<DatabaseID> ids = ontology.getDatabaseIDsForConcept(concept.getID());
		      for (DatabaseID id: ids){
		        if (id.database.equals("KEGD")){
		          if (idToInchi.get(id.ID)!=null){
		            inchid = new DatabaseID("INCH", idToInchi.get(id.ID));
		            if (idToInchi.get(id.ID).length()>255) longInchi++;
		          }
		        }
		      }
		      if (inchid!=null)
		        ontology.setDatabaseIDForConcept(concept.getID(), inchid);
		    }
		    System.out.println("Done. ");
		    System.out.println("No of InChi longer than 255 characters "+longInchi);
		    return ontology;
		  }

}
