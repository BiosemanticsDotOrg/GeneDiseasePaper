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
 *      
 */

package org.erasmusmc.dataimport.Medline.xmlparsers.medline;
import java.sql.Types;

import org.erasmusmc.dataimport.Medline.xmlparsers.NodeHandler;
 
public class DataBank extends NodeHandler{


    static final String[] columnNameDef = {
        "pmid",
        "data_bank_name",
        "accession_number"
    };

    static final String[] xmlElementNameDef = {
        "PMID",
        "DataBankName",
        "AccessionNumber"
    };

    static final int[] columnTypeDef = {
        Types.INTEGER,
        Types.VARCHAR,
        Types.VARCHAR
    };

    public DataBank(String pmid, String dataBankName) throws Exception{
	
        tableName = "medline_data_bank";

        columnName = columnNameDef;
        xmlElementName = xmlElementNameDef;
        columnType = columnTypeDef;

        initialize();

        /* Add any data that does not come through XML to the hashtable */
        putColumnValue("PMID", pmid);
        putColumnValue("DataBankName", dataBankName);
    }

}
