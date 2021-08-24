package org.yeastrc.limelight.xml.tide.utils;

import static java.lang.Math.toIntExact;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.yeastrc.limelight.xml.tide.objects.TidePSM;

import net.systemsbiology.regis_web.pepxml.AltProteinDataType;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.NameValueType;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType.ModAminoacidMass;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;

public class TidePepXMLParsingUtils {

	/**
	 * Attempt to get whether or not sp was computed. Return false if it cannot be determined.
	 *
	 * @param msAnalysis
	 * @return
	 */
	public static boolean getIsSpCalculatedFromXML( MsmsPipelineAnalysis msAnalysis ) {

		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SearchSummary searchSummary : runSummary.getSearchSummary() ) {

				for( NameValueType nvt : searchSummary.getParameter() ) {
					if(nvt.getName().equals("compute-sp")) {
						return Boolean.valueOf(nvt.getValueAttribute());
					}
				}

			}
		}

		return false;
	}

	/**
	 * Attempt to get whether or not exact p-value was computed. Return false if it cannot be determined.
	 *
	 * @param msAnalysis
	 * @return
	 */
	public static boolean getIsExactPValueCalculatedFromXML( MsmsPipelineAnalysis msAnalysis ) {

		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SearchSummary searchSummary : runSummary.getSearchSummary() ) {

				for( NameValueType nvt : searchSummary.getParameter() ) {
					if(nvt.getName().equals("exact-p-value")) {
						return Boolean.valueOf(nvt.getValueAttribute());
					}
				}

			}
		}

		return false;
	}

	public static String getDecoyPrefixFromXML( MsmsPipelineAnalysis msAnalysis ) {

		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SearchSummary searchSummary : runSummary.getSearchSummary() ) {

				for( NameValueType nvt : searchSummary.getParameter() ) {
					if(nvt.getName().equals("decoy-prefix")) {
						return nvt.getValueAttribute();
					}
				}

			}
		}

		return null;
	}

	public static Map<BigDecimal, Map<String, BigDecimal>> getStaticModsFromXML( MsmsPipelineAnalysis msAnalysis ) {

		Map<BigDecimal, Map<String, BigDecimal>> staticMods = new HashMap<>();

		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SearchSummary searchSummary : runSummary.getSearchSummary() ) {

				for(SearchSummary.AminoacidModification xMod : searchSummary.getAminoacidModification() ) {
					if(xMod.getVariable().equals("N")) {
						// we have a static mod

						{
							BigDecimal totalMass = xMod.getMass().setScale(2, RoundingMode.HALF_DOWN).stripTrailingZeros();
							BigDecimal massDiff = xMod.getMassdiff();
							String residue = xMod.getAminoacid();

							if (!staticMods.containsKey(totalMass)) {
								staticMods.put(totalMass, new HashMap<>());
							}

							staticMods.get(totalMass).put(residue, massDiff);
						}

						{
							BigDecimal totalMass = xMod.getMass().setScale(2, RoundingMode.FLOOR).stripTrailingZeros();
							BigDecimal massDiff = xMod.getMassdiff();
							String residue = xMod.getAminoacid();

							if (!staticMods.containsKey(totalMass)) {
								staticMods.put(totalMass, new HashMap<>());
							}

							staticMods.get(totalMass).put(residue, massDiff);
						}
					}
				}

			}
		}

		return staticMods;
	}


	public static Map<BigDecimal, Map<String, BigDecimal>> getDynamicModsFromXML( MsmsPipelineAnalysis msAnalysis ) {

		Map<BigDecimal, Map<String, BigDecimal>> dynamicMods = new HashMap<>();

		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SearchSummary searchSummary : runSummary.getSearchSummary() ) {

				for(SearchSummary.AminoacidModification xMod : searchSummary.getAminoacidModification() ) {
					if(xMod.getVariable().equals("Y")) {
						// we have a var mod

						{
							BigDecimal totalMass = xMod.getMass().setScale(2, RoundingMode.FLOOR).stripTrailingZeros();
							BigDecimal massDiff = xMod.getMassdiff();
							String residue = xMod.getAminoacid();

							if (!dynamicMods.containsKey(totalMass)) {
								dynamicMods.put(totalMass, new HashMap<>());
							}

							dynamicMods.get(totalMass).put(residue, massDiff);
						}

						{
							BigDecimal totalMass = xMod.getMass().setScale(2, RoundingMode.HALF_UP).stripTrailingZeros();
							BigDecimal massDiff = xMod.getMassdiff();
							String residue = xMod.getAminoacid();

							if (!dynamicMods.containsKey(totalMass)) {
								dynamicMods.put(totalMass, new HashMap<>());
							}

							dynamicMods.get(totalMass).put(residue, massDiff);
						}

					}
				}

			}
		}

		return dynamicMods;
	}


	/**
	 * Return true if this searchHit is a decoy. This means that it only matches
	 * decoy proteins.
	 * 
	 * @param searchHit
	 * @return
	 */
	public static boolean searchHitIsDecoy( SearchHit searchHit, String decoyPrefix) {

		if(decoyPrefix == null) {
			return false;
		}

		String protein = searchHit.getProtein();

		if( CometParsingUtils.isDecoyProtein( protein, decoyPrefix ) ) {

			if( searchHit.getAlternativeProtein() != null ) {
				for( AltProteinDataType ap : searchHit.getAlternativeProtein() ) {

					if( !CometParsingUtils.isDecoyProtein( ap.getProtein(), decoyPrefix ) ) {
						return false;
					}
				}
			}
			
			return true;			
		}
		
		return false;
	}
	
	/**
	 * Return the top-most parent element of the pepXML file as a JAXB object.
	 * 
	 * @param file
	 * @return
	 * @throws Throwable
	 */
	public static MsmsPipelineAnalysis getMSmsPipelineAnalysis( File file ) throws JAXBException {
		
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(MsmsPipelineAnalysis.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			jaxbUnmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			MsmsPipelineAnalysis msAnalysis = (MsmsPipelineAnalysis) jaxbUnmarshaller.unmarshal(file);

			return msAnalysis;
		} catch(JAXBException e) {

			throw e;
		}
	}
	
	/**
	 * Get the retention time from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static BigDecimal getRetentionTimeFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getRetentionTimeSec();
	}
	
	/**
	 * Get the neutral mass from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static BigDecimal getNeutralMassFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getPrecursorNeutralMass();
	}
	
	/**
	 * Get the scan number from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static int getScanNumberFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return toIntExact( spectrumQuery.getStartScan() );
	}
	
	/**
	 * Get the charge from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static int getChargeFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getAssumedCharge().intValue();
	}

    /**
     * Get a TPPPSM (psm object) from the supplied searchHit JAXB object.
     *
     * If the searchHit has no peptideprophet score, null is returned.
     *
     * @param searchHit
     * @param charge
     * @param scanNumber
     * @param obsMass
     * @param retentionTime
     * @return
     * @throws Throwable
     */
	public static TidePSM getPsmFromSearchHit(
			SearchHit searchHit,
			int charge,
			int scanNumber,
			BigDecimal obsMass,
			BigDecimal retentionTime,
			String decoyPrefix,
			Map<BigDecimal, Map<String, BigDecimal>> staticMods,
			Map<BigDecimal, Map<String, BigDecimal>> dynamicMods) throws Throwable {
				
		TidePSM psm = new TidePSM();
		
		psm.setCharge( charge );
		psm.setScanNumber( scanNumber );
		psm.setPrecursorNeutralMass( obsMass );
		psm.setRetentionTime( retentionTime );
		psm.setHitRank( getHitRankForSearchHit( searchHit ) );
		
		psm.setPeptideSequence( searchHit.getPeptide() );
		
		psm.setxCorr( getScoreForType( searchHit, "xcorr_score" ) );
		psm.setDeltaCn( getScoreForType( searchHit, "deltacn" ) );
		psm.setDeltaLCn( getScoreForType( searchHit, "deltalcn" ) );
		psm.setSpScore( getScoreForType( searchHit, "spscore" ) );
		psm.setSpRank( getScoreForType( searchHit, "sprank" ) );
		psm.setExactPvalue( getScoreForType( searchHit, "exact_pvalue" ) );
		psm.setRefactoredXcorr( getScoreForType( searchHit, "refactored_xcorr" ) );

		try {
			psm.setProteinNames( getProteinNamesForSearchHit( searchHit, decoyPrefix ) );
		} catch( Throwable t ) {

			String error = "Error getting protein names for PSM.\n";
			error += "Psm: " + psm + "\n";
			error += "Error: " + t.getMessage();

			System.err.println( error );
			throw t;
		}

		try {
			psm.setModifications( getModificationsForSearchHit( searchHit, staticMods, dynamicMods ) );
		} catch( Throwable t ) {

			String error = "Error getting mods for PSM.\n";
			error += "Psm: " + psm + "\n";
			error += "Error: " + t.getMessage();

			System.err.println( error );
			throw t;
		}

		return psm;
	}
	
	public static int getHitRankForSearchHit( SearchHit searchHit ) throws Exception {
		
		return toIntExact( searchHit.getHitRank() );
		
	}

	/**
	 * Get the requested score from the searchHit JAXB object. Returns null if it can't be found.
	 *
	 * @param searchHit
	 * @param type
	 * @return
	 * @throws Throwable
	 */
	public static BigDecimal getScoreForType( SearchHit searchHit, String type ) throws Throwable {
		
		for( NameValueType searchScore : searchHit.getSearchScore() ) {
			if( searchScore.getName().equals( type ) ) {
				
				return new BigDecimal( searchScore.getValueAttribute() );
			}
		}
		
		return null;
	}

	/**
	 * Get the variable modifications from the supplied searchHit JAXB object
	 *
	 * @param searchHit
	 * @return
	 * @throws Throwable
	 */
	public static Map<Integer, BigDecimal> getModificationsForSearchHit( SearchHit searchHit,
																		 Map<BigDecimal, Map<String, BigDecimal>> staticMods,
																		 Map<BigDecimal, Map<String, BigDecimal>> dynamicMods) throws Throwable {
		
		Map<Integer, BigDecimal> modMap = new HashMap<>();
		
		ModInfoDataType mofo = searchHit.getModificationInfo();
		if( mofo != null ) {

			String peptide = searchHit.getPeptide();

			for( ModAminoacidMass mod : mofo.getModAminoacidMass() ) {

				if(!isModAStaticMod(mod, peptide, staticMods)) {
					BigDecimal massDiff = getMassDiffForDynamicMod(mod, peptide, dynamicMods);
					modMap.put(mod.getPosition().intValueExact(), massDiff);
				}
			}

			// set n-term mod at position 0
			if( mofo.getModNtermMass() != null ) {
				modMap.put( 0, CometParsingUtils.getNTerminalModMass( BigDecimal.valueOf( mofo.getModNtermMass() ) ) );
			}

			// set c-term mod at peptide_length + 1
			if( mofo.getModCtermMass() != null ) {
				modMap.put( searchHit.getPeptide().length() + 1, CometParsingUtils.getCTerminalModMass( BigDecimal.valueOf( mofo.getModNtermMass() ) ) );
			}
		}

		
		return modMap;
	}

	public static Collection<String> getProteinNamesForSearchHit(SearchHit searchHit, String decoyPrefix ) throws Throwable {

		Collection<String> proteins = new HashSet<>();

		if( searchHit.getProtein() != null && !CometParsingUtils.isDecoyProtein( searchHit.getProtein(), decoyPrefix ) ) {
			proteins.add( searchHit.getProtein());
		}

		if( searchHit.getAlternativeProtein() != null && searchHit.getAlternativeProtein().size() > 0 ) {

			for( AltProteinDataType apdt : searchHit.getAlternativeProtein() ) {
				if( !CometParsingUtils.isDecoyProtein( apdt.getProtein(), decoyPrefix ) ) {
					proteins.add( apdt.getProtein() );
				}
			}

		}

		if( proteins.size() < 1 ) {
			throw new Exception( "Found zero target proteins for searchHit." );
		}

		return proteins;
	}

	private static BigDecimal getMassDiffForDynamicMod(ModAminoacidMass mod, String peptide, Map<BigDecimal, Map<String, BigDecimal>> dynamicMods) throws Exception {

		if(dynamicMods == null || dynamicMods.keySet().size() < 1) {
			throw new Exception("Dynamic mod found in results (" + mod + "), but no dynamic mods defined in search.");
		}

		BigDecimal modMass = BigDecimal.valueOf( mod.getMass() );

		if( !dynamicMods.containsKey(modMass)) {
			throw new Exception("Found a dynamic mod with mass " + modMass + " in results, but no dynamic mod w/ that mass was defined in the search." );
		}

		int position = (mod.getPosition()).intValueExact();
		String residue = peptide.substring( position - 1, position );

		if(dynamicMods.get(modMass).containsKey(residue)) {
			return dynamicMods.get(modMass).get(residue);
		}

		throw new Exception("Got a mod mass of " + modMass + " on position " + position + " in peptide " + peptide + ", but no dynamic mod is defined with that mass for that residue in the search.");
	}

	private static boolean isModAStaticMod(ModAminoacidMass mod, String peptide, Map<BigDecimal, Map<String, BigDecimal>> staticMods) {

		if(staticMods == null || staticMods.keySet().size() < 1) {
			return false;
		}

		BigDecimal modMass = BigDecimal.valueOf( mod.getMass() );

		if( !staticMods.containsKey(modMass)) {
			return false;
		}

		int position = (mod.getPosition()).intValueExact();
		String residue = peptide.substring( position - 1, position );

		return staticMods.get(modMass).containsKey(residue);
	}


	
}
