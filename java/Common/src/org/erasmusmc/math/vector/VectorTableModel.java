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

package org.erasmusmc.math.vector;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class VectorTableModel<D> implements TableModel {
  protected Vector<D> vector;
  
  public VectorTableModel(Vector<D> vector) {
    this.vector = vector;
  }
    
  public int getRowCount() {
    return vector.getSpace().getDimensions();
  }

  public int getColumnCount() {
    return 2;
  }

  public String getColumnName(int column) {
    switch(column) {
      case 0:   return vector.getSpace().getDimensionsCaption();
      case 1:   return vector.getSpace().getValuesCaption();
      default:  return "Unknown column";
    }
  }

  public Class<?> getColumnClass(int column) {
    switch(column) {
      case 0:   return Object.class;
      case 1:   return Double.class;
      default:  return null;
    }
  }

  public boolean isCellEditable(int row, int column) {
    return false;
  }

  public Object getValueAt(int row, int column) {
    switch(column) {
      case 0:   return vector.getSpace().objectForIndex(row);
      case 1:   return vector.get(vector.getSpace().objectForIndex(row));
      default:  return "Unknown column";
    }
  }

  public void setValueAt(Object arg0, int arg1, int arg2) {
  }

  public void addTableModelListener(TableModelListener arg0) {
  }

  public void removeTableModelListener(TableModelListener arg0) {
  }
}
