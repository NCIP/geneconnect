#!/usr/local/bin/perl

# Author: Robert Freimuth, July 2006
# Washington University

use strict;
use warnings;

use Data::Dumper;
use Frontier::Client;

use GeneConnect::Config qw( get_config_params );

#*****************************************************************************************

# get config data for the XML RPC server and set up the client

my $params_href = get_config_params();

my $server = Frontier::Client->new( 
									url   => join( '',
												   'http://', $params_href->{'server.IP'},
											 	   ':', $params_href->{'server.port'},
											 	   '/RPC2' )
								   )
	or die "Error creating Frontier::Client object\n";

#*****************************************************************************************

# test data goes here

my $sleeptime_secs = 5;

my $input_data = 
{
	SRCFILE => 'fastarefmrna10.fasta',
#	SRCFILE => 'fastarefprotein500.fasta',
	DESTINATIONDB => 'Homo_sapiens.NCBI36.apr.cdna.fa',
#	DESTINATIONDB => 'Homo_sapiens.NCBI36.apr.pep.fa',
	PATHS => {
				base => '/home/clususer/geneconnect/XML_RPC_system/persistent/',
				source => '/home/clususer/geneconnect/XML_RPC_system/persistent/',
				db => '/state/partition1/blastDB/',
				output => '/home/clususer/geneconnect/XML_RPC_system/persistent/',
			},
	BLAST => {
				blastType => 'blastn',
#				blastType => 'blastp',
				EValue => 1e-25,
				ScoringMatrix => 'BLOSUM62',
				GapOpenCost => undef,
				GapExtensionCost => undef,
				Dropoff => undef,
				WordSize => undef,
				seqType => 'nucleotide',
#				seqType => 'protein',
			},
	BLAST_FILTER => {
						PercentSequenceAlignment => 0.80,
					},
	NEEDLE => {
				GapOpenCost => undef,
                GapExtend => undef,
                Matrix => undef,
              },
	NEEDLE_FILTER => {
                 		PercentSequenceAlignment => 0.90,
	                 	SimilarityScore => 0.90,
	                 	IdentityScore => 0.90,
                 		NumberMisMatch => 2,
                 	},
	CONFIG => {
				numSeqsPerJob => 1,
                saveAlignOutput => 1,
                controlFileHeader => 'test header',
                controlFileColumnSeparator => '###',
              },
};

#*****************************************************************************************

# submit a job to the server and dump the return structure when it is done

mach_status();
job_queue_size();

print "\nTesting seq_align_pipeline:";

my $href = $server->call( 'seq_align_pipeline', $input_data );
my $job_id = $href->{JobID};

print " job ID = $job_id\n";


while( ! defined $href->{jobStatus} || $href->{jobStatus} ne 'complete' )
{
	sleep $sleeptime_secs;

	print "checking status of job $job_id ... ";
	$href = $server->call( 'check_job_status', { JobID => $job_id } );

	# count the number of complete/incomplete sub-jobs

	my $complete = 0;

	foreach my $filename ( keys %{ $href->{outputFiles} } )
	{
		$complete += ( $href->{outputFiles}{$filename}{jobStatus} eq 'complete' ? 1 : 0 );
	}

	print "$complete of ", scalar keys %{ $href->{outputFiles} }, " sub-jobs are complete\n";
}

print "job $job_id is complete:\n";
print Dumper( $href );

#*****************************************************************************************

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

