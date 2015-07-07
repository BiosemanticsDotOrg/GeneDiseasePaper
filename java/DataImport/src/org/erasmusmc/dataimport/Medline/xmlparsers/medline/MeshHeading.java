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
 */

package org.erasmusmc.dataimport.Medline.xmlparsers.medline;
import java.sql.Types;

import org.erasmusmc.dataimport.Medline.xmlparsers.NodeHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
 
public class MeshHeading extends NodeHandler{


    protected String pmid = null;

    static final String[] columnNameDef = {
        "pmid",
        "descriptor_name",
        "descriptor_name_major_yn"
    };

    static final String[] xmlElementNameDef = {
        "PMID",
        "MeshHeading.DescriptorName",
        "MeshHeading.DescriptorName.MajorTopicYN"
    };

    static final int[] columnTypeDef = {
        Types.INTEGER,
        Types.VARCHAR,
        Types.VARCHAR
    };

    public MeshHeading(String pmid) throws Exception{
	
        tableName = "medline_mesh_heading";

        columnName = columnNameDef;
        xmlElementName = xmlElementNameDef;
        columnType = columnTypeDef;

        initialize();

        /* Add any data that does not come through XML to the hashtable */
        putColumnValue("PMID", pmid);

        this.pmid = pmid;

    }

    @Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

        NodeHandler handler;
        String descriptor = null;

        try{
            if (currentElement != null) {
                if (descriptor == null)
                    descriptor = getColumnValue("MeshHeading.DescriptorName");

                if (qName.equals("QualifierName")){
                    handler = new MeshHeadingQualifier(pmid, descriptor);
                    setContentHandler(handler, namespaceURI, localName, qName, atts);
                }
                else {
                    super.startElement(namespaceURI, localName, qName, atts);
                }
            } else {
                super.startElement(namespaceURI, localName, qName, atts);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new SAXException("Problem creating qualifier for Mesh descriptor");
        }
    }
}
