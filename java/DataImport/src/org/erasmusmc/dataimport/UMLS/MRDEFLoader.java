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

import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.ReadTextFile;

public class MRDEFLoader {

  public static void addDefinition(Ontology ontology, String filename, List<String> log_output) {

    int cuiCol = 0;
    int defCol = 5;
    String defFromMRDEF = "";
    int cui = -1;
    int previousCUI = -1;
    Concept concept = null; 

    ReadTextFile textFile = new ReadTextFile(filename);
    Iterator<String> fileIterator = textFile.getIterator();

    while (fileIterator.hasNext()) {
      String line = fileIterator.next();
      if (line.length() != 0) {
        // Process concept information line
        List<String> columns = StringUtilities.safeSplit(line, '|');
        // Save the cui as Integer, and definition as String
        cui = Integer.parseInt(columns.get(cuiCol).trim().substring(1, columns.get(cuiCol).length()));
        if (cui != previousCUI){
          previousCUI = cui;
          defFromMRDEF = columns.get(defCol).trim();
          if (defFromMRDEF.length() != 0){
            concept = ontology.getConcept(cui);
            if (concept != null){
              String definition = defFromMRDEF;
//            If longer than 1024 characters, then substring and add a full stop.
              if (!definition.endsWith(".") && definition.length()<=1024){
                definition = definition+".";
              } else if (!definition.endsWith(".") && definition.length()>1024){
                definition = definition.substring(0, 1023)+".";
              }
              defFromMRDEF = definition;
              if (concept != null && !defFromMRDEF.equals("")) {
                concept.setDefinition(defFromMRDEF);
                ontology.setConcept(concept);
              }
            }
          }
        }
      }
    }
  }  
}
