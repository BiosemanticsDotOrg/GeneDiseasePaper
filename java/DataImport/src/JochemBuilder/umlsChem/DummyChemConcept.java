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

package JochemBuilder.umlsChem;

import java.util.Set;

public class DummyChemConcept {
	  protected Integer ID;
	  protected Integer CUI;
	  protected Integer SUI;
	  protected String termText;
	  protected Set<Integer> semType;
	  
	  public void setID(Integer ID) {
	    this.ID = ID;
	  }
	  public void setCUI(Integer CUI) {
	    this.CUI = CUI;
	  }
	  public void setSUI(Integer SUI) {
	    this.SUI = SUI;
	  }
	  public void setTermText(String termText) {
	    this.termText = termText;
	  }
	  public void setSemType(Set<Integer> semType) {
	    this.semType = semType;
	  }
	  public Integer getID() {
	    return ID;
	  }
	  public Integer getCUI() {
	    return CUI;
	  }
	  public Integer getSUI() {
	    return SUI;
	  }
	  public String getTermText() {
	    return termText;
	  } 
	  public Set<Integer> getSemType() {
	    return semType;
	  }
	  

}
