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

package org.erasmusmc.ids;

import java.util.HashMap;
import java.util.Map;

public class IDManager {
  private static Map<String, Map<String, DatabaseID>> database2Map = new HashMap<String, Map<String, DatabaseID>>();
  public static DatabaseID get(String database, String id){
    Map<String, DatabaseID> map = database2Map.get(database);
    if (map == null){
      map = new HashMap<String, DatabaseID>();
      database2Map.put(database, map);
    }
    DatabaseID result = map.get(id);
    if (result == null){
      result = new DatabaseID(database, id);
      map.put(id, result);
    }
    return result;
  }
}
