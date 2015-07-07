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

package org.erasmusmc.dataimport.Medline.util;
import java.sql.*;
import java.util.*;

import org.erasmusmc.dataimport.Medline.xmlparsers.medline.ParseAll;

public class BioTextDBConnection {

  /* Database connection specific parameters, defined in the files
   * config.properties
   */

  private ResourceBundle rb = ResourceBundle.getBundle("org.erasmusmc.dataimport.Medline.util.config");

  private String driverName = rb.getString("driverName");
  private String host = rb.getString("host");
  private String port = rb.getString("port");
  private String dbname = rb.getString("dbname");
  private String user = rb.getString("user");
  private String passwd = rb.getString("passwd");
  private String schema = rb.getString("schema");
  private String urlprefix = rb.getString("urlprefix");

  private Connection dbConnection;

  public BioTextDBConnection() throws Exception{
    connect();
    setSchema(schema);
  }

  public BioTextDBConnection(String schema) throws Exception {
    connect();
    setSchema(schema);
  }

  public void connect() throws Exception {
    if (ParseAll.database != null)
      dbname = ParseAll.database;
    if (ParseAll.server != null)
      host = ParseAll.server;
    if (ParseAll.port != null)
      port = ParseAll.port;
    if (ParseAll.username != null)
      user = ParseAll.username;
    if (ParseAll.password != null)
      passwd = ParseAll.password;
    
    try {
      Class.forName(driverName);
      System.err.println("Opening db connection");
      String url = urlprefix + host + ":" + port + "/" + dbname;
      System.out.println(url + "\t" + user + "\t" + passwd);
      dbConnection = DriverManager.getConnection(url, user, passwd);


    }
    catch (ClassNotFoundException ex) {
      System.err.println("Cannot find the database driver classes.");
      throw new Exception (ex.getMessage());
    }
    catch (SQLException ex) {
      System.err.println("Cannot connect to this database.");
      throw new Exception (ex.getMessage());
    }
  }

  public void setSchema(String schema) throws Exception {

    if (schema != null) {
      try {
        Statement stmt = dbConnection.createStatement();
        //stmt.executeUpdate("set schema " + schema);
        stmt.close();
        this.schema = schema;
      }
      catch (SQLException e) {
        throw new Exception ("Cannot set the current schema to " + schema);
      }
    }
  }


  public Connection getConnection() {

    return dbConnection;
  }

  /*public void finalize() {
        try {
            dbConnection.close();
        }
        catch (SQLException e) {
            System.err.println("Unable to close the connection to the Database");
        }
    }*/
}
