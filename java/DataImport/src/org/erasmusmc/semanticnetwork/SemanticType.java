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

package org.erasmusmc.semanticnetwork;

import java.io.Serializable;

import org.erasmusmc.semanticnetwork.SemanticGroup;

/**
 * <p>Title: ACS Viewer</p>
 * <p>Description: A viewer to visualize Associative Concept Spaces</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC, Medical Informatics</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class SemanticType implements Serializable{
  public int ID = -1;
  public String name = "";
  public String description = "";
  public SemanticType parent = null;
  public SemanticGroup group = null;

  @Override
public String toString() {
    return name;
  }
  public SemanticType(){}
  
  public SemanticType(int ID, String name){
    this.ID = ID;
    this.name = name;
  }
  private static final long serialVersionUID = 1L;
}