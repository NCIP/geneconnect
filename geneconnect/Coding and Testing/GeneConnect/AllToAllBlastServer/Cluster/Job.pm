package Cluster::Job;

# This is a class that contains a queue of jobs to run on the cluster

# Author: Robert Freimuth

use strict;
use warnings;

use Carp;
use Data::Dumper;
use File::Spec;

our $VERSION = 0.03;

#**********************************************************************************************
#                                       Example of Use
#**********************************************************************************************

=pod


=cut

#**********************************************************************************************
#                                    Class Methods and Data
#**********************************************************************************************

{
	my @queued_jobs;

	sub queue_job
	{
		my ( $self ) = @_;
		push( @queued_jobs, $self );
	}

	sub get_queued_job
	{
		return shift( @queued_jobs );
	}

	sub get_queue_size
	{
		return scalar @queued_jobs;
	}
}

#**********************************************************************************************
#                                       Public Methods
#**********************************************************************************************

sub new
{
	# Creates a new Cluster::Job object.  Requires a hash ref as a parameter.  The href
	# should contain the following keys:
	#    cmd_line = the command line to be run (<program name> <args>) [REQUIRED]
	#    infile   = the input filename [OPTIONAL]
	#    outfile  = the output filename [OPTIONAL]
	#    tempfile = the name of a temporary filename [OPTIONAL]
	#    pass_machine_id = if set to true (1), the machine ID will be added to the end
	#                      of the cmd line before the job is started [OPTIONAL]
	#	 no_ssh   = do not prepend 'ssh' to the cmd line [OPTIONAL](default = 0)
	#	 no_bg    = do not run the job in the background (append '&' to the cmd line)
	#				[OPTIONAL](default = 0)
	# Any additional keys can be included in $href, but no accessors are defined for them.

	my ( $class, $href ) = @_;

	if( not defined $href )
	{
		carp "Cluster::Job->new() requires a hash ref as a parameter";
		return;
	}

	if( not defined $href->{cmd_line} )
	{
		carp "Cannot create a new Cluster::Job object without a defined command line";
		return;
	}

	if( defined $href->{pass_machine_id} && $href->{pass_machine_id} )
	{
		$href->{pass_machine_id} = 1;
	}

	return bless( $href, $class );
}

sub get_cmd_line
{
	my ( $self ) = @_;
	return $self->{cmd_line};
}

sub get_infile
{
	my ( $self ) = @_;
	return $self->{infile};
}

sub get_outfile
{
	my ( $self ) = @_;
	return $self->{outfile};
}

sub get_tempfile
{
	my ( $self ) = @_;
	return $self->{get_tempfile};
}

sub set_machine
{
	my ( $self, $machine ) = @_;
	$self->{machine} = $machine;
	return $self->{machine};
}

sub get_machine
{
	my ( $self ) = @_;
	return $self->{machine};
}

sub get_pass_machine_id_flag
{
	my ( $self ) = @_;
	return $self->{pass_machine_id} || 0;
}

sub run_job
{
	my ( $self, $machine ) = @_;

	$machine = $machine ? $machine : '';

	# changed ssh and & so not hard-coded
	# this change is backwards-compatible, since the default is to use both

	my $cmd;

	if( $^O eq 'MSWin32' )
	{
		$cmd = join( ' ', 'cmd.exe', '/c', 'start',
						  $self->get_cmd_line,
						  $self->get_pass_machine_id_flag ? $machine : '' );
	}
	else
	{
		$cmd = join( ' ', $self->{no_ssh} ? '' : 'ssh',
						  $machine,
						  $self->get_cmd_line,
						  $self->get_pass_machine_id_flag ? $machine : '',
						  $self->{no_bg}  ? '' : '&' );
	}

	# can add creation of temp filename and renaming/deleting here,
	# but will need to change cmd lines, etc

	# the system call will return immediately if the job is being run in the background
	my $status = system( $cmd );
	return 1;
}

#**********************************************************************************************

1;
