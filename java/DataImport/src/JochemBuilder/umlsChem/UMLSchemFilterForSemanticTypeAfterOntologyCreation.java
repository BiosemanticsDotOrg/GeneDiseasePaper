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

package JochemBuilder.umlsChem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.DefaultTypes;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.utilities.StringUtilities;

public class UMLSchemFilterForSemanticTypeAfterOntologyCreation {

	static int aminoacidPeptideOrProtein =-116;
	static int enzyme =-126;
	static int receptor =-192;
	//static int immunologicFactor = -129;
	static int chemicalViewedFunctionally = -120;
	//static int chemicalViewedStructually = -104;
	static int biomedOrDentalMaterial = -122;
	static int virus = -5;
	static int plant = -2;
	static int chemical = -103;
	static int food = -168;
	static int cell = -25;
	static int geneOrGenome = -28;
	static int spatialConcept = -82;
	static int environmentalEffectOfHumans = -69;
	static int bodySubstance = -31;
	static int clinicalDrug = -200;
	static int medicalDevice = -74;
	static int cellComponent = -26;
	static int nucleotideSequence = -86;
	static int biomedicalOccupationOrdiscipline = -91;
	static int manufacturedObject = -73;
	static int bodyPartOrganOrOrganComponent = -23;
	static int aminoAcidSequence = -87;
	static int classification = -185;
	static int drugDeliveryDevice = -203;
	static int tissue = -24;
	static int bacterium = -7;
	static int fungus = -4;
	static int molecularFunction = -44;

	public static int generalChemicalVocID = -3000;
	public static String generalChemicalVocName = "CHEMICAL";
	public static int specificChemicalVocID = -3009;
	public static String specificChemicalVocName = "UMLSCHEM";
	public static int umlsSemID = -103;
	public static String umlsSemName = "Chemical";


	public void run(String nameOfOldOntologyFile, String nameOfNewOntologyFile) {
		System.out.println("Starting script: "+StringUtilities.now());

		Map<Integer, String> semanticTypes = new HashMap<Integer, String>();
		Ontology filteredOntology = new OntologyStore();
		
		//Set the standard vocabulary and umls semantic type concepts
		Concept generalVocabulary = new Concept(generalChemicalVocID);
		generalVocabulary.setName(generalChemicalVocName);
		filteredOntology.setConcept(generalVocabulary);	
		Concept specificVocabulary = new Concept(specificChemicalVocID);
		specificVocabulary.setName(specificChemicalVocName);
		filteredOntology.setConcept(specificVocabulary);		

		Concept semantictype = new Concept(umlsSemID);
		semantictype.setName(umlsSemName);
		filteredOntology.setConcept(semantictype);

		Set<Integer> includedCUIs = new HashSet<Integer>();
		OntologyFileLoader filteredLoader = new OntologyFileLoader();

		OntologyFileLoader loader = new OntologyFileLoader();
		OntologyStore ontology = loader.load(nameOfOldOntologyFile);
		
		Iterator<Concept> conceptIterator = ontology.getConceptIterator();
		int lineCount = 0;
		while (conceptIterator.hasNext()) {
			lineCount++;
			if (lineCount % 10000 == 0)
				System.out.println(lineCount);
			Concept concept = conceptIterator.next();
			if (concept.getID() < 0 && concept.getID() > -1000){
				semanticTypes.put(concept.getID(), concept.getName());
			}
			else if (concept.getID() > 0){        
				List<Relation> semRelations = ontology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.isOfSemanticType);
				if (!conceptHasUndesiredSemanticType(semRelations, semanticTypes, concept) ){
					includedCUIs.add(concept.getID());
					filteredOntology.setConcept(concept);
					//Set vocabularies and standard semantic type
					Relation generalVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, generalChemicalVocID);
					filteredOntology.setRelation(generalVocRelation);
					Relation specificVocRelation = new Relation(concept.getID(), DefaultTypes.fromVocabulary, specificChemicalVocID);
					filteredOntology.setRelation(specificVocRelation);

					Relation semRelation = new Relation(concept.getID(), DefaultTypes.isOfSemanticType, umlsSemID);
					filteredOntology.setRelation(semRelation);


				}
			}
		}

		// Copy databaseIDs:
		List<DatabaseID> databaseIDs;
		for (int cui: includedCUIs) {
			databaseIDs = ontology.getDatabaseIDsForConcept(cui);
			if (databaseIDs != null)
				for (DatabaseID databaseID: databaseIDs)
					filteredOntology.setDatabaseIDForConcept(cui, databaseID);
		}
		filteredLoader.save((OntologyStore)filteredOntology, nameOfNewOntologyFile);

	}


	public static boolean conceptHasUndesiredSemanticType(List<Relation> semRelations, Map<Integer, String> semanticTypes, Concept concept){
		for (Relation semRelation: semRelations){
			int key = semRelation.object;
			if (undesiredSemanticType().contains(key)){
				return true;
			}
		}
		return false;
	}

	public static Set<Integer> undesiredSemanticType(){
		Set<Integer> result = new TreeSet<Integer>();
		result.add(aminoacidPeptideOrProtein);
		result.add(enzyme);
		result.add(receptor);
		//   result.add(immunologicFactor);
		result.add(chemicalViewedFunctionally);
		// result.add(chemicalViewedStructually);
		result.add(biomedOrDentalMaterial);
		result.add(virus);
		result.add(plant);
		result.add(chemical);
		result.add(food);
		result.add(cell);
		result.add(geneOrGenome);
		result.add(spatialConcept);
		result.add(environmentalEffectOfHumans);
		result.add(bodySubstance);
		result.add(clinicalDrug);
		result.add(medicalDevice);
		result.add(cellComponent);
		result.add(nucleotideSequence);
		result.add(biomedicalOccupationOrdiscipline);
		result.add(manufacturedObject);
		result.add(bodyPartOrganOrOrganComponent);
		result.add(aminoAcidSequence);
		result.add(classification);
		result.add(drugDeliveryDevice);
		result.add(tissue);
		result.add(bacterium);
		result.add(fungus);
		result.add(molecularFunction);
		return result;
	}

}
