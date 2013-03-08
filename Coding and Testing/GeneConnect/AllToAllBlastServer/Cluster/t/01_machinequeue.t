use strict;
use warnings;

use Data::Dumper;
#use Test::More tests => 96;
use Test::More qw( no_plan );

# This is the first test file for Cluster::MachineQueue

# NEED TO ENSURE ALL CASES ARE COVERED

#***************************************************************************************************

# first test - make sure the modules load correctly

BEGIN { print "\n"; use_ok( "Cluster::MachineQueue" ); }

# Cluster::Machine may be better as a Test::MockObject
BEGIN { print "\n"; use_ok( "Cluster::Machine" ); }

# object construction

{
	print "\nTesting object construction and methods:\n";

	{
		print "\nThese should fail:\n";

		my $obj = Cluster::MachineQueue->new( );
		ok( (not defined $obj), "Detect invalid struct in new() (no param)" );

		$obj = Cluster::MachineQueue->new( [ 'arrayref should fail' ] );
		ok( (not defined $obj), "Detect invalid struct in new() (array ref)" );
	}

	print "\nThese should succeed:\n";

	my $obj = Cluster::MachineQueue->new( { } );
	isa_ok( $obj, 'Cluster::MachineQueue', "Created object" );

	foreach my $method qw(
							load_machines
							load_machines_file
							get_all_machines
							get_available_machines
							get_next_avail_machine
							get_machine_by_name
						  )
	{
		can_ok( $obj, $method );
	}
}

#***************************************************************************************************

# object methods

print "\nTesting object methods:\n";

{
	# load_machines, get_machine_by_name

	print "\n*** load_machines, get_machine_by_name ***\n";

	print "\nThese should cause errors:\n";

	my $obj = Cluster::MachineQueue->new( { } );
	isa_ok( $obj, 'Cluster::MachineQueue', "Created object" );

	foreach my $value ( undef, '', [] )
	{
		my $ret_val = $obj->load_machines( $value );
		is( $ret_val, undef, "Return value for load_machines with invalid argument" );
	}

	print "\nThese should succeed:\n";

	my %params = ( mach1 => 1, mach2 => 2 );

	my $ret_val = $obj->load_machines( \%params );
	is( $ret_val, 1, "Return value for load_machines with valid params hash" );

	foreach my $mach ( keys %params )
	{
		my $mach_obj = $obj->get_machine_by_name( $mach );
		isa_ok( $mach_obj, 'Cluster::Machine', "Created object ($mach)" );
		my $max_jobs = $mach_obj->get_max_num_jobs();
		is( $max_jobs, $params{$mach}, "Set max number of jobs" );
	}

	# keep 1 the same, change 2, add new (3)
	$params{mach2}++;
	$params{mach3} = 0;

	$ret_val = $obj->load_machines( \%params );
	is( $ret_val, 1, "Return value for load_machines with valid params hash" );

	foreach my $mach ( keys %params )
	{
		my $mach_obj = $obj->get_machine_by_name( $mach );
		isa_ok( $mach_obj, 'Cluster::Machine', "Created object ($mach)" );
		my $max_jobs = $mach_obj->get_max_num_jobs();
		is( $max_jobs, $params{$mach}, "Set max number of jobs" );
	}
}

{
	# load_machines_file

	print "\n*** load_machines_file ***\n";

	print "\nThese should succeed:\n";

print "\n\nMISSING THESE TESTS\n\n";

=pod

stuff goes here

=cut

}

{
	# get_all_machines, get_available_machines, get_next_avail_machine

	print "\n*** get_all_machines, get_available_machines, get_next_avail_machine ***\n";

	print "\nThese should succeed:\n";

	my $obj = Cluster::MachineQueue->new( { } );
	isa_ok( $obj, 'Cluster::MachineQueue', "Created object" );

	my %params = ( mach1 => 1, mach2 => 2 );

	my $ret_val = $obj->load_machines( \%params );
	is( $ret_val, 1, "Return value for load_machines with valid params hash" );

	my @ret_vals = $obj->get_all_machines();

	# verify all returned values are of the correct class

	foreach my $value ( @ret_vals )
	{
		isa_ok( $value, 'Cluster::Machine', "Return Cluster::Machine object" );
	}

	# check for any missing or extra items

	my %ret_objs = map { $_->get_name => 1 } @ret_vals;

	my @missing = grep { exists $ret_objs{$_} ? 0 : 1; } keys %params;
	is( scalar @missing, 0, "Number of missing objects - get_all_machines" );

	my @extra = grep { exists $params{$_} ? 0 : 1; } keys %ret_objs;
	is( scalar @extra, 0, "Number of extra objects - get_all_machines" );

	# get_available_machines (all should be available, since no running jobs yet)

	my %avail_objs = map { $_->get_name => 1 } $obj->get_available_machines();

	@missing = grep { exists $avail_objs{$_} ? 0 : 1; } keys %params;
	is( scalar @missing, 0, "Number of missing objects - get_available_machines" );

	@extra = grep { exists $params{$_} ? 0 : 1; } keys %avail_objs;
	is( scalar @extra, 0, "Number of extra objects - get_available_machines" );

	# make 1 machine unavailable

	my $unavail_mach = $ret_vals[0];
	my $unavail_name = $unavail_mach->get_name();
	$unavail_mach->alter_num_running_jobs( $params{$unavail_name} );

	%avail_objs = map { $_->get_name => 1 } $obj->get_available_machines();

	my $unavail_is_missing = exists $avail_objs{$unavail_name} ? 0 : 1;
	is( $unavail_is_missing, 1, "Unavailable machine not returned - get_available_machines" );

	@missing = grep { exists $avail_objs{$_} ? 0 : 1; } keys %params;
	is( scalar @missing, 1, "Number of missing objects - get_available_machines" );

	@extra = grep { exists $params{$_} ? 0 : 1; } keys %avail_objs;
	is( scalar @extra, 0, "Number of extra objects - get_available_machines" );

	# should only be 1 machine available, make sure get it as next avail machine

	my $avail_mach = $ret_vals[1];
	my $avail_name = $avail_mach->get_name();

	my $next_avail_obj = $obj->get_next_avail_machine();
	my $next_avail_name = $next_avail_obj->get_name();

	is( $next_avail_name, $avail_name, "get_next_avail_machine" );

	# make it unavail, make sure get nothing for get avail machines and get next avail machine

	$next_avail_obj->alter_num_running_jobs( $params{$next_avail_name} );

	is( scalar $obj->get_available_machines, 0, "get_available_machines - no machines available" );
	is( $obj->get_next_avail_machine, undef, "get_next_avail_machine - no machines available" );
}
