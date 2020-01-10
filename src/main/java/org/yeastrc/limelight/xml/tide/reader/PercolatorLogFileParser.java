package org.yeastrc.limelight.xml.tide.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PercolatorLogFileParser {

    /**
     * Attempt to get the version of Crux used. Will return "unknown" if it cannot be determined
     *
     * @param logFileInputStream
     * @return
     * @throws IOException
     */
    public static String getCruxVersionFromLogFile( InputStream logFileInputStream ) throws IOException {

        try (BufferedReader br = new BufferedReader( new InputStreamReader( logFileInputStream ) ) ) {

            for ( String line = br.readLine(); line != null; line = br.readLine() ) {

                // skip immediately if it's not a line we want
                if( !line.startsWith( "INFO: Crux version: " ) )
                    continue;

                String[] fields = line.split( "\\s" );
                return fields[ fields.length - 1 ];
            }
        }

        return "unknown";
    }

    /**
     * Attempt to get the version of percolator used. Returns "unknown" if it cannot be determined.
     *
     * @param logFileInputStream
     * @return
     * @throws IOException
     */
    public static String getPercolatorVersionFromLogFile( InputStream logFileInputStream ) throws IOException {

        try (BufferedReader br = new BufferedReader( new InputStreamReader( logFileInputStream ) ) ) {

            for ( String line = br.readLine(); line != null; line = br.readLine() ) {

                // skip immediately if it's not a line we want
                if( !line.startsWith( "INFO: Percolator version " ) )
                    continue;

                return line;
            }
        }

        return "unknown";
    }

    /**
     * Attempt to get the string used to denote decoy hits in this search. Will return null if none can be
     * found.
     *
     * @param logFileInputStream
     * @return
     * @throws Exception
     */
    public static String getDecoyPrefixStringFromLogFile( InputStream logFileInputStream ) throws IOException {

        try (BufferedReader br = new BufferedReader( new InputStreamReader( logFileInputStream ) ) ) {

            for ( String line = br.readLine(); line != null; line = br.readLine() ) {

                // skip immediately if it's not a line we want
                if( !line.startsWith( "INFO: percolator --" ) )
                    continue;

                Pattern p = Pattern.compile( "--protein-decoy-pattern (\\S+)");
                Matcher m = p.matcher(line);

                if(m.matches()) {
                    return m.group(1);
                }
            }
        }

        return null;
    }

}
