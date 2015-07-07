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

package org.erasmusmc.textMining.LVG;

import gov.nih.nlm.nls.lvg.Api.NormApi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Vector;

import org.erasmusmc.utilities.AbstractNormaliser;

public class LVGNormaliser extends AbstractNormaliser implements Serializable {

	private static final long serialVersionUID = 4718712204307521955L;
	private NormApi norm;
  public String propertiesPath = "/tmp/lvg2011lite/data/config/lvg.properties";


  public LVGNormaliser() {
    norm = new NormApi(propertiesPath);
  }

  /**
   * Use this constructor to specify the location of the lvg.properties file.
   * 
   * @param propertiesPath
   *          The exact location of the lvg.properties file.
   */
  public LVGNormaliser(String propertiesPath) {
    this.propertiesPath = propertiesPath;
    norm = new NormApi(propertiesPath);
  }
 
  @Override
  protected String externalnormalise(String word) {
    Vector<String> outputFromNorm = null;
    try {
      outputFromNorm = norm.Mutate(word);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (outputFromNorm.size() == 0) return word; else return outputFromNorm.get(0).toString();
  }

  protected void finalize() {
    norm.CleanUp();
  }



  private void writeObject(ObjectOutputStream s) throws IOException {
    s.writeObject(cache);
    s.writeObject(propertiesPath);
  }

  @SuppressWarnings( { "unchecked"})
  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    cache = (Map<String, String>) s.readObject();
    propertiesPath = (String) s.readObject();
    norm = new NormApi(propertiesPath);
  }
}