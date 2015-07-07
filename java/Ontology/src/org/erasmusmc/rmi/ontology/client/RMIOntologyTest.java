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

package org.erasmusmc.rmi.ontology.client;

import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;

public class RMIOntologyTest {
  public static void main(String[] args) throws Exception {
    RMIOntology rmiOntology = new RMIOntology("mojojojo.biosemantics.org", 1011, "RMIOntologyServerService");
    //RMIOntology rmiOntology = new RMIOntology("mi-bios2.erasmusmc.nl");
    //rmiOntology.getRelationsForConceptAsObject(-116);
    Iterator<Concept> conceptIterator = rmiOntology.getConceptIterator();
    Integer i=0;
    while(conceptIterator.hasNext()) {
      i++;
      Concept concept = conceptIterator.next();
      if(concept == null) System.exit(0);
      //Test every function with this concept
      //Concept concept2 = rmiOntology.getConcept(concept.getID());
      //System.out.println(concept.getName() + "("+concept.getID()+")");
      
      List<DatabaseID> dbids = rmiOntology.getDatabaseIDsForConcept(concept.getID());

      for (DatabaseID id : dbids)
        rmiOntology.getConceptIDs(id);
      rmiOntology.getRelationsForConceptAsSubject(concept.getID());
      
      rmiOntology.size();
      
      if(i%10 == 0) System.out.println(i);
    }
  }
}
