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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class MRRELLoader {
  static public boolean useBroaderThenRelations = false;
  static public boolean filterVocabularies = true;
  static public Set<String> vocFilter = loadVocFilter();
  static int cuiFromCol = 4;
  static int cuiToCol = 0;
  static int relIDCol = 3;
  static String parentRelationID = "PAR";
  static String broaderthenRelationID = "RB";

  public static void addParentRelations(Ontology ontology, String mrrelFile) {
    ReadTextFile mrrelHandle = new ReadTextFile(mrrelFile);
    Iterator<String> it = mrrelHandle.iterator();
    int count = 0;
    while (it.hasNext()) {
      String line = it.next();
      if (line.length() != 0) {
        List<String> columns = StringUtilities.safeSplit(line, '|');
        String relID = columns.get(relIDCol).trim();
        if (relID.equalsIgnoreCase(parentRelationID) || (useBroaderThenRelations && relID.equalsIgnoreCase(broaderthenRelationID))) {
          if (!filterVocabularies || vocFilter.contains(columns.get(10).trim())) {
            Integer cuiFrom = Integer.parseInt(columns.get(cuiFromCol).trim().substring(1, columns.get(cuiFromCol).length()));
            Integer cuiTo = Integer.parseInt(columns.get(cuiToCol).trim().substring(1, columns.get(cuiToCol).length()));
            Concept cFrom = ontology.getConcept(cuiFrom);
            Concept cTo = ontology.getConcept(cuiTo);
            if (cFrom != null && cTo != null && cFrom != cTo) {
              Relation relation = new Relation(cuiFrom, DefaultTypes.isParentOf, cuiTo);
              ontology.setRelation(relation);
              //System.out.println( relation.toString());
              count++;
            }
          }
        }
      }

    }
    System.out.println("Added " + count + " relations to " + ontology.getName());

  }

  private static Set<String> loadVocFilter() {
    Set<String> vocs = new HashSet<String>();
    // alle vocs staan aangegeven hier, heel vies selecteren via aan en uitvinken.
    // vocs.add("HCPCS");
    // vocs.add("CTCAE");
    // vocs.add("ICD9CM");
    // vocs.add("CSP");
    // vocs.add("AIR");
    // vocs.add("HL7V2.5");
    // vocs.add("USPMG");
    // vocs.add("NCBI");
    // vocs.add("NCI");
    // vocs.add("NDFRT");
    // vocs.add("LNC");
    // vocs.add("CCS");
    // vocs.add("CST");
    // vocs.add("AOD");
    // vocs.add("AOT");
    // vocs.add("PDQ");
    vocs.add("MSH");
    vocs.add("GO");
    // vocs.add("HL7V3.0");
    // vocs.add("UWDA");
    // vocs.add("ICPC");
    // vocs.add("PNDS");

    return vocs;
  }
}
