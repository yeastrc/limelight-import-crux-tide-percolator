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


import org.yeastrc.limelight.xml.tide.objects.PercolatorPSM;
import org.yeastrc.limelight.xml.tide.objects.PercolatorPeptideData;
import org.yeastrc.limelight.xml.tide.objects.PercolatorPeptideScores;
import org.yeastrc.limelight.xml.tide.objects.PercolatorResults;
import org.yeastrc.limelight.xml.tide.utils.PercolatorParsingUtils;
import org.yeastrc.proteomics.percolator.out.PercolatorOutXMLUtils;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPeptide;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPercolatorOutput;
import org.yeastrc.proteomics.percolator.out.perc_out_common_interfaces.IPsm;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PercolatorResultsReader {

	/**
	 * Get the parsed percolator results for the given percolator xml data file
	 * 
	 * @param file
	 * @return
	 * @throws Throwable
	 */
	public static PercolatorResults getPercolatorResults(File file ) throws Throwable {
				
		IPercolatorOutput po = getIPercolatorOutput( file );
		
		String version = getPercolatorVersion( po );

		Map<String, Collection<PercolatorPSM>> psmMap = getPercolatorPSMs( po );
		Map<String, PercolatorPeptideData> percolatorPeptideData = getPercolatorPeptideData( po, psmMap );
		
		PercolatorResults results = new PercolatorResults();
		results.setPercolatorVersion( version );
		results.setReportedPeptideResults( percolatorPeptideData );
		
		return results;
	}

	
	/**
	 * Get a map of percolator peptide => map of scan number => percolator psm
	 * 
	 * @param po The IPercolatorOutput JAXB object created from parsing the XML
	 * @param psmMap A map of all psms found for each reported peptide string
	 * @return
	 * @throws Exception 
	 */
	protected static Map<String, PercolatorPeptideData> getPercolatorPeptideData(IPercolatorOutput po, Map<String, Collection<PercolatorPSM>> psmMap) throws Exception {
		
		Map<String, PercolatorPeptideData> resultsMap = new HashMap<>();
		
		// loop through the repoted peptides
	    for( IPeptide xpeptide : po.getPeptides().getPeptide() ) {

	    	PercolatorPeptideScores percolatorPeptide = getPercolatorPeptideFromJAXB( xpeptide );
	    	
	    	if( resultsMap.containsKey( percolatorPeptide ) )
	    		throw new Exception( "Found two instances of the same reported peptide: " + percolatorPeptide + " and " + resultsMap.get( percolatorPeptide ) );
	    	
	    	Map<Integer, PercolatorPSM> psmsForPeptide = getPercolatorPSMsForPeptide( xpeptide, psmMap );
	    	
	    	if( psmsForPeptide == null || psmsForPeptide.keySet().size() < 1 )
	    		throw new Exception( "Found no PSMs for peptide: " + percolatorPeptide );
	    	
	    	PercolatorPeptideData rpData = new PercolatorPeptideData();
	    	rpData.setPercolatorPeptideScores( percolatorPeptide );
	    	rpData.setPercolatorPSMs( psmsForPeptide );
	    	
	    	resultsMap.put(percolatorPeptide.getReportedPeptide(), rpData);
	    }
		
		
		return resultsMap;
	}
	
	/**
	 * Get a map of scan number => PercolatorPSM for all PSMs associated with the supplied JAXB peptide object
	 * @param xpeptide
	 * @param psmMap
	 * @return
	 * @throws Exception
	 */
	protected static Map<Integer, PercolatorPSM> getPercolatorPSMsForPeptide(IPeptide xpeptide, Map<String, Collection<PercolatorPSM>> psmMap ) throws Exception {
		
		Map<Integer, PercolatorPSM> psmsForPeptide = new HashMap<>();

		if(!psmMap.containsKey(xpeptide.getPeptideId()) || psmMap.get(xpeptide.getPeptideId()).size() < 1) {
			throw new Exception("Did not find any PSMs for peptide: " + xpeptide.getPeptideId() );
		}

		for(PercolatorPSM pPsm : psmMap.get(xpeptide.getPeptideId())) {
			psmsForPeptide.put( pPsm.getScanNumber(), pPsm );
		}
		
		return psmsForPeptide;
	}
	
	/**
	 * Get the PercolatorPeptide object for the given JAXB representation of a percolator peptide
	 * 
	 * @param xpeptide
	 * @return
	 */
	protected static PercolatorPeptideScores getPercolatorPeptideFromJAXB( IPeptide xpeptide ) {
		
		PercolatorPeptideScores pp = new PercolatorPeptideScores();
		
		pp.setPep( Double.valueOf( xpeptide.getPep() ) );
		pp.setpValue( Double.valueOf( xpeptide.getPValue() ) );
		pp.setqValue( Double.valueOf( xpeptide.getQValue() ) );
		pp.setReportedPeptide( xpeptide.getPeptideId() );
		pp.setSvmScore( Double.valueOf( xpeptide.getSvmScore() ) );
		
		return pp;
	}
	
	
	/**
	 * Return a collection of all the PercolatorPSMs parsed from the JAXB top level percolator XML object
	 * 
	 * @param po
	 * @return
	 */
	protected static Map<String, Collection<PercolatorPSM>> getPercolatorPSMs(IPercolatorOutput po ) throws Exception {

		Map<String, Collection<PercolatorPSM>> psmMap = new HashMap<>();
		
	    // loop through PSMs
	    for( IPsm xpsm : po.getPsms().getPsm() ) {
	    	
	    	PercolatorPSM psm = getPercolatorPSMFromJAXB( xpsm );

	    	if(!psmMap.containsKey(psm.getReportedPeptide())) {
	    		psmMap.put(psm.getReportedPeptide(), new HashSet<>());
			}

	    	if(psmMap.get(psm.getReportedPeptide()).contains(psm)) {
	    		throw new Exception("Encountered multiple PSMs with the same reported peptide sequence and scan number: " + psm);
			}

			psmMap.get(psm.getReportedPeptide()).add(psm);

	    }
		
		return psmMap;
	}
	
	/**
	 * Get a PercolatorPSM from the JAXB object generated from parsing the XML
	 * @param xpsm
	 * @return
	 */
	protected static PercolatorPSM getPercolatorPSMFromJAXB( IPsm xpsm ) {
		
		PercolatorPSM psm = new PercolatorPSM();
		
		psm.setPep( Double.valueOf( xpsm.getPep() ) );
		psm.setpValue( Double.valueOf( xpsm.getPValue() ) );
		psm.setqValue( Double.valueOf( xpsm.getQValue() ) );
		psm.setReportedPeptide( xpsm.getPeptideSeq().getSeq() );
		psm.setScanNumber( PercolatorParsingUtils.getScanNumberFromScanId( xpsm.getPsmId() ) );
		psm.setSvmScore( Double.valueOf( xpsm.getSvmScore() ) );
		
		return psm;
	}
	

	/**
	 * Get the version of percolator used to generate the XML. If unable to determine
	 * return "unknown"
	 * 
	 * @param po
	 * @return
	 */
	protected static String getPercolatorVersion( IPercolatorOutput po ) {
		
		String version = null;
		
		try {
			
			version = po.getPercolatorVersion();
			
		} catch ( Exception e ) {
			
			version = "unknown";
			
		}
		
		return version;
	}
	
	
	/**
	 * Get the top level JAXB object for the given percolator XML file
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	protected static IPercolatorOutput getIPercolatorOutput(File file ) throws Exception {
		
		String xsdVersion = PercolatorOutXMLUtils.getXSDVersion( file );
		
		JAXBContext jaxbContext = JAXBContext.newInstance( "com.per_colator.percolator_out._" + xsdVersion );
		Unmarshaller u = jaxbContext.createUnmarshaller();
		IPercolatorOutput po = (IPercolatorOutput)u.unmarshal( file );
	
		return po;
	}

	
}
