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

package org.erasmusmc.groundhog;

import java.util.Collection;
import java.util.Map;

import org.erasmusmc.storecaching.StoreMapCaching;

public class ConceptSumOfValuesCache extends StoreMapCaching<Integer,Float> {
  ConceptToRecordIndex conceptIndex;
  public ConceptSumOfValuesCache(ConceptToRecordIndex conceptIndex){
    this.conceptIndex=conceptIndex;
  }
   @Override
  protected Float getEntryFromStoreWithID(Integer id) {
     ConceptToConceptVectorRecordIndexEntry conceptToRecordIndexEntry = conceptIndex.get(id);
     if (conceptToRecordIndexEntry == null) {
       return 0f;
     }
     else {
       return conceptToRecordIndexEntry.sumOfValuesInRecords;
     }
    
  }
  @Override
  protected Map<Integer, Float> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
    // TODO Auto-generated method stub
    return null;
  }
  @Override
  protected void setEntryInStore(Integer id, Float value) {
    // TODO Auto-generated method stub
    
  }
  @Override
  public int size() {
    // TODO Auto-generated method stub
    return 0;
  }

}
