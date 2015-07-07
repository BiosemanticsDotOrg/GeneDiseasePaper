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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.erasmusmc.collections.IntList;
import org.erasmusmc.databases.mysql.MySQLconnectionPool;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.rmi.ontology.conceptrelations.ConceptRelation;
import org.erasmusmc.storecaching.StoreMapCaching;
import org.erasmusmc.utilities.StringUtilities;

/**
 * Ontology implementation for accessing the ontology stored in a MySQL database.
 * @author Schuemie
 *
 */
public class OntologyClient extends Ontology {
  private MySQLconnectionPool connectionPool;
  private String name = "";
  private ConceptCache conceptCache;
  private DatabaseIDCache databaseIDCache;
  
  /**
   * Constructor. Creates the connection to the MySQL database.
   * @param server  The name or IP address of the server.
   * @param user    The username.
   * @param password    The password
   * @param name    The name of the ontology
   */
  public OntologyClient(String server, String user, String password, String name){
    try {
      Class.forName("com.mysql.jdbc.Driver");
      String url = "jdbc:mysql://"+server+":3306/?autoReconnect=true";
      connectionPool = new MySQLconnectionPool(url,user, password, name);
      conceptCache = new ConceptCache(connectionPool);
      databaseIDCache = new DatabaseIDCache(connectionPool);
    }catch( Exception e ) {
      e.printStackTrace();
    }    
  }  
  
  @Override
  public Map<Integer, Concept> getConceptSubset(int offset, int limit) {
    Connection connection = connectionPool.getConnection();
    List<Integer> ids = new ArrayList<Integer>();
    try {
      Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      ResultSet result = statement.executeQuery("SELECT conceptid FROM concept LIMIT "+offset+","+limit);       
      result.beforeFirst();
      while (result.next()){
        ids.add(result.getInt("conceptid"));
        
      }
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      connectionPool.returnConnection(connection);
    }    
    return getConcepts(ids, connectionPool);
  }
  
  @Override
  public Concept getConcept(int id) {
    return conceptCache.get(id);
  }
  
  @Override
  public Map<Integer, Concept> getConcepts(Collection<Integer> ids) {
    return conceptCache.getEntriesFromStoreWithIDs(ids);
  }
  
  @Override
  public void setConcept(Concept concept) {
    conceptCache.set(concept.getID(), concept);
  }
  
  @Override
  public String getName() {
    return name;
  }
  
  @Override
  public void setName(String name) {
    this.name = name;
    connectionPool.setDatabase(name);
  }
  
  @Override
  public int size() {
    return conceptCache.size();
  }
  
  @Override
  public List<Relation> getRelationsForConceptAsObject(int id) {
    Connection connection = connectionPool.getConnection();
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      ResultSet results = stmt.executeQuery("SELECT * FROM relation WHERE conceptid2="+id);
      List<Relation> result = resultset2RelationList(results);
      stmt.close();
      return result;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      connectionPool.returnConnection(connection);
    }
  }

  @Override
  public List<Relation> getRelationsForConceptAsObject(int id, int relationtype) {
    Connection connection = connectionPool.getConnection();
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      ResultSet results = stmt.executeQuery("SELECT * FROM relation WHERE conceptid2="+id+" AND relationtypeid="+relationtype);
      List<Relation> result = resultset2RelationList(results);
      stmt.close();
      return result;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      connectionPool.returnConnection(connection);
    }
  }
  
  @Override
  public List<Relation> getRelationsForConceptAsSubject(int id) {
    Connection connection = connectionPool.getConnection();
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      ResultSet results = stmt.executeQuery("SELECT * FROM relation WHERE conceptid1="+id);
      List<Relation> result = resultset2RelationList(results);
      stmt.close();
      return result;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      connectionPool.returnConnection(connection);
    }
  }
  
  @Override
  public List<Relation> getRelationsForConceptAsSubject(int id, int relationtype) {
    Connection connection = connectionPool.getConnection();
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      ResultSet results = stmt.executeQuery("SELECT * FROM relation WHERE conceptid1="+id+" AND relationtypeid="+relationtype);
      List<Relation> result = resultset2RelationList(results);
      stmt.close();
      return result;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      connectionPool.returnConnection(connection);
    }
  }
  
  @Override
  public List<Relation> getRelations() {
    Connection connection = connectionPool.getConnection();
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      ResultSet results = stmt.executeQuery("SELECT * FROM relation");
      List<Relation> result = resultset2RelationList(results);
      stmt.close();
      return result;
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    } finally {
      connectionPool.returnConnection(connection);
    }
  }
  
  @Override
  public void setRelation(Relation relation) {
    Connection connection = connectionPool.getConnection();
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
      stmt.execute("INSERT INTO relation (conceptid1, conceptid2, relationtypeid) VALUES ("+relation.subject+","+relation.object+","+relation.predicate+")");
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      connectionPool.returnConnection(connection);
    }
  }
  
  
  @Override
  public Iterator<Concept> getConceptIterator() {
    return new ConceptIterator(connectionPool);
  }
    
  @Override
  public void removeConcept(int id) {
    Connection connection = connectionPool.getConnection();
    // remove concept:
    conceptCache.remove(id);
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

      StringBuffer sql = new StringBuffer();
      sql.append("DELETE FROM concept WHERE conceptid = ");
      sql.append(id);
      stmt.execute(sql.toString());

      // remove terms:
      sql = new StringBuffer();
      sql.append("DELETE FROM term WHERE conceptid = ");
      sql.append(id);
      stmt.execute(sql.toString());

      // remove relations:
      sql = new StringBuffer();
      sql.append("DELETE FROM relation WHERE conceptid1 = ");
      sql.append(id);
      sql.append(" OR conceptid2 =");
      sql.append(id);
      stmt.execute(sql.toString());

      // remove database links:
      databaseIDCache.remove(id);

      sql = new StringBuffer();
      sql.append("DELETE FROM dblink WHERE conceptid = ");
      sql.append(id);
      stmt.execute(sql.toString());

      stmt.close();
    } catch (SQLException e1) {
      e1.printStackTrace();
    } finally {
      connectionPool.returnConnection(connection);
    }
  }

  @Override
  public List<DatabaseID> getDatabaseIDsForConcept(int id) {
    return databaseIDCache.get(id);
  }

  @Override
  public void setDatabaseIDForConcept(int id, DatabaseID databaseID) {
    List<DatabaseID> databaseIDs = getDatabaseIDsForConcept(id);
    databaseIDs.add(databaseID);
    databaseIDCache.set(id, databaseIDs);    
  }
  
  public Set<Integer> getConceptIDs(DatabaseID databaseID){
    Connection connection = connectionPool.getConnection();
    Set<Integer> result = new HashSet<Integer>();
    try {
      Statement stmt = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
      ResultSet results = stmt.executeQuery("SELECT conceptid FROM dblink WHERE dbid=\""+databaseID.database+"\" AND id=\""+databaseID.ID+"\"");
      results.beforeFirst();
      while (results.next()){
        result.add(results.getInt("conceptid"));
      }     
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      connectionPool.returnConnection(connection);
    }
    return result;
  }

  private List<Relation> resultset2RelationList(ResultSet results) throws SQLException{
    if (!results.first()) return new ArrayList<Relation>();
    List<Relation> relations = new ArrayList<Relation>();
      results.beforeFirst();
      while (results.next()){
        int type = results.getInt("relationtypeid");        
        Relation relation = new Relation(results.getInt("conceptid1"), type, results.getInt("conceptid2"));
        relations.add(relation);
      } 
    return relations;
  }
  

  private class ConceptCache extends StoreMapCaching<Integer, Concept>{   
    private int resultSize = -1;

    public ConceptCache(MySQLconnectionPool connectionPool){
      this.connectionPool = connectionPool;
    }
    
    @Override
    public int size() {
      if(resultSize == -1) {
        Connection connection = connectionPool.getConnection();
        try {
          Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

          ResultSet result = statement.executeQuery("SELECT COUNT(*) AS size FROM concept");
          result.beforeFirst();
          result.next();
          resultSize = result.getInt("size");
          statement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        } finally {
          connectionPool.returnConnection(connection);
        }
      }
      return resultSize;
    }
    
    @Override
    protected Concept getEntryFromStoreWithID(Integer id) {
      return getConcept(id, connectionPool);
    }
    
    @Override
    protected Map<Integer, Concept> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
      return getConcepts(ids, connectionPool);
    }
    
    @Override
    protected void setEntryInStore(Integer id, Concept concept) {
      Connection connection = connectionPool.getConnection();
      try {
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);

        StringBuffer sql = new StringBuffer();
        sql.append("REPLACE INTO concept (conceptid, name, definition) VALUES ");
        sql.append("(\""+concept.getID()+"\",\""+StringUtilities.escape(concept.getName())+"\",\""+StringUtilities.escape(concept.getDefinition())+"\")");
        statement.execute(sql.toString());
        
        List<TermStore> terms = concept.getTerms();
        if (terms.size() != 0){
          sql = new StringBuffer();
          sql.append("REPLACE INTO term (conceptid, termid, text, casesensitive, ordersensitive, normalised) VALUES ");
          boolean previous = false;
          for (int i = 0; i < terms.size(); i++){
            TermStore term = terms.get(i);
            if (previous) sql.append(",");
            previous = true;
            sql.append("("+concept.getID()+","+i+",\""+StringUtilities.escape(term.text)+"\","+term.caseSensitive+","+term.orderSensitive+","+term.normalised+")");
          }
          statement.execute(sql.toString());
        }
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        connectionPool.returnConnection(connection);
      }
    }    
    
    private MySQLconnectionPool connectionPool;  
    
    public void remove(Integer id){
      index.remove(id);
    }
  }
  
  protected Map<Integer, Concept> getConcepts(int offset, int limit, MySQLconnectionPool connectionPool) {
    List<Integer> conceptlist = new ArrayList<Integer>();
    Connection connection = connectionPool.getConnection();
    try {
      Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

      ResultSet result = statement.executeQuery("SELECT conceptid FROM concept ORDER BY conceptid LIMIT "+offset+", "+limit);
      result.beforeFirst();
      while(result.next()) conceptlist.add(result.getInt("conceptid")); 
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
      System.out.println(e.toString());
    } finally {
      connectionPool.returnConnection(connection);
    }
    return getConcepts(conceptlist, connectionPool);
  }
  
  protected static Map<Integer, Concept> getConcepts(Collection<Integer> ids, MySQLconnectionPool connectionPool){
    Map<Integer, Concept> results = new TreeMap<Integer, Concept>();
    String query = "(" + StringUtilities.join(ids,",") +  ")";
    Connection connection = connectionPool.getConnection();
    try {
      Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      
      ResultSet result = statement.executeQuery("SELECT * FROM concept WHERE conceptid IN"+query);
      result.beforeFirst();
      while (result.next()){
        Concept concept = new Concept(result.getInt("conceptid"));
        concept.setName(result.getString("name"));
        concept.setDefinition(result.getString("definition"));
        results.put(concept.getID(), concept);
      }
      result = statement.executeQuery("SELECT * FROM term WHERE conceptid IN"+query);
      result.beforeFirst();
      while (result.next()){
        TermStore term = new TermStore(result.getString("text"));
        term.caseSensitive = result.getBoolean("casesensitive");
        term.orderSensitive = result.getBoolean("ordersensitive");
        term.normalised = result.getBoolean("normalised");
        Concept concept = results.get(result.getInt("conceptid"));
        if (concept.terms == null) concept.terms = new ArrayList<TermStore>();
        concept.terms.add(term);
      }
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      connectionPool.returnConnection(connection);
    }
    return results;
  } 
  
  protected static void updateConceptsWithNames(List<Integer> ids, Map<Integer,Concept> concepts, MySQLconnectionPool connectionPool){
    
    String query = "(" + StringUtilities.join(ids,",") +  ")";
    Connection connection = connectionPool.getConnection();
    try {
      Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      
      ResultSet result = statement.executeQuery("SELECT * FROM concept WHERE conceptid IN"+query);
      result.beforeFirst();
      while (result.next()){
        Concept concept = concepts.get(result.getInt("conceptid"));
        concept.setName(result.getString("name"));
        concept.setDefinition(result.getString("definition"));
      }
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      connectionPool.returnConnection(connection);
    }
    
  }
  
  protected static Concept getConcept(Integer id, MySQLconnectionPool connectionPool){
    Concept concept = null;

    Connection connection = connectionPool.getConnection();
    try {
      Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
      
      ResultSet result = statement.executeQuery("SELECT * FROM concept WHERE conceptid ="+id);
      if (result.first()) {
        concept = new Concept(result.getInt("conceptid"));
        concept.setName(StringUtilities.unescape(result.getString("name")));
        concept.setDefinition(StringUtilities.unescape(result.getString("definition")));
        result = statement.executeQuery("SELECT * FROM term WHERE conceptid ="+id);
        result.beforeFirst();
        while (result.next()){
          TermStore term = new TermStore(StringUtilities.unescape(result.getString("text")));
          term.caseSensitive = result.getBoolean("casesensitive");
          term.orderSensitive = result.getBoolean("ordersensitive");
          term.normalised = result.getBoolean("normalised");
          if (concept.terms == null) concept.terms = new ArrayList<TermStore>();
          concept.terms.add(term);
        }
      }
      statement.close();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      connectionPool.returnConnection(connection);
    }
    return concept;
  }
  
  private class ConceptIterator implements Iterator<Concept> {
    private MySQLconnectionPool connectionPool;
    private int offset = 0;
    private boolean eof = false;
    private Collection<Concept> miniCache;
    private Iterator<Concept> cacheIterator;
    
    public ConceptIterator(MySQLconnectionPool connectionPool){
      this.connectionPool = connectionPool;
    }
    public boolean hasNext() {
      if (cacheIterator != null && cacheIterator.hasNext())
        return true;
      else {
        getNextBatch();
        return !eof;
      }
    }
    
    public Concept next() {
      if (cacheIterator == null || !cacheIterator.hasNext())
        getNextBatch();
      return cacheIterator.next();
    }
    
    public void remove() {
      System.out.println("Remove is not implemented for OntologyClient iterator!");
    }
    
    private void getNextBatch(){
      int batchsize = 10000;
      List<Integer> ids = new ArrayList<Integer>();   
      Connection connection = connectionPool.getConnection();
      try {
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet result = statement.executeQuery("SELECT conceptid FROM concept LIMIT "+offset+","+batchsize);       
        offset += batchsize;
        result.beforeFirst();
        eof = true;
        while (result.next()){
          eof = false;
          ids.add(result.getInt("conceptid"));
        }
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        connectionPool.returnConnection(connection);
      }    
      if (!eof){
        miniCache = getConcepts(ids, connectionPool).values();
        cacheIterator = miniCache.iterator();
      }
    }
  }    
  
  private class DatabaseIDCache extends StoreMapCaching<Integer, List<DatabaseID>>{   
    private MySQLconnectionPool connectionPool;

    public DatabaseIDCache(MySQLconnectionPool connectionPool){
      this.connectionPool = connectionPool;
    }
    
    @Override
    protected List<DatabaseID> getEntryFromStoreWithID(Integer id) {
      List<DatabaseID> dblinks = new ArrayList<DatabaseID>();
      Connection connection = connectionPool.getConnection();
      try {
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        ResultSet result = statement.executeQuery("SELECT * FROM dblink WHERE conceptid = "+id);
        result.beforeFirst();
        
        while (result.next()){
          dblinks.add(new DatabaseID(result.getString("dbid"), result.getString("id")));
        }     
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        connectionPool.returnConnection(connection);
      }       
      return dblinks;
    }
    
    @Override
    protected Map<Integer, List<DatabaseID>> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
      Map<Integer, List<DatabaseID>> results = new HashMap<Integer, List<DatabaseID>>();
      for (Integer id : ids){
        results.put(id, get(id));
      }
      return results;
    }
    
    @Override
    protected void setEntryInStore(Integer id, List<DatabaseID> databaseIDs) {
      Connection connection = connectionPool.getConnection();
      try {
        Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
 
        StringBuffer sql = new StringBuffer();
        sql.append("REPLACE INTO dblink (conceptid, dbid, id) VALUES ");
        boolean previous = false;
        for (DatabaseID databaseID : databaseIDs){
          if (previous) sql.append(",");
          previous = true;
          sql.append("("+id+",\""+databaseID.database+"\",\""+databaseID.ID+"\")");
        }
        statement.execute(sql.toString());
        statement.close();
      } catch (SQLException e) {
        e.printStackTrace();
      } finally {
        connectionPool.returnConnection(connection);
      }  
    }    
    
    @Override
    public int size() {
      return 0;
    }
    
    public void remove(Integer id){
      index.remove(id);
      
    }
  }

  @Override
  public Set<ConceptRelation> getParentRelationsForConceptSet(IntList ids) {
    Set<ConceptRelation> set = new HashSet<ConceptRelation>();
    Set<Integer> seenSet = new HashSet<Integer>();
    for (int id: ids) {
      getParentRelationsForConcept(id, set, seenSet);
    }
    return set;
  }

  private void getParentRelationsForConcept(int conceptid, Set<ConceptRelation> set, Set<Integer> seenSet) {
    List<Relation> parentlist = getRelationsForConceptAsObject(conceptid, DefaultTypes.isParentOf);
    seenSet.add(conceptid);
    for (Relation relation: parentlist) {
      if (set.add(new ConceptRelation(relation.subject, conceptid))) {
        // Avoid circulair references : If a parent is already seen, do not
        // traverse up anymore.
        if (!seenSet.contains(relation.subject))
          getParentRelationsForConcept(relation.subject, set, seenSet);
      }
    }
  }
  public Iterator<Concept> iterator() {
    return getConceptIterator();
  }
}
