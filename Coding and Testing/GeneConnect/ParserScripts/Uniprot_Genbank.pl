#! /usr/bin/perl
use strict;
use warnings;

#To indicate the annotation types
use constant DIRECT => '1';
use constant INFERRED => '2';
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



if($inputFile =~/.*\.gz/)
{


     my @filename_ext = split (/\.gz/,$inputFile);
     system("gunzip $inputFile");
     $inputFile=$filename_ext[0];
}
else {}

open(READ,"$inputFile")|| die "cannot find file!\n";

#File handle for output file
open(WRITE1, ">$configParams{BASEDIR}/$outputFile");

print WRITE1 "uniprot_genbank.txt\ngenbank_uniprot.txt\n";


#File handles for the link files
open(WRITE,">$configParams{BASEDIR}/uniprot_genbank.txt");

open(WRITE2,">$configParams{BASEDIR}/genbank_uniprot.txt");

print WRITE "LOAD DATA INFILE * APPEND INTO TABLE UNIPROT_GENBANKPROTEIN_U FIELDS TERMINATED BY '###' (UGE_UNIPROTKBID, UGE_GENBANKPROTEINID, UGE_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";
print WRITE2 "LOAD DATA INFILE * APPEND INTO TABLE GENBANKPROT_UNIPROT_U FIELDS TERMINATED BY '###' (GPU_GENBANKPROTID, GPU_UNIPROTID, GPU_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";

my $temp;
my @genbank;

while(<READ>)
{
#reading line by line
	if($_=~/(.*)\t(.*)\t.*/)
	{
#regular expression matching the line in the tab seperated input file,anything matching 1st (.*) 
#comes in $1 and so on.
	$temp = $2;
#since we dont need version numbers but only accession numbers , split by '.'		
	@genbank =split(/\./,$temp);
	print WRITE "$1".DELIMITER."$genbank[0]".DELIMITER.DIRECT."\n";
	print WRITE2 "$genbank[0]".DELIMITER."$1".DELIMITER.INFERRED."\n";
	}
}

close(READ);
close(WRITE1);
close(WRITE);
close(WRITE2);