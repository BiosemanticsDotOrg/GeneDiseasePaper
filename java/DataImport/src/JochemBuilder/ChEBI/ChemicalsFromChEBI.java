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

package JochemBuilder.ChEBI;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.ReadTextFile;

public class ChemicalsFromChEBI {
	public int generalChemicalVocID = -3000;
	public String generalChemicalVocName = "CHEMICAL";
	public int specificChemicalVocID = -3002;
	public String specificChemicalVocName = "CHEBI";
	public int umlsSemID = -103;
	public String umlsSemName = "Chemical";

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
		boolean synonym = false;
		List<TermStore> terms = null;
		List<DatabaseID> databaseIds = new ArrayList<DatabaseID>();
		String chebiID = "";
		String term = "";
		String definition = "";
		boolean databaseLinks = false;
		String dbIdString = "";
		int cui = 9000000;
		int lineCount = 0;
		while(iterator.hasNext()){
			lineCount++;
			if (lineCount % 10000 == 0)
				System.out.println(lineCount);
			String line = iterator.next();
			if (line.startsWith("name")){
				term = line.substring("name:".length()).trim();
				terms = new ArrayList<TermStore>();
				if (term.length()<255)
					terms.add(new TermStore(term));
			} else if (line.startsWith("synonym:")){
				synonym = true;
			} else if (line.startsWith("id:")){
				line = line.substring("id: ".length());
				line = line.trim().split(":")[1];
				chebiID = line.trim();
			} else if (line.startsWith("def:")){
				line = line.substring("def: ".length()+1).trim();
				definition = line.substring(0, line.indexOf(" [")-1);
			} else if (line.startsWith("xref:")){
				databaseLinks = true;
			}
			if (databaseLinks){
				if (line.startsWith("xref:")){
					line = line.substring("xref: ".length()).trim();
					dbIdString = line.trim();
					String[] ids = dbIdString.split(":");
					String dbName = ids[0].trim();
					String dbId = ids[1].trim();
					if (dbName.equals("KEGG COMPOUND")){
						if (dbId.contains("CAS Registry Number")){
							dbId = dbId.substring(0, dbId.indexOf("CAS Registry Number")-1).trim();
							databaseIds.add(new DatabaseID("CAS", dbId));
						}else if (dbId.contains("KEGG COMPOUND")){
							dbId = dbId.substring(0, dbId.indexOf("KEGG COMPOUND")-1).trim();
							databaseIds.add(new DatabaseID("KEGG", dbId));
						}
					}
					if (dbName.equals("KEGG DRUG")){
						if (dbId.contains("CAS Registry Number")){
							dbId = dbId.substring(0, dbId.indexOf("CAS Registry Number")-1).trim();
							databaseIds.add(new DatabaseID("CAS", dbId));
						}else if (dbId.contains("KEGG DRUG")){
							dbId = dbId.substring(0, dbId.indexOf("KEGG DRUG")-1).trim();
							databaseIds.add(new DatabaseID("KEGG", dbId));
						}
					}
					if (dbName.equals("ChemIDplus")){
						if (dbId.contains("CAS Registry Number")){
							dbId = dbId.substring(0, dbId.indexOf("CAS Registry Number")-1).trim();
							databaseIds.add(new DatabaseID("CAS", dbId));
						}
					}
					if (dbName.equals("NIST Chemistry WebBook")){
						if (dbId.contains("CAS Registry Number")){
							dbId = dbId.substring(0, dbId.indexOf("CAS Registry Number")-1).trim();
							databaseIds.add(new DatabaseID("CAS", dbId));
						}
					}
					if (dbName.equals("ChEBI")){
						if (dbId.contains("KEGG COMPOUND")){
							dbId = dbId.substring(0, dbId.indexOf("KEGG COMPOUND")-1).trim();
							databaseIds.add(new DatabaseID("KEGG", dbId));
						}
					}
					if (dbName.equals("DrugBank")){
						if (dbId.contains("DrugBank")){
							dbId = dbId.substring(0, dbId.indexOf("DrugBank")-1).trim();
							databaseIds.add(new DatabaseID("DRUG", dbId));
						}
					}
				} else databaseLinks = false;
			} 
			if (synonym){
				if (line.startsWith("synonym:") && !line.contains("RELATED InChI") && !line.contains("RELATED SMILES")){
					// remove formulas:          if (line.startsWith("synonym:") && !line.contains("RELATED FORMULA") && !line.contains("RELATED InChI") && !line.contains("RELATED SMILES")){
					term = line.substring("synonym: ".length()+1).trim();
					if (term.indexOf("RELATED")!=-1)
						term = term.substring(0, term.indexOf("RELATED")-2).trim();            
					else if (term.indexOf("EXACT")!=-1)
						term = term.substring(0, term.indexOf("EXACT")-2).trim(); 
					if (term.length()<255)
						terms.add(new TermStore(term));
				}else if(line.startsWith("synonym:") && line.contains("InChI=")){
					String dbId = line.substring("synonym: ".length()+1).trim();
					if (dbId.indexOf("RELATED")!=-1)
						dbId = dbId.substring(0, dbId.indexOf("RELATED")-2).trim();            
					else if (dbId.indexOf("EXACT")!=-1)
						dbId = dbId.substring(0, dbId.indexOf("EXACT")-2).trim(); 
					databaseIds.add(new DatabaseID("INCH", dbId));
				} else synonym = false;
			}

			else if (line.length()==0){
				if (!chebiID.equals("23091") && !chebiID.equals("24431") && !chebiID.equals("23367") && terms!=null && terms.size() != 0){
					Concept concept = new Concept(cui++);
					//Replace double quotation mark with single if there is only one mark in the string
					for (TermStore termToCheck: terms){
						int i = 0;
						char currentchar;
						int numberOfQuotationMarks = 0;
						while (i < termToCheck.text.length()){
							currentchar = termToCheck.text.charAt(i);
							if (currentchar =='"'){
								numberOfQuotationMarks++;
							}
							i++;
						}
						if (numberOfQuotationMarks==1){
							termToCheck.text = termToCheck.text.replace('"', '\'');
						}
					}
					//Set terms and remove duplicates
					concept.setTerms(terms);
					OntologyUtilities.removeDuplicateTerms(terms);
					//Set definition. If longer than 1024 characters, then substring and add a full stop.
					if (definition.length()!=0){
						if (!definition.endsWith(".") && definition.length()<=1024){
							definition = definition+".";
						} else if (!definition.endsWith(".") && definition.length()>1024){
							definition = definition.substring(0, 1023)+".";
						}
						concept.setDefinition(definition);
					}
					//Set databaseIDs
					databaseIds.add(new DatabaseID("CHEB", chebiID));
					for (DatabaseID databaseId: databaseIds){
						ontology.setDatabaseIDForConcept(concept.getID(), databaseId);
					}
					//Set concept
					ontology.setConcept(concept);
					
					//Set vocabularies and standard semantic type
					Relation generalVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, generalChemicalVocID);
					ontology.setRelation(generalVocRelation);
					Relation specificVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, specificChemicalVocID);
					ontology.setRelation(specificVocRelation);

					Relation semRelation = new Relation(concept.getID(), DefaultTypes.isOfSemanticType, umlsSemID);
					ontology.setRelation(semRelation);

					databaseIds  = new ArrayList<DatabaseID>();
					definition = "";
					chebiID = "";
					term = "";
				}
			}
			if (line.startsWith("[Typedef]")){
				break;
			}
		}
		return ontology;
	}
}
