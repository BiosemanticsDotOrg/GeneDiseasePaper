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

public class UTF8Parser {
	public static String parse(String utf8){
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < utf8.length(); i++){
			char ch = utf8.charAt(i);
			switch ((int)ch) {
			case 0x391 : sb.append("Alpha"); break;
			case 0x392 : sb.append("Beta"); break;
			case 0x393 : sb.append("Gamma"); break;
			case 0x394 : sb.append("Delta"); break;
			case 0x395 : sb.append("Epsilon"); break;
			case 0x396 : sb.append("Zeta"); break;
			case 0x397 : sb.append("Eta"); break;
			case 0x398 : sb.append("Theta"); break;
			case 0x399 : sb.append("Iota"); break;
			case 0x39A : sb.append("Kappa"); break;
			case 0x39B : sb.append("Lambda"); break;
			case 0x39C : sb.append("Mu"); break;
			case 0x39D : sb.append("Nu"); break;
			case 0x39E : sb.append("Xi"); break;
			case 0x39F : sb.append("Omicron"); break;
			case 0x3A0 : sb.append("Pi"); break;
			case 0x3A1 : sb.append("Rho"); break;
			case 0x3A3 : sb.append("Sigma"); break;
			case 0x3A4 : sb.append("Tau"); break;
			case 0x3A5 : sb.append("Upsilon"); break;
			case 0x3A6 : sb.append("Phi"); break;
			case 0x3A7 : sb.append("Chi"); break;
			case 0x3A8 : sb.append("Psi"); break;
			case 0x3A9 : sb.append("Omega"); break;
			case 0x3B1 : sb.append("alpha"); break;
			case 0x3B2 : sb.append("beta"); break;
			case 0x3B3 : sb.append("gamma"); break;
			case 0x3B4 : sb.append("delta"); break;
			case 0x3B5 : sb.append("epsilon"); break;
			case 0x3B6 : sb.append("zeta"); break;
			case 0x3B7 : sb.append("eta"); break;
			case 0x3B8 : sb.append("theta"); break;
			case 0x3B9 : sb.append("iota"); break;
			case 0x3BA : sb.append("kappa"); break;
			case 0x3BB : sb.append("lambda"); break;
			case 0x3BC : sb.append("mu"); break;
			case 0x3BD : sb.append("nu"); break;
			case 0x3BE : sb.append("xi"); break;
			case 0x3BF : sb.append("omicron"); break;
			case 0x3C0 : sb.append("pi"); break;
			case 0x3C1 : sb.append("rho"); break;
			case 0x3C2 : sb.append("sigma"); break;
			case 0x3C3 : sb.append("sigma"); break;
			case 0x3C4 : sb.append("tau"); break;
			case 0x3C5 : sb.append("upsilon"); break;
			case 0x3C6 : sb.append("phi"); break;
			case 0x3C7 : sb.append("chi"); break;
			case 0x3C8 : sb.append("psi"); break;
			case 0x3C9 : sb.append("omega"); break;
			case 0x3D0 : sb.append("Gamma"); break;
			case 0x3D1 : sb.append("Theta"); break;
			case 0x3D5 : sb.append("Phi"); break;
			case 0x3D6 : sb.append("Pi"); break;
			case 0x3D7 : sb.append("Kai"); break;
			case 0x3F0 : sb.append("Kappa"); break;
			case 0x3F1 : sb.append("Rho"); break;
			default: sb.append(ch); break;
			}
		}
		return sb.toString();
	}
}