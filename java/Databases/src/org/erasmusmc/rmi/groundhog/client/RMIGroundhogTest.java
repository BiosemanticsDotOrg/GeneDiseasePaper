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

package org.erasmusmc.rmi.groundhog.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.erasmusmc.ontology.ConceptProfile;


public class RMIGroundhogTest {
  public static void main(String[] args) throws Exception {
    RMIConceptGroundhog rmiGroundhog = new RMIConceptGroundhog("mojojojo.biosemantics.org", 1011, "RMIGroundhogServerService");
    //System.out.println(rmiGroundhog.size());
    
    for(int i=0;i<1;i++) {
      ArrayList<Integer> list = new ArrayList<Integer>();
      list.add(4461);
      list.add(1216);
    Map<Integer, ConceptProfile> cvrs = rmiGroundhog.getConceptProfiles(list);
    for(ConceptProfile cvr: cvrs.values()) {
    Iterator<Integer> it = cvr.conceptVector.values.keyIterator();
    while(it.hasNext()) {
      Integer floatEntry = it.next();
      System.out.println(floatEntry);
    }
    }
    }
    
    

  }

}
