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

/**
 *      @author Ariel Schwartz
 *      @author Gaurav Bhalotia
 *      
 */

package org.erasmusmc.dataimport.Medline.xmlparsers.medline;
import java.sql.Types;

import org.erasmusmc.dataimport.Medline.xmlparsers.NodeHandler;

public class CommentsCorrections extends NodeHandler{

	String pmid;

	public CommentsCorrections(String pmid, String type) throws Exception{
		tableName = "medline_comments_corrections";
		xmlNodeName = "CommentsCorrections";

		String[] columnNameDef = {
				"pmid", 
				"ref_pmid",
				"note",
				"type",
				"ref_source"

		};

		columnName = columnNameDef;

		String[] xmlElementNameDef = {
				"PPMID",
				"CommentsCorrections.PMID",          /* The parent element in the stack is a variable*/
				"CommentsCorrections.Note",
				"CommentsCorrections.RefType",
				"CommentsCorrections.RefSource"
		};

		xmlElementName = xmlElementNameDef;

		int columnTypeDef[] = {
				Types.INTEGER,
				Types.INTEGER,
				Types.VARCHAR,
				Types.VARCHAR,
				Types.VARCHAR
		};

		columnType = columnTypeDef;
		initialize();
		/* Add any data that does not come through XML to the hashtable */
		putColumnValue("PPMID", pmid);
		// putColumnValue("Type", type);
	}

}
