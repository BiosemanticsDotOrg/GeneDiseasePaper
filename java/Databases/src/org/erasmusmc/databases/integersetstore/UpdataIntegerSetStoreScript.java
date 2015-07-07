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
import java.util.List;

import org.erasmusmc.groundhog.ConceptFrequencyCache;
import org.erasmusmc.groundhog.ConceptSumOfValuesCache;
import org.erasmusmc.groundhog.ConceptToRecordIndex;
import org.erasmusmc.groundhog.RecordDataBaseBinding;
import org.erasmusmc.groundhog.UpdateGroundhogScript;
import org.erasmusmc.ontology.Ontology;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public class UpdataIntegerSetStoreScript {
  //This script changes the name of the database from the same as the dir to integerset
  // this allows the changing of the source dir name (standard in new Groundhogs)
  /**
   * @param args
   */
  private static final long serialVersionUID = -1070115985621064906L;
  protected Ontology ontology = null;
  protected String databaseName ="IntegerSetStore";
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

  public UpdataIntegerSetStoreScript(File datadir) throws DatabaseException {
    environmentConfig = new EnvironmentConfig();
    environmentConfig.setAllowCreate(true);
    environmentConfig.setTransactional(true);
    environmentConfig.setCacheSize(302400000);

    // perform other environment configurations

    environment = new Environment(datadir, environmentConfig);
    List<String> names = environment.getDatabaseNames();
    if (names.contains(datadir.getName())) {
      environment.renameDatabase(null, datadir.getName(), databaseName);
    }
    environment.close();
  }

  public static void main(String[] args) throws Exception {
    new UpdataIntegerSetStoreScript(new File(args[0]));

  }
}
