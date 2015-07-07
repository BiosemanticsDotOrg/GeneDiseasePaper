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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LinuxUtilities {
	
	/**
	 * Runs a command in a shell and waits for it to finish. Output is send to the console.
	 * @param command
	 */
	public static void runExternalCMD(String command){
		try {
			Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			// read the output from the command
			String s;
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) 
				System.out.println(s);
		
			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) 
				System.out.println(s);
		}
		catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}

	}
	
	@SuppressWarnings("unused")
	public static void runExternalCMDNoOutput(String command){
		try {
			Process p = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command});

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s;
			while ((s = stdInput.readLine()) != null) 
				s = null;
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		}
		catch (IOException e) {
			System.out.println("exception happened - here's what I know: ");
			e.printStackTrace();
			System.exit(-1);
		}

	}
}
