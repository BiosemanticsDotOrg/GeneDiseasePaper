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

package org.erasmusmc.dataimport.genes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyPSFLoader;

public class GeneID2CID extends HashMap<String, Integer>{
  public void loadFromOntology(String filename, String idPrefix){
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadFromPSF(filename);
    Iterator<Concept> iterator = loader.ontology.getConceptIterator();
    while (iterator.hasNext()){
      Concept concept = iterator.next();
      List<DatabaseID> databaseIDs = loader.ontology.getDatabaseIDsForConcept(concept.getID());
      if (databaseIDs != null)
        for (DatabaseID databaseID : databaseIDs)
          if (databaseID.database.equals(idPrefix))
            put(databaseID.ID, concept.getID());
    }
  }
  
  private static final long serialVersionUID = 1L;
}
