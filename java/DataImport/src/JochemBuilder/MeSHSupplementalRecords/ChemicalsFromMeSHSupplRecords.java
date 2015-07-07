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

package JochemBuilder.MeSHSupplementalRecords;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.ReadTextFile;

import JochemBuilder.SharedCurationScripts.JochemCurator;

public class ChemicalsFromMeSHSupplRecords {
	public int generalChemicalVocID = -3000;
	public String generalChemicalVocName = "CHEMICAL";
	public int specificChemicalVocID = -3008;
	public String specificChemicalVocName = "MESHSUPPRECORDS";
	public int umlsSemID = -103;
	public String umlsSemName = "Chemical";

  public static Set<Integer> chemicalSemanticTypes = JochemCurator.getAllChemicalSemanticTypes(); 
  public static Set<Integer> undesiredChemicalSemanticTypes = JochemCurator.getUndesiredSemanticTypes();
  public OntologyStore run(String filename){
    OntologyStore ontology = new OntologyStore();
    //Set the standard vocabulary and umls semantic type concepts
	Concept generalVocabulary = new Concept(generalChemicalVocID);
	generalVocabulary.setName(generalChemicalVocName);
	ontology.setConcept(generalVocabulary);	
	Concept specificVocabulary = new Concept(specificChemicalVocID);
	specificVocabulary.setName(specificChemicalVocName);
	ontology.setConcept(specificVocabulary);		

	Concept semantictype = new Concept(umlsSemID);
	semantictype.setName(umlsSemName);
	ontology.setConcept(semantictype);

    ReadTextFile file = new ReadTextFile(filename);
    Iterator<String> iterator = file.getIterator();
    boolean name = false;
    List<TermStore> terms = new ArrayList<TermStore>();
    boolean semanticType = false;
    List<DatabaseID> databaseIds = new ArrayList<DatabaseID>();
    Integer sem = 0;
    List<Integer> sems = new ArrayList<Integer>();
    String meshID = "";
    String term = "";
    boolean databaseLinks = false;
    String dbIdString = "";
    int cui = 8500000;
    int lineCount = 0;
    while(iterator.hasNext()){
      lineCount++;
      if (lineCount % 10000 == 0)
        System.out.println(lineCount);
      String line = iterator.next();
      if (line.startsWith("NM =")){
        name = true;
      } else if (line.startsWith("N1 =")){
        name = true;
      } else if (line.startsWith("SY =")){
        name = true;
      } else if (line.startsWith("ST =")){
        semanticType = true;
      } else if (line.startsWith("RN =")){
        databaseLinks = true;
      }{
        if (semanticType){
          if (line.startsWith("ST =")){
            sem = Integer.parseInt(line.substring(6));
            sem = -sem;
            sems.add(sem);
          } else semanticType = false;
        }
        if (databaseLinks){
          if (line.startsWith("RN =")){
            dbIdString = line.substring(5).trim();
            if (dbIdString.contains("|")){
              String[] ids = dbIdString.split("\\|");
              String dbID = ids[0].trim();
              if (!dbID.equals("0") && !dbID.startsWith("EC"))
                databaseIds.add(new DatabaseID("CAS", dbID));
            } else {
              if (!dbIdString.equals("0") && !dbIdString.startsWith("EC"))
                databaseIds.add(new DatabaseID("CAS", dbIdString));
            }
          } else databaseLinks = false;
        } 
        if (name){
          if (line.startsWith("SY =")){
            term = line.substring(5).trim();
            if (term.contains("|")){
              String[] termString = term.split("\\|");
              if (term.length()<=225)
                terms.add(new TermStore(termString[0]));
            } else {
              if (term.length()<=225)
                terms.add(new TermStore(term));
            }
          }else if (line.startsWith("NM =")){
            term = line.substring(5).trim();
            if (term.length()<=225)
              terms.add(new TermStore(term));
          }else if (line.startsWith("N1 =")){
            term = line.substring(5).trim();
            if (term.length()<=225)
              terms.add(new TermStore(term));
          }else{
            name = false;          
          }
        }      
        else if (line.startsWith("UI")){
          meshID = line.substring(5);
          if (terms.size() != 0 && !sems.isEmpty()){
            boolean add = false;
            for (Integer semtype: sems){
              if (chemicalSemanticTypes.contains(semtype)
                  && !undesiredChemicalSemanticTypes.contains(semtype)) add = true;                
            }
            if (add){
              Concept concept = new Concept(cui++);
//            Set terms and remove duplicates
              concept.setTerms(terms);
              OntologyUtilities.removeDuplicateTerms(terms);
              //Set databaseIDs
              databaseIds.add(new DatabaseID("MESH", meshID));
              for (DatabaseID databaseId: databaseIds){
                ontology.setDatabaseIDForConcept(concept.getID(), databaseId);
              }
//            Set concept
              ontology.setConcept(concept);
              
              //Set vocabularies and standard semantic type
				Relation generalVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, generalChemicalVocID);
				ontology.setRelation(generalVocRelation);
				Relation specificVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, specificChemicalVocID);
				ontology.setRelation(specificVocRelation);

				Relation semRelation = new Relation(concept.getID(), DefaultTypes.isOfSemanticType, umlsSemID);
				ontology.setRelation(semRelation);

            }
          }
          term = "";
          databaseIds = new ArrayList<DatabaseID>();
          terms = new ArrayList<TermStore>();
          sems = new ArrayList<Integer>();
        }
      }   
    }
    return ontology;
  }
}
