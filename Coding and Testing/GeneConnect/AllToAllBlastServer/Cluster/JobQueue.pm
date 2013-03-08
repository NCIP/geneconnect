package Cluster::JobQueue;

# Cluster::JobQueue contains a set of Cluster::Job objects that will be run on the
# cluster (either a compute node or the master node).  See also Cluster::Job.
# NOTE: although this class was originally intended to queue Cluster::Job objects,
# any scalar (reference) can be stored in this queue.

# Author: Robert Freimuth
# Washington University, July 2006

use strict;
use warnings;

use Carp;

our $VERSION = 0.02;

#*****************************************************************************************
#                                       Example of Use
#*****************************************************************************************

=pod

methods:

my $jobQ = Cluster::JobQueue->new();
my $new_queue_size = $jobQ->queue_job( $job_obj ); # $job_obj isa Cluster::Job
my $job_obj = $jobQ->get_queued_job(); # $job_obj isa Cluster::Job (or undef if empty)
my $queue_size = $jobQ->get_queue_size();
my $new_job_id = $jobQ->get_new_id();

=cut


#*****************************************************************************************
#                                       Public Methods
#*****************************************************************************************

sub new
{
	# Creates a new Cluster::JobQueue object

	my ( $class ) = @_;

	my $jobQ_obj = bless( { new_id => construct_counter(), queue => [] }, $class );
	return $jobQ_obj;
}

sub queue_job
{
	my ( $self, $job_obj ) = @_;
	push( @{ $self->{queue} }, $job_obj );
	return $self->get_queue_size;
}

sub get_queued_job
{
	my ( $self ) = @_;
	return shift( @{ $self->{queue} } );
}

sub get_queue_size
{
	my ( $self ) = @_;
	return scalar @{ $self->{queue} };
}

sub get_new_id
{
	my ( $self ) = @_;
	return $self->{new_id}->();
}

#*****************************************************************************************
#                                      Private Subroutines
#*****************************************************************************************

sub construct_counter
{
	# this will return a new counter each time it is called, which can be used for
	# generating unique job IDs (unique for this queue, that is)

	my $job_id = 1;
	my $subref = sub { $job_id++ };
	return $subref;
}

#*****************************************************************************************

1;
