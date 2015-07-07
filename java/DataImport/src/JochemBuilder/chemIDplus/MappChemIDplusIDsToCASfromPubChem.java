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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.ReadTextFile;

public class MappChemIDplusIDsToCASfromPubChem {
	  public OntologyStore run(OntologyStore ontology, String chidsFile, String casFile) {
	    List<String> chids = new ArrayList<String>();
	    ReadTextFile chemidplusIds = new ReadTextFile(chidsFile);
	    Iterator<String> chemIterator = chemidplusIds.getIterator();
	    while (chemIterator.hasNext()) {
	      String line = chemIterator.next();
	      chids.add(line);
	    }
	    Map<String, String> parsedCASnumbers = new HashMap<String, String>();
	    ReadTextFile casnumbers = new ReadTextFile(casFile);
	    Iterator<String> casIterator = casnumbers.getIterator();
	    while (casIterator.hasNext()) {
	      String cas = casIterator.next();
	      String id = cas.replaceAll("-", "");
	      int size = id.length();
	      if (size<9){
	        int numberOfZerosToAdd = 9-size;
	        int i = 0;
	        while (i<numberOfZerosToAdd){
	          id = "0"+id;
	          i++;
	        }
	      }
	      parsedCASnumbers.put(id,cas);
	    }
	   Iterator<Concept> conceptIterator = ontology.getConceptIterator();
	    while (conceptIterator.hasNext()){
	      DatabaseID newID = null;
	      Concept concept = conceptIterator.next();      
	      List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
	      for (DatabaseID databaseID: databaseIDs){
	        String db = databaseID.database;
	        if (db.equals("CHID")){
	          String id = databaseID.ID;
	          String casToAdd = parsedCASnumbers.get(id);
	          if (casToAdd!=null){
	            newID = new DatabaseID("CAS", casToAdd);
	          }
	        }
	      }
	      if (newID!=null)
	        ontology.setDatabaseIDForConcept(concept.getID(), newID);
	    }
	   return ontology;
	  }
}
