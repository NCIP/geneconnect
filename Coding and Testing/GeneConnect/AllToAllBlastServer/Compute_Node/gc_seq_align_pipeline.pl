#!/usr/local/bin/perl

# This program...
#  It takes as
# input a job ID, input seq filename (full path) and the machine name it is being run on (passed on the cmd line).
# It also communicates with the GeneConnect XML-RPC server.

# Author: Robert Freimuth, July 2006
# Washington University

use warnings;
use strict;

use Bio::AlignIO;
use Bio::Factory::EMBOSS;
use Bio::SearchIO; 
use Bio::SeqIO;
use Bio::Tools::Run::StandAloneBlast;
use Data::Dumper;
use File::Basename;
use File::Path;
use File::Spec;
use File::Temp;
use File::Copy;
use Frontier::Client;
use Getopt::Long;

use GeneConnect::Config 0.03 qw( get_config_params );
use GeneConnect::Params 0.02;

use constant IDENTITY => '4';
use constant ALIGNMENT => '8';

our $VERSION = 0.02;

sub getProcessId;
sub getNeedleVersion;
sub connectToServer;

#*****************************************************************************************

# parse job id and mach id from cmd line
my ( $job_id, $q_mgr, $inpathfile, $machine_id , $config_pathfile_compute );

GetOptions ( 'job_id=s'  => \$job_id,
			 'qmgr=s'    => \$q_mgr,
			 'infile=s'  => \$inpathfile,
			 'config_file_compute:s' => \$config_pathfile_compute,
			 'mach_id=s' => \$machine_id );

my $params_href = GeneConnect::Config::get_config_params($config_pathfile_compute);

my $test_log_filename = File::Spec->catfile( $params_href->{'compute_nodes.working_directory'} , "Compute-Node-$machine_id.log" );
open(COMPUTE_LOG_FILE, '>>' ,$test_log_filename) or die "Can't create log file. Error opening $test_log_filename.";

my $skipped_id_filename = File::Spec->catfile( $params_href->{'compute_nodes.working_directory'} , "skippedid.txt" );
open(SKIPPED_ID_FILE,">>" , $skipped_id_filename) or die "Can't create skipped ID file. Error opening $skipped_id_filename.";

print COMPUTE_LOG_FILE "Compute Node Job has been started...\n";
print COMPUTE_LOG_FILE "\nStarting job ID $job_id (", File::Basename::basename( $inpathfile ), ") on $machine_id\n";

# create a server object for contacting the XML RPC server
# if new() fails and dies, machine won't ever be freed and job won't return
# need to identify orphaned job ids and rerun on a diff machine? (free the orig machine, too)
print COMPUTE_LOG_FILE "Connecting to the server..\n";
my $server = connectToServer($params_href);

my $processID = getProcessId(File::Basename::basename($inpathfile));
print COMPUTE_LOG_FILE "\n\nProcess Id for job $job_id : (", File::Basename::basename( $inpathfile ), ") on $machine_id is : $processID\n";

# call server to set the machine id and to get params for this job
$server->call( 'update_split_source_files', $job_id, { $inpathfile => { machine => $machine_id,
							jobStatus => 'aligning' , processID => $processID } } )
							or warn "Error updating machine file and job status!!\n";

my $record_href = $server->call( 'get_record_for_job_id', $job_id );
#print COMPUTE_LOG_FILE "** RECORDS HREF : %record_href\n";

# create a GeneConnect::Params object for the params to insulate this code from changes to
# the params data structure, then use that object to construct a params hash that is used
# in the code below (legacy system - should be refactored)
my $param_obj = GeneConnect::Params->new( $record_href->{params} );

my %params = (
				query => {
							pathfile => $inpathfile,
							filename => File::Basename::basename( $inpathfile ),
							file_format => 'fasta',
						 },
				blast => {
							program => $param_obj->blast_program,
							db_file => $param_obj->target_db,
							db_path => $param_obj->path_db,
							expectvalue => $param_obj->blast_evalue,
							min_align_len_pct => $param_obj->blast_filter_min_pct_max_seq_align,
							is_protein => $param_obj->blast_seqtype,
						 },
				needle => {
							min_align_len_pct => $param_obj->needle_filter_min_pct_max_align_len,
							min_align_identity => $param_obj->needle_filter_min_pct_ident,
							num_allowed_mismatches_per_kb => $param_obj->needle_filter_max_mismat_per_kb,
						  },
				logfile => {
							filename => 'pipeline_log.txt',
						   },
			  );

#print COMPUTE_LOG_FILE "** PARAMS HASH : %params\n";

# set the BLASTDB environment variable
$ENV{BLASTDB} = $params{blast}{db_path};

my $working_dir = $record_href->{working_dir};

print COMPUTE_LOG_FILE "\nWorking directory : ", $working_dir, ".\n";

# get the name of the working dir for this job and create it on the compute node
my $local_working_dir = File::Spec->catdir( $params_href->{'compute_nodes.working_directory'},
											( File::Spec->splitdir( $working_dir ) )[-1] );

print COMPUTE_LOG_FILE "\nLocal Working directory : ", $local_working_dir, ".\n";

#Create Loacl Wroking directory
if( ! -e $local_working_dir )
{
	my ( $createdpath ) = File::Path::mkpath( $local_working_dir );

	if( not defined $createdpath || ($createdpath ne $local_working_dir))
	{
	# SHOULD UPDATE SERVER WITH 'ERROR' FOR STATUS INSTEAD OF DIE
		die "Error creating $local_working_dir";
	}
	print COMPUTE_LOG_FILE "\nCreated working dir: $local_working_dir\n";
}
else
{
	print COMPUTE_LOG_FILE "\nWorking directory ($local_working_dir) already exists.\n";
}

my $needle_version = getNeedleVersion(File::Spec->catfile( $local_working_dir,"NeedleVersion.txt"));
print COMPUTE_LOG_FILE "\nNeedle Version : '$needle_version'\n";

my $gapopen = $param_obj->{NEEDLE}{GapOpenCost};
my $gapextend = $param_obj->{NEEDLE}{GapExtend};

my $local_input_pathfile = File::Spec->catfile( $local_working_dir, File::Basename::basename( $inpathfile ));
print COMPUTE_LOG_FILE "Copying input sequence file $inpathfile to  $local_input_pathfile\n";
copy ($inpathfile, $local_input_pathfile) or die "Error while copying input sequence file $inpathfile to  $local_input_pathfile\n";
#print COMPUTE_LOG_FILE "Copied successfully\n";

my $log_pathfile = File::Spec->catfile( $local_working_dir, $params{query}{filename} . "_$params{logfile}{filename}" );

# SHOULD UPDATE SERVER WITH 'ERROR' FOR STATUS INSTEAD OF DIE
open( LOG_FILE, '>', $log_pathfile ) or die "Error opening $log_pathfile$!";
print LOG_FILE "Parameters:\n";
print LOG_FILE Dumper( \%params );

my $output_file = $params{query}{filename} . "_pipeline_output.txt";
my $out_pathfile = File::Spec->catfile( $local_working_dir, $output_file );

# SHOULD UPDATE SERVER WITH 'ERROR' FOR STATUS INSTEAD OF DIE
open(OUT_FILE, '>', $out_pathfile ) or die "Error opening output file ($out_pathfile)$!";

my $control_file = $params{query}{filename} . "_control.txt";
my $ctrl_pathfile = File::Spec->catfile( $local_working_dir, $control_file );
print COMPUTE_LOG_FILE "\nCreating ouput control file $ctrl_pathfile..\n";

# SHOULD UPDATE SERVER WITH 'ERROR' FOR STATUS INSTEAD OF DIE
open( OUTPUT_CONTROL_FILE, '>', $ctrl_pathfile ) or die "Error opening control file ($ctrl_pathfile)$!";
print OUTPUT_CONTROL_FILE $param_obj->config_control_file_header, "\n";

# update the server with the location of all of the output files
my $server_ctrl_pathfile = File::Spec->catfile( $working_dir, $control_file );
my $server_out_pathfile = File::Spec->catfile( $working_dir, $output_file );

$server->call( 'update_split_source_files', $job_id, { $inpathfile => { log_file => $log_pathfile,
																	control_file => $server_ctrl_pathfile,
																	old_output_file => $server_out_pathfile } } )
				or warn "Error updating machine file for output files!!\n";


#************* THE FOLLOWING IS ALL STRAIGHT FROM BLAST_NEEDLE_TIMETRIALS_4.PL *********************

print OUT_FILE ( timestamp() . "\n" );
print OUT_FILE join( "\t", 'TIMESTAMP', scalar localtime( time() ) ), "\n";


#print OUT_FILE "skipping alignment section\n";
# add the '=pod' tag below to comment out the alignment section for testing on Windows
#=pod

# read in the file containing the query sequences

#print COMPUTE_LOG_FILE "** 5 ** BLAST input file (params{query}{pathfile} : $params{query}{pathfile}\n";

#my $query_seqio_obj = Bio::SeqIO->new( -file => $params{query}{pathfile},
#									   -format => $params{query}{file_format} );

my $query_seqio_obj = Bio::SeqIO->new( -file => $local_input_pathfile,
									   -format => $params{query}{file_format} );
# create blast objects
# per standaloneblast docs: Note that for improved script readibility one can modify
# the name of the BLAST parameters as desired as long as the initial letter (and case)
# of the parameter are preserved.  BLAST parameters can be changed and/or examined at
# any time after the factory has been created.  StandAloneBlast uses the same single-
# letter, case-sensitive parameter names as the actual blast program.

# bug in StandAloneBlast - 'n' is not in @BLASTALL_PARAMS (can't run blastall with
# megablast turned on) - will try to compensate by increasing word size from 11 to 28 (didn't work)

# make sure -v and -b match (will get bioperl error if missing alignments for some seqs
# listed in the summary table - hit object created but without hsps (length_aln method))
#  -v  Number of database sequences to show one-line descriptions for (V) [Integer] default = 500
#  -b  Number of database sequence to show alignments for (B) [Integer] default = 250

#my $blastall_factory = Bio::Tools::Run::StandAloneBlast->new(
#							p_program  => $params{blast}{program},
#							d_database => $params{blast}{db_file},
##							'outfile' => 'blastall.out',
#						    F_filter_query_seq => 'T',
#						    W_word_size => 3,   #bWordSize=undef
#                                 X_Drop_off => 25, #<<bDropoff=undef>>
#                                 G_Gap_Open_Cost => 11,  #<<bGapOpenCost=undef>>
#                                 E_Gap_Extension_Cost => 1, #<<bGapExtensionCost=undef>>
##                                M_Matrix => 'undef',
##						    n_use_megablast => 'T',
#						    b_num_alignments_shown => 500,
#						    v_num_descriptions_shown => 500,
#						    e_expectvalue => $params{blast}{expectvalue} );


my $blastall_factory = Bio::Tools::Run::StandAloneBlast->new(
							p_program  => $params{blast}{program},
							d_database => $params{blast}{db_file},
#							'outfile' => 'blastall.out',
						    F_filter_query_seq => 'T',
						    W_word_size => $param_obj->{BLAST}{WordSize},   #bWordSize=undef
                                 X_Drop_off => $param_obj->{BLAST}{Dropoff}, #<<bDropoff=undef>>
                                 G_Gap_Open_Cost => $param_obj->{BLAST}{GapOpenCost},  #<<bGapOpenCost=undef>>
                                 E_Gap_Extension_Cost => $param_obj->{BLAST}{GapExtensionCost}, #<<bGapExtensionCost=undef>>
#                                M_Matrix => 'undef',
#						    n_use_megablast => 'T',
						    b_num_alignments_shown => 500,
						    v_num_descriptions_shown => 500,
						    e_expectvalue => $params{blast}{expectvalue} );


print COMPUTE_LOG_FILE "\nW_word_size : $param_obj->{BLAST}{WordSize}\n";
print COMPUTE_LOG_FILE "\nX_Drop_off : $param_obj->{BLAST}{Dropoff}\n";
print COMPUTE_LOG_FILE "\nG_Gap_Open_Cost : $param_obj->{BLAST}{GapOpenCost}\n";
print COMPUTE_LOG_FILE "\nE_Gap_Extension_Cost : $param_obj->{BLAST}{GapExtensionCost}\n";
print COMPUTE_LOG_FILE "\nGapOpenCost : $gapopen";
print COMPUTE_LOG_FILE "\nGapExtend : $gapextend\n";

# run each query seq through the pipeline

my $num_blast_jobs = 0;
my %seqs;

while( my $query_seq_obj = $query_seqio_obj->next_seq() )
{
	#my $query_seq_id = $query_seq_obj->accession_number();
	my $query_seq_id = $query_seq_obj->display_id();
	$query_seq_id =~ tr/|/_/; # change pipes into underscores if using display_id

	$query_seq_id = extract_id($query_seq_id);
	#next unless $query_seq_id eq 'NM_030569';

	my $query_len = $query_seq_obj->length();

	print LOG_FILE ( '-' ) x 50, "\n";
	print LOG_FILE "BLASTing $query_seq_id\n";
	print LOG_FILE ( timestamp() . "\n" );
#	print "BLASTing $query_seq_id\n";

	# perform blast search

	$blastall_factory->outfile( File::Spec->catfile( $local_working_dir, $query_seq_id . '_blast.out' ) );
#	$blastall_factory->outfile( File::Spec->catfile( $working_dir, $query_seq_id . '_blast.out' ) );
	my $blast_obj = $blastall_factory->blastall( $query_seq_obj );
	$num_blast_jobs++;

	my $num_hits_pre_blast_filter = 0;
	my @filtered_blast_hits;

	while( my $result_obj = $blast_obj->next_result() )
	{
		$num_hits_pre_blast_filter += $result_obj->num_hits();

		HIT:
		while( my $hit_obj = $result_obj->next_hit() )
		{
			my $hit_acc_num = $hit_obj->accession();
			print OUT_FILE join( "\t", 'BLAST_hit', $query_seq_id, $hit_acc_num ), "\n";

			# get the total length of the aligned region for query or sbjct seq
			# (includes all HSPs, calculated after tiling)

#			my $align_len = $hit_obj->length_aln( 'query' );
			my $align_len = eval{ $hit_obj->length_aln( 'query' ) };

			if( $@ )
			{
# THIS IS A MAJOR PROBLEM AND NEEDS TO BE CORRECTED
# INSTEAD OF SKIPPING THE HIT, IT SHOULD SKIP THE REST OF THE BLAST FILTER AND JUST GO TO NEEDLE
# THIS SPECIAL CASE WON'T BE NEEDED IF/WHEN BIOPERL FIXES THE BUG
#				print LOG_FILE "Caught fatal error in length_aln method - hit $hit_acc_num for query $query_seq_id (machine $machine_id):\n$@\n";
#				print LOG_FILE "   faking alignment length so it passes the BLAST length filter\n";
#				$align_len = int( $params{blast}{min_align_len_pct} * $query_len ) + 1;
				print LOG_FILE "Caught fatal error in length_aln method - skipping hit $hit_acc_num for query $query_seq_id (machine $machine_id):\n$@\n";
				print SKIPPED_ID_FILE "$query_seq_id\n";
				next;
			}

			# determine if this hit meets filter criteria

			my $blast_min_len_thresh = int( $params{blast}{min_align_len_pct} * $query_len );
			print LOG_FILE "Alignment length must be >= $blast_min_len_thresh ($params{blast}{min_align_len_pct} * $query_len)\n";

			if( $align_len >= $blast_min_len_thresh )
			{
				print LOG_FILE $hit_obj->name(), "[", $hit_acc_num, "] met filter criteria (len = $align_len)\n";
				print OUT_FILE join( "\t", 'PASSED_BLAST_align_len', $query_seq_id, $hit_acc_num ), "\n";

				# since needle req's 90% length of query, seq must be at least
				# that long or can skip needle altogether - get seq from blast db

				my $hit_filename = File::Spec->catfile( $local_working_dir, $hit_acc_num . '.fasta' );
#				my $hit_filename = File::Spec->catfile( $working_dir, $hit_acc_num . '.fasta' );

				extract_seq_from_blast_db( $hit_acc_num, $hit_filename );

				my $sbjct_seqio_obj  = Bio::SeqIO->new( -file => $hit_filename , '-format' => 'fasta' );
				my $sbjct_seq_obj = $sbjct_seqio_obj->next_seq();

				# do a quick string comparison to determine if the seqs are identical

				if( lc( $query_seq_obj->seq ) eq lc( $sbjct_seq_obj->seq ) )
				{
					print LOG_FILE "seqs are identical\n";
					print OUT_FILE join( "\t", 'PASSED_string_identity', $query_seq_id, $hit_acc_num ), "\n";

					push( @filtered_blast_hits, {
													name => $hit_obj->name(),
													acc_num => $hit_acc_num,
													strand => 0,
													filename => $hit_filename,
													identical => 1,
												} );

					next HIT;
				}


				my $needle_min_len_thresh = int( $params{needle}{min_align_len_pct} * $query_len );

				if( $sbjct_seq_obj->length() < $needle_min_len_thresh )
				{
					print LOG_FILE "seq is too short (", $sbjct_seq_obj->length(), ") to meet needle length criteria (", $needle_min_len_thresh, ") - doesn't matter what blast hit len is\n";
					print OUT_FILE join( "\t", 'FAILED_assert_sbjct_len', $query_seq_id, $hit_acc_num ), "\n";
				}
				else
				{ 
					# check strand orientation so can revcomp if necessary when do needle

					my ( $query_strand, $sbjct_strand ) = $hit_obj->strand();
					my $revcomp_hit = $query_strand == $sbjct_strand ? 0 : 1;

					if( $revcomp_hit )
					{
						print LOG_FILE "*** hit is on opposite strands! ***\n";
					}

					push( @filtered_blast_hits, {
													name => $hit_obj->name(),
													acc_num => $hit_acc_num,
													strand => $revcomp_hit,
													filename => $hit_filename,
													identical => 0,
												} );

					print OUT_FILE join( "\t", 'PASSED_assert_sbjct_len', $query_seq_id, $hit_acc_num ), "\n";
				}
			}
			else
			{
				print LOG_FILE $hit_obj->name(), "[", $hit_acc_num, "] failed to meet filter criteria (len = $align_len)\n";
				print OUT_FILE join( "\t", 'FAILED_BLAST_align_len', $query_seq_id, $hit_acc_num ), "\n";
			}
		} # while( my $hit_obj
	} # while( my $result_obj

	my $num_hits_post_blast_filter = scalar @filtered_blast_hits;

	print LOG_FILE "number of hits pre blast filter = $num_hits_pre_blast_filter\n";
	print LOG_FILE "number of hits post blast filter = $num_hits_post_blast_filter\n";
	print OUT_FILE join( "\t", 'num_hits_pre_blast_filter', $query_seq_id, $num_hits_pre_blast_filter ), "\n";
	print OUT_FILE join( "\t", 'num_hits_post_blast_filter', $query_seq_id, $num_hits_post_blast_filter ), "\n";

	my ( @identical_hits, @filtered_hits );

	if( $num_hits_post_blast_filter == 0 )
	{
		print LOG_FILE "No blast hits were found, or none made it through the filter\n";
	}
	else
	{
		# need to do needle for each hit

		print LOG_FILE "will do $num_hits_post_blast_filter needle alignments\n";

		# create a bunch of temp files - flag for deletion when program ends
		# if too many files accumulate during the run, should unlink each one
		# manually at end of needle (remove UNLINK param)

		# write the query sequence to a file

# DON'T UNLINK IF WANT TO KEEP ALIGNS
		my ( $needle_query_fh, $needle_query_filename ) =
			File::Temp::tempfile( $query_seq_id . 'XXXX', DIR => $local_working_dir, UNLINK => 1 );
#			File::Temp::tempfile( $query_seq_id . 'XXXX', DIR => $working_dir, UNLINK => 1 );

		print LOG_FILE "creating query file ($needle_query_filename)\n";

		my $seqio_obj_q = Bio::SeqIO->new( '-fh' => $needle_query_fh,
										   '-format' => 'fasta' );

		$seqio_obj_q->write_seq( $query_seq_obj );

		close $needle_query_fh;

		# perform a needle alignment for each seq that made it through the
		# blast filter, add the results to %aligns and apply the filter criteria

		my $top_score = 0;
		my ( @filtered_needle_hits, %aligns );

		foreach my $href ( @filtered_blast_hits )
		{
			my $seqid = $href->{acc_num};

			if( $href->{identical} )
			{
				print OUT_FILE join( "\t", 'PASSED_string_identity', $query_seq_id, $seqid ), "\n";
				push( @identical_hits, $seqid );
				next;
			}

			# $aligndata_ref has keys: num_matches (from %ID), length, score
#			my $aligndata_ref = needle_alignment( $working_dir, $needle_query_filename,
			my $aligndata_ref = needle_alignment( $local_working_dir, $needle_query_filename,
												  $href->{filename}, $href->{strand}, $needle_version, $gapopen, $gapextend);

			$aligns{$seqid} = $aligndata_ref;

			my $align_len = $aligndata_ref->{length};
			my $num_matches = $aligndata_ref->{num_matches};

			my $pct_id = sprintf( '%.3f', $num_matches / $align_len ); # 0.xxx
			my $mismatches_per_kb = ( $query_len - $num_matches ) / ( $query_len / 1000 ); # inc gaps
			$aligns{$seqid}{mismatches_per_kb} = sprintf( '%.1f', $mismatches_per_kb );

			print LOG_FILE "$seqid: $num_matches / $align_len = $pct_id\n";

			# first filter - by alignment length and percent identity
			if( $align_len >= $params{needle}{min_align_len_pct} * $query_len
				&& $pct_id >= $params{needle}{min_align_identity} )
			{
				print OUT_FILE join( "\t", 'PASSED_needle_align_len_id', $query_seq_id, $seqid ), "\n";
				push( @filtered_needle_hits, $seqid );
			}
			else
			{
				print OUT_FILE join( "\t", 'FAILED_needle_align_len_id', $query_seq_id, $seqid ), "\n";
				print LOG_FILE "   $seqid failed first filter (min len and % ID)\n";
			}

			$top_score = $aligns{$seqid}{score} < $top_score ? $top_score : $aligns{$seqid}{score};
		}

		# second filter - keep all seqs with top score, also any seq with 
		# <= $params{needle}{num_allowed_mismatches_per_kb} mismatches / kb of query seq

		print LOG_FILE "top score = $top_score, allowed mismatches / kb = $params{needle}{num_allowed_mismatches_per_kb}\n";

		@filtered_needle_hits =
			grep
			{
				if( $aligns{$_}{score} == $top_score ||
				    $aligns{$_}{mismatches_per_kb} <= $params{needle}{num_allowed_mismatches_per_kb} )
				{
					print OUT_FILE join( "\t", 'PASSED_needle_score_mismat', $query_seq_id, $_ ), "\n";
					1;
				}
				else
				{
					print LOG_FILE "$_: score = $aligns{$_}{score}, mismatches/kb = $aligns{$_}{mismatches_per_kb}\n";
					print LOG_FILE "   $_ failed second filter (not top score or too many mismatches)\n";
					print OUT_FILE join( "\t", 'FAILED_needle_score_mismat', $query_seq_id, $_ ), "\n";
					0;
				}
			} @filtered_needle_hits;

		print LOG_FILE "num hits pre needle filter  = ", scalar keys %aligns, "\n";
		print LOG_FILE "num hits post needle filter = ", scalar @filtered_needle_hits + scalar @identical_hits, "\n";
		print OUT_FILE join( "\t", 'num_hits_pre_needle_filter', $query_seq_id, scalar keys %aligns ), "\n";
		print OUT_FILE join( "\t", 'num_hits_post_needle_filter', $query_seq_id, scalar @filtered_needle_hits + scalar @identical_hits ), "\n";

#		unlink( $needle_out_filename ) or warn "Error deleting $needle_out_filename:\n$!";
#		unlink( $needle_hits_filename ) or warn "Error deleting $needle_hits_filename:\n$!";
#			unlink( $hit_acc_num . '.fasta' ); delete hit seq

		@filtered_hits = @filtered_needle_hits;
	} # do needle

	# record matching ids (compare to annots later)

	print LOG_FILE "Here are the hits that passed the filters (query seq ID, hit ID):\n";

	foreach my $hit_id ( @identical_hits )
	{
 		$hit_id = extract_id($hit_id);
		print OUT_FILE join( "\t", 'hit_passed_all_filters_id', $query_seq_id, $hit_id ), "\n";
		print LOG_FILE join( "\t", $query_seq_id, $hit_id ), "\n";
		print OUTPUT_CONTROL_FILE join( $param_obj->config_control_file_col_separator, $query_seq_id, $hit_id, IDENTITY ), "\n";
	}

	foreach my $hit_id ( @filtered_hits )
	{
		$hit_id = extract_id($hit_id);
		print OUT_FILE join( "\t", 'hit_passed_all_filters_align', $query_seq_id, $hit_id ), "\n";
		print LOG_FILE join( "\t", $query_seq_id, $hit_id ), "\n";
		print OUTPUT_CONTROL_FILE join( $param_obj->config_control_file_col_separator, $query_seq_id, $hit_id, ALIGNMENT ), "\n";
	}

	if( ( scalar @filtered_hits + scalar @identical_hits ) == 0 )
	{
		print OUT_FILE join( "\t", 'no_hits_passed_all_filters', $query_seq_id ), "\n";
		print LOG_FILE "No seqs passed the filters for $query_seq_id\n";
	}

} # while( my $query_seq_obj

timestamp();
print OUT_FILE join( "\t", 'TIMESTAMP', scalar localtime( time() ) ), "\n";

# add the '=cut' tag below to comment out the alignment section to test on Windows
#=cut

# CLOSE ALL FILES
close(SKIPPED_ID_FILE);

close(LOG_FILE);
close(OUT_FILE);

close(OUTPUT_CONTROL_FILE);

my $ctrl_pathfile_server = File::Spec->catfile( $working_dir, $control_file );
copy($ctrl_pathfile, $ctrl_pathfile_server);
print COMPUTE_LOG_FILE "\nCopied file $ctrl_pathfile to $ctrl_pathfile_server.\n";

#************* THE PREVIOUS IS ALL STRAIGHT FROM BLAST_NEEDLE_TIMETRIALS_4.PL **********************
$server = connectToServer($params_href);

$server->call( 'update_split_source_files', $job_id, { $inpathfile => { jobStatus => 'complete' } } )
	or warn "Error updating machine file and job status!!\n";

print COMPUTE_LOG_FILE "\nFinished job ID $job_id ($params{query}{filename}) on $machine_id\n";
print COMPUTE_LOG_FILE "\n**************************************************************************************************************\n";

my $new_num_running = $server->call( 'job_done_on_machine', $machine_id, $q_mgr, $job_id, $inpathfile, 'false');
close(COMPUTE_LOG_FILE);

#************* THE FOLLOWING IS ALL STRAIGHT FROM BLAST_NEEDLE_TIMETRIALS_4.PL *********************


sub timestamp
{
	return scalar localtime( time() );
}

sub extract_seq_from_blast_db
{
	my ( $acc_num_string, $outfilename ) = @_;

	my $retval = system( 'fastacmd', '-d', $params{blast}{db_file},
									 '-p', $params{blast}{is_protein},
									 '-s', $acc_num_string,
									 '-o', $outfilename ) / 256;

	my %fastacmd_retvals = (
							     0 => 'Completed successfully',
							     1 => 'An error occurred',
							     2 => 'Blast database was not found',
							     3 => 'Failed search (accession, gi, taxonomy info)',
							     4 => 'No taxonomy database was found',
							);

	print LOG_FILE "getting $acc_num_string from the blast db -> $outfilename ($fastacmd_retvals{$retval})\n";
}

sub needle_alignment
{
	my ( $working_dir, $seq1_pathfile, $seq2_pathfile, $revcomp_seq2_flag, $needle_version, $gapopen, $gapextend) = @_;

	# perform needle alignments

	#print COMPUTE_LOG_FILE "working_dir : $working_dir\n";
	#print COMPUTE_LOG_FILE "seq1_pathfile : $seq1_pathfile\n";
	#print COMPUTE_LOG_FILE "seq2_pathfile : $seq2_pathfile\n";

	my $template = join( '_', File::Basename::basename( $seq1_pathfile ),
							  File::Basename::basename( $seq2_pathfile ),
							  'needle_XXXX' );

	my( undef, $out_filename ) = File::Temp::tempfile( $template,
													   OPEN => 0,
													   DIR => $working_dir );

	print LOG_FILE "running needle, output file = $out_filename\n";
#	print "running needle, output file = $out_filename\n";

	my $factory = Bio::Factory::EMBOSS->new();
	my $needle_prog_obj = $factory->program( 'needle' );

# compute-0-18 has a newer version of needle, which includes the -bsequence param
# compute-0-12 has an older version of needle that uses -seqall instead of -bsequence
	
    if ($needle_version eq '2.7.1') 
	{
			$needle_prog_obj->run( {
							'-asequence' => $seq1_pathfile,
			             	'-seqall' => $seq2_pathfile,
			             	'-outfile' => $out_filename,
							'-gapopen'   => $gapopen,
                            '-gapextend' => $gapextend,
			             	( $revcomp_seq2_flag ? '-sreverse2' : '' ) => '',
			               } );
	}
	else
	{
			$needle_prog_obj->run( {
							'-asequence' => $seq1_pathfile,
			             	'-bsequence' => $seq2_pathfile,
			             	'-outfile' => $out_filename,
							'-gapopen'   => $gapopen,
                            '-gapextend' => $gapextend,
			             	( $revcomp_seq2_flag ? '-sreverse2' : '' ) => '',
			               } );
	}

	print LOG_FILE "reading needle alignments and applying filter criteria\n";
#	print "reading needle alignments and applying filter criteria\n";

	# get data from the needle output file

	open(NEEDLE_FILE, '<', $out_filename ) or die "Error opening $out_filename$!";

	my %align_data;

	while( my $line = <NEEDLE_FILE> )
	{
		if( $line =~ m[^# Identity:\s+(\d+)\/(\d+)] )
		{
			$align_data{num_matches} = $1;
			$align_data{length} = $2;
		}
		elsif( $line =~ m/^# Score: (\S+)/ )
		{
			$align_data{score} = $1;
			last;
		}
	}

	close NEEDLE_FILE;

	return( \%align_data );
}

#************* THE PREVIOUS IS ALL STRAIGHT FROM BLAST_NEEDLE_TIMETRIALS_4.PL **********************
sub extract_id
{
	my $string =shift;
	my $string2;
        my @array;
	my @array1;
	if ($string =~ /gb_(.*)/)
	{

	  @array= split(/gb_/,$string);
	   
	  @array1= split(/\./,$array[1]);

	   $string2=  $array1[0];

	}

	elsif ($string =~ /ref_(.*)/)
	{

	   @array= split(/ref_/,$string);
	   
	  @array1= split(/\./,$array[1]);

	   $string2=  $array1[0];

     }
	 else
	{
      $string2 = $string;

	}
	return $string2;

}

sub getProcessId()
{
  my ($filename)  = @_;
  $filename = join ("" , $filename, "_processId.tmp");
  my $cmd = join (" " , "echo $$ >", $filename);
  #print "Command to obtain process id : '$cmd' \n";
  system($cmd);
  open PID_FILE, $filename;
  my $processId = <PID_FILE>;
  close PID_FILE;
  unlink($filename);
  chomp($processId);
  #print "\nProcess Id is : $processId\n";
  return $processId; 
}

sub getNeedleVersion()
{
  my ($filename)  = @_;
  if (! -e $filename) 
  {
	  my $cmd = join (" " , "embossversion -auto >", $filename);
	  #print "Command to obtain process id : '$cmd' \n";
	  system($cmd);
  }

  open NEEDLE_VERSION_FILE, $filename;
  my $needle_version = <NEEDLE_VERSION_FILE>;
  close NEEDLE_VERSION_FILE;

  chomp($needle_version);
  return $needle_version ; 
}


sub connectToServer()
{
	my ($params_href) = @_;
	my $server = Frontier::Client->new( 
									url   => join( '',
												   'http://', $params_href->{'server.IP'},
											 	   ':', $params_href->{'server.port'},
											 	   '/RPC2' )
								   )
									or die "Error creating Frontier::Client object";
	return $server;
}