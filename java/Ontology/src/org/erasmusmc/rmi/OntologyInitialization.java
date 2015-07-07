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

package org.erasmusmc.rmi;

import java.util.List;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyClient;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;

public class OntologyInitialization {
  private String psfFile = "../Thesauri/UMLS_Genelist_filtered_final.psf";
  private String mysqlserver = "mi-bios4.erasmusmc.nl";
  private String ontologyFile = "";
  private String username = "root";
  private String database = "";
  private String password = "";
  private static enum OntologySource {none, client, psf, file};
  private OntologySource ontologySource = OntologySource.none;

  /**
   * Helper class for initalizing an ontology from psf file or MySQL database for RMI servers.
   * @param arguments
   */
  public void parseArguments(List<String> arguments){
    if(arguments.contains("-mysqlserver")) {
    	ontologySource = OntologySource.client;
      mysqlserver = arguments.get(arguments.indexOf("-mysqlserver")+1);
      System.out.println("mysqlserver: "+ mysqlserver);
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

    if(arguments.contains("-psffile")) {
      if(ontologySource != OntologySource.none) {
        System.out.println("ERR: Multiple ontology sources specified.");
        usage();
        System.exit(1);
      }
      psfFile = arguments.get(arguments.indexOf("-psffile")+1);
      System.out.println("psffile: "+ psfFile);
    }
    
    if (arguments.contains("-ontologyfile")){
      if(ontologySource != OntologySource.none) {
        System.out.println("ERR: Multiple ontology sources specified.");
        usage();
        System.exit(1);
      }
      ontologyFile = arguments.get(arguments.indexOf("-ontologyfile")+1);
      System.out.println("ontologyfile: " + ontologyFile);
    }
  }

  public void usage(){
    System.out.println();
    System.out.println("      -ontologyfile ontologyfile      :   [String] Use this ontology file as thesaurus");
    System.out.println("        or use a PSF file:");
    System.out.println("      -psffile psffile                :   [String] Use this PSF file as thesaurus, defaults to: " + psfFile);
    System.out.println("        or use an ontology in a MySQL database:");
    System.out.println("      -mysqlserver mysqlserver        :   [String] Connect to this MySQL server");
    System.out.println("      -mysqlusername mysqlusername    :   [String] Use this username to connect to the MySQL server");
    System.out.println("      -mysqlpassword mysqlpassword    :   [String] Use this password to connect to the MySQL server");
    System.out.println("      -mysqldatabase mysqldatabase    :   [String] Use this database on the MySQL server");
    System.out.println();
  }

  public void showRunningInfo(){
    if (ontologySource == OntologySource.client)
      System.out.println("Ontology       : " + database + ", from mysql://" + username + "@" + mysqlserver);
    else if (ontologySource == OntologySource.psf)
      System.out.println("Ontology       : " + psfFile);
    else if (ontologySource == OntologySource.file)
      System.out.println("Ontology       : " + ontologyFile);
  }

  public Ontology getOntology(boolean lean){
    Ontology ontology;
    if (ontologySource == OntologySource.client){
      ontology = new OntologyClient(mysqlserver, username, password, database);
    } else if (ontologySource == OntologySource.psf) {
      OntologyPSFLoader loader = new OntologyPSFLoader();
      if (lean){
        loader.loadDefinitions = false;
        loader.loadHierarchy = false;
      } else {
        loader.loadDefinitions = true;
        loader.loadHierarchy = true;
      }
      loader.loadFromPSF(psfFile);
      ontology = loader.ontology;
      OntologyCurator curator = new OntologyCurator();
      curator.curateAndPrepare(loader.ontology);
    } else {
    	OntologyFileLoader loader = new OntologyFileLoader();
    	loader.setLoadTermsOnly(lean);
    	ontology = loader.load(ontologyFile);
    }
    return ontology;
  }
}
