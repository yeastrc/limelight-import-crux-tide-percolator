Crux Tide + Percolator to limelight XML Converter
=======================================

Use this program to convert the results of a Crux Tide + Percolator analysis to
limelight XML suitable for import into the limelight web application. Requires
PepXML output and that the Percolator output be represented as XML
(see --pout-output option in Crux).

How To Run
-------------
1. Download the [latest release](https://github.com/yeastrc/limelight-import-crux-tide-percolator/releases).
2. Run the program ``java -jar cruxTide2LimelightXML.jar`` with no arguments to see the possible parameters. Requires Java 8.

Command line documentation
---------------------------

NOTE: Tide currently contains a couple of issues in its pepxml output. Fixes to tide are currently pending; however,
you may run the following *nix commands to "fix" the pepxml so that it may be processed by this
converter:

In the crux-output directory, create a backup of your tide-search.target.pep.xml file. The run the following commands:

```
perl -p -i -e 's/num_tol_term=\"\"/num_tol_term=\"2\"/g' tide-search.target.pep.xml
perl -p -i -e 's/date=\".+?\" /date=\"2020-01-07T17:05:18\" /g' tide-search.target.pep.xml
perl -0777 -p -i -e 's/<\/modification_info>\n<modification_info.+//g' tide-search.target.pep.xml
```

Once that's done you may use the converter as:

Usage: java -jar cruxTide2LimelightXML.jar -d path -f path -o path

Example: java -jar cruxTide2LimelightXML.jar
                                       -f /path/to/fasta.fa
                                       -d /path/to/crux-output
                                       -o ./crux.limelight.xml

```
Options:
    -f	[Required] Path to FASTA file used in the experiment.
    -d	[Required] Path to the output directory for Crux, usually called "crux-output"
	    Contains percolator XML output and pepXML output from Tide.
    -o  [Required] Path you to which you would like to output the limelight XML.
```
