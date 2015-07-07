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

package JochemBuilder.chemIDplus;

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
import java.util.regex.Pattern;

import jregex.MatchIterator;
import jregex.MatchResult;
import jregex.Matcher;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

import casperSoftwareCode.Rules;

public class ChemicalsFromChemIDplus {
	public Pattern indexNameNotYetAssignedPattern = Pattern.compile("Index name not yet assigned", Pattern.CASE_INSENSITIVE);
	public jregex.Pattern bracketsPattern = new jregex.Pattern("\\[[^]]*\\]");

	public int generalChemicalVocID = -3000;
	public String generalChemicalVocName = "CHEMICAL";
	public int specificChemicalVocID = -3001;
	public String specificChemicalVocName = "CHEMIDPLUS";
	public int beginSemID = -300;
	public int umlsSemID = -103;
	public String umlsSemName = "Chemical";
	public boolean inTag = false;
	public boolean first = false;
	public int cID = 6000000;

	public StringBuffer record = new StringBuffer();
	public String nameList = "";
	public String semList = "";

	public OntologyStore chemIDplusOntology = new OntologyStore();

	public List<String> countriesAndLanguages = getCountriesAndLanguages();
	public Set<Integer> foundSemTypesForConcept = null;
	public Concept concept = null;

	public OntologyStore run(String filename, String semanticTypesMergeLog){
		//Set the standard vocabulary and umls semantic type concepts
		Concept generalVocabulary = new Concept(generalChemicalVocID);
		generalVocabulary.setName(generalChemicalVocName);
		chemIDplusOntology.setConcept(generalVocabulary);	
		Concept specificVocabulary = new Concept(specificChemicalVocID);
		specificVocabulary.setName(specificChemicalVocName);
		chemIDplusOntology.setConcept(specificVocabulary);		

		Concept semantictype = new Concept(umlsSemID);
		semantictype.setName(umlsSemName);
		chemIDplusOntology.setConcept(semantictype);

		System.out.println("Processing ChemIDplus XML file " + StringUtilities.now());
		processFile(filename);

		System.out.println("Merging similar semantic types " + StringUtilities.now());
		WriteTextFile out = new WriteTextFile(semanticTypesMergeLog);
		String name = "";
		Map<String,Integer> nameToCui = new HashMap<String,Integer>();
		Iterator<Concept> it = chemIDplusOntology.getConceptIterator();
		while (it.hasNext()){
			Concept concept = it.next();
			if (concept.getID()<=-300 && concept.getID()>=-400){
				name = concept.getName();
				String modifiedName = Rules.makeLowerCaseAndRemoveEos(name);
				Integer cui = nameToCui.get(modifiedName);
				if (cui!=null){
					out.writeln(cui+"\t"+concept.getID()+"\t"+chemIDplusOntology.getConcept(cui).getName()+"\t"+concept.getName());
					mergeSemanticTypes(chemIDplusOntology, concept.getID(), cui, true);
				}else nameToCui.put(modifiedName, concept.getID());
			}
		}
		out.close();
		System.out.println("Writing logfile " + StringUtilities.now());
		return chemIDplusOntology;
	}

	private void processFile(String chemIDplusFile){

		ReadTextFile textFile = new ReadTextFile(chemIDplusFile);
		Iterator<String> lineIterator = textFile.getIterator();    
		int tagCount = 0;
		while (lineIterator.hasNext()) {
			String line = lineIterator.next();
			if (line.length() != 0) {
				Integer beginTagIndex = line.toLowerCase().indexOf("<chemical");
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
		Integer endTagIndex = line.toLowerCase().indexOf("</chemical>");
		if (endTagIndex == -1){
			if (first){
				String firstline = line.substring(beginTagIndex, line.length()-1);
				record.append(firstline);
				first = false;
			}
			else record.append(line);      
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
		String chemID = getChemID();
		String displayName = getChemName();
		if (chemID.length() != 0 && displayName.length() != 0 && !indexNameNotYetAssignedPattern.matcher(displayName).find()){
			cID++;
			concept = new Concept(cID);
			foundSemTypesForConcept = new TreeSet<Integer>();
			List<TermStore> terms = concept.getTerms();

			//Store displayName as name and term
			if (displayName.length() < 255 && !termNotEnglish(displayName)){
				//concept.setName(displayName);
				terms.add(new TermStore(displayName));
			}
			//	    Store displayFormula as term
			String displayFormula = getFormula();
			if (displayFormula.length()!=0 && !displayFormula.contains("Unspecified") && !displayFormula.contains("unspecified") && !displayFormula.contains("UNSPECIFIED"))
				terms.add(new TermStore(displayFormula));

			//Store chemIdplus database ID      
			DatabaseID databaseID = new DatabaseID("CHID", chemID);
			chemIDplusOntology.setDatabaseIDForConcept(concept.getID(), databaseID);

			//Store CAS number as database ID
			if (record.indexOf("<CASRegistryNumber>") != -1){
				String casnr = getCASnr();
				if (!casnr.equals("Not valid")){
					DatabaseID casID = new DatabaseID("CAS", casnr);
					chemIDplusOntology.setDatabaseIDForConcept(concept.getID(), casID);
				}
			}

			//Create list of names
			Integer beginNameIndex = record.indexOf("<NameList>");
			Integer endNameIndex = record.indexOf("</NameList>");
			if (beginNameIndex!=-1 && endNameIndex!=-1){
				nameList = record.substring(beginNameIndex, endNameIndex);

				//Store name of substance as term
				if (nameList.indexOf("<NameOfSubstance>") != -1){
					terms = addNameOfSubstance(terms);      
				}

				//	      Store systematic name as term
				if (nameList.indexOf("<SystematicName>") != -1){
					terms = addSystematicNames(terms);      
				}

				//Store synonyms as terms
				if (nameList.indexOf("<Synonyms>") != -1){
					terms = addSynonyms(terms);                
				}
				nameList = "";
			}


			//Set terms
			if (!terms.isEmpty()){
				//	      Replace double quotation mark with single if there is only one mark in the string
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
				//	      Set terms and remove duplicates
				concept.setTerms(terms);
				OntologyUtilities.removeDuplicateTerms(concept.getTerms());
			}

			//Set definition
			if (record.indexOf("<Note>") != -1){
				String definition = findDefinition();        
				concept.setDefinition(definition);
			}

			//Set semantic types
			if (record.indexOf("<ClassificationCode>") != -1){
				Integer beginSemIndex = record.indexOf("<ClassificationList>");
				Integer endSemIndex = record.indexOf("</ClassificationList>");
				if (beginSemIndex!=-1 && endSemIndex!=-1){
					semList = record.substring(beginSemIndex, endSemIndex);
					List<String> semStrings = findClassificationCodes();
					List<String> semStrings2 = findSuperListClassificationCodes();
					semStrings.addAll(semStrings2);
					for (String semString: semStrings){
						addSemanticType(semString);
					}

					semList = "";
				}
			}

			//Set concept
			if (concept != null) {
				if (concept.getTerms().size() != 0)
					chemIDplusOntology.setConcept(concept);
			} 

			//Set vocabularies and standard semantic type
			Relation generalVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, generalChemicalVocID);
			chemIDplusOntology.setRelation(generalVocRelation);
			Relation specificVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, specificChemicalVocID);
			chemIDplusOntology.setRelation(specificVocRelation);

			Relation semRelation = new Relation(concept.getID(), DefaultTypes.isOfSemanticType, umlsSemID);
			chemIDplusOntology.setRelation(semRelation);
		}
	}   

	private String getChemName(){
		String name = record.substring(record.indexOf("displayName=\""), record.indexOf(">", record.indexOf("displayName=\"")));
		String finalDisplayName = name.substring(13, name.lastIndexOf("\""));
		name = finalDisplayName.trim();
		return name;
	}

	private String getFormula(){
		String formula = record.substring(record.indexOf("displayFormula=\""), record.indexOf("displayName")).trim();
		String finalDisplayFormula = formula.substring(16, formula.lastIndexOf("\""));
		formula = finalDisplayFormula.trim();
		return formula;
	}

	private String getChemID(){
		String chemID = record.substring(record.indexOf("Chemical id=\""), record.indexOf("displayFormula")).trim();
		chemID = chemID.substring(13, chemID.lastIndexOf("\""));
		return chemID;
	}

	private String getCASnr(){
		int substringIndex = record.indexOf("<SourceList", record.indexOf("<CASRegistryNumber>"));

		/**		int substringIndex = record.indexOf("<SourceList>", record.indexOf("<CASRegistryNumber>"));
				if(substringIndex < 0){
		maybe we have an empty sourceList tag
					substringIndex = record.indexOf("<SourceList/>", record.indexOf("<CASRegistryNumber>"));
				}*/
		String casnr = record.substring(record.indexOf("<CASRegistryNumber>"), substringIndex).trim();
		String finalCasNR = casnr.substring(19);
		if (validCASnr(finalCasNR)){
			casnr = finalCasNR;
			return casnr;
		}
		return "Not valid";
	}

	private boolean validCASnr(String cas){
		if (cas.contains("-")){
			String[] parts = cas.split("-");
			if (parts.length==3){
				if (parts[0].length() > 0 && parts[0].length() < 8 && parts[1].length() == 2 && parts[2].length() == 1){
					String concatCas = parts[0]+parts[1];
					int[] intArray = new int[concatCas.length()];					 
					for (int i = 0; i < concatCas.length(); i++) {
						if (Character.digit(concatCas.charAt(i), 10)==-1) return false;
						intArray[i] = Character.digit(concatCas.charAt(i), 10);
					}
					int sum = 0;
					int check = intArray.length-1;
					for (int i = 1; i < intArray.length+1; i++){
						sum = sum + intArray[check]*i;
						check--;
					}
					if ((sum % 10) == Integer.parseInt(parts[2]))
						return true;
				}
			}
		}
		return false;

	}

	private List<TermStore> addSynonyms(List<TermStore> terms){
		String[] columns = nameList.split("<Synonyms>");
		for (String column : columns){
			if (column.contains("</Synonyms>")){
				int substringIndex =  column.indexOf("<SourceList");
				/**				int substringIndex =  column.indexOf("<SourceList>");
				if(substringIndex < 0){
					// we did not find the tag, perhaps it was empty
					substringIndex = column.indexOf("<SourceList/>");
				}*/
				String synonym = column.substring(0, substringIndex).trim();
				if (synonym.length()!=0 && !termNotEnglish(synonym) && synonym.length() < 255  && !synonym.contains("Beilstein Handbook Reference")){
					terms.add(new TermStore(synonym));
				}
			}
		}
		return terms;
	}

	private List<TermStore> addSystematicNames(List<TermStore> terms){
		String[] columns = nameList.split("<SystematicName>");
		for (String column : columns){
			if (column.contains("</SystematicName>")){
				int substringIndex =  column.indexOf("<SourceList");
				/**				int substringIndex =  column.indexOf("<SourceList>");
				if(substringIndex < 0){
					// we did not find the tag, perhaps it was empty
					substringIndex = column.indexOf("<SourceList/>");
				}*/
				String synonym = column.substring(0, substringIndex).trim();
				if (synonym.length()!=0 && !termNotEnglish(synonym) && synonym.length() < 255  && !synonym.contains("Beilstein Handbook Reference")){
					terms.add(new TermStore(synonym));
				}
			}
		}
		return terms;
	}

	private List<TermStore> addNameOfSubstance(List<TermStore> terms){
		String[] columns = nameList.split("<NameOfSubstance>");
		for (String column : columns){
			if (column.contains("</NameOfSubstance>")){
				int substringIndex =  column.indexOf("<SourceList");
				/**				int substringIndex =  column.indexOf("<SourceList>");
				if(substringIndex < 0){
					// we did not find the tag, perhaps it was empty
					substringIndex = column.indexOf("<SourceList/>");
				}*/
				String synonym = column.substring(0, substringIndex).trim();
				if (synonym.length()!=0 && !termNotEnglish(synonym) && synonym.length() < 255  && !synonym.contains("Beilstein Handbook Reference")){
					terms.add(new TermStore(synonym));
				}
			}
		}
		return terms;
	}

	private List<String> findClassificationCodes(){
		List<String> semTypes = new ArrayList<String>();
		String[] columns = semList.split("<ClassificationCode>");
		for (String column : columns){
			if (column.contains("</ClassificationCode>")){
				String voc = StringUtilities.findBetween(column, "<Source>", "</Source>").trim();
				int substringIndex =  column.indexOf("<SourceList>");
				if(substringIndex < 0){
					substringIndex =  column.indexOf("<SourceList/>");
				}
				String semType = column.substring(0, substringIndex).trim();
				if (voc.equalsIgnoreCase("RTECS") || voc.equalsIgnoreCase("IARC") || voc.equalsIgnoreCase("NTPA")){
					if (semType.equalsIgnoreCase("DNA topoisomerase II inhibitors")) semType = "DNA topoisomerase II inhibitor";
					if (semType.equalsIgnoreCase("Skin / eye Irritation")) semType = "Skin / Eye Irritant";
					if (semType.equalsIgnoreCase("Skin /eye Irritation")) semType = "Skin / Eye Irritant";
					if (semType.equalsIgnoreCase("Reasonlly anticipated as a human carcinogen")) semType = "Reasonably anticipated to be a human carcinogen";
					if (semType.equalsIgnoreCase("Overall Carcinogenic Evaluation: 2A")) semType = "Overall Carcinogenic Evaluation: Group 2A";
					if (semType.equalsIgnoreCase("Overall Carcinogenic Evaluation: 2B")) semType = "Overall Carcinogenic Evaluation: Group 2B";
					if (semType.equalsIgnoreCase("Overall Carcinogenic Evaluation: Group 2B (auramine, technical-grade)")) semType = "Overall Carcinogenic Evaluation: Group 2B";
					if (semType.equalsIgnoreCase("Overall Carcinogenic Evaluation: Group 1 (manufacture of auramine)")) semType = "Overall Carcinogenic Evaluation: Group 1";
					semTypes.add(semType);
				}
			}
		}
		return semTypes;
	}

	private List<String> findSuperListClassificationCodes(){
		List<String> semTypes = new ArrayList<String>();
		String[] columns = semList.split("<SuperlistClassCode>");
		for (String column : columns){
			if (column.contains("</SuperlistClassCode>")){
				String voc = StringUtilities.findBetween(column, "<Source>", "</Source>").trim();
				int substringIndex =  column.indexOf("<SourceList>");
				if(substringIndex < 0){
					substringIndex =  column.indexOf("<SourceList/>");
				}
				String semType = column.substring(0, substringIndex).trim();
				if (voc.equalsIgnoreCase("IARC") || voc.equalsIgnoreCase("NTPA")){
					if (semType.equalsIgnoreCase("DNA topoisomerase II inhibitors")) semType = "DNA topoisomerase II inhibitor";
					if (semType.equalsIgnoreCase("Skin / eye Irritation")) semType = "Skin / Eye Irritant";
					if (semType.equalsIgnoreCase("Skin /eye Irritation")) semType = "Skin / Eye Irritant";
					if (semType.equalsIgnoreCase("Reasonlly anticipated as a human carcinogen")) semType = "Reasonably anticipated to be a human carcinogen";
					if (semType.equalsIgnoreCase("Overall Carcinogenic Evaluation: 2A")) semType = "Overall Carcinogenic Evaluation: Group 2A";
					if (semType.equalsIgnoreCase("Overall Carcinogenic Evaluation: 2B")) semType = "Overall Carcinogenic Evaluation: Group 2B";					
					if (semType.equalsIgnoreCase("Overall Carcinogenic Evaluation: Group 2B (auramine, technical-grade)")) semType = "Overall Carcinogenic Evaluation: Group 2B";
					if (semType.equalsIgnoreCase("Overall Carcinogenic Evaluation: Group 1 (manufacture of auramine)")) semType = "Overall Carcinogenic Evaluation: Group 1";
					semTypes.add(semType);
				}
			}
		}
		return semTypes;
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

	private Map<String, Integer> semanticTypes = new HashMap<String, Integer>();

	private void addSemanticType(String semString){
		Integer semID = semanticTypes.get(semString);
		if (semID == null) {
			semID = beginSemID - semanticTypes.size();
			semanticTypes.put(semString, semID);
			Concept semanticType = new Concept(semID);
			semanticType.setName(semString);
			chemIDplusOntology.setConcept(semanticType);
		}
		if (!foundSemTypesForConcept.contains(semID)) {
			Relation relation = new Relation(concept.getID(), DefaultTypes.isOfSemanticType, semID);
			chemIDplusOntology.setRelation(relation);
			foundSemTypesForConcept.add(semID);
		}
	}

	private String findDefinition(){
		int substringIndex =  record.indexOf("<SourceList>", record.indexOf("<Note>"));
		if(substringIndex < 0){
			substringIndex =  record.indexOf("<SourceList/>", record.indexOf("<Note>"));
		}
		String definition = record.substring(record.indexOf("<Note>"), substringIndex).trim();
		String finalDefinition = definition.substring(6);
		//  If longer than 1024 characters, then substring and add a full stop.
		if (!finalDefinition.endsWith(".") && finalDefinition.length()<=1024){
			finalDefinition = finalDefinition+".";
		} else if (!finalDefinition.endsWith(".") && finalDefinition.length()>1024){
			finalDefinition = finalDefinition.substring(0, 1023)+".";
		}
		definition = finalDefinition;
		return definition;
	}

	private static void mergeSemanticTypes(Ontology ontology, int fromCUI, int toCUI, boolean removeFromConcept) {
		if (fromCUI == toCUI) {
			System.out.println("ERROR: attempted to merge " + fromCUI + " to itself!");
		}
		else {
			Concept fromConcept = ontology.getConcept(fromCUI);
			Concept toConcept = ontology.getConcept(toCUI);
			if (fromConcept != null && toConcept != null) {
				String name = fromConcept.getName();
				toConcept.setName(name);

				List<Relation> fromRelationsSub = ontology.getRelationsForConceptAsSubject(fromCUI);
				List<Relation> fromRelationsObj = ontology.getRelationsForConceptAsObject(fromCUI);
				List<DatabaseID> fromDbIDs = ontology.getDatabaseIDsForConcept(fromCUI);

				if (!fromConcept.getDefinition().equals("")) {
					String def = fromConcept.getDefinition();
					if (!toConcept.getDefinition().equals("")) {
						def = toConcept.getDefinition() + ";" + def;
					}
					toConcept.setDefinition(def);
				}
				for (Relation relation: fromRelationsObj) {
					relation.object = toCUI;
					ontology.setRelation(relation);
				}
				for (Relation relation: fromRelationsSub) {
					relation.subject = toCUI;
					ontology.setRelation(relation);
				}
				for (DatabaseID databaseID: fromDbIDs) {
					ontology.setDatabaseIDForConcept(toCUI, databaseID);
				}
				if (removeFromConcept)
					ontology.removeConcept(fromCUI);
			}
			else {
				System.out.println("Attempted merge with a non existing Concept: either " + toCUI + " and/or " + fromCUI);
			}
		}

	}
}
