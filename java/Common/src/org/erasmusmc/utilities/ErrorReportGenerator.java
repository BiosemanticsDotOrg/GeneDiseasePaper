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

package org.erasmusmc.utilities;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Class for generating detailed error reports when an exception occurs. 
 * @author schuemie
 *
 */
public class ErrorReportGenerator {
	
	/**
	 * Creates a report and writes it to the working folder.
	 * @param folder Current working folder (can be empty)
	 * @param e The exception
	 * @return The filename of the error report 
	 */
  public static String createReport(String folder, String additionalInfo, Exception e){
  	if (folder == null || folder.length() == 0)
  		folder = System.getProperty("user.dir");
  	folder = folder + "/";
  	String filename = folder + "Error.txt";
		int i = 1;
		while (new File(filename).exists())
			filename = folder + "Error"+(i++)+".txt";
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setGroupingSeparator(',');
		df.setDecimalFormatSymbols(dfs);
		
		WriteTextFile out = new WriteTextFile(filename);
		out.writeln("*** Generic error information ***");
		out.writeln("Message: " + e.getMessage());
		out.writeln("Time: " + StringUtilities.now());
		Runtime runTime = Runtime.getRuntime();
		out.writeln("Processor type: " + System.getProperty("sun.cpu.isalist"));
		out.writeln("Available processors: " + runTime.availableProcessors());
		out.writeln("Maximum available memory: " + df.format(runTime.maxMemory())+" bytes");
		out.writeln("Used memory: " + df.format(runTime.totalMemory()-runTime.freeMemory())+" bytes");
		out.writeln("Java version: " + System.getProperty("java.version"));
		out.writeln("Java vendor: " + System.getProperty("java.vendor"));
		out.writeln("OS architecture: " + System.getProperty("os.arch"));
		out.writeln("OS name: " + System.getProperty("os.name"));
		out.writeln("OS version: " + System.getProperty("os.version"));
		out.writeln("OS patch level: " + System.getProperty("sun.os.patch.level"));		
		out.writeln("");
		out.writeln("*** Stack trace ***");
		for (StackTraceElement element : e.getStackTrace())
			out.writeln(element.toString());
		out.writeln("");
		out.writeln("*** Working folder contents ***");
		out.writeln("Directory of " + new File(folder).getAbsolutePath());
		out.writeln("");
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		File[] files = new File(folder).listFiles();
		Arrays.sort(files, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return Long.valueOf(o1.lastModified()).compareTo(o2.lastModified());
			}
		});
		
		for (File file : files){
			String name = file.getName();
			String length = df.format(file.length());
			String dir = file.isDirectory()?"<DIR>":"     ";
			String modifiedDate = sdf.format(new Date(file.lastModified()));
			StringBuilder filler = new StringBuilder();
			for (int x = 0; x < (80-name.length()-length.length()); x++)
					filler.append(' ');
			out.writeln(name + filler.toString() + length + "      " + dir + "        " + modifiedDate);
		}
		out.writeln("");
		out.writeln("Available disc space: " + df.format(new File(folder).getFreeSpace()) +" bytes");
		out.writeln("");
		out.writeln("*** Extra information ***");
		out.writeln(additionalInfo);
		out.close();
		return filename;
	}
}
