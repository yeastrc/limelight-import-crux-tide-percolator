package org.yeastrc.limelight.xml.tide.objects;

import java.util.Map;

public class PercolatorPeptideData {

	private PercolatorPeptideScores percolatorPeptideScores;
	private Map<Integer, PercolatorPSM> percolatorPSMs;
	
	/**
	 * @return the percolatorPeptideScores
	 */
	public PercolatorPeptideScores getPercolatorPeptideScores() {
		return percolatorPeptideScores;
	}
	/**
	 * @param percolatorPeptideScores the percolatorPeptideScores to set
	 */
	public void setPercolatorPeptideScores(PercolatorPeptideScores percolatorPeptideScores) {
		this.percolatorPeptideScores = percolatorPeptideScores;
	}

	public Map<Integer, PercolatorPSM> getPercolatorPSMs() {
		return percolatorPSMs;
	}

	public void setPercolatorPSMs(Map<Integer, PercolatorPSM> percolatorPSMs) {
		this.percolatorPSMs = percolatorPSMs;
	}
}
