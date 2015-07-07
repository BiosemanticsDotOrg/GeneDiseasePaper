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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.utilities.ReadTextFile;

/**
 * Class for retrieving Medline records from our local database using multithreading.
 * 
 * @author schuemie
 *
 */
public class MedlineRecordIterator implements Iterator<MedlineRecord> {
	
	private FetchRecordsthread medlineThread;
	private Iterator<String> lineIterator;
	private Iterator<MedlineRecord> bufferIterator;
	private boolean isFetching = false;
	public static int batchSize = 1000;
	
	/**
	 * Some test code. Please ignore
	 * @param args
	 */
	public static void main(String[] args){
		FetchSettings fetchSettings = new FetchSettings();
		fetchSettings.fetchMesh = true;
		MedlineRecordIterator iterator = new MedlineRecordIterator("/home/public/PMIDs/Random10.000.PMIDs",fetchSettings);
		//MedlineRecordIterator iterator = new MedlineRecordIterator("/home/temp/PMIDsToBeIndexed.txt");
		int count = 0;
		while (iterator.hasNext()){
			count++;
			MedlineRecord record = iterator.next();
			if (count % 1000 == 0){
				System.out.println(count + "\t" + record.title);
				for (MeSHHeader header : record.meshHeaders){
					if (header.qualifier == null)
						System.out.println("- " + header.descriptor + (header.descriptorMajor?"*":""));
					else
					  System.out.println("- " + header.descriptor + (header.descriptorMajor?"*":"") + "/" + header.qualifier + (header.qualifierMajor?"*":""));
				}
			}
		}
		System.out.println("Retrieved: " + count);
		System.out.println("Unretrieved: " + iterator.getUnretrievedPMIDs().size());
	}
	
	/**
	 * Creates an iterator that iterates over the records with PMIDs specified in the pmid file (one PMID per line).
	 * Uses the standard fetch settings (only titles and abstracts)
	 * 
	 * @param pmidFilename
	 */
	public MedlineRecordIterator(String pmidFilename) {
		FetchSettings fetchSettings = new FetchSettings();
		init(pmidFilename, fetchSettings);
	}
	
	/**
	 * Creates an iterator that iterates over the records with PMIDs specified in the pmid file (one PMID per line)
	 * using the fetch settings specified.
	 * 
	 * @param pmidFilename
	 * @param fetchSettings
	 */
	public MedlineRecordIterator(String pmidFilename, FetchSettings fetchSettings) {
		init(pmidFilename, fetchSettings);
	}
	
	/**
	 * Returns the PMIDs that could not be retrieved from the database (so far)
	 * 
	 * @return List of PMIDs
	 */
	public List<Integer> getUnretrievedPMIDs(){
		return medlineThread.unretrievedPmids;
	}
	
	private void init(String pmidFilename, FetchSettings fetchSettings) {
		ReadTextFile pmidFile = new ReadTextFile(pmidFilename);
		lineIterator = pmidFile.iterator();
		
    medlineThread = new FetchRecordsthread();
    medlineThread.fetchTitleAndAbstract = fetchSettings.fetchTitleAndAbstract;
    medlineThread.fetchGeneSymbol = fetchSettings.fetchGeneSymbol;
    medlineThread.fetchMesh = fetchSettings.fetchMesh;
    medlineThread.fetchGeneSymbol = fetchSettings.fetchGeneSymbol;
    medlineThread.fetchJournal = fetchSettings.fetchJournal;
    medlineThread.fetchJournalShortForm = fetchSettings.fetchJournalShortForm;
    medlineThread.fetchSubstances = fetchSettings.fetchSubstances;
    medlineThread.fetchPublicationType = fetchSettings.fetchPublicationType;
    medlineThread.fetchPublicationDate = fetchSettings.fetchPublicationDate;
    medlineThread.fetchAuthors = fetchSettings.fetchAuthors;
    medlineThread.fetchLanguage = fetchSettings.fetchLanguage;
    medlineThread.fetchAffiliation = fetchSettings.fetchAffiliation;
    medlineThread.fetchISSN = fetchSettings.fetchISSN;
    medlineThread.fetchVolumeIssuePages = fetchSettings.fetchVolumeIssuePages;    
    
    startFetch();
    do {
      copyFetchedToBuffer();
      startFetch();
    } while (!bufferIterator.hasNext() && isFetching);
	}
	
	private void copyFetchedToBuffer() {
		if (isFetching)
			medlineThread.waitUntilFinished();
		isFetching = false;
		bufferIterator = medlineThread.records.iterator();
	}

	private void startFetch() {
		List<Integer> pmids = new ArrayList<Integer>(batchSize);
		while (lineIterator.hasNext() && pmids.size() < batchSize){
			String pmid = lineIterator.next();
			pmids.add(Integer.parseInt(pmid));
		}
		if (pmids.size() == 0) {
			isFetching = false;
			medlineThread.terminate();
		} else {
		  medlineThread.pmids = pmids;
		  medlineThread.proceed();
		  isFetching = true;
		}
	}

	@Override
	public boolean hasNext() {
		return (bufferIterator.hasNext());
	}

	@Override
	public MedlineRecord next() {
		MedlineRecord next = bufferIterator.next();
		while (!bufferIterator.hasNext() && isFetching){
			copyFetchedToBuffer();
			startFetch();
		}
		return next;
	}

	@Override
	public void remove() {
		System.err.println("Calling unimplemented method remove() in class " + this.getClass().getCanonicalName());
	}

	public static class FetchSettings {
		public boolean fetchTitleAndAbstract = true;
		public boolean fetchMesh = false;
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
		public boolean fetchVolumeIssuePages = false;
	}

}
