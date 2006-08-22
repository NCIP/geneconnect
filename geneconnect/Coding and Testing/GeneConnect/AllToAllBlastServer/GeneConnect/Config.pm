package GeneConnect::Config;

# This module reads the configuration file for the GeneConnect XML RPC system.

# Author: Robert Freimuth, August 2006
# Washington University

use warnings;
use strict;

use Carp;
use Config::Simple; # same dependency as for FetchParams.pm

use Exporter;
our @ISA = ( "Exporter" );
our @EXPORT_OK = qw( get_config_params );

our $VERSION = 0.02;


#*****************************************************************************************
#                                     Load Config File
#*****************************************************************************************

my %cfg_env_vars = ( GENECONNECT_XMLRPC_CONFIG_MASTER => 'master',
					 GENECONNECT_XMLRPC_CONFIG_COMPUTE => 'compute' );

# ensure that both ENV variables have been set

foreach my $env_var ( keys %cfg_env_vars )
{
	if( not defined $ENV{$env_var} )
	{
		die "Environment variable $env_var not set";
	}
}

# Try to load the config file from the compute node first (since this will be done many
# more times than loading from the master).  If that doesn't work, try to load the
# config file from the master.

my %config;

eval
{
	Config::Simple->import_from( $ENV{GENECONNECT_XMLRPC_CONFIG_COMPUTE}, \%config )
		or die Config::Simple->error();
};

if( $@ )
{
	#warn "loading from compute failed:\n$@\n trying master\n";
	eval { Config::Simple->import_from( $ENV{GENECONNECT_XMLRPC_CONFIG_MASTER}, \%config ); };
}

_validate_config( \%config ) or die "Error loading config file: $@";

#*****************************************************************************************
#                                       Public Routines
#*****************************************************************************************

sub get_config_params
{
	return \%config;
}

#*****************************************************************************************
#                                       Private Routines
#*****************************************************************************************

sub _validate_config
{
	my ( $href ) = @_;

	# note the group name is prepended to each key, joined with a '.'
	my @reqd_keys = qw(
						server.URL
                        server.port
                        server.max_num_jobs
                        server.path_to_split_fasta_file
                        compute_nodes.machines_file_path
                        server.machine_name
                        compute_nodes.path_to_gc_seq_align_pipeline
                        server.IP
                        compute_nodes.machines_file_name
                        );

	my $ok = 1;

	foreach my $key ( @reqd_keys )
	{
		if( (not defined $href->{$key}) or ($href->{$key} eq '') )
		{
			warn "Required value ($key) missing in the config file";
			$ok = 0;
		}
	}

	return $ok;
}

#*****************************************************************************************

=pod

Example of %config:

$VAR1 = {
          'server.URL' => 'http://im-vishnu.wustl.edu',
          'server.port' => '4201',
          'server.max_num_jobs' => '1',
          'server.path_to_split_fasta_file' => '/home/clususer/geneconnect/XML_RPC_system',
          'compute_nodes.machines_file_path' => '/home/clususer/geneconnect/XML_RPC_system',
          'server.machine_name' => 'im-vishnu',
          'compute_nodes.path_to_gc_seq_align_pipeline' => '/state/partition1/geneconnect',
          'server.IP' => '128.252.161.213',
          'compute_nodes.machines_file_name' => 'machines.txt'
        };

=cut

#*****************************************************************************************

1;
