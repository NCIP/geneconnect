#!/usr/local/bin/perl

# This program monitors and interacts with the GeneConnect XML-RPC server.

# Author: Robert Freimuth, July 2006
# Washington University

use strict;
use warnings;

use Data::Dumper;
use Frontier::Client;
use Getopt::Long;

use GeneConnect::Config 0.03 qw( get_config_params );

# get the path/file to the config file from the cmd line (optional, will use the env var otherwise)

my ( $config_pathfile );

GetOptions ( 'config_file_master:s' => \$config_pathfile );

my $params_href = GeneConnect::Config::get_config_params( $config_pathfile );

my $server = Frontier::Client->new( 
									url   => join( '',
												   'http://', $params_href->{'server.IP'},
											 	   ':', $params_href->{'server.port'},
											 	   '/RPC2' )
								   )
	or die "Error creating Frontier::Client object\n";

#*****************************************************************************************

my %menu = (
				1 => { text => 'Check machine status', sub => \&mach_status },
				2 => { text => 'Check size of job queues', sub => \&job_queue_size },
				3 => { text => 'Export DB (this could be BIG!)', sub => \&export_db },
				4 => { text => 'Free up a machine', sub => \&force_free_machine },
				5 => { text => 'Dump config options', sub => \&dump_config },
				6 => { text => 'Shut down the server', sub => \&shut_down_server },
				7 => { text => 'Quit this program', sub => \&quit },
			);

while( 1 )
{
	print "\n**************************************************\n";

	print_menu();

	print "\nPlease select an option: ";

	my $ans = <STDIN>;
	chomp $ans;

	if( exists $menu{$ans} )
	{
		$menu{$ans}{sub}->();
	}
	else
	{
		print "Invalid response\n";
	}
}

#*****************************************************************************************

sub print_menu
{
	print "\nAvailable functions:\n";

	foreach my $item ( sort { $a <=> $b } keys %menu )
	{
		print "   $item   $menu{$item}{text}\n";
	}
}

#*****************************************************************************************

sub mach_status
{
	my $result = $server->call( 'check_machine_status' );
	print $result;
}

sub job_queue_size
{
	my $result = $server->call( 'job_queue_size' );
	print $result;
}

sub export_db
{
	my $result = $server->call( 'export_db' );
	print Dumper( $result ); # could add print to file
}

sub force_free_machine
{
	print "Enter 'q' to quit at any time\n";

	print "  Enter the machine ID: ";
	my $machine_id = <STDIN>;
	chomp $machine_id;
	return if $machine_id eq 'q';

	print "  Enter the queue name: ";
	my $q_mgr = <STDIN>;
	chomp $q_mgr;
	return if $q_mgr eq 'q';

    

	print "  Enter the number of jobs to reduce the load by: ";
	my $num_jobs = <STDIN>;
	chomp $num_jobs;
	return if $num_jobs eq 'q';

	for( 1 .. $num_jobs )
	{
		print "\n Enter job Id : ";
		my $job_id =  <STDIN>;
	        chomp $job_id;

		print "\n Enter part file name : ";
                my $fileName =  <STDIN>;
                chomp $fileName;

		print "\n Kill process (true/false) ? ";
		my $kill_process = <STDIN>;
		chomp $kill_process;

		print "Freeing 1 job on $machine_id: ";
		my $new_num_running = $server->call( 'job_done_on_machine', $machine_id, $q_mgr, $job_id , $fileName, $kill_process );
		print "now $new_num_running jobs are running\n";
	}
}

sub dump_config
{
	print "The XML RPC Server Monitor was loaded with these parameters\n";
	printf "(from %s):\n", $config_pathfile ? $config_pathfile : 'the environment variable';
	print Dumper( $params_href ), "\n";
}

sub shut_down_server
{
	my $shut_down = 1;

	while( 1 )
	{
		print "\n\nWARNING: shutting down the server will clear the job queues!!\n";
		print "\nAre you sure you want to shut the server down? [Y/N] ";

		my $ans = <STDIN>;

		if( $ans =~ m/^n/i )
		{
			print "The server is still running\n";
			$shut_down = 0;
			last;
		}
		elsif( $ans =~ m/^y/i )
		{
			print "Shutting down the server\n";
			last;
		}
	}

	if( $shut_down )
	{
		# $no_prompt is set to true since we've already checked (above).  This
		# eliminates the need to reprompt on the server screen, or allows us to
		# shut down a server that is running in the background.
		# The call is wrapped in an eval to trap the fatal error thrown by the xml parser
		# that occurs because there is no return value from the server.

		eval { $server->call( 'shut_down_server', 1 ); };

		quit();
	}
}

sub quit
{
	print "Exiting...\n";
	exit;
}

