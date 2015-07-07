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

/*
 * @author Ariel Schwartz
 * @author Gaurav Bhalotia
 */

package org.erasmusmc.dataimport.Medline.xmlparsers.medline;

import org.erasmusmc.dataimport.Medline.xmlparsers.GenericXMLParser;
import org.erasmusmc.dataimport.Medline.xmlparsers.NodeHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * This class the parses a medline document, extends the geenric xml parser
 */
public class MedlineParser extends GenericXMLParser {

  /* For patching errors */
  public static int eCount = 0;

  /* For printing the current status */
  private int numCitations = 0;

  public MedlineParser() {
    super();
  }

  static public void main(String[] args) throws Exception {
    GenericXMLParser.main(args, MedlineParser.class);
  }

  /* Parser calls this for each element in a document */
  @Override
public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    MedlineCitation medlineCitation;
    boolean isNotMedlineCitation = qName.compareTo("MedlineCitation") != 0;
    if (isNotMedlineCitation || currentElement == null) {
      currentElement = qName;
      NodeHandler.pushElement(qName);
    }
    else if (currentElement.equals("MedlineCitationSet")) {
      try {
        medlineCitation = new MedlineCitation(xmlFileName);
        numCitations++;
        if ((numCitations % 500) == 0) {
          System.out.println("Parsed " + numCitations + " Citations");
        }
        medlineCitation.setContentHandler(medlineCitation, namespaceURI, localName, qName, atts);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
