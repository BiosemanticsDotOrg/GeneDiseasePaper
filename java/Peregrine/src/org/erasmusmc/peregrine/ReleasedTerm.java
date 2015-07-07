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

package org.erasmusmc.peregrine;

import java.io.Serializable;

/** Data structure created by Peregrine after release. */
public class ReleasedTerm implements Serializable{
  
  /** The concept ids of the concepts that are associated with this term */
  public int[] conceptId;
  
  /** For each of the concepts in the conceptID list, this list contains the term ID. 
   * The first term in the ontology for a concept has termID 0, the second has termID 1, etc. */
  public int[] termId;
  
  protected byte length = 0;
  protected boolean ordered = true;
  

  //modified field keeps track of when Term was last modified (this indexation, or a previous one?)
  protected int modified = 0;
  
  public void addConceptAndTermID(int cid, int tid){
    if (conceptId == null){
      conceptId = new int[1];
      conceptId[0] = cid;
    } else {
      int[] temp = new int[conceptId.length+1];
      for (int i = 0; i < conceptId.length; i++)
        temp[i] = conceptId[i];
      temp[conceptId.length] = cid;
      conceptId = temp;
    }
    
    if (termId == null){
      termId = new int[1];
      termId[0] = tid;
    } else {
      int[] temp = new int[termId.length+1];
      for (int i = 0; i < termId.length; i++)
        temp[i] = termId[i];
      temp[termId.length] = tid;
      termId = temp;
    }
  }

  
  private static final long serialVersionUID = -4285808916406972402L;
  
  public static class ReleasedTermIndexed extends ReleasedTerm {
    private static final long serialVersionUID = 5746230295685285437L;
    public int index = -1;
  }

}
