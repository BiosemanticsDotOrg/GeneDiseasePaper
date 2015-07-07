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

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.erasmusmc.math.vector.VectorCursor;
import org.erasmusmc.ontology.ConceptVectorRecord;
import org.erasmusmc.storecaching.StoreMapCaching;
import org.erasmusmc.utilities.StringUtilities;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseStats;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class ConceptToRecordIndex extends StoreMapCaching<Integer, ConceptToConceptVectorRecordIndexEntry> implements Serializable{
  private static final long serialVersionUID = 6755259561426980631L;
  protected Environment environment;
  protected Database conceptToConceptVectorIndexStore;
  protected DatabaseConfig databaseConfig;
  protected EntryBinding myIntegerBinding;
  protected EntryBinding myDataBinding;
  protected String dbName = "CUI_2_FPUI_INDEX";
  protected Set<Integer> indexedConcepts;
  protected Set<Integer> indexedConceptsWithDuplicates;
  protected Boolean useBulkImportMode = false;
  protected Comparator<Integer> comparator;

  public ConceptToRecordIndex(Environment environment,Comparator<Integer> comparator) throws DatabaseException {
    this.comparator=comparator;
    databaseConfig = new DatabaseConfig();
    databaseConfig.setAllowCreate(true);
    databaseConfig.setTransactional(false);
    //databaseConfig.setSortedDuplicates(true);
    this.environment = environment;
    openDB();
    
    myIntegerBinding = TupleBinding.getPrimitiveBinding(Integer.class);
    myDataBinding = new ConceptToRecordIndexEntryBinding(comparator);
    
  }

  public void openDB() {
    try {
     this.conceptToConceptVectorIndexStore = environment.openDatabase(null, this.dbName, this.databaseConfig);
   //    environment.removeDatabase(null,this.dbName);
    } catch (DatabaseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public GroundhogStatistics getGroundhogStatistics() {
    GroundhogStatistics wholeGroundhogStatistics = new GroundhogStatistics();

    try {
      Cursor myCursor = conceptToConceptVectorIndexStore.openCursor(null, null);
      DatabaseEntry foundKey = new DatabaseEntry();
      DatabaseEntry foundData = new DatabaseEntry();
      while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
        ConceptToConceptVectorRecordIndexEntry entry = (ConceptToConceptVectorRecordIndexEntry) myDataBinding.entryToObject(foundData);
        Integer key = (Integer) myIntegerBinding.entryToObject(foundKey);
        ConceptStatistic conceptStatistic = new ConceptStatistic();
        conceptStatistic.docFrequency = entry.conceptVectorRecordIDs.size();
        conceptStatistic.termFrequency = Math.round(entry.sumOfValuesInRecords);
        // RvS: fix compile error by rounding off float to int

        wholeGroundhogStatistics.conceptStatistics.put(key, conceptStatistic);
        wholeGroundhogStatistics.allConceptOccurrences += entry.sumOfValuesInRecords;
      }
      myCursor.close();
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

    return wholeGroundhogStatistics;
  }

  public void clearIndex() {
    // be careful! you have to rebuild your index EXPLICITELY if your
    // recordstore hasn't been cleared as well.
    try {

      conceptToConceptVectorIndexStore.close();

      //Transaction transaction = environment.beginTransaction(null, null);
      environment.truncateDatabase(null, dbName, false);
      //transaction.commit();
      openDB();
      index.clear();
      
    } catch (DatabaseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void addProcessedRecordsMapToStore(Map<Integer, ConceptToConceptVectorRecordIndexEntry> processedRecordsMap) {
    // System.out.println("storing processed records map");
    //assumes ascending ids
    for (Map.Entry<Integer, ConceptToConceptVectorRecordIndexEntry> entry : processedRecordsMap.entrySet()){
      setEntryInStore(entry.getKey(), entry.getValue());
      /*if (indexedConcepts.contains(entry.getKey())) {
        indexedConceptsWithDuplicates.add(entry.getKey());
      }
      else {
        indexedConcepts.add(entry.getKey());
      }
      */
    }
  }

  public void mergeDuplicateEntries() {
    try {
      System.out.println("Starting to merge duplicate entries in the Database " + StringUtilities.now());
      Cursor myCursor = conceptToConceptVectorIndexStore.openCursor(null, null);
      DatabaseEntry searchKey = new DatabaseEntry();
      DatabaseEntry foundData = new DatabaseEntry();
      for (Integer key: indexedConceptsWithDuplicates) {
        myIntegerBinding.objectToEntry(key, searchKey);
        if (myCursor.getSearchKey(searchKey, foundData, null) == OperationStatus.SUCCESS) {
          if (myCursor.count() > 1) {
            ConceptToConceptVectorRecordIndexEntry entry = (ConceptToConceptVectorRecordIndexEntry) myDataBinding.entryToObject(foundData);
            //OperationStatus status = myCursor.delete();
            while (myCursor.getNextDup(searchKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
              ConceptToConceptVectorRecordIndexEntry addition = (ConceptToConceptVectorRecordIndexEntry) myDataBinding.entryToObject(foundData);
              entry.conceptVectorRecordIDs.addAll(addition.conceptVectorRecordIDs);
              entry.sumOfValuesInRecords += addition.sumOfValuesInRecords;
              myCursor.delete();
            }
            setEntryInStore(key, entry);
          }
        }
      }
      System.out.println("Done merging duplicate entries in the Database " + StringUtilities.now());
      myCursor.close();
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  public void addFingerprintRecordToIndex(ConceptVectorRecord record) {
    VectorCursor<Integer> cursor = record.getConceptVector().getNonzeroCursor();
    if (!useBulkImportMode) {
      while (cursor.isValid()) {
        ConceptToConceptVectorRecordIndexEntry entry = get(cursor.dimension());
        if (entry == null) {
          entry = new ConceptToConceptVectorRecordIndexEntry(record.getID(), new Double(cursor.get()).floatValue());
        }
        else {
          if (!entry.conceptVectorRecordIDs.contains(record.getID())) {
            entry.addRecordData(record.getID(), new Double(cursor.get()).floatValue());
          }
        }
        set(cursor.dimension(), entry);
        cursor.next();
      }
    }
    else {
      System.out.println("addRecordToIndex should never be called in bulkimportmode!");
    }
  }

  public boolean removeConceptVectorRecordFromIndex(ConceptVectorRecord record) {

    VectorCursor<Integer> cursor = record.getConceptVector().getNonzeroCursor();
    Boolean result = false;
    if (!useBulkImportMode) {
      while (cursor.isValid()) {
        ConceptToConceptVectorRecordIndexEntry entry = get(cursor.dimension());
        if (entry != null) {
          if (entry.conceptVectorRecordIDs.contains(record.getID())) {
            entry.removeRecordData(record.getID(), new Double(cursor.get()).floatValue());
            set(cursor.dimension(), entry);

          }
        }
        cursor.next();
      }
      result = true;
    }
    return result;
  }

  public Iterator<ConceptToConceptVectorRecordIndexEntry> getIterator() {
    return new ConceptToConceptVectorIndexIterator();

  }

  @Override
  public ConceptToConceptVectorRecordIndexEntry get(Integer key) {
    if (!useBulkImportMode) {
      ConceptToConceptVectorRecordIndexEntry result = getFromCache(key);

      if (result == null)
        return fetch(key);
      else
        return result;
    }
    else {
      return null;
    }
  }

  @Override
  public int size() {
    // this is a slow operation... Store in database?
    if (useBulkImportMode) {
      return -1;
    }
    else {
      int size = 0;

      try {
        DatabaseStats stats = conceptToConceptVectorIndexStore.getStats(null);
        Pattern p = Pattern.compile("numLeafNodes=([0-9]+)");
        Matcher m = p.matcher(stats.toString());
        if (m.find()) {
          size = Integer.parseInt(m.group(1));
        }
      } catch (DatabaseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return size;
    }
  }

  @Override
  protected ConceptToConceptVectorRecordIndexEntry getEntryFromStoreWithID(Integer id) {
    ConceptToConceptVectorRecordIndexEntry conceptToRecordIndexEntry = null;
    try {
      DatabaseEntry databaseKey = new DatabaseEntry();
      DatabaseEntry databaseValue = new DatabaseEntry();
      myIntegerBinding.objectToEntry(id, databaseKey);
      conceptToConceptVectorIndexStore.get(null, databaseKey, databaseValue, LockMode.DEFAULT);
      if (databaseValue.getSize() != 0) {
        conceptToRecordIndexEntry = (ConceptToConceptVectorRecordIndexEntry) myDataBinding.entryToObject(databaseValue);
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

    return conceptToRecordIndexEntry;
  }

  @Override
  protected Map<Integer, ConceptToConceptVectorRecordIndexEntry> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected void setEntryInStore(Integer id, ConceptToConceptVectorRecordIndexEntry value) {
    try {
 //     Transaction transaction = environment.beginTransaction(null, null);
      DatabaseEntry databaseKey = new DatabaseEntry();
      myIntegerBinding.objectToEntry(id, databaseKey);
      DatabaseEntry databaseValue = new DatabaseEntry();
      myDataBinding.objectToEntry(value, databaseValue);
      conceptToConceptVectorIndexStore.put(null, databaseKey, databaseValue);
      //transaction.commit();
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

  }

  protected class ConceptToConceptVectorIndexIterator implements Iterator<ConceptToConceptVectorRecordIndexEntry> {
    Cursor myCursor;
    DatabaseEntry foundKey = new DatabaseEntry();
    DatabaseEntry foundData = new DatabaseEntry();
    ConceptToConceptVectorRecordIndexEntry next = null;
    Boolean nextHasBeenRetrieved = false;

    public ConceptToConceptVectorIndexIterator() {
      try {
        myCursor = conceptToConceptVectorIndexStore.openCursor(null, null);
      } catch (DatabaseException e) {
        e.printStackTrace();
      }
    }

    public boolean hasNext() {
      Boolean result = false;
      if (myCursor != null) {
        try {
          if (!nextHasBeenRetrieved) {

            if (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
              Integer key = (Integer) myIntegerBinding.entryToObject(foundKey);
              next = (ConceptToConceptVectorRecordIndexEntry) myDataBinding.entryToObject(foundData);
              next.key = key;
              nextHasBeenRetrieved = true;
              result = true;
            }
            else {
              nextHasBeenRetrieved = true;
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

    public ConceptToConceptVectorRecordIndexEntry next() {
      ConceptToConceptVectorRecordIndexEntry result = null;
      if (myCursor != null) {
        if (nextHasBeenRetrieved) {
          result = next;
        }
        else {
          try {
            if (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
              Integer key = (Integer) myIntegerBinding.entryToObject(foundKey);
              result = (ConceptToConceptVectorRecordIndexEntry) myDataBinding.entryToObject(foundData);
              result.key = key;

            }

          } catch (DatabaseException e) {
            e.printStackTrace();
          }
        }
      }
      nextHasBeenRetrieved = false;
      return result;
    }

    public void remove() {
      // not implemented
      System.out.println("Remove is not implemented for ConceptToConceptVectorIndexIterator iterator!");

    }

  }
}
