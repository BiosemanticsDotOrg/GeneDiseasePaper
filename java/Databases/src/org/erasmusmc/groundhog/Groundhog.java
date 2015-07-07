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

package org.erasmusmc.groundhog;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.erasmusmc.collections.ComparatorFactory;
import org.erasmusmc.collections.IntList;
import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.databases.BatchNumberAndIntegerIDBinding;
import org.erasmusmc.databases.BatchwiseIntegerID;
import org.erasmusmc.math.vector.VectorCursor;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.ConceptVectorRecord;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.storecaching.StoreMapCaching;
import org.erasmusmc.utilities.StringUtilities;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseStats;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

@Deprecated
public class Groundhog extends StoreMapCaching<Integer, ConceptVectorRecord> implements Serializable {
  private static final long serialVersionUID = -1070115985621064906L;
  protected Ontology ontology = null;
  protected String databaseName = "groundhog";
  protected EnvironmentConfig environmentConfig;
  protected Environment environment;
  protected DatabaseConfig databaseConfig;
  protected Database groundhog;

  protected RecordDataBaseBinding recordDatabaseBinding;
  protected TupleBinding integerBinding;
  protected TupleBinding tempkeyBinding;
  protected TupleBinding conceptToRecordIndexEntryBinding;
  protected ConceptToRecordIndex conceptIndex;
  protected Boolean bulkImportMode = false;
  protected ConceptFrequencyCache conceptFrequencyCache;
  protected ConceptSumOfValuesCache conceptSumOfValuesCache;
  protected int cachesize = 302400000;
  protected int reindexBatchSize = 200000; // higher values lead to quicker
  // reindexation but more memory
  // usage
  protected GroundhogShutdown gsh;
  private File datadir;

  public Groundhog(File datadir, int cachesize) throws DatabaseException {
    this.cachesize = cachesize;
    this.datadir = datadir;
    initalize();
  }

  public Groundhog(File datadir) throws DatabaseException {
    this.datadir = datadir;
    initalize();
  }

  private void initalize() throws DatabaseException {
    environmentConfig = new EnvironmentConfig();
    environmentConfig.setAllowCreate(true);
    environmentConfig.setTransactional(true);
    environmentConfig.setCacheSize(cachesize);

    // perform other environment configurations

    environment = new Environment(datadir, environmentConfig);

    databaseConfig = new DatabaseConfig();
    databaseConfig.setAllowCreate(true);
    databaseConfig.setTransactional(true);

    // perform other database configurations
    openDB();
    recordDatabaseBinding = new RecordDataBaseBinding();
    integerBinding = TupleBinding.getPrimitiveBinding(Integer.class);
    tempkeyBinding = new BatchNumberAndIntegerIDBinding();
    conceptToRecordIndexEntryBinding = new ConceptToRecordIndexEntryBinding(ComparatorFactory.getAscendingIntegerComparator());
    // this.ontology = new SimpleOntologyImplementation();
    recordDatabaseBinding.ontology = ontology;
    conceptIndex = new ConceptToRecordIndex(environment, ComparatorFactory.getAscendingIntegerComparator());
    conceptFrequencyCache = new ConceptFrequencyCache(conceptIndex);
    conceptSumOfValuesCache = new ConceptSumOfValuesCache(conceptIndex);

    gsh = new GroundhogShutdown();
    gsh.g = this;
    Runtime.getRuntime().addShutdownHook(gsh);

  }

  public void setOntology(Ontology ontology) {
    this.ontology = ontology;
    recordDatabaseBinding.ontology = ontology;
  }

  public Set<Integer> checkForEntries(Collection<Integer> ids) {
    Set<Integer> result = new HashSet<Integer>(new Long(Math.round(1.34*(double)ids.size())).intValue());
    
    for (Integer key: ids) {
      ConceptVectorRecord v = get(key);
      
      if (v != null)
        result.add(key);
    }
    return result;
  }

  public boolean hasEntry(Integer cui) {
    return (get(cui) != null);
  }

  public Ontology getOntology() {
    return ontology;
  }

  public GroundhogStatistics getGroundhogStatistics() {
    GroundhogStatistics groundhogStatistics = conceptIndex.getGroundhogStatistics();
    groundhogStatistics.totalNumberOfDocuments = size();
    return groundhogStatistics;
  }

  /**
   * Method for retrieving groundhog statistics while still in bulk import mode
   * (useful for very large grounhogs where you cannot turn bulkimport mode off
   * 
   * @param cleanupSize
   *            after this amount of records, concepts that have only occurred
   *            once are removed from the statistics. Set to null if you don't
   *            want this to happen.
   * @returnReturns the whole grounhog statistics
   */

  public GroundhogStatistics getGroundhogStatisticsInBulkImportMode(Integer cleanupSize) {
    GroundhogStatistics statistics = new GroundhogStatistics();
    Cursor myCursor;

    try {
      myCursor = groundhog.openCursor(null, null);
      DatabaseEntry foundKey = new DatabaseEntry();
      DatabaseEntry foundData = new DatabaseEntry();
      while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
        statistics.totalNumberOfDocuments++;
        ConceptVector conceptvector = (ConceptVector) recordDatabaseBinding.entryToObject(foundData);
        VectorCursor<Integer> cursor = conceptvector.getNonzeroCursor();
        while (cursor.isValid()) {
          Integer cid = cursor.dimension();
          Double value = cursor.get();
          statistics.allConceptOccurrences += value.intValue();
          ConceptStatistic statistic = statistics.conceptStatistics.get(cid);
          if (statistic == null) {
            statistic = new ConceptStatistic();
            statistics.conceptStatistics.put(cid, statistic);
          }
          statistic.docFrequency++;
          statistic.termFrequency += value.intValue();
          cursor.next();
        }
        if (statistics.totalNumberOfDocuments % 10000 == 0) {
          System.out.println(StringUtilities.now() + "\tNumber of documents: " + statistics.totalNumberOfDocuments + ", number of concepts: " + statistics.conceptStatistics.size());
        }
        if (cleanupSize != null && statistics.totalNumberOfDocuments % cleanupSize == 0) {
          int preCleanSize = statistics.conceptStatistics.size();
          Iterator<Map.Entry<Integer, ConceptStatistic>> entryIterator = statistics.conceptStatistics.entrySet().iterator();
          while (entryIterator.hasNext()) {
            if (entryIterator.next().getValue().docFrequency == 1)
              entryIterator.remove();
          }
          System.out.println("Cleaned up statistics. Number of concepts before cleaning = " + preCleanSize + ", after cleaning: " + statistics.conceptStatistics.size());
        }
      }
      myCursor.close();
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

    return statistics;
  }

  public String getName() {
    return databaseName;
  }

  public void clearGroundhog() {
    try {
      closeDatabase();
      Transaction transaction = environment.beginTransaction(null, null);
      environment.truncateDatabase(transaction, databaseName, false);
      transaction.commit();
      openDB();
      conceptIndex.clearIndex();

    } catch (DatabaseException e) {
      e.printStackTrace();
    }

  }

  public void saveConceptVectorRecord(ConceptVectorRecord record) {
    try {
      // Transaction transaction = environment.beginTransaction(null, null);
      DatabaseEntry theKey = new DatabaseEntry();
      integerBinding.objectToEntry(record.getID(), theKey);
      DatabaseEntry theValue = new DatabaseEntry();
      recordDatabaseBinding.objectToEntry(record.getConceptVector(), theValue);
      // transaction.commit();
      groundhog.put(null, theKey, theValue);

      if (!bulkImportMode) {
        conceptIndex.addFingerprintRecordToIndex(record);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void removeConceptVectorRecord(Integer id) {
    ConceptVectorRecord record = loadConceptVectorRecord(id);
    if (record != null) {
      DatabaseEntry theKey = new DatabaseEntry();
      integerBinding.objectToEntry(id, theKey);
      try {
        groundhog.removeSequence(null, theKey);
        conceptIndex.removeConceptVectorRecordFromIndex(record);
      } catch (DatabaseException e) {
        e.printStackTrace();
      }
    }
  }

  public void setBulkImportMode(Boolean toggle) {
    if (bulkImportMode && !toggle) {
      bulkImportMode = toggle;
      conceptIndex.useBulkImportMode = toggle;
      rebuildConceptIndex();
    }
    else if (!bulkImportMode && toggle) {
      conceptIndex.useBulkImportMode = toggle;
      bulkImportMode = toggle;
    }
  }

  public boolean getBulkImportMode() {
    return bulkImportMode;
  }

  public Integer getConceptDocumentFrequency(Integer conceptID) {
    Integer conceptFrequency = conceptFrequencyCache.get(conceptID);
    return conceptFrequency;
  }

  public Float getConceptSumOfValues(Integer conceptID) {
    // Calculates the sum of the values of this concept in all the documents
    return conceptSumOfValuesCache.get(conceptID);
  }

  public SortedIntListSet getRecordIDsForConcept(Integer conceptID) {
    ConceptToConceptVectorRecordIndexEntry conceptToRecordIndexEntry = conceptIndex.get(conceptID);
    if (conceptToRecordIndexEntry == null) {
      return new SortedIntListSet();
    }
    else {
      return conceptToRecordIndexEntry.getConceptVectorRecordIDs();
    }
  }

  public Integer getNumberOfConcepts() {
    return conceptIndex.size();
  }

  public void clearConceptIndex() {
    conceptIndex.clearIndex();
  }

  public void rebuildConceptIndex() {
    try {
      conceptIndex.clearIndex();
      conceptFrequencyCache.clear();
      conceptSumOfValuesCache.clear();
      conceptIndex.indexedConcepts = new HashSet<Integer>();
      conceptIndex.indexedConceptsWithDuplicates = new HashSet<Integer>();
      groundhog.close();
      openDB();
      Runtime.getRuntime().removeShutdownHook(gsh);
      Cursor myCursor;

      int size = size();
      if (size > reindexBatchSize) {
        batchwiseIndexing(size);

      }
      else {

        myCursor = groundhog.openCursor(null, null);
        DatabaseEntry foundKey = new DatabaseEntry();
        DatabaseEntry foundData = new DatabaseEntry();
        Map<Integer, ConceptToConceptVectorRecordIndexEntry> processedRecords = new HashMap<Integer, ConceptToConceptVectorRecordIndexEntry>();
        while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
          Integer key = (Integer) integerBinding.entryToObject(foundKey);
          ConceptVector conceptVector = (ConceptVector)recordDatabaseBinding.entryToObject(foundData);
          VectorCursor<Integer> cursor = conceptVector.getNonzeroCursor();
          while (cursor.isValid()) {
            ConceptToConceptVectorRecordIndexEntry entry = processedRecords.get(cursor.dimension());
            if (entry == null) {
              entry = new ConceptToConceptVectorRecordIndexEntry(key, cursor.get());
            }
            else {
              entry.appendConsecutiveRecordToList(key, cursor.get());
            }
            processedRecords.put(cursor.dimension(), entry);
            cursor.next();
          }


          /*
           * if (records.size() == reindexBatchSize) {
           * 
           * Map<Integer, ConceptToConceptVectorRecordIndexEntry>
           * processedRecords = process(records);
           * conceptIndex.addProcessedRecordsMapToStore(processedRecords);
           * records = new ArrayList<ConceptVectorRecord>();
           * 
           * tally++; double fraction = (double) (100 * reindexBatchSize *
           * tally) / (double) size; System.out.println(fraction + "%"); }
           */

        }
        myCursor.close();
        conceptIndex.addProcessedRecordsMapToStore(processedRecords);

        /*
         * if (tally > 0) { conceptIndex.mergeDuplicateEntries(); }
         */
        conceptIndex.indexedConcepts = null;
        conceptIndex.indexedConceptsWithDuplicates = null;
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
    Runtime.getRuntime().addShutdownHook(gsh);
  }

  private void batchwiseIndexing(Integer size) {
    try {
      Database temp = environment.openDatabase(null, "temp", databaseConfig);
      Map<Integer, IntList> batchHistory = new HashMap<Integer, IntList>();
      Cursor myCursor = groundhog.openCursor(null, null);
      DatabaseEntry foundKey = new DatabaseEntry();
      DatabaseEntry foundData = new DatabaseEntry();
      List<ConceptVectorRecord> records = new ArrayList<ConceptVectorRecord>();
      Integer batchNumber = 0;
      while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {

        Integer key = (Integer) integerBinding.entryToObject(foundKey);
        // System.out.println(key);
        ConceptVectorRecord record = new ConceptVectorRecord(key);
        record.setConceptVector((ConceptVector) recordDatabaseBinding.entryToObject(foundData));
        records.add(record);

        if (records.size() == reindexBatchSize) {
          Map<Integer, ConceptToConceptVectorRecordIndexEntry> processedRecords = process(records);
          processRecordsToTempStore(processedRecords, temp, batchNumber, batchHistory);
          records = new ArrayList<ConceptVectorRecord>();
          batchNumber++;
          double fraction = 100d * (double) (reindexBatchSize * batchNumber) / (double) size;
          System.out.println(fraction + "%");
        }
      }
      Map<Integer, ConceptToConceptVectorRecordIndexEntry> processedRecords = process(records);
      processRecordsToTempStore(processedRecords, temp, batchNumber, batchHistory);
      double fraction = 100d * (double) ((reindexBatchSize * batchNumber + records.size())) / (double) size;
      System.out.println(fraction + "%");

      myCursor.close();
      mergeBatchIndex(batchHistory, temp);
      temp.close();
      environment.removeDatabase(null, "temp");
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  private void processRecordsToTempStore(Map<Integer, ConceptToConceptVectorRecordIndexEntry> processedRecordsMap, Database temp, Integer batch, Map<Integer, IntList> batchHistory) {
    try {
      for (Integer key: processedRecordsMap.keySet()) {
        ConceptToConceptVectorRecordIndexEntry addition = processedRecordsMap.get(key);
        BatchwiseIntegerID batchwiseConceptID = new BatchwiseIntegerID(key, batch);
        DatabaseEntry databaseKey = new DatabaseEntry();
        tempkeyBinding.objectToEntry(batchwiseConceptID, databaseKey);
        DatabaseEntry databaseValue = new DatabaseEntry();
        conceptToRecordIndexEntryBinding.objectToEntry(addition, databaseValue);
        temp.put(null, databaseKey, databaseValue);
        IntList batchArray = batchHistory.get(key);
        if (batchArray == null) {
          batchArray = new IntList();
          batchHistory.put(key, batchArray);
        }
        batchArray.add(batch);
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

  }

  private void mergeBatchIndex(Map<Integer, IntList> batchHistory, Database temp) {
    try {
      System.out.println("Merging batch index: ");
      int bs = batchHistory.size();
      double i = 0;
      for (Integer cui: batchHistory.keySet()) {
        i++;
        List<Integer> batches = batchHistory.get(cui);
        ConceptToConceptVectorRecordIndexEntry entry = new ConceptToConceptVectorRecordIndexEntry();

        int entries = 0;
        List<SortedIntListSet> idsets = new ArrayList<SortedIntListSet>();
        for (Integer batch: batches) {
          BatchwiseIntegerID batchwiseConceptID = new BatchwiseIntegerID(cui, batch);
          DatabaseEntry databaseKey = new DatabaseEntry();
          tempkeyBinding.objectToEntry(batchwiseConceptID, databaseKey);
          DatabaseEntry databaseValue = new DatabaseEntry();
          temp.get(null, databaseKey, databaseValue, LockMode.DEFAULT);
          ConceptToConceptVectorRecordIndexEntry addition = (ConceptToConceptVectorRecordIndexEntry) conceptToRecordIndexEntryBinding.entryToObject(databaseValue);
          idsets.add(addition.conceptVectorRecordIDs);
          entries += addition.conceptVectorRecordIDs.size();
          entry.sumOfValuesInRecords += addition.sumOfValuesInRecords;
        }
        IntList intList = new IntList(entries);
        for (SortedIntListSet set: idsets) {
          Iterator<Integer> it = set.getSortedList().iterator();
          while (it.hasNext())
            intList.add(it.next());
        }
        entry.conceptVectorRecordIDs.setSortedList(intList);
        conceptIndex.set(cui, entry);
        if (i % 10000 == 0) {
          System.out.println("Entry: " + cui + "\t" + 100d * i / bs + "%");
        }
      }
    } catch (DatabaseException e) {

      e.printStackTrace();
    }

  }

  public ConceptVectorRecord loadConceptVectorRecord(Integer ID) {
    return get(ID);
  }

  public Iterator<ConceptVectorRecord> getIterator() {
    return new GroundhogIterator();
  }

  public GroundhogCursor getCursor() {
    return new GroundhogCursor();

  }

  public Iterator<ConceptToConceptVectorRecordIndexEntry> getConceptToRecordIndexIterator() {
    return conceptIndex.getIterator();

  }

  @Override
  public int size() {
    // THIS IS A SLOW OPERATION; it may be bright to simply keep a tally and
    // write it in the database

    int size = 0;
    try {

      DatabaseStats stats = groundhog.getStats(null);

      Pattern p = Pattern.compile("numLeafNodes=([0-9]+)");
      Matcher m = p.matcher(stats.toString());
      if (m.find()) {
        size = Integer.parseInt(m.group(1));
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
    return size;
  }

  private void closeDatabase() {
  	if (groundhog != null) {
  		try {
  			conceptIndex.conceptToConceptVectorIndexStore.close();  
  		} catch (DatabaseException e) {
  			// do nothing
  		}
  		
  		try {
  			groundhog.close();
  		} catch (DatabaseException e) {
  			// do nothing
  		}

  	}

  	if (environment != null) {
  		try {
  			environment.cleanLog(); // Clean the log before closing
  			environment.close();
  		} catch (DatabaseException e) {
  			// do nothing
  		}

  	}

  }
  
  public void close(){
    closeDatabase();
    Runtime.getRuntime().removeShutdownHook(gsh);   
  }

  protected void finalize() {
    close();
  }

  private void openDB() {
    try {
      groundhog = environment.openDatabase(null, databaseName, databaseConfig);
    } catch (DatabaseException e) {

      e.printStackTrace();
    }
  }

  protected Map<Integer, ConceptToConceptVectorRecordIndexEntry> process(List<ConceptVectorRecord> records) {
    Map<Integer, ConceptToConceptVectorRecordIndexEntry> index = new HashMap<Integer, ConceptToConceptVectorRecordIndexEntry>();
    for (ConceptVectorRecord record: records) {
      VectorCursor<Integer> cursor = record.getConceptVector().getNonzeroCursor();
      while (cursor.isValid()) {
        ConceptToConceptVectorRecordIndexEntry entry = index.get(cursor.dimension());
        if (entry == null) {
          entry = new ConceptToConceptVectorRecordIndexEntry(record.getID(), cursor.get());
        }
        else {
          entry.appendConsecutiveRecordToList(record.getID(), cursor.get());
        }
        index.put(cursor.dimension(), entry);
        cursor.next();
      }
    }
    return index;

  }

  @Override
  public ConceptVectorRecord getEntryFromStoreWithID(Integer id) {
    ConceptVectorRecord record = new ConceptVectorRecord(id);

    DatabaseEntry theKey = new DatabaseEntry();
    integerBinding.objectToEntry(record.getID(), theKey);
    DatabaseEntry theValue = new DatabaseEntry();
    try {
      groundhog.get(null, theKey, theValue, LockMode.DEFAULT);
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
    if (theValue.getSize() != 0) {
      record.setConceptVector((ConceptVector) recordDatabaseBinding.entryToObject(theValue));
      return record;
    }
    else {
      return null;
    }
  }

  @Override
  public Map<Integer, ConceptVectorRecord> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
    Map<Integer, ConceptVectorRecord> result = new HashMap<Integer, ConceptVectorRecord>(new Long(Math.round(1.34 * (double) ids.size())).intValue());
    Iterator<Integer> iterator = ids.iterator();
    while (iterator.hasNext()) {
      ConceptVectorRecord record = getEntryFromStoreWithID(iterator.next());
      if (record != null) {
        result.put(record.getID(), record);
      }
    }
    return result;
  }

  @Override
  protected void setEntryInStore(Integer id, ConceptVectorRecord value) {
    try {
      Transaction transaction = environment.beginTransaction(null, null);

      DatabaseEntry theDBKey = new DatabaseEntry();
      integerBinding.objectToEntry(id, theDBKey);
      DatabaseEntry theDBValue = new DatabaseEntry();
      recordDatabaseBinding.objectToEntry(value.getConceptVector(), theDBValue);
      groundhog.put(transaction, theDBKey, theDBValue);
      transaction.commit();
    } catch (DatabaseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public class GroundhogCursor implements org.erasmusmc.collections.Cursor<ConceptVectorRecord> {
    Cursor myCursor;
    DatabaseEntry foundKey = new DatabaseEntry();
    DatabaseEntry foundData = new DatabaseEntry();
    ConceptVectorRecord conceptVectorRecord = null;
    Transaction transaction;

    public GroundhogCursor() {
      // When using this cursor changes will only be permanent when you either
      // reach the end of the groundhog and the cursor closes or by calling
      // close();

      try {
        transaction = environment.beginTransaction(null, null);
        myCursor = groundhog.openCursor(transaction, null);

        next();
      } catch (DatabaseException e) {
        e.printStackTrace();
      }

    }

    public void close() {
      try {
        myCursor.close();
        conceptVectorRecord = null;
        transaction.commit();
      } catch (DatabaseException e) {
        e.printStackTrace();
      }
    }

    public boolean isValid() {
      if (conceptVectorRecord != null) {
        return true;
      }
      else {
        return false;
      }
    }

    public void next() {
      try {
        if (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
          Integer key = (Integer) integerBinding.entryToObject(foundKey);
          conceptVectorRecord = new ConceptVectorRecord(key);
          conceptVectorRecord.setConceptVector((ConceptVector) recordDatabaseBinding.entryToObject(foundData));

        }
        else {

          close();
          conceptVectorRecord = null;
        }
      } catch (DatabaseException e) {

        e.printStackTrace();
      }
    }

    /**
     * Moves the cursor to the given key
     */
    public void getSearchKey(Integer key) {
      try {
        DatabaseEntry dbKey = new DatabaseEntry();
        integerBinding.objectToEntry(key, dbKey);
        if (myCursor.getSearchKey(dbKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
          conceptVectorRecord = new ConceptVectorRecord(key);
          conceptVectorRecord.setConceptVector((ConceptVector) recordDatabaseBinding.entryToObject(foundData));
        }
        else {
          conceptVectorRecord = null;
        }
      } catch (DatabaseException e) {

        e.printStackTrace();
      }

    }

    public void getLast() {

      try {
        if (myCursor.getLast(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
          Integer key = (Integer) integerBinding.entryToObject(foundKey);
          conceptVectorRecord = new ConceptVectorRecord(key);
          conceptVectorRecord.setConceptVector((ConceptVector) recordDatabaseBinding.entryToObject(foundData));

        }
        else {
          conceptVectorRecord = null;
        }
      } catch (DatabaseException e) {

        e.printStackTrace();
      }

    }

    public void delete() {
      try {

        if (!(myCursor.delete() == OperationStatus.SUCCESS)) {
          System.out.println("unsuccesful cursor delete on Groundhog!");
        }

      } catch (DatabaseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

    public ConceptVectorRecord get() {

      return conceptVectorRecord;
    }

    public void put(ConceptVectorRecord d) {
      integerBinding.objectToEntry(d.getID(), foundKey);
      recordDatabaseBinding.objectToEntry(d.getConceptVector(), foundData);
      try {
        if (myCursor.put(foundKey, foundData) != OperationStatus.SUCCESS) {
          System.out.println("unsuccesful cursor put on Groundhog!");
        }
      } catch (DatabaseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

  }

  protected class GroundhogIterator implements Iterator<ConceptVectorRecord> {
    Cursor myCursor;
    DatabaseEntry foundKey = new DatabaseEntry();
    DatabaseEntry foundData = new DatabaseEntry();
    ConceptVectorRecord next = null;
    Boolean toggle = false;

    public GroundhogIterator() {
      try {
        myCursor = groundhog.openCursor(null, null);
      } catch (DatabaseException e) {
        e.printStackTrace();
      }
    }

    public boolean hasNext() {
      Boolean result = false;
      if (myCursor != null) {
        try {
          if (!toggle) {

            if (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
              Integer key = (Integer) integerBinding.entryToObject(foundKey);
              next = new ConceptVectorRecord(key);
              next.setConceptVector((ConceptVector) recordDatabaseBinding.entryToObject(foundData));
              toggle = true;
              result = true;
            }
            else {
              toggle = true;
              next = null;
              myCursor.close();
            }

          }
          else {
            result = true;
          }
        } catch (DatabaseException e) {
          e.printStackTrace();

        }
      }
      return result;
    }

    public ConceptVectorRecord next() {
      ConceptVectorRecord result = null;
      if (myCursor != null) {
        if (toggle) {
          result = next;
        }
        else {
          try {
            if (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
              Integer key = (Integer) integerBinding.entryToObject(foundKey);
              result = new ConceptVectorRecord(key);
              result.setConceptVector((ConceptVector) recordDatabaseBinding.entryToObject(foundData));

            }

          } catch (DatabaseException e) {
            e.printStackTrace();
          }
        }
      }
      toggle = false;
      return result;
    }

    public void remove() {

      // not implemented
      System.out.println("Remove is not implemented for Groundhog iterator!");
    }
  }

  public int getReindexBatchSize() {
    return reindexBatchSize;
  }

  public void setReindexBatchSize(int reindexBatchSize) {
    this.reindexBatchSize = reindexBatchSize;
  }

  protected class GroundhogShutdown extends Thread {
    public Groundhog g;

    public void run() {
      g.closeDatabase();
      System.out.println("Groundhog shutdown hook called!");
    }
  }
}
