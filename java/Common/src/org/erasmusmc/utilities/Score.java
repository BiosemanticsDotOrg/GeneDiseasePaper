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

/**
 * Class for measuring the performance of binary classifiers against a binary gold standard.
 * @author schuemie
 *
 */
public class Score {
  public int fp = 0;
  public int tp = 0;
  public int fn = 0;
  public int tn = 0;
  
  public static double alpha = 0.05; //Alpha for the confidence interval calculations
  
  public static void main(String[] args){
  	Score score = new Score();
  	score.tp = 1021;
  	score.tn = 42589;
  	score.fn = 184;
  	score.fp = 126;
  	System.out.println(score.sensitivityWithCI());
  }
  
  public void add(boolean reference, boolean system){
    if (reference){
      if (system)
        tp++;
      else
        fn++;
    } else {
      if (system)
        fp++;
      else
        tn++;
    }
  }
  
  public double specificity(){
    return tn/(double)(fp+tn);
  }
  
  public double precision(){
    return tp/(double)(tp+fp);
  }
  
  public double recall(){
    return tp/(double)(tp+fn);
  }
  
  public double sensitivity(){
    return recall();
  }
  
  public double f(){
    double p = precision();
    double r = recall();
    if (p == 0 && r == 0)
      return 0;
    else
      return (2*p*r) / (p + r);
  }
  public void add(Score score){
    fp += score.fp;
    tp += score.tp;
    fn += score.fn;
    tn += score.tn;
  }
  
  public String toString(){
    return "Precision: " + StringUtilities.formatNumber("0.000", precision()) + 
           "\trecall(sens): " + StringUtilities.formatNumber("0.000", recall()) + 
           "\tspecificity: " + StringUtilities.formatNumber("0.000", specificity()) + 
           "\tf: " + StringUtilities.formatNumber("0.000", f());
  }
  
  public String rawData(){
  	return "TP: " + tp + "\t FP: " + fp + "\t TN: " + tn + "\t FN: " + fn;
  }
  
  public static class MetricWithCI {
  	public double estimate;
  	public double ci95down;
  	public double ci95up;
  	
  	public String toString(){
  		return estimate + " (" + ci95down + " - " + ci95up + ")";
  	}
  }
  
  /* all code below this point is ported from StatPages: http://statpages.org/ctab2x2.html */
  
  private class CommonCalculations {
  	double a;
  	double b; 
  	double c;
  	double d;
  	double r1;
  	double r2;
  	double c1;
  	double c2;
  	double lex_B; 
  	double lex_A; 
  	double lex_D; 
  	double lex_C; 
  	double uex_B; 
  	double uex_A; 
  	double uex_D; 
  	double uex_C; 
  	
  	public CommonCalculations(){
  		a = tp;
    	b = fp;
    	c = fn;
    	d = tn;
    	r1 = a+b;
    	r2 = c+d;
    	c1 = a+c;
    	c2 = b+d;
    	double loSlop = Math.min(a, d);
    	double del = loSlop;
    	lex_B=b+loSlop; 
    	lex_A=a-loSlop; 
    	lex_D=d-loSlop; 
    	lex_C=c+loSlop; 
    	double pval=0;
    	while(del>0.000001) {
    	  del=del/2d;
    		if(pval<alpha) { 
    			lex_B=lex_B-del; 
    		} else { 
    			lex_B=lex_B+del;
    		}
    		lex_A = r1-lex_B; 
    		lex_D = c2-lex_B; 
    		lex_C = r2-lex_D;
    		pval=csp(csq(b,lex_B,0.5)+csq(a,lex_A,0.5)+csq(d,lex_D,0.5)+csq(c,lex_C,0.5));
    	}
    	
    	double hiSlop = Math.min(b, c);
    	del=hiSlop;
    	uex_B=b-hiSlop; uex_A=a+hiSlop; uex_D=d+hiSlop; uex_C=c-hiSlop; pval=0;
    	while(del>0.000001) {
    		del=del/2d;
    		if(pval<alpha) { uex_B=uex_B+del; } else { uex_B=uex_B-del; }
    		uex_A=r1-uex_B; uex_D=c2-uex_B; uex_C=r2-uex_D;
    		pval=csp(csq(b,uex_B,0.5)+csq(a,uex_A,0.5)+csq(d,uex_D,0.5)+csq(c,uex_C,0.5));
    	}
  	}
  }

  public MetricWithCI sensitivityWithCI(){
  	MetricWithCI sensitivity = new MetricWithCI();
  	CommonCalculations calc = new CommonCalculations();
  	sensitivity.estimate = calc.a/calc.c1;
  	sensitivity.ci95down = calc.lex_A/calc.c1;
  	sensitivity.ci95up = calc.uex_A/calc.c1;
  	return sensitivity;
  }
  
  public MetricWithCI specificityWithCI(){
  	MetricWithCI specificity = new MetricWithCI();
  	CommonCalculations calc = new CommonCalculations();
  	specificity.estimate = calc.d/calc.c2;
  	specificity.ci95down = calc.lex_D/calc.c2;
  	specificity.ci95up = calc.uex_D/calc.c2;
  	return specificity;
  }
  
  public MetricWithCI ppvWithCI(){
  	MetricWithCI ppv = new MetricWithCI();
  	CommonCalculations calc = new CommonCalculations();
  	ppv.estimate = calc.a/calc.r1;
  	ppv.ci95down = calc.lex_A/calc.r1;
  	ppv.ci95up = calc.uex_A/calc.r1;
  	return ppv;
  }
  
  private double csq(double o, double e, double y) {
  	if(e==0) { return 0; }
  	double x=Math.abs(o-e)-y; if(x<0) { return 0; }
  	return x*x/e;
  }
  
  private double csp(double x) {
    return chiSq(x,1);
   }
  
  private double chiSq(double x, double n) {
    if(x>1000 | n>1000) { double q=norm((Math.pow(x/n,1/3d)+2/(9*n)-1)/Math.sqrt(2/(9*n)))/2d; if (x>n) {return q;}{return 1-q;} }
    double p=Math.exp(-0.5*x); if((n%2)==1) { p=p*Math.sqrt(2*x/Math.PI); }
    double k=n; while(k>=2) { p=p*x/k; k=k-2; }
    double t=p; double cell_B=n; while(t>1e-15*p) { cell_B=cell_B+2; t=t*x/cell_B; p=p+t; }
    return 1-p;
  }
  
  private double norm(double z) { 
  	double q=z*z;
  	  if(Math.abs(z)>7) {return (1-1/q+3/(q*q))*Math.exp(-q/2)/(Math.abs(z)*Math.sqrt(Math.PI/2d));} {return chiSq(q,1); }
  }
}
