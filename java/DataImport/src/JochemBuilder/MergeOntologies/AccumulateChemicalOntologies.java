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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.ontology.Relation;
import org.erasmusmc.utilities.StringUtilities;

public class AccumulateChemicalOntologies {
    public static String home = "/home/khettne/Projects/Jochem";
    public static String date = "24-10-2010";
    public static String parameter = "curated";
    
    public static String newOntologyPath = home+"/chem_accumulated_"+parameter+"_"+date+".ontology";
    
    public static String firstOntologyPath = home+"/ChemIDplus/ChemIDplusCAS_"+parameter+"_"+date+".ontology";
    public static String secondOntologyPath = home+"/KEGG/Drug/KEGGdCAS_"+parameter+"_"+date+".ontology";
    public static String thirdOntologyPath = home+"/KEGG/Compound/KEGGcCAS_"+parameter+"_"+date+".ontology";
    public static String fourthOntologyPath = home+"/UMLSCHEM/UMLSCHEMCAS_"+parameter+"_"+date+".ontology";
    public static String fifthOntologyPath = home+"/HMDB/HMDBCAS_"+parameter+"_"+date+".ontology";
    public static String sixthOntologyPath = home+"/ChEBI/ChEBICAS_"+parameter+"_"+date+".ontology";
    public static String seventhOntologyPath = home+"/DrugBank/DrugBankCAS_"+parameter+"_"+date+".ontology";
    public static String ninthOntologyPath = home+"/MeSH/MeSHCAS_"+parameter+"_"+date+".ontology";
    public static String tenthOntologyPath = home+"/MeSH/MeSHSuppCAS_"+parameter+"_"+date+".ontology";
	
	  public static void main(String[] args) {
		    System.out.println("Starting script "+StringUtilities.now());
		    System.out.println("Loading thesauri "+StringUtilities.now());
		    OntologyFileLoader firstOntologyLoader = new OntologyFileLoader();
		    OntologyStore firstOntology = firstOntologyLoader.load(firstOntologyPath);    
		    firstOntology.setName("ChemIDplus");
		    
		    OntologyFileLoader secondOntologyLoader = new OntologyFileLoader();
		    OntologyStore secondOntology = secondOntologyLoader.load(secondOntologyPath);
		    secondOntology.setName("KEGGdrug");
		    
		    OntologyFileLoader thirdOntologyLoader = new OntologyFileLoader();
		    OntologyStore thirdOntology = thirdOntologyLoader.load(thirdOntologyPath);
		    thirdOntology.setName("KEGGcompound");
		    
		    OntologyFileLoader fourthOntologyLoader = new OntologyFileLoader();
		    OntologyStore fourthOntology = fourthOntologyLoader.load(fourthOntologyPath);
		    fourthOntology.setName("UMLSCHEM");
		    
		    OntologyFileLoader fifthOntologyLoader = new OntologyFileLoader();
		    OntologyStore fifthOntology = fifthOntologyLoader.load(fifthOntologyPath);
		    fifthOntology.setName("HMDB");
		    
		    OntologyFileLoader sixthOntologyLoader = new OntologyFileLoader();
		    OntologyStore sixthOntology = sixthOntologyLoader.load(sixthOntologyPath);
		    sixthOntology.setName("ChEBI");
		    
		    OntologyFileLoader seventhOntologyLoader = new OntologyFileLoader();
		    OntologyStore seventhOntology = seventhOntologyLoader.load(seventhOntologyPath);
		    seventhOntology.setName("DrugBank");
		    
		    /** old code for pubchem
		    //ChemicalPsfLoader eightOntologyLoader = new ChemicalPsfLoader();
		    //eightOntologyLoader.loadFromPSF(eightPsf);    
		    //OntologyStore eightOntology = eightOntologyLoader.ontology;
		    //eightOntology.setName("PubChem");*/

		    OntologyFileLoader ninthOntologyLoader = new OntologyFileLoader();
		    OntologyStore ninthOntology = ninthOntologyLoader.load(ninthOntologyPath);
		    ninthOntology.setName("MeSHheadings");

		    OntologyFileLoader tenthOntologyLoader = new OntologyFileLoader();
		    OntologyStore tenthOntology = tenthOntologyLoader.load(tenthOntologyPath);
		    tenthOntology.setName("MeSHsupp");

		    OntologyStore accumulatedOntology = new OntologyStore();
		    
		    System.out.println("Accumulate thesauri "+StringUtilities.now());
		    accumulatedOntology = addOneThesauriToAnother(firstOntology, accumulatedOntology);
		    accumulatedOntology = addOneThesauriToAnother(secondOntology, accumulatedOntology);
		    accumulatedOntology = addOneThesauriToAnother(thirdOntology, accumulatedOntology);
		    accumulatedOntology = addOneThesauriToAnother(fourthOntology, accumulatedOntology);
		    accumulatedOntology = addOneThesauriToAnother(fifthOntology, accumulatedOntology);
		    accumulatedOntology = addOneThesauriToAnother(sixthOntology, accumulatedOntology);
		    accumulatedOntology = addOneThesauriToAnother(seventhOntology, accumulatedOntology);
		    //accumulatedOntology = addOneThesauriToAnother(eightOntology, accumulatedOntology);
		    accumulatedOntology = addOneThesauriToAnother(ninthOntology, accumulatedOntology);
		    accumulatedOntology = addOneThesauriToAnother(tenthOntology, accumulatedOntology);
		 
		    System.out.println("Saving to ontology file "+StringUtilities.now());
		    OntologyFileLoader loader = new OntologyFileLoader();
		    loader.save(accumulatedOntology,newOntologyPath);
		    System.out.println("Done. "+StringUtilities.now());
		    
		  }
		  
		  public static OntologyStore addOneThesauriToAnother(OntologyStore originalThesaurus, OntologyStore mergedThesaurus){
		    Set<Integer> includedCUIs = new HashSet<Integer>();    
		    Iterator<Concept> conceptIterator = originalThesaurus.getConceptIterator();
		    int lineCount = 0;
		    while (conceptIterator.hasNext()) {
		      lineCount++;
		      if (lineCount % 10000 == 0)
		        System.out.println(lineCount);
		      Concept concept = conceptIterator.next();
		      mergedThesaurus.setConcept(concept);      
		      includedCUIs.add(concept.getID());
		    }
//		  Copy relationships:
		    List<Relation> relations = originalThesaurus.getRelations();
		    for (Relation relation: relations)
		      if (includedCUIs.contains(relation.subject) && includedCUIs.contains(relation.object))
		        mergedThesaurus.setRelation(relation);

		    // Copy databaseIDs:
		    List<DatabaseID> databaseIDs;
		    for (Integer cui: includedCUIs) {
		      databaseIDs = originalThesaurus.getDatabaseIDsForConcept(cui);
		      if (databaseIDs != null)
		        for (DatabaseID databaseID: databaseIDs)
		          mergedThesaurus.setDatabaseIDForConcept(cui, databaseID);
		    }
		    return mergedThesaurus;
		  }

}
