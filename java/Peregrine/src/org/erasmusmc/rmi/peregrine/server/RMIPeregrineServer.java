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

package org.erasmusmc.rmi.peregrine.server;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.LookupPeregrine;
import org.erasmusmc.rmi.OntologyInitialization;
import org.erasmusmc.rmi.RMIServer;
import org.erasmusmc.utilities.TextFileUtilities;

public class RMIPeregrineServer extends RMIServer {

  private OntologyInitialization ontologyInitialization = new OntologyInitialization();
  private String stopWordFile;
  private String lvgPropertiesPath;
  private String peregrineType;
  private int minConceptID = 2000000;
  private String normaliserCacheFile;


  protected String getName(){
    return "Peregrine";
  }

  protected void processAdditionalArguments(List<String> arguments) { 
    ontologyInitialization.parseArguments(arguments);
    if(arguments.contains("-stopwordsfile")) {
      stopWordFile = arguments.get(arguments.indexOf("-stopwordsfile")+1);
      System.out.println("stopWordFile: "+ stopWordFile);
    }
    if(arguments.contains("-normcachefile")) {
      normaliserCacheFile = arguments.get(arguments.indexOf("-normcachefile")+1);
      System.out.println("normaliserCacheFile: "+ normaliserCacheFile);
    }
    if(arguments.contains("-lvgpropertiespath")) {
      lvgPropertiesPath = arguments.get(arguments.indexOf("-lvgpropertiespath")+1);
      System.out.println("lvgpropertiespath: "+ lvgPropertiesPath);
    }
    if(arguments.contains("-minconceptid")) {
      minConceptID = Integer.parseInt(arguments.get(arguments.indexOf("-minconceptid")+1));
      System.out.println("minconceptid: "+ minConceptID);
    }
    if(arguments.contains("-peregrinetype")) {
      peregrineType = arguments.get(arguments.indexOf("-peregrinetype")+1);
      System.out.println("peregrinetype: "+ peregrineType);
    }
  }

  protected Remote instantiateService() throws RemoteException{
    Ontology ontology = ontologyInitialization.getOntology(true);

    RMIPeregrineImplementation rmiPeregrine = null;
    if (peregrineType == null || peregrineType.toLowerCase().equals("conceptperegrine")){
      rmiPeregrine = new RMIPeregrineImplementation(new ConceptPeregrine(lvgPropertiesPath));
      rmiPeregrine.setMinGeneConceptID(minConceptID);
    } else if (peregrineType.toLowerCase().equals("lookupperegrine"))
      rmiPeregrine = new RMIPeregrineImplementation(new LookupPeregrine(lvgPropertiesPath));

    File test = new File(normaliserCacheFile);
    if(!test.isFile()) {
      System.err.println("Cannot find cacheFile: "+normaliserCacheFile);
      System.exit(1);
    }
    rmiPeregrine.getPeregrine().normaliser.loadCacheBinary(normaliserCacheFile);
    
    test = new File(stopWordFile);
    if(!test.isFile()) {
      System.err.println("Cannot find stopwords file: "+stopWordFile);
      System.exit(1);
    }
    rmiPeregrine.getPeregrine().stopwords.addAll(TextFileUtilities.loadFromFile(stopWordFile));
    
    rmiPeregrine.setOntology(ontology);
    rmiPeregrine.release();
    return rmiPeregrine;
  }

  protected void showAdditionalRunningInfo() {
    ontologyInitialization.showRunningInfo();
  }

  protected void showAdditionalUsage() {
    ontologyInitialization.usage();
    System.out.println("      -stopwordsfile stopwordsfile    :   [String] Use this file as stopword list");
    System.out.println("      -normcachefile normcachefile    :   [String] Use this file as normalized word list");
    System.out.println("      (-minconceptid minconceptid)    :   [int]    Sets the conceptID which below is UMLS thesaurus, above are genes. (Default = "+minConceptID+")");
    System.out.println("      -lvgpropertiespath lvgpropertiespath  :   [String] Sets the path where the properties for the LVG can be found");
    System.out.println("      (-peregrinetype peregrinetype)  :   [String] conceptperegrine(default) or lookupperegrine");
  }
}