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

package org.erasmusmc.peregrine.disambiguator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.erasmusmc.peregrine.ResultConcept;

/**
 * Data class describing details of the disambiguation performed on an indexation.
 * 
 * @author martijn
 *
 */
public class DisambiguationDetails implements Serializable {
  private static final long serialVersionUID = -6170420119244867296L;
  
  /**
   * Lists the concepts that were removed during disambiguation.
   */
  public List<ResultConcept> removedConcepts = new ArrayList<ResultConcept>();
  
  /**
   * Lists, for each concept ID, the evaluations performed for the concept with that ID. 
   */
  public Map<Integer, List<EvaluationResult>> conceptID2EvaluationResult = new HashMap<Integer, List<EvaluationResult>>();
  
  /**
   * Data class describing a single evaluation performed for disambiguation.
   * 
   * @author martijn
   *
   */
  public static class EvaluationResult implements Serializable {
    private static final long serialVersionUID = 1956311580937017106L;
    
    /**
     * The unique identifier of the rule. Use the DisambiguatorRuleRegistry to retrieve information about a rule.
     */
    public int ruleID;
    
    /**
     * The result of the evaluation.
     */
    public boolean result;
    
    /**
     * A list of extra information about the evaluation. Can be null.
     */
    public List<ExtraData> extraDatas;

    public EvaluationResult(int ruleID, boolean result){
      this.ruleID = ruleID;
      this.result = result;
    }
    
    public EvaluationResult(int ruleID, boolean result, List<ExtraData> extraDatas){
      this.ruleID = ruleID;
      this.result = result;
      this.extraDatas = extraDatas; 
    }
    
    /**
     * Data class describing the extradata.
     * 
     * @author martijn
     *
     */
    public static class ExtraData implements Serializable {
      private static final long serialVersionUID = -8443078288006090591L;
      public static final int TERM_ID = 0;
      public static final int KEYWORD = 1;
      public static final int OTHER_CONCEPT = 2;
      public static String[] typeStrings = new String[]{"TermID", "Keyword","OtherConcept"};
      /**
       * Specifies the type of extra data. Use typeStrings to convert to a string.
       */
      public int type;
      
      /**
       * Value of the extra data.
       */
      public String value;
      
      public ExtraData(int type, String value){
        this.type = type;
        this.value = value;
      }
    }
  }
  
  /**
   * Combines the information of two disambiguationDetails objects.
   * @param disambiguationDetails
   */
  public void add(DisambiguationDetails disambiguationDetails){
    this.removedConcepts.addAll(disambiguationDetails.removedConcepts);
    this.conceptID2EvaluationResult.putAll(disambiguationDetails.conceptID2EvaluationResult);
  }
}
