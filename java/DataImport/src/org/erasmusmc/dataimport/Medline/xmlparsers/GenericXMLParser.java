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
 *     @author Ariel Schwartz
 *     @author Gaurav Bhalotia
 *     
 *     This is the Generic XML Parser, you need to extend this class to parse a 
 *     specific file conforming to a given DTD.
 *
 */

package org.erasmusmc.dataimport.Medline.xmlparsers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.erasmusmc.dataimport.Medline.util.BioTextDBConnection;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The generic parser which extends the default handler provided by the SAX
 * library. This is an event based parsing, that does not require the
 * construction of a DOM tree in memory. Thus it is good for parsing large XML
 * files.
 */
public class GenericXMLParser extends DefaultHandler {

  /** Constants used for JAXP 1.2 */
  static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  // static final String JAXP_SCHEMA_LANGUAGE =
  // "http://localhost/xml/jaxp/properties/schemaLanguage";

  static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
  // static final String W3C_XML_SCHEMA = "http://localhost/2001/XMLSchema";

  static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  // static final String JAXP_SCHEMA_SOURCE =
  // "http://localhost/xml/jaxp/properties/schemaSource";

  static protected Connection dbConnection;

  static XMLReader xmlReader;

  static Stack childHandlers = new Stack();

  protected String currentElement;

  protected String xmlFileName = null;

  static String filename = null;

  static FileOutputStream outfile = null;

  /* If the parser needs to be validated */
  static boolean parseValidate = false;

  /* If the output needs to go to a intermediate file */
  static boolean toFile = false;

  /**
   * The default constructor. Initializes the data base connection
   * 
   * @throws ClassNotFoundException
   *           If the database driver class is not found
   * @throws SQLException
   *           If there is a problem in connection to the database
   */
  public GenericXMLParser() {

    xmlFileName = new File(filename).getName();

    try {
      dbConnection = (new BioTextDBConnection()).getConnection();
    } catch (Exception ex) {
      System.err.println(ex.getMessage());
      System.exit(-1);
    }
    addChildHandler(this);
  }

  /**
   * Convert from a filename to a file URL.
   */
  protected static String convertToFileURL(String filename) throws Exception {

    File file = new File(filename);
    String path = file.toURI().toString();
    return path;
  }

  /**
   * Prints the correct usage for the code
   */
  protected static void usage() {

    System.err.println("Usage: [-options] <file.xml>");
    System.err.println("\t-dtd = DTD validation");
    System.err.println("\t-validate = Parse validation (Checks that all tags are being handled)");
    System.err.println("\t-file = Output to an intermediate file (Will be file.xml-insert.sql)");
    System.err.println("\t-host = database hostname");
    System.err.println("\t-dbname = database database name");
    System.err.println("\t-user = database username");
    System.err.println("\t-psswd = database password");
    System.err.println("\t-xsd | -xsdss <file.xsd> = W3C XML Schema validation using xsi: hints");
    System.err.println("\t\tin instance document or schema source <file.xsd>");
    System.err.println("\t-xsdss <file> = W3C XML Schema validation using schema source <file>");
    System.err.println("\t-usage or -help = this message");
    System.exit(1);
  }

  static public void main(String[] args, Class parserClass) throws Exception {

    boolean dtdValidate = false;
    boolean xsdValidate = false;
    String schemaSource = null;

    /* Parse arguments to get the supplied options */
    for (int i = 0; i < args.length; i++) {

      if (args[i].equals("-dtd")) {
        dtdValidate = true;
      }
      else if (args[i].equals("-xsd")) {
        xsdValidate = true;
      }
      else if (args[i].equals("-validate")) {
        parseValidate = true;
      }
      else if (args[i].equals("-file")) {
        toFile = true;
      }
      else if (args[i].equals("-xsdss")) {
        if (i == args.length - 1) {
          usage();
        }
        xsdValidate = true;
        schemaSource = args[++i];
      }
      else if (args[i].equals("-usage")) {
        usage();
      }
      else if (args[i].equals("-help")) {
        usage();
      }
      else {
        filename = args[i];

        /* Must be last arg */
        if (i != args.length - 1) {
          usage();
        }
      }
    }
    if (filename == null) {
      usage();
    }
    else {
      if (toFile == true) {
        /* If intermediate file chosen then open it to write */
        outfile = new FileOutputStream(filename + "-insert.sql");
      }
    }

    /* Create a JAXP SAXParserFactory and configure it */
    SAXParserFactory spf = SAXParserFactory.newInstance();

    /*
     * Set namespaceAware to true to get a parser that corresponds to the
     * default SAX2 namespace feature setting. This is necessary because the
     * default value from JAXP 1.0 was defined to be false.
     */
    spf.setNamespaceAware(false);

    /* Validation part 1: set whether validation is on */
    spf.setValidating(dtdValidate || xsdValidate);

    /* Create a JAXP SAXParser */
    SAXParser saxParser = spf.newSAXParser();

    /* Validation part 2a: set the schema language if necessary */
    if (xsdValidate) {
      try {
        saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
      } catch (SAXNotRecognizedException x) {
        /* This can happen if the parser does not support JAXP 1.2 */
        System.err.println("Error: JAXP SAXParser property not recognized: " + JAXP_SCHEMA_LANGUAGE);
        System.err.println("Check to see if parser conforms to JAXP 1.2 spec.");
        System.exit(1);
      }
    }

    /*
     * Validation part 2b: Set the schema source, if any. See the JAXP 1.2
     * maintenance update specification for more complex usages of this feature.
     */
    if (schemaSource != null) {
      saxParser.setProperty(JAXP_SCHEMA_SOURCE, new File(schemaSource));
    }

    /* Get the encapsulated SAX XMLReader */
    xmlReader = saxParser.getXMLReader();

    /* Get an instance of the parser for the specific class */
    GenericXMLParser parser = (GenericXMLParser) parserClass.newInstance();

    /* Set the ContentHandler of the XMLReader */
    xmlReader.setContentHandler(parser);

    /* Set an ErrorHandler before parsing */
    xmlReader.setErrorHandler(new MyErrorHandler(System.err));

    System.out.println("Going to parse the File " + filename);

    /* Tell the XMLReader to parse the XML document */
    boolean succes = false;
    while (!succes) {
      try {
        xmlReader.parse(convertToFileURL(filename));
        succes = true;
      } catch (UnknownHostException e){
        System.err.println(e.getMessage());
        System.err.println("Retrying in 30 seconds");
        Thread.sleep(30000);
      } 
    }  

  }

  /**
   * Error handler to report errors and warnings
   */
  private static class MyErrorHandler implements ErrorHandler {
    /* Error handler output goes here */
    private PrintStream out;

    MyErrorHandler(PrintStream out) {
      this.out = out;
    }

    /**
     * Returns a string describing parse exception details
     */
    private String getParseExceptionInfo(SAXParseException spe) {
      String systemId = spe.getSystemId();
      if (systemId == null) {
        systemId = "null";
      }
      String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
      return info;
    }

    /**
     * The following methods are standard SAX ErrorHandler methods. See SAX
     * documentation for more info.
     */
    @Override
	public void warning(SAXParseException spe) throws SAXException {
      out.println("Warning: " + getParseExceptionInfo(spe));
    }

    @Override
	public void error(SAXParseException spe) throws SAXException {
      String message = "Error: " + getParseExceptionInfo(spe);
      throw new SAXException(message);
    }

    @Override
	public void fatalError(SAXParseException spe) throws SAXException {
      String message = "Fatal Error: " + getParseExceptionInfo(spe);
      throw new SAXException(message);
    }
  }

  /**
   * Stores the handler for the current node, it also sets the handler in the
   * XML reader.
   */
  static public void addChildHandler(ContentHandler childHandler) {
    childHandlers.push(childHandler);
    xmlReader.setContentHandler(childHandler);
  }

  /**
   * Removes the current childhandler from the heap and sets the parent handler
   * as the current handler
   */
  static public void removeChildHandler() {
    childHandlers.pop();
    ContentHandler parentHandler = (ContentHandler) childHandlers.peek();
    xmlReader.setContentHandler(parentHandler);
  }

  /**
   * Returns the current database connection
   */
  static public Connection getDbConnection() {
    return dbConnection;
  }
}
