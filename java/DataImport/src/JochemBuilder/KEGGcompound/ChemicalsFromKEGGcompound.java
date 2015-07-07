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

package JochemBuilder.KEGGcompound;

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
import org.erasmusmc.utilities.WriteTextFile;

public class ChemicalsFromKEGGcompound {

	public int generalChemicalVocID = -3000;
	public String generalChemicalVocName = "CHEMICAL";
	public int specificChemicalVocID = -3005;
	public String specificChemicalVocName = "KEGGCOMPUND";
	public int umlsSemID = -103;
	public String umlsSemName = "Chemical";

	public OntologyStore run(String filename, String mappingfile){

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

		WriteTextFile writeFile = new WriteTextFile(mappingfile);
		ReadTextFile file = new ReadTextFile(filename);
		Iterator<String> iterator = file.getIterator();
		boolean name = false;
		List<TermStore> terms = null;
		List<DatabaseID> databaseIds = null;
		String keggID = "";
		String formula = "";
		String drugId = "";
		String prevTerm = "";
		String term = "";
		boolean wait = false;
		boolean map = false;
		boolean databaseLinks = false;
		String dbIdString = "";
		int cui = 5000000;
		int lineCount = 0;
		while(iterator.hasNext()){
			lineCount++;
			if (lineCount % 10000 == 0)
				System.out.println(lineCount);
			String line = iterator.next();
			if (line.startsWith("NAME")){
				name = true;
				line = line.substring("NAME".length());
				terms = new ArrayList<TermStore>();
			} else if (line.startsWith("ENTRY")){
				line = line.substring("ENTRY".length());
				line = line.trim().split(" ")[0];
				keggID = line.trim();
			} else if (line.startsWith("REMARK")){
				line = line.substring("REMARK".length());
				if (line.indexOf("Same as:")!=-1){
					map = true;
					String[] mapping = line.split(":");
					drugId = mapping[1].trim();
				}
			} else if (line.startsWith("FORMULA")){
				line = line.substring("FORMULA".length());
				line = line.trim().split(" ")[0];
				formula = line.trim();
				terms.add(new TermStore(formula));
			} else if (line.startsWith("DBLINKS")){
				databaseLinks = true;
				line = line.substring("DBLINKS".length());
				databaseIds = new ArrayList<DatabaseID>();
			}
			if (databaseLinks){
				if (line.startsWith(" ")){
					dbIdString = line.trim();
					String[] ids = dbIdString.split(":");
					String dbName = ids[0].trim();
					if (dbName.equals("CAS")){
						databaseIds.add(new DatabaseID(dbName, ids[1].trim()));
					}
					if (dbName.equals("PubChem")){
						databaseIds.add(new DatabaseID("PUBS", ids[1].trim()));
					}
					if (dbName.equals("ChEBI")){
						databaseIds.add(new DatabaseID("CHEB", ids[1].trim()));
					}
				} else databaseLinks = false;
			} 
			if (name){
				if (line.startsWith(" ")){
					term = line.trim();
					if (term.endsWith("-")||term.endsWith(",")){
						wait = true;
						prevTerm = term;
					}else if (term.endsWith(";") && wait){
						wait = false;
						term = term.substring(0, term.length()-1);
						term = prevTerm+term;
						if (!term.contains("Transferred to"))
							terms.add(new TermStore(term));
					}
					else if (term.endsWith(";") && !wait){
						term = term.substring(0, term.length()-1);
						if (!term.contains("Transferred to"))
							terms.add(new TermStore(term));
					}
					else if (!wait){
						if (!term.contains("Transferred to"))
							terms.add(new TermStore(term));
					} 
				} else{
					name = false;
					if (wait && !term.contains("Transferred to") && !term.equals(prevTerm) && prevTerm.length()!=0){
						wait = false;
						term = prevTerm+term;
						terms.add(new TermStore(term));
					} else if (wait && !term.contains("Transferred to") && term.equals(prevTerm) && prevTerm.length()!=0){
						wait = false;
						terms.add(new TermStore(term));
					}
				}
			}

			else if (line.startsWith("///")){
				if (terms.size() != 0){
					Concept concept = new Concept(cui++);
					//        Replace double quotation mark with single if there is only one mark in the string
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
							//            System.out.println(termToCheck.text);
						}
					}
					//        Set terms and remove duplicates
					concept.setTerms(terms);
					OntologyUtilities.removeDuplicateTerms(terms);
					//Set databaseIDs
					databaseIds.add(new DatabaseID("KEGG", keggID));
					for (DatabaseID databaseId: databaseIds){
						ontology.setDatabaseIDForConcept(concept.getID(), databaseId);
					}
					
					//        Set concept
					ontology.setConcept(concept);
					
					//Set vocabularies and standard semantic type
					Relation generalVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, generalChemicalVocID);
					ontology.setRelation(generalVocRelation);
					Relation specificVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, specificChemicalVocID);
					ontology.setRelation(specificVocRelation);

					Relation semRelation = new Relation(concept.getID(), DefaultTypes.isOfSemanticType, umlsSemID);
					ontology.setRelation(semRelation);

					if (map){
						writeFile.writeln(drugId+" TO "+keggID);
						map = false;
					}
				}
				databaseIds = new ArrayList<DatabaseID>();
				terms = new ArrayList<TermStore>();
				term = "";
				prevTerm = "";
				keggID = "";
				drugId ="";
			}
		}   
		writeFile.close();
		return ontology;
	}

}
