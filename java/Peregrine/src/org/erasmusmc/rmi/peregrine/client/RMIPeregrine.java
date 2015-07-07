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

package org.erasmusmc.rmi.peregrine.client;

import org.erasmusmc.peregrine.AbstractPeregrine;
import org.erasmusmc.peregrine.disambiguator.DisambiguationDetails;
import org.erasmusmc.rmi.peregrine.server.RMIPeregrineInterface;

/**
 * RMI interface to a Peregrine running on a server
 * @author Schuemie
 *
 */
public class RMIPeregrine extends AbstractPeregrine {
  private RMIPeregrineConnector connector;
  private boolean disambiguate = true;
  private boolean getDetails = false;
  
  /**
   * Data object containing the disambiguation details of the last indexation (if DisambiguationDetails is set to true).
   */
  public DisambiguationDetails disambiguationDetails;

  /**
   * Connect to the Peregrine server running on the server
   * @param server  The name or IP address of the server
   * @param port    The port on which the server can be contacted
   * @param serviceName The name of the service
   */
  public RMIPeregrine(String server, int port, String serviceName) throws Exception {
    connector = new RMIPeregrineConnector(server, port, serviceName);
  }

  
  @Override
  public void index(String string) {
    int disambiguation;
    if (disambiguate)
      if (getDetails)
        disambiguation = RMIPeregrineInterface.DISAMBIGUATION_WITH_DETAILS;
      else
        disambiguation = RMIPeregrineInterface.DISAMBIGUATION;
    else
      disambiguation = RMIPeregrineInterface.NO_DISAMBIGUATION;
    
    RMIPeregrineResult result = connector.index(string, disambiguation);
    resultConcepts = result.resultConcepts;
    resultTerms = result.resultTerms;
    tokenizer = result.tokenizer;
    disambiguationDetails = result.disambiguationDetails;
  }
  
  /**
   * Specify whether the indexations should make use of the disambigation. The default value is true.
   * @param disambiguate    Set to true to automatically apply disambiguation after indexation.
   */
  public void setDisambiguate(boolean disambiguate) {
    this.disambiguate = disambiguate;
  }
  
  /**
   * Specifiy whether the indexations should return the details of the disambiguation. The default value
   * is false.
   * @param getDetails
   */
  public void setDisambiguationDetails(boolean getDetails){
    this.getDetails = getDetails;
  }
  
  public void release() {
    
  }

}
