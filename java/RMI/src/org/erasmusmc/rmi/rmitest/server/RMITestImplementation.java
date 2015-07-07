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

package org.erasmusmc.rmi.rmitest.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class RMITestImplementation extends UnicastRemoteObject implements RMITestInterface {
    private static final long serialVersionUID = 9156105901381400487L;
    private String string = "testing ";
    private ArrayList<String> list = new ArrayList<String>();

    public RMITestImplementation() throws RemoteException {
        this.list.add(string+1);
        this.list.add(string+2);
        this.list.add(string+3);
    }
    public ArrayList getList() throws RemoteException {
        return list;
    }

    public String testConnection() throws RemoteException {
        return "Het werkt !!";
    }

}
