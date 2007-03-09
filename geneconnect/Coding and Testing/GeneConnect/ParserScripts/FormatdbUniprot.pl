#! /usr/bin/perl

#use lib '/usr/lib/perl5/site_perl/5.8.8';
#use strict;
use warnings;

use Bio::Seq;
use Bio::SeqIO;
use Bio::SeqIO::fasta;
use FetchParams;
use File::Copy;

# GeneConnect server (Java) sends a properties file
# Read this properties file and store it in a hash

# first arguement is a -f; collect it and ignore it for now

my $optionalArgument = shift;

my $configFile = shift;

# read contents of config file into a hash by using module FetchParams

my %configParams = %{FetchParams::getParams($configFile)};

#The name of file and type of database to be parsed is obtained from the hash.

my $inputFile = ($configParams{INPUTFILE});

my @arrayofinputfile = @$inputFile;



#reading parameters specific for this parser from the parser specific properties file
#and putting them in a hash

my $propertiesfile ="properties_blastdestnuniprot.txt";
my %properties = %{FetchParams::getParams($propertiesfile)};

#reading the database type, db name, uniprot name to primary accession mapping file and list of organisms
#from this parameter file

my $isdbtypeprotein=($properties{IsDBTypeProtein});
my $organism=($properties{Organism});

#my $uniaccmappingfile=($properties{UniprotPrimaryAccMappingFile});

my @arrayfororganism = @$organism;

#creating a hash of organism name and file name as key-value pair
my %orgnameandfile=();

foreach my $orgs (@arrayfororganism)
{
    my $temp="uniprot_$orgs".".fasta";
    $orgnameandfile{ $orgs } = $temp;
}

#opening organism specific fasta files and writing the names of these fasta files into an array
my @arrayoffastafiles=();

foreach my $org (@arrayfororganism)
{
     open($org,">>uniprot_" .$org . ".fasta");
     push(@arrayoffastafiles,"uniprot_" .$org . ".fasta");
}

#creating a hash from the uniprot name to primary accsn file
#containing uniprot name and primary accsn as key value pair

my @files_unzipped;
my $inputfilepath;
foreach my $file (@arrayofinputfile)
{

if($file =~/.*\.gz/)
	{
	
	my @filename_ext = split (/\.gz/,$file);
	
		system("gunzip $configParams{BASEDIR}/$file");
		$inputfilepath = ($configParams{BASEDIR}) . "\/" . $filename_ext[0];
    				
	}
else
        {
		$inputfilepath = ($configParams{BASEDIR}) . "\/" . $file;
    	
    	}
    	
    	
    my $Seq_in = Bio::SeqIO ->new(-format => 'Fasta', -file => $inputfilepath);
    while(my $query = $Seq_in->next_seq())
    {
         
         my $accession=$query->display_id;
         
         
         my $sequence=$query->seq;
         my @organism_acc=split(/_/,$accession);
         my $orgformacession=$organism_acc[1];
         my $desc = $query-> desc;
        
	#Taking the primary accession from the description line in to $primary_accn[1]      
         my @acc= split(/\)/,$desc);
         my @primary_accn = split(/\(/,$acc[0]);
         
         #if organism name exists in the orgnameandfile hash containing organism name and their fasta file names 
         if (exists  $orgnameandfile{ $orgformacession })
         {
             
                  
                  
                  #printing the primary accesion number and sequence in organism specific fasta files
                  print $orgformacession ">$primary_accn[1]\n$sequence\n";
             
            
               
            
        }#if 
         
    }#while next seq 


}#for each input file

foreach my $fasta (@arrayoffastafiles)
{

	#formatdb the fasta input files

	system("formatdb -i $fasta -p $isdbtypeprotein -o T");
	move("$fasta\.pin","$configParams{BASEDIR}");
	move("$fasta\.phr","$configParams{BASEDIR}");
	move("$fasta\.psq","$configParams{BASEDIR}");

	
}

