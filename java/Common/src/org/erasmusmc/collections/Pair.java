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

package org.erasmusmc.collections;

public class Pair<A, B> {
	public A object1;
	public B object2;
	public Pair(A object1, B object2){
		this.object1 = object1;
		this.object2 = object2;
	}

	public String toString(){
		return "[[" + object1.toString() + "],[" + object2.toString() + "]]";
	}

	public int hashCode(){
		return object1.hashCode() + object2.hashCode();
	}

	@SuppressWarnings("rawtypes")
	public boolean equals(Object other){
		if (other instanceof Pair)
			if (((Pair)other).object1.equals(object1))
				if (((Pair)other).object2.equals(object2))
					return true;
		return false;  		 
	}
}
