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

import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.erasmusmc.rmi.registry.RMIMasterServer;
import org.erasmusmc.utilities.StringUtilities;


public class RMIServer {
  private String servername;
  private String servicename;
  private int serverport;
  private int objectport;
  private Registry registry;
  private int reload = 0;
  
  public static void main(String[] args) {
    RMIServer server = new RMIServer();
    server.init(args);
  }

  public void init(String[] args){
    List<String> arguments = new ArrayList<String>();
    for (String arg: args) {
      arguments.add(arg);
    }
    init(arguments);
  }
  
  public void init(List<String> arguments){
    System.out.println("Initializing " + getName());
    System.out.println();
    if (arguments.contains("-servername")) {
      servername = arguments.get(arguments.indexOf("-servername") + 1);
    } else
      servername = getServername();
    if (arguments.contains("-serverport")) {
      serverport = Integer.parseInt(arguments.get(arguments.indexOf("-serverport") + 1));
    }
    if (arguments.contains("-objectport")) {
      objectport = Integer.parseInt(arguments.get(arguments.indexOf("-objectport") + 1));
    }
    if (arguments.contains("-servicename")) {
      servicename = arguments.get(arguments.indexOf("-servicename") + 1);
    }
    if(arguments.contains("-periodicreload")) {
      reload = Integer.parseInt(arguments.get(arguments.indexOf("-periodicreload")+1));
      System.out.println("periodicreload: "+ reload);
    }
    if (arguments.contains("-help") || arguments.size() == 0) {
      usage();
      System.exit(0);
    }
    processAdditionalArguments(arguments);
    getRegistry();
    startTimer();
    startService();
  }
  

  private String getServername() {
    try {
      return java.net.InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    return "localhost";
  }

  private void getRegistry() {
    try {
      registry = RMIMasterServer.getRegistry(serverport, objectport, servername);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  private void startTimer() {
    if(reload>0){
      Timer timer = new Timer();
      ReloadServer reloadP = new ReloadServer(this);
      long period = 1000*60*reload;
      timer.scheduleAtFixedRate(reloadP, new Date(System.currentTimeMillis()+period), period);
    } 
  }

  private void startService() {
    try {
      Remote service = instantiateService();
      registry.rebind(servicename, service);
    } catch (RemoteException e) {
      e.printStackTrace();
    }
    System.out.println();
    System.out.println(getName()+" up and running...");
    String tempname = servername;
    if (tempname == null)
      tempname = "localhost";   
    System.out.println("ServerAddress  : rmi://" + tempname + ":" + serverport + "/"  + servicename);
    showAdditionalRunningInfo();
  }

  private void usage() {
    System.out.println(getName());
    System.out.println("");
    System.out.println("Usage:");
    System.out.println("    "+getName()+" [-option1 argument1] [-option2 argument2] ...");
    System.out.println("");
    System.out.println("    options:");
    System.out.println("      -servername servername          :   [String] Bind to this IP address");
    System.out.println("      -serverport serverport          :   [int]    Bind the server to this port");
    System.out.println("      -objectport objectport          :   [int]    Exports objects on this port");
    System.out.println("      -servicename servicename        :   [String] Sets the name for the RMIService");
    System.out.println("      (-periodicreload periodicreload):   [int]    Enable periodic reload of the service, sets reload time in minutes. Reload is off by default");
    showAdditionalUsage();
    System.out.println("");
    System.out.println("(c) ErasmusMC - Biosemantics group");
    System.out.println("http://www.biosemantics.org");
    System.out.println("");
    System.out.println("Author : Martijn Schuemie");
  }
  protected String getName(){
    return "ConceptMapper";
  }

  protected void processAdditionalArguments(List<String> arguments) { }

  protected Remote instantiateService() throws RemoteException{
    return null;
  }

  protected void showAdditionalRunningInfo() {}

  protected void showAdditionalUsage() {}
  
  private final class ReloadServer extends TimerTask {
    RMIServer server;
    //Timer timer;

    public ReloadServer(RMIServer server) {
      System.out.println("Periodic reloadtask initialized, frequency: once every " + reload + " min.");
      this.server = server;
    }
    /**
     * Implements TimerTask's abstract run method.
     */
    public void run(){
      System.out.println("Restarting server at " + StringUtilities.now());
      server.startService();
    }
  }
}
