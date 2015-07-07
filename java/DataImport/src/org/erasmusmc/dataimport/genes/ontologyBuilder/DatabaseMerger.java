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

package org.erasmusmc.dataimport.genes.ontologyBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.erasmusmc.collections.CountingSet;
import org.erasmusmc.collections.OneToManyList;
import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.utilities.StringUtilities;

public class DatabaseMerger {
  private OneToManyList<DatabaseID, Gene> ids2geneInfos = new OneToManyList<DatabaseID, Gene>();
  private GeneList geneList = new GeneList();
  private CountingSet<String> overlapCounts = new CountingSet<String>();
  
  public void merge(GeneList newGeneInfos){
    overlapCounts.clear();
    for (Gene gene : newGeneInfos){
      Set<Gene> matches = findMatches(gene);
      if (matches.size() == 0)
        add(gene);
      else {
        int merged = 0;
        for (Gene match : matches)
          if (validMatch(match, gene)){
            merge(match, gene);
            merged++;
          }
        if (merged == 0)
          add(gene);
        else if (merged > 1)
          System.out.println(merged + " matches found for " + gene.preferredSymbol);

      }
    }
    System.out.println("Overlap statistics: ");
    overlapCounts.printCounts();
  }

  private boolean validMatch(Gene match, Gene gene) {
    List<String> overlap = new ArrayList<String>();
    Set<String> agreement = new HashSet<String>();
    Set<String> disagreement = new HashSet<String>();
    for (DatabaseID databaseID1 : gene.ids)
      for (DatabaseID databaseID2 : match.ids)
        if (databaseID1.database.equals(databaseID2.database))
          if (databaseID1.ID.equals(databaseID2.ID)){
            overlap.add(databaseID1.database);
            agreement.add(databaseID1.database);
          } else
            disagreement.add(databaseID1.database);
        
    int unresolvedDisagreements = 0;
    for (String database : disagreement){
      if (!agreement.contains(database))
          unresolvedDisagreements++;
    }
    //int resolvedDisagreements = disagreement.size() - unresolvedDisagreements;
    if (agreement.size() > unresolvedDisagreements){
      Collections.sort(overlap);
      overlapCounts.add(StringUtilities.join(overlap, " "));
      return true;
    } else
      return false;
  }

  private void merge(Gene match, Gene geneInfo) {
    for (DatabaseID databaseID : geneInfo.ids)
      if (match.ids.add(databaseID))
        ids2geneInfos.put(databaseID, match);
    match.names.addAll(geneInfo.names);
    match.symbols.addAll(geneInfo.symbols);
  }

  private void add(Gene gene) {
    geneList.add(gene);
    for (DatabaseID databaseID : gene.ids)
      ids2geneInfos.put(databaseID, gene);    
  }

  private Set<Gene> findMatches(Gene geneInfo) {
    Set<Gene> result = new HashSet<Gene>();
    for (DatabaseID databaseID : geneInfo.ids)
      result.addAll(ids2geneInfos.get(databaseID));
    return result;
  }
  
  public GeneList getMergedGeneList(){
    return geneList;
  }  
}
