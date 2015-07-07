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
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyClient;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.UMLSGeneChemTokenizer;
import org.erasmusmc.rmi.registry.RMIMasterServer;
import org.erasmusmc.utilities.TextFileUtilities;

/**
 * Use this class from the command line to start a Peregrine service by either using a MySQL
 * database or a PSF file containing the ontology.
 * @author erasmusmc
 *
 */
public class RMIKnewcoPeregrineServer {
  private static String servername = "localhost";
  private static String servicename = "KnewcoPeregrine";
  private static String psfFile = "../Thesauri/UMLS_Genelist_filtered_final.psf";
  private static String stopWordFile = "../Peregrine/miniStopwords.txt";
  private static String lvgPropertiesPath = "../LVG/lvg2006lite/data/config/lvg.properties";
  private static Set<String> stopWordList = new HashSet<String>();

  private static int minConceptID = 2000000;
  private static String normaliserCacheFile = "../Peregrine/standardNormcache2006.bin";
  private static int serverport = 1099;
  private static int objectport = 1100;
  private static boolean useOntologyClient = false;
  private static String server,username, password, database;
  private static int reload = 0;
  private Registry registry;

  /**
   * @param args
   */
  public static void main(String args[]) {
    List<String> arguments = new ArrayList<String>();
    for(String arg: args) {
      arguments.add(arg);
    }
    System.out.println("Parameters:");
    
    if(arguments.contains("-servername")) {
      servername = arguments.get(arguments.indexOf("-servername")+1);
      System.out.println("servername: "+ servername);
    }
    if(arguments.contains("-serverport")) {
      serverport = Integer.parseInt(arguments.get(arguments.indexOf("-serverport")+1));
      System.out.println("serverport: "+ serverport);
    }
    if(arguments.contains("-objectport")) {
      objectport = Integer.parseInt(arguments.get(arguments.indexOf("-objectport")+1));
      System.out.println("objectport: "+ objectport);
    }
    if(arguments.contains("-servicename")) {
      servicename = arguments.get(arguments.indexOf("-servicename")+1);
      System.out.println("servicename: "+ servicename);
    }
    if(arguments.contains("-stopwordsfile")) {
      stopWordFile = arguments.get(arguments.indexOf("-stopwordsfile")+1);
      System.out.println("stopWordFile: "+ stopWordFile);
    }
    if(arguments.contains("-normcachefile")) {
      normaliserCacheFile = arguments.get(arguments.indexOf("-normcachefile")+1);
      System.out.println("normaliserCacheFile: "+ normaliserCacheFile);
    }
    if(arguments.contains("-mysqlserver")) {
      useOntologyClient = true;
      server = arguments.get(arguments.indexOf("-mysqlserver")+1);
      System.out.println("mysqlserver: "+ server);
    }
    if(arguments.contains("-mysqlusername")) {
      username = arguments.get(arguments.indexOf("-mysqlusername")+1);
      System.out.println("mysqlusername: "+ username);
    }
    if(arguments.contains("-mysqlpassword")) {
      password = arguments.get(arguments.indexOf("-mysqlpassword")+1);
      System.out.println("mysqlpassword: "+ password);
    }
    if(arguments.contains("-mysqldatabase")) {
      database = arguments.get(arguments.indexOf("-mysqldatabase")+1);
      System.out.println("mysqldatabase: "+ database);
    }
    if(arguments.contains("-lvgpropertiespath")) {
      lvgPropertiesPath = arguments.get(arguments.indexOf("-lvgpropertiespath")+1);
      System.out.println("lvgpropertiespath: "+ lvgPropertiesPath);
    }
    if(arguments.contains("-periodicreload")) {
      reload = Integer.parseInt(arguments.get(arguments.indexOf("-periodicreload")+1));
      System.out.println("periodicreload: "+ reload);
    }
    if(arguments.contains("-help")) {
      usage();
      System.exit(0);
    }
    if(arguments.contains("-psffile")) {
      if(useOntologyClient) {
        System.out.println("ERR: Cannot use BOTH OnologyClient and a PSF file.");
        usage();
        System.exit(1);
      }
      psfFile = arguments.get(arguments.indexOf("-psffile")+1);
      System.out.println("psffile: "+ psfFile);
    }
    if(arguments.contains("-minconceptid")) {
      minConceptID = Integer.parseInt(arguments.get(arguments.indexOf("-minconceptid")+1));
      System.out.println("minconceptid: "+ minConceptID);
    }
    new RMIKnewcoPeregrineServer();
  }

  public RMIKnewcoPeregrineServer() {
    preliminaries();
    loadOntologyAndLaunch();
    if(reload>0){
      Timer timer = new Timer();
      ReloadPeregrine reloadP = new ReloadPeregrine(this);
      long period = 1000*60*reload;
      timer.scheduleAtFixedRate(reloadP, new Date(System.currentTimeMillis()+period), period);
    }
    System.out.println("RMIPeregrineServer up and running...");
    System.out.println("ServerAddress  : rmi://" + servername + ":" + serverport + "/"  + servicename);
  }
  private void preliminaries(){
    try {
      registry = RMIMasterServer.getRegistry(serverport, objectport, servername);
      File test = new File(normaliserCacheFile);
      if(!test.isFile()) {
        System.err.println("Cannot find cacheFile: " + normaliserCacheFile);
        System.exit(1);
      }
      stopWordList.addAll(TextFileUtilities.loadFromFile(stopWordFile));
    }catch (Exception e) {
      e.printStackTrace();
    }
  }
  private void loadOntologyAndLaunch(){
    try {    
      RMIPeregrineImplementation rmiPeregrine = new RMIPeregrineImplementation(new ConceptPeregrine(lvgPropertiesPath));
      rmiPeregrine.getPeregrine().normaliser.loadCacheBinary(normaliserCacheFile);
      rmiPeregrine.setMinGeneConceptID(minConceptID);
      rmiPeregrine.getPeregrine().stopwords = stopWordList;
      
      /*
       * settings specific for the new EU-ADR thesaurus diseases/genes/drugs
       */
      rmiPeregrine.getPeregrine().tokenizer = new UMLSGeneChemTokenizer();
      //rmiPeregrine.setGeneDisambiguator(new GeneDisambiguator((ConceptPeregrine) rmiPeregrine.getPeregrine(), 2000000, 4503386 ));
      //rmiPeregrine.setUmlsDisambiguator(new UMLSDisambiguator(0, 1999999));
      
      if(useOntologyClient) {
        Ontology ontology = new OntologyClient(server, username, password, database);
        rmiPeregrine.getPeregrine().setOntology(ontology);
      } else {     
        System.out.println("Loading ontology from PSF file");
        OntologyPSFLoader loader = new OntologyPSFLoader();
        loader.loadDefinitions   = false;
        loader.loadHierarchy     = false;
        loader.loadFromPSF(psfFile);
        OntologyCurator curator = new OntologyCurator();
        curator.curateAndPrepare(loader.ontology);
        rmiPeregrine.getPeregrine().setOntology(loader.ontology);
      }
      System.out.println("Releasing ontology");
      rmiPeregrine.release(); //Important: call this release instead of the peregrine.release() if you want to use disambiguation
      System.out.println("Rebinding");
      registry.rebind(servicename, rmiPeregrine);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void reload() {
    loadOntologyAndLaunch();
  }
  
  private static void usage() {
    System.out.println("RMIKnewcoPeregrineServer:");
    System.out.println();
    System.out.println("Usage:");
    System.out.println("    RMIPeregrineServer [-servername servername] [-serverport serverport] [-objectport objectport] [-servicename servicename] ([-psffile psffile] | [-mysqlserver mysqlserver] [-mysqlusername mysqlusername] [-mysqlpassword mysqlpassword] [-mysqldatabase mysqldatabase]) [-stopwordsfile stopwordsfile] [-minconceptid minconceptid]");
    System.out.println();
    System.out.println("      -servername servername          :   [String] Bind to this IP address, defaults to: " + servername );
    System.out.println("      -serverport serverport          :   [int]    Bind the server to this port, defaults to: " + serverport );
    System.out.println("      -objectport objectport          :   [int]    Exports objects on this port, defaults to: " + objectport );
    System.out.println("      -servicename servicename        :   [String] Sets the name for the RMIService, defaults to: " + servicename);
    System.out.println("      -psffile psffile				  :   [String] Use this PSF file as thesaurus, defaults to: " + psfFile);
    System.out.println("      -stopwordsfile stopwordsfile    :   [String] Use this file as stopword list, defaults to: " + stopWordFile);
    System.out.println("      -normcachefile normcachefile    :   [String] Use this file as normalized word list, defaults to: " + normaliserCacheFile);
    System.out.println("      -minconceptid minconceptid      :   [int]    Sets the conceptID which below is UMLS thesaurus, above are genes, defaults to: " + minConceptID);
    System.out.println("      -lvgpropertiespath lvgpropertiespath  :   [String]    Sets the path where the properties for the LVG can be found, defaults to: " + minConceptID);
    System.out.println("      -periodicreload periodicreload  :   [int]    Enable periodic reload of the psf file and sets reload time in minutes, defaults to: " + reload);
    System.out.println();
    System.out.println("		With no arguments RMIKnewcoPeregrineServer will start as follows;");
    System.out.println("		RMIPeregrineServer -servername " + servername + " -serverport " + serverport + " -objectport " + objectport + " -servicename " + servicename + " -psffile " + psfFile + " -stopwordsfile " + stopWordFile + " -normcachefile " + normaliserCacheFile + " -minconceptid " + minConceptID);
    System.out.println();
    System.out.println("		To start RMIKnewcoPeregrineServer with an OntologyClient use the following arguments (but leave out the -psffile argument)");
    System.out.println("      -mysqlserver mysqlserver        :   [String] Connect to this MySQL server");
    System.out.println("      -mysqlusername mysqlusername    :   [String] Use this username to connect to the MySQL server");
    System.out.println("      -mysqlpassword mysqlpassword	  :   [String] Use this password to connect to the MySQL server");
    System.out.println("      -mysqldatabase mysqldatabase	  :   [String] Use this database on the MySQL server");
    System.out.println();
    System.out.println("		-help displays this screen.");
    System.out.println();
    System.out.println("� ErasmusMC - Biosemantics group");
    System.out.println("http://www.biosemantics.org");
    System.out.println();
    System.out.println("Author : Antoine Veldhoven");
  }

  private final class ReloadPeregrine extends TimerTask {
    RMIKnewcoPeregrineServer server;
    //Timer timer;

    public ReloadPeregrine(RMIKnewcoPeregrineServer server) {
      System.out.println("Periodic reloadtask initialized, frequency: once every " + reload + " min.");
      this.server = server;
    }
    /**
     * Implements TimerTask's abstract run method.
     */
    public void run(){
      server.reload();
    }
  }


}


