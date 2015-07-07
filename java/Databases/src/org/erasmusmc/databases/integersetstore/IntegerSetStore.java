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

package org.erasmusmc.databases.integersetstore;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.erasmusmc.collections.SortedIntListSet;
import org.erasmusmc.storecaching.StoreMapCaching;

import com.sleepycat.bind.EntryBinding;
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

public class IntegerSetStore extends StoreMapCaching<Integer, SortedIntListSet> {
  protected String databaseName = "IntegerSetStore";
  protected EnvironmentConfig environmentConfig;
  protected Environment environment;
  protected DatabaseConfig databaseConfig;
  protected Database integerToSetOfIntegersStore;
  protected EntryBinding myIntegerBinding;
  protected EntryBinding myDataBinding;
  protected IntegerSetStoreShutdown sh;
  
  public IntegerSetStore(String foldername){
    File file = new File(foldername);
    if (!file.exists())
      file.mkdir();
    init(file);
  }
  public IntegerSetStore(File datadir) {
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
      myDataBinding = new IntegerToSetOfIntegersBinding();
      
      sh = new IntegerSetStoreShutdown();
      sh.g = this;
      Runtime.getRuntime().addShutdownHook(sh);
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

  }

  public void openDB() {
    try {
      this.integerToSetOfIntegersStore = environment.openDatabase(null, this.databaseName, this.databaseConfig);
      // environment.removeDatabase(null,this.dbName);
    } catch (DatabaseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @Override
  public int size() {
    int size = 0;
    try {
      DatabaseStats stats = integerToSetOfIntegersStore.getStats(null);
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

  public Iterator<Integer2IntegerSet> iterator() {
    return new DBIterator();

  }
  private void closeDatabase() {
    try {
      if (integerToSetOfIntegersStore != null) {
        integerToSetOfIntegersStore.close();
      }

      if (environment != null) {
        environment.cleanLog(); // Clean the log before closing
        environment.close();
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }
  
  public void close(){
    closeDatabase();
    Runtime.getRuntime().removeShutdownHook(sh);
  }

  protected void finalize() {
    close();
  }

  public static Comparator<Integer> getAscendingIntegerComparator() {
    return new Comparator<Integer>() {

      public int compare(Integer arg0, Integer arg1) {

        return arg0 - arg1;
      }

    };
  }

  @Override
  protected void setEntryInStore(Integer id, SortedIntListSet value) {
    try {
      DatabaseEntry databaseKey = new DatabaseEntry();
      myIntegerBinding.objectToEntry(id, databaseKey);
      DatabaseEntry databaseValue = new DatabaseEntry();
      myDataBinding.objectToEntry(value, databaseValue);
      integerToSetOfIntegersStore.put(null, databaseKey, databaseValue);

    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected Map<Integer, SortedIntListSet> getEntriesFromStoreWithIDs(Collection<Integer> ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  protected SortedIntListSet getEntryFromStoreWithID(Integer id) {
    SortedIntListSet result = null;
    try {
      DatabaseEntry databaseKey = new DatabaseEntry();
      DatabaseEntry databaseValue = new DatabaseEntry();
      myIntegerBinding.objectToEntry(id, databaseKey);
      integerToSetOfIntegersStore.get(null, databaseKey, databaseValue, LockMode.DEFAULT);
      if (databaseValue.getSize() != 0) {
        result = (SortedIntListSet) myDataBinding.entryToObject(databaseValue);
      }
    } catch (DatabaseException e) {
      e.printStackTrace();
    }

    return result;
  }

  protected class DBIterator implements Iterator<Integer2IntegerSet> {
    Cursor myCursor;
    DatabaseEntry foundKey = new DatabaseEntry();
    DatabaseEntry foundData = new DatabaseEntry();
    Integer2IntegerSet next = null;
    Boolean nextHasBeenRetrieved = false;

    public DBIterator() {
      try {
        myCursor = integerToSetOfIntegersStore.openCursor(null, null);
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
              SortedIntListSet set = (SortedIntListSet) myDataBinding.entryToObject(foundData);
              next = new Integer2IntegerSet(key, set);
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

    public Integer2IntegerSet next() {
      Integer2IntegerSet result = null;
      if (myCursor != null) {
        if (nextHasBeenRetrieved) {
          result = next;
        }
        else {
          try {
            if (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
              Integer key = (Integer) myIntegerBinding.entryToObject(foundKey);
              SortedIntListSet set = (SortedIntListSet) myDataBinding.entryToObject(foundData);
              result = new Integer2IntegerSet(key, set);

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
  protected class IntegerSetStoreShutdown extends Thread {
    public IntegerSetStore g;

    public void run() {
      g.closeDatabase();
    }
  }

}
