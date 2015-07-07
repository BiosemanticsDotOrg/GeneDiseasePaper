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

package org.erasmusmc.dataimport;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.tree.DefaultMutableTreeNode;

//import org.erasmusmc.applications.ontologyviewer.OntologyViewerPanel;
import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.databases.integersetstore.IntegerSetStore;
import org.erasmusmc.databases.mysql.MySQLgenericQuery;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class GeneOntology {
  
  private String goFile;
  
//  public static void main(String[] args){
//    GeneOntology go = new GeneOntology("/home/data/gene ontology/go_200804-assocdb.rdf-xml");
//    OntologyManager ontologyManager = new OntologyManager();
//    Ontology ontology = ontologyManager.fetchClient("UMLS2006Homologene_v1_6");
//    go.dumpPMIDs(ontology, "/home/temp/gocid2pmid.txt");
//  }
  
  public GeneOntology(String goFile){
    this.goFile = goFile;
  }
  
  public void doAll(){
    OntologyStore ontology = buildOntology();
    
    dumpPMIDs(ontology, "/tmp/go2pmid.txt");
    
    //dumpInMySQL();
    
    dumpInPSF(ontology, "/tmp/go.psf");
    
    //showTree();
  }

  
  /**
   * Creates an ontologyStore containing the GO
   * @return
   */
  public OntologyStore buildOntology() {    
    System.out.println("Building ontology");
    OntologyStore ontology = new OntologyStore();
    int conceptID = 6000000;
    Concept concept = null;
    Map<String, Integer> go2cid = new HashMap<String, Integer>();
    String goCode = "";
    ReadTextFile file = new ReadTextFile(goFile);
    for (String fullLine : file){
      String line = fullLine.trim();
      if (line.startsWith("<go:term")){
        concept = new Concept(conceptID);
        concept.setTerms(new ArrayList<TermStore>());
        conceptID++;
        ontology.setConcept(concept);
      }
      
      if (line.startsWith("<go:accession>")){
        goCode = StringUtilities.findBetween(line, "<go:accession>","</go:accession>");
        DatabaseID databaseID = new DatabaseID("GO", goCode);
        ontology.setDatabaseIDForConcept(concept.getID(), databaseID);
        go2cid.put(goCode, concept.getID()); 
      }
      
      if (line.startsWith("<go:name>") && concept.getTerms().size() == 0)
        concept.getTerms().add(new TermStore(StringUtilities.findBetween(line, "<go:name>","</go:name>")));
      
      if (line.startsWith("<go:synonym>")){
        String term = StringUtilities.findBetween(line, "<go:synonym>","</go:synonym>");
        if (term.startsWith("GO:")){ //Alternative ID
          DatabaseID databaseID = new DatabaseID("GO", term);
          ontology.setDatabaseIDForConcept(concept.getID(), databaseID);
          go2cid.put(term, concept.getID()); 
        } else       
          concept.getTerms().add(new TermStore(term));
      }
      
      if (line.startsWith("<go:definition>"))
        concept.setDefinition(StringUtilities.findBetween(line, "<go:definition>","</go:definition>"));
    }
    System.out.println("Concepts found: " + ontology.size());
    System.out.println("Creating hierarchy");
    buildHierarchy(ontology);
    System.out.println("Finished building ontology");
    return ontology;
  }

  /**
   * Adds GO relationships to an ontology
   * @param ontology
   */
  private void buildHierarchy(OntologyStore ontology) {
    Map<String, List<String>> go2parents = new HashMap<String, List<String>>();
    String goCode = "";
    ReadTextFile file = new ReadTextFile(goFile);
    for (String fullLine : file){
      String line = fullLine.trim();
      
      if (line.startsWith("<go:accession>")){
        goCode = StringUtilities.findBetween(line, "<go:accession>","</go:accession>");
      }
      
      if (line.startsWith("<go:part_of rdf:resource=\"http://www.geneontology.org/go#")){
        String parentGOcode = StringUtilities.findBetween(line, "<go:part_of rdf:resource=\"http://www.geneontology.org/go#","\" />");
        List<String> parents = go2parents.get(goCode);
        if (parents == null){
          parents = new ArrayList<String>();
          go2parents.put(goCode, parents);
        }
        parents.add(parentGOcode);
      }     
        
      if (line.startsWith("<go:is_a rdf:resource=\"http://www.geneontology.org/go#")){
        String parentGOcode = StringUtilities.findBetween(line, "<go:is_a rdf:resource=\"http://www.geneontology.org/go#","\" />");
        List<String> parents = go2parents.get(goCode);
        if (parents == null){
          parents = new ArrayList<String>();
          go2parents.put(goCode, parents);
        }
        parents.add(parentGOcode);
      }
    }
    
    Map<String, Integer> go2cid = getGO2CID(ontology);
    
    for (Entry<String, List<String>> entry : go2parents.entrySet()){
      Integer childCID = go2cid.get(entry.getKey());
      for (String parent : entry.getValue()){
        Integer parentCID = go2cid.get(parent);
        if (parentCID == null) System.out.println("not found: " + parent); 
        else if (childCID == null) System.out.println("not found: " + entry.getKey());
        else ontology.setRelation(new Relation(parentCID, DefaultTypes.isParentOf, childCID));
      }
    }
  }

  public void dumpInPSF(Ontology ontology, String filename) {
    OntologyPSFLoader loader = new OntologyPSFLoader();
    loader.ontology = (OntologyStore)ontology;
    loader.saveToPSF(filename);
  }
  
//   commented unused method, causes compilation trouble [RvS] 
//  public void showTree(Ontology ontology){
//    OntologyViewerPanel panel = new OntologyViewerPanel(ontology, DefaultTypes.isParentOf);
//    JDialog dialog = panel;
//    dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//    dialog.pack();
//    dialog.setVisible(true);
//  }
  
  private Map<String, Integer> getGO2CID(Ontology ontology) {
    System.out.println(StringUtilities.now() + "\tFetching cids for GO concepts");
    Map<String, Integer> go2cid = new HashMap<String, Integer>();
    Iterator<Concept> iterator = ontology.getConceptIterator();
    while (iterator.hasNext()){
      Concept concept = iterator.next();
      List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
      for (DatabaseID databaseID : databaseIDs){
        if (databaseID.database.equals("GO"))
          go2cid.put(databaseID.ID, concept.getID());
      }
    }
    System.out.println(StringUtilities.now() + "\tCids fetched");
    return go2cid;
  }

  /**
   * Creates an integersetstore containing PMIDS for the GO concepts in the ontology.
   * @param ontology
   * @param filename    Name of the IntegerSetStore
   */public void dumpPMIDs(Ontology ontology, String filename) {
    System.out.println(StringUtilities.now() + "\tFetching PMIDS");
    String goCode = "";
    boolean isPMID = false;
    Map<Integer, List<Integer>> cid2pmids = new HashMap<Integer, List<Integer>>();
    Map<String, Integer> go2cid = getGO2CID(ontology);
    ReadTextFile file = new ReadTextFile(goFile);
    for (String fullLine : file){
      String line = fullLine.trim();
      
      if (line.startsWith("<go:accession>")){
        goCode = StringUtilities.findBetween(line, "<go:accession>","</go:accession>");
      }
      
      if (isPMID){
        String pmid = StringUtilities.findBetween(line, "<go:reference>","</go:reference>");
        if (StringUtilities.isNumber(pmid)){
          Integer cid = go2cid.get(goCode);
          if (cid == null){
            System.err.println("GO code " + goCode + " not found in thesaurus");
          } else {
            List<Integer> pmids = cid2pmids.get(cid);
            if (pmids == null){
              pmids = new ArrayList<Integer>();
              cid2pmids.put(cid, pmids);
            }
            pmids.add(Integer.parseInt(pmid));  
          }
        }
      }
      
      isPMID = line.equals("<go:database_symbol>PMID</go:database_symbol>");
    }
    
    System.out.println(StringUtilities.now() + "\tDumping to IntegerSetStore");
    IntegerSetStore integerSetStore = new IntegerSetStore(filename);
    for (Entry<Integer, List<Integer>> entry : cid2pmids.entrySet()){
      SortedIntListSet pmids = new SortedIntListSet();
      for (Integer pmid : entry.getValue())
        pmids.add(pmid);
      integerSetStore.set(entry.getKey(), pmids);  
    }  
    integerSetStore.close();
    /*System.out.println(StringUtilities.now() + "\tDumping to file");
    WriteTextFile out = new WriteTextFile(filename);
    for (Entry<Integer, List<Integer>> entry : cid2pmids.entrySet()){
      StringBuffer line = new StringBuffer();
      line.append(entry.getKey());
      line.append("\t");
      for (Integer pmid : entry.getValue()){
        line.append(pmid);
        line.append(";");
      }
      out.writeln(line.toString());
    }
    out.close();*/
    System.out.println(StringUtilities.now() + "\tDone");
  }
  
  
  public void dumpDatasetsInMySQL(Ontology geneOntology, Ontology umls, String server, String database, String username, String password, int molfunc, int celcomp, int bioproc){
	  System.out.println(StringUtilities.now() + "\tInserting GO");
    fetchRelations(geneOntology, DefaultTypes.isParentOf);
    DefaultMutableTreeNode tree = buildTree(geneOntology);
    Enumeration<DefaultMutableTreeNode> categories = tree.children();
    MySQLgenericQuery query = new MySQLgenericQuery(server, database, username, password);
    int conceptsetid = -1;
    while (categories.hasMoreElements()){
      DefaultMutableTreeNode category = categories.nextElement();
      String categoryName = category.getUserObject().toString();
      System.out.println("Adding GO category "+categoryName);
      Set<Integer> conceptIDs = new HashSet<Integer>();
      Enumeration<DefaultMutableTreeNode> members = category.breadthFirstEnumeration();
      List<String> stringList = new ArrayList<String>();
      while (members.hasMoreElements()){
        Concept concept = (Concept)members.nextElement().getUserObject();
        for (DatabaseID databaseID : geneOntology.getDatabaseIDsForConcept(concept.getID())){
          Set<Integer> cuis = umls.getConceptIDs(databaseID);
          conceptIDs.addAll(cuis);          
        }
      }
      if(categoryName.equals("cellular_component")) {
        conceptsetid = celcomp;
      }
      else if(categoryName.equals("molecular_function")) {
        conceptsetid = molfunc;
      }
      else if(categoryName.equals("biological_process")) {          
        conceptsetid = bioproc;
      }
      for(Integer cui: conceptIDs) {
        stringList.add("('" + cui + "','" + conceptsetid + "')");
      }
      query.nonThreadedUpdate("DELETE FROM set_2_concept WHERE conceptsetid='" + conceptsetid + "'");
      query.nonThreadedUpdate("INSERT INTO set_2_concept (conceptid, conceptsetid) VALUES " +  StringUtilities.join(stringList, ","));
    }
  }

  private DefaultMutableTreeNode buildTree(Ontology ontology) {
    DefaultMutableTreeNode top;
    List<Integer> parents = findParents();
    if (parents.size() == 1) {
      top = addNode(ontology, parents.get(0)); 
    } else {
      top = new DefaultMutableTreeNode("root");
      for (Integer parent : parents){
        top.add(addNode(ontology, parent));
      }
    }
    return top;
  }

  //Stuff for builing tree:
  private void fetchRelations(Ontology ontology, int relationType) {
    List<Relation> relations = ontology.getRelations();
    for (Relation relation : relations){
      if (relation.predicate == relationType){
        List<Integer> children = cui2children.get(relation.subject);
        if (children == null){
          children = new ArrayList<Integer>();
          cui2children.put(relation.subject, children);
        }
        children.add(relation.object);
        hasParent.add(relation.object);
      }
    }
  }

  private DefaultMutableTreeNode addNode(Ontology ontology, Integer cui) {
    DefaultMutableTreeNode node = new DefaultMutableTreeNode(ontology.getConcept(cui));
    inTree.add(cui);
    List<Integer> children = cui2children.get(cui);
    if (children != null)
      for (Integer child : children)
          node.add(addNode(ontology, child)); 
    
    return node;
  }

  private List<Integer> findParents() {
    List<Integer> parents = new ArrayList<Integer>();
    for (Integer cui : cui2children.keySet()){
      if (!hasParent.contains(cui)) parents.add(cui);
    }
      
    return parents;
  }

  private Set<Integer> hasParent = new HashSet<Integer>();
  private Set<Integer> inTree = new HashSet<Integer>();
  private Map<Integer, List<Integer>> cui2children = new HashMap<Integer, List<Integer>>();
  private static final long serialVersionUID = 1L;
  /*
  public void dumpGOA(String filename) {
    ReadTextFile file = new ReadTextFile(goFile);
    WriteTextFile output = new WriteTextFile(filename);
    String evidence = null;
    boolean gene = false;
    String databaseSymbol = null;
    for (String line : file){
      String trimLine = line.trim();
      if (trimLine.startsWith("<go:accession>")){
        goCode = StringUtilities.findBetween(trimLine, "<go:accession>","</go:accession>");  
      } else if (trimLine.startsWith("<go:evidence evidence_code=")){
        evidence = StringUtilities.findBetween(trimLine, "<go:evidence evidence_code=\"","\"");  
      } else if (trimLine.equals("<go:gene_product rdf:parseType=\"Resource\">")){
        gene = true; 
      } else if (gene && trimLine.startsWith("<go:database_symbol>")){
        databaseSymbol = StringUtilities.findBetween(trimLine, "<go:database_symbol>","</go:database_symbol>");  
      } else if (gene && trimLine.startsWith("<go:reference>")){
        String databaseID = StringUtilities.findBetween(trimLine, "<go:reference>","</go:reference>");
        DatabaseID id = new DatabaseID(databaseSymbol, databaseID);
        output.writeln(goCode + "\t" + id.database + "\t" + id.ID + "\t" + evidence);
        gene = false;
      } else if (trimLine.equals("</go:gene_product>")){
        gene = false; 
      }   
    }
    output.close();
  }
  */

}
