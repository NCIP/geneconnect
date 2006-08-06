#! /usr/bin/perl
use strict;
use warnings;

#to indicate the type of annotation,here its direct
use constant DIRECT => 'direct_annotation';
use constant DELIMITER => '###';
use FetchParams;

# GeneConnect server (Java) sends a properties file
# Read this properties file and store it in a hash
# first arguement is a -f; collect it and ignore it for now

my $optionalArgument = shift;
my $configFile = shift;

# read contents of config file into a hash by using module
my %configParams = %{FetchParams::getParams($configFile)};

#The name of file to be parsed is obtained from the hash.
my $inputFile = ($configParams{BASEDIR}) . "\/" . ($configParams{INPUTFILE});

# Open the file into which to write all 'output file names' created by
# this parser.
my $outputFile = $configParams{OUTPUTFILE};

open(READ,"$inputFile")|| die "cannot find file!\n";
open(WRITE1, ">../$outputFile");

print WRITE1 "uniprot_refseq.txt\n";
open(WRITE,">../uniprot_refseq.txt");

print WRITE "LOAD DATA INFILE * APPEND INTO TABLE UNIPROT_REFSEQPROTEIN FIELDS TERMINATED BY '###' (URE_UNIPROTKBID, URE_REFSEQPROTEINID, URE_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";

my $temp;
my @refseq;

while(<READ>)
{
#reading line by line
	if($_=~/(.*)\t(.*)\t.*/)
#regular expression matching the line in the tab seperated input file,anything matching 1st (.*) 
#comes in $1 and so on.
	{
	$temp = $2;
#since we dont need version numbers but only accession numbers , split by '.'	
	@refseq =split(/\./,$temp);
      	print WRITE "$1".DELIMITER."$refseq[0]".DELIMITER.DIRECT."\n";
	}
}

close(READ);
close(WRITE1);
close(WRITE);