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

package org.erasmusmc.ontology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores the information pertaining a concept
 * @author Schuemie
 *
 */
public class Concept implements Serializable {
	public static enum DisambiguationType {loose, strict};
	private static final long serialVersionUID = -6706166740204675277L;
  protected Integer ID;
  protected String name;
  protected String definition;
  protected List<TermStore> terms;
  protected DisambiguationType disambiguationType;
  
  /**
   * Returns the terms associated with this concept. Returns an empty list if no terms are found.
   * @return    The list of terms.
   */
  public List<TermStore> getTerms(){
    if (terms == null) 
      return new ArrayList<TermStore>();
    else
      return terms;
  }
  
  /**
   * Sets the list of terms
   * @param terms
   */
  public void setTerms(List<TermStore> terms){
    this.terms = terms;
  }

  public Concept(Integer ID) {
    this.ID = ID;
  }

  /**
   * Returns the concept ID.
   * @return
   */
  public Integer getID() {
    return ID;
  }
  
  public void setID(Integer value){
  	ID = value;
  }
  
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    if (name == null) {
      if (terms == null || terms.size() == 0)
        return ID.toString();
      else
        return terms.get(0).text;
    } else return name;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  /**
   * Returns the definition of the concept. Returns an empty string if no definition is set.
   * @return    The definition
   */
  public String getDefinition() {
    if (definition == null) return ""; else return definition;
  }
  
  public void setDisambiguationType(DisambiguationType type){
  	disambiguationType = type;
  }
  
  public DisambiguationType getDisambiguationType(){
  	return disambiguationType;
  }

  public String toString() {
    return getName();
  }
}
