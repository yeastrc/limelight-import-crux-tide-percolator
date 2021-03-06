package org.yeastrc.limelight.xml.tide.utils;

import org.yeastrc.limelight.xml.tide.constants.CometConstants;
import org.yeastrc.limelight.xml.tide.objects.TideReportedPeptide;
import org.yeastrc.limelight.xml.tide.objects.TideResults;

import java.math.BigDecimal;

public class CometParsingUtils {
	
	public static TideReportedPeptide getTideReportedPeptideForString(String reportedPeptide, TideResults tideResults) {
		
		for( TideReportedPeptide cometPeptide : tideResults.getPeptidePSMMap().keySet() ) {
			if( cometPeptide.getReportedPeptideString().equals( reportedPeptide ) ) {
				return cometPeptide;
			}
		}
		
		return null;
	}

	/**
	 * Comet reports n-terminal mod mass as the mod mass plus Hydrogen (don't ask). So subtract hydrogen
	 * from the reported mod mass to get real mod mass.
	 *
	 * @param reportedModMass
	 * @return
	 */
	public static BigDecimal getNTerminalModMass( BigDecimal reportedModMass ) {
		return reportedModMass.subtract(CometConstants.COMET_MASS_HYDROGEN_MONO );
	}

	/**
	 * Comet reports c-terminal mod mass as the mod mass plus Hydrogen plus Oxygen (don't ask). So subtract hydrogen
	 * and oxygen from the reported mod mass to get real mod mass.
	 *
	 * @param reportedModMass
	 * @return
	 */
	public static BigDecimal getCTerminalModMass( BigDecimal reportedModMass ) {
		return reportedModMass.subtract( CometConstants.COMET_MASS_HYDROGEN_MONO ).subtract( CometConstants.COMET_MASS_OXYGEN_MONO );
	}

	/**
	 * Return true if the mod at the reported position is an N-terminal mod in that peptide
	 *
	 * @param peptide
	 * @param position
	 * @return
	 */
	public static boolean isNTerminalMod( String peptide, int position ) {
		if( position == 0 ) { return true; }
		return false;
	}

	/**
	 * Return true if the mod at the reported position is a C-terminal mod in that peptide
	 *
	 * @param peptide
	 * @param position
	 * @return
	 */
	public static boolean isCTerminalMod( String peptide, int position ) {
		if( position == peptide.length() + 1 ) { return true; }
		return false;
	}

	/**
	 * Return true if the given protein name is a decoy protein hit, given the comet parameters
	 * used in the search.
	 *
	 * @param proteinName
	 * @param decoyPrefix
	 * @return
	 */
	public static boolean isDecoyProtein(String proteinName, String decoyPrefix ) {
		if( decoyPrefix != null ) {
			if( proteinName.startsWith( decoyPrefix ) ) {
				return true;
			}
		}

		return false;
	}

}
