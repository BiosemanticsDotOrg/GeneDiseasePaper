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

package org.erasmusmc.csv2sql;


public class TestCode {

	//public static String folder = "C:/home/temp/CMForEUADR/OMOP1/Phase3/EUADR/CM/results/";
	public static String folder = "C:/home/schuemie/Research/OMOP experiment 2/";
	public static void main(String[] args) {
		//CSV2SQL.main(new String[]{folder+"OMOP2011_condition_prev_count.csv","-db","mdrr"});
		//CSV2SQL.main(new String[]{folder+"OMOP2011_drug_prev_count.csv","-db","mdrr"});
		//CSV2SQL.main(new String[]{folder+"OMOP2011_prev_count.csv","-db","mdrr"});
		CSV2SQL.main(new String[]{folder+"testcase_ref_full.csv","-db","mdrr"});
	}

}
