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

package org.erasmusmc.rmi.peregrine.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.peregrine.AbstractPeregrine;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.Tokenizer;
import org.erasmusmc.peregrine.disambiguator.GeneDisambiguator;
import org.erasmusmc.peregrine.disambiguator.UMLSDisambiguator;
import org.erasmusmc.rmi.peregrine.client.RMIPeregrineResult;


/**
 * Class used for running a Peregrine server
 * @author Schuemie
 *
 */
public class RMIPeregrineImplementation extends UnicastRemoteObject implements RMIPeregrineInterface {
  private static final long serialVersionUID = 5208934555368074225L;
  private AbstractPeregrine peregrine;
  private Lock lock = new ReentrantLock();
  private GeneDisambiguator geneDisambiguator;
  private UMLSDisambiguator umlsDisambiguator;

  private int minConceptID = 2000000;
  
  /**
   * Constructor 
   * @param peregrine   Specify a specific Peregrine that will be used by this instance 
   * @throws RemoteException
   */
  public RMIPeregrineImplementation(AbstractPeregrine peregrine) throws RemoteException {
    this.peregrine = peregrine;
    if (peregrine instanceof ConceptPeregrine)
      initConceptPeregrine();
  }
  
  /**
   * Constructor. Will use a ConceptPeregrine with the LVG properties file in the default location 
   * ("/home/public/LVG/lvg2006lite/data/config/lvg.properties") 
   * @throws RemoteException
   */
  public RMIPeregrineImplementation() throws RemoteException {
    peregrine = new ConceptPeregrine();
    initConceptPeregrine();
  }
  
  private void initConceptPeregrine(){
    ((ConceptPeregrine)peregrine).destroyOntologyDuringRelease = true;
  }
  
  @Override
  protected void finalize() throws Throwable {
   System.out.println("finalizing!");
    super.finalize();
  }
  /**
   * Specify the ontology to be used by the peregrine
   * @param ontology    The ontology
   * @throws RemoteException
   */
  public void setOntology(Ontology ontology) throws RemoteException {
    peregrine.setOntology(ontology);
  }
  
  /**
   * Specify the lowest concept ID for genes (and highest ID for other concepts)
   * @param minConceptID
   */
  public void setMinGeneConceptID(int minConceptID) {
    this.minConceptID = minConceptID;
  }
  
  /**
   * Release the ontology, and initializes the disambiguator (if any)
   * @throws RemoteException
   */
  public void release() throws RemoteException {
    peregrine.release();
    peregrine.setOntology(null);
    if (peregrine instanceof ConceptPeregrine){
      geneDisambiguator = new GeneDisambiguator((ConceptPeregrine)peregrine, minConceptID, Integer.MAX_VALUE);
      umlsDisambiguator = new UMLSDisambiguator(0, minConceptID);
    }
  }
  
  /**
   * Defines the list of stopwords that will be used for indexation.
   * Should be specified before releasing the thesaurus.
   * Stopwords should be in lowercase.
   */
  //public void setStopWords(Set<String> stopwords) throws RemoteException {
  //  peregrine.stopwords = stopwords;
  //}
  
  /**
   * Define the cache containing the normaliser cache (if any)
   * @param normaliserCacheFile
   * @throws RemoteException
   */
  //public void setNormaliserCache(String normaliserCacheFile) throws RemoteException {
  //  peregrine.normaliser.loadCacheBinary(normaliserCacheFile);
 // }

/*
  private void setCountTokenUsage(boolean bool) throws RemoteException {
    peregrine.countTokenUsage = bool;
  }
*/
  
  /** If several terms map to the same words, only the term consisting of the most words will be selected,
   * if this parameter is set to true.
   * e.g.: Suppose 'Alzheimer's disease' maps to two terms: 'Alzheimer's disease' and 'disease', then
   * only the first term will be selected if this parameter is set to true. 
   * <br><br>The default value is True.*/
  //public void setBiggestMatchOnly(boolean bool) throws RemoteException {
  //  if (peregrine instanceof ConceptPeregrine)
  //    ((ConceptPeregrine)peregrine).biggestMatchOnly = bool;
 // }
  
  /** If true, the entire ontology structure will be destroyed during release, thus saving memory.
   * <br><br>The default value is False.*/  
  //public void setDestroyOntologyDuringRelease(boolean bool) throws RemoteException {
  //  if (peregrine instanceof ConceptPeregrine)
  //    ((ConceptPeregrine)peregrine).destroyOntologyDuringRelease = bool;
 // }
  
  /** Specifies the window size for finding the next word of a term. 
   * A window size of 1 means that no other words are allowed between the words of a term. 
   * <br><br>The default value is 1.
   */  
  //public void setWindowSize(int num) throws RemoteException {
  //  if (peregrine instanceof ConceptPeregrine)
  //    ((ConceptPeregrine)peregrine).windowSize = num;
  //}
  
  /**
   * Method used by the remote rmiPeregrine object. Do not call directly
   */
  public RMIPeregrineResult index(String string, int disambiguation) throws RemoteException {
    lock.lock();
    RMIPeregrineResult result = new RMIPeregrineResult();
    peregrine.index(string);
    if(peregrine instanceof ConceptPeregrine) 
      if (disambiguation == DISAMBIGUATION){
        geneDisambiguator.disambiguate((ConceptPeregrine)peregrine);
        umlsDisambiguator.disambiguate((ConceptPeregrine)peregrine);
      } else if (disambiguation == DISAMBIGUATION_WITH_DETAILS){
        result.disambiguationDetails = geneDisambiguator.disambiguateWithDetails((ConceptPeregrine)peregrine);
        result.disambiguationDetails.add(umlsDisambiguator.disambiguateWithDetails((ConceptPeregrine)peregrine));
      }
    result.tokenizer = new Tokenizer(peregrine.tokenizer);
    result.resultConcepts = new ArrayList<ResultConcept>(peregrine.resultConcepts);
    result.resultTerms = new ArrayList<ResultTerm>(peregrine.resultTerms);  
    lock.unlock();
    return result;    
  }
  
  /**
   * The normal behavior of the disambiguator is to, when in doubt, assign all possible meanings. If 
   * the number of meanings is higher than the value specified here, it will assign none of the
   * possible meanings.
   * <br>
   * <br>
   * The default value is 3.
   */
  public void setMaxMeaningsForAutoAssign(int val) { 
    umlsDisambiguator.maxMeaningsForAutoAssign = val;
  }
  
  /**
   * Returns the Peregrine used by this instance
   * @return
   */
  public AbstractPeregrine getPeregrine(){
    return peregrine;
  }
  

}
