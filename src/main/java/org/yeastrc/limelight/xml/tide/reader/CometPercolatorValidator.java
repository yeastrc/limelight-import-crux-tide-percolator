package org.yeastrc.limelight.xml.tide.reader;

import org.yeastrc.limelight.xml.tide.objects.TideReportedPeptide;
import org.yeastrc.limelight.xml.tide.objects.TideResults;
import org.yeastrc.limelight.xml.tide.objects.IndexedPercolatorPeptideData;
import org.yeastrc.limelight.xml.tide.objects.IndexedPercolatorResults;
import org.yeastrc.limelight.xml.tide.utils.CometParsingUtils;

public class CometPercolatorValidator {

	/**
	 * Ensure all percolator results have a result in the comet data
	 *
	 * @param tideResults
	 * @param percolatorResults
	 * @throws Exception if the data could not be validated
	 */
	public static void validateData(TideResults tideResults, IndexedPercolatorResults percolatorResults, Integer fileIndex ) throws Exception {

		for( String percolatorReportedPeptide : percolatorResults.getIndexedReportedPeptideResults().keySet() ) {

			TideReportedPeptide tideReportedPeptide = CometParsingUtils.getCometReportedPeptideForString( percolatorReportedPeptide, tideResults);
			IndexedPercolatorPeptideData indexedPercolatorPeptideData = percolatorResults.getIndexedReportedPeptideResults().get( percolatorReportedPeptide );

			// There are no percolator data for this peptide in this file index
			if( !indexedPercolatorPeptideData.getPercolatorPSMs().containsKey( fileIndex ) ) {
				continue;
			}

			if( tideReportedPeptide == null ) {
				throw new Exception( "Error: Comet results not found for peptide: " + percolatorReportedPeptide );
			}

			for( int scanNumber : indexedPercolatorPeptideData.getPercolatorPSMs().get( fileIndex ).keySet() ) {

				if( !tideResults.getPeptidePSMMap().get(tideReportedPeptide).containsKey( scanNumber ) ) {
					throw new Exception( "Error: Could not find PSM data for scan number " + scanNumber + " in percolator results for peptide: " + percolatorReportedPeptide );
				}
			}

		}

	}
	
}
