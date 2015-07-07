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

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.utilities.StringUtilities;

public class AddUMLSidAsDatabaseID {
	public static Ontology addUMLSid(Ontology ontology){
		for (Concept concept: ontology){
			if (concept.getID()>-1){
				DatabaseID databaseID = new DatabaseID("UMLS", StringUtilities.formatNumber("C0000000", concept.getID()));
				ontology.setDatabaseIDForConcept(concept.getID(), databaseID);
			}
		}
		return ontology;
	}
}
