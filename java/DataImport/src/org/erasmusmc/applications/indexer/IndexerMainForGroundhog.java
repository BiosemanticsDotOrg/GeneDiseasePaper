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

package org.erasmusmc.applications.indexer;

import java.util.ArrayList;
import java.util.List;

import org.erasmusmc.groundhog.Groundhog;
import org.erasmusmc.medline.MedlineIterator;
import org.erasmusmc.medline.MedlineListener;
import org.erasmusmc.medline.MedlineRecord;
import org.erasmusmc.ontology.ConceptVector;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.PeregrineOutputConverter;
import org.erasmusmc.peregrine.disambiguator.GeneDisambiguator;
import org.erasmusmc.peregrine.disambiguator.UMLSDisambiguator;
import org.erasmusmc.utilities.StringUtilities;

public class IndexerMainForGroundhog implements MedlineListener {
  
  public String normaliserCacheFile = "/tmp/standardNormCache2006.bin";
  public MedlineIterator medlineIterator = new MedlineIterator();
  public Groundhog groundhog;
  public boolean disambiguate = true;

  private ConceptPeregrine indexer = new ConceptPeregrine();
  public Ontology ontology;

  private GeneDisambiguator geneDisambiguator;
  private UMLSDisambiguator UMLSdisambiguator;
  
  private IndexerUpdateThreadForGroundhog updateThread;
  
  public boolean destroyOntologyDuringRelease = false;

  public void start() {
    System.out.println("Loading normaliser cache. " + StringUtilities.now());
    
    indexer.normaliser.loadCacheBinary(normaliserCacheFile);
    indexer.setOntology(ontology);
    System.out.println("Releasing thesaurus. " + StringUtilities.now());
    indexer.destroyOntologyDuringRelease = destroyOntologyDuringRelease;
    indexer.release();
    if (disambiguate) {
      geneDisambiguator = new GeneDisambiguator(indexer, 3000000, Integer.MAX_VALUE);
      UMLSdisambiguator = new UMLSDisambiguator(0, 3000000);
    }

    initUpdateThread();
    updateThread.start();

    System.out.println("Starting indexation cycles. " + StringUtilities.now());
    medlineIterator.iterate(this);

    try {
      updateThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    System.out.println("Done. " + StringUtilities.now());
  }

  @Override
public void processMedlineRecords(List<MedlineRecord> records) {
    // Index buffer:
    List<ConceptVector> conceptVectors = new ArrayList<ConceptVector>(records.size());

    for (int i = 0; i < records.size(); i++) {
      MedlineRecord currentRecord = records.get(i);
      indexer.index(currentRecord.titleAbsMeshSubs());
      if (disambiguate){
        geneDisambiguator.disambiguate(indexer);
        UMLSdisambiguator.disambiguate(indexer);
      }
      conceptVectors.add(PeregrineOutputConverter.convertResult2ConceptVector(indexer, groundhog.getOntology()));
    }
    // Store buffer:
    try { // wait until previous update job is done:
      updateThread.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    initUpdateThread();
    updateThread.records = records;
    updateThread.conceptVectors = conceptVectors;
    updateThread.start();
  }

  private void initUpdateThread() {
    updateThread = new IndexerUpdateThreadForGroundhog();
    updateThread.groundhog = groundhog;
  }

}
