/*
 * Concept profile generation and analysis for Gene-Disease paper
 * Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
 *  Leiden, The Netherlands
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

package textmining.myprofiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.erasmusmc.utilities.TextFileUtilities;

public class MapHumanGenes2Concepts {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// for this resource see: https://beehub.nl/biosemantics/Groundhogs/Thesauriv16.zip
		HashMap<String,String>  hm = loadThesaurus("/home/hvanhaagen/textmining/Thesauri/UMLS2010ABHomologeneJochemToxV1_6_dblinks_all.txt");
		
		List<String> result = new ArrayList<String>();
		List<String> idmappings = TextFileUtilities.loadFromFile("/home/hvanhaagen/Databases/HPRD/FLAT_FILES_072010/HPRD_ID_MAPPINGS.txt");
		idmappings.remove(0);
		for(String row:idmappings){
			String entrezgeneid = row.split("\t")[4];
			
			String cid = hm.get(entrezgeneid);
			if(cid!=null)
				result.add(cid+"\t"+entrezgeneid);
			
		}
		
		result.add(0,"conceptID\tentrezgeneID");
		TextFileUtilities.saveToFile(result, "/home/hvanhaagen/Desktop/HPRD_genes_v16.txt");
	}

	public static HashMap<String,String>  loadThesaurus(String filename){
		HashMap<String,String> hm = new HashMap<String, String>();
		List<String> in = TextFileUtilities.loadFromFile(filename);
		for(String row:in){
			String fields[] = row.split(",");
			String type = fields[1];
			if(type.equals("EG")){
				hm.put(fields[2], fields[0]);
			}
		}
		return hm;
	}
}

