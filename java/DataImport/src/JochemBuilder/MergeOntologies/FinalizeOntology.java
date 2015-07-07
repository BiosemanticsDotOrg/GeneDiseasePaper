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

package JochemBuilder.MergeOntologies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.ontology.ontologyutilities.OntologyUtilities;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

import JochemBuilder.SharedCurationScripts.CurateUsingManualCurationFile;
import JochemBuilder.SharedCurationScripts.MoveINCHIfromDbid2Def;

public class FinalizeOntology {
	public static int umlsSemID = -103;
	public static String umlsSemName = "Chemical";
	public static int vocID = -3000;
	public static String vocName = "CHEMICAL";
	public static String date = "12-01-2011";
	public static String home = "/home/khettne/Projects/Jochem/";

	//public static String oldOntology = home+"chem_merged_curated4_"+date+".ontology";
	public static String oldOntology = home+"chem_merged_curated4_22-12-2010.ontology";
	public static String newOntologyName = "Jochem_V1_5";
	public static String newOntology = home+newOntologyName+".ontology";

	public static String termsToRemove = "JochemTermsToRemove.txt";
	public static String filterLog =  home+"/Jochem_removedTerms_MartijnsRulerule"+date+".log";
	public static String curatedLog =  home+"/Jochem_removedTerms_"+date+".log";

	public static boolean moveInchi = true;

	public static void main(String[] args) {
		WriteTextFile logfile = new WriteTextFile(filterLog);
		
		System.out.println("Loading ontology. "+StringUtilities.now());
		OntologyFileLoader loader = new OntologyFileLoader();
		OntologyStore oldChemOntology = new OntologyStore();
		oldChemOntology = loader.load(oldOntology);

		OntologyStore newChemOntology = new OntologyStore();
		newChemOntology.setName(newOntologyName);
		Concept semantictype = new Concept(umlsSemID);
		semantictype.setName(umlsSemName);
		newChemOntology.setConcept(semantictype);

		Concept vocabulary = new Concept(vocID);
		vocabulary.setName(vocName);
		newChemOntology.setConcept(vocabulary);

		System.out.println("Iterating. "+StringUtilities.now());
		Iterator<Concept> conceptIterator = oldChemOntology.getConceptIterator();
		//Re-enumerate ontology and remove terms based on Martijns rule
		Integer conceptCount = 4000000;
		int lineCount = 0;
		while (conceptIterator.hasNext()) {
			lineCount++;
			if (lineCount % 10000 == 0)
				System.out.println(lineCount);
			Concept concept = conceptIterator.next();
			if (concept.getID()>0){
				Concept copyConcept = new Concept(conceptCount);
				copyConcept.setName(concept.getName());
				Iterator<TermStore> termIterator = concept.getTerms().iterator();
				while (termIterator.hasNext()) {
					TermStore term = termIterator.next();
					if (OntologyUtilities.MartijnsFilterRule(term.text, stopwordsForFiltering) || term.text.length()<OntologyUtilities.minTermSize){
						termIterator.remove();
						logfile.writeln(term.text + "|"+concept.getName() +"|"+ concept.getID());
					} //else if (term.text.endsWith("ase") || term.text.endsWith("ASE"))
//						logfile.writeln(term.text + "|"+concept.getName() +"|"+ concept.getID());
//						termIterator.remove();
				}
				copyConcept.setTerms(concept.getTerms());
				copyConcept.setDefinition(concept.getDefinition());
				if (!copyConcept.getTerms().isEmpty()){
					newChemOntology.setConcept(copyConcept);
					List<DatabaseID> databaseIDs = oldChemOntology.getDatabaseIDsForConcept(concept.getID());
					for (DatabaseID databaseID: databaseIDs){
						newChemOntology.setDatabaseIDForConcept(copyConcept.getID(), databaseID);
					}
					Relation vocRelation = new Relation(copyConcept.getID(), DefaultTypes.fromVocabulary, vocID);
					newChemOntology.setRelation(vocRelation);
					Relation semRelation = new Relation(copyConcept.getID(), DefaultTypes.isOfSemanticType, umlsSemID);
					newChemOntology.setRelation(semRelation);
					conceptCount++;
				}
			}
		}
		logfile.close();
		
		//Remove terms based on medline frequency
		CurateUsingManualCurationFile curate = new CurateUsingManualCurationFile(); 
		newChemOntology = curate.run(newChemOntology, curatedLog,termsToRemove);

		//Set flags
		OntologyCurator curator = new OntologyCurator();
		curator.curateAndPrepare(newChemOntology);

		if (moveInchi){
			MoveINCHIfromDbid2Def move = new MoveINCHIfromDbid2Def();
			newChemOntology = move.run(newChemOntology);
		}

		System.out.println("Saving to ontology file. "+StringUtilities.now());
		OntologyFileLoader loader2 = new OntologyFileLoader();
		loader2.save(newChemOntology,newOntology);
	}

	private static Set<String> stopwordsForFiltering = getDefaultStopWordsForFiltering();
	private static Set<String> getDefaultStopWordsForFiltering() {
		Set<String> result = new TreeSet<String>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(OntologyCurator.class.getResourceAsStream("DefaultStopwordsForFiltering.txt")));
		try {
			while (bufferedReader.ready()) {
				result.add(bufferedReader.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
