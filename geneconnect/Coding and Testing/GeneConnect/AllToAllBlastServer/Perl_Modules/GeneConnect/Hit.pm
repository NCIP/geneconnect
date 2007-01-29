package GeneConnect::Hit;

# This is a class that represents candidate hits in the GeneConnect alignment pipeline

# Author: Robert Freimuth

use strict;
use warnings;

use Carp;
use Data::Dumper;
use File::Spec;

our $VERSION = 0.01;
our $AUTOLOAD;

#**********************************************************************************************
#                                       Example of Use
#**********************************************************************************************

=pod


=cut

#**********************************************************************************************
#                                    Class Methods and Data
#**********************************************************************************************

# %fields contains the permitted field names for these objects

my %fields = map { $_ => 1 } qw(
	query_accnum
	sbjct_accnum
	query_name
	sbjct_name
	query_seq_filename
	sbjct_seq_filename
	sbjct_seq_len
	query_seq_len
	on_same_strand
	needle_num_matches
	needle_align_len
	needle_score
	needle_align_filename
);

#**********************************************************************************************
#                                       Public Methods
#**********************************************************************************************

sub new
{
	# Creates a new GeneConnect::Hit object.  Requires a hash ref as a parameter.

	my ( $class, $href ) = @_;

	if( not defined $href || ref( $href ) ne 'HASH' )
	{
		croak "GeneConnect::Hit->new() requires a hash ref as a parameter";
		return;
	}

    my $self  = { _permitted => \%fields };

	foreach my $key ( keys %{ $href } )
	{
		if( exists $self->{_permitted}{$key} )
		{
			$self->{$key} = $href->{$key};
		}
	}

	return bless( $href, $class );
}

sub AUTOLOAD
{
    my $self = shift;
    my $type = ref( $self ) or croak "$self is not an object";

    my $name = $AUTOLOAD;
    $name =~ s/.*://;   # strip fully-qualified portion

    if( not exists $self->{_permitted}->{$name} )
    {
        croak "Can't access '$name' field in class $type";
    }

    if( @_ )
    {
        return $self->{$name} = shift;
    }
    else
    {
        return $self->{$name};
    }
}

sub shortest_seq_len
{
	my ( $self ) = @_;

	foreach my $seqlen qw( query_seq_len sbjct_seq_len )
	{
		if( ! defined $self->{$seqlen} )
		{
			carp "$seqlen is not defined - cannot calculate shortest seq len";
			return undef;
		}
	}

	return( $self->{query_seq_len} < $self->{sbjct_seq_len} ?
			$self->{query_seq_len} : $self->{sbjct_seq_len}  );
}

#**********************************************************************************************

1;
