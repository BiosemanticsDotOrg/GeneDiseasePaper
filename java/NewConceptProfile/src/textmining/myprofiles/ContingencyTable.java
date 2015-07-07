/*
 * Concept profile generation and analysis for Gene-Disease paper
 * Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
 *  Leiden, The Netherlands
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

package textmining.myprofiles;

public class ContingencyTable {

	public static Double JaccardIndex(double M11, double M10, double M01){
		Double ji = M11/(M11+M10+M01);
		return ji;
	}

	public static double PhiCoefficient(double M11, double M10, double M01, double M00){
		double numerator = M11*M00-M10*M01;
		double denum = Math.sqrt(M11+M10)*Math.sqrt(M11+M01)*Math.sqrt(M00+M10)*Math.sqrt(M00+M01);
		double phi = numerator/denum;
		return phi;
	}
	
	public static double MutualInformation(double M11, double M10, double M01, double M00){
		// Information taken from -> http://en.wikipedia.org/wiki/Mutual_information
		
		double N = M11+M10+M01+M00;
		
		double Px = (M11+M01)/N;
		double Pnotx = (M10+M00)/N;
		double Py = (M11+M10)/N;
		double Pnoty = (M01+M00)/N;
		
		double Pxy = M11/N;
		double Pxnoty = M01/N;
		double Pnotxy = M10/N;
		double Pnotxnoty = M00/N;
		
		double miA = Pxy*Math.log(Pxy/(Px*Py));
		if(Double.isNaN(miA))
			miA = 0.0;
		double miB = Pxnoty*Math.log(Pxnoty/(Px*Pnoty));
		if(Double.isNaN(miB))
			miB = 0.0;
		double miC = Pnotxy*Math.log(Pnotxy/(Pnotx*Py));
		if(Double.isNaN(miC))
			miC = 0.0;
		double miD = Pnotxnoty*Math.log(Pnotxnoty/(Pnotx*Pnoty));
		if(Double.isNaN(miD))
			miD = 0.0;
		double mi = miA+miB+miC+miD;
		return mi;
	}
	
	public static double Independence(double M11, double M10, double M01, double M00){
		// Information taken from -> http://en.wikipedia.org/wiki/Mutual_information
		
		double N = M11+M10+M01+M00;
		
		double PAB = Math.log(M11)-Math.log(N);
		double PA = Math.log(M10+M11)-Math.log(N);
		double PB = Math.log(M11+M01)-Math.log(N);
		
		double mi = PAB-PA-PB;
		return mi;
	}
	
	public static double UncertaintyCoefficient(double M11, double M10, double M01, double M00){
		// Information taken from -> http://en.wikipedia.org/wiki/Mutual_information
		
		double N = M11+M10+M01+M00;
		
		double Px = (M11+M01)/N;
		double Pnotx = (M10+M00)/N;
		double Py = (M11+M10)/N;
		double Pnoty = (M01+M00)/N;
		
		double Pxy = M11/N;
		double Pxnoty = M01/N;
		double Pnotxy = M10/N;
		double Pnotxnoty = M00/N;
		
		double Hx = -Px*Math.log(Px)-Pnotx*Math.log(Pnotx);
		double Hy = -Py*Math.log(Py)-Pnoty*Math.log(Pnoty);
		
		double miA = Pxy*Math.log(Pxy/(Px*Py));
		if(Double.isNaN(miA))
			miA = 0.0;
		double miB = Pxnoty*Math.log(Pxnoty/(Px*Pnoty));
		if(Double.isNaN(miB))
			miB = 0.0;
		double miC = Pnotxy*Math.log(Pnotxy/(Pnotx*Py));
		if(Double.isNaN(miC))
			miC = 0.0;
		double miD = Pnotxnoty*Math.log(Pnotxnoty/(Pnotx*Pnoty));
		if(Double.isNaN(miD))
			miD = 0.0;
		double mi = miA+miB+miC+miD;
		
		
		double uc = (2*mi)/(Hx+Hy);
		return uc;
	}
	
	public static double ChiSquare(double M11, double M10, double M01, double M00){
		double cs = 0.0;
		double N = M11+M10+M01+M00;
		
		double x = M11+M01;
		double notx = M10+M00;
		double y = M11+M10;
		double noty = M01+M00;
		
		double A_expected = (x*y)/N;
		double B_expected = (x*noty)/N;
		double C_expected = (notx*y)/N;
		double D_expected = (notx*noty)/N;
		
		cs += ((A_expected-M11)*(A_expected-M11))/A_expected;
		cs += ((B_expected-M01)*(B_expected-M01))/B_expected;
		cs += ((C_expected-M10)*(C_expected-M10))/C_expected;
		cs += ((D_expected-M00)*(D_expected-M00))/D_expected;
		return cs;
	}
}
