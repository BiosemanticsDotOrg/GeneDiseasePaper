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

package org.erasmusmc.csv2sql;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.erasmusmc.collections.Pair;
import org.erasmusmc.utilities.ReadCSVFile;
import org.erasmusmc.utilities.StringUtilities;

public class CSV2SQL {
	private Parameters parameters;
	private Connection connection;
	private static String INT = "INT";
	private static String VARCHAR = "VARCHAR";
	private static String DATE = "DATE";
	private static String DOUBLE = "DOUBLE";

	public static void main(String[] args) {
		new CSV2SQL(args);
	}

	public CSV2SQL(String[] args) {
		parameters = new Parameters(args);
		connection = connectToMySQL();
		dropTable();
		createTable();
		loadTable();
		close(connection);
	}
	
	private void dropTable() {
		StringBuilder sql = new StringBuilder();
		sql.append("DROP TABLE IF EXISTS ");
		sql.append(parameters.table);
	
		try {
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			statement.execute("USE " + parameters.database);
			statement.execute(sql.toString());
		} catch (SQLException e) {
			System.err.println("Error in SQL statement: " + sql.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private void loadTable() {
		Iterator<List<String>> iterator = new ReadCSVFile(parameters.sourceFile).iterator();
		List<String> header = iterator.next();
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ");
		sql.append(parameters.table);
		sql.append("(");
		sql.append(StringUtilities.join(header, ","));
		sql.append(") VALUES ");
		boolean first = true;
		while (iterator.hasNext()){
			List<String> row = iterator.next();
			if (first)
				first = false;
			else
				sql.append(',');
			sql.append("('");
			sql.append(StringUtilities.join(escape(row), "','"));	
			sql.append("')");
		}
		try {
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			statement.execute("USE " + parameters.database);
			statement.execute(sql.toString());
		} catch (SQLException e) {
			System.err.println("Error in SQL statement: " + sql.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private List<String> escape(List<String> row) {
		for (int i = 0; i < row.size(); i++)
			row.set(i, row.get(i).replace("'", "\\'"));
		return row;
	}

	private void close(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createTable() {
		List<Pair<String, String>> variablesAndtypes = getVariablesAndTypes();
		
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ");
		sql.append(parameters.table);
		sql.append("(");
		boolean first = true;
		for (Pair<String, String> variablesAndtype : variablesAndtypes){
			if (first)
				first = false;
			else
				sql.append(",");
			sql.append(variablesAndtype.object1);
			sql.append('\t');
			sql.append(variablesAndtype.object2);
		}
		sql.append(")");
		
		try {
			Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
			statement.execute("USE " + parameters.database);
			statement.execute(sql.toString());
		} catch (SQLException e) {
			System.err.println("Error in SQL statement: " + sql.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}



	private List<Pair<String, String>> getVariablesAndTypes() {
		Iterator<List<String>> iterator = new ReadCSVFile(parameters.sourceFile).iterator();
		List<String> header = iterator.next();
		List<VarType> types = new ArrayList<VarType>(header.size());
		for (int i = 0; i < header.size();  i++)
			types.add(new VarType());
		while (iterator.hasNext()){
			List<String> row = iterator.next();
			for (int i = 0; i < header.size();  i++){
				VarType varType = types.get(i);
				String value = row.get(i);
				if (isDate(value)){
					//Do nothing
				} else if (isInteger(value)) {
					if (varType.type == DATE)
					  varType.type = INT;
				} else if (isNumber(value)) {
					if (varType.type == DATE || varType.type == INT)
					varType.type = DOUBLE;
				} else {
					varType.type = VARCHAR;
				}
				if (value.length() > varType.maxChars)
					varType.maxChars = value.length();
			}
		}
		List<Pair<String,String>> variablesAndTypes = new ArrayList<Pair<String,String>>();
		for (int i = 0; i < header.size();  i++){
			VarType varType = types.get(i);
			String typeString = varType.type;
			if (varType.type.equals(VARCHAR) || varType.type.equals(INT)){
				typeString = typeString + "(" + varType.maxChars + ")";
			}
			variablesAndTypes.add(new Pair<String,String>(header.get(i),typeString));
		}
		return variablesAndTypes;
	}

	private boolean isNumber(String string) {
		return StringUtilities.isNumber(string);
	}

	private boolean isInteger(String string) {
		return StringUtilities.isInteger(string);
	}

	private boolean isDate(String string) {
		// TODO Auto-generated method stub
		return false;
	}

	private static class VarType {
		public int maxChars = 0;
		public String type = DATE;
	}

	private static class Parameters {
		public String sourceFile;
		public String database;
		public String table;
		public String server = "127.0.0.1";
		public String user = "root";
		public String password = "21**";

		public Parameters(String[] args){
			sourceFile = args[0];
			String label = "";
			for (String arg : args){
				arg = arg.trim();
				if (label.equals("-db"))
					database = arg;

				label = arg;
			}
			if (table == null){ //Use filename as table name
				table = new File(sourceFile).getName();
				table = table.substring(0,table.indexOf('.'));
			}
		}
	}
	
	private Connection connectToMySQL() {
		//Step one: load the JDBC driver classes:
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			throw new RuntimeException("Cannot find JDBC driver. Make sure the file mysql-connector-java-x.x.xx-bin.jar is in the path");
		}

		//Step two: connect to the database server:
		String url = "jdbc:mysql://"+parameters.server+":3306/?useCursorFetch=true";

		try {
			return DriverManager.getConnection(url, parameters.user, parameters.password);
		} catch (SQLException e1) {
			throw new RuntimeException("Cannot connect to DB server: " + e1.getMessage());
		}
	}

}
