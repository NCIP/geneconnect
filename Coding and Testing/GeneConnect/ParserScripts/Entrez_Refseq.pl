#! /usr/bin/perl
use strict;
use warnings;

#to indicate the type of annotation,here its direct
use constant DIRECT => '1';
use constant DELIMITER => '###';
use FetchParams;
use Config::Simple;

# GeneConnect server (Java) sends a properties file
# Read this properties file and store it in a hash
# first arguement is a -f; collect it and ignore it for now

my $optionalArgument = shift;
my $configFile = shift;

# read contents of config file into a hash by using module
my %configParams = %{FetchParams::getParams($configFile)};

#The name of file to be parsed is obtained from the hash.
#my $inputFile = ($configParams{BASEDIR}) . "\\" . ($configParams{INPUTFILE});

my $inputFile = ($configParams{BASEDIR}) . "\/" . ($configParams{INPUTFILE});

# $basedir=($configParams{basedir});

# Open the file into which to write all 'output file names' created by
# this parser.
my $outputFile = $configParams{OUTPUTFILE};

open(WRITE4, ">$configParams{BASEDIR}/$outputFile");
print WRITE4 "entrezgene_refseqmrna.txt\nrefseqmrna_refseqprotein.txt\n";

if($inputFile =~/.*\.gz/)
{


     my @filename_ext = split (/\.gz/,$inputFile);
     system("gunzip $inputFile");
     $inputFile=$filename_ext[0];
}
else {}


open(READ,"$inputFile") || die "cannot open filei!\n";
open(WRITE,">$configParams{BASEDIR}/entrezgene_refseqmrna.txt");
open(WRITE1,">$configParams{BASEDIR}/refseqmrna_refseqprotein.txt");

print WRITE "LOAD DATA INFILE * APPEND INTO TABLE ENTREZGENE_REFSEQMRNA_U FIELDS TERMINATED BY '###' (ENR_GENEID, ENR_REFSEQMRNAID, ENR_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";

print WRITE1 "LOAD DATA INFILE * APPEND INTO TABLE REFSEQMRNA_REFSEQPROTEIN_U FIELDS TERMINATED BY '###' (RER_REFSEQMRNAID, RER_REFSEQPROTID, RER_LINKTYPE)" . "\n" . "BEGINDATA" . "\n";


while (<READ>)
{
#reading line by line
chomp($_);
#input file is tab seperated with columns of entrez gene id ,refseqmrna id and refseq protein id
my @all_id=split(/\t/,$_);
my $entrezgene=$all_id[1];
my $refseqmrna=$all_id[3];
my $refseqprtn=$all_id[5];

#since we requie only version numbers
my @acc_mrna=split(/\./,$refseqmrna);
my @acc_prtn=split(/\./,$refseqprtn);

#to make sure that refseq mrna id is present otherwise the file has a '-' in place of refseq mrna id
	if ($refseqmrna ne '-')
	{
#mapping both ways (entrezgene <-> refseqmrna) and printing in different files
	print WRITE "$entrezgene".DELIMITER."$acc_mrna[0]".DELIMITER.DIRECT."\n";
	
	}
#similarly to make sure that refseq protein id is available. 	
	if (($refseqmrna ne '-') and ($refseqprtn ne '-'))
	{
#mapping both ways ($refseqmrna <-> refseqmrna) and printing in different files
	print WRITE1 "$acc_mrna[0]".DELIMITER."$acc_prtn[0]".DELIMITER.DIRECT."\n";
	
	}
}

close(WRITE4);
close(READ);
close(WRITE);
close(WRITE1);