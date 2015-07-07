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

import org.erasmusmc.collections.MapCursor;
import org.erasmusmc.collections.SortedIntList2FloatMap;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.Ontology;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class RecordDataBaseBinding extends TupleBinding {
  public Ontology ontology;

  @Override
  public Object entryToObject(TupleInput ti) {
    int numberofentries = ti.readInt();
    SortedIntList2FloatMap map = new SortedIntList2FloatMap(numberofentries);
    //float squarednorm = ti.readFloat();
    ti.readFloat();
    for (int i = 0; i < numberofentries; i++) {
      map.addEntry(ti.readInt(), ti.readFloat());
    }
    ConceptVector conceptvector = new ConceptVector(ontology, map);
    return conceptvector;
  }

  @Override
  public void objectToEntry(Object conceptVectorObject, TupleOutput to) {
    ConceptVector conceptvector = (ConceptVector) conceptVectorObject;
    int numberofentries = conceptvector.getStoredValueCount();
    to.writeInt(numberofentries);
    if (conceptvector.isSquaredNormCalculated()) {
      to.writeFloat(conceptvector.getSquaredNorm().floatValue());
    }
    else {
      to.writeFloat(-1f);
    }
    MapCursor<Integer, Float> entrycursor = conceptvector.values.getEntryCursor();
    while (entrycursor.isValid()) {
      to.writeInt(entrycursor.key());
      to.writeFloat(entrycursor.value());
      entrycursor.next();
    }
  }

}
