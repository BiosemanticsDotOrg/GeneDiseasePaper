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

package org.erasmusmc.medline;

/***Usage:
 * Use this class if you want to process a series of Medline records. Records can be selected either 
 * through a file containing a list of PMIDs, or by specifying a year-range. 
 * Call the iterate method to start retrieving the records, pass an object implementing the MedlineListener 
 * interface as parameter and it will be called with batches of records. 
 * Note: uses multi-threading, so expect the next call to be made immediately after the last batch is processed
 */

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.ReadTextFile;

public class MedlineIterator {
  public String pmidsFile = "";
  public List<Integer> givenPmids = null;
  public int beginYear = -1;
  public int endYear = -1;
  public int batchsize = 1000;
  public List<Integer> unretrievedPmids;
  public Boolean verbose = true;
  public boolean fetchTitleAndAbstract = true;
  public boolean fetchMesh = true;
  public boolean fetchGeneSymbol = false;
  public boolean fetchJournal = false;
  public boolean fetchJournalShortForm = false;
  public boolean fetchSubstances = false;
  public boolean fetchPublicationType = false;
  public boolean fetchPublicationDate = false;
  public boolean fetchAuthors = false;
  public boolean fetchLanguage = false;
  public boolean fetchAffiliation = false;
  public boolean fetchISSN = false;
  private FetchRecordsthread medlineThread;
  private MedlineListener listener;

  public void iterate(MedlineListener listener) {
    this.listener = listener;
    if (verbose)
      System.out.println("Connecting to Medline server. " + StringUtilities.now());
    medlineThread = new FetchRecordsthread();
    medlineThread.fetchTitleAndAbstract = fetchTitleAndAbstract;
    medlineThread.fetchGeneSymbol = fetchGeneSymbol;
    medlineThread.fetchMesh = fetchMesh;
    medlineThread.fetchGeneSymbol = fetchGeneSymbol;
    medlineThread.fetchJournal = fetchJournal;
    medlineThread.fetchJournalShortForm = fetchJournalShortForm;
    medlineThread.fetchSubstances = fetchSubstances;
    medlineThread.fetchPublicationType = fetchPublicationType;
    medlineThread.fetchPublicationDate = fetchPublicationDate;
    medlineThread.fetchAuthors = fetchAuthors;
    medlineThread.fetchLanguage = fetchLanguage;
    medlineThread.fetchAffiliation = fetchAffiliation;
    medlineThread.fetchISSN = fetchISSN;

    int cycles = 1;

    List<Integer> pmids = null;

    for (int cycle = 0; cycle < cycles; cycle++) {
      if (verbose)
        System.out.println("Loading PMIDs. " + StringUtilities.now());

      if (givenPmids != null) {
        process(givenPmids);
        System.out.println("Processed " + givenPmids.size() + " PMIDs");
      } else {
        // Load from file

        ReadTextFile textFile = new ReadTextFile(pmidsFile);
        int pmidsPerBatch = 10000;
        int counter = 0;
        List<String> pmidsString = textFile.loadFromFileInBatches(pmidsPerBatch);
        while (pmidsString.size() > 0) {
          pmids = new ArrayList<Integer>(pmidsString.size());
          counter += pmidsString.size();
          System.out.println(counter);
          for (String pmid: pmidsString) {

            try {
              pmids.add(Integer.parseInt(pmid.trim()));
            } catch (NumberFormatException n) {
              System.out.println("Could not convert \"" + pmid + "\" to int");
            }
          }
          process(pmids);

          pmidsString = textFile.loadFromFileInBatches(pmidsPerBatch);
        }
        if (verbose)
          System.out.println("Found " + counter + " PMIDs in file.");
      }
      if (verbose)
        System.out.println("Processing texts. " + StringUtilities.now());

    }
    unretrievedPmids = medlineThread.unretrievedPmids;
    if (verbose)
      System.out.println("Could not retrieve " + unretrievedPmids.size() + " pmids from Database");
    medlineThread.terminate();
  }

  private void process(List<Integer> pmids) {
    List<MedlineRecord> inputBuffer = new ArrayList<MedlineRecord>();
    int offset = 0;
    boolean done = false;
    boolean fetching = false;
    boolean processed = true;

    while (!done) {
      if (!fetching && offset < pmids.size()) {
        medlineThread.pmids.clear();
        for (int i = 0; i < batchsize && i + offset < pmids.size(); i++) {
          medlineThread.pmids.add(pmids.get(i + offset));
        }
        offset = offset + medlineThread.pmids.size();
        // System.out.println(offset);
        medlineThread.proceed();
        fetching = true;
      }

      if (processed) {
        if (fetching) {
          medlineThread.waitUntilFinished();
          inputBuffer = medlineThread.records;
          processed = false;
          fetching = false;
        }
        else {
          done = true;
        }
      }
      else {
        listener.processMedlineRecords(inputBuffer);
        processed = true;
      }
    }
  }
}
