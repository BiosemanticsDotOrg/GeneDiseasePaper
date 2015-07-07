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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class FetchAllPMIDsFromMedlineDatabase {
  
  public static void save(String filename){
    WriteTextFile file = new WriteTextFile(filename);
    Statement stmt = getStatement();
    int offset = 0;
    int batchsize = 100000;
    boolean done = false;
    while (!done){
      String SQL = "SELECT pmid FROM medline_citation LIMIT "+offset+","+batchsize;
      System.out.println(SQL);
      ResultSet rs;
      try {
        rs = stmt.executeQuery(SQL);
        int count = 0;
        rs.beforeFirst();
        while (rs.next()){
          count++;
          String pmid = rs.getString("pmid");
          file.writeln(pmid);
        }   
        if (count < batchsize)
          done = true;
        offset += batchsize;
        System.out.println(StringUtilities.now() + "\t" + offset);
        file.flush();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    file.close();
  }

    private static Statement getStatement(){
      Statement stmt = null;
      try {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://mi-bios1.erasmusmc.nl:3306/Medline_2012";
        Connection con = DriverManager.getConnection(url,"root", "21**");
        stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      }catch( Exception e ) {
        e.printStackTrace();
      }   
      return stmt;
    }
}
