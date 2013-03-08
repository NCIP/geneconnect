package Cluster::MachineController;

# This is a class that contains a list of available machines on which jobs can be run

# This class could be cleaned up a bit.  Right now the object returned from new() is
# useless - all of the data is stored in the class hash.  A singleton approach is
# probably a better way to go, although this is already about 80% of the way there.

# Author: Robert Freimuth

use strict;
use warnings;

use Carp;

our $VERSION = 0.01;

#*****************************************************************************************
#                                       Example of Use
#*****************************************************************************************

=pod

methods:

Cluster::MachineController->new( { see comments below } );
Cluster::MachineController->load_machines( @machine_list );
Cluster::MachineController->get_all_machines();
Cluster::MachineController->get_available_machines();
Cluster::MachineController->free_machine( $machine );
Cluster::MachineController->load_machines_file( $pathfile );
my $machine = Cluster::MachineController->get_avail_machine()

=cut

#*****************************************************************************************
#                                    Class Methods and Data
#*****************************************************************************************

{
    my %avail_machines;

	sub load_machines
	{
		my ( $self, @machines ) = @_;

		foreach my $machine ( @machines )
		{
			print "Loading $machine\n";
			$avail_machines{$machine} = 1;
		}
	}

	sub get_all_machines
	{
		return keys %avail_machines;
	}

	sub get_available_machines
	{
    	my @available = grep { $avail_machines{$_} == 1 } keys %avail_machines;
    	return @available;
    }

	sub free_machine
	{
		my ( $self, $machine ) = @_;

		$self->_validate_machine( $machine ) or return;
		$avail_machines{$machine} = 1;
	}

	sub _make_machine_busy
	{
		my ( $self, $machine ) = @_;

		$self->_validate_machine( $machine ) or return;
		$avail_machines{$machine} = 0;
	}

	sub _validate_machine
	{
		my ( $self, $machine ) = @_;

		if( not defined $machine )
		{
			print "Machine ID not defined\n";
			return;
		}
		elsif( not exists $avail_machines{$machine} )
		{
			print "Cannot free unrecognized machine: $machine\n";
			return;
		}

		return 1;
	}
}

#*****************************************************************************************
#                                       Public Methods
#*****************************************************************************************

sub new
{
	# Creates a new Cluster::MachineController object.  Requires a hash ref as a
	# parameter.  The href should contain one of the following keys:
	#    infile   = a filename (with full path) that contains a list of machines to use
    #    machines = an array ref that contains a list of machines to use
    # If both keys are present, the union of the lists will be used.
	# Any additional keys can be included in $href, but no accessors are defined for them.

	my ( $class, $href ) = @_;

	if( not defined $href )
	{
		Carp::carp "Cluster::MachineController->new() requires a hash ref as a parameter";
		return;
	}

	if( (not defined $href->{infile}) and (not defined $href->{machines}) )
	{
		Carp::carp "Cannot create a new Cluster::MachineController object without specified machines";
		return;
	}

	my $obj = bless( $href, $class );

	if( defined $href->{infile} )
	{
		$obj->load_machines_file( $href->{infile} );
	}

	if( (defined $href->{machines}) && (ref( $href->{machines} ) eq 'ARRAY') )
	{
		$obj->load_machines( @{ $href->{machines} } );
	}

	return $obj;
}

sub load_machines_file
{
	my ( $self, $pathfile ) = @_;

    open( my $infh, '<', $pathfile ) or
        die "Error opening $pathfile:\n$!";

	my @machines;

    # read the first line (the total number of machines available) (not used)
    my $line = <$infh>;

    while( my $line = <$infh> )
    {
        chomp $line;
        next if( $line =~ m/^\s*$/ );
        push( @machines, $line );
    }

    close $infh;

	$self->load_machines( @machines );
}

sub get_avail_machine
{
	my ( $self ) = @_;

	my $machine = ( $self->get_available_machines() )[0];

	if( not defined $machine )
	{
		return undef;
	}

	$self->_make_machine_busy( $machine );

    return $machine;
}


#*****************************************************************************************

1;

