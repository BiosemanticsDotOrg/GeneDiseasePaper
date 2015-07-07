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
import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.utilities.StringUtilities;

public class FetchPMIDs {
	
	public static void main(String[] args){
		List<Integer> pmids = getPMIDs("1980-01-01", "3000-12-31");
		System.out.println(pmids.size());
	}
	
	/**
	 * Returns the PMIDs with publication dates between the start and end date
	 * @param start	Start date (inclusive), e.g. "1975-08-05"
	 * @param end		End date (exclusive). e.g. "1999-12-31"
	 * @return List of PMIDs
	 */
  public static List<Integer> getPMIDs(String start, String end){
  	List<Integer> pmids = new ArrayList<Integer>();
    Statement stmt = getStatement();
    int offset = 0;
    int batchsize = 500000;
    boolean done = false;
    while (!done){
      String SQL = "SELECT pmid FROM medline_citation WHERE pub_date >= '"+start+"' AND pub_date < '"+end+"' LIMIT "+offset+","+batchsize;
      System.out.println(SQL);
      ResultSet rs;
      try {
        rs = stmt.executeQuery(SQL);
        int count = 0;
        rs.beforeFirst();
        while (rs.next()){
          count++;
          int pmid = rs.getInt("pmid");
          pmids.add(pmid);
        }   
        if (count < batchsize)
          done = true;
        offset += batchsize;
        //System.out.println(StringUtilities.now() + " retrieved " + offset);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return pmids;
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
