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

package org.erasmusmc.conceptprofilegenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Set;

import org.erasmusmc.applications.conceptprofileevaluator.LoadDataFiles;
import org.erasmusmc.applications.conceptprofileevaluator.SubGroundhogStatistics;
import org.erasmusmc.collections.IntList;
import org.erasmusmc.conceptprofilegenerator.generators.CPGeneratorUncertaintyCoefficientConceptFrequencies;
import org.erasmusmc.databases.integersetstore.Integer2IntegerSet;
import org.erasmusmc.databases.integersetstore.IntegerSetStore;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.groundhog.GroundhogStatistics;
import org.erasmusmc.ontology.ConceptProfile;
import org.erasmusmc.ontology.ConceptVectorRecord;

import com.sleepycat.je.DatabaseException;

public class GenerateConceptProfilesFromIntegerSetStore {
  // parameters
  public String integerSetStoreFilename;
  public String sourceGroundhogName;
  public String targetGroundhogName;
  public String groundhogRoot = "";
  public String groundhogStatisticsFilename;  
  public String conceptsToBeFilteredFileName;
  public int minNumberOfPmidsForCp = 5;
  public Integer maximumNumberOfPmidsForCP = 10000;
  public Integer maximumNumberOfConceptsPerCP = 50000;
  public double cutoff = 10E-8;
  public boolean assumeEmptyGroundhog = true; //If false, will check if profile exists in target groundhog

  public static void main(String[] args) throws Exception {
    GenerateConceptProfilesFromIntegerSetStore scriptObject = new GenerateConceptProfilesFromIntegerSetStore();
    scriptObject.integerSetStoreFilename = "/home/jelier/data/Projects/weighted_Globaltest/RandomIntSetStore/";
    scriptObject.sourceGroundhogName = "Groundhog_Medline";
    scriptObject.targetGroundhogName = "RandomCPs";
    scriptObject.groundhogRoot = "/home/jelier/data/";
    scriptObject.groundhogStatisticsFilename = "/home/jelier/data/Groundhog_Medline/GroundhogStatistics.txt";
    scriptObject.conceptsToBeFilteredFileName = "/home/jelier/Projects/weighted_Globaltest/Excl. filter for genes.conceptset";
    scriptObject.minNumberOfPmidsForCp = 5;
    scriptObject.maximumNumberOfPmidsForCP = 300000;
    scriptObject.maximumNumberOfConceptsPerCP = 50000;
    scriptObject.cutoff = 10E-8;

    scriptObject.run();
  } 

  private void initialize() {
    store = new IntegerSetStore(new File(integerSetStoreFilename));
    try {
      sourceGroundhog = new Groundhog(new File(groundhogRoot+ sourceGroundhogName),9000000);
      File targetFile = new File(groundhogRoot+ targetGroundhogName);
      if (!targetFile.exists())
        targetFile.mkdir();
      targetGroundhog = new Groundhog(targetFile,1000000);
    } catch (DatabaseException e1) {
     e1.printStackTrace();
    }
    wholeGroundhogStatistics = new GroundhogStatistics();
    FileInputStream wholeCollexionStatisticsFileStream;
    try {
      wholeCollexionStatisticsFileStream = new FileInputStream(new File(groundhogStatisticsFilename));
      wholeGroundhogStatistics.loadGroundhogStatisticsFromFile(wholeCollexionStatisticsFileStream);
      if (conceptsToBeFilteredFileName != null) {
        FileInputStream conceptsToBeFilteredFile = new FileInputStream(new File(conceptsToBeFilteredFileName));
        conceptsToBeFiltered = LoadDataFiles.loadIDs(conceptsToBeFilteredFile);
      }
      conceptProfileGenerator = new CPGeneratorUncertaintyCoefficientConceptFrequencies(wholeGroundhogStatistics, conceptsToBeFiltered);
      conceptProfileGenerator.maxNumberOfConceptsPerProfile = maximumNumberOfConceptsPerCP;
      conceptProfileGenerator.cutoff = cutoff;
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

  public void run() {
    initialize();
    targetGroundhog.setBulkImportMode(true);
    Iterator<Integer2IntegerSet> iterator = store.iterator();
    int count = 0;
    while (iterator.hasNext()) {
      Integer2IntegerSet entry = iterator.next();
        
      if (assumeEmptyGroundhog || !targetGroundhog.hasEntry(entry.id)) {
        IntList pmids = entry.setofIntegers.getSortedList();
        
        if (pmids.size() >= minNumberOfPmidsForCp) {
          if (pmids.size() > maximumNumberOfPmidsForCP) {
            pmids = pmids.subList(pmids.size() - maximumNumberOfPmidsForCP, pmids.size());
          }
          //System.out.println(entry.id + "\t" + pmids.size() + "\t" + entry.setofIntegers.size());
          SubGroundhogStatistics subGroundhogStatistics = new SubGroundhogStatistics(sourceGroundhog, pmids);
          ConceptProfile conceptProfile = conceptProfileGenerator.generateConceptProfile(subGroundhogStatistics, entry.id);
          ConceptVectorRecord record = conceptProfile.conceptProfileToRecord();
          targetGroundhog.saveConceptVectorRecord(record);
          //target.saveEntryNoCaching(record);
          count++;
          if (count % 1000 == 0)
            System.out.println("Created " + count + " concept profiles");
        }
      }
    }
    System.out.println("Created " + count + " concept profiles");
    //targetGroundhog.setReindexBatchSize(1000);
    //targetGroundhog.setBulkImportMode(false);
  }

  private IntegerSetStore store;
  private Groundhog targetGroundhog;
  private Groundhog sourceGroundhog;
  private GroundhogStatistics wholeGroundhogStatistics;
  private Set<Integer> conceptsToBeFiltered = null;
  private CPGeneratorUncertaintyCoefficientConceptFrequencies conceptProfileGenerator;
}
