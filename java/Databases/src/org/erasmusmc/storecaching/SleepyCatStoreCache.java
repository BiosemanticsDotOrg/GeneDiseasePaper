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

package org.erasmusmc.storecaching;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

public class SleepyCatStoreCache<K,V>  extends StoreMapCaching<K, V> implements Iterable<Map.Entry<K, V>>{
  private String databaseName = "cache";
  private EnvironmentConfig environmentConfig;
  private Environment environment;
  private DatabaseConfig databaseConfig;
  private Database database;
  private int cachesize = 1000000;
  private Shutdown sh;
  protected TupleBinding keyBinding;
  protected TupleBinding valueBinding;
  private File datadir;
  protected V notInDatabase = null; 

  @Override
  protected Map<K, V> getEntriesFromStoreWithIDs(Collection<K> ids) {
    Map<K, V> result = new HashMap<K, V>();
    for (K id : ids)
      result.put(id, getEntryFromStoreWithID(id));
    return result;
  }
  
  public void clearCache() {
    closeDatabase();
    for (File file : datadir.listFiles())
      file.delete();
    try {
      initialize();
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }
  
  public void flush(){
    try {
      database.sync();
    } catch (DatabaseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  private void closeDatabase() {
    try {
      if (database != null) {
        database.close();
      }

      if (environment != null) {
        environment.cleanLog(); // Clean the log before closing
        environment.close();
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }
  @SuppressWarnings("unchecked")
  @Override
  protected V getEntryFromStoreWithID(K id) {
    DatabaseEntry theKey = new DatabaseEntry();
    keyBinding.objectToEntry(id, theKey);
    DatabaseEntry theValue = new DatabaseEntry();
    try {
      database.get(null, theKey, theValue, LockMode.DEFAULT);
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
    if (theValue.getSize() != 0) 
      return (V) valueBinding.entryToObject(theValue);
    else
      return notInDatabase;
  }
  @Override
  protected void setEntryInStore(K id, V value) {
    try {
      DatabaseEntry theKey = new DatabaseEntry();
      keyBinding.objectToEntry(id, theKey);
      DatabaseEntry theValue = new DatabaseEntry();
      valueBinding.objectToEntry(value, theValue);
      database.put(null, theKey,theValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void setSkippingCache(K id, V value){
  	setEntryInStore(id, value);
  }

  @Override
  public int size() {
    System.err.println("Calling unimplemented method size()");
    return 0;
  }

  public SleepyCatStoreCache(String cacheFolder) throws DatabaseException{
    datadir = new File(cacheFolder);
    if( !datadir.exists())
      datadir.mkdir();
    initialize();
  }

  private void initialize() throws DatabaseException{
    environmentConfig = new EnvironmentConfig();
    environmentConfig.setAllowCreate(true);
    environmentConfig.setTransactional(false);
    environmentConfig.setCacheSize(cachesize);

    // perform other environment configurations

    environment = new Environment(datadir, environmentConfig);

    databaseConfig = new DatabaseConfig();
    databaseConfig.setAllowCreate(true);
    databaseConfig.setTransactional(false);

    // perform other database configurations

    openDB();

    // add a shutdown hook to close the database when the VM is terminated
    sh = new Shutdown();
    sh.cache = this;
    Runtime.getRuntime().addShutdownHook(sh);
  }
  
  public void close(){
  	this.closeDatabase();
  	Runtime.getRuntime().removeShutdownHook(sh);
  }
  

  private void openDB() {
    try {
      database = environment.openDatabase(null, databaseName, databaseConfig);
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  public Iterator<Map.Entry<K, V>> iterator() {
    return new CacheIterator();
  }
  
  public Iterator<V> valueIterator(){
    return new ValueIterator();
  }
  
  private class ValueIterator implements Iterator<V>{
    private CacheIterator cacheIterator;
    public ValueIterator(){
      cacheIterator = new CacheIterator();
    }
    public boolean hasNext() {
      return cacheIterator.hasNext();
    }

    public V next() {
      return cacheIterator.next().getValue();
    }

    public void remove() {
      System.err.println("Method not implemented");
    }
    
  }
  
  private class CacheIterator implements Iterator<Map.Entry<K, V>> {
    Cursor myCursor;
    DatabaseEntry foundKey = new DatabaseEntry();
    DatabaseEntry foundData = new DatabaseEntry();
    Map.Entry<K, V> next = null;
    Boolean toggle = false;

    public CacheIterator() {
      try {
        myCursor = database.openCursor(null, null);
      } catch (DatabaseException e) {
        e.printStackTrace();
      }
    }

    @SuppressWarnings("unchecked")
    public boolean hasNext() {
      Boolean result = false;
      if (myCursor != null) {
        try {
          if (!toggle) {
            if (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
              K key = (K) keyBinding.entryToObject(foundKey);
              V value = (V) valueBinding.entryToObject(foundData);
              next = new Entry(key, value);
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

    @SuppressWarnings("unchecked")
    public Map.Entry<K, V> next() {
      Map.Entry<K, V> result = null;
      if (myCursor != null) {
        if (toggle) {
          result = next;
        }
        else {
          try {
            if (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
              K key = (K) keyBinding.entryToObject(foundKey);
              V value = (V) valueBinding.entryToObject(foundData);
              result = new Entry(key, value);
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
      System.out.println("Remove is not implemented for iterator!");
    }
    
    private class Entry implements Map.Entry<K, V>{
      private K key;
      private V value;
      public K getKey() {
        return key;
      }

      public V getValue() {
        return value;
      }

      public V setValue(V value) {
        this.value = value;
        return value;
      }
      
      public Entry(K key, V value){
        this.key = key;
        this.value = value;
      }
        
      
    }
  }
  protected class Shutdown extends Thread {
    public SleepyCatStoreCache<K, V> cache;

    public void run() {
    	cache.closeDatabase();
      System.out.println("Shutdown hook called!");
    }
  }

}