package org.yeastrc.limelight.xml.tide.objects;

import java.io.File;

public class ConversionParameters {

    public ConversionParameters(File cruxOutputDirectory, File fastaFilePath, String outputFilePath, ConversionProgramInfo conversionProgramInfo) {
        this.cruxOutputDirectory = cruxOutputDirectory;
        this.fastaFilePath = fastaFilePath;
        this.outputFilePath = outputFilePath;
        this.conversionProgramInfo = conversionProgramInfo;
    }

    public File getCruxOutputDirectory() {
        return cruxOutputDirectory;
    }

    public File getFastaFilePath() {
        return fastaFilePath;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public ConversionProgramInfo getConversionProgramInfo() {
        return conversionProgramInfo;
    }

    private File cruxOutputDirectory;
    private File fastaFilePath;
    private String outputFilePath;
    private ConversionProgramInfo conversionProgramInfo;

}
