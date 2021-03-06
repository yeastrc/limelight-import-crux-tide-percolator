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

		System.err.println( "Determining location of Crux output files..." );

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

		File percolatorLogFile = new File(conversionParameters.getCruxOutputDirectory(), CruxConstants.percolatorOutputLogFileName);
		if( percolatorLogFile.exists() ) {
			System.err.println( "\tFound percolator log file: " + percolatorLogFile.getAbsolutePath() );
		} else {
			throw new Exception( "Could not find percolator log file: " + percolatorLogFile.getAbsolutePath() );
		}

		File tideLogFile = new File(conversionParameters.getCruxOutputDirectory(), CruxConstants.tideOutputLogFileName);
		if( tideLogFile.exists() ) {
			System.err.println( "\tFound tide log file: " + percolatorLogFile.getAbsolutePath() );
		} else {
			tideLogFile = null;
		}

		System.err.print( "\nReading Percolator XML data into memory..." );
		PercolatorResults percResults = PercolatorResultsReader.getPercolatorResults( percolatorXMLFile );
		System.err.println( " Found " + percResults.getReportedPeptideResults().size() + " peptides. " );


		System.err.print( "Reading pepXML file..." );
		TideResults tideResults = TidePepXMLResultsParser.getTideResults( pepXMLFile );
		System.err.println( " Found " + tideResults.getPeptidePSMMap().size() + " peptides. " );

		System.err.println( "Verifying all percolator results have comet results..." );
		TidePercolatorValidator.validateData(tideResults, percResults );

		System.err.print( "\nWriting out XML..." );
		(new XMLBuilder()).buildAndSaveXML(
				conversionParameters,
				tideResults,
				percResults,
				tideLogFile,
				percolatorLogFile
				);

		System.err.println( " Done." );

	}
}
