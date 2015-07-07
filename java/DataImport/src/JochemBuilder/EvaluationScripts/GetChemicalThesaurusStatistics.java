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

package JochemBuilder.EvaluationScripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class GetChemicalThesaurusStatistics {
	

    public static String home = "/home/khettne/Projects/Jochem/";

    public static String ontologyFile = home+"Jochem_V1_2.ontology";
    public static String statistics = home+"Jochem_V1_2.statistics";
    public static String casAndInchi = home+"Jochem_V1_2_casAndInchi.txt";

	public static void main(String[] args) {
	    System.out.println("Starting script "+StringUtilities.now());

	    WriteTextFile out = new WriteTextFile(statistics); 
	    WriteTextFile out2 = new WriteTextFile(casAndInchi); 

	    System.out.println("Loading ontology "+StringUtilities.now());
	    OntologyFileLoader ontologyLoader = new OntologyFileLoader();
	    OntologyStore ontology = ontologyLoader.load(ontologyFile);
	    Iterator<Concept> conceptIterator = ontology.getConceptIterator();

	    //Set<String> casnumbers = new TreeSet<String>();
	    Map<String, Integer> casMap = new HashMap<String, Integer>();
	    //Set<String> inchiCodes = new TreeSet<String>();
	    Map<String, Integer> inChiMap = new HashMap<String, Integer>();

	    Map<Integer, String> cuiToDbidMap = new HashMap<Integer, String>();
	    Map<String, List<Integer>> casOrInChIToCuiMap = new HashMap<String, List<Integer>>();

	    int lineCount = 0;
	    while (conceptIterator.hasNext()) {
	      lineCount++;
	      if (lineCount % 10000 == 0)
	        System.out.println(lineCount);
	      Concept concept = conceptIterator.next();
	      if (concept.getID()>0){
	        int noOfConceptCAS = 0;
	        int noOfConceptInChi = 0 ;
	        noOfConcepts++;
	        int termListSize = concept.getTerms().size();
	        noOfTerms = noOfTerms + termListSize;
	        List<DatabaseID> dbIds = ontology.getDatabaseIDsForConcept(concept.getID());
	        for (DatabaseID id: dbIds){
	          if (id.database.equals("CAS")){
	            noOfCAS++;
	            noOfConceptCAS++;
	            List<Integer> cuis = casOrInChIToCuiMap.get(id.ID);
	            if (cuis==null){
	              cuis = new ArrayList<Integer>();
	              cuis.add(concept.getID());
	              casOrInChIToCuiMap.put(id.ID, cuis);
	            } else {
	              cuis.add(concept.getID());
	              casOrInChIToCuiMap.put(id.ID, cuis);
	            }
	            if (!casMap.containsKey(id.ID)){
	              casMap.put(id.ID, 1);              
	            } else {
	              Integer value = casMap.get(id.ID);
	              if (value==null){
	                casMap.put(id.ID, 2);
	              } else {
	                value = value+1;
	                casMap.put(id.ID, value);
	              }
	            }
	          }          
	          if (id.database.equals("INCH")){
	            noOfInChI++;
	            noOfConceptInChi++;
	            List<Integer> cuis = casOrInChIToCuiMap.get(id.ID);
	            if (cuis==null){
	              cuis = new ArrayList<Integer>();
	              cuis.add(concept.getID());
	              casOrInChIToCuiMap.put(id.ID, cuis);
	            } else {
	              cuis.add(concept.getID());
	              casOrInChIToCuiMap.put(id.ID, cuis);
	            }
	            if (!inChiMap.containsKey(id.ID)){
	              inChiMap.put(id.ID, 1);
	            } else {
	              Integer value = inChiMap.get(id.ID);
	              if (value==null){
	                inChiMap.put(id.ID, 2);
	              } else {
	                value = value+1;
	                inChiMap.put(id.ID, value);
	              }
	            }
	          }          
	          if (id.database.equals("PUBC")) noOfPubChemC++;
	          if (id.database.equals("PUBS")) noOfPubChemS++;
	          if (id.database.equals("KEGD")) noOfKEGGd++;
	          if (id.database.equals("KEGG")) noOfKEGGc++;
	          if (id.database.equals("CHEB")) noOfChebi++;
	          if (id.database.equals("DRUG")){
	            noOfDrugBank++;
	            cuiToDbidMap.put(concept.getID(), id.ID);
	          }
	          if (id.database.equals("HMDB")) noOfHmbd++;
	          if (id.database.equals("CHID")) noOfChemIDplus++;

	        }
	        if (noOfConceptCAS==1){
	          noOf1CAS++;
	          out2.writeln(concept.getID().toString()+"\t"+"1");
	        } else if (noOfConceptCAS==2){
	          noOf2CAS++;
	          out2.writeln(concept.getID().toString()+"\t"+"2");
	        } else if (noOfConceptCAS==3){
	          noOf3CAS++;
	          out2.writeln(concept.getID().toString()+"\t"+"3");
	        } else if (noOfConceptCAS==4){
	          noOf4CAS++;
	          out2.writeln(concept.getID().toString()+"\t"+"4");
	        } else if (noOfConceptCAS==5){
	          noOf5CAS++;
	          out2.writeln(concept.getID().toString()+"\t"+"5");
	        } else if (noOfConceptCAS>=5 && noOfConceptCAS<=9){
	          noOf5To9CAS++;
	          out2.writeln(concept.getID().toString()+"\t"+"5-9");
	        } else if (noOfConceptCAS>=10 && noOfConceptCAS<=19){
	          noOf10To19CAS++;
	          out2.writeln(concept.getID().toString()+"\t"+"10-19");
	        }else if (noOfConceptCAS>=20){
	          noOfmorethan20CAS++;
	          out2.writeln(concept.getID().toString()+"\t"+">=20");
	        }
	        if (noOfConceptInChi==1){
	          noOf1InChI++;
	          out2.writeln(concept.getID().toString()+"\t"+"1");
	        } else if (noOfConceptInChi==2){
	          noOf2InChI++;
	          out2.writeln(concept.getID().toString()+"\t"+"2");
	        } else if (noOfConceptInChi==3){
	          noOf3InChI++;
	          out2.writeln(concept.getID().toString()+"\t"+"3");
	        } else if (noOfConceptInChi==4){
	          noOf4InChI++;
	          out2.writeln(concept.getID().toString()+"\t"+"4");
	        } else if (noOfConceptInChi==5){
	          noOf5InChI++;
	          out2.writeln(concept.getID().toString()+"\t"+"5");
	        } else if (noOfConceptInChi>=5 && noOfConceptInChi<=9){
	          noOf5To9InChI++;
	          out2.writeln(concept.getID().toString()+"\t"+"5-9");
	        } else if (noOfConceptInChi>=10 && noOfConceptInChi<=19){
	          noOf10To19InChI++;
	          out2.writeln(concept.getID().toString()+"\t"+"10-19");
	        }else if (noOfConceptInChi>=20){
	          noOfmorethan20InChI++;
	          out2.writeln(concept.getID().toString()+"\t"+">=20");
	        }
	      }
	    }

	    noOfUniqueCAS = casMap.size();
	    noOfUniqueInChI = inChiMap.size();

	    out.writeln("Concepts: "+noOfConcepts);
	    out.writeln("Terms: "+noOfTerms);
	    out.writeln("CAS numbers: "+noOfCAS);
	    out.writeln("Unique CAS numbers: "+noOfUniqueCAS);
	    out.writeln("InChI codes: "+noOfInChI);
	    out.writeln("Unique InChI codes: "+noOfUniqueInChI); 
	    out.writeln("PubChem Compound ref: "+noOfPubChemC);
	    out.writeln("PubChem Substance ref: "+noOfPubChemS);
	    out.writeln("KEGG drug ref: "+noOfKEGGd);
	    out.writeln("KEGG compound ref: "+noOfKEGGc);
	    out.writeln("ChEBI ref: "+noOfChebi);
	    out.writeln("DrugBank ref: "+noOfDrugBank);
	    out.writeln("HMDB ref: "+noOfHmbd);
	    out.writeln("ChemIDplus ref: "+noOfChemIDplus);
	    out.writeln("\n");
	    out.writeln("Concepts with more than one CAS number or InChI:");
	    out.writeln("2 CAS: "+noOf2CAS);
	    out.writeln("3 CAS: "+noOf3CAS);
	    out.writeln("4 CAS: "+noOf4CAS);
	    out.writeln("5 CAS: "+noOf5CAS);
	    out.writeln("5-9 CAS: "+noOf5To9CAS);
	    out.writeln("10-19 CAS: "+noOf10To19CAS);
	    out.writeln(">20 CAS: "+noOfmorethan20CAS);
	    out.writeln("2 InChI: "+noOf2InChI);
	    out.writeln("3 InChI: "+noOf3InChI);
	    out.writeln("4 InChI: "+noOf4InChI);
	    out.writeln("5 InChI: "+noOf5InChI);
	    out.writeln("5-9 InChI: "+noOf5To9InChI);
	    out.writeln("10-19 InChI: "+noOf10To19InChI);
	    out.writeln(">20 InChI: "+noOfmorethan20InChI);

	    out.close();
	    out2.close();

	    System.out.println("Done! "+StringUtilities.now());
	  }
	  private static int noOfTerms = 0;
	  private static int noOfConcepts = 0 ;
	  private static int noOfCAS = 0;
	  private static int noOf1CAS = 0;
	  private static int noOf2CAS = 0;
	  private static int noOf3CAS = 0;
	  private static int noOf4CAS = 0;
	  private static int noOf5CAS = 0;
	  private static int noOf5To9CAS = 0;
	  private static int noOf10To19CAS = 0;
	  private static int noOfmorethan20CAS = 0;
	  private static int noOfUniqueCAS = 0;
	  private static int noOfInChI = 0;
	  private static int noOf1InChI = 0;
	  private static int noOf2InChI = 0;
	  private static int noOf3InChI = 0;
	  private static int noOf4InChI = 0;
	  private static int noOf5InChI = 0;
	  private static int noOf5To9InChI = 0;
	  private static int noOf10To19InChI = 0;
	  private static int noOfmorethan20InChI = 0;
	  private static int noOfUniqueInChI = 0;
	  private static int noOfPubChemC = 0;
	  private static int noOfPubChemS = 0;
	  private static int noOfKEGGd = 0;
	  private static int noOfKEGGc = 0;
	  private static int noOfChebi = 0;
	  private static int noOfDrugBank = 0;
	  private static int noOfHmbd = 0;
	  private static int noOfChemIDplus = 0;
	}
