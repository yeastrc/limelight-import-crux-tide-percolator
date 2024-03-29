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

package org.yeastrc.limelight.xml.tide.annotation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterDirectionType;
import org.yeastrc.limelight.limelight_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.limelight.xml.tide.constants.Constants;
import org.yeastrc.limelight.xml.tide.constants.CruxConstants;


public class PSMAnnotationTypes {

	// comet scores
	public static final String TIDE_ANNOTATION_TYPE_XCORR = "XCorr";
	public static final String TIDE_ANNOTATION_TYPE_DELTACN = "DeltaCn";
	public static final String TIDE_ANNOTATION_TYPE_DELTALCN = "DeltaLCn";
	public static final String TIDE_ANNOTATION_TYPE_SPSCORE = "Sp Score";
	public static final String TIDE_ANNOTATION_TYPE_SPRANK = "Sp Rank";
	public static final String TIDE_ANNOTATION_TYPE_HIT_RANK = "Hit Rank";
	public static final String TIDE_ANNOTATION_TYPE_EXACT_PVALUE = "Exact P-value";
	public static final String TIDE_ANNOTATION_TYPE_REFACTORED_XCORR = "Refactored XCorr";
	public static final String TIDE_ANNOTATION_TYPE_RESIDUE_EVIDENCE_SCORE = "Res. Ev. Score";
	public static final String TIDE_ANNOTATION_TYPE_RESIDUE_EVIDENCE_PVALUE = "Res. Ev. P-value";
	public static final String TIDE_ANNOTATION_TYPE_COMBINED_PVALUE = "Combined P-value";

	// percolator scores
	public static final String PERCOLATOR_ANNOTATION_TYPE_QVALUE = "q-value";
	public static final String PERCOLATOR_ANNOTATION_TYPE_PVALUE = "p-value";
	public static final String PERCOLATOR_ANNOTATION_TYPE_PEP = "PEP";
	public static final String PERCOLATOR_ANNOTATION_TYPE_SVMSCORE = "SVM Score";

	
	
	public static List<FilterablePsmAnnotationType> getFilterablePsmAnnotationTypes( String programName, Boolean wasSpComputed, Boolean isExactPvalue, String scoreFunction ) {
		List<FilterablePsmAnnotationType> types = new ArrayList<FilterablePsmAnnotationType>();

		if( programName.equals( Constants.PROGRAM_NAME_TIDE ) ) {

			boolean showXcorr = scoreFunction.equals(CruxConstants.SCORE_FUNCTION_XCORR) || scoreFunction.equals(CruxConstants.SCORE_FUNCTION_BOTH);
			boolean showResidueEvidence = scoreFunction.equals(CruxConstants.SCORE_FUNCTION_RESIDUE_EVIDENCE) || scoreFunction.equals(CruxConstants.SCORE_FUNCTION_BOTH);

			if(isExactPvalue) {

				if(showXcorr) {

					{
						FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
						type.setName(TIDE_ANNOTATION_TYPE_EXACT_PVALUE);
						type.setDescription("Tide exact p-value calculation");
						type.setFilterDirection(FilterDirectionType.BELOW);

						types.add(type);
					}

					{
						FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
						type.setName(TIDE_ANNOTATION_TYPE_REFACTORED_XCORR);
						type.setDescription("Tide refactored cross-correlation coefficient, triggered by exact p-value parameter.");
						type.setFilterDirection(FilterDirectionType.ABOVE);

						types.add(type);
					}
				}

				if(showResidueEvidence) {

						FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
						type.setName(TIDE_ANNOTATION_TYPE_RESIDUE_EVIDENCE_PVALUE);
						type.setDescription("Tide residue evidence p-value.");
						type.setFilterDirection(FilterDirectionType.BELOW);

						types.add(type);
				}

			} else if(scoreFunction.equals(CruxConstants.SCORE_FUNCTION_XCORR)) {

				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( TIDE_ANNOTATION_TYPE_XCORR );
				type.setDescription( "Tide cross-correlation coefficient" );
				type.setFilterDirection( FilterDirectionType.ABOVE );

				types.add( type );
			}


			if(showResidueEvidence) {

					FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
					type.setName(TIDE_ANNOTATION_TYPE_RESIDUE_EVIDENCE_SCORE);
					type.setDescription("Tide residue evidence score.");
					type.setFilterDirection(FilterDirectionType.ABOVE);

					types.add(type);
			}

			if(scoreFunction.equals(CruxConstants.SCORE_FUNCTION_BOTH)) {
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName(TIDE_ANNOTATION_TYPE_COMBINED_PVALUE);
				type.setDescription("Combined p-value from xcorr and residence evidence scoring.");
				type.setFilterDirection(FilterDirectionType.BELOW);

				types.add(type);
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( TIDE_ANNOTATION_TYPE_DELTACN );
				type.setDescription( "The normalized difference in XCorr for this PSM relative to the next ranked PSM for the same spectrum and charge. The denominator for normalization is the minimum of the current XCorr and 1.0. If exact-p-value=T, then the difference is computed between -log(p-value) rather than XCorr, and no normalization is applied." );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				
				types.add( type );
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( TIDE_ANNOTATION_TYPE_DELTALCN );
				type.setDescription( "Similar to delta_cn, except that the difference is computed with respect to the lowest reported XCorr score for a given spectrum and charge state." );
				type.setFilterDirection( FilterDirectionType.ABOVE );
				
				types.add( type );
			}

			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( TIDE_ANNOTATION_TYPE_HIT_RANK );
				type.setDescription( "The rank of this PSM for this scan. Rank 1 means highest scoring hit." );
				type.setFilterDirection( FilterDirectionType.BELOW );
				type.setDefaultFilterValue( BigDecimal.valueOf( 1 ) );

				types.add( type );
			}

			if(wasSpComputed != null && wasSpComputed) {
				{
					FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
					type.setName(TIDE_ANNOTATION_TYPE_SPSCORE);
					type.setDescription("Score indicating how well theoretical and actual peaks matched.");
					type.setFilterDirection(FilterDirectionType.ABOVE);

					types.add(type);
				}

				{
					FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
					type.setName(TIDE_ANNOTATION_TYPE_SPRANK);
					type.setDescription("The rank of this peptide match for this spectrum based on Sp Score");
					type.setFilterDirection(FilterDirectionType.BELOW);

					types.add(type);
				}
			}
			
		}

		else if( programName.equals( Constants.PROGRAM_NAME_PERCOLATOR ) ) {
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( PERCOLATOR_ANNOTATION_TYPE_QVALUE );
				type.setDescription( "Q-value" );
				type.setFilterDirection( FilterDirectionType.BELOW );
				type.setDefaultFilterValue( BigDecimal.valueOf( 0.05 ) );
	
				types.add( type );
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( PERCOLATOR_ANNOTATION_TYPE_PVALUE );
				type.setDescription( "P-value" );
				type.setFilterDirection( FilterDirectionType.BELOW );
	
				types.add( type );
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( PERCOLATOR_ANNOTATION_TYPE_PEP );
				type.setDescription( "Posterior error probability" );
				type.setFilterDirection( FilterDirectionType.BELOW );
	
				types.add( type );
			}
			
			{
				FilterablePsmAnnotationType type = new FilterablePsmAnnotationType();
				type.setName( PERCOLATOR_ANNOTATION_TYPE_SVMSCORE );
				type.setDescription( "SVN Score from kernel function" );
				type.setFilterDirection( FilterDirectionType.ABOVE );
	
				types.add( type );
			}
		}

		
		return types;
	}
	
	
}
