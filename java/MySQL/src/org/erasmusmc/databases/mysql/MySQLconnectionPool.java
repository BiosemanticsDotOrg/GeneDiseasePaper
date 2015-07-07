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

package org.erasmusmc.databases.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MySQLconnectionPool {
  public MySQLconnectionPool(String url, String user, String password, String name) {
    this.url = url;
    this.user = user;
    this.password = password;
    this.name = name;
  }

  public Connection getConnection() {
    try {
      available.acquire();
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return getNextAvailableItem();
  }

  public void returnConnection(Connection connection) {
    returnItem(connection);
    available.release();
  }

  public synchronized Connection getNextAvailableItem() {
    Connection result = null;

    for (Con con: pool) {
      if (!con.inUse) {
        con.inUse = true;
        if (System.currentTimeMillis() - con.lastUsed > timeOut){
          con.connection = createConnection(); //connection timed out: create new one
          System.out.println("Connection time out: creating new connection");
        }
        result = con.connection;
        break;
      }
    }

    if (result == null)
      if (pool.size() < maxPoolSize) {
        result = createConnection();
        
        Con con = new Con();
        con.inUse = true;
        con.connection = result;
        pool.add(con);
      }

    return result;
  }
  
  public synchronized void setDatabase(String name){
    for (Con con : pool)
      setDatabaseName(con.connection);
  }

  private Connection createConnection() {
    Connection connection = null;
    try {
      connection = DriverManager.getConnection(url, user, password);
      setDatabaseName(connection);
    } catch (SQLException e) {
      e.printStackTrace();
    }    
    return connection;
  }

  private void setDatabaseName(Connection connection) {
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
      stmt.execute("USE " + name);
      stmt.close();
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }    
  }

  private void returnItem(Connection connection) {
    for (Con con: pool)
      if (con.connection == connection){
        con.inUse = false;
        con.lastUsed = System.currentTimeMillis();
      }  
  }
  
  protected void finalize(){
    for (Con con: pool)
      try {
        con.connection.close();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
  }

  private String user;
  private String url;
  private String password;
  private String name;
  private final int maxPoolSize = 25;
  private final int timeOut = 5*60*1000;
  private final Semaphore available = new Semaphore(maxPoolSize, true);
  private List<Con> pool = new ArrayList<Con>();

  private class Con {
    Connection connection;
    boolean inUse = false;
    long lastUsed = 0;
  }

}
