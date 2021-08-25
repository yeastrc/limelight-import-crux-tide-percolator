package org.yeastrc.limelight.xml.tide.objects;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

public class TidePSM {

	private BigDecimal xCorr;
	private BigDecimal deltaCn;
	private BigDecimal deltaLCn;
	private BigDecimal spScore;
	private BigDecimal spRank;
	private int hitRank;
	private BigDecimal exactPvalue;
	private BigDecimal refactoredXcorr;
	private BigDecimal residueEvidenceScore;
	private BigDecimal residueEvidencePvalue;
	private BigDecimal combinedPvalue;
	
	private int scanNumber;
	private BigDecimal precursorNeutralMass;
	private int charge;
	private BigDecimal retentionTime;

	private String peptideSequence;
	private Map<Integer,BigDecimal> modifications;
	private BigDecimal fdr;
	private Collection<String> proteinNames;

	@Override
	public String toString() {
		return "TidePSM{" +
				"xCorr=" + xCorr +
				", deltaCn=" + deltaCn +
				", deltaLCn=" + deltaLCn +
				", spScore=" + spScore +
				", spRank=" + spRank +
				", hitRank=" + hitRank +
				", exactPvalue=" + exactPvalue +
				", refactoredXcorr=" + refactoredXcorr +
				", residueEvidenceScore=" + residueEvidenceScore +
				", residueEvidencePvalue=" + residueEvidencePvalue +
				", combinedPvalue=" + combinedPvalue +
				", scanNumber=" + scanNumber +
				", precursorNeutralMass=" + precursorNeutralMass +
				", charge=" + charge +
				", retentionTime=" + retentionTime +
				", peptideSequence='" + peptideSequence + '\'' +
				", modifications=" + modifications +
				", fdr=" + fdr +
				", proteinNames=" + proteinNames +
				'}';
	}

	public BigDecimal getResidueEvidenceScore() {
		return residueEvidenceScore;
	}

	public void setResidueEvidenceScore(BigDecimal residueEvidenceScore) {
		this.residueEvidenceScore = residueEvidenceScore;
	}

	public BigDecimal getResidueEvidencePvalue() {
		return residueEvidencePvalue;
	}

	public void setResidueEvidencePvalue(BigDecimal residueEvidencePvalue) {
		this.residueEvidencePvalue = residueEvidencePvalue;
	}

	public BigDecimal getCombinedPvalue() {
		return combinedPvalue;
	}

	public void setCombinedPvalue(BigDecimal combinedPvalue) {
		this.combinedPvalue = combinedPvalue;
	}

	public BigDecimal getExactPvalue() {
		return exactPvalue;
	}

	public void setExactPvalue(BigDecimal exactPvalue) {
		this.exactPvalue = exactPvalue;
	}

	public BigDecimal getRefactoredXcorr() {
		return refactoredXcorr;
	}

	public void setRefactoredXcorr(BigDecimal refactoredXcorr) {
		this.refactoredXcorr = refactoredXcorr;
	}

	public Collection<String> getProteinNames() {
		return proteinNames;
	}

	public void setProteinNames(Collection<String> proteinNames) {
		this.proteinNames = proteinNames;
	}

	/**
	 * @return the xCorr
	 */
	public BigDecimal getxCorr() {
		return xCorr;
	}
	/**
	 * @param xCorr the xCorr to set
	 */
	public void setxCorr(BigDecimal xCorr) {
		this.xCorr = xCorr;
	}
	/**
	 * @return the deltaCn
	 */
	public BigDecimal getDeltaCn() {
		return deltaCn;
	}
	/**
	 * @param deltaCn the deltaCn to set
	 */
	public void setDeltaCn(BigDecimal deltaCn) {
		this.deltaCn = deltaCn;
	}

	public BigDecimal getDeltaLCn() {
		return deltaLCn;
	}

	public void setDeltaLCn(BigDecimal deltaLCn) {
		this.deltaLCn = deltaLCn;
	}

	/**
	 * @return the spScore
	 */
	public BigDecimal getSpScore() {
		return spScore;
	}
	/**
	 * @param spScore the spScore to set
	 */
	public void setSpScore(BigDecimal spScore) {
		this.spScore = spScore;
	}
	/**
	 * @return the spRank
	 */
	public BigDecimal getSpRank() {
		return spRank;
	}
	/**
	 * @param spRank the spRank to set
	 */
	public void setSpRank(BigDecimal spRank) {
		this.spRank = spRank;
	}
	/**
	 * @return the hitRank
	 */
	public int getHitRank() {
		return hitRank;
	}
	/**
	 * @param hitRank the hitRank to set
	 */
	public void setHitRank(int hitRank) {
		this.hitRank = hitRank;
	}
	/**
	 * @return the scanNumber
	 */
	public int getScanNumber() {
		return scanNumber;
	}
	/**
	 * @param scanNumber the scanNumber to set
	 */
	public void setScanNumber(int scanNumber) {
		this.scanNumber = scanNumber;
	}
	/**
	 * @return the precursorNeutralMass
	 */
	public BigDecimal getPrecursorNeutralMass() {
		return precursorNeutralMass;
	}
	/**
	 * @param precursorNeutralMass the precursorNeutralMass to set
	 */
	public void setPrecursorNeutralMass(BigDecimal precursorNeutralMass) {
		this.precursorNeutralMass = precursorNeutralMass;
	}
	/**
	 * @return the charge
	 */
	public int getCharge() {
		return charge;
	}
	/**
	 * @param charge the charge to set
	 */
	public void setCharge(int charge) {
		this.charge = charge;
	}
	/**
	 * @return the retentionTime
	 */
	public BigDecimal getRetentionTime() {
		return retentionTime;
	}
	/**
	 * @param retentionTime the retentionTime to set
	 */
	public void setRetentionTime(BigDecimal retentionTime) {
		this.retentionTime = retentionTime;
	}
	/**
	 * @return the peptideSequence
	 */
	public String getPeptideSequence() {
		return peptideSequence;
	}
	/**
	 * @param peptideSequence the peptideSequence to set
	 */
	public void setPeptideSequence(String peptideSequence) {
		this.peptideSequence = peptideSequence;
	}
	/**
	 * @return the modifications
	 */
	public Map<Integer, BigDecimal> getModifications() {
		return modifications;
	}
	/**
	 * @param modifications the modifications to set
	 */
	public void setModifications(Map<Integer, BigDecimal> modifications) {
		this.modifications = modifications;
	}
	/**
	 * @return the fdr
	 */
	public BigDecimal getFdr() {
		return fdr;
	}
	/**
	 * @param fdr the fdr to set
	 */
	public void setFdr(BigDecimal fdr) {
		this.fdr = fdr;
	}
	
	
}
