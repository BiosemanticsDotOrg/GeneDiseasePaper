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
 *      @author Gaurav Bhalotia
 *      @author Ariel Schwartz
 *      
 *      This file handles the parsing of author node and population of the relational database
 */

package org.erasmusmc.dataimport.Medline.xmlparsers.medline;


import java.sql.Types;

import org.erasmusmc.dataimport.Medline.xmlparsers.NodeHandler;

public class Author extends NodeHandler{
  protected String pmid = null;


  static final String[] columnNameDef = {
    "author_order",
    "pmid",
    "last_name",
    "fore_name",
    "first_name",
    "middle_name",
    "initials",
    "suffix",
    "affiliation",
    "collective_name",
    "dates_associated_with_name",
    "name_qualifier",
    "other_information",
    "title_associated_with_name",
    "author_valid_yn"
  };

  static final String[] xmlElementNameDef = {
    "author_order",
    "PMID",
    "Author.LastName",
    "Author.ForeName",
    "Author.FirstName",
    "Author.MiddleName",
    "Author.Initials",
    "Author.Suffix",
    "Author.Affiliation",
    "Author.CollectiveName",
    "Author.DatesAssociatedWithName",
    "Author.NameQualifier",
    "Author.OtherInformation",
    "Author.TitleAssociatedWithName",
    "Author.ValidYN"
  };

  static final int[] columnTypeDef = {
    Types.SMALLINT,
    Types.INTEGER,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.VARCHAR,
    Types.CHAR
  };

  public Author(String pmid, int authorPlace) throws Exception{
    tableName = "medline_author";
    xmlNodeName = "Author";

    columnName = columnNameDef;
    xmlElementName = xmlElementNameDef;
    columnType = columnTypeDef;

    initialize();

    /* Add any data that does not come through XML to the hashtable */
    putColumnValue("PMID", pmid);
    putColumnValue("author_order", Integer.toString(authorPlace));
    this.pmid = pmid;

  }

}
