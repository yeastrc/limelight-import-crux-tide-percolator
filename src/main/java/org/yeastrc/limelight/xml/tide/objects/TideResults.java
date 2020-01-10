package org.yeastrc.limelight.xml.tide.objects;

import java.math.BigDecimal;
import java.util.Map;

public class TideResults {

	private Map<TideReportedPeptide, Map<Integer, TidePSM>> peptidePSMMap;
	private boolean computeSp = false;
	private Map<String, BigDecimal> staticMods;

	/**
	 * @return the peptidePSMMap
	 */
	public Map<TideReportedPeptide, Map<Integer, TidePSM>> getPeptidePSMMap() {
		return peptidePSMMap;
	}
	/**
	 * @param peptidePSMMap the peptidePSMMap to set
	 */
	public void setPeptidePSMMap(Map<TideReportedPeptide, Map<Integer, TidePSM>> peptidePSMMap) {
		this.peptidePSMMap = peptidePSMMap;
	}

	public boolean isComputeSp() {
		return computeSp;
	}

	public void setComputeSp(boolean computeSp) {
		this.computeSp = computeSp;
	}

	public Map<String, BigDecimal> getStaticMods() {
		return staticMods;
	}

	public void setStaticMods(Map<String, BigDecimal> staticMods) {
		this.staticMods = staticMods;
	}
}
