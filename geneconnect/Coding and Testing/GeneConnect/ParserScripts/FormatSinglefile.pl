
#! /usr/bin/perl

#use lib '/usr/lib/perl5/site_perl/5.8.8';

use strict;
use warnings;

use FetchParams;

# GeneConnect server (Java) sends a properties file
# Read this properties file and store it in a hash
# first arguement is a -f; collect it and ignore it for now

my $optionalArgument = shift;
my $configFile = shift;
# read contents of config file into a hash by using module FetchParams
my %configParams = %{FetchParams::getParams($configFile)};

#The name of file to be parsed is obtained from the hash.
my $inputFile = ($configParams{BASEDIR}) . "\/" . ($configParams{INPUTFILE});

my $propertiesfile ="properties_blastDestnSingleFile.txt";
my %properties = %{FetchParams::getParams($propertiesfile)};

my $isdbtypeprotein=($properties{IsDBTypeProtein});
my $dbname=($properties{DBName});

#formatdb the fasta input file 

system("formatdb -i $inputFile -p $isdbtypeprotein -n $dbname -o T");