package org.yeastrc.limelight.xml.tide.objects;

import java.math.BigDecimal;
import java.util.Map;

public class TideResults {

	private Map<TideReportedPeptide, Map<Integer, TidePSM>> peptidePSMMap;
	private boolean computeSp = false;
	private boolean exactPvalue = false;
	private Map<BigDecimal, Map<String, BigDecimal>> staticMods;
	private Map<BigDecimal, Map<String, BigDecimal>> dynamicMods;
	private String decoyPrefix;

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

	public boolean isExactPvalue() {
		return exactPvalue;
	}

	public void setExactPvalue(boolean exactPvalue) {
		this.exactPvalue = exactPvalue;
	}

	public Map<BigDecimal, Map<String, BigDecimal>> getStaticMods() {
		return staticMods;
	}

	public void setStaticMods(Map<BigDecimal, Map<String, BigDecimal>> staticMods) {
		this.staticMods = staticMods;
	}

	public String getDecoyPrefix() {
		return decoyPrefix;
	}

	public void setDecoyPrefix(String decoyPrefix) {
		this.decoyPrefix = decoyPrefix;
	}

	public Map<BigDecimal, Map<String, BigDecimal>> getDynamicMods() {
		return dynamicMods;
	}

	public void setDynamicMods(Map<BigDecimal, Map<String, BigDecimal>> dynamicMods) {
		this.dynamicMods = dynamicMods;
	}
}
