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

package org.erasmusmc.dataimport;

/* [RvS] Comment out this class because it's causing compilation problems and 
 * no other classes depend on it
 */

/*
public class UpdateSemantictypes {
  
  public static void main(String[] args) {
    Ontology ontology = new OntologyClient("mi-bios4", "root", "21**", "Anni");
    SemanticNetwork semanticNetwork = new SemanticNetwork();
    
    semanticNetwork.loadDefaultsFromFile();
    for(SemanticType semanticType:semanticNetwork.types.values()) {
      Concept concept = new Concept(-semanticType.ID);
      concept.setDefinition(semanticType.description);
      concept.setName(semanticType.name);
      ontology.setConcept(concept);
      
      
    }
  }
  

}
*/
