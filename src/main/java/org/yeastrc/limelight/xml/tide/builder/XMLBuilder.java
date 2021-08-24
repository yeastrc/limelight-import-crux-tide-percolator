package org.yeastrc.limelight.xml.tide.builder;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Map;

import org.yeastrc.limelight.limelight_import.api.xml_dto.*;
import org.yeastrc.limelight.limelight_import.api.xml_dto.ReportedPeptide.ReportedPeptideAnnotations;
import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchProgram.ReportedPeptideAnnotationTypes;
import org.yeastrc.limelight.limelight_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;
import org.yeastrc.limelight.xml.tide.annotation.PSMAnnotationTypeSortOrder;
import org.yeastrc.limelight.xml.tide.annotation.PSMAnnotationTypes;
import org.yeastrc.limelight.xml.tide.annotation.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.limelight.xml.tide.annotation.PeptideAnnotationTypeSortOrder;
import org.yeastrc.limelight.xml.tide.annotation.PeptideAnnotationTypes;
import org.yeastrc.limelight.xml.tide.annotation.PeptideDefaultVisibleAnnotationTypes;
import org.yeastrc.limelight.xml.tide.constants.Constants;
import org.yeastrc.limelight.xml.tide.objects.*;
import org.yeastrc.limelight.xml.tide.reader.PercolatorLogFileParser;
import org.yeastrc.limelight.xml.tide.utils.CometParsingUtils;

public class XMLBuilder {

	public void buildAndSaveXML( ConversionParameters conversionParameters,
			                     TideResults tideResults,
			                     PercolatorResults percolatorResults,
								 File tideLogFile,
								 File percolatorLogFile )
    throws Exception {


		LimelightInput limelightInputRoot = new LimelightInput();

		limelightInputRoot.setFastaFilename( conversionParameters.getFastaFilePath().getName() );

		// add in the conversion program (this program) information
		ConversionProgramBuilder.createInstance().buildConversionProgramSection( limelightInputRoot, conversionParameters);
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		limelightInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );

			searchProgram.setName( Constants.PROGRAM_NAME_CRUX );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_CRUX );
			searchProgram.setVersion(PercolatorLogFileParser.getCruxVersionFromLogFile( new FileInputStream( percolatorLogFile ) ) );
		}

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_TIDE );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_TIDE );
			searchProgram.setVersion(PercolatorLogFileParser.getCruxVersionFromLogFile( new FileInputStream( percolatorLogFile ) ) );
			
			
			//
			// Define the annotation types present in tide data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_TIDE, tideResults.isComputeSp(), tideResults.isExactPvalue() ) ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
			
		}

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_PERCOLATOR );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_PERCOLATOR );
			searchProgram.setVersion( percolatorResults.getPercolatorVersion() );
			
			
			//
			// Define the annotation types present in percolator data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_PERCOLATOR, null, null ) ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
			
			
			ReportedPeptideAnnotationTypes reportedPeptideAnnotationTypes = new ReportedPeptideAnnotationTypes();
			searchProgram.setReportedPeptideAnnotationTypes( reportedPeptideAnnotationTypes );

			FilterableReportedPeptideAnnotationTypes filterableReportedPeptideAnnotationTypes = new FilterableReportedPeptideAnnotationTypes();
			reportedPeptideAnnotationTypes.setFilterableReportedPeptideAnnotationTypes( filterableReportedPeptideAnnotationTypes );
			
			for( FilterableReportedPeptideAnnotationType annoType : PeptideAnnotationTypes.getFilterablePeptideAnnotationTypes( Constants.PROGRAM_NAME_PERCOLATOR ) ) {
				filterableReportedPeptideAnnotationTypes.getFilterableReportedPeptideAnnotationType().add( annoType );
			}
		}
		
		
		
		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );
		
		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		for( SearchAnnotation sa : PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes() ) {
			xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
		}
		
		VisibleReportedPeptideAnnotations xmlVisibleReportedPeptideAnnotations = new VisibleReportedPeptideAnnotations();
		xmlDefaultVisibleAnnotations.setVisibleReportedPeptideAnnotations( xmlVisibleReportedPeptideAnnotations );

		for( SearchAnnotation sa : PeptideDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes() ) {
			xmlVisibleReportedPeptideAnnotations.getSearchAnnotation().add( sa );
		}
		
		//
		// Define the default display order in limelight
		//
		AnnotationSortOrder xmlAnnotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( xmlAnnotationSortOrder );
		
		PsmAnnotationSortOrder xmlPsmAnnotationSortOrder = new PsmAnnotationSortOrder();
		xmlAnnotationSortOrder.setPsmAnnotationSortOrder( xmlPsmAnnotationSortOrder );
		
		for( SearchAnnotation xmlSearchAnnotation : PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder() ) {
			xmlPsmAnnotationSortOrder.getSearchAnnotation().add( xmlSearchAnnotation );
		}
		
		ReportedPeptideAnnotationSortOrder xmlReportedPeptideAnnotationSortOrder = new ReportedPeptideAnnotationSortOrder();
		xmlAnnotationSortOrder.setReportedPeptideAnnotationSortOrder( xmlReportedPeptideAnnotationSortOrder );
		
		for( SearchAnnotation xmlSearchAnnotation : PeptideAnnotationTypeSortOrder.getPeptideAnnotationTypeSortOrder() ) {
			xmlReportedPeptideAnnotationSortOrder.getSearchAnnotation().add( xmlSearchAnnotation );
		}
		
		//
		// Define the static mods
		//
		if( tideResults.getStaticMods() != null && tideResults.getStaticMods().keySet().size() > 0 ) {
			StaticModifications smods = new StaticModifications();
			limelightInputRoot.setStaticModifications( smods );
			
			for( BigDecimal totalMass : tideResults.getStaticMods().keySet() ) {
				for( String residue : tideResults.getStaticMods().get(totalMass).keySet()) {
					BigDecimal massDiff = tideResults.getStaticMods().get(totalMass).get(residue);

					StaticModification xmlSmod = new StaticModification();
					xmlSmod.setAminoAcid( residue );
					xmlSmod.setMassChange( massDiff );

					smods.getStaticModification().add( xmlSmod );
				}
			}
		}


		//
		// Build MatchedProteins section and get map of protein names to MatchedProtein ids
		//
		Map<String, Integer> proteinNameIds = MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				limelightInputRoot,
				conversionParameters.getFastaFilePath(),
				tideResults.getPeptidePSMMap().keySet()
		);


		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		limelightInputRoot.setReportedPeptides( reportedPeptides );
		
		// iterate over each distinct reported peptide
		for( String percolatorReportedPeptide : percolatorResults.getReportedPeptideResults().keySet() ) {

			PercolatorPeptideData percolatorPeptideData = percolatorResults.getReportedPeptideResults().get( percolatorReportedPeptide );
			TideReportedPeptide tideReportedPeptide = CometParsingUtils.getTideReportedPeptideForString( percolatorReportedPeptide, tideResults);
			
			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( tideReportedPeptide.getReportedPeptideString() );
			xmlReportedPeptide.setSequence( tideReportedPeptide.getNakedPeptide() );

			MatchedProteinsForPeptide xProteinsForPeptide = new MatchedProteinsForPeptide();
			xmlReportedPeptide.setMatchedProteinsForPeptide( xProteinsForPeptide );

			// add in protein inference info
			for( String proteinName : tideReportedPeptide.getProteinMatches() ) {

				int matchedProteinId = proteinNameIds.get( proteinName );

				MatchedProteinForPeptide xProteinForPeptide = new MatchedProteinForPeptide();
				xProteinsForPeptide.getMatchedProteinForPeptide().add( xProteinForPeptide );

				xProteinForPeptide.setId( BigInteger.valueOf( matchedProteinId ) );
			}

			// add in the filterable peptide annotations (e.g., q-value)
			ReportedPeptideAnnotations xmlReportedPeptideAnnotations = new ReportedPeptideAnnotations();
			xmlReportedPeptide.setReportedPeptideAnnotations( xmlReportedPeptideAnnotations );
			
			FilterableReportedPeptideAnnotations xmlFilterableReportedPeptideAnnotations = new FilterableReportedPeptideAnnotations();
			xmlReportedPeptideAnnotations.setFilterableReportedPeptideAnnotations( xmlFilterableReportedPeptideAnnotations );
			
			// handle q-value
			{
				FilterableReportedPeptideAnnotation xmlFilterableReportedPeptideAnnotation = new FilterableReportedPeptideAnnotation();
				xmlFilterableReportedPeptideAnnotations.getFilterableReportedPeptideAnnotation().add( xmlFilterableReportedPeptideAnnotation );
				
				xmlFilterableReportedPeptideAnnotation.setAnnotationName( PeptideAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_QVALUE );
				xmlFilterableReportedPeptideAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PERCOLATOR );
				xmlFilterableReportedPeptideAnnotation.setValue( BigDecimal.valueOf( percolatorPeptideData.getPercolatorPeptideScores().getqValue()) );
			}
			// handle p-value
			{
				FilterableReportedPeptideAnnotation xmlFilterableReportedPeptideAnnotation = new FilterableReportedPeptideAnnotation();
				xmlFilterableReportedPeptideAnnotations.getFilterableReportedPeptideAnnotation().add( xmlFilterableReportedPeptideAnnotation );
				
				xmlFilterableReportedPeptideAnnotation.setAnnotationName( PeptideAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_PVALUE );
				xmlFilterableReportedPeptideAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PERCOLATOR );
				xmlFilterableReportedPeptideAnnotation.setValue( BigDecimal.valueOf( percolatorPeptideData.getPercolatorPeptideScores().getpValue()) );
			}
			// handle pep
			{
				FilterableReportedPeptideAnnotation xmlFilterableReportedPeptideAnnotation = new FilterableReportedPeptideAnnotation();
				xmlFilterableReportedPeptideAnnotations.getFilterableReportedPeptideAnnotation().add( xmlFilterableReportedPeptideAnnotation );
				
				xmlFilterableReportedPeptideAnnotation.setAnnotationName( PeptideAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_PEP );
				xmlFilterableReportedPeptideAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PERCOLATOR );
				xmlFilterableReportedPeptideAnnotation.setValue( BigDecimal.valueOf( percolatorPeptideData.getPercolatorPeptideScores().getPep()) );
			}
			// handle svm score
			{
				FilterableReportedPeptideAnnotation xmlFilterableReportedPeptideAnnotation = new FilterableReportedPeptideAnnotation();
				xmlFilterableReportedPeptideAnnotations.getFilterableReportedPeptideAnnotation().add( xmlFilterableReportedPeptideAnnotation );
				
				xmlFilterableReportedPeptideAnnotation.setAnnotationName( PeptideAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_SVMSCORE );
				xmlFilterableReportedPeptideAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PERCOLATOR );
				xmlFilterableReportedPeptideAnnotation.setValue( BigDecimal.valueOf( percolatorPeptideData.getPercolatorPeptideScores().getSvmScore()) );
			}
			
			
			// add in the mods for this peptide
			if( tideReportedPeptide.getMods() != null && tideReportedPeptide.getMods().keySet().size() > 0 ) {
					
				PeptideModifications xmlModifications = new PeptideModifications();
				xmlReportedPeptide.setPeptideModifications( xmlModifications );
					
				for( int position : tideReportedPeptide.getMods().keySet() ) {

					PeptideModification xmlModification = new PeptideModification();
					xmlModifications.getPeptideModification().add( xmlModification );

					xmlModification.setMass( tideReportedPeptide.getMods().get( position ) );

					if( CometParsingUtils.isNTerminalMod( tideReportedPeptide.getNakedPeptide(), position ) ) {

						xmlModification.setIsNTerminal( true );

					} else if( CometParsingUtils.isCTerminalMod( tideReportedPeptide.getNakedPeptide(), position ) ) {

						xmlModification.setIsCTerminal( true );

					} else {
						xmlModification.setPosition( BigInteger.valueOf( position ) );
					}
				}
			}

			
			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );

			// iterate over all PSMs for this reported peptide

			for( int scanNumber : percolatorResults.getReportedPeptideResults().get( percolatorReportedPeptide ).getPercolatorPSMs().keySet() ) {

				TidePSM psm = tideResults.getPeptidePSMMap().get(tideReportedPeptide).get( scanNumber );

				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );

				xmlPsm.setScanNumber( new BigInteger( String.valueOf( scanNumber ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( psm.getCharge() ) ) );

				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );

				// handle tide scores
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.TIDE_ANNOTATION_TYPE_DELTACN );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_TIDE );
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaCn() );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.TIDE_ANNOTATION_TYPE_DELTALCN );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_TIDE );
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaLCn() );
				}

				if(tideResults.isComputeSp()) {
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.TIDE_ANNOTATION_TYPE_SPRANK);
						xmlFilterablePsmAnnotation.setSearchProgram(Constants.PROGRAM_NAME_TIDE);
						xmlFilterablePsmAnnotation.setValue(psm.getSpRank());
					}
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add(xmlFilterablePsmAnnotation);

						xmlFilterablePsmAnnotation.setAnnotationName(PSMAnnotationTypes.TIDE_ANNOTATION_TYPE_SPSCORE);
						xmlFilterablePsmAnnotation.setSearchProgram(Constants.PROGRAM_NAME_TIDE);
						xmlFilterablePsmAnnotation.setValue(psm.getSpScore());
					}
				}

				if(tideResults.isExactPvalue()) {

					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

						xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.TIDE_ANNOTATION_TYPE_EXACT_PVALUE );
						xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_TIDE );
						xmlFilterablePsmAnnotation.setValue( psm.getExactPvalue() );
					}

					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

						xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.TIDE_ANNOTATION_TYPE_REFACTORED_XCORR );
						xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_TIDE );
						xmlFilterablePsmAnnotation.setValue( psm.getRefactoredXcorr() );
					}

				} else {
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

						xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.TIDE_ANNOTATION_TYPE_XCORR );
						xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_TIDE );
						xmlFilterablePsmAnnotation.setValue( psm.getxCorr() );
					}
				}


				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.TIDE_ANNOTATION_TYPE_HIT_RANK );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_TIDE );
					xmlFilterablePsmAnnotation.setValue( BigDecimal.valueOf( psm.getHitRank() ).setScale( 0, RoundingMode.HALF_UP ) );
				}

				// handle percolator scores
				PercolatorPSM percolatorPSM = percolatorResults.getReportedPeptideResults().get( percolatorReportedPeptide ).getPercolatorPSMs().get( scanNumber );
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_PEP );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PERCOLATOR );
					xmlFilterablePsmAnnotation.setValue( BigDecimal.valueOf( percolatorPSM.getPep() ) );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_PVALUE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PERCOLATOR );
					xmlFilterablePsmAnnotation.setValue( BigDecimal.valueOf( percolatorPSM.getpValue() ) );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_QVALUE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PERCOLATOR );
					xmlFilterablePsmAnnotation.setValue( BigDecimal.valueOf( percolatorPSM.getqValue() ) );
				}
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );
					
					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PERCOLATOR_ANNOTATION_TYPE_SVMSCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PERCOLATOR );
					xmlFilterablePsmAnnotation.setValue( BigDecimal.valueOf( percolatorPSM.getSvmScore() ) );
				}
				
			}// end iterating over psms for a reported peptide
		
		}//end iterating over reported peptides

		
		
		// add in the output log file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		limelightInputRoot.setConfigurationFiles( xmlConfigurationFiles );

		{
			ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
			xmlConfigurationFiles.getConfigurationFile().add(xmlConfigurationFile);

			xmlConfigurationFile.setSearchProgram(Constants.PROGRAM_NAME_PERCOLATOR);
			xmlConfigurationFile.setFileName(percolatorLogFile.getName());
			xmlConfigurationFile.setFileContent(Files.readAllBytes(FileSystems.getDefault().getPath(percolatorLogFile.getAbsolutePath())));
		}


		if(tideLogFile != null && tideLogFile.exists()) {
			ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
			xmlConfigurationFiles.getConfigurationFile().add(xmlConfigurationFile);

			xmlConfigurationFile.setSearchProgram(Constants.PROGRAM_NAME_TIDE);
			xmlConfigurationFile.setFileName(tideLogFile.getName());
			xmlConfigurationFile.setFileContent(Files.readAllBytes(FileSystems.getDefault().getPath(tideLogFile.getAbsolutePath())));
		}
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( new File(conversionParameters.getOutputFilePath() ), limelightInputRoot);

	}
	
}
