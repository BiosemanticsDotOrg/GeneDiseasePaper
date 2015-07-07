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

package org.erasmusmc.dataimport.genes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.OntologyManager;
import org.erasmusmc.ontology.OntologyPSFLoader;
import org.erasmusmc.ontology.OntologyStore;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

/**
 * Fetches the EMBL identifiers from SwissProt and adds them to the thesaurus.  
 * 
 * @author Schuemie
 *
 */public class ImportEMBLIdentifiersFromSwissProt {

    public static void main(String[] args){
      new ImportEMBLIdentifiersFromSwissProt();
    }
    
    public ImportEMBLIdentifiersFromSwissProt(){
      //Load ontology
      System.out.println(StringUtilities.now() + "\tLoading ontology");
      OntologyManager manager = new OntologyManager();
      OntologyStore ontology = manager.fetchStoreFromDatabase("Homologene_curated_may2007");
      //OntologyPSFLoader loader1 = new OntologyPSFLoader();
      //loader1.loadFromPSF("/home/public/thesauri/GenelistHuman/GenelistHuman_v2.4.0.psf");
      //OntologyStore ontology = loader1.ontology;
      ontology.createIndexForDatabaseIDs();
      
      //Process SwissProt file
      System.out.println(StringUtilities.now() + "\tProcessing SwissProt file");
      ReadTextFile file = new ReadTextFile("/data/SwissProt/uniprot_sprot.dat");
      WriteTextFile log = new WriteTextFile("/temp/EMBLIDlog.txt");
      Iterator<String> iterator = file.getIterator();
      Set<String> SPIDs = new HashSet<String>();
      Set<String> EMBLIDs = new HashSet<String>();
      boolean correctOrganism = false;
      while (iterator.hasNext()){
        String line = iterator.next();
        if (line.startsWith("//")){
          if (correctOrganism){
            Set<Integer> CIDs = new HashSet<Integer>();
            for (String SPID : SPIDs){
              DatabaseID dbID = new DatabaseID("SP", SPID);
              CIDs.addAll(ontology.getConceptIDs(dbID));
            }
            if (CIDs.size() == 1){
              for (Integer CID : CIDs)
                for (String EMBLID : EMBLIDs)
                  ontology.setDatabaseIDForConcept(CID, new DatabaseID("EMBL", EMBLID));
            } else              
              log.writeln(("Incorrect number ("+CIDs.size()+") of CIDs for :" + SPIDs.toString()));
          }
          SPIDs.clear();
          EMBLIDs.clear();
          correctOrganism = false;
        } else if (line.startsWith("AC   ")){
          String[] tempSPIDs = line.substring(4).split(";");
          for (String SPID : tempSPIDs)
            SPIDs.add(SPID.trim());
        } else if (line.startsWith("DR   EMBL; ")){
          String[] tempEMBL = line.split(";");
          EMBLIDs.add(tempEMBL[1].trim());
        } else if (line.startsWith("OS   ")){
          if (line.equals("OS   Homo sapiens (Human).") ||
              line.equals("OS   Mus musculus (Mouse).") ||
              line.equals("OS   Rattus norvegicus (Rat)."))
            correctOrganism = true;
        }
      }
      log.close();
      
      //Save ontology
      System.out.println(StringUtilities.now() + "\tSave ontology");
      OntologyPSFLoader loader = new OntologyPSFLoader();
      loader.ontology = ontology;
      loader.saveToPSF("/temp/Homologe_EMBL.psf");
      
      
    }
}
