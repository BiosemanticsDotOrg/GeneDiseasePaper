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

package org.erasmusmc.rmi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class RMIProperties {
    public static Properties getProperties() {
        Properties defaultProps = new Properties();
        try {
            FileInputStream in = new FileInputStream(System.getProperty("user.dir") + "/" + "globalrmi.properties");
            defaultProps.load(in);
            in.close();
        }
        catch (Exception e) {
            // Default properties not found in current dir
            // Eclipse Fix:
            try {
                FileInputStream in = new FileInputStream(System.getProperty("user.dir") + "/bin/" + "globalrmi.properties");
                defaultProps.load(in);
                in.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return defaultProps;
    }

}
