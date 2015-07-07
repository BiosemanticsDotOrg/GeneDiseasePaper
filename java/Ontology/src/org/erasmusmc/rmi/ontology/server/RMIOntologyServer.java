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

package org.erasmusmc.rmi.ontology.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.rmi.OntologyInitialization;
import org.erasmusmc.rmi.RMIServer;

/** Class for running a RMI ontology service on a server */
public class RMIOntologyServer extends RMIServer {
  
  private OntologyInitialization ontologyInitialization = new OntologyInitialization();
  
  protected String getName(){
    return "Ontology";
  }

  protected void processAdditionalArguments(List<String> arguments) { 
    ontologyInitialization.parseArguments(arguments);
  }

  protected Remote instantiateService() throws RemoteException{
    Ontology ontology = ontologyInitialization.getOntology(false);
    RMIOntologyImplementation service = new RMIOntologyImplementation(ontology);
    return service;
  }

  protected void showAdditionalRunningInfo() {
    ontologyInitialization.showRunningInfo();
  }

  protected void showAdditionalUsage() {
    ontologyInitialization.usage();
  }
}
