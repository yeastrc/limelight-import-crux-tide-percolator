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

package org.yeastrc.limelight.xml.tide.main;

import org.yeastrc.limelight.xml.tide.builder.XMLBuilder;
import org.yeastrc.limelight.xml.tide.constants.CruxConstants;
import org.yeastrc.limelight.xml.tide.objects.*;
import org.yeastrc.limelight.xml.tide.reader.*;

import java.io.File;

public class ConverterRunner {

	// conveniently get a new instance of this class
	public static ConverterRunner createInstance() { return new ConverterRunner(); }
	
	
	public void convertCruxTidePercolatorToLimelightXML(ConversionParameters conversionParameters ) throws Throwable {

		System.err.print( "Determining location of Crux output files..." );

		File pepXMLFile = new File(conversionParameters.getCruxOutputDirectory(), CruxConstants.cruxOutputTidePepXMLFileName );
		if( pepXMLFile.exists() ) {
			System.err.println( "\tFound pep xml file: " + pepXMLFile.getAbsolutePath() );
		} else {
			throw new Exception( "Could not find pep xml file: " + pepXMLFile.getAbsolutePath() );
		}

		File percolatorXMLFile = new File(conversionParameters.getCruxOutputDirectory(), CruxConstants.cruxOutputPercolatorXMLFileName );
		if( percolatorXMLFile.exists() ) {
			System.err.println( "\tFound percolator xml file: " + percolatorXMLFile.getAbsolutePath() );
		} else {
			throw new Exception( "Could not find percolator xml file: " + percolatorXMLFile.getAbsolutePath() );
		}

		File percolatorLogFile = new File(conversionParameters.getCruxOutputDirectory(), CruxConstants.cruxOutputLogFileName );
		if( percolatorLogFile.exists() ) {
			System.err.println( "\tFound percolator log file: " + percolatorLogFile.getAbsolutePath() );
		} else {
			throw new Exception( "Could not find percolator log file: " + percolatorLogFile.getAbsolutePath() );
		}

		System.err.println( " Done." );

		System.err.print( "Reading Percolator XML data into memory..." );
		PercolatorResults percResults = PercolatorResultsReader.getPercolatorResults( percolatorXMLFile );
		System.err.print( " Got " + percResults.getReportedPeptideResults().size() + " peptides. " );
		System.err.println( " Done." );


		System.err.println( "\nReading pepXML file..." );
		TideResults tideResults = TidePepXMLResultsParser.getTideResults( pepXMLFile );
		System.err.println( " Done." );

		System.err.print( "\tVerifying all percolator results have comet results..." );
		CometPercolatorValidator.validateData(tideResults, percResults, fileIndex );
		System.err.println( " Done." );

		System.err.print( "\tWriting out XML..." );
		(new XMLBuilder()).buildAndSaveXML(
				conversionParameters,
				tideResults,
				percResults,
				cruxOutputParams,

				);

		System.err.println( " Done." );

	}
}
