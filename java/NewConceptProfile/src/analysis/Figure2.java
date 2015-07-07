/*
 * Concept profile generation and analysis for Gene-Disease paper
 * Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
 *  Leiden, The Netherlands
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

package analysis;

import static KnowledgeTransfer.ConceptProfileUtil.loadConceptFrequencies;
import static KnowledgeTransfer.ConceptProfileUtil.readCidFile;
import static KnowledgeTransfer.PathConfigs.ANALYSIS_DIR;
import static KnowledgeTransfer.PathConfigs.CONCEPT_FREQUENCIES_FILENAME;
import static KnowledgeTransfer.PathConfigs.THESAURUS_DISEASE_CIDS;
import static KnowledgeTransfer.PathConfigs.HPRD_GENE_CIDS;
import static java.util.Collections.reverseOrder;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.erasmusmc.utilities.WriteCSVFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;


public class Figure2 {

  private static Logger _LOG = LoggerFactory.getLogger(Figure2.class);


  public static List<Integer> getFrequenciesForConcepts(Map<Integer, Integer> conceptid2frequency,
      List<Integer> concepts) {
    List<Integer> result = new ArrayList<Integer>();

    for (Integer concept : concepts) {
      Integer freq = conceptid2frequency.get(concept);

      if (freq != null) {
        result.add(freq);
      }
    }

    return result;
  }


  public static void writeColumns(String fileName, List<Integer> freqs) {
    WriteCSVFile output = new WriteCSVFile(fileName);
    int count = 1;
    output.write(Arrays.asList("Rank", "Frequency"));

    for (Integer freq : freqs) {
      String col1 = Integer.toString(count++);
      String col2 = freq.toString();
      output.write(Arrays.asList(col1, col2));
    }

    output.close();
  }


  public static void main(String[] args) {
    Stopwatch stopWatch = Stopwatch.createStarted();

    Map<Integer, Integer> conceptid2frequency =
        loadConceptFrequencies(CONCEPT_FREQUENCIES_FILENAME);

    // First for genes:
    List<Integer> cids = readCidFile(HPRD_GENE_CIDS);
    List<Integer> result = getFrequenciesForConcepts(conceptid2frequency, cids);
    Collections.sort(result, reverseOrder());
    writeColumns(ANALYSIS_DIR + "gene_literature_freq.csv", result);

    // Diseases:
    cids = readCidFile(THESAURUS_DISEASE_CIDS);
    
    result = getFrequenciesForConcepts(conceptid2frequency, cids);
    Collections.sort(result, Collections.reverseOrder());
    writeColumns(ANALYSIS_DIR + "disease_literature_freq.csv", result);

    _LOG.info("{} ", stopWatch);// stopWatch
  }
}
