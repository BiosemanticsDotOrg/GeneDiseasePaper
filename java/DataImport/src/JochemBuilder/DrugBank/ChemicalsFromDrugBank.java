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

package JochemBuilder.DrugBank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jregex.MatchIterator;
import jregex.MatchResult;
import jregex.Matcher;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

import JochemBuilder.chemIDplus.ChemicalsFromChemIDplus;

public class ChemicalsFromDrugBank {

	public int generalChemicalVocID = -3000;
	public String generalChemicalVocName = "CHEMICAL";
	public int specificChemicalVocID = -3003;
	public String specificChemicalVocName = "DRUGBANK";
	public int umlsSemID = -103;
	public String umlsSemName = "Chemical";

	public jregex.Pattern bracketsPattern = new jregex.Pattern("\\[[^]]*\\]");

	public boolean inTag = false;
	public boolean first = false;
	public int cID = 3000000;

	public StringBuffer record = new StringBuffer();

	public OntologyStore drugbankOntology = new OntologyStore();

	public List<String> countriesAndLanguages = getCountriesAndLanguages();
	public Set<Integer> foundSemTypesForConcept = null;
	public Concept concept = null;
	public Map<String, Integer> semanticTypes = new HashMap<String, Integer>();

	public OntologyStore run(String filename){
		//Set the standard vocabulary and umls semantic type concepts
		Concept generalVocabulary = new Concept(generalChemicalVocID);
		generalVocabulary.setName(generalChemicalVocName);
		drugbankOntology.setConcept(generalVocabulary);	
		Concept specificVocabulary = new Concept(specificChemicalVocID);
		specificVocabulary.setName(specificChemicalVocName);
		drugbankOntology.setConcept(specificVocabulary);		

		Concept semantictype = new Concept(umlsSemID);
		semantictype.setName(umlsSemName);
		drugbankOntology.setConcept(semantictype);
		
		System.out.println("Processing drug cards file " + StringUtilities.now());
		processFile(filename);
		System.out.println("Writing DrugBank ontology " + StringUtilities.now());
		return drugbankOntology;
	}

	private void processFile(String drugBankFile){

		ReadTextFile textFile = new ReadTextFile(drugBankFile);
		Iterator<String> lineIterator = textFile.getIterator();    
		int tagCount = 0;
		while (lineIterator.hasNext()) {
			String line = lineIterator.next();
			if (line.length() != 0) {
				Integer beginTagIndex = line.toLowerCase().indexOf("#begin_drugcard");
				if (!inTag && beginTagIndex != -1){
					inTag = true;
					first = true;
					tagCount++;
					if (tagCount % 10000 == 0)
						System.out.println(tagCount);
				}
				if (inTag){
					processTag(line, beginTagIndex);
				}
			}
		}
	}  

	private void processTag(String line, Integer beginTagIndex){    
		Integer endTagIndex = line.toLowerCase().indexOf("#end_drugcard");
		if (endTagIndex == -1){
			if (first){
				String firstline = line.substring(beginTagIndex, line.length());
				record.append(firstline+"\t");
				first = false;
			}
			else record.append(line+"\t");      
		}
		if (endTagIndex != -1){
			String substring = line.substring(0, endTagIndex);
			record.append(substring);
			storeRecord();
			record = new StringBuffer();
			inTag = false;
		}
	}

	private void storeRecord(){    
		cID++;
		concept = new Concept(cID);
		foundSemTypesForConcept = new TreeSet<Integer>();
		String name = "";
		String term = "";
		List<TermStore> terms = new ArrayList<TermStore>();
		List<DatabaseID> databaseIds = new ArrayList<DatabaseID>();
		String definition = "";
//		String sem = "";
//		List<String> sems = new ArrayList<String>();

		String recordString = record.toString();
		String[] columns = recordString.split("#");
		for (String column: columns ){
			column = column.trim();
			if (column.startsWith("BEGIN_DRUGCARD")){
				String[] parts = column.split(" ");
				String dbNr = parts[1].trim();
				DatabaseID dbID = new DatabaseID("DRUG", dbNr);
				databaseIds.add(dbID);
			}else if (column.startsWith("Brand_Names")){
				String[] parts = column.split("\t");
				for (String part: parts){
					if (!part.contains("Brand_Names") && part.length()<=255 && !part.contains("Not Available") && !termNotEnglish(part)){
						term = part.trim();
						terms.add(new TermStore(term));
					}
				}
			}else if (column.startsWith("CAS_Registry_Number")){
				String[] parts = column.split("\t");
				String dbNr = parts[1].trim();
				if (!dbNr.contains("Not Available")){
					DatabaseID dbID = new DatabaseID("CAS", dbNr);
					databaseIds.add(dbID);
				}
			}else if (column.startsWith("InChI_Identifier")){
				String[] parts = column.split("\t");
				String dbNr = parts[1].trim();
				if (!dbNr.contains("Not Available")){
					DatabaseID dbID = new DatabaseID("INCH", dbNr);
					databaseIds.add(dbID);
				}          
			}else if (column.startsWith("ChEBI_ID")){
				String[] parts = column.split("\t");
				String dbNr = parts[1].trim();
				if (!dbNr.contains("Not Available")){
					DatabaseID dbID = new DatabaseID("CHEB", dbNr);
					databaseIds.add(dbID);
				}
			}
			else if (column.startsWith("Chemical_Formula")){
				String[] parts = column.split("\t");
				for (String part: parts){
					if (!part.contains("Chemical_Formula") && part.length()<=255 && !part.contains("Not Available") && !termNotEnglish(part)){
						term = part.trim();
						terms.add(new TermStore(term));
					}
				}
			}else if (column.startsWith("Chemical_IUPAC_Name")){
				String[] parts = column.split("\t");
				for (String part: parts){
					if (!part.contains("Chemical_IUPAC_Name") && part.length()<=255 && !part.contains("Not Available") && !termNotEnglish(part)){
						term = part.trim();
						terms.add(new TermStore(term));
					}
				}
			}else if (column.startsWith("Description")){
				String[] parts = column.split("\t");
				if (!parts[1].contains("Not Available"))
					definition = definition + parts[1].trim();
			}/**else if (column.startsWith("Drug_Category")){
        String[] parts = column.split("\t");
        for (String part: parts){
          if (!part.contains("Drug_Category") && part.length()<=255 && !part.contains("Not Available")){
            sem = part.trim();
            sems.add(sem);
          }
        }   
      }  */  else if (column.startsWith("Generic_Name")){
    	  String[] parts = column.split("\t");
    	  for (String part: parts){
    		  if (!part.contains("Generic_Name") && part.length()<=255 && !part.contains("Not Available") && !termNotEnglish(part)){
    			  term = part.trim();
    			  name = term;
    			  terms.add(new TermStore(term));
    		  }
    	  }
      }else if (column.startsWith("KEGG_Compound_ID")){
    	  String[] parts = column.split("\t");
    	  String dbNr = parts[1].trim();
    	  if (!dbNr.contains("Not Available")){
    		  DatabaseID dbID = new DatabaseID("KEGG", dbNr);
    		  databaseIds.add(dbID);
    	  }
      }else if (column.startsWith("KEGG_Drug_ID")){
    	  String[] parts = column.split("\t");
    	  String dbNr = parts[1].trim();
    	  if (!dbNr.contains("Not Available")){
    		  DatabaseID dbID = new DatabaseID("KEGD", dbNr);
    		  databaseIds.add(dbID);
    	  }
      }else if (column.startsWith("Mechanism_Of_Action")){
    	  String[] parts = column.split("\t");
    	  if (!parts[1].contains("Not Available"))
    		  definition = definition + parts[1].trim();
      }else if (column.startsWith("PubChem_Compound_ID")){
    	  String[] parts = column.split("\t");
    	  String dbNr = parts[1].trim();
    	  if (!dbNr.contains("Not Available")){
    		  DatabaseID dbID = new DatabaseID("PUBC", dbNr);
    		  databaseIds.add(dbID);
    	  }
      }else if (column.startsWith("PubChem_Substance_ID")){
    	  String[] parts = column.split("\t");
    	  String dbNr = parts[1].trim();
    	  if (!dbNr.contains("Not Available")){
    		  DatabaseID dbID = new DatabaseID("PUBS", dbNr);
    		  databaseIds.add(dbID);
    	  }
      }else if (column.startsWith("Synonyms")){
    	  String[] parts = column.split("\t");
    	  for (String part: parts){
    		  if (!part.contains("Synonyms") && part.length()<=255 && !part.contains("Not Available") && !termNotEnglish(part)){
    			  term = part.trim();
    			  terms.add(new TermStore(term));
    		  }
    	  }      
      }else if (column.startsWith("Toxicity")){
    	  String[] parts = column.split("\t");
    	  if (!parts[1].contains("Not Available"))
    		  definition = definition + parts[1].trim();
      }

		}
		//Set terms
		if (terms.size() != 0){
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
					//          System.out.println(termToCheck.text);
				}
			}
			//    Set terms and remove duplicates
			terms.add(0, (new TermStore(name)));
			concept.setTerms(terms);
			OntologyUtilities.removeDuplicateTerms(terms);
		}
		//Set definition. If longer than 1024 characters, then substring and add a full stop.
		if (definition.length()!=0){
			if (!definition.endsWith(".") && definition.length()<=1024){
				definition = definition+".";
			} else if (!definition.endsWith(".") && definition.length()>1024){
				definition = definition.substring(0, 1023)+".";
			}
			concept.setDefinition(definition);
		}
		/**    //Set semantic types
    if (!sems.isEmpty()){
      for (String semString: sems){
        Integer semID = semanticTypes.get(semString);
        if (semID == null) {
          semID = -300 - semanticTypes.size();
          semanticTypes.put(semString, semID);
          Concept semanticType = new Concept(semID);
          semanticType.setName(semString);
          drugbankOntology.setConcept(semanticType);
        }
        if (!foundSemTypesForConcept.contains(semID)) {
          Relation relation = new Relation(concept.getID(), DefaultTypes.isOfSemanticType, semID);
          drugbankOntology.setRelation(relation);
          foundSemTypesForConcept.add(semID);
        }
      }
    }*/
		//Set database IDs
		if (!databaseIds.isEmpty()){
			for (DatabaseID databaseId: databaseIds){
				drugbankOntology.setDatabaseIDForConcept(concept.getID(), databaseId);
			}
		}
		//  Set concept
		if (concept != null) {
			if (concept.getTerms().size() != 0)
				drugbankOntology.setConcept(concept);
		}
		
		//Set vocabularies and standard semantic type
		Relation generalVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, generalChemicalVocID);
		drugbankOntology.setRelation(generalVocRelation);
		Relation specificVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, specificChemicalVocID);
		drugbankOntology.setRelation(specificVocRelation);

		Relation semRelation = new Relation(concept.getID(), DefaultTypes.isOfSemanticType, umlsSemID);
		drugbankOntology.setRelation(semRelation);
	} 

	private boolean termNotEnglish(String name){
		boolean found = false;
		Matcher m = bracketsPattern.matcher(name);
		MatchIterator mi = m.findAll();    
		while(mi.hasMore()){
			MatchResult mr=mi.nextMatch();
			String match = mr.toString().toLowerCase();
			Iterator listiterator = countriesAndLanguages.iterator();        
			while (listiterator.hasNext()){  
				String term = listiterator.next().toString().toLowerCase();
				if (match.contains(term)){
					found = true;
				}
			}
		}    
		return found;
	}

	private ArrayList<String> getCountriesAndLanguages() {
		ArrayList<String> countries = new ArrayList<String>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ChemicalsFromChemIDplus.class.getResourceAsStream("countriesAndLanguages.txt")));
	    try {
	      while (bufferedReader.ready()) {
	    	  countries.add(bufferedReader.readLine().trim());
	      }
	    } catch (IOException e) {
	      e.printStackTrace();
	    }
		return countries;
	}

}
