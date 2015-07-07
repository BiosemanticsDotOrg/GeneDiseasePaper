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

package org.erasmusmc.ontology.ontologyutilities.evaluationScripts;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.erasmusmc.medline.MedlineIterator;
import org.erasmusmc.medline.MedlineListener;
import org.erasmusmc.medline.MedlineRecord;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ReleasedTerm;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.disambiguator.GeneDisambiguator;
import org.erasmusmc.peregrine.disambiguator.UMLSDisambiguator;
import org.erasmusmc.utilities.StringUtilities;

public class OntologyFrequencyCount implements MedlineListener {

  public static String pmidsFile = "/home/public/PMIDs/Random100.000.PMIDs";
  public static String outputFile = "/home/khettne/UMLS/2008AB/freq_umls2008AB.txt";
  public static String ontologyPath = "/home/khettne/UMLS/2008AB/UMLS2008AB_060209.ontology";
  
  public static boolean disambiguate = false;

  public static String normaliserCacheFile = "/home/public/Peregrine/standardNormCache2006.bin";

  private GeneDisambiguator disambiguator;
  private UMLSDisambiguator UMLSdisambiguator;

  public static void main(String[] args) {
    System.out.println("Starting script. " + StringUtilities.now());  
    System.out.println("Loading ontology. " + StringUtilities.now());
    
    OntologyFileLoader loader = new OntologyFileLoader();    
    Ontology ontology = loader.load(ontologyPath);
    
    System.out.println("Done loading ontology. " + StringUtilities.now());
    
    new OntologyFrequencyCount(ontology);
  }

  public OntologyFrequencyCount(Ontology ontology) {

    indexer.setOntology(ontology);
    medlineIterator.pmidsFile = pmidsFile;

    System.out.println("Loading normaliser cache. " + StringUtilities.now());
    indexer.normaliser.loadCacheBinary(normaliserCacheFile);

    System.out.println("Releasing thesaurus. " + StringUtilities.now());
    indexer.destroyOntologyDuringRelease = false;
    indexer.release();

    if (disambiguate) {
      UMLSdisambiguator = new UMLSDisambiguator(0,3000000);
      disambiguator = new GeneDisambiguator(indexer, 3000000, Integer.MAX_VALUE);
    }
    System.out.println("Starting indexation cycles. " + StringUtilities.now());
    medlineIterator.iterate(this);

    System.out.println("Generating results. " + StringUtilities.now());
    generateResults(outputFile, ontology);

    System.out.println("Done. " + StringUtilities.now());
  }

  private void generateResults(String filename, Ontology ontology) {
    try {
      FileOutputStream PSFFile = new FileOutputStream(filename);
      BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(PSFFile), 1000000);
      try {
        for (Entry<ReleasedTerm, Count> entry: releasedTerm2Count.entrySet()) {
          ReleasedTerm term = entry.getKey();
          StringBuffer line = new StringBuffer();
          line.append(entry.getValue().count);
          line.append("\t");
          int id = term.conceptId[0];
          int tid = term.termId[0];
          line.append(ontology.getConcept(id).getTerms().get(tid).text);
          line.append("\t");
          for (int cid: term.conceptId) {
            line.append(cid);
            line.append(";");
          }
          bufferedWrite.write(line.toString());
          bufferedWrite.newLine();
        }
        bufferedWrite.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public void processMedlineRecords(List<MedlineRecord> records) {
    for (int i = 0; i < records.size(); i++) {
      MedlineRecord currentRecord = records.get(i);
      indexer.index(currentRecord.titleAbsMesh());
      if (disambiguate){
        UMLSdisambiguator.disambiguate(indexer);
        disambiguator.disambiguate(indexer);
      }
      for (ResultTerm term: indexer.resultTerms) {
        Count count = releasedTerm2Count.get(term.term);
        if (count == null) {
          count = new Count();
          releasedTerm2Count.put(term.term, count);
        }
        count.count++;
      }
    }
  }

  private class Count {
    int count = 0;
  }

  protected class ReleasedTermComparator implements Comparator<ReleasedTerm> {
    public int compare(ReleasedTerm arg0, ReleasedTerm arg1) {
      int result = arg0.conceptId[0] - arg1.conceptId[0];
      if (result == 0)
        result = arg0.termId[0] - arg1.termId[0];
      return result;
    }
  }

  private Map<ReleasedTerm, Count> releasedTerm2Count = new TreeMap<ReleasedTerm, Count>(new ReleasedTermComparator());
  private MedlineIterator medlineIterator = new MedlineIterator();
  private ConceptPeregrine indexer = new ConceptPeregrine();
}
