#!/usr/bin/perl

#This code is a XML RPC client that would be used to call the XML RPC Server
#The aim of the code is to call the server to perform sequence alignment(Blast and Needle)
#between two genomic Identifier source(e.g. GenBank mRNA and Ensembl Transcript) and return the link files
#When we call the server to perform sequence alignment , it first splits the source file into small fasta files and uses each
#of these small fasta files to perform sequence alignment.


use strict;
use warnings;

#Modules required for XML RPC setup

use Frontier::Client;

#Module required for moving files from current locatiopn to base directory and for handling the parameters
use File::Copy;
use FetchParams;
use Config::Simple;
use File::Basename;
use Net::FTP;
#use Getopt::Long;


open (TIME,">time_refseqmrna2ensprot_run.out");

print TIME "CLIENT START TIME--->",scalar localtime( time() ),"\n";

#use GeneConnect::Config 0.03 qw( get_config_params );


# GeneConnect server (Java) sends a "-f config.txt"
# first arguement is a -f; collect it and ignore it for now
#my $optionalArgument = shift;

# "config.txt" is the properties file sent by the GC server
# It contains basedir, input file(s) and output file (file of file
# names)
# read contents of config file into a hash by using FetchParams module

my $optionalArgument = shift;
my $configFile = shift;
my %configParams = %{FetchParams::getParams($configFile)};


#This is parser specific properties file containing Blast/Needle parameters

my $propertiesfile = "properties_seqaln_refseqmrna_enst.txt";
my %seqAlignParams = %{FetchParams::getParams($propertiesfile)};

#open the output file
open(FILE,">$configParams{BASEDIR}/$configParams{OUTPUTFILE}");
#ms{BASEDIR}/$configParams{OUTPUTFILE}");
open(FAILED,">>$configParams{INPUTFILE}_failed.fasta");



#global variables
my $href;
my $machine_id;

print "\nEntering seq_align_pipeline:\n";

my $filename=join('',"$configParams{BASEDIR}","$configParams{INPUTFILE}");
print "filename---> ",$filename,"\n";
move($filename,"/home/rakesh/geneconnect/ParserScripts") || die "cannot move\n";

my  $ftp = Net::FTP->new("im-vishnu.wustl.edu", Debug => 0)
or die "Cannot connect to im-vishnu.wustl.edu";

$ftp->login("clususer",'cl~user4vishnu')
or die "Cannot login ", $ftp->message;

print " imvishnu login done\n";

$ftp->cwd($seqAlignParams{xmlrpcserverlocation})
 or die "Cannot change working directory ", $ftp->message;

$ftp->put($configParams{INPUTFILE})
or die "put failed ", $ftp->message;

print  "put worked\n";

#data structure for holding the information read from the config file and properties file and going to be used in sequence al#ignment
my $input_data = 
             { SRCFILE => $configParams{INPUTFILE} ,

               DESTINATIONDB =>  $seqAlignParams{destinationdb} ,

               PATHS =>  {source => $seqAlignParams{xmlrpcserverlocation},

                            base => $seqAlignParams{xmlrpcserverlocation},

                            db =>  $seqAlignParams{pathdb},

                            output => $seqAlignParams{xmlrpcserverlocation}},

               BLAST => { blastType => $seqAlignParams{bBlasttype},

                           EValue => $seqAlignParams{bEvalue},

                           ScoringMatrix => $seqAlignParams{bScoringMatrix},

                           GapOpenCost => $seqAlignParams{bGapOpenCost},

                           GapExtensionCost => $seqAlignParams{bGapExtensionCost},

                           Dropoff => $seqAlignParams{bDropoff},

                           WordSize => $seqAlignParams{bWordSize},

                           seqType => $seqAlignParams{bseqType} },

               BLAST_FILTER => {PercentSequenceAlignment => $seqAlignParams{bPercentSequenceAlignment} },

               NEEDLE => {GapOpenCost => $seqAlignParams{nGapOpenCost},

                            GapExtend => $seqAlignParams{nGapExtend},

                            Matrix => $seqAlignParams{nMatrix}  },

               NEEDLE_FILTER => {PercentSequenceAlignment => $seqAlignParams{nPercentSequenceAlignment},

                                   SimilarityScore => $seqAlignParams{nSimilarityScore},

                                   IdentityScore => $seqAlignParams{nIdentityScore},

                                   NumberMisMatch => $seqAlignParams{nNumberMisMatch}  },
                                   

               CONFIG => {numSeqsPerJob => $seqAlignParams{numSeqsPerJob},

                            saveAlignOutput => $seqAlignParams{saveAlignOutput},

                            controlFileHeader => $seqAlignParams{controlFileHeader},

                            controlFileColumnSeparator => $seqAlignParams{controlFileColumnSeparator} } 

            };

#Call to the XML RPC server
#Have to use the same port from where server is run

my $server = Frontier::Client->new(

                               url   => join( '','http://',$seqAlignParams{xmlrpcserverip} ,                                                                                     ':', $seqAlignParams{xmlrpcserverport},'/RPC2' )
                                                                   )
        or die "Error creating Frontier::Client object\n";

#subroutines to check the server status
mach_status();
job_queue_size();

#Call XML RPC Server with the above mentioned data structure and get a hash reference in return

$href = $server->call( 'seq_align_pipeline', $input_data );

#extract the job id from the hash reference returned by the server
#This job id would be used to poll the server to get back the results of sequence alignment

my $job_id = $href->{JobID};

print "job id------> $job_id\n";


mach_status();
job_queue_size();

#hashes to hold the completed and failed splitted file names
my %filenames=();
my %failedfiles=();

#polling the server for getting back results

#continue polling the server until the job is done
while( ! defined $href->{jobStatus} || $href->{jobStatus} ne 'complete')
{ 
   
   #wait for 
   sleep 30;
   #polling the server again

   print "\nchecking status of job $job_id ...\n ";
   $href = $server->call( 'check_job_status', { JobID => $job_id } );

  #for each splitted files check the status. If the status is complete then collect the control file and bring to the testserver through net ftp
   foreach my $filename ( keys %{ $href->{outputFiles} } )
	{
          my $controlfile= $href->{outputFiles}{$filename}{id_file};
          my $file=File::Basename::basename($controlfile);
          my $dir=File::Basename::dirname($controlfile); 
          my $onlyfilename=File::Basename::basename($filename);
		print "$href->{outputFiles}{$filename}{jobStatus}";
          if(($href->{outputFiles}{$filename}{jobStatus} eq 'complete') && (! exists $filenames{$file}) && (! exists $failedfiles{$onlyfilename}))
			{					
                                        my $mins=$href->{outputFiles}{$filename}{checkInMinutes};
                                        print "mins------>$mins\n";
			
                                        print "inside sub job status\n";
	
					$ftp = Net::FTP->new("im-vishnu.wustl.edu", Debug => 0)
					or die "Cannot connect to im-vishnu.wustl.edu";

					$ftp->login("clususer",'cl~user4vishnu')
					or die "Cannot login ", $ftp->message;

					print " imvishnu login done\n";

					#when the job is done move the result files to the base directory
                    #and also takes care of instances when the job is done after the first call itself

					#for each filenames i.e for each splitted fasta file get the output link files
					#write the name of these link files in the output file read from the config file
					#and move these files from their current location to the base directory as given by the config file
					

					$ftp->cwd("$dir")
					or die "Cannot change working directory ", $ftp->message;

					$ftp->get("$file")
					or die "get failed ", $ftp->message;
			
					move($file,"$configParams{BASEDIR}") || die "cant move\n";
	 
					#copying the name of this file to the output file read from the config file
					print FILE "$file"."\n";

					$ftp->quit;

                                        $filenames{$file}=$file;


				}#if sub job status
#if the sub job status is not complete and if its stuck for more than the time given in the properties file then we will declare that job failed and will collect the failed fasta seqs and form a file which can be run later.the machine on which the job was running wud also be freed.				
        		 elsif(($href->{outputFiles}{$filename}{jobStatus} eq 'incomplete') && (($href->{outputFiles}{$filename}{checkInMinutes})>=$seqAlignParams{mins2waitforstuckjobs}))                              
		         {    
					print "in failes";

                                        #storing the name of the failed files in a hash structure
                                        $failedfiles{$onlyfilename}=$onlyfilename;

                                        #connecting to invishnu to get the failed files
                                        $ftp = Net::FTP->new("im-vishnu.wustl.edu", Debug => 0)
					or die "Cannot connect to im-vishnu.wustl.edu";

					$ftp->login("clususer",'cl~user4vishnu')
					or die "Cannot login ", $ftp->message;

					print " imvishnu login done\n";

                                        $ftp->cwd("$href->{outputFiles}{$filename}{path}")
					or die "Cannot change working directory ", $ftp->message;

					$ftp->get("$filename")
					or die "get failed ", $ftp->message;
                                       #taking only the filename
                                       my $onlyfilename=File::Basename::basename($filename);
                   #getting the failed fasta seqs and writing them into one file 
                   open (FAILEDFILE, "$onlyfilename");
                   my  @failedseqs= <FAILEDFILE>;
		    close(FAILEDFILE);

                   print FAILED @failedseqs,"\n";

		   @failedseqs=();

                   #getting the machine2filename.txt from imvishnu to check on which machine the failed splitted file was running
                  #the stuck /failed job will be stopped then
                   $ftp->cwd("$seqAlignParams{xmlrpcserverlocation}")
		   or die "Cannot change working directory ", $ftp->message;

                   $ftp->get("machine2filename.txt")
                   or die "get failed ", $ftp->message;

          	   open(CHECK,"machine2filename.txt");
                   while(<CHECK>)
					 {
                                                chomp $_;
						my @info = split(/\#\#\#/,$_);


      #finding the machine name for the failed fasta seqs in the machine2filename.txt file
						if ($info[0] eq $onlyfilename) 
						{
                          #storing the machine name in a variable
							$machine_id = $info[1];
							last;
						}


					 }#while
                  #stuck jobs  stopped with the machine id

                   $server->call( 'job_done_on_machine', $machine_id, 'pipe_queue' ) ||die "cant call job_done_on_machine";

                  print "failed jobs stopped on $machine_id\n\nFailed sequences will be rerun later\n";


				 } #if job failed
        }#for each

}#while


#moving the file with failed fasta  seqs to the base directory

move("$configParams{INPUTFILE}_failed.fasta","$configParams{BASEDIR}");

#closing the file handler for the output file
close(FILE);
print TIME "CLIENT END TIME--->",scalar localtime( time() ),"\n";
close (TIME);


#subroutines to check the job and machine status
#*********************************

sub mach_status
{
	my $result = $server->call( 'check_machine_status' );
	print $result;
}


sub job_queue_size
{
	my $result = $server->call( 'job_queue_size' );
	print $result;
}

