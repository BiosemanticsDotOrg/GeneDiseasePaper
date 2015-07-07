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


package KnowledgeTransfer;

public class ConceptPairData {
	private Integer cidA;
	private Integer cidB;
	private Double similarity;
	
	public ConceptPairData(Integer cidA, Integer cidB, Double similarity) {
		super();
		this.cidA = cidA;
		this.cidB = cidB;
		this.similarity = similarity;
	}
	
	public Integer getCidA() {
		return cidA;
	}
	public void setCidA(Integer cidA) {
		this.cidA = cidA;
	}
	public Integer getCidB() {
		return cidB;
	}
	public void setCidB(Integer cidB) {
		this.cidB = cidB;
	}
	public Double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(Double similarity) {
		this.similarity = similarity;
	}
}
