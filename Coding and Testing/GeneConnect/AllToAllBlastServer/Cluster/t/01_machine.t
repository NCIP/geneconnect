use strict;
use warnings;

use Data::Dumper;
use Test::More tests => 96;
#use Test::More qw( no_plan );

# This is the first test file for Cluster::Machine

# NEED TO ENSURE ALL CASES ARE COVERED

#***************************************************************************************************

# first test - make sure the module loaded correctly

BEGIN { print "\n"; use_ok( "Cluster::Machine" ); }

# object construction

{
	print "\nTesting object construction and methods:\n";

	{
		print "\nThese should fail:\n";

		my $obj = Cluster::Machine->new( );
		ok( (not defined $obj), "Detect invalid struct in new() (no param)" );

		$obj = Cluster::Machine->new( [ 'arrayref should fail' ] );
		ok( (not defined $obj), "Detect invalid struct in new() (array ref)" );

		$obj = Cluster::Machine->new( { missing_reqd_key => 'should fail' } );
		ok( (not defined $obj), "Missing required param in new() (name)" );
	}

	print "\nThese should succeed:\n";

	my $obj = Cluster::Machine->new( { name => 'test_name' } );
	isa_ok( $obj, 'Cluster::Machine', "Created object" );

	foreach my $method qw(
							get_max_num_jobs
							set_max_num_jobs
							get_name
							set_name
							get_num_running_jobs
							alter_num_running_jobs
							get_num_avail_jobs
							is_free
						  )
	{
		can_ok( $obj, $method );
	}
}

#***************************************************************************************************

# object methods

print "\nTesting object methods:\n";

{
	# get_name, set_name

	print "\n*** get_name, set_name ***\n";

	print "\nThese should succeed:\n";

	my $orig_name = 'test_name';
	my $obj = Cluster::Machine->new( { name => $orig_name } );
	isa_ok( $obj, 'Cluster::Machine', "Created object" );

	is( $obj->get_name, $orig_name, "Retrieve machine name" );

	print "\nThese should cause errors:\n";

	foreach my $new_name ( undef, '' )
	{
		my $ret_val = $obj->set_name( $new_name );
		is( $ret_val, undef, "Return value for set_name with invalid argument" );
		is( $obj->get_name, $orig_name, "Value of name with invalid argument" );
	}

	print "\nThese should succeed:\n";

	my $new_name = 'new_name';
	my $ret_val = $obj->set_name( $new_name );
	is( $ret_val, $new_name, "Return value for set_name with valid argument" );
	is( $obj->get_name, $new_name, "Value of name with valid argument" );
}

{
	# get_max_num_jobs, set_max_num_jobs

	print "\n*** get_max_num_jobs, set_max_num_jobs ***\n";

	print "\nThese should succeed:\n";

	# create without specifying max jobs, should get default value
	foreach my $value ( undef, '' )
	{
		my $obj = Cluster::Machine->new( { name => 'test_name', max_num_jobs => $value } );
		isa_ok( $obj, 'Cluster::Machine', "Created object" );
		is( $obj->get_max_num_jobs, 0, "Default value assigned during object creation" );
	}

	# create with specified max jobs - these should be ok
	foreach my $value ( 0, 2, +2 )
	{
		my $obj = Cluster::Machine->new( { name => 'test_name', max_num_jobs => $value } );
		isa_ok( $obj, 'Cluster::Machine', "Created object" );
		is( $obj->get_max_num_jobs, $value, "Value ($value) assigned during object creation" );
	}

	print "\nThese should cause errors:\n";

	# create with specified max jobs - these should cause errors but still return an object
	foreach my $value ( 1.1, -2, 'a' )
	{
		my $obj = Cluster::Machine->new( { name => 'test_name', max_num_jobs => $value } );
		isa_ok( $obj, 'Cluster::Machine', "Created object" );
		is( $obj->get_max_num_jobs, 0, "Invalid value ($value) for max_num_jobs converted to 0 during object creation" );
	}

	print "\nThese should succeed:\n";

	# set max num jobs - these should be ok
	{
		my $max_num_jobs = 10;
		my $obj = Cluster::Machine->new( { name => 'test_name', max_num_jobs => $max_num_jobs } );
		isa_ok( $obj, 'Cluster::Machine', "Created object" );

		my $ret_val = $obj->set_max_num_jobs( undef );
		is( $ret_val, $max_num_jobs, "set_max_num_jobs return value with valid value (undef)" );
		is( $obj->get_max_num_jobs, $max_num_jobs, "set_max_num_jobs works with valid value (undef)" );

		foreach my $value ( 0, 2, +2, 3.0 )
		{
			my $ret_val = $obj->set_max_num_jobs( $value );
			is( $ret_val, $value, "set_max_num_jobs return value with valid value ($value)" );
			is( $obj->get_max_num_jobs, $value, "set_max_num_jobs works with valid value ($value)" );
		}
	}

	print "\nThese should cause errors:\n";

	# set max num jobs - these should cause errors but still return an object
	{
		my $max_num_jobs = 10;
		my $obj = Cluster::Machine->new( { name => 'test_name', max_num_jobs => $max_num_jobs } );
		isa_ok( $obj, 'Cluster::Machine', "Created object" );

		foreach my $value ( 2.01, -2, '2.a', 'a', '' )
		{
			my $ret_val = $obj->set_max_num_jobs( $value );
			isa_ok( $obj, 'Cluster::Machine', "Created object" );
			is( $obj->get_max_num_jobs, $max_num_jobs, "set_max_num_jobs skips invalid value ($value)" );
		}
	}
}

{
	# get_num_running_jobs, alter_num_running_jobs

	print "\n*** get_num_running_jobs, alter_num_running_jobs ***\n";

	print "\nThese should succeed:\n";

	# these should be ok
	{
		my $max_num_jobs = 10;
		my $obj = Cluster::Machine->new( { name => 'test_name', max_num_jobs => $max_num_jobs } );
		isa_ok( $obj, 'Cluster::Machine', "Created object" );

		my $num_running_jobs = $obj->get_num_running_jobs();
		is( $num_running_jobs, 0, "number of running jobs before explicitly set" );

		foreach my $value ( 0, 2, +2, -2, 3.0 )
		{
			my $ret_val = $obj->alter_num_running_jobs( $value );
			$num_running_jobs += $value;
			is( $ret_val, $num_running_jobs, "alter_num_running_jobs return value with valid value ($value)" );
			is( $obj->get_num_running_jobs, $num_running_jobs, "alter_num_running_jobs works with valid value ($value)" );
		}

		# go < 0 and > $max
		my $ret_val = $obj->alter_num_running_jobs( -1 * $num_running_jobs );
		is( $ret_val, 0, "clear number of running jobs" );

		$ret_val = $obj->alter_num_running_jobs( -2 );
		is( $ret_val, 0, "minimum number of running jobs is 0" );

		$ret_val = $obj->alter_num_running_jobs( -1 * $num_running_jobs );
		is( $ret_val, 0, "clear number of running jobs" );

		# this should cause a warning but still get set
		$ret_val = $obj->alter_num_running_jobs( $max_num_jobs + 2 );
		is( $ret_val, $max_num_jobs + 2, "maximum number of running jobs is > the set max number of jobs" );
	}

	print "\nThese should cause errors:\n";

	# these should cause errors
	{
		my $max_num_jobs = 10;
		my $obj = Cluster::Machine->new( { name => 'test_name', max_num_jobs => $max_num_jobs } );
		isa_ok( $obj, 'Cluster::Machine', "Created object" );

		my $num_running_jobs = $obj->get_num_running_jobs();
		is( $num_running_jobs, 0, "number of running jobs before explicitly set" );

		foreach my $value ( 2.01, '2.a', 'a', '', undef )
		{
			my $ret_val = $obj->alter_num_running_jobs( $value );
			is( $ret_val, undef, "alter_num_running_jobs return value with invalid value (" . ( defined $value ? $value : 'undef' ) . ")" );
			is( $obj->get_num_running_jobs, 0, "alter_num_running_jobs skips invalid value" );
		}
	}
}

{
	# get_num_avail_jobs, is_free

	print "\n*** get_num_avail_jobs, is_free ***\n";

	print "\nThese should succeed:\n";

	my $max_num_jobs = 10;
	my $obj = Cluster::Machine->new( { name => 'test_name', max_num_jobs => $max_num_jobs } );
	isa_ok( $obj, 'Cluster::Machine', "Created object" );

	# num running < max num jobs
	my $num_avail_jobs = $obj->get_num_avail_jobs();
	is( $num_avail_jobs, $max_num_jobs, "num of available jobs when num running < max number of jobs" );
	my $ret_val = $obj->is_free();
	is( $ret_val, 1, "is_free when number jobs running < max" );

	# num running = max num jobs
	$ret_val = $obj->alter_num_running_jobs( $max_num_jobs );
	is( $ret_val, $max_num_jobs, "set number of running jobs" );
	$num_avail_jobs = $obj->get_num_avail_jobs();
	is( $num_avail_jobs, 0, "num of available jobs when num running = max number of jobs" );
	$ret_val = $obj->is_free();
	is( $ret_val, 0, "is_free when number jobs running = max" );

	# num running > max num jobs
	$ret_val = $obj->alter_num_running_jobs( +1 );
	is( $ret_val, $max_num_jobs + 1, "set number of running jobs" );
	$num_avail_jobs = $obj->get_num_avail_jobs();
	is( $num_avail_jobs, -1, "num of available jobs when num running > max number of jobs" );
	$ret_val = $obj->is_free();
	is( $ret_val, 0, "is_free when number jobs running > max" );
}


