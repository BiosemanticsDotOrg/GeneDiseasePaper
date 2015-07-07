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

package org.erasmusmc.semanticnetwork;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.erasmusmc.applications.thesaurusenricher.InternetDatabase;
import org.erasmusmc.webservice.ServerConnection;

public class SemanticNetwork implements Serializable{
  private static final String DEFAULTTYPESFILE = "semanticTypes.txt";
  private static final String DEFAULTGROUPSFILE = "semanticGroups.txt";
  
  public boolean loaded = false;
  public Map<Integer, SemanticType> types = new TreeMap<Integer, SemanticType>();
  public Map<String, SemanticGroup> groups = new TreeMap<String, SemanticGroup>();
  public Map<Integer, InternetDatabase> internetDatabases = new TreeMap<Integer, InternetDatabase>();
  
  public void load(ServerConnection serverConnection, String thesaurusName) throws RemoteException {
    if (!loaded) {
      groups = serverConnection.getSemanticGroups(thesaurusName);
      types = serverConnection.getSemanticTypes(thesaurusName, groups);
      internetDatabases = serverConnection.getInternetDatabases(thesaurusName);
      loaded = true;
    }
  }
  
  public void loadFromFile(InputStream semanticGroupStream, InputStream semanticFileStream) {
    int currentpos = -1;
    SemanticType currentParent = null;
    Stack<SemanticType> stack = new Stack<SemanticType>();
    stack.push(currentParent);
    
    String groupAbr = "";
    
    if (!loaded) {
      BufferedReader brGroup = new BufferedReader(new InputStreamReader(semanticGroupStream));
      BufferedReader brTypes = new BufferedReader(new InputStreamReader(semanticFileStream));
      
      try {
        while(brTypes.ready()) {
          String[] columns = brTypes.readLine().trim().split("\\|");
          if(columns.length > 3) {
            int pos = Integer.parseInt(columns[0]);
            int id = Integer.parseInt(columns[1]);
            String name = columns[2];
            String description = columns[3];
            
            SemanticType semanticType = new SemanticType();
            semanticType.ID = id;
            semanticType.name = name;
            semanticType.description = description;
            
            types.put(semanticType.ID, semanticType);
  
            if(pos == currentpos) {
              stack.pop();
              currentParent = stack.pop();
            }
            else {
              for(int i=currentpos; i > (pos-2); i--) currentParent =  stack.pop();
            }

            stack.push(currentParent);
            currentpos = pos;
            semanticType.parent = currentParent;
            currentParent = semanticType;
            stack.push(semanticType);

          }
        }
      }
      catch (Exception ex) {ex.printStackTrace(); }
      
      try {
        SemanticGroup semanticGroup = null;
        while(brGroup.ready()) {
          String[] columns = brGroup.readLine().trim().split("\\|");
          if(columns.length > 3) {
            if(groupAbr.compareTo(columns[0]) != 0) {
              semanticGroup = new SemanticGroup();
              semanticGroup.ID = groupAbr = columns[0];
              semanticGroup.name = columns[1];
              groups.put(semanticGroup.ID, semanticGroup);
            }
            for(SemanticType type: types.values()) {
              if(type.ID == Integer.parseInt(columns[2].substring(1))) type.group = semanticGroup;
            }
          }
        }
      }
      catch (Exception ex) {ex.printStackTrace();  }
      loaded = true;
    }    
  }
  
  public void loadDefaultsFromFile() {
    loadFromFile(SemanticNetwork.class.getResourceAsStream(DEFAULTGROUPSFILE), SemanticNetwork.class.getResourceAsStream(DEFAULTTYPESFILE));
  }
  private static final long serialVersionUID = 1L;
}
