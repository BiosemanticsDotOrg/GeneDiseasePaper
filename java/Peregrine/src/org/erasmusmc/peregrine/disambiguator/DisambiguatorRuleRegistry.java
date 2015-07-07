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

import java.util.HashMap;
import java.util.Map;

/**
 * Class with statis methods for retrieving information on disambiguation rules
 * 
 * @author martijn
 *
 */
public class DisambiguatorRuleRegistry {
  private static Map<Integer, String> ruleID2Class = registerRules();

  private static Map<Integer, String> registerRules() {
    Map<Integer, String> map = new HashMap<Integer, String>();
    map.put(ApplyGeneDisambiguatorRule.ruleID, ApplyGeneDisambiguatorRule.class.getSimpleName()); //0
    map.put(IsHomonymRule.ruleID, IsHomonymRule.class.getSimpleName()); //1
    map.put(HasSynonymRule.ruleID, HasSynonymRule.class.getSimpleName()); //2
    map.put(HasKeywordsRule.ruleID, HasKeywordsRule.class.getSimpleName()); //3
    map.put(IsPreferredTermRule.ruleID, IsPreferredTermRule.class.getSimpleName()); //4
    map.put(IsComplexRule.ruleID, IsComplexRule.class.getSimpleName()); //5
    map.put(ApplyUMLSDisambiguatorRule.ruleID, ApplyUMLSDisambiguatorRule.class.getSimpleName()); //6
    map.put(OtherDisambiguatorRule.ruleID, OtherDisambiguatorRule.class.getSimpleName()); //7
    map.put(TooManyConceptsRule.ruleID, TooManyConceptsRule.class.getSimpleName()); //8
    return map;
  }
  
  /**
   * Returns the name of a rule.
   * 
   * @param ruleID
   * @return
   *    Name of the rule.
   */
  public static String getRuleName(int ruleID) {
    return ruleID2Class.get(ruleID);
  }
  
}
