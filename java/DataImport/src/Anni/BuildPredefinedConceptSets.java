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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.dataimport.GeneOntology;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.semanticnetwork.SemanticGroup;
import org.erasmusmc.semanticnetwork.SemanticNetwork;
import org.erasmusmc.semanticnetwork.SemanticType;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class BuildPredefinedConceptSets {
  public static String server = "localhost";
  public static String database = "ConceptSets_july2012perOrg";
  public static String user = "root";
  public static String password = "";
  public static String ontologyName = "UMLS2010ABHomologeneJochemToxV1_6";
  public static String geneOntologyFilename = "/home/biosemantics/GO/go_20120714-assocdb.rdf-xml";
  public static String toxClassificationTreeFilename = "/home/biosemantics/AnniUpdate/tox_classification_tree.txt";
  public static String toxConcept2SetFilename = "/home/biosemantics/AnniUpdate/chemConceptIDToToxClass_v3.txt";
  public static int molfuncConceptSet = 3;
  public static int celcompConceptSet = 4;
  public static int bioprocConceptSet = 5;
  public static int semanticGroupdConceptSet = 6;
  public static int genesConceptSet = 7;
  public static int chemicalsConceptSet = 8;
  public static int toxConceptSet = 9;
  public static int filtersConceptSet = 10;
  

  public static void main(String[] args) {
  	OntologyManager manager = new OntologyManager(server, user, password);
  	Ontology ontology = manager.fetchClient(ontologyName);
  	
  	insertTopSets();
    doSemanticTypesandGroups(ontology);
    doGenes(ontology);
    doChemicals(ontology);
    doExcFilter(ontology);
    GeneOntology geneOntology = new GeneOntology(geneOntologyFilename);
    geneOntology.dumpDatasetsInMySQL(geneOntology.buildOntology(), ontology, server, database, user, password, molfuncConceptSet, celcompConceptSet, bioprocConceptSet);
  	doToxTree(ontology);
  	
  	System.out.println(StringUtilities.now() + "\tDone");
  }
  
  private static void doToxTree(Ontology ontology) {
	  System.out.println(StringUtilities.now() + "\tInserting tox tree");
	  Map<String, Integer> internalID2ConceptSet = new HashMap<String, Integer>();
  	try {
  	  Statement stmt = initStatement();
  	  //Add tree:
  	  for (String line : new ReadTextFile(toxClassificationTreeFilename)){
  	  	String[] cols = line.split("\t");
  	  	String internalID = cols[0];
  	  	String name = cols[1];
  	  	stmt.execute("INSERT INTO conceptsets (name,parent) VALUES (\""+name+"\","+toxConceptSet+");");	
        ResultSet result = stmt.executeQuery("SELECT LAST_INSERT_ID()");
        if (result.next()){
          Integer conceptSet = result.getInt(1);
          internalID2ConceptSet.put(internalID, conceptSet);
        }
  	  }
  	  
  	  //Add concepts:
  	  for (String line : new ReadTextFile(toxConcept2SetFilename)){
  	  	String[] cols = line.split("\t");
  	  	String conceptID = cols[0];
  	  	String internalID = cols[1];
  	  	Integer conceptSet = internalID2ConceptSet.get(internalID);
  	  	if (conceptSet == null)
  	  		System.err.println("Illegal concept set in tox file: " + internalID);
  	  	else {
  	  		stmt.execute("REPLACE INTO set_2_concept VALUES (" + conceptID + "," + conceptSet + ");");
  	  	}
  	  }
  	  
		} catch (SQLException e) {
			e.printStackTrace();
		} 	  
  	  
  }

private static void insertTopSets() {
  	System.out.println(StringUtilities.now() + "\tInserting top sets");
  	try {
  	  Statement stmt = initStatement();
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES (1, \"Predefined Concept Sets\", 0);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES (2, \"GO\", 1);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES (3, \"Molecular Function\", 2);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES (4, \"Cellular Component\", 2);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES (5, \"Biological Process\", 2);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES ("+semanticGroupdConceptSet+", \"Semantic Groups\", 1);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES ("+genesConceptSet+", \"Genes\", 1);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES ("+chemicalsConceptSet+", \"Chemicals\", 1);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES ("+toxConceptSet+", \"Toxic Effect (RTECS & IARC) \", 1);");
			stmt.execute("REPLACE INTO conceptsets (id,name,parent) VALUES ("+filtersConceptSet+", \"Filters\", 1);");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void doSemanticTypesandGroups(Ontology ontology) {
      System.out.println(StringUtilities.now() + "\tInserting semantic types and groups");
	  int parent = 1;
	  SemanticNetwork semanticNetwork = new SemanticNetwork();
	  semanticNetwork.loadDefaultsFromFile();
	  
	  for(SemanticGroup semanticGroup: semanticNetwork.groups.values()) {
	    int autoIncKeyFromFuncGroup = -1;
	    Statement stmt = initStatement();

	    ResultSet result;
	    try {
	      result = stmt.executeQuery("SELECT id FROM conceptsets WHERE name='" + semanticGroup.name + "' AND parent='" + semanticGroupdConceptSet + "'");
	      if(result.next())
	        autoIncKeyFromFuncGroup = result.getInt(1);
	      else {
	        stmt.execute("INSERT INTO conceptsets VALUES (NULL, '" + semanticGroup.name + "','" + semanticGroupdConceptSet + "')");
	        result = stmt.executeQuery("SELECT LAST_INSERT_ID()");
	        if (result.next())
	          autoIncKeyFromFuncGroup = result.getInt(1);
	        for(SemanticType semanticType: semanticNetwork.types.values()) {
	          int autoIncKeyFromFuncGroupType = -1;
	          if(semanticType.group == semanticGroup) {
	              result = stmt.executeQuery("SELECT id FROM conceptsets WHERE name='" + semanticType.name + "' AND parent='" + autoIncKeyFromFuncGroup +"'");
	              if(result.next())
	                autoIncKeyFromFuncGroupType = result.getInt(1);
	              else {
	                stmt.execute("INSERT INTO conceptsets VALUES (NULL, '" + semanticType.name + "','" + autoIncKeyFromFuncGroup + "')");
	                result = stmt.executeQuery("SELECT LAST_INSERT_ID()");
	                if (result.next())
	                  autoIncKeyFromFuncGroupType = result.getInt(1);
	              }
	              try {
	                List<Relation> relations = ontology.getRelationsForConceptAsObject(-semanticType.ID);
	                ArrayList<String> sqlTypes = new ArrayList<String>();
	                for(Relation rel: relations) {
	                  sqlTypes.add("('" + rel.subject +"','" + autoIncKeyFromFuncGroupType + "')");
	                }
	                if(sqlTypes.size() > 10000) {
	                  for(int i=0;i<sqlTypes.size();i+=10000) {
	                    int end = i + 10000;
	                    if(end > sqlTypes.size()) end = sqlTypes.size();
	                    stmt.execute("REPLACE INTO set_2_concept VALUES " + StringUtilities.join(sqlTypes.subList(i, end),","));
	                  }
	                }
	                else
	                  if(sqlTypes.size() != 0)
	                  stmt.execute("REPLACE INTO set_2_concept VALUES " + StringUtilities.join(sqlTypes,","));
	
	              } catch (SQLException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	              }
	          }
	        }
	      }      
	    } catch (SQLException e) {
	      // TODO Auto-generated catch block
	      e.printStackTrace();
	    }
	  }
	  //getChildren(ontology, null, semanticNetwork, parent);
	}

	private static void getChildren(Ontology ontology, SemanticType parentType, SemanticNetwork semanticNetwork, int parentid) {
	    Statement stmt = initStatement();
	    for (SemanticType semanticType: semanticNetwork.types.values()) {
	      if (semanticType.parent == parentType) {
	        ResultSet result;
	        int autoIncKeyFromFunc = -1;
	        String parentName = "Semantic Types";
	        if(parentType != null)
	          parentName = parentType.name;
	        try {
	          result = stmt.executeQuery("SELECT id FROM conceptsets WHERE name='" + parentName + "' AND parent='" + parentid + "'");
	          if (result.next())
	            autoIncKeyFromFunc = result.getInt(1);
	          else {
	            stmt.execute("INSERT INTO conceptsets VALUES (NULL, '" + parentName + "','" + parentid + "')");
	            result = stmt.executeQuery("SELECT LAST_INSERT_ID()");
	            if (result.next())
	                autoIncKeyFromFunc = result.getInt(1);
	          }

	
	        } catch (SQLException e) {
	          e.printStackTrace();
	          System.out.println("INSERT INTO conceptsets VALUES (NULL, '" + parentName + "','" + parentid + "')");
	        }
	        try {
	          List<Relation> relations = ontology.getRelationsForConceptAsObject(-semanticType.ID);
	          ArrayList<String> sqlTypes = new ArrayList<String>();
	          for(Relation rel: relations) {
	            sqlTypes.add("('" + rel.subject +"','" + autoIncKeyFromFunc + "')");
	          }
	          if(sqlTypes.size() > 10000) {
	            for(int i=0;i<sqlTypes.size();i+=10000) {
	              int end = i + 10000;
	              if(end > sqlTypes.size()) end = sqlTypes.size();
	              stmt.execute("REPLACE INTO set_2_concept VALUES " + StringUtilities.join(sqlTypes.subList(i, end),","));
	            }
	          }
	          else
	            if(sqlTypes.size() != 0)
	            stmt.execute("REPLACE INTO set_2_concept VALUES " + StringUtilities.join(sqlTypes,","));
	        } catch (SQLException e) {
	          e.printStackTrace();
	        }
	        getChildren(ontology, semanticType, semanticNetwork, autoIncKeyFromFunc);
	      }
	    }
	  }

	private static Statement initStatement() {
    Statement stmt = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      try {
        Connection con = DriverManager.getConnection("jdbc:mysql://"+server+":3306/",user, password);
        stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        stmt.execute("USE " + database);

      } catch (SQLException e) {
        e.printStackTrace();
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return stmt;
	}

	private static void doExcFilter(Ontology ontology) {
		System.out.println(StringUtilities.now() + "\tInserting exc. filter for genes");
		Statement stmt = initStatement();
		
		//Insert exclusion filter concept set:
		int excFilterConceptSet = -1;
		try {
			stmt.execute("REPLACE INTO conceptsets (name,parent) VALUES (\"Excl. filter for genes\", "+filtersConceptSet+");");
			ResultSet result = stmt.executeQuery("SELECT LAST_INSERT_ID()");
			if (result.next())
				excFilterConceptSet = result.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//Generate filter:
		List<Integer> semanticTypes = new ArrayList<Integer>();
    semanticTypes.add(-1);
    semanticTypes.add(-2);
    semanticTypes.add(-3);
    semanticTypes.add(-4);
    semanticTypes.add(-5);
    semanticTypes.add(-6);
    semanticTypes.add(-7);
    semanticTypes.add(-8);
    semanticTypes.add(-9);
    semanticTypes.add(-10);
    semanticTypes.add(-11);
    semanticTypes.add(-12);
    semanticTypes.add(-13);
    semanticTypes.add(-14);
    semanticTypes.add(-15);
    semanticTypes.add(-16);
    semanticTypes.add(-51);
    semanticTypes.add(-52);
    semanticTypes.add(-53);
    semanticTypes.add(-54);
    semanticTypes.add(-55);
    semanticTypes.add(-56);
    semanticTypes.add(-57);
    semanticTypes.add(-58);
    semanticTypes.add(-59);
    semanticTypes.add(-60);
    semanticTypes.add(-61);
    semanticTypes.add(-62);
    semanticTypes.add(-63);
    semanticTypes.add(-64);
    semanticTypes.add(-65);
    semanticTypes.add(-66);
    semanticTypes.add(-67);
    semanticTypes.add(-68);
    semanticTypes.add(-69);
    semanticTypes.add(-70);
    semanticTypes.add(-71);
    semanticTypes.add(-72);
    semanticTypes.add(-73);
    semanticTypes.add(-74);
    semanticTypes.add(-75);
    semanticTypes.add(-77);
    semanticTypes.add(-78);
    semanticTypes.add(-79);
    semanticTypes.add(-80);
    semanticTypes.add(-81);
    semanticTypes.add(-82);
    semanticTypes.add(-83);
    semanticTypes.add(-85);
    semanticTypes.add(-86);
    semanticTypes.add(-87);
    semanticTypes.add(-88);
    semanticTypes.add(-89);
    semanticTypes.add(-90);
    semanticTypes.add(-91);
    semanticTypes.add(-92);
    semanticTypes.add(-93);
    semanticTypes.add(-94);
    semanticTypes.add(-95);
    semanticTypes.add(-96);
    semanticTypes.add(-97);
    semanticTypes.add(-98);
    semanticTypes.add(-99);
    semanticTypes.add(-100);
    semanticTypes.add(-101);
    semanticTypes.add(-102);
    semanticTypes.add(-169);
    semanticTypes.add(-170);
    semanticTypes.add(-171);
    semanticTypes.add(-185);
    semanticTypes.add(-203);
    List<String> cuiList = new ArrayList<String>();
    for(Integer cui: semanticTypes) {
      List<Relation> relationList = ontology.getRelationsForConceptAsObject(cui);
      for(Relation relation: relationList) {
        cuiList.add("('"+relation.subject+"', '"+excFilterConceptSet+"')");
      }
    }
    try {
			stmt.execute("REPLACE INTO set_2_concept (conceptid, conceptsetid) VALUES " +  StringUtilities.join(cuiList, ","));
		} catch (SQLException e) {
			e.printStackTrace();
		}
  }
	
  private static void doGenes(Ontology ontology) {
  	System.out.println(StringUtilities.now() + "\tInserting genes");
  	
  	Map<String, String> voc2vocName = new HashMap<String, String>();
  	voc2vocName.put("HSAPIENS", "H.sapiens");
  	voc2vocName.put("MMUSCULUS", "M.musculus");
  	voc2vocName.put("RNORVEGICUS", "R.norvegicus");
  	voc2vocName.put("ECOLI", "E.coli");
  	voc2vocName.put("SCEREVISIAE", "S.cerevisiae");
  	voc2vocName.put("DRERIO", "D.rerio");
  	voc2vocName.put("CELEGANS", "C.elegans");
  	voc2vocName.put("GGALLUS", "G.gallus");
  	voc2vocName.put("DMELANOGASTER", "D.melanogaster");
  	Map<String,Integer> voc2conceptSetID = new HashMap<String, Integer>();
  	Statement stmt = initStatement();
  	for (Map.Entry<String, String> entry: voc2vocName.entrySet()){
  		try {
  			stmt.execute("INSERT INTO conceptsets VALUES (NULL, '" + entry.getValue() + "','" + genesConceptSet + "')");
  			ResultSet result = stmt.executeQuery("SELECT LAST_INSERT_ID()");
  			if (result.next())
  				voc2conceptSetID.put(entry.getKey(),result.getInt(1));
  		} catch (SQLException e) {
  			e.printStackTrace();
  		}
  	}
  	Map<Integer,Integer> vocID2conceptSetID = new HashMap<Integer, Integer>();
  	List<String> stringList = new ArrayList<String>();
    //int geneVocID = 0;
    for (Concept concept : ontology){
    	if (concept.getID() < 0){//It might be a vocabulary
    		Integer conceptSetID = voc2conceptSetID.get(concept.getName());
    		if (conceptSetID != null)
    			vocID2conceptSetID.put(concept.getID(), conceptSetID);
    		//else if (concept.getName().equals(OntologyUtilities.geneVocabulary))
    		//	geneVocID = concept.getID();
    	} else {
    	  for (Relation relation : ontology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.fromVocabulary))
    	  	if (vocID2conceptSetID.containsKey(relation.object))
    	  		stringList.add("('" + concept.getID() + "', '" + vocID2conceptSetID.get(relation.object) +"')");
    	}
    }
    try {
			stmt.execute("REPLACE INTO set_2_concept (conceptid, conceptsetid) VALUES " +  StringUtilities.join(stringList, ","));
		} catch (SQLException e) {
			e.printStackTrace();
		}
  }
  
  private static void doChemicals(Ontology ontology) {
  	System.out.println(StringUtilities.now() + "\tInserting chemicals");
    List<String> stringList = new ArrayList<String>();
    for (Concept concept : ontology)
      if (OntologyUtilities.hasChemVoc(concept, ontology))
        stringList.add("('" + concept.getID() + "', '" + chemicalsConceptSet +"')");  
    
    Statement stmt = initStatement();
    try {
			stmt.execute("REPLACE INTO set_2_concept (conceptid, conceptsetid) VALUES " +  StringUtilities.join(stringList, ","));
		} catch (SQLException e) {
			e.printStackTrace();
		}
  }
}
