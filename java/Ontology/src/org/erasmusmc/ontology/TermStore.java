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

/**
 * Class for storing information about a term.
 * @author Schuemie
 *
 */
public class TermStore  implements Serializable{
   /**
    * The text of the term.
    */
  public String text;
  
  /**
   * Should the term be matched case-sensitive?
   * <br><br>The default value is false.
   */
  public boolean caseSensitive=false;
  
  /**
   * Should the term be matched order-sensitive?
   * <br><br>The default value is false.
   */  
  public boolean orderSensitive=false;
  
  /**
   * Should the term be matched after normalisation?
   * <br><br>The default value is false.
   */  
  public boolean normalised=false;
  public TermStore(String atext){
    text = atext;
  }
  public String toString(){
    return text;
  }
  
  public TermStore copy(){
    TermStore result = new TermStore(text);
    result.caseSensitive = caseSensitive;
    result.normalised = normalised;
    result.orderSensitive = orderSensitive;
    return result;
  }
  private static final long serialVersionUID = -829354967754798748L;  
}
