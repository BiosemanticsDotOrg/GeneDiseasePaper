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

import java.util.HashSet;
import java.util.Set;

import org.erasmusmc.ids.DatabaseID;

public class Gene {
  String preferredSymbol;
  Set<DatabaseID> ids = new HashSet<DatabaseID>();
  Set<String> symbols = new HashSet<String>();
  Set<String> names = new HashSet<String>();
  Set<Integer> taxonIDs = new HashSet<Integer>(1);
  String source;
  
  public Gene(String source){
    this.source = source;
  }
  
  public void merge(Gene other) {
    this.names.addAll(other.names);
    this.symbols.addAll(other.symbols);
    this.ids.addAll(other.ids);
    this.taxonIDs.addAll(other.taxonIDs);
  }
}
