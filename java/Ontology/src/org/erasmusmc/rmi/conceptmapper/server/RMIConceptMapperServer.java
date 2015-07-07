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

package org.erasmusmc.rmi.conceptmapper.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.erasmusmc.rmi.RMIServer;

public class RMIConceptMapperServer extends RMIServer {
  private String mappingfile;
  
  protected String getName(){
    return "ConceptMapper";
  }

  protected void processAdditionalArguments(List<String> arguments) {
    if (arguments.contains("-mappingfile")) {
      mappingfile = arguments.get(arguments.indexOf("-mappingfile") + 1);
    }
  }

  protected Remote instantiateService() throws RemoteException{
    return new RMIConceptMapperImplementation(mappingfile);
  }

  protected void showAdditionalRunningInfo() {
    System.out.println("Mapping file   : " + mappingfile);
  }

  protected void showAdditionalUsage() {
    System.out.println("      -mappingfile mappingfile          :   [String] Name of the file containing the concept mapping information");
  }
  
}
