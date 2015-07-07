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

import java.util.List;

import org.erasmusmc.peregrine.ResultConcept;

public class RMIPeregrineTest {
  public static void main(String[] args) throws Exception {
    //RMIPeregrine peregrine = new RMIPeregrine("kristina.erasmusmc.nl", 111, "RMIPeregrineServerService");
    RMIPeregrine peregrine = new RMIPeregrine("mojojojo.biosemantics.org", 1011, "RMIPeregrineServerService");
    peregrine.setDisambiguate(false);
    
    //peregrine.setDisambiguate(true);
    for(int i=0; i<10000;i++) {
      peregrine.index("PURPOSE: The aim of this study was to evaluate the feasibility and safety of salvage high-d" +
            "ose-rate (HDR) brachytherapy for locally recurrent prostate cancer after external beam radiotherapy (EBRT). " +
            "METHODS AND MATERIALS: We retrospectively analyzed 21 consecutively accrued patients undergoing salvage HDR " +
            "brachytherapy for locally recurrent prostate cancer after EBRT between November 1998 and December 2005. After" +
            " pathologic confirmation of locally recurrent disease, all patients were treated with 36 Gy in six fractions" +
            " using two transrectal ultrasound-guided HDR prostate implants, separated by 1 week. Eleven patients received " +
            "neoadjuvant hormonal therapy immediately presalvage, whereas none received adjuvant hormonal therapy postsalvage." +
            " Median follow-up time from recurrence was 18.7 months (range, 6-84 months). Determination of subsequent biochemical " +
            "failure after brachytherapy was based on the definition by the American Society for Therapeutic Radiology and Oncology." +
            " RESULTS: Based on the Common Terminology Criteria for Adverse Events (CTCAE version 3), 18 patients reported Grade 1 to 2" +
            " genitourinary symptoms by 3 months postsalvage. Three patients developed Grade 3 genitourinary toxicity. Maximum observed" +
            " gastrointestinal toxicity was Grade 2; all cases spontaneously resolved. The 2-year Kaplan-Meier estimate of biochemical" +
            " control after recurrence was 89%. Thirteen patients have achieved a PSA nadir </=0.1 ng/ml, but at the time of writing this" +
            " endpoint has not yet been reached for all patients. All patients are alive; however 2 have experienced biochemical failure, " +
            "both with PSA nadirs >/=1, and have subsequently been found to have distant metastases. CONCLUSIONS: Salvage HDR prostate " +
            "brachytherapy appears to be feasible and effective.");
      
      List<ResultConcept>  resultConcepts = peregrine.resultConcepts;
      if (i % 100 == 0){
        for(ResultConcept result: resultConcepts) {
          System.out.println(result.conceptId);
        }
        System.out.println("***"+i);
        /*for(ResultTerm result: resultTerms) {
          System.out.println(result.term.termId);
          for(String str :tokenizer.tokens) {
            System.out.println(str);
          }
        }*/
      }
    }
    
    
    
  }
}
