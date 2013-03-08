package Cluster::Machine;

# Cluster::Machine represents a machine on the cluster (either a compute node or the
# master node).  It is used primarily to track the number of jobs started/availabe
# on each machine.  See also Cluster::MachineQueue.

# Author: Robert Freimuth
# Washington University, July 2006

use strict;
use warnings;

use Carp;

our $VERSION = 0.01;

#*****************************************************************************************
#                                       Example of Use
#*****************************************************************************************

=pod

my $machine = Cluster::Machine->new( { 'name' => <name>, ['max_num_jobs' => <integer>] } );

my $max_jobs     = $machine->get_max_num_jobs();
my $max_jobs     = $machine->set_max_num_jobs( $integer ); # must be >= 0

my $machine_name = $machine->get_name();
my $machine_name = $machine->set_name( $name );

my $num_running_jobs     = $machine->get_num_running_jobs();
my $new_num_running_jobs = $machine->alter_num_running_jobs( $integer ); # can be + or -

my $num_avail_jobs = $machine->get_num_avail_jobs();
my $is_free = $machine->is_free();

=cut

#*****************************************************************************************
#                                       Public Methods
#*****************************************************************************************

sub new
{
	# Creates a new Cluster::Machine object.  Requires a hash ref as a parameter.
	# The href must contain a 'name' key that specifies the name of the machine (the
	# name that would be used to ssh to it).  The href may also contain a value for
	# 'max_num_jobs', which sets the maximum number of jobs that can be run simultaneously
	# (assuming the other methods in this class are used to verify that a machine is free
	# before starting a new job), and 'num_running_jobs', which is the number of jobs
	# already started on the machine.
	# Additional keys can be included in $href, but they will be ignored by this class.

	my ( $class, $href ) = @_;

	if( (not defined $href) or (ref $href ne 'HASH') )
	{
		Carp::carp "$class->new() requires a hash ref as a parameter";
		return;
	}

	if( not defined $href->{name} or $href->{name} eq '' )
	{
		Carp::carp "Cannot create a new $class object without a specified machine name";
		return;
	}

	# copy the keys for Cluster::Machine into a class-specific subhash

	if( exists $href->{_Cluster_Machine} )
	{
		Carp::carp "Warning: class-specific key already exists. Data will be overwritten";
	}

	my $obj = bless( $href, $class );

	$obj->set_name( $href->{name} );
	$obj->set_max_num_jobs( $href->{max_num_jobs} || 0 );
	$obj->alter_num_running_jobs( $href->{num_running_jobs} || 0 );

	return $obj;
}

sub get_name
{
	my ( $self ) = @_;
	return $self->{_Cluster_Machine}{name};
}

sub set_name
{
	my ( $self, $name ) = @_;

	if( (not defined $name) or ($name eq '') )
	{
		Carp::carp "Argument to set_name must be defined";
		return;
	}

	$self->{_Cluster_Machine}{name} = $name;
	return $self->{_Cluster_Machine}{name};
}

sub get_max_num_jobs
{
	my ( $self ) = @_;
	return $self->{_Cluster_Machine}{max_num_jobs};
}

sub set_max_num_jobs
{
	my ( $self, $num ) = @_;

	if( _is_integer( $num ) )
	{
		if( $num < 0 )
		{
			Carp::carp "Warning: cannot set max_num_jobs < 0 (now set to 0)";
			$num = $self->{_Cluster_Machine}{max_num_jobs} || 0;
		}
	}
	else
	{
		Carp::carp "Argument to set_max_num_jobs must be an integer >= 0 (now set to 0)";
		$num = $self->{_Cluster_Machine}{max_num_jobs} || 0;
	}

	$self->{_Cluster_Machine}{max_num_jobs} = $num;

	return $self->{_Cluster_Machine}{max_num_jobs};
}

sub get_num_running_jobs
{
	my ( $self ) = @_;
	return $self->{_Cluster_Machine}{num_running_jobs};
}

sub alter_num_running_jobs
{
	my ( $self, $num ) = @_;

#	$num = ( ! defined $num || $num eq '' ) ? 0 : $num;

	if( ! _is_integer( $num ) )
	{
		Carp::carp "Argument to alter_num_running_jobs must be an integer";
		print "\n ERROR : Argument to alter_num_running_jobs must be an integer";
		return;
	}

	$self->{_Cluster_Machine}{num_running_jobs} += $num;

	if( $self->get_num_running_jobs < 0 )
	{
		Carp::carp "Cannot set number of running jobs < 0 (now set to 0)";
		print "\n Cannot set number of running jobs < 0 (now set to 0).\n";
		$self->{_Cluster_Machine}{num_running_jobs} = 0;
	}

	if( $self->get_num_running_jobs > $self->get_max_num_jobs )
	{
		Carp::carp "Warning: number of running jobs exceeds max number of jobs";
		print  "\n Warning: number of running jobs exceeds max number of jobs\n";
	}
	print "\n********  No. of jobs running on machine(", $self->get_name(), ") (after adding ", $num, ") : ",  $self->get_num_running_jobs, "\n" ;
	return $self->get_num_running_jobs;
}

sub get_num_avail_jobs
{
	my ( $self ) = @_;
	return $self->get_max_num_jobs() - $self->get_num_running_jobs();
}

sub is_free
{
	my ( $self ) = @_;
	return $self->get_num_avail_jobs > 0 ? 1 : 0;
}

#*****************************************************************************************
#                                       Private Methods
#*****************************************************************************************

sub _is_integer
{
	my ( $string ) = @_;

	if( (! defined $string) || $string !~ m/^-?\d+\.?0*$/ )
	{
		return 0;
	}

	return 1;
}

#*****************************************************************************************

1;

