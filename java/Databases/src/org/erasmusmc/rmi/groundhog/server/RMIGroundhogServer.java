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

package org.erasmusmc.rmi.groundhog.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.erasmusmc.rmi.RMIServer;

public class RMIGroundhogServer extends RMIServer {
  private String groundhog;
  private boolean allowSetting;
  
  protected String getName(){
    return "Groundhog";
  }

  protected void processAdditionalArguments(List<String> arguments) {
    if(arguments.contains("-groundhog")) {
      groundhog = arguments.get(arguments.indexOf("-groundhog")+1);
      System.out.println("groundhog: "+ groundhog);
    }
    if(arguments.contains("-allowsetting")) {
      allowSetting = Boolean.parseBoolean(arguments.get(arguments.indexOf("-allowsetting")+1));
      System.out.println("allowsetting: "+ allowSetting);
    }
  }

  protected Remote instantiateService() throws RemoteException{
    return new RMIGroundhogImplementation(groundhog,allowSetting);
  }

  protected void showAdditionalRunningInfo() {
    System.out.println("Groundhog      : " + groundhog);
  }

  protected void showAdditionalUsage() {
    System.out.println("      -groundhog groundhog            :   [String] Path to the groundhog");
    System.out.println("      (-allowsetting allowsetting)    :   [Boolean] Allow groundhog to be modified (true/false)");
  }
}
