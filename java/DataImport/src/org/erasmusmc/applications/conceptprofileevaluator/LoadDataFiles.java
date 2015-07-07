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

package org.erasmusmc.applications.conceptprofileevaluator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import com.collexis.data.Thesaurus;

public class LoadDataFiles {
  public static Map<Integer, List<Integer>> loadPmidsPerGene ( InputStream inputStream){
    Map<Integer, List<Integer>> result = new HashMap<Integer, List<Integer>>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream),1000000);
    try {
      while (bufferedReader.ready()){
        String[] pmids = bufferedReader.readLine().split("\t");
    
        List<Integer> pmidsInList = new ArrayList<Integer>();
        for (int i= 1 ; i<pmids.length; i++){
          pmidsInList.add(Integer.parseInt(pmids[i]));
        }
        result.put(Integer.parseInt(pmids[0]),pmidsInList);
      }
    
    } catch (IOException e) {
      
      e.printStackTrace();
    }
    return result;
  }
  /*
  public static Map<Integer, List<String>> loadPmidsPerGO ( InputStream inputStream, Thesaurus thesaurus, ConceptProfileEvaluatorDataSet conceptprofileDataSet){
    Map<Integer, List<String>> result = new HashMap<Integer, List<String>>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream),1000000);
    try {
      while (bufferedReader.ready()){
        String[] pmids = bufferedReader.readLine().split("\t");
    
        List<String> pmidsInList = new ArrayList<String>();
        for (int i= 1 ; i<pmids.length; i++){
          pmidsInList.add(pmids[i]);
              
        result.put(Integer.parseInt(pmids[0]),pmidsInList);
      }
      }
    
    } catch (IOException e) {
      
      e.printStackTrace();
    }
    return result;
  } 
  */
  public static Set<Integer> loadIDs (InputStream inputStream ){
    Set<Integer> conceptsToBeFiltered = new HashSet<Integer>();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    Pattern p = Pattern.compile("^[0-9]+");
    try {
      while (bufferedReader.ready()){
        Matcher m = p.matcher(bufferedReader.readLine());
        if (m.find()){
        conceptsToBeFiltered.add(Integer.parseInt(m.group()));
        }
      }
    } catch (IOException e) {
      
      e.printStackTrace();
    }
    return conceptsToBeFiltered;
  }

}
