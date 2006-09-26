#! /usr/bin/perl

#use lib '/usr/lib/perl5/site_perl/5.8.8';
#use strict;
use warnings;

use Bio::Seq;
use Bio::SeqIO;
use Bio::SeqIO::fasta;
use FetchParams;
use File::Copy;

# GeneConnect server (Java) sends a config file
# Read this config file and store it in a hash

# first arguement is a -f; collect it and ignore it for now

my $optionalArgument = shift;

my $configFile = shift;

# read contents of config file into a hash by using module FetchParams

my %configParams = %{FetchParams::getParams($configFile)};

#The name of file and type of database to be parsed is obtained from the hash.

my $file = ($configParams{BASEDIR}) . "\/" .($configParams{INPUTFILE});

#reading parameters specific for this parser from the parser specific properties file
#and putting them in a hash

my $propertiesfile ="properties_blastsourcegenbankprotein.txt";
my %properties = %{FetchParams::getParams($propertiesfile)};

#reading the list of organisms
#from this parameter file

my $organism=($properties{Organism});
my @arrayfororganism = @$organism;

#creating a hash of organism name and file name as key-value pair
my %orgnameandfile=();

foreach my $orgs (@arrayfororganism)
{
    my $temp="genbank_protein_$orgs".".fasta";
    $orgnameandfile{ $orgs } = $temp;
}

#opening organism specific fasta files and writing the names of these fasta files into an array
my @arrayoffastafiles=();

foreach my $org (@arrayfororganism)
{
    my @species = split(/\s/,$org);
    my $org1 = "$species[0]_$species[1]";
    open($org,">>genbank_protein_" .$org1. ".fasta");
}

if($file =~/.*\.gz/)
		{
		
		my @filename_ext = split (/\.gz/,$file);
		
			system("gunzip $file");
			$file = $filename_ext[0];
		}
else
	        {
			    	
    		}

    my $Seq_in = Bio::SeqIO ->new(-format => 'Fasta', -file => $file);
print "file = $file\n";
    while(my $query = $Seq_in->next_seq())
    {

         my $accession=$query->display_id;
         my $sequence=$query->seq;
         my $desc = $query->desc;
         
         my @foraccn = split(/\|/,$accession);
         my @primary_accn =split(/\./,$foraccn[3]);

 
         my @fororg = split(/\[/,$desc);
         if (defined @fororg1)
         {
         my @fororg1 = split(/\]/,$fororg[1]);
         
         #print "$fororg1[0]\n";
         my $org_name=$fororg1[0];         
	 }
         else
         {
 		next;
         }  
        
         #if organism name exists in the orgnameandfile hash containing organism name and their fasta file names
         if (exists  $orgnameandfile{ $org_name } )
         {
                  #printing the primary accesion number and sequence in organism specific fasta files
                  print $org_name ">$primary_accn[0]\n$sequence\n";

         }###if
     }#while next seq

foreach my $org (@arrayfororganism)
 {
 	my @species = split(/\s/,$org);
 	my $filename = "genbank_protein_" . "$species[0]_$species[1]" . ".fasta" ;
 	move("$filename","$configParams{BASEDIR}");
 }
