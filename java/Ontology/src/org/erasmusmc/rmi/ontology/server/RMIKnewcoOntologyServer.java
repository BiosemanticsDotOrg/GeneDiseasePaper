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

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.erasmusmc.rmi.registry.RMIMasterServer;

/** Class for running a RMI ontology service on a server */
public class RMIKnewcoOntologyServer {
  private static String psfFile = "../Thesauri/UMLS_Genelist_filtered_final.psf";
  private static String servername = "localhost";
  private static String servicename = "KnewcoOntology";
  private static int serverport = 1199;
  private static int objectport = 1200;
  private static int reload = 0;
  private Registry registry;

  /**
   * Creates a RMI ontology service using an OntologyClient connection to a psf
   * file.
   */
  public RMIKnewcoOntologyServer() {
    preliminaries();
    loadOntologyAndLaunch();
    if (reload > 0) {
      Timer timer = new Timer();
      ReloadPeregrine reloadP = new ReloadPeregrine(this);
      long period = 1000 * 60 * reload;
      timer.scheduleAtFixedRate(reloadP, new Date(System.currentTimeMillis() + period), period);
    }
    System.out.println("RMIOntologyServer up and running...");
    System.out.println("ServerAddress  : rmi://" + servername + ":" + serverport + "/" + servicename);
    System.out.println("Ontology       : " + psfFile);
  }

  private void preliminaries() {
    try {
      registry = RMIMasterServer.getRegistry(serverport, objectport, servername);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   */
  public void reload() {
    // System.out.println("reloading ...");
    // System.out.println(TestHyperG.measuremem());
    loadOntologyAndLaunch();
    // System.out.println(TestHyperG.measuremem());

  }

  private void loadOntologyAndLaunch() {
    try {
      RMIOntologyInterface rmiOntology = new RMIOntologyImplementation(psfFile);
      registry.rebind(servicename, rmiOntology);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Trouble: " + e);
    }

  }

  /**
   * @param args
   */
  public static void main(String args[]) {
    ArrayList<String> arguments = new ArrayList<String>();
    for (String arg: args) {
      arguments.add(arg);
    }
    if (arguments.contains("-servername")) {
      servername = arguments.get(arguments.indexOf("-servername") + 1);
    }
    if (arguments.contains("-serverport")) {
      serverport = Integer.parseInt(arguments.get(arguments.indexOf("-serverport") + 1));
    }
    if (arguments.contains("-objectport")) {
      objectport = Integer.parseInt(arguments.get(arguments.indexOf("-objectport") + 1));
    }
    if (arguments.contains("-servicename")) {
      servicename = arguments.get(arguments.indexOf("-servicename") + 1);
    }
    if (arguments.contains("-help")) {
      usage();
      System.exit(0);
    }
    if (arguments.contains("-psffile")) {
      psfFile = arguments.get(arguments.indexOf("-psffile") + 1);
    }
    if (arguments.contains("-periodicreload")) {
      reload = Integer.parseInt(arguments.get(arguments.indexOf("-periodicreload") + 1));
    }

    new RMIKnewcoOntologyServer();
  }

  private static void usage() {
    System.out.println("RMIOntologyServer:");
    System.out.println("");
    System.out.println("Usage:");
    System.out.println("    RMIOntologyServer name");
    System.out.println("");
    System.out.println("      name       :   [String] fileName of the Ontology psfFile");
    System.out.println("");
    System.out.println("(c) ErasmusMC - Biosemantics group");
    System.out.println("http://www.biosemantics.org");
    System.out.println("");
    System.out.println("Author : Antoine Veldhoven");
  }

  /**
   * @author erasmusmc
   * 
   */
  public final class ReloadPeregrine extends TimerTask {
    RMIKnewcoOntologyServer server;
    Timer timer;

    /**
     * @param server
     */
    public ReloadPeregrine(RMIKnewcoOntologyServer server) {
      System.out.println("Periodic reloadtask initialized, frequency: once every " + reload + " min.");
      this.server = server;
    }

    /**
     * Implements TimerTask's abstract run method.
     */
    public void run() {
      server.reload();
    }
  }

}
