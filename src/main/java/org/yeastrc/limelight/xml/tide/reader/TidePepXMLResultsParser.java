/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.limelight.xml.tide.reader;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.yeastrc.limelight.xml.tide.objects.TidePSM;
import org.yeastrc.limelight.xml.tide.objects.TideReportedPeptide;
import org.yeastrc.limelight.xml.tide.objects.TideResults;
import org.yeastrc.limelight.xml.tide.utils.TidePepXMLParsingUtils;
import org.yeastrc.limelight.xml.tide.utils.ReportedPeptideUtils;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;

/**
 * @author Michael Riffle
 *
 */
public class TidePepXMLResultsParser {

	public static TideResults getTideResults(File pepXMLFile) throws Throwable {

		Map<TideReportedPeptide,Map<Integer, TidePSM>> resultMap = new HashMap<>();
				
		MsmsPipelineAnalysis msAnalysis = null;
		try {
			msAnalysis = TidePepXMLParsingUtils.getMSmsPipelineAnalysis( pepXMLFile );
		} catch( Throwable t ) {
			System.err.println( "Got an error parsing the pep XML file. Error: " + t.getMessage() );
			throw t;
		}
		
		
		TideResults results = new TideResults();
		results.setPeptidePSMMap( resultMap );

		results.setCometVersion( TidePepXMLParsingUtils.getCometVersionFromXML( msAnalysis ) );

		
		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				
				int charge = TidePepXMLParsingUtils.getChargeFromSpectrumQuery( spectrumQuery );
				int scanNumber = TidePepXMLParsingUtils.getScanNumberFromSpectrumQuery( spectrumQuery );
				BigDecimal neutralMass = TidePepXMLParsingUtils.getNeutralMassFromSpectrumQuery( spectrumQuery );
				BigDecimal retentionTime = TidePepXMLParsingUtils.getRetentionTimeFromSpectrumQuery( spectrumQuery );
				
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {
						
						// do not include decoy hits
						if( TidePepXMLParsingUtils.searchHitIsDecoy( searchHit, cometParams ) ) {
							continue;
						}
						
						TidePSM psm = null;
						
						try {
							
							psm = TidePepXMLParsingUtils.getPsmFromSearchHit( searchHit, charge, scanNumber, neutralMass, retentionTime, cometParams  );
							
						} catch( Throwable t) {
							
							System.err.println( "Error reading PSM from pepXML. Error: " + t.getMessage() );
							throw t;
							
						}
						
						if( psm != null ) {
							TideReportedPeptide tppRp = ReportedPeptideUtils.getTPPReportedPeptideForTPPPSM( psm );
							
							if( !results.getPeptidePSMMap().containsKey( tppRp ) )
								results.getPeptidePSMMap().put( tppRp, new HashMap<>() );
							
							results.getPeptidePSMMap().get( tppRp ).put( psm.getScanNumber(), psm );
						}
					}
				}
			}
		}
		
		return results;
	}
	
}
