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

//Usage:
//This class can be used in two ways: multi-threaded or non-multithreaded:
//
//Multithreaded:
//Call Start() method
//Put SQL string in sql field
//Call GoFetch() to start query (in other thread)
//Call WaitUntilFetched() to stop the current thread until query is finished
//Get results from result field
//When done querying: call Terminate() method to kill thread
//
//Non-multithreaded:
//Call nonThreadedQuery with SQL sting as parameter and resultset as result

import java.sql.ResultSet;
import java.sql.SQLException;


public class MySQLgenericQuery extends MySQLthread{
  public MySQLgenericQuery(String server, String database, String user, String password){
    super(server, database,user,password);
  }
  
  public ResultSet nonThreadedQuery(String sql){
    this.sql = sql;
    DoQuery();
    return result;    
  }
  
  public void nonThreadedUpdate(String sql){
    this.sql = sql;
    DoUpdate();
  }
  
  public String sql = "";
  public ResultSet result; 
  
  public void DoQuery(){
    try {
      result = stmt.executeQuery(sql);
    } catch (SQLException e) {
      System.err.println(sql);
      e.printStackTrace();
    }
  }
  
  public void DoUpdate(){
    try {
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
}
