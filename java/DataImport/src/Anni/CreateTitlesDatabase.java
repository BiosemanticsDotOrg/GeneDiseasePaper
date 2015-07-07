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

package Anni;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.medline.MedlineIterator;
import org.erasmusmc.medline.MedlineListener;
import org.erasmusmc.medline.MedlineRecord;

public class CreateTitlesDatabase implements MedlineListener{
  public static void create(String databaseName, String pmidsFilename){
    CreateTitlesDatabase script = new CreateTitlesDatabase(databaseName, pmidsFilename);
  }
  public String server = "mi-bios4";
  public String user = "root";
  public String password = "21**";
  private Connection con;
  protected Statement stmt;
  
  public CreateTitlesDatabase(String databaseName, String pmidsFilename){
    try {
      Class.forName("com.mysql.jdbc.Driver");
      String url = "jdbc:mysql://"+server+":3306/";
      con = DriverManager.getConnection(url,user, password);
      stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);      
      stmt.execute("CREATE DATABASE " + databaseName);
      stmt.execute("USE " + databaseName);
      stmt.execute("CREATE TABLE titles (" +
          "pmid INT NOT NULL," +
          "title TEXT," +
          "PRIMARY KEY (pmid)" +
      ");");
    }catch( Exception e ) {
      e.printStackTrace();
    }    
    MedlineIterator iterator = new MedlineIterator();
    iterator.fetchMesh = false;
    iterator.pmidsFile = pmidsFilename;
    iterator.iterate(this);
  }

  @Override
public void processMedlineRecords(List<MedlineRecord> records) {
    StringBuilder sql = new StringBuilder();
    sql.append("INSERT INTO titles (pmid,title) VALUES ");
    Iterator<MedlineRecord> iterator = records.iterator();
    while (iterator.hasNext()) {
      MedlineRecord record = iterator.next();
      sql.append("(");
      sql.append(record.pmid);
      sql.append(",\"");
      sql.append(record.title.replace("\"", "\\\""));
      sql.append("\")");
      if (iterator.hasNext())
        sql.append(",");
    }
    try {
      stmt.execute(sql.toString());
    } catch (SQLException e) {
      System.err.println(sql.toString());
      e.printStackTrace();
    }
  }
}
