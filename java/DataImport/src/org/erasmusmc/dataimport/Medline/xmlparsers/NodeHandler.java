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
 *
 *      This is a superclass for all the classes that parse the XML from a given node to populate
 *      the tables in the relational database
 *
 *      @author Gaurav Bhalotia
 *      @author Ariel Schwartz
 *
 */

package org.erasmusmc.dataimport.Medline.xmlparsers;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class NodeHandler extends DefaultHandler {

  protected PreparedStatement pstmt = null;
  protected static final Hashtable preparedStatements = new Hashtable();
  protected int numColumns;
  protected String tableName = null;
  protected String xmlNodeName;

  protected boolean updateChildren = true;

  /* Names of the columns to be stored in the database */
  protected String[] columnName;

  /* Corresponding XML element names for each of the columns 
   * If element name is DATE and the type is Types.DATE then it 
   * is composed of Year, Month and Day 
   */
  protected String[] xmlElementName;

  /* Corresponding the type for each of the columns */
  protected int[] columnType;

  /* The hashtable to store the values for each element obtained from the XML file */
  protected Hashtable columnValues = null;

  /* Hash Set to store column names for validating the parser */
  protected HashSet columnNames = null;

  /* The various columns needed for the record                */
  /* To store the current element and value in the parse stream from the GenericXMLParser */
  protected String currentElement = null;
  protected String currentValue = null;
  static protected Stack elements = new Stack();

  /* The ContentHandlers of the children nodes */
  protected ArrayList childrenHandlers = new ArrayList();

  /* The ContentHandlers of the parent nodes, e.g. Journal for Article */
  protected ArrayList parentHandlers = new ArrayList();

  /* The list of Columns Values that need to be returned from the corresponding parent handlers to
   * this node, e.g. Journal returns ISSN to MedlineCitation */
  protected ArrayList parentHandlerReturnColumns = new ArrayList();

  protected String tagPrefix = "";
  protected boolean ignoreDuplicateKeyError = true;

  protected static final int DB2_DUPLICATE_ERROR = -803;
  protected static final int MySQL_DUPLICATE_ERROR = 1062;

  /** The constructor takes in the SAX event handler. Which is used to
   * parse the elements in the authorlist subnode.
   *
   */
  public void initialize() throws Exception {

    /* Initialize the number of columns */
    numColumns = columnName.length;

    /* Check if the statement is null then compile a statement */
    if (pstmt == null) {
      /* Now prepare the statement to be used for updating DB */
      compileStatement();
    }

    /* Initialize the hash table */
    columnValues = new Hashtable();

    /* If parse validate option is on initialize the set object to hold the columnNames */
    columnNames = new HashSet();
  }

  /** Compile a statement, later on while updating the DB we just need
   * to supply the arguments*/
  private void compileStatement() throws Exception {

    try {
      pstmt = (PreparedStatement) preparedStatements.get(tableName);
      if (pstmt != null)
        return;
      Connection con = GenericXMLParser.dbConnection;
      /* Create the parameter string */
      if (numColumns < 1) {
        throw new Exception("This table does not have any columns");
      }
      else {
        String pString = "(";
        String columnNameString = "";
        for (int i = 0; i < numColumns - 1; i++) {
          pString += "?,";
          columnNameString += (columnName[i] + ",");
        }
        pString += "?)";
        columnNameString += columnName[numColumns - 1];
        pstmt = con.prepareStatement("REPLACE INTO " + tableName + " (" + columnNameString + ") VALUES " + pString);
        preparedStatements.put(tableName, pstmt);
      }
    } catch (SQLException e) {
      System.out.println("---" + e.getMessage());
      throw new Exception("Problems with the connection to the database");
    }
  }

  /** Function to handle the event where an element begins corresponding to the author stream
   * 
   * @param namespaceURI  The namespace information for this element 
   * @param localName     The actual name of the element 
   * @param qName         Combination of the Namespace alias and the localName
   * @param atts          Any attributes for the element
   */
  @Override
public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

    /* Assuming well formed XML, previous element ends before a new one starts */
    currentElement = qName;
    elements.push(qName);
    tagPrefix += qName + ".";

    /* Add an empty string for this element to the hashtable. to be later filled in the character()
     * method
     */
    currentValue = "";

    /* Add the attributes with their values to the hashtable */
    for (int att = 0; att < atts.getLength(); att++) {
      String attName = atts.getQName(att);
      /* Prepend the atribute names by the element name */
      putColumnValue(tagPrefix + attName, atts.getValue(att));
    }

  }

  /** Function to handle the end element event from the GenericXMLParser
   * 
   * @param namespaceURI  
   * @param localName
   * @param qName
   */
  @Override
public void endElement(String namespaceURI, String localName, String qName) throws SAXException {

    /* Assuming here that the XML is well formed and the endElement are in correct order */
    elements.pop();
    if (qName.equals(currentElement)) {

      tagPrefix = tagPrefix.substring(0, tagPrefix.length() - currentElement.length() - 1);

      if (currentValue != null) {

        /* Prefix the TAG with the ancestors (using the stack), e.g. DateCreated.Year */
        /* For now just appending with the parents */
        putColumnValue(tagPrefix + currentElement, currentValue);
      }
      try {
        currentElement = (String) elements.peek();
        currentValue = null;
      } catch (Exception e) {
        throw new SAXException("Empty stack. currentElement: " + currentElement + " qName: " + qName);
      }
      if (tagPrefix.equals("")) {
        /* This just means the end of the Table type, e.g Author */
        GenericXMLParser.removeChildHandler();
      }
    }
    else if (currentElement != null) {
      System.out.println(elements);
      throw new SAXException("misformed XML currentElement: " + currentElement + " qName: " + qName);
    }
  }

  /** Function to handle the characters that have been passed to this object from the main
   * GenericXMLParser; The element these characters belong to has been set by the previous startElement
   * event
   * 
   * @param ch        The character array containing the characters
   * @param start     The position where the characters corresponding to this element start
   * @param length    The length of the character string for the current element
   */
  @Override
public void characters(char[] ch, int start, int length) throws SAXException {

    /* I assume that the XML is well formed, the characters coming now should correspond to the
     * current element
     */
    if (currentElement == null) {
      throw new SAXException("misformed XML");
    }
    else {
      currentValue += new String(ch, start, length);
    }

  }

  /** Function to materialize the elements in this object to the database 
   *  This method has to be implemented by the extending class
   */
  public void updateDB() throws Exception {

    String retColumnName = null;
    String retColumnValue = null;
    String pmid = null;

    /* Execute updates for parent nodes */
    for (int i = 0; i < parentHandlers.size(); i++) {
      retColumnName = (String) parentHandlerReturnColumns.get(i);
      if (retColumnName.toLowerCase().endsWith("pmid"))
        pmid = retColumnValue;
      if (retColumnName.equals("")) {
        /* Do nothing */
      }
      else {
        try {
          /* Get the column value and put in this current node */
          retColumnValue = ((NodeHandler) parentHandlers.get(i)).getColumnValue(retColumnName);
          putColumnValue(retColumnName, retColumnValue);
        } catch (NullPointerException e) {
          System.out.println(retColumnName);
          System.out.println(retColumnValue);
          /* Also print the hashtable to know the context */
          System.out.println(columnValues);
          e.printStackTrace();
          System.exit(1);
        }
      }
      /* Update update for the parent */
      ((NodeHandler) parentHandlers.get(i)).updateDB();
    }

    try {
      Object tempVal;

      for (int i = 0; i < numColumns; i++) {
        try {
          if (columnType[i] == Types.DATE) {
            /* handle this separately */

            String date = getColumnValue(xmlElementName[i] + ".Year");
            if (date == null) {
              pstmt.setNull(i + 1, Types.DATE);
            }
            else {
              date += "-" + getColumnValue(xmlElementName[i] + ".Month") + "-" + getColumnValue(xmlElementName[i] + ".Day");

              /* Now create a date type from this */
              pstmt.setString(i + 1, date); /* DB converts from string to DATE*/
            }

          }
          else {
            tempVal = getColumnValue(xmlElementName[i]);

            if (tempVal == null) {
              /* Set the parameter to be null */
              pstmt.setNull(i + 1, columnType[i]);
            }
            else {
              /* Set the parameter value with appropriate type */
              pstmt.setObject(i + 1, tempVal, columnType[i]);
            }
          }
        } catch (ArrayIndexOutOfBoundsException e) {
          throw new Exception("Problem updating table " + tableName + " i: " + i + " columnType.length: " + columnType.length);
        }
      }

      /* If parse validation is ON check if all the values in the hashtable have been used */
      if (GenericXMLParser.parseValidate == true) {

        if (columnNames.isEmpty()) {
          /* Parse is good, all values are being used */
        }
        else {

          /* There are some values that are not being used */

          /* Print the current hashtable */
          //System.out.println("Some unused values in the hashtable for " + this);
          System.out.println(columnNames + "\n" + columnValues + "\n");

        }

      }
    }

    catch (SQLException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
      System.exit(-1);
      //throw new Exception("Problems with the prepared statement");
    }

    /* Now execute the update with the database */
    try {
      if (GenericXMLParser.toFile == false) {
        //                if(this instanceof Author) {
        //                  Author that = (Author)this;
        //                  Statement delete_stmt = GenericXMLParser.getDbConnection().createStatement();
        //                  delete_stmt.execute("DELETE FROM " + tableName + " where pmid = " + that.getPMID());
        //                }
        pstmt.executeUpdate();
      }
      else {
        /* Write it to a file (Opened already) */
        //GenericXMLParser.outfile.write(pstmt);
        PrintStream out = new PrintStream(GenericXMLParser.outfile);
        out.println(pstmt);
        //System.out.println(pstmt);
      }

    } catch (SQLException e) {
      if (!handleSQLException(e)) {
        System.err.println(pstmt);
        System.err.println(e.getMessage());
        System.err.println(columnValues);
        throw new Exception("Problem in inserting values into the " + tableName + " table");
      }
    }

    /* Execute updates for children nodes */
    if (updateChildren == true) {
      for (int i = 0; i < childrenHandlers.size(); i++) {
        ((NodeHandler) childrenHandlers.get(i)).updateDB();
      }
    }
  }

  /**
   * Handles SQLException. Should be overloaded by inheriting classes to handle special cases
   * @returns true if the exception has been handled, false otherwise 
   */
  protected boolean handleSQLException(SQLException e) {
    if (ignoreDuplicateKeyError && e.getErrorCode() == DB2_DUPLICATE_ERROR) {
      updateChildren = false;
      return true;
      /* Don't do anything, the tuple for this primary key has already been inserted */
    }
    else if (ignoreDuplicateKeyError && e.getErrorCode() == MySQL_DUPLICATE_ERROR) {
      updateChildren = false;
      return true;
      /* Don't do anything, the tuple for this primary key has already been inserted */
    }
    else {
      System.err.println("ERROR CODE == " + e.getErrorCode());
      return false;
    }
  }

  /** Normal Content Handler where the handler being set has to be serialized
   * to the DB after this node
   */
  public void setContentHandler(NodeHandler childHandler, String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    if (this != childHandler) {
      childrenHandlers.add(childHandler);
    }
    GenericXMLParser.addChildHandler(childHandler);
    childHandler.startElement(namespaceURI, localName, qName, atts);
    currentValue = null;
  }

  /** Content handler for a node that needs to be serialized to the DB before this node
   *  
   *  @param returnColumn     Stores the column name that needs to be returned by the parent
   *                          should be "" if no return is desired 
   *       
   */
  public void setContentHandlerParent(String returnColumn, NodeHandler childHandler, String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    if (this != childHandler) {
      parentHandlers.add(childHandler);
      parentHandlerReturnColumns.add(returnColumn);
    }
    GenericXMLParser.addChildHandler(childHandler);
    childHandler.startElement(namespaceURI, localName, qName, atts);
  }

  /* Return the value of a column given its name, removing it from the hashtable */
  protected String getColumnValue(String columnName) {

    if (GenericXMLParser.parseValidate == true) {
      /* remove the name from the hashset, as it has been used */
      columnNames.remove(columnName);
    }

    return (String) columnValues.get(columnName);
  }

  /* Store the value of a column given its name */
  protected void putColumnValue(String columnName, String columnValue) {
    columnValues.put(columnName, columnValue);

    if (GenericXMLParser.parseValidate == true) {
      /* Store the column name in the set */
      columnNames.add(columnName);
    }
  }

  /** Adds an element to the element stack
   *  @param qName the element to be added
   */
  public static void pushElement(Object qName) {
    elements.push(qName);
  }

}
