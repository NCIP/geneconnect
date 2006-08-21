
#! /usr/bin/perl

use strict;
use warnings;
use File::Copy;
use FetchParams;

# GeneConnect server (Java) sends a properties file
# Read this properties file and store it in a hash
# first arguement is a -f; collect it and ignore it for now

my $optionalArgument = shift;
my $configFile = shift;
# read contents of config file into a hash by using module FetchParams
my %configParams = %{FetchParams::getParams($configFile)};

#The name of file to be parsed is obtained from the hash.
my $file = ($configParams{INPUTFILE});
my $inputfilewthpath;

if($file =~/.*\.gz/)
{

        my @filename_ext = split (/\.gz/,$file);

        system("gunzip $configParams{BASEDIR}/$file");

        $inputfilewthpath = ($configParams{BASEDIR}) . "\/" . $filename_ext[0];

}
else
{
	$inputfilewthpath  = ($configParams{BASEDIR}) . "\/" . $file;
}



my $propertiesfile ="properties_blastdestnensmblprot.txt";
my %properties = %{FetchParams::getParams($propertiesfile)};

my $isdbtypeprotein=($properties{IsDBTypeProtein});
my $dbname=($properties{DBName});

#formatdb the fasta input file with dbname and type
system("formatdb -i $inputfilewthpath -p $isdbtypeprotein -n $dbname ");

 move("$dbname\.pin","$configParams{BASEDIR}");
 move("$dbname\.phr","$configParams{BASEDIR}");
 move("$dbname\.psq","$configParams{BASEDIR}");

