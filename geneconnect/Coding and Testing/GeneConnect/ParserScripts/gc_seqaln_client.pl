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
use Getopt::Long;
use Bio::SeqIO;

sub getTime;
sub connectToServer;

open(CLIENT_LOG_FILE, '>>' , 'XML_RPC_Client.log') or die "Can't create log file. Error opening log file.";
print CLIENT_LOG_FILE getTime(), "Client has been started..\n ";

#use GeneConnect::Config 0.03 qw( get_config_params );


# GeneConnect server (Java) sends a "-f config.txt"
# first arguement is -f
# second argument is -p

my ( $configFile, $propertiesfile );

GetOptions ( 'f:s' => \$configFile,
         	 'p:s' => \$propertiesfile);

print "\n\n  configFile : $configFile \n propertiesfile : $propertiesfile";

# "config.txt" is the properties file sent by the GC server
# It contains basedir, input file(s) and output file (file of file
# names)
# read contents of config file into a hash by using FetchParams module
my %configParams = %{FetchParams::getParams($configFile)};

#This is parser specific properties file containing Blast/Needle parameters
my %seqAlignParams = %{FetchParams::getParams($propertiesfile)};

#open the output file
open(OUTPUT_FILE,">$configParams{BASEDIR}/$configParams{OUTPUTFILE}");
open(FAILED_SEQUENCES_FASTA,">>$configParams{BASEDIR}/$configParams{INPUTFILE}_failed.fasta");

#global variables
my $href = 0 ;
my $machine_id;

print CLIENT_LOG_FILE getTime(), "Starting sequnce alignment operation...\n";

my $filename=join('',"$configParams{BASEDIR}","$configParams{INPUTFILE}");
print CLIENT_LOG_FILE getTime(), "Input filenames containing source sequences : ",$filename,"\n";
#move($filename,"/home/rakesh/geneconnect_test/ParserScripts") || die "Cannot move input file ", $filename , " to base directory..\n";

move($filename, ".") || die "Cannot move input file ", $filename , " to current directory ..\n";

my  $ftp = Net::FTP->new($seqAlignParams{xmlrpcserverip}, Debug => 0)
or die "Cannot connect to server ftp address $seqAlignParams{xmlrpcserverip}.\n"; 

$ftp->login($seqAlignParams{ftpusername},$seqAlignParams{ftppwd})
or die "Cannot login to server ftp site.\n", $ftp->message;

print CLIENT_LOG_FILE getTime(), "Connected and Logged into XML-RPC Server FTP site.\n";

$ftp->cwd($seqAlignParams{xmlrpcserverlocation})
or die "Cannot change working directory on ftp site of XML-RPC server.\n", $ftp->message;

$ftp->put($configParams{INPUTFILE})
or die "Error while copying input file to Server. FTP put operation failed.\n ", $ftp->message;

$ftp->quit;

print  "Copied input file " , $filename , " successfully to the server.\n";


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

                            controlFileColumnSeparator => $seqAlignParams{controlFileColumnSeparator}, 

				maxTimeAllowedForSubJob => $seqAlignParams{mins2waitforstuckjobs}} 

            };

print CLIENT_LOG_FILE getTime(), "Finished reading input config parameters.\n";

#Call to the XML RPC server
#Have to use the same port from where server is run

my $server = connectToServer(%seqAlignParams);

#calling subroutines to check the server status
mach_status();
job_queue_size();

#Call XML RPC Server with the above mentioned data structure and get a hash reference in return
$href = $server->call( 'seq_align_pipeline', $input_data ) or die "Cannot connect to server... XML RPC server call failed.\n";

#extract the job id from the hash reference returned by the server
#This job id would be used to poll the server to get back the results of sequence alignment

my $job_id = $href->{JobID};
print CLIENT_LOG_FILE getTime(), "Job execution has been started on server with the job ID : $job_id\n";

#calling subroutines to check the server status again
mach_status();
job_queue_size();

sleep(360);

print CLIENT_LOG_FILE getTime(), "Polling the server for getting back results...\n";

$server = connectToServer(%seqAlignParams);
print CLIENT_LOG_FILE getTime(), "Connected to the server\n";

#hashes to hold the completed and failed splitted file names
my %completed_files=();
my %failed_files=();
my $count=0;

#print "href jobstatus --->  $href->{jobStatus}\n";

#polling the server for getting back results
#continue polling the server until the job is done
while( ! defined $href->{jobStatus} || $href->{jobStatus} ne 'complete')
{ 
	#wait for 
	if($count == 0)
	{
		sleep 360;
	}
	else
	{
		sleep 240;
	}
	$count++;

   #polling the server again
   #print "\nchecking status of job $job_id ...\n ";
   print CLIENT_LOG_FILE getTime(), "Checking job status..";
   $href = $server->call( 'check_job_status', { JobID => $job_id } );
   print CLIENT_LOG_FILE getTime(), "Received status from server..";

   if (!defined $href || !exists $href->{jobStatus} || !defined $href->{jobStatus})
	{
		next;
	}

   #print $href;

   #for each splitted files check the status. If the status is complete then collect the control file and bring to the testserver through net ftp
    foreach my $filename ( keys %{ $href->{outputFiles} } )
	{
          my $controlfile= $href->{outputFiles}{$filename}{id_file};
          my $file=File::Basename::basename($controlfile);
          my $dir=File::Basename::dirname($controlfile); 
          my $onlyfilename=File::Basename::basename($filename);


          if(($href->{outputFiles}{$filename}{jobStatus} eq 'complete') && (! exists $completed_files{$file}) && (! exists $failed_files{$onlyfilename}))
			{					
				print CLIENT_LOG_FILE "\n\n\n", getTime(), "Sub-job has been completed...";         
				print CLIENT_LOG_FILE getTime(), "File Name : $file";
				print CLIENT_LOG_FILE getTime(), "Directory : $dir\n";

				my $mins=$href->{outputFiles}{$filename}{checkInMinutes};
				print CLIENT_LOG_FILE getTime(), "Time required to complete the job : $mins minutes\n";
			
				$ftp = Net::FTP->new($seqAlignParams{xmlrpcserverip}, Debug => 0)
				or die "Cannot connect to FTP site. IP Adddress : $seqAlignParams{xmlrpcserverip}";

				$ftp->login($seqAlignParams{ftpusername},$seqAlignParams{ftppwd})
				or die "Cannot login to ftp site. IP Adddress : $seqAlignParams{xmlrpcserverip}", $ftp->message;

				#when the job is done move the result files to the base directory
                #and also takes care of instances when the job is done after the first call itself

				#for each filenames i.e for each splitted fasta file get the output link files
				#write the name of these link files in the output file read from the config file
				#and move these files from their current location to the base directory as given by the config file
					
				$ftp->cwd("$dir")
				or die "Cannot change to working directory on ftp site of server.\n", $ftp->message;

				$ftp->get("$file")
				or die "Error while fetching output control file from server ftp site.\n", $ftp->message;
			
				move($file,"$configParams{BASEDIR}") || die "Can not move output control file to the base diretory.\n";
	 
				#copying the name of this file to the output file read from the config file
				print OUTPUT_FILE "$file"."\n";
				$ftp->quit;

				$server->call( 'update_split_source_files', $job_id, { $filename => { jobStatus => 'Finished' } } )
				or warn "Error updating machine file and job status!!\n";

                $completed_files{$file}=$file;

			}#if sub job status


#if the sub job status is not complete and if its stuck for more than the time given in the properties file then we will declare that job failed and will collect the failed fasta seqs and form a file which can be run later.the machine on which the job was running wud also be freed.				

			elsif((($href->{outputFiles}{$filename}{jobStatus} eq 'aligning') ||($href->{outputFiles}{$filename}{jobStatus} eq 'incomplete')) && (($href->{outputFiles}{$filename}{checkInMinutes})>=$seqAlignParams{mins2waitforstuckjobs}) && (! exists $failed_files{$onlyfilename}))                              
			{    
				print CLIENT_LOG_FILE "\n\n", getTime(), "Execution of sub-job ($onlyfilename) has been failed.\n";
				print CLIENT_LOG_FILE "Time taken : $href->{outputFiles}{$filename}{checkInMinutes}\n";
				print CLIENT_LOG_FILE "Status : $href->{outputFiles}{$filename}{jobStatus}\n";

				#storing the name of the failed files in a hash structure
				$failed_files{$onlyfilename}=$onlyfilename;

				#connecting to invishnu to get the failed files
				$ftp = Net::FTP->new($seqAlignParams{xmlrpcserverip}, Debug => 0)
				or die "Cannot connect to FTP site. IP Adddress : $seqAlignParams{xmlrpcserverip}";

				$ftp->login($seqAlignParams{ftpusername},$seqAlignParams{ftppwd})
				or die "Cannot login to Server ftp site.\n", $ftp->message;

				print CLIENT_LOG_FILE "Fetching file ($filename) from $href->{outputFiles}{$filename}{path} through FTP.\n";

				$ftp->cwd("$href->{outputFiles}{$filename}{path}")
				or die "Cannot change working directory on Server ftp site.\n", $ftp->message;

				$ftp->get("$filename")
				or die "Error while fetching file ($filename) from the server ftp.\n", $ftp->message;
				
				#taking only the filename
				#my $onlyfilename=File::Basename::basename($filename);
				
				#getting the failed fasta seqs and writing them into one file 
				open (FAILEDFILE, "$onlyfilename");
				my  @failedseqs= <FAILEDFILE>;
				close(FAILEDFILE);

				print FAILED_SEQUENCES_FASTA @failedseqs,"\n";
				unlink($onlyfilename);

				@failedseqs=();

				$ftp->quit;

				$machine_id = $href->{outputFiles}{$filename}{machine};

				#stuck jobs  stopped with the machine id
				$server->call( 'update_split_source_files', $job_id, { $filename => { jobStatus => 'Finished' } } )
				or warn "Error updating machine file and job status!!\n";

				print CLIENT_LOG_FILE "Terminated failed job ID $job_id ($onlyfilename on $machine_id\n";

				#my $new_num_running = $server->call( 'job_done_on_machine', $machine_id, $q_mgr );

				my $new_num_running = $server->call( 'job_done_on_machine', $machine_id, 'pipe_queue', $job_id, $filename, 'true') ||die "cant call job_done_on_machine";

				print CLIENT_LOG_FILE "Failed jobs stopped on $machine_id. Failed sequences will be rerun later.\n";

				} #if job failed
        }#for each
}#while


print CLIENT_LOG_FILE "\n\n", getTime(), "Failed sub-jobs : ", %failed_files,"\n";
print CLIENT_LOG_FILE "\n\n", getTime(), "Completed job files : ", %completed_files, "\n";

#moving the file with failed fasta  seqs to the base directory
print CLIENT_LOG_FILE "Moving failed fasta file ($configParams{INPUTFILE}_failed.fasta) to $configParams{BASEDIR}.. \n";
move("$configParams{INPUTFILE}_failed.fasta","$configParams{BASEDIR}");

#closing the file handler for the output file
close(OUTPUT_FILE);

print CLIENT_LOG_FILE "Shutting down the server..\n";

eval { $server->call( 'shut_down_server', 1 ); } ;
print CLIENT_LOG_FILE "\n\n" , getTime(), "Server has been  shut down successfully.\n";




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

sub getTime()
{
        my @months = qw(Jan Feb Mar Apr May Jun Jul Aug Sep Oct Nov Dec);
        my @weekDays = qw(Sun Mon Tue Wed Thu Fri Sat Sun);
        my ($second, $minute, $hour, $dayOfMonth, $month, $yearOffset, $dayOfWeek, $dayOfYear, $daylightSavings) = localtime();
        my $year = 1900 + $yearOffset;
        my $theTime = "\n[$hour:$minute:$second, $weekDays[$dayOfWeek] $months[$month] $dayOfMonth, $year] ";
        return $theTime;
}

sub connectToServer()
{
	my ($seqAlignParams) = @_;
	my $server = Frontier::Client->new(
									url   => join( '','http://',$seqAlignParams{xmlrpcserverip} , ':', $seqAlignParams{xmlrpcserverport},'/RPC2' )
                                )
						        or die "Error creating Frontier::Client object\n";
	return $server;
}
