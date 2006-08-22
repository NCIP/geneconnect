package Cluster::QueueManager;

# Cluster::QueueManager contains a Cluster::MachineQueue object and a Cluster::JobQueue
# object, and provides methods to simplify starting and queueing jobs on a queue of
# compute nodes.  It is primarily used for XML-RPC servers.
# See also Cluster::MachineQueue and Cluster::JobQueue.

# Author: Robert Freimuth
# Washington University, July 2006

# suggestion from diotalevi:
# use Parallel::ForkManager and do sub { exec "ssh user\@host $command" } instead of this module

use strict;
use warnings;

use Carp;

use Cluster::Job 0.03;
use Cluster::JobQueue 0.02;
use Cluster::Machine 0.01;
use Cluster::MachineQueue 0.01;

our $VERSION = 0.01;

#*****************************************************************************************
#                                       Example of Use
#*****************************************************************************************

=pod

my $Qmgr_obj = Cluster::QueueManager->new( { machQ => $cluster_machinequeue_obj,
											 jobQ  => $cluster_jobqueue_obj,
											 %params } );

my $Qmgr_name = $Qmgr_obj->get_name();

my $cluster_jobqueue_obj = $Qmgr_obj->get_jobqueue_obj();
my $cluster_machinequeue_obj = $Qmgr_obj->get_machinequeue_obj();

my $status_table = $Qmgr_obj->machine_status_table();
my $job_queue_size = $Qmgr_obj->get_queue_size();

$Qmgr_obj->execute_job( $cluster_jobqueue_obj, $cluster_machinequeue_obj );
my $new_job_queue_size = $Qmgr_obj->queue_job( $cluster_job_obj );
$Qmgr_obj->spawn_jobs( $return_flag );
my $new_num_running_jobs = Qmgr_obj->job_done_on_machine( $machine ); # does $jobs--
my $new_job_id = Qmgr_obj->get_new_job_id();

Once a Cluster::QueueManager instance is created, the most important methods are
queue_job (which adds a thing, usually a Cluster::Job object, to the job queue) and
spawn_jobs (which starts as many jobs from the job queue as there are available machines).

=cut


#*****************************************************************************************
#                                       Public Methods
#*****************************************************************************************

sub new
{
	# Creates a new Cluster::MachineQueue object.  Requires a hash ref as a
	# parameter.  The href must contain the following keys:
	#    machQ = a Cluster::MachineQueue object
    #    jobQ  = a Cluster::JobQueue object
    # The href may also contain the following keys (optional):
    #    verbose = 0/1, turns off/on printing when jobs are started and queued (default 0)
    #	 name = the name assigned to this QueueManager object (optional), will be a random
    #           string if none is provided

	my ( $class, $href ) = @_;

	if( (not defined $href) or (ref $href ne 'HASH') )
	{
		Carp::carp "$class->new() requires a hash ref as a parameter";
		return;
	}

	if( not defined $href->{machQ} or $href->{machQ} eq '' )
	{
		Carp::carp "Cannot create a new $class object without a machine queue object";
		return;
	}

	if( not defined $href->{jobQ} or $href->{jobQ} eq '' )
	{
		Carp::carp "Cannot create a new $class object without a job queue object";
		return;
	}

	# copy the keys for Cluster::QueueManager into a class-specific subhash

	if( exists $href->{_Cluster_QueueManager} )
	{
		Carp::carp "Warning: class-specific key already exists. Data will be overwritten";
	}

	$href->{_Cluster_MachineQueue}{machinequeue_obj} = $href->{machQ};
	$href->{_Cluster_MachineQueue}{jobqueue_obj} = $href->{jobQ};
	$href->{_Cluster_MachineQueue}{verbose} = $href->{verbose} || 0;
	$href->{_Cluster_MachineQueue}{name} = $href->{name} || get_rand_string( 10 );

	my $obj = bless( $href, $class );

	return $obj;
}

sub get_name
{
	my ( $self ) = @_;

	return $self->{_Cluster_MachineQueue}{name};
}

sub get_rand_string
{
	my ( $len ) = @_;

	my @chars = ( 'a'..'z', 'A'..'Z', 0..9 );
	my $string = '';

	for( 1 .. $len )
	{
		$string .= $chars[ rand( scalar @chars ) ];
	}

	return $string;
}

sub get_machinequeue_obj
{
	my ( $self ) = @_;

	return $self->{_Cluster_MachineQueue}{machinequeue_obj};
}

sub get_jobqueue_obj
{
	my ( $self ) = @_;

	return $self->{_Cluster_MachineQueue}{jobqueue_obj};
}

sub set_verbose
{
	my ( $self, $value ) = @_;

	$self->{_Cluster_MachineQueue}{verbose} = ( $value ? 1 : 0 );
}

sub get_verbose
{
	my ( $self ) = @_;

	return $self->{_Cluster_MachineQueue}{verbose};
}

sub execute_job
{
    my ( $self, $job_obj, $mach_obj ) = @_;

	my $machine_name = $mach_obj->get_name;

	if( $self->get_verbose )
	{
	    print "\nAvailable machine found ($machine_name), starting job:\n";
    	print '  ', $job_obj->get_cmd_line, "\n";
    }

    $job_obj->set_machine( $machine_name );
    $job_obj->run_job( $machine_name );

	$mach_obj->alter_num_running_jobs( 1 );

	return 1;
}

sub job_done_on_machine
{
	my ( $self, $machine ) = @_;

	my $mach_Q = $self->get_machinequeue_obj;
	my $mach_obj = $mach_Q->get_machine_by_name( $machine );

	if( not defined $mach_obj )
	{
		return;
	}

	my $new_num_running_jobs = $mach_obj->alter_num_running_jobs( -1 );

	return $new_num_running_jobs;
}

sub queue_job
{
    my ( $self, $job_obj ) = @_;

    my $jobQ = $self->get_jobqueue_obj;

	my $queuesize = $jobQ->queue_job( $job_obj );

	if( $self->get_verbose )
	{
	    print "\nAdding job to queue:\n";
    	print '  ', $job_obj->get_cmd_line, "\n";
    }

    return $queuesize; # should always be > 0
}

sub spawn_jobs
{
	my ( $self, $return_flag ) = @_;

    # start as many jobs from the queue as possible

    my $machineQ = $self->get_machinequeue_obj;
    my $jobQ = $self->get_jobqueue_obj;

    while( ( $jobQ->get_queue_size > 0 ) &&
           ( scalar $machineQ->get_available_machines > 0 ) )
    {
        my $next_job = $jobQ->get_queued_job;
	    my $mach_obj = $machineQ->get_next_avail_machine;

        # check for definedness to avoid race conditions

        if( defined $next_job )
        {
        	if( defined $mach_obj )
        	{
	            $self->execute_job( $next_job, $mach_obj );
        	}
        	else
        	{
        		$self->queue_job( $next_job );
        	}
        }
    }

	return 1;
}

sub machine_status_table
{
	my ( $self ) = @_;

    my $machineQ = $self->get_machinequeue_obj;
	my $status_table = $machineQ->machine_status_table;

	return $status_table;
}

sub get_queue_size
{
	my ( $self ) = @_;

    my $jobQ = $self->get_jobqueue_obj;
	my $job_queue_size = $jobQ->get_queue_size;

	return $job_queue_size;
}

sub get_new_job_id
{
	my ( $self ) = @_;

    my $jobQ = $self->get_jobqueue_obj;
	my $job_id = $jobQ->get_new_id;

	return $job_id;
}

#*****************************************************************************************

1;
