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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FetchYearthread extends MySQLthread{
  
  public List<Integer> pmids;
  public int year;
  

  public FetchYearthread(String server, String database, String user, String password){
    super(server, database, user, password);
  }
  public FetchYearthread(){
    super();
  }

  protected void DoQuery(){
    pmids = new ArrayList<Integer>();
    
    try {
      ResultSet rs = stmt.executeQuery("select PMID from year where year = "+year);
      rs.beforeFirst();
      while (rs.next()){
        pmids.add(Integer.parseInt(rs.getString("PMID")));
      }
    }catch( Exception e ) {
      e.printStackTrace();
    } 
  } 
}