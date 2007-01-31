#!/usr/local/bin/perl

# GeneConnect XML-RPC server 1 for sequence alignments

# Author: Robert Freimuth, July 2006

use strict;
use warnings;

use DBM::Deep;
use File::Path;
use File::Spec;
use File::Temp;
use Frontier::Daemon;
use Getopt::Long;
use POSIX qw( strftime );

use Cluster::Job 0.03;
use Cluster::JobQueue 0.02;
use Cluster::Machine 0.01;
use Cluster::MachineQueue 0.01;
use Cluster::QueueManager 0.01;
use GeneConnect::Config 0.03 qw( get_config_params );
use GeneConnect::Params 0.02;

our $VERSION = 0.01;

#*****************************************************************************************

# get the path/file to the config file from the cmd line (optional, will use the env var otherwise)

my ( $config_pathfile_master, $config_pathfile_compute );

GetOptions ( 'config_file_master:s' => \$config_pathfile_master,
			 'config_file_compute:s' => \$config_pathfile_compute );

my $params_href = GeneConnect::Config::get_config_params( $config_pathfile_master );

print "\n";

# create the Machine queue and the Job queue, and initialize the Queue manager
# (need to do this for both the db job queue and the alignment pipeline job queue)

# It might be more efficient to make job ids unique for each server, rather than for each
# queue mgr.  Then we could look up the mgr based on the job id and keep the mgr data private
# to the server code (no need to pass to external programs).  That would require taking the
# id generator out of the job queue module and putting it into the server itself.
# This would also clean up the params betw the server and the slaves (need both job id and
# chunk filename - could eliminate the chunk filename).

print "Initializing queues\n";

# this queue manager should contain only the master node
my $db_Q_mgr   = initialize_queues(
					{
						machines => {
										$params_href->{'server.machine_name'} =>
										$params_href->{'server.max_num_jobs'}
									}
					}, 'db_queue' );

# this queue manager should contain only the compute nodes
my $pipe_Q_mgr = initialize_queues(
					{
						infile => $params_href->{'compute_nodes.machines_file_name'}
					}, 'pipe_queue' );

my %Q_mgrs = map { $_->get_name => $_ } ( $db_Q_mgr, $pipe_Q_mgr );

# create the database used as a failsafe and to monitor progress
# filename = YYYYMMDD_rrrr (4 digit year, 2 digit month, 2 digit date, 4 random chars)

my $datestr = strftime( "%Y%m%d", localtime() );

print "Creating database: ";

my $dbfilename;

my $DB = do
	{
		my $dbfh;
		( $dbfh, $dbfilename ) = File::Temp::tempfile( $datestr . '_XXXX',
													   SUFFIX => '.dbm_deep.db' );
		close $dbfh;

		print "$dbfilename\n";

		# must use the filename in the constructor - fh has a bug
# DEBUG SET TO 1 FOR DEVELOPMENT
		DBM::Deep->new( file => $dbfilename, autoflush => 1, debug => 1 ) or
			die "Error creating the DBM::Deep database\n";
	};

# start the XML-RPC daemon

print "Starting daemon on port $params_href->{'server.port'}\n\n";

Frontier::Daemon->new(
                        LocalPort => $params_href->{'server.port'},
                        methods => {
                                    'seq_align_pipeline' => \&seq_align_pipeline,
                                    'do_alignments' => \&do_alignments,

                                    'get_params_for_job_id' => \&get_params_for_job_id,
                                    'get_record_for_job_id' => \&get_record_for_job_id,
                                    'set_machine_for_job_id' => \&set_machine_for_job_id,
                                    'update_split_source_files' => \&update_split_source_files,

                                    'check_job_status' => \&check_job_status,
                                    'job_done_on_machine' => \&job_done_on_machine,
                                    'check_machine_status' => \&check_machine_status,
                                    'job_queue_size' => \&job_queue_size,
                                    'export_db' => \&export_db,
                                    'shut_down_server' => \&shut_down_server,
                                    }
                     ) or
	do
	{
		unlink( $dbfilename ) or print "Error deleting $dbfilename\n";
	    die "Failed to start daemon: $!\n";
	};

print "Startied daemon\n\n";

#**`*****************************************************************************************
#                               Methods recognized by the daemon
#*****************************************************************************************

#***********************************   Core Methods   ************************************

sub seq_align_pipeline
{
	my ( $href ) = @_;

#	print "\nEntering seq_align_pipeline\n";

         # VALIDATE STRUCT OF HREF

#        print "href--->",%$href,"\n";

	my $job_id = $db_Q_mgr->get_new_job_id;

	$DB->import( { $job_id => { params => $href,
								job_id => $job_id,
								overall_status => 'incomplete' } } );

	my $working_dir = create_working_dir( $href, $datestr );

	if( not defined $working_dir )
	{
		return { error => 'error creating working directory' };
	}

	$DB->{$job_id}{working_dir} = $working_dir;

	# the machine id will be appended to the end of the cmd line
	# config file should be for the master node
# COULD USE NET::SSH TO DO THIS
	my $cmd = join( ' ', 'perl',
						 File::Spec->catfile(
						 	$params_href->{'server.path_to_split_fasta_file'},
						 	'split_fasta_file.pl' ),
						 '--job_id', $job_id,
						 '--qmgr', $db_Q_mgr->get_name,
						 $config_pathfile_master ? "--config_file_master $config_pathfile_master" : '',
						 '--mach_id' );

# REMOVE SSH FOR TESTING
	my $job_obj = Cluster::Job->new( { cmd_line => $cmd,
									   pass_machine_id => 1,
									   no_ssh => 0 } );

	$db_Q_mgr->queue_job( $job_obj );

	$db_Q_mgr->spawn_jobs();

	return { JobID => $job_id };
}

sub do_alignments
{
	my ( $href ) = @_;

#	print "\nEntering do_alignments\n";

	# $href = { job_id => $job_id, seqfile => $filename }


	my $pipe_job_id = $pipe_Q_mgr->get_new_job_id;

	$DB->{ $href->{job_id} }{split_source_files}{ $href->{seqfile} }{pipe_job_id} = $pipe_job_id;

# ADDED PATH TO PROGRAM NAME FOR TESTING
	# the machine id will be appended to the end of the cmd line
	# config file is on the compute nodes
	my $cmd = join( ' ', 'perl',
						 File::Spec->catfile(
						 	$params_href->{'compute_nodes.path_to_gc_seq_align_pipeline'},
						 	'gc_seq_align_pipeline.pl' ),
						 '--job_id', $href->{job_id},
						 '--infile', $href->{seqfile},
						 '--qmgr', $pipe_Q_mgr->get_name,
						 $config_pathfile_compute ? "--config_file_compute $config_pathfile_compute" : '',
						 '--mach_id' );
#         print "do alignment----->$cmd\n";

# REMOVE SSH FOR TESTING
	my $job_obj = Cluster::Job->new( { cmd_line => $cmd,
					   pass_machine_id => 1,
					   no_ssh => 0 } );
print "do alignment----->$cmd\n";

	$pipe_Q_mgr->queue_job( $job_obj );

	$pipe_Q_mgr->spawn_jobs();

my $test1=$pipe_Q_mgr->get_machinequeue_obj();

print "test1",$test1,"\n";

	return $pipe_job_id;
}

#*********************************   Internal Methods   **********************************

sub get_params_for_job_id
{
	my ( $job_id ) = @_;

	if( not exists $DB->{$job_id} )
	{
		return;
	}

	my $href = $DB->{$job_id}{params}->export();

	return $href;
}

sub get_record_for_job_id
{
	my ( $job_id ) = @_;

	if( not exists $DB->{$job_id} )
	{
		return;
	}

	my $href = $DB->{$job_id}->export();

	return $href;
}

sub set_machine_for_job_id
{
	my ( $job_id, $machine ) = @_;

	if( not exists $DB->{$job_id} )
	{
		return;
	}

	$DB->{$job_id}{machine} = $machine;

	return $machine;
}

sub update_split_source_files
{
	my ( $job_id, $href ) = @_;

	# the format of $href should be { $chunk_filename => { $key => $value } }

#	print "Updating source file data\n";

	if( not exists $DB->{$job_id} )
	{
		warn "   Invalid job ID ($job_id) - YOU SHOULD NEVER SEE THIS!\n";
		return;
	}

	if( not exists $DB->{$job_id}{split_source_files} )
	{
		# autovivification doesn't work on tied hashes
		$DB->{$job_id}{split_source_files} = {};
	}

	# this won't work because it overwrites the subhash (doesn't merge and DWIM)
	#$DB->{$job_id}{split_source_files}->import( $href );

	foreach my $filename ( keys %{ $href } )
	{
		if( not exists $DB->{$job_id}{split_source_files}{$filename} )
		{
			# autovivification doesn't work on tied hashes
			$DB->{$job_id}{split_source_files}{$filename} = {};
		}

		$DB->{$job_id}{split_source_files}{$filename}->import( $href->{$filename} );
	}

	return 1;
}

#********************************   Management Methods   *********************************

sub check_job_status
{
	my ( $href ) = @_;

	my $job_id = $href->{JobID};
        
        print "href in check job status---->",%$href,"\n";
       
	if( (not exists $DB->{$job_id}) or (not exists $DB->{$job_id}{split_source_files}) )
	{
		print "Job not done yet\n";
		return;
	}

	my %results = ( JobID => $job_id );
        
        print "results--",%results,"\n";

	my $all_complete = 1;

	my $source_href = $DB->{$job_id}{split_source_files};

	foreach my $filename ( keys %{ $source_href } )
	{
		$results{outputFiles}{$filename}{jobStatus} = $source_href->{$filename}{jobStatus};
		$results{outputFiles}{$filename}{checkInMinutes} = 'TBD';
		$results{outputFiles}{$filename}{path} = $source_href->{$filename}{path};
		$results{outputFiles}{$filename}{id_file} = $source_href->{$filename}{control_file};
		$results{outputFiles}{$filename}{log_file} = $source_href->{$filename}{log_file};
		$results{outputFiles}{$filename}{error_id_file} = 'n/a';

		if( $source_href->{$filename}{jobStatus} ne 'complete' )
		{
			$all_complete = 0;
		}
	}

	$results{jobStatus} = $all_complete == 1 ? 'complete' : 'incomplete';

	return \%results;
}

sub job_done_on_machine
{
	my ( $machine, $q_mgr_name ) = @_;

	my $mgr_obj = $Q_mgrs{$q_mgr_name};

	my $new_num_running_jobs = $mgr_obj->job_done_on_machine( $machine );

	$mgr_obj->spawn_jobs();

	return 1;
}

sub check_machine_status
{
	my ( $print_here ) = @_;

	my $tables = '';

	foreach my $mgr_name ( keys %Q_mgrs )
	{
		my $header = join( '', "Machine status for queue manager [$mgr_name]" );
		my $table = $Q_mgrs{$mgr_name}->machine_status_table;

		$tables = join( "\n\n", $tables, $header, $table );
	}

	if( $print_here )
	{
		print $tables;
	}

	return $tables;
}

sub job_queue_size
{
	my ( $print_here ) = @_;

	my $results = '';

	foreach my $mgr_name ( keys %Q_mgrs )
	{
		my $data = join( '', "Job queue size for queue manager [$mgr_name] = ", $Q_mgrs{$mgr_name}->get_queue_size );

		$results = join( "\n", $results, $data );
	}

	if( $print_here )
	{
		print $results;
	}

	return $results;
}

sub export_db
{
	return $DB->export();
}

sub shut_down_server
{
	my ( $no_prompt ) = @_;

	my $shut_down = 1;

	unless( $no_prompt )
	{
		while( 1 )
		{
			print "\n\nWARNING: shutting down this server will clear the job queues!!\n";
			print "\nAre you sure you want to shut this server down? [Y/N] ";

			my $ans = <STDIN>;

			if( $ans =~ m/^n/i )
			{
				print "The server is still running\n";
				$shut_down = 0;
				last;
			}
			elsif( $ans =~ m/^y/i )
			{
				print "Shutting down the server\n";
				last;
			}
		}
	}

	if( $shut_down )
	{
		warn "Shutting down.\n";
		warn "The database for this server instance is: $dbfilename\n";
		exit;
	}
}
# ADD THESE METHODS?
#	free machine (totally clear (set # running to 0) or just by a set amt)
#	spawn jobs
#	clear job queue

#*****************************************************************************************
#                                      Private Subroutines
#*****************************************************************************************

sub initialize_queues
{
	my ( $href, $queue_name ) = @_;

	my $machQ = Cluster::MachineQueue->new( $href ) or
				die "Error creating Cluster::MachineQueue\n";

	my $Q_mgr = Cluster::QueueManager->new( { machQ => $machQ,
											  jobQ  => Cluster::JobQueue->new(),
											  verbose => 0,
											  name => $queue_name } ) or
				die "Error creating the Cluster::QueueManager\n";

	return $Q_mgr;
}

sub create_working_dir
{
	my ( $href, $datestr ) = @_;

	my $param_obj = GeneConnect::Params->new( $href );

	my $basepath = $param_obj->path_base;
	my $source = $param_obj->source_file;
	my $target = $param_obj->target_db;

	my $working_dir = File::Spec->catdir( $basepath, join( '_', $datestr, $source, $target ) );

	if( ! -e $working_dir )
	{
		my ( $createdpath ) = File::Path::mkpath( $working_dir );

		if( (not defined $createdpath) || ($createdpath ne $working_dir) )
		{
			print "Error creating working directory ($working_dir)\n";
			return;
		}

		print "Created working dir: $working_dir\n";
	}
	else
	{
		print "Working directory already exists ($working_dir)\n";
	}

	return $working_dir;
}

#*****************************************************************************************
#                                          Guts
#*****************************************************************************************

=pod

%DB:

$jobid = {
          'params' => {
                        'SRCFILE' => 'test_file.fasta',
                        'DESTINATIONDB' => 'fake_db',
                        'CONFIG' => {
                                      'numSeqsPerJob' => '300'
                                    },
                        'PATHS' => {
                                     'base' => 'C:\\Bob\\perlcode\\modules\\GeneConnect',
                                     'source' => 'C:\\Bob\\perlcode\\modules\\GeneConnect'
                                   }
                      },
          'split_source_files' => {
                                    'C:\\ <path & filename> .fa' => {
																         'pipe_job_id' => '1',
																         'path' => 'C:\\Bob\\perlcode\\modules\\GeneConnect',
																         'jobStatus' => 'incomplete',
																         'machine' => 'machine1'
																       },
                                  },
          'machine' => 'machine1',
          'overall_status' => 'incomplete',
          'working_dir' => 'C:\\Bob\\perlcode\\modules\\GeneConnect\\20060727_test_file.fasta_fake_db',
          'job_id' => '1'
        };

=cut
