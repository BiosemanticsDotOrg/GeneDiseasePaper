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

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.erasmusmc.collections.MapCursor;
import org.erasmusmc.collections.SortedIntList2FloatMap;
import org.erasmusmc.math.vector.ObjectAndDoubleEntry;
import org.erasmusmc.math.vector.SparseVectorInt2Float;

public class ConceptVector extends SparseVectorInt2Float implements Serializable {
  private static final long serialVersionUID = -8690611406482615779L;
  public Ontology ontology;

  public ConceptVector(Ontology ontology) {
    this.ontology = ontology;
  }

  public ConceptVector(Ontology ontology, SortedIntList2FloatMap map) {
    super(map);
    this.ontology = ontology;
  }
  
  //Copy constructor
  public ConceptVector copy(){
    SortedIntList2FloatMap map = new SortedIntList2FloatMap(values.size());
    for (int i = 0; i < values.size(); i++)
      map.addEntry(values.getKey(i), values.getValue(i));
    return new ConceptVector(this.ontology, map);
  }

  public Concept getConceptByIndex(int index) {
    Concept concept = ontology.getConcept(values.getKey(index));
    if (concept == null) {
      concept = new Concept(values.getKey(index));
    }
    return concept;
  }

  /*public VectorCursor<Concept> getNonZeroConceptCursor() {
    return new NonZeroConceptCursor();
  }*/

  public String printValues() {
    List<ObjectAndDoubleEntry<Integer>> values = getNonZeroEntriesSortedOnDescendingValue();

    StringBuffer result = new StringBuffer();
    for (ObjectAndDoubleEntry<Integer> entry: values){
      if (ontology != null)
        result.append(ontology.getConcept(entry.key).toString());
      else
        result.append(entry.key.toString());
      result.append("\t");
      result.append(entry.value);
      result.append("\n");
    }
    return result.toString();
  }

  private void writeObject(java.io.ObjectOutputStream out) throws IOException {
//    GZIPOutputStream gos = new GZIPOutputStream(out);
//    ObjectOutputStream oos = new ObjectOutputStream(gos);
//    oos.writeInt(values.size());
    out.writeInt(values.size());
    MapCursor<Integer, Float> cursor = values.getEntryCursor();
    while (cursor.isValid()) {
//    oos.writeInt(cursor.key());
//    oos.writeFloat(cursor.value());
      out.writeInt(cursor.key());
      out.writeFloat(cursor.value());
      cursor.next();
    }
//    oos.flush();
//    gos.finish();
  }

  private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
//    GZIPInputStream gis = new GZIPInputStream(in);
//    ObjectInputStream ois = new ObjectInputStream(gis);
//    int size = ois.readInt();
    int size = in.readInt();
    SortedIntList2FloatMap local = new SortedIntList2FloatMap(size);
    for (int i = 0; i < size; i++) {
//      int read = ois.readInt();
//      float read2 = ois.readFloat();
      int read = in.readInt();
      float read2 = in.readFloat();
      local.addEntry(read, read2);
    }
    values = local;
  }
  //Martijn says: no idea what this was still doing here, but I found it confusing so removed it. 
  //Please let me know if it was essential!
  /*protected class NonZeroConceptCursor implements VectorCursor<Concept>, Serializable {
    private static final long serialVersionUID = 5262996189749587626L;
    protected VectorCursor<Integer> nonzerocursor;

    public NonZeroConceptCursor() {
      nonzerocursor = getNonzeroCursor();
    }

    public boolean isValid() {

      return nonzerocursor.isValid();
    }

    public void next() {
      nonzerocursor.next();

    }

    public Concept dimension() {

      return ontology.getConcept(nonzerocursor.dimension());
    }

    public int index() {

      return nonzerocursor.index();
    }

    public double get() {

      return nonzerocursor.get();
    }

    public void set(double value) {
      nonzerocursor.set(value);

    }

  }*/
}
