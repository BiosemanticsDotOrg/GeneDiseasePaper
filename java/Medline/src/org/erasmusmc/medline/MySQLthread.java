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
//Usage:
//Initialize with server name or leave empty for default server
//Call start() method to start new thread
//Use GoFetch() when ready to do queries
//Use WaitUntilFetched() to pause the current thread until queries are completed
//Use Terminate() to stop thread;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.erasmusmc.concurrency.BatchProcessingThread;

public class MySQLthread extends BatchProcessingThread{
	
	public static String defaultDatabase = "Medline2014";
	public static String defaultServer = "127.0.0.1";

  public MySQLthread(String server, String database){
    Initialize(server, database);
  }
  public MySQLthread(String server, String database, String user, String password){
    Initialize(server, database,user,password);
  }
  public MySQLthread(String server){
    Initialize(server, defaultDatabase);
  }
  
  public MySQLthread(){
    Initialize(defaultServer, defaultDatabase);
  }
  
  @Deprecated
  public void GoFetch(){ //This method will be run in the other thread!
  	proceed();
  }
  
  @Deprecated
  public void WaitUntilFetched(){ //Runs in other thread!
    waitUntilFinished();
  }
  
  @Deprecated
  public synchronized void Terminate(){
  	terminate();
  }
  
  private void Initialize(String server, String database){
	    //Initialize(server, database, "root", "blabla"); // Eelke's local server through ssh tunnel
	    Initialize(server, database, "root", "");
  }
  
  private void Initialize(String server, String database, String user, String password){
    try {
      Class.forName("com.mysql.jdbc.Driver");
      String url = "jdbc:mysql://"+server+":3306/"+database;
      con = DriverManager.getConnection(url,user, password);
      stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }catch( Exception e ) {
      e.printStackTrace();
    }      
  }

  
  @Deprecated
  protected void DoQuery(){};
  
  protected void process(){
  	DoQuery();
  }
  
  private Connection con;
  protected Statement stmt;
   
  protected void finalize(){
    try {
      con.close();
    }catch( Exception e ) {
      e.printStackTrace();
    }       
  }

}
