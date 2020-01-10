package org.yeastrc.limelight.xml.tide.objects;

import java.io.File;

public class ConversionParameters {

    public ConversionParameters(File cruxOutputDirectory, File fastaFilePath) {
        this.cruxOutputDirectory = cruxOutputDirectory;
        this.fastaFilePath = fastaFilePath;
    }

    public File getCruxOutputDirectory() {
        return cruxOutputDirectory;
    }

    public File getFastaFilePath() {
        return fastaFilePath;
    }

    private File cruxOutputDirectory;
    private File fastaFilePath;

}
