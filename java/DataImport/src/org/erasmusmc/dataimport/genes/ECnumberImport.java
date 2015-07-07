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

package org.erasmusmc.dataimport.genes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.TextFileUtilities;

/** Loads the EC numbers from the enzyme file of Swiss-prot and inserts them into a gene/protein ontology**/
public class ECnumberImport {
  public static String classFile = "/data/Swiss-Prot/enzclass_pimped.txt";
  public static String enzymeFile = "/data/Swiss-Prot/enzyme.dat";
  public static String dataFile = "/data/Swiss-Prot/uniprot_sprot.dat";

  public static void addECnumbers(Ontology ontology, String classFile, String enzymeFile, String dataFile){
    ECnumberImport.classFile = classFile;
    ECnumberImport.enzymeFile = enzymeFile;
    ECnumberImport.dataFile = dataFile;
    new ECnumberImport(ontology);
  }
  
  public ECnumberImport(Ontology ontology) {
    this.ontology = ontology;
    System.out.println(StringUtilities.now() + "\tLoading protein ontology");
    if (ontology instanceof OntologyStore)
      ((OntologyStore)ontology).createIndexForDatabaseIDs();

    System.out.println(StringUtilities.now() + "\tLoading enzyme files");
    List<String> classLines = TextFileUtilities.loadFromFile(classFile);
    List<String> enzymeLines = TextFileUtilities.loadFromFile(enzymeFile);

    Concept concept = new Concept(-1999);
    concept.setName("ENZYME");
    ontology.setConcept(concept);

    System.out.println(StringUtilities.now() + "\tProcessing enzyme files");
    processClassLines(classLines);
    processLines(enzymeLines);

    System.out.println(StringUtilities.now() + "\tFetching EC numbers per protein");
    processDataFile(dataFile);

    System.out.println(StringUtilities.now() + "\tGenerating protein - enzyme class relations");
    generateRelations();
  }

  private void generateRelations() {
    //List<String> out = new ArrayList<String>();
    for (Concept concept : ontology){

      //Find protein 2 EC relations
      int proteinConceptID = concept.getID();
      List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(proteinConceptID);
      Set<Integer> ecConceptIDs = new HashSet<Integer>();
      for (DatabaseID databaseID: databaseIDs) {
        if (databaseID.database.equals("SP")) {
          List<Integer> ecs = SPID2EC.get(databaseID.ID);
          if (ecs != null)
            ecConceptIDs.addAll(ecs);
        }
      }
      //Create relations:
      for (Integer ecConceptID: ecConceptIDs) {
        Relation relation = new Relation(ecConceptID, DefaultTypes.isParentOf, proteinConceptID);
        ontology.setRelation(relation);
        //out.add(ontology.getConcept(proteinConceptID).getName() + "\t" + ontology.getConcept(ecConceptID).getName());
      }

      //Find EC 2 EC superclass relations:
      if (concept.getName().startsWith("EC ")) {
        String[] parts = concept.getName().split("\\.");
        if (parts.length == 4){
          for (int i = 3; i > 0; i--) {
            parts[i] = "-";
            String className = StringUtilities.join(parts, ".");
            Integer superclassID = ECclass2cid.get(className);
            if (superclassID != null && superclassID != proteinConceptID) {
              Relation relation = new Relation(superclassID, DefaultTypes.isParentOf, proteinConceptID);
              ontology.setRelation(relation);
              //out.add(ontology.getConcept(proteinConceptID).getName() + "\t" + ontology.getConcept(superclassID).getName());
              break;
            }
          }
        }  
      }

    }
    System.out.println("Relations added: " + ontology.getRelations().size());
    //TextFileUtilities.saveToFile(out, "/home/temp/relations.txt");
  }

  private void processDataFile(String filename) {
    String spidline = "";
    ReadTextFile in = new ReadTextFile(filename);
    for (String line : in){
      if (line.startsWith("AC"))
        spidline = line;

      if (line.startsWith("DE")) {
        String[] cols = line.substring(6).split("\\(");
        for (String col: cols) {
          String[] cols2 = col.split("\\)");
          if (cols2.length > 0 && cols2[0].startsWith("EC ")) { //synonym is an EC number
            Integer cid = EC2CID.get(cols2[0].trim());
            if (cid != null) {
              String[] spids = spidline.substring(5).split(";");
              for (String spid: spids) {
                List<Integer> cids = SPID2EC.get(spid.trim());
                if (cids == null) {
                  cids = new ArrayList<Integer>();
                  SPID2EC.put(spid.trim(), cids);
                }
                cids.add(cid);
              }
            }
          }
        }
      }
    }
  }

  private void processClassLines(List<String> classLines) {
    Concept concept = null;
    for (String line: classLines) {
      if (line.length() > 1) {
        String[] cols = line.split("\t");
        concept = new Concept(conceptid);
        String ECnumber = cols[0];
        //TermStore term = createTerm(ECnumber);
        EC2CID.put(ECnumber, concept.getID());
        //List<TermStore> terms = new ArrayList<TermStore>();
        //terms.add(term);
        //concept.setTerms(terms);
        concept.setName(ECnumber);
        concept.setDefinition(cols[1]);
        ECclass2cid.put(ECnumber, concept.getID());
        ontology.setConcept(concept);
        setVoc(concept);
        conceptid++;
      }
    }
  }

  private void processLines(List<String> enzymeLines) {
    Concept concept = null;
    for (String line: enzymeLines) {
      if (line.startsWith("ID")) {
        concept = new Concept(conceptid);
        String ECnumber = "EC " + line.substring(3).trim();
        //TermStore term = createTerm(ECnumber);//new TermStore(ECnumber);
        EC2CID.put(ECnumber, concept.getID());
        //List<TermStore> terms = new ArrayList<TermStore>();
        //terms.add(term);
        //concept.setTerms(terms);
        concept.setName(ECnumber);
        ontology.setConcept(concept);
        setVoc(concept);
        conceptid++;
      }
      if (line.startsWith("DE"))
        if (!line.contains("Transferred entry") && !line.contains("Deleted entry"))
          concept.setDefinition(line.substring(3, line.length() - 1).trim()+"\n\n");
      /*if (line.startsWith("AN")){
        String term = line.substring(3, line.length() - 1).trim();
        if (StringUtilities.parenthesisMatch(term))
          concept.getTerms().add(createTerm(term));
        else
          System.out.println("Discarted: "  + term);
      }
      */  
      if (line.startsWith("CC") && concept != null)
        concept.setDefinition(concept.getDefinition() + " " + line.substring(3).replace("-!-", "").trim());
      /*if (line.startsWith("DR")){
       String cols[] = line.substring(3).split(";");
       for (String col : cols){
       String parts[] = col.split(",");
       Set<Integer> proteinIDs = ontology.getConceptIDs(new DatabaseID("SP", parts[0]));
       if (proteinIDs != null){
       for (Integer proteinID : proteinIDs){
       ontology.setRelation(new Relation(conceptid, DefaultTypes.isParentOf, proteinID));
       }
       }  
       }
       }*/
    }
  }

  private void setVoc(Concept concept) {
    Relation relation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, -1999);
    ontology.setRelation(relation);
  }
  /*
  private TermStore createTerm(String string) {
    TermStore term = new TermStore(string);
    term.caseSensitive = false;
    term.orderSensitive = true;
    term.normalised = false;
    return term;
  }
*/
  private Map<String, List<Integer>> SPID2EC = new HashMap<String, List<Integer>>();
  private Map<String, Integer> EC2CID = new HashMap<String, Integer>();
  private Map<String, Integer> ECclass2cid = new HashMap<String, Integer>();
  private Ontology ontology;
  private int conceptid = 50000000;
}
