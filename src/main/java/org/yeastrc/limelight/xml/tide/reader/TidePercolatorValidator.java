package org.yeastrc.limelight.xml.tide.reader;

import org.yeastrc.limelight.xml.tide.objects.PercolatorPeptideData;
import org.yeastrc.limelight.xml.tide.objects.PercolatorResults;
import org.yeastrc.limelight.xml.tide.objects.TideReportedPeptide;
import org.yeastrc.limelight.xml.tide.objects.TideResults;
import org.yeastrc.limelight.xml.tide.utils.CometParsingUtils;

public class TidePercolatorValidator {

	/**
	 * Ensure all percolator results have a result in the comet data
	 *
	 * @param tideResults
	 * @param percolatorResults
	 * @throws Exception if the data could not be validated
	 */
	public static void validateData(TideResults tideResults, PercolatorResults percolatorResults) throws Exception {

		for( String percolatorReportedPeptide : percolatorResults.getReportedPeptideResults().keySet() ) {

			TideReportedPeptide tideReportedPeptide = CometParsingUtils.getTideReportedPeptideForString( percolatorReportedPeptide, tideResults);
			PercolatorPeptideData percolatorPeptideData = percolatorResults.getReportedPeptideResults().get( percolatorReportedPeptide );

			if( tideReportedPeptide == null ) {
				throw new Exception( "Error: Tide results not found for peptide: " + percolatorReportedPeptide );
			}

			for( int scanNumber : percolatorPeptideData.getPercolatorPSMs().keySet() ) {

				if( !tideResults.getPeptidePSMMap().get(tideReportedPeptide).containsKey( scanNumber ) ) {
					throw new Exception( "Error: Could not find PSM data for scan number " + scanNumber + " in percolator results for peptide: " + percolatorReportedPeptide );
				}
			}

		}

	}
}
