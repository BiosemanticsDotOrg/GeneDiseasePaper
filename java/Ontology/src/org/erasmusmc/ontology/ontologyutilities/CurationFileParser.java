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

package org.erasmusmc.ontology.ontologyutilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.peregrine.SimpleTokenizer;
import org.erasmusmc.textMining.LVG.LVGNormaliser;
//import org.erasmusmc.utilities.LVGNormaliser;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;

public class CurationFileParser {

	public Map<String, Set<String>> suppressedTermsPerVoc = new HashMap<String, Set<String>>();
	public Set<String> suppressedTermsAllVocs = new HashSet<String>();
	public Map<DatabaseID, Set<String>> suppressedTermsPerDatabaseID = new HashMap<DatabaseID, Set<String>>();
	public List<DatabaseID> suppressedWholeUMLSConcepts = new ArrayList<DatabaseID>();
	public Map<DatabaseID, Set<String>> addedTermsPerDatabaseID = new HashMap<DatabaseID, Set<String>>();
	public List<DatabaseIDmap> mappingsFromToDBID = new ArrayList<DatabaseIDmap>();
	public NormaliseUsingLVG lvg = new NormaliseUsingLVG();

	/**
	 * Reads a curation file and stores the information in the public variables of this class
	 * @param curationInstructionsFile
	 */
	public CurationFileParser(String curationInstructionsFile) {
		for (String line: new ReadTextFile(curationInstructionsFile)){
			if (!line.startsWith("#")) { // check if it is not a comment line!
				List<String> cells = StringUtilities.safeSplit(line, '|');
				if (cells.size() > 1)
					if (cells.get(0).equals("ADD") && cells.get(1).equals("DBLINK")) {
						String dbID = cells.get(2);
						String dbEntry = cells.get(3);
						DatabaseID databaseID = new DatabaseID(dbID, dbEntry);
						Set<String> addedTerms = new TreeSet<String>();
						for (int i = 4; i < cells.size(); i++) 
							addedTerms.add(cells.get(i));
						addedTermsPerDatabaseID.put(databaseID, addedTerms);
					} else if (cells.get(0).equals("SUPPRESS")) {
						if (cells.get(1).equals("VOC")) {
							String voc = cells.get(2);
							if (voc.equals("ALL")){
								suppressedTermsAllVocs.add(normalizeTerm(cells.get(3)));
								if (!OntologyUtilities.isGeneSymbol(cells.get(3)))
									suppressedTermsAllVocs.add(lvg.lvgnormalise(cells.get(3)));
							}else {
								Set<String> suppressedTerms = suppressedTermsPerVoc.get(voc);
								if (suppressedTerms == null) {
									suppressedTerms = new HashSet<String>();
									suppressedTermsPerVoc.put(voc, suppressedTerms);
								}
								if (!OntologyUtilities.isGeneSymbol(cells.get(3)))
									suppressedTerms.add(lvg.lvgnormalise(cells.get(3)));
								suppressedTerms.add(normalizeTerm(cells.get(3)));
							}
						}
						else if (cells.get(1).equals("DBLINK")) {
							String dbID = cells.get(2);
							String dbEntry = cells.get(3);
							DatabaseID databaseID = new DatabaseID(dbID, dbEntry);
							Set<String> suppressedTerms = new TreeSet<String>();
							for (int i = 4; i < cells.size(); i++) {
								suppressedTerms.add(normalizeTerm(cells.get(i)));
								if (!OntologyUtilities.isGeneSymbol(cells.get(3)))
									suppressedTerms.add(lvg.lvgnormalise(cells.get(i)));
							}
							if (!suppressedTermsPerDatabaseID.containsKey(databaseID)){
								suppressedTermsPerDatabaseID.put(databaseID, suppressedTerms);
							}else suppressedTermsPerDatabaseID.get(databaseID).addAll(suppressedTerms);
						}

					}
					else if (cells.get(0).equals("MAP")) {
						if (cells.get(1).equals("DBLINK")) {
							String dbIDfrom = cells.get(2);
							String dbEntryfrom = cells.get(3);
							String dbIDto = cells.get(4);
							String dbEntryto = cells.get(5);
							mappingsFromToDBID.add(new DatabaseIDmap(new DatabaseID(dbIDfrom, dbEntryfrom), new DatabaseID(dbIDto, dbEntryto)));
						}
					}
					else if (cells.get(0).equals("SUPPRESS_WHOLE_UMLSCONCEPT")) {
						if (cells.get(1).equals("DBLINK")) {
							String dbID = cells.get(2);
							String dbEntry = cells.get(3);
							if (dbID.equals("UMLS")){
								suppressedWholeUMLSConcepts.add(new DatabaseID(dbID, dbEntry));
							}
						}
					}
			}
		}
	}

	/**
	 * Normalizes (tokenization, conversion to lowercase) the input string, to compare it with the terms found in the curation file
	 * @param string
	 * @return	normalized string
	 */
	public static String normalizeTerm(String string){
		SimpleTokenizer tokenizer = new SimpleTokenizer();
		tokenizer.tokenize(string);
		StringBuilder sb = new StringBuilder();
		for (String token : tokenizer.tokens){
			token = StringUtilities.firstLetterToLowerCase(token);
			//if (!StringUtilities.isAbbr(token))
			//	token = token.toLowerCase();
			if (!OntologyUtilities.stopwordsForIndexing.contains(token)){
				if (sb.length() != 0)
					sb.append(' ');
				sb.append(token);
			}
		}

		return sb.toString();
	}

	public class DatabaseIDmap {
		DatabaseID from;
		DatabaseID to;

		public DatabaseIDmap(DatabaseID from, DatabaseID to) {
			this.from = from;
			this.to = to;
		}
	}

	private class NormaliseUsingLVG extends LVGNormaliser{
		public String lvgnormalise(String string){
			return externalnormalise(string);
		}
	}

}
