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

package org.erasmusmc.ontology;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileFilter;

import org.erasmusmc.utilities.StringUtilities;

public class ConceptProfile implements Serializable {
  private static final long serialVersionUID = -8619201793112735985L;
  public Integer cui;
  public ConceptVector conceptVector;
  private String name;

  public ConceptProfile() {
  }

  public String toString() {
    if (conceptVector.ontology != null) {
      if(name == null) name = conceptVector.ontology.getConcept(cui).getName(); 
      return "Concept profile: " + name;
    }
      
    if(name == null) name = cui.toString();
    return "Concept profile: " + name;
  }

  public ConceptProfile(Concept concept, ConceptVector conceptVector) {
    this.cui = concept.getID();
    this.conceptVector = conceptVector;
  }

  public ConceptProfile(Integer concept, ConceptVector conceptVector) {
    this.cui = concept;
    this.conceptVector = conceptVector;
  }

  public ConceptProfile(ConceptVectorRecord conceptVectorRecord) {
    this.cui = conceptVectorRecord.ID;
    this.conceptVector = conceptVectorRecord.getConceptVector();
  }

  public ConceptVectorRecord conceptProfileToRecord() {
    ConceptVectorRecord record;
    record = new ConceptVectorRecord(this.cui);

    record.setConceptVector(this.conceptVector);
    return record;
  }

  public Concept getConcept() {
    return conceptVector.ontology.getConcept(cui);
  }

  public static Map<Integer, ConceptProfile> makeConceptProfilesFromRecords(Map<Integer, ConceptVectorRecord> records) {
    Iterator<ConceptVectorRecord> iterator = records.values().iterator();
    Map<Integer, ConceptProfile> conceptProfiles = new HashMap<Integer, ConceptProfile>();// getAllConceptProfilesFromCollexion(sourceCollexion);

    while (iterator.hasNext()) {
      ConceptVectorRecord record = iterator.next();
      ConceptProfile conceptProfile = new ConceptProfile(record.getConceptVector().ontology.getConcept(record.getID()), record.getConceptVector());
      conceptProfiles.put(record.getID(), conceptProfile);

    }
    return conceptProfiles;
  }


  public void export(String filename, FileFilter type) {
    List<String> lines = new ArrayList<String>();
    List<String> cells = new ArrayList<String>();
    cells.add("Concept");
    cells.add("Weight");
    lines.add(StringUtilities.join(cells, "\t"));

    for(int i=0;i<conceptVector.values.size();i++) {
      cells = new ArrayList<String>();
      cells.add(conceptVector.ontology.getConcept(conceptVector.getNonZeroEntriesSortedOnDescendingValue().get(i).key).getName());
      cells.add(Double.toString(conceptVector.getNonZeroEntriesSortedOnDescendingValue().get(i).value));
      lines.add(StringUtilities.join(cells, "\t"));
    }
    
    if(!filename.endsWith(".txt")) filename += ".txt";
    File file = new File(filename);
      try {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(StringUtilities.join(lines, "\n"));
        bw.flush();
        bw.close();
      } catch (IOException e) {
        System.err.println(e.getMessage());
      }
      
  }

  public String getNameForExport() {
    return name;
  }

  public FileFilter getFilefilter() {
    return new ConceptVecorFileFilter();
  }

  
  private class ConceptVecorFileFilter extends FileFilter {
    public boolean accept(File arg0) {
      return true;
    }

    @Override
    public String getDescription() {
      return "Text tab-delimited (.txt)";
    }
  }
}

