use strict;
use warnings;

use Data::Dumper;
#use Test::More tests => 96;
use Test::More qw( no_plan );

# This is the first test file for Cluster::JobQueue

#***************************************************************************************************

# first test - make sure the module loads correctly

BEGIN { print "\n"; use_ok( "Cluster::JobQueue" ); }

# object construction

{
	print "\nTesting object construction and methods:\n";

	print "\nThese should succeed:\n";

	my $obj = Cluster::JobQueue->new( );
	isa_ok( $obj, 'Cluster::JobQueue', "Created object" );

	foreach my $method qw(
							queue_job
							get_queued_job
							get_queue_size
						  )
	{
		can_ok( $obj, $method );
	}
}

#***************************************************************************************************

# object methods

print "\nTesting object methods:\n";

{
	my $obj = Cluster::JobQueue->new( );
	isa_ok( $obj, 'Cluster::JobQueue', "Created object" );

	my $q_size = $obj->get_queue_size;
	is( $q_size, 0, "Starting queue size (get_queue_size)" );

	# add 2 jobs to the queue

	$q_size = $obj->queue_job( 'job1' );
	is( $q_size, 1, "Queue size - 1 job (queue_job)" );
	$q_size = $obj->get_queue_size;
	is( $q_size, 1, "Queue size - 1 job (get_queue_size)" );

	$q_size = $obj->queue_job( 'job2' );
	is( $q_size, 2, "Queue size - 2 jobs (queue_job)" );
	$q_size = $obj->get_queue_size;
	is( $q_size, 2, "Queue size - 2 jobs (get_queue_size)" );

	# remove 2 jobs from the queue (ensure come off in correct order - FIFO)

	my $job = $obj->get_queued_job;
	is( $job, 'job1', "Returned first queued job - (get_queued_job)" );
	$q_size = $obj->get_queue_size;
	is( $q_size, 1, "Queue size - 1 job (get_queue_size)" );

	$job = $obj->get_queued_job;
	is( $job, 'job2', "Returned second queued job - (get_queued_job)" );
	$q_size = $obj->get_queue_size;
	is( $q_size, 0, "Queue size - 0 jobs (get_queue_size)" );

	$job = $obj->get_queued_job;
	is( $job, undef, "Return value from empty queue - (get_queued_job)" );
}


