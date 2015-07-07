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

package org.erasmusmc.medline;

import java.util.List;

import org.erasmusmc.utilities.TextFileUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class RandomPMIDSelect {

  static String source = "/home/schuemie/journals/AddedLast3days+10dec.PMIDs";
  static String target = "/home/schuemie/journals/Random2000Last3days+10dec.PMIDs";
  static int size = 2000; 
  public static void main(String[] args) {
    List<String> sourcePMIDs = TextFileUtilities.loadFromFile(source);
    WriteTextFile targetPMIDs = new WriteTextFile(target);
    for (int i = 0; i < size; i++){
      int index = (int)Math.round(Math.random() * (sourcePMIDs.size()-1));
      targetPMIDs.writeln(sourcePMIDs.get(index));  
      sourcePMIDs.remove(index);
    }    
    targetPMIDs.close();
  }
}
