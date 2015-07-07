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

package Anni;

import java.io.File;

import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.databases.integersetstore.IntegerSetStore;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogManager;
import org.erasmusmc.utilities.ReadTextFile;

public class CreateIntegerSetStoreFromGroundhog {
  public static void create(String source, String target, String cuisFile){
    GroundhogManager Gmanager = new GroundhogManager("");
    Groundhog groundhog = Gmanager.getGroundhog(source);
    File folder = new File(target);
    if (!folder.exists())
      folder.mkdir();
    IntegerSetStore integerSetStore = new IntegerSetStore(folder);
    ReadTextFile in = new ReadTextFile(cuisFile);
    for (String cui : in){
      Integer i = Integer.parseInt(cui);
      SortedIntListSet pmids = groundhog.getRecordIDsForConcept(i);
      if (pmids.size() > 0) {
        integerSetStore.set(i, pmids);
      }
    }
    //integerSetStore.close();
  }
}
