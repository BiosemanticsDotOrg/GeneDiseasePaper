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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.ReadTextFile;

public class MRSTYLoader {
  
  public static void addSemanticType(Ontology ontology, String mrstyFile, String srdefFile) {
    
    int cui = -1;
    int cuiCol = 0;
    int semID = -1;
    int semNameCol = 3;
    int semIDCol = 1;
    int semDefCol = 4;
    String semName = "";
    String semDef = "";    
    
    //Assign every semantic type a definition
    ReadTextFile srdefTextFile = new ReadTextFile(srdefFile);
    Map<Integer, String> semanticTypes = new HashMap<Integer, String>();
    Iterator<String> srdefFileIterator = srdefTextFile.getIterator();
    
    while (srdefFileIterator.hasNext()) {
      String srdefLine = srdefFileIterator.next();
      if (srdefLine.length() != 0) {
        List<String> columns = StringUtilities.safeSplit(srdefLine, '|');
        semID = Integer.parseInt(columns.get(semIDCol).trim().substring(1, columns.get(semIDCol).length()));
        semDef = columns.get(semDefCol).trim();
        semanticTypes.put(semID, semDef);
      }
    }    
    
    //Connect concepts with semantic types
    ReadTextFile mrstyTextFile = new ReadTextFile(mrstyFile);
    Iterator<String> mrstyFileIterator = mrstyTextFile.getIterator();
    
    while (mrstyFileIterator.hasNext()) {
      String mrstyLine = mrstyFileIterator.next();
      if (mrstyLine.length() != 0) {
        // Process concept information line
        List<String> columns = StringUtilities.safeSplit(mrstyLine, '|');
        // Save the cui and semID as Integers, and semName as String
        cui = Integer.parseInt(columns.get(cuiCol).trim().substring(1, columns.get(cuiCol).length()));
        semID = Integer.parseInt(columns.get(semIDCol).trim().substring(1, columns.get(semIDCol).length()));
        semName = columns.get(semNameCol).trim();
        // If the concept is found in ontology, proceed to set the semantic type
        if (ontology.getConcept(cui) != null) {
          // check if already in semantic network of ontology
          Concept semtype = ontology.getConcept(-semID);
          if (semtype == null) {            
            semtype = new Concept(-semID);
            semtype.setDefinition(semanticTypes.get(semID));
            semtype.setName(semName);
            ontology.setConcept(semtype);
          }
          Relation relation = new Relation(cui, DefaultTypes.isOfSemanticType, -semID);
          ontology.setRelation(relation);
        }
      }
    }
  }
}
