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

package org.erasmusmc.ontology.ontologyutilities.evaluationScripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.ontologyutilities.FamilyNameFinder;
import org.erasmusmc.utilities.TextFileUtilities;

public class FamilyNameFinderScript {
  public static void main(String[] args) {
    FamilyNameFinder familyNameFinder = new FamilyNameFinder();
   // OntologyManager ontologyManager = new OntologyManager();
   // Ontology ontology = ontologyManager.fetchStoreFromDatabase("Homologene_curated_min3_291206");
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.loadFromPSF("/home/schuemie/Leiden/ecoli/ecoli.psf");
    Ontology ontology = loader.ontology;
    Map<String, Integer> familyNames = familyNameFinder.findFamilyNames(ontology);
    List<String> lines = new ArrayList<String>();
    for (Entry<String, Integer> family: familyNames.entrySet()) {
      lines.add(family.getKey()+ "\t" + family.getValue().toString());
    }
    TextFileUtilities.saveToFile(lines, "/home/temp/ECOliNames.txt");
  }
}
