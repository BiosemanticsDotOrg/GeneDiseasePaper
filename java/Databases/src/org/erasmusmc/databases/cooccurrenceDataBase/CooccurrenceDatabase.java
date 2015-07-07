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

package org.erasmusmc.databases.cooccurrenceDataBase;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.erasmusmc.collections.ComparatorFactory;
import org.erasmusmc.collections.IntList;
import org.erasmusmc.collections.SortedIntList2FloatMap;
import org.erasmusmc.collections.SortedIntList2IntMap;
import org.erasmusmc.collections.SortedListMap;
import org.erasmusmc.collections.SortedPair;
import org.erasmusmc.databases.BatchNumberAndIntegerIDBinding;
import org.erasmusmc.databases.BatchwiseIntegerID;
import org.erasmusmc.groundhog.ConceptToConceptVectorRecordIndexEntry;
import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.ConceptVectorRecord;
import org.erasmusmc.storecaching.StoreMapCaching;
import org.erasmusmc.utilities.StringUtilities;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.DatabaseStats;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;

public class CooccurrenceDatabase extends StoreMapCaching<Integer, SortedIntList2IntMap> {
  /** Changing this name will render older databases unreadable */
  protected String databaseName = "CooccurrenceDatabase";
  /** higher values lead to quicker reindexation but more memory usage */
  protected int reindexBatchSize = 500000;

  protected Environment environment;
  protected DatabaseConfig databaseConfig;
  protected Database cooccurrenceDB;
  protected EntryBinding myIntegerBinding;
  protected EntryBinding myDataBinding;
  protected EnvironmentConfig environmentConfig;
  protected TupleBinding tempkeyBinding = null;

  protected CooccurrenceDatabaseShutdown sh;
  public CooccurrenceDatabase(String foldername){
    File datadir = new File(foldername);
    if (!datadir.exists())
      datadir.mkdir();
    init(datadir);
  }
  public CooccurrenceDatabase(File datadir) {
    init(datadir);
  }  
  private void init(File datadir){
    try {
      environmentConfig = new EnvironmentConfig();
      environmentConfig.setAllowCreate(true);
      environmentConfig.setTransactional(true);
      environmentConfig.setCacheSize(30240000);

      environment = new Environment(datadir, environmentConfig);

      databaseConfig = new DatabaseConfig();
      databaseConfig.setAllowCreate(true);
      databaseConfig.setTransactional(true);

      openDB();

      myIntegerBinding = TupleBinding.getPrimitiveBinding(Integer.class);
      myDataBinding = new Integer2Integer2IntegerMapBinding();
      
      sh = new CooccurrenceDatabaseShutdown();
      sh.c = this;
      Runtime.getRuntime().addShutdownHook(sh);
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

  }

  private void parseConceptVector(ConceptVector conceptVector, SortedListMap<Integer, SortedIntList2IntMap> coocMap) {
    SortedIntList2FloatMap cvrmap = conceptVector.values;
    for (int index = 0; index < cvrmap.size(); index++) {
      int id1 = cvrmap.getKey(index);
      for (int index2 = index; index2 < cvrmap.size(); index2++) {
        int id2 = cvrmap.getKey(index2);
        int small = id1;
        int big = id2;
        if (id1 > id2) {
          small = id2;
          big = id1;
        }
        SortedIntList2IntMap map = coocMap.get(small);
        if (map == null) {
          map = new SortedIntList2IntMap();
          coocMap.put(small, map);
        }
        Integer base = map.get(big);
        if (base == null)
          base = 0;
        base++;
        map.put(big, base);
      }
    }
  }

  public void makeFromGroundhog(Groundhog groundhog) {
    int size = groundhog.size();
    if (size > reindexBatchSize) {
      batchwiseIndexing(size, groundhog);
    }
    else {
      SortedListMap<Integer, SortedIntList2IntMap> coocMap = new SortedListMap<Integer, SortedIntList2IntMap>(ComparatorFactory.getAscendingIntegerComparator());
      System.out.println("Start iteration: " + StringUtilities.now());
      Iterator<ConceptVectorRecord> iterator = groundhog.getIterator();
      int counter = 0;
      while (iterator.hasNext()) {
        ConceptVectorRecord cvr = iterator.next();
        if ((counter % 100000) == 0) {
          System.out.println(100 * (double) counter / size + "%\t" + StringUtilities.now());
        }
        parseConceptVector(cvr.getConceptVector(), coocMap);
        counter++;
      }
      Iterator<SortedListMap<Integer, SortedIntList2IntMap>.MapEntry<Integer,SortedIntList2IntMap>> entryIt = coocMap.entryIterator();
      while (entryIt.hasNext()) {
        SortedListMap<Integer, SortedIntList2IntMap>.MapEntry<Integer,SortedIntList2IntMap> entry = entryIt.next();
        setEntryInStore(entry.getKey(), entry.getValue());
      }
      System.out.println("done" + StringUtilities.now());
    }
  }

  private void batchwiseIndexing(Integer size, Groundhog groundhog) {
    try {
      Database temp = environment.openDatabase(null, "temp", databaseConfig);
      tempkeyBinding = new BatchNumberAndIntegerIDBinding();
      Map<Integer, IntList> batchHistory = new HashMap<Integer, IntList>();
      SortedListMap<Integer, SortedIntList2IntMap> coocMap = new SortedListMap<Integer, SortedIntList2IntMap>(ComparatorFactory.getAscendingIntegerComparator());
      Integer batchNumber = 0;
      Integer counter = 0;

      Iterator<ConceptVectorRecord> iterator = groundhog.getIterator();
      while (iterator.hasNext()) {
        ConceptVectorRecord cvr = iterator.next();
        parseConceptVector(cvr.getConceptVector(), coocMap);
        counter++;
        if (counter % reindexBatchSize == 0) {
          processToTempStore(coocMap, temp, batchNumber, batchHistory);
          coocMap = new SortedListMap<Integer, SortedIntList2IntMap>(ComparatorFactory.getAscendingIntegerComparator());
          batchNumber++;
          double fraction = 100d * (double) (counter) / (double) size;
          System.out.println(fraction + "%");
        }
      }
      processToTempStore(coocMap, temp, batchNumber, batchHistory);
      double fraction = 100d * (double) (counter) / (double) size;
      System.out.println(fraction + "%");
      mergeBatchIndex(batchHistory, temp);
      temp.close();
      environment.removeDatabase(null, "temp");
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  private void processToTempStore(SortedListMap<Integer, SortedIntList2IntMap> coocMap, Database temp, Integer batch, Map<Integer, IntList> batchHistory) {
    try {
      Iterator<SortedListMap<Integer, SortedIntList2IntMap>.MapEntry<Integer,SortedIntList2IntMap>> entryIt = coocMap.entryIterator();
      while (entryIt.hasNext()) {
        SortedListMap<Integer, SortedIntList2IntMap>.MapEntry<Integer,SortedIntList2IntMap> entry = entryIt.next();
        Integer key = entry.getKey();
        BatchwiseIntegerID batchwiseConceptID = new BatchwiseIntegerID(key, batch);
        DatabaseEntry databaseKey = new DatabaseEntry();
        tempkeyBinding.objectToEntry(batchwiseConceptID, databaseKey);
        DatabaseEntry databaseValue = new DatabaseEntry();
        myDataBinding.objectToEntry(entry.getValue(), databaseValue);
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
        SortedIntList2IntMap entry = new SortedIntList2IntMap();
        for (Integer batch: batches) {
          BatchwiseIntegerID batchwiseConceptID = new BatchwiseIntegerID(cui, batch);
          DatabaseEntry databaseKey = new DatabaseEntry();
          tempkeyBinding.objectToEntry(batchwiseConceptID, databaseKey);
          DatabaseEntry databaseValue = new DatabaseEntry();
          temp.get(null, databaseKey, databaseValue, LockMode.DEFAULT);
          SortedIntList2IntMap addition = (SortedIntList2IntMap) myDataBinding.entryToObject(databaseValue);
          Iterator<SortedIntList2IntMap.MapEntry> it = addition.entryIterator();
          while (it.hasNext()) {
            SortedIntList2IntMap.MapEntry batchEntry = it.next();
            Integer val = entry.get(batchEntry.getKey());
            if (val == null)
              val = batchEntry.getValue();
            else
              val += batchEntry.getValue();
            entry.put(batchEntry.getKey(), val);
          }
        }
        setEntryInStore(cui, entry);
        if (i % 10000 == 0) {
          System.out.println("Entry: " + cui + "\t" + 100d * i / bs + "%");
        }
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

  }

  public void checkDB(Groundhog groundhog) {
    Iterator<ConceptToConceptVectorRecordIndexEntry> iterator = groundhog.getConceptToRecordIndexIterator();
    System.out.println("Checking coocdb " + StringUtilities.now());
    while (iterator.hasNext()) {
      ConceptToConceptVectorRecordIndexEntry entry = iterator.next();
      Integer keyfreq = entry.conceptVectorRecordIDs.size();
      Integer keyfreq2 = getCooccurrenceCount(entry.key, entry.key);
      if (keyfreq != keyfreq2) {
        System.out.println("Error for entry " + entry.key + "; in groundhog: " + keyfreq + ", in coocdb: " + keyfreq2);
      }
    }
    System.out.println("Done checking coocdb");
  }

  public void openDB() {
    try {
      this.cooccurrenceDB = environment.openDatabase(null, this.databaseName, this.databaseConfig);
      // environment.removeDatabase(null,this.dbName);
    } catch (DatabaseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public int getCooccurrenceCount(Integer cui1, Integer cui2) {
    if(cui1 == null || cui2 == null) return 0;
    Integer smaller = cui1;
    Integer bigger = cui2;
    if (smaller > bigger) {
      bigger = smaller;
      smaller = cui2;
    }
    SortedIntList2IntMap map = get(smaller);
    if (map != null) {
      Integer id = map.get(bigger);
      if (id != null) {
        return id;
      }
    }
    return 0;
  }

  public int getCooccurrenceCount(SortedPair<Integer> integerPair) {
    return getCooccurrenceCount(integerPair.getObject1(), integerPair.getObject2());
  }

  public List<Integer> getBatch(List<SortedPair<Integer>> ids) {
    List<Integer> result = new ArrayList<Integer>();
    for (SortedPair<Integer> integerPair: ids) {
      result.add(getCooccurrenceCount(integerPair));
    }
    return result;
  }

  @Override
  protected Map<Integer, SortedIntList2IntMap> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected SortedIntList2IntMap getEntryFromStoreWithID(Integer id) {
    SortedIntList2IntMap result = null;
    try {
      DatabaseEntry databaseKey = new DatabaseEntry();
      DatabaseEntry databaseValue = new DatabaseEntry();
      myIntegerBinding.objectToEntry(id, databaseKey);
      cooccurrenceDB.get(null, databaseKey, databaseValue, LockMode.DEFAULT);
      if (databaseValue.getSize() != 0) {
        result = (SortedIntList2IntMap) myDataBinding.entryToObject(databaseValue);
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

    return result;
  }

  @Override
  protected void setEntryInStore(Integer id, SortedIntList2IntMap value) {
    try {
      DatabaseEntry databaseKey = new DatabaseEntry();
      myIntegerBinding.objectToEntry(id, databaseKey);
      DatabaseEntry databaseValue = new DatabaseEntry();
      myDataBinding.objectToEntry(value, databaseValue);
      cooccurrenceDB.put(null, databaseKey, databaseValue);

    } catch (DatabaseException e) {
      e.printStackTrace();
    }

  }

  @Override
  public int size() {
    int size = 0;
    try {
      DatabaseStats stats = cooccurrenceDB.getStats(null);
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
  private void closeDatabase() {
    try {
      if (cooccurrenceDB != null) {
        cooccurrenceDB.close();
      }

      if (environment != null) {
        environment.cleanLog(); // Clean the log before closing
        environment.close();
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  protected void finalize() {
    closeDatabase();
    Runtime.getRuntime().removeShutdownHook(sh);
  }
  
  protected class CooccurrenceDatabaseShutdown extends Thread {
    public CooccurrenceDatabase c;

    public void run() {
      c.closeDatabase();
    }
  }
}
