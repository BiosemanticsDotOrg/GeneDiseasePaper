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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtilities {
  public static void write (Object object, String filename) throws IOException {
    ObjectOutputStream out = null;

    try {
        out = new ObjectOutputStream (new FileOutputStream (filename));

        out.writeObject (object);
        out.flush ();
    } finally {
        if (out != null) {
            try {
                out.close ();
            } catch (IOException exception) {}
        }
    }
}

public static Object read (String filename) throws ClassNotFoundException, IOException {
    ObjectInputStream in = null;

    try {
        in = new ObjectInputStream (new FileInputStream (filename));

        return in.readObject ();
    } finally {
        if (in != null) {
            try {
                in.close ();
            } catch (IOException exception) {}
        }
    }
}

}
