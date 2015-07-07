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

package org.erasmusmc.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WindowsUtilities {
  public static void execute(String command, boolean showStd, boolean showErr){
    try {
      Process p = Runtime.getRuntime().exec(command);
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      
      String s;
      if (showStd)
        System.out.println("*** Standard output ***");
      while ((s = stdInput.readLine()) != null)
        if (showStd)
          System.out.println(s);
      if (showErr)
        System.out.println("*** Standard error output ***");
      while ((s = stdError.readLine()) != null)
        if (showErr)
          System.out.println(s);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
}