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

package org.erasmusmc.databases;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class BatchNumberAndIntegerIDBinding extends TupleBinding {

  @Override
  public Object entryToObject(TupleInput arg0) {
    BatchwiseIntegerID result = new BatchwiseIntegerID();
    result.batchID=arg0.readInt();
    result.ID=arg0.readInt();
    return result;
  }

  @Override
  public void objectToEntry(Object arg0, TupleOutput arg1) {
    BatchwiseIntegerID entry = (BatchwiseIntegerID)arg0;
    arg1.writeInt(entry.batchID);
    arg1.writeInt(entry.ID);
  }

}
