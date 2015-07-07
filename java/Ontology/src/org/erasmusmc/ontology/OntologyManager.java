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

package org.erasmusmc.ontology;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.utilities.StringUtilities;

/**Used to manage ontology databases on a MySQL server*/
public class OntologyManager {
  public String server = "127.0.0.1";
  public String user = "root";
  public String password = "";
  
  /** 
   * Constructor for research purposes. Do not use.
   */
  public OntologyManager(){
    initializeConnection();
  }

  /** 
   * Constructor for research purposes. Do not use.
   */
  public OntologyManager(String server){
    this.server = server;
    initializeConnection();
  }
  
  /**
   * Constructor for connecting to the specified server running MySQL. If there is no OntologyManager database on the server, it will construct it.
   * @param server  Name of IP-address of the server running MySQL
   * @param user    Username
   * @param password    Password
   */
  public OntologyManager(String server, String user, String password){
    this.server = server;
    this.user = user;
    this.password = password;
    initializeConnection();
  }
  
  /** Creates an empty ontology database with the specified name
   * 
   * @param ontologyName    Name of the ontology.
   * @return
   */
  public OntologyClient createOntology(String ontologyName){
    try {
      stmt.execute("CREATE DATABASE " + ontologyName);
      stmt.execute("USE " + ontologyName);
      stmt.execute("CREATE TABLE concept (" +
            "conceptid INT NOT NULL," +
            "name VARCHAR(255)," +
            "definition VARCHAR(10000)," +
            "PRIMARY KEY (conceptid)" +
            ");");
      stmt.execute("CREATE TABLE term (" +
          "conceptid INT NOT NULL," +
          "termid INT UNSIGNED NOT NULL," +
          "text VARCHAR(255), " +
          "casesensitive BOOLEAN,"+
          "ordersensitive BOOLEAN,"+
          "normalised BOOLEAN,"+
          "PRIMARY KEY (conceptid, termid), " +
          "FOREIGN KEY (conceptid) REFERENCES concept(conceptid)" +
          ");");
      stmt.execute("CREATE TABLE dblink (" +
          "conceptid INT NOT NULL," +
          "dbid VARCHAR(4)," +
          "id VARCHAR(255)," +
          "PRIMARY KEY (conceptid, dbid, id)," +
          "INDEX (dbid, id)," +
          "FOREIGN KEY (conceptid) REFERENCES concept(conceptid)" +
          ");");      
      stmt.execute("CREATE TABLE relation (" +
          "relationid INT UNSIGNED NOT NULL AUTO_INCREMENT," +
          "conceptid1 INT NOT NULL," +
          "conceptid2 INT NOT NULL," +
          "relationtypeid INT UNSIGNED NOT NULL, " +
          "PRIMARY KEY (relationid), " +
          "INDEX(conceptid1), " +
          "INDEX(conceptid2), " +
          "FOREIGN KEY (conceptid1) REFERENCES concept(conceptid)," +
          "FOREIGN KEY (conceptid2) REFERENCES concept(conceptid)" +
          ");");    
      //stmt.execute("INSERT INTO "+ontologyManagerDatabase+".ontology (name) VALUES (\""+ontologyName+"\");");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return new OntologyClient(server, user, password, ontologyName);
  }
  
  /** Delete the specified ontology database.
   * 
   * @param ontologyName    Name of the ontology
   */
  public void deleteOntology(String ontologyName){
    try {
      stmt.execute("DROP DATABASE " + ontologyName);
      //stmt.execute("DELETE FROM "+ontologyManagerDatabase+".ontology WHERE name =\"" + ontologyName+"\"");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  
  /** Make a complete copy of the specified ontology.
   * 
   * @param sourceName  Name of the source ontology
   * @param targetName  Name of the new ontology
   */
  public void copyOntology(String sourceName, String targetName){
    try {
      stmt.execute("CREATE DATABASE " + targetName);
      ResultSet result = stmt.executeQuery("SHOW TABLES FROM " + sourceName);
      List<String> tables = new ArrayList<String>();
      result.beforeFirst();
      while (result.next()) tables.add(result.getString(1));
      
      for (String table : tables){  
        result = stmt.executeQuery("SHOW CREATE TABLE "+sourceName+"."+table);
        result.first();
        String createString = result.getString(2);
        createString = createString.replace("CREATE TABLE ", "CREATE TABLE "+targetName+".");
        createString = createString+ " SELECT * FROM "+sourceName+"."+table;
        stmt.execute(createString);
      }
      //stmt.execute("INSERT INTO "+ontologyManagerDatabase+".ontology (name) VALUES (\""+targetName+"\");");
    } catch (SQLException e) {
      e.printStackTrace();
    }    
  }
  
  /**
   * Generate a list of ontologies on the server.
   * @return    The list of ontologies
   */
  /*public List<String> listOntologies(){
    List<String> result = new ArrayList<String>();
    try {
      ResultSet resultset = stmt.executeQuery("SELECT name FROM "+ontologyManagerDatabase+".ontology");
      resultset.beforeFirst();
      while (resultset.next()){
        result.add(resultset.getString("name"));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }    
    return result;
  }
  */
  /**
   * Create a new ontology on the server, and store all the information from the given OntologyStore in the 
   * database. The name of the new ontology is equal to the name of the OntologyStore.
   * @param ontology
   */
  public void dumpStoreInDatabase(OntologyStore ontology){
    createOntology(ontology.getName());
    addStoreToDatabase(ontology);
  }
  
  /** 
   * Adds all the concepts in the OntologyStore to the EXISTING database with the same name as the ontologystore.
   * @param ontology
   */
  public void addStoreToDatabase(OntologyStore ontology){
    StringBuffer sql = new StringBuffer();
    try {    
      //Dump concepts
      sql.append("INSERT INTO concept (conceptid, name, definition) VALUES ");
      int batchSize = 10000;
      int count = 0;
      boolean previous = false;
      Iterator<Concept> conceptIterator = ontology.getConceptIterator();
      while (conceptIterator.hasNext()){
        Concept concept = conceptIterator.next();
        if (previous) sql.append(",");
        previous = true;
//        String definition = StringUtilities.escape(concept.getDefinition());
//        if (definition.length() > 1024) definition = definition.substring(0, 1024);
        sql.append("("+concept.getID()+",\""+StringUtilities.escape(concept.getName())+"\",\""+StringUtilities.escape(concept.getDefinition())+"\")");
        if (count == batchSize || !conceptIterator.hasNext()) {
          stmt.execute(sql.toString());
          sql = new StringBuffer();
          sql.append("INSERT INTO concept (conceptid, name, definition) VALUES ");
          previous = false;
          count = 0;
        } else count++;
      }
      
      //Dump terms
      sql = new StringBuffer();
      sql.append("INSERT INTO term (conceptid, termid, text, casesensitive, ordersensitive, normalised) VALUES ");
      conceptIterator = ontology.getConceptIterator();
      batchSize = 500;
      count = 0;   
      boolean hasTerm = false;
      previous = false;
      while (conceptIterator.hasNext()){
        Concept concept = conceptIterator.next();
        List<TermStore> terms = concept.getTerms();
        for (int i = 0; i < terms.size(); i++){
          TermStore term = terms.get(i);
          if (previous) 
            sql.append(",");
          previous = true;
          if (term.text.length()>255)
        	  System.out.println(concept.getID()+ "\tterm longer than 255 characters: "+term.text);
          else if (StringUtilities.escape(term.text).length()>255)
        	  System.out.println(concept.getID()+ "\tescapedterm longer than 255 characters: "+StringUtilities.escape(term.text));
          sql.append("("+concept.getID()+","+i+",\""+StringUtilities.escape(term.text)+"\","+term.caseSensitive+","+term.orderSensitive+","+term.normalised+")");
          hasTerm = true;
        }
        if (count == batchSize || !conceptIterator.hasNext()) {
          if (hasTerm){
            stmt.execute(sql.toString());
            sql = new StringBuffer();
            sql.append("INSERT INTO term (conceptid, termid, text, casesensitive, ordersensitive, normalised) VALUES ");
            previous = false;
          }
          count = 0;
        } else count++;      
      }
      
      //Dump relations
      sql = new StringBuffer();
      sql.append("INSERT INTO relation (conceptid1, conceptid2, relationtypeid) VALUES ");
      batchSize = 100000;
      count = 0;     
      previous = false;
      List<Relation> relations = ontology.getRelations();
      for (int i = 0; i < relations.size(); i++){
        Relation relation = relations.get(i);
        if (previous) sql.append(",");
        previous = true;
        sql.append("("+relation.subject+","+relation.object+","+relation.predicate+")");
        if (count == batchSize || i == relations.size()-1) {
          stmt.execute(sql.toString());
          sql = new StringBuffer();
          sql.append("INSERT INTO relation (conceptid1, conceptid2, relationtypeid) VALUES ");
          count = 0;
          previous = false;
        } else count++;           
      }
      
      //Dump DBlinks    
      sql = new StringBuffer();
      sql.append("INSERT IGNORE INTO dblink (conceptid, dbid, id) VALUES ");
      conceptIterator = ontology.getConceptIterator();
      batchSize = 100000;
      count = 0;      
      previous = false;
      while (conceptIterator.hasNext()){
        Concept concept = conceptIterator.next();
        List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
        if (databaseIDs != null)
          for (int i = 0; i < databaseIDs.size(); i++){
            DatabaseID databaseID = databaseIDs.get(i);
            if (previous) sql.append(",");
            previous = true;
            sql.append("("+concept.getID()+",\""+databaseID.database+"\",\""+databaseID.ID+"\")");
            count++;
          }
        if (count > batchSize || (!conceptIterator.hasNext() && count != 0)) {
          stmt.execute(sql.toString());
          sql = new StringBuffer();
          sql.append("INSERT IGNORE INTO dblink (conceptid, dbid, id) VALUES ");
          count = 0;
          previous = false;
        }      
      }      
      
    } catch (SQLException e) {
      e.printStackTrace();
    }       
  }
  
  /**
   * Creates an OntologyClient connected the MySQL database.
   * @param name    The name of the ontology
   * @return    The OntologyClient
   */
  public OntologyClient fetchClient(String name){
    return new OntologyClient(server, user, password, name);
  }

  /**
   * Retrieves all the information from an ontology in the MySQL database and stores it in an OntologyStore
   * @param name    Name of the ontology
   * @return    The OntologyStore
   */
  public OntologyStore fetchStoreFromDatabase(String name){
    OntologyStore ontology = new OntologyStore();
    try{
      stmt.execute("USE " + name);
      // Get concepts      
      int batchsize = 10000;
      int offset = 0;
      boolean done = false;
      while (!done) {
        ResultSet result = stmt.executeQuery("SELECT * FROM concept LIMIT "+offset+","+batchsize);
        offset += batchsize;
        result.beforeFirst();
        done = true;
        while (result.next()){
          done = false;
          Concept concept = new Concept(result.getInt("conceptid"));
          concept.setName(StringUtilities.unescape(result.getString("name")));
          concept.setDefinition(StringUtilities.unescape(result.getString("definition").replace("\n", "")));
          ontology.setConcept(concept);
        }
      }
      
      // Get terms      
      batchsize = 10000;
      offset = 0;
      done = false;
      while (!done) {
        ResultSet result = stmt.executeQuery("SELECT * FROM term LIMIT "+offset+","+batchsize);
        offset += batchsize;
        result.beforeFirst();
        done = true;
        while (result.next()){
          done = false;
          Concept concept = ontology.getConcept(result.getInt("conceptid"));
          TermStore term = new TermStore(StringUtilities.unescape(result.getString("text")).replace("\n", ""));
          term.caseSensitive = result.getBoolean("casesensitive");
          term.orderSensitive = result.getBoolean("ordersensitive");
          term.normalised = result.getBoolean("normalised");
          if (concept.terms == null) concept.terms = new ArrayList<TermStore>();
          concept.terms.add(term);
        }
      }     
            
      // Get relations 
      Set<Integer> usedTypes = new TreeSet<Integer>();
      batchsize = 10000;
      offset = 0;
      done = false;
      while (!done) {
        ResultSet result = stmt.executeQuery("SELECT * FROM relation LIMIT "+offset+","+batchsize);
        offset += batchsize;
        result.beforeFirst();
        done = true;
        while (result.next()){
          done = false;
          Integer type = result.getInt("relationtypeid");
          usedTypes.add(type);
          Relation relation = new Relation(result.getInt("conceptid1"), type, result.getInt("conceptid2"));
          ontology.setRelation(relation);
        }
      }     
      
      // Get DBlinks 
      batchsize = 10000;
      offset = 0;
      done = false;
      while (!done) {
        ResultSet result = stmt.executeQuery("SELECT * FROM dblink LIMIT "+offset+","+batchsize);
        offset += batchsize;
        result.beforeFirst();
        done = true;
        while (result.next()){
          done = false;
          DatabaseID databaseID = new DatabaseID(result.getString("dbid"), result.getString("id"));
          ontology.setDatabaseIDForConcept(result.getInt("conceptid"), databaseID);
        }
      }          
    } catch (SQLException e) {
      e.printStackTrace();
    }     
    return ontology;
  }
  
  private void initializeConnection(){
    try {
      Driver drv = Class.forName("com.mysql.jdbc.Driver").asSubclass(Driver.class).newInstance();
      String url = "jdbc:mysql://"+server+":3306/";
      //DriverManager.registerDriver(drv);
      //con = DriverManager.getConnection(url,user, password);
      Properties props = new Properties();
      props.put("user", "root");
      //props.put("password", "blabla"); // Eelke's server
      props.put("password", "");
      //Driver drv = DriverManager.getDriver(url);
      con = drv.connect(url, props);
      stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
    }catch( Exception e ) {
      e.printStackTrace();
    }    
  }
  /*
  private void createOntologyManagerDatabase() {
    try {
      stmt.execute("CREATE DATABASE "+ontologyManagerDatabase);
      stmt.execute("USE " + ontologyManagerDatabase);
      stmt.execute("CREATE TABLE ontology (" +
          "name VARCHAR(255) NOT NULL," +
          "PRIMARY KEY (name)" +
      ");");  
      stmt.execute("CREATE TABLE externaldatabase (" +
          "dbid INT UNSIGNED NOT NULL,"+
          "name VARCHAR(255)," +
          "url VARCHAR(255) NOT NULL," +
          "PRIMARY KEY (dbid)" +
      ");");          
    }catch( Exception e ) {
      e.printStackTrace();
    }        
  }
*/
  private Connection con;
  //private String ontologyManagerDatabase = "ontologymanager";
  protected Statement stmt;
  
  protected void finalize(){
    try {
      con.close();
    }catch( Exception e ) {
      e.printStackTrace();
    }       
  }  
}
