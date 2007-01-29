package Cluster::MachineQueue;

# Cluster::MachineQueue contains a set of Cluster::Machine objects that represent machines
# on the cluster (either a compute node or the master node).  It is used primarily to
# identify machines that are available so jobs can be started on them.
# See also Cluster::Machine.

# Author: Robert Freimuth
# Washington University, July 2006

use strict;
use warnings;

use Carp;
use Cluster::Machine;

our $VERSION = 0.01;

#*****************************************************************************************
#                                       Example of Use
#*****************************************************************************************

=pod

methods:

my $machQ = Cluster::MachineQueue->new( { see comments below } );
$machQ->load_machines( @machine_list );
$machQ->load_machines_file( $pathfile );

my @machine_objs = $machQ->get_all_machines();
my @machine_objs = $machQ->get_available_machines();
my $machine_obj  = $machQ->get_next_avail_machine()
my $status_table = $machQ->machine_status_table();

my $machine_obj  = $machQ->get_machine_by_name( $name );

=cut


#*****************************************************************************************
#                                       Public Methods
#*****************************************************************************************

sub new
{
	# Creates a new Cluster::MachineQueue object.  Requires a hash ref as a
	# parameter.  The href should contain at least one of the following keys:
	#    infile   = a filename (with full path) that contains a list of machines to use
    #    machines = a hash ref that contains a list of machines ( name => max # jobs )
    # If both keys are present, the union of the lists will be used.
	# Any additional keys can be included in $href, but no accessors are defined for them.

	my ( $class, $href ) = @_;

	if( (not defined $href) || (ref $href ne 'HASH') )
	{
		Carp::carp "$class->new() requires a hash ref as a parameter";
		return;
	}

	# copy the keys for Cluster::MachineQueue into a class-specific subhash

	if( exists $href->{_Cluster_MachineQueue} )
	{
		Carp::carp "Warning: class-specific key already exists. Data will be overwritten";
	}

	my $obj = bless( $href, $class );

	# load machines

	if( defined $href->{infile} )
	{
		$obj->load_machines_file( $href->{infile} );
	}

	if( defined $href->{machines} )
	{
		$obj->load_machines( $href->{machines} );
	}

	return $obj;
}

sub load_machines_file
{
	my ( $self, $pathfile ) = @_;

    open( my $infh, '<', $pathfile ) or
        die "Error opening $pathfile:\n$!";

	my %machines;

    while( my $line = <$infh> )
    {
        chomp $line;
        next if( $line =~ m/^\s*$/ );
		next if( $line =~ m/^#/ );

		my ( $name, $num_jobs ) = split( /\t/, $line );
		$num_jobs = ( defined $num_jobs ? $num_jobs : 1 );

		$machines{$name} += $num_jobs;
    }

    close $infh;

	$self->load_machines( \%machines );
}

sub load_machines
{
	my ( $self, $href ) = @_;

	if( ref $href ne 'HASH' )
	{
		Carp::carp "load_machines requires a hash ref as a parameter";
		return;
	}

	foreach my $machine ( keys %{ $href } )
	{
		my $machine_obj = $self->get_machine_by_name( $machine );

		if( defined $machine_obj )
		{
			# overwrite previous value
			$machine_obj->set_max_num_jobs( $href->{$machine} );
		}
		else
		{
			# create new Cluster::Machine object
			$machine_obj = Cluster::Machine->new( { name => $machine, max_num_jobs => $href->{$machine} } );
		}

		$self->{_Cluster_MachineQueue}{loaded_machines}{$machine} = $machine_obj;
	}

	return 1;
}

sub get_all_machines
{
	# returns a list of Cluster::Machine objects
	my ( $self ) = @_;
	return values %{ $self->{_Cluster_MachineQueue}{loaded_machines} };
}

sub get_available_machines
{
	# returns a list of Cluster::Machine objects
	my ( $self ) = @_;
	my @available = grep { $_->is_free() } $self->get_all_machines;
	return @available;
}

sub get_next_avail_machine
{
	# there could be a race condition here, where one piece of code gets a machine obj but
	# fails to update the job status of the machine before the next request to this sub
	# (not a big deal - if it happens the only effect is that there might be more than
	# $max jobs started on that machine

	my ( $self ) = @_;

	my $machine_obj = ( $self->get_available_machines() )[0];

	if( not defined $machine_obj )
	{
		return undef;
	}

    return $machine_obj;
}

sub get_machine_by_name
{
	my ( $self, $name ) = @_;

	if( (not defined $name) ||
		(not defined $self->{_Cluster_MachineQueue}{loaded_machines}{$name}) )
	{
		# Carp::carp "Machine name or object not defined\n";
		return;
	}

	return $self->{_Cluster_MachineQueue}{loaded_machines}{$name};
}

sub machine_status_table
{
	my ( $self ) = @_;

	my @mach_objs = $self->get_all_machines;

	@mach_objs = sort { $a->get_name cmp $b->get_name } @mach_objs;

	my $format = "%-15.15s %7.7s %7.7s\n";

	my $table = sprintf( $format, 'Name', 'Running', 'MaxJobs' );

	foreach my $mach_obj ( @mach_objs )
	{
		my $line = sprintf( $format,
								$mach_obj->get_name,
							   	$mach_obj->get_num_running_jobs,
							   	$mach_obj->get_max_num_jobs );

		$table .= $line;
	}

	return $table;
}

#*****************************************************************************************

1;

