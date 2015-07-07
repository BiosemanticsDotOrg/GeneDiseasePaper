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

package JochemBuilder.HMDB;

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

public class ChemicalsFromHMDB {

	public int generalChemicalVocID = -3000;
	public String generalChemicalVocName = "CHEMICAL";
	public int specificChemicalVocID = -3004;
	public String specificChemicalVocName = "HMDB";
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
		boolean name = false;
		String preferredName = "";
		boolean synonyms = false;
		boolean databaseLinks = false;
		boolean description = false;
		List<TermStore> terms = new ArrayList<TermStore>();
		List<DatabaseID> databaseIds = new ArrayList<DatabaseID>();
		String term = "";
		String database = "";
		String definition = "";
		String synonymString = "";
		int cui = 7000000;
		int lineCount = 0;
		while(iterator.hasNext()){
			lineCount++;
			if (lineCount % 1000000 == 0)
				System.out.println(lineCount);
			String line = iterator.next();
			if (line.startsWith("# hmdb_id:")){
				databaseLinks = true;
				database = "HMDB";
			} else if (line.startsWith("# name:")){
				name = true;
			} else if (line.startsWith("# cas_number:")){
				databaseLinks = true;
				database = "CAS";
			} else if (line.startsWith("# chebi_id:")){
				databaseLinks = true;
				database = "CHEB";
			} else if (line.startsWith("# chemical_formula:")){
				name = true;
			} else if (line.startsWith("# iupac:")){
				name = true;
			} else if (line.startsWith("# description:")){
				description = true;
			} else if (line.startsWith("# pubchem_compound_id:")){
				databaseLinks = true;
				database = "PUBC";
			} else if (line.startsWith("# pubchem_substance_id:")){
				databaseLinks = true;
				database = "PUBS";
			} else if (line.startsWith("# kegg_compound_id:")){
				databaseLinks = true;
				database = "KEGG";
			} else if (line.startsWith("# synonyms:")){
				synonyms = true;
			} else if (line.startsWith("# inchi_identifier:")){
				databaseLinks = true;
				database = "INCH";
			}       
			if (databaseLinks){
				if (!(line.startsWith(" ") || line.startsWith("#"))){
					String id = line.trim();
					if (!id.contains("Not Available")){
						if (!database.equals("INCH")){
							String[] ids = id.split(";");
							for (String dbId: ids){
								if (id.length()>65535) System.out.println(id);
								databaseIds.add(new DatabaseID(database, dbId));
							}
						} else {
							String dbId = id;
							databaseIds.add(new DatabaseID(database, dbId));
						}
					}
					databaseLinks = false; 
				}
			} 
			if (name){
				if (!(line.startsWith(" ") || line.startsWith("#"))){
					term = line.trim();
					if (!term.contains("Not Available") && term.length()<256){
						preferredName = term;
						terms.add(new TermStore(term));
					}
					name = false;
				}         
			}
			if (synonyms){
				if (!(line.startsWith(" ") || line.startsWith("#"))){
					synonymString = line.trim();
					if (!synonymString.contains("Not Available")){
						String[] names = synonymString.split(";");
						for (String string: names)
							if (string.length()<256)
								terms.add(new TermStore(string.trim()));
					}
					synonyms = false;
				}         
			}
			if (description){
				if (!(line.startsWith(" ") || line.startsWith("#"))){
					definition = line.trim();
					if (!definition.contains("Not Available")){
						definition = line.trim();
					} else definition = "";
					description = false;
				}         
			}
			else if (line.startsWith("#END_METABOCARD")){
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
							//              System.out.println(termToCheck.text);
						}
					}
					//        Set terms and remove duplicates
					if (preferredName.length()!=0) terms.add(0, (new TermStore(preferredName)));
					concept.setTerms(terms);
					OntologyUtilities.removeDuplicateTerms(terms);
					//        Set definition. If longer than 1024 characters, then substring and add a full stop.
					if (definition.length()!=0){
						if (!definition.endsWith(".") && definition.length()<=1024){
							definition = definition+".";
						} else if (!definition.endsWith(".") && definition.length()>1024){
							definition = definition.substring(0, 1023)+".";
						}
						concept.setDefinition(definition);
					}
					//Set databaseIDs
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

					terms = new ArrayList<TermStore>();
					databaseIds = new ArrayList<DatabaseID>();
					definition = "";
					term = "";
					database = "";
					synonymString = "";
					preferredName = "";
				}  
			}
		}
		return ontology;
	}

}
