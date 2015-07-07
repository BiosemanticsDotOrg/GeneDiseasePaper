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

package org.erasmusmc.applications.indexer;

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.medline.MedlineRecord;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.ConceptVectorRecord;
import org.erasmusmc.groundhog.Groundhog;


public class IndexerUpdateThreadForGroundhog extends Thread {
  public List<MedlineRecord> records = new ArrayList<MedlineRecord>(0);
  public List<ConceptVector> conceptVectors;
  public Groundhog groundhog;
  
  @Override
public void run(){
    
    for (int i = 0; i < records.size(); i++){
        ConceptVectorRecord record = new ConceptVectorRecord(records.get(i).pmid);
        record.setConceptVector(conceptVectors.get(i));
        groundhog.saveConceptVectorRecord(record);
    }
  }  
}
