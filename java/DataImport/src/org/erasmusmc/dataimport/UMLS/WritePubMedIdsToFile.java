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

package org.erasmusmc.dataimport.UMLS;

import java.util.List;

import org.erasmusmc.medline.FetchPMIDs;
import org.erasmusmc.utilities.WriteTextFile;

public class WritePubMedIdsToFile {

	public static void main(String[] args) {
		List<Integer> pmids = FetchPMIDs.getPMIDs("1980-01-01", "2011-01-01");
		WriteTextFile write = new WriteTextFile("/home/khettne/Public/PMIDs/all_2010.PMIDs");
		for (Integer pmid: pmids){
			write.writeln(pmid.toString());
		}
		write.close();
	}

}
