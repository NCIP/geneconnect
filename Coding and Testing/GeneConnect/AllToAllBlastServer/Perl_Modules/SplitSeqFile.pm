
=pod

This routine splits a sequence file containing many sequences into a number of files each
containing fewer sequences.  For example, it can split a file for a whole chromosome into
a series of files each containing no more than 100 seqs.

The split_file() routine takes a href as a param.  Required keys include 'infile' (path
and filename for the input file), 'infile_format' (the format of infile, see below),
and 'outfile_format'.  Optional parameters include 'seqs_per_file' (which can be set to
control the maximum number of seqs in each output file, 100 by default) and 'outpath'
(path for the output files, default is the path used for infile).

The output files from split_file() are named based on the input file, with '_part#.fa'
appended (where # is a number).

    Recognized sequence formats include:
          Fasta       FASTA format
          largefasta  FASTA format, support for very large seq files
          EMBL        EMBL format
          GenBank     GenBank format
          swiss       Swissprot format
          PIR         Protein Information Resource format
          GCG         GCG format
          raw         Raw format (one sequence per line, no ID)
          ace         ACeDB sequence format
          game        GAME XML format
          phd         phred output
          qual        Quality values (get a sequence of quality scores)
          Fastq       Fastq format
          SCF         SCF tracefile format
          ABI         ABI tracefile format
          ALF         ALF tracefile format
          CTF         CTF tracefile format
          ZTR         ZTR tracefile format
          PLN         Staden plain tracefile format
          EXP         Staden tagged experiment tracefile format

# Author: Robert Freimuth, July 2006
# Washington University

=cut

package SplitSeqFile;

use warnings;
use strict;

use Bio::SeqIO;
use Carp;
use File::Basename;
use File::Spec;

use Exporter;
our @ISA = ( "Exporter" );
#our @EXPORT = qw();
our @EXPORT_OK = qw( split_file );

our $VERSION = 0.01;

#**********************************************************************************************

sub split_file
{
	my ( $href ) = @_;

	validate_params( $href ) or return;

	$href->{basename} = File::Basename::basename( $href->{infile} );

# MUST ADD ERROR CHECKING TO THIS
	my $in_seqio = Bio::SeqIO->new( -file => $href->{infile},
									-format => $href->{infile_format} );

	my @outfiles;

	my ( $pathfile, $outfh ) = get_new_out_fh( $href->{outpath}, $href->{basename}, $href->{outfile_format} );
	push( @outfiles, $pathfile );

	my $seq_counter = 0;

	while( my $seq_obj = $in_seqio->next_seq() )
	{
	    print $outfh $seq_obj;
	    $seq_counter++;

		if( $seq_counter == $href->{seqs_per_file} )
		{
			( $pathfile, $outfh ) = get_new_out_fh( $href->{outpath}, $href->{basename}, $href->{outfile_format} );
			$seq_counter = 0;
			push( @outfiles, $pathfile );
		}
	}

	return @outfiles;
}

#**********************************************************************************************

BEGIN
{
	# this would probably be better as a closure

	my $file_counter = 1;

	sub get_new_out_fh
	{
		my ( $path, $basename, $format ) = @_;

		my $filename = join( '', $basename, '_part', $file_counter, '.fa' );
		my $pathfile = File::Spec->catfile( $path, $filename );

		my $fh = Bio::SeqIO->newFh( '-file' => '>' . $pathfile , '-format' => $format );

		$file_counter++;

		return( $pathfile, $fh );
	}
}

#**********************************************************************************************

sub validate_params
{
    my ( $href ) = @_;

    my $params_ok = 1;

	# check for required params

	foreach my $reqd_param qw( infile infile_format outfile_format )
	{
		if( not defined $href->{$reqd_param} )
		{
			Carp::carp "Error: $reqd_param is a required parameter";
			$params_ok = 0;
		}
	}

	# verify formats

    my @formats = qw( Fasta  largefasta  EMBL  GenBank  swiss  PIR  GCG  raw  ace  game
                      phd  qual  Fastq  SCF  ABI  ALF  CTF  ZTR  PLN  EXP  );

    my %fmts = map { lc($_) => 1 } @formats;

    foreach my $format ( $href->{infile_format}, $href->{outfile_format} )
    {
        if( not exists $fmts{ lc($format) } )
        {
            Carp::carp "Error: [$format] is not a recognized format";
            $params_ok = 0;
        }
    }

	# check seqs_per_file

	$href->{seqs_per_file} = $href->{seqs_per_file} || 100;

	if( $href->{seqs_per_file} =~ m/\D/ )
	{
		Carp::carp "Error: seqs_per_file must be a positive integer (using 100)";
		$href->{seqs_per_file} = 100;
	}

	# check outpath

	$href->{outpath} = $href->{outpath} || File::Basename::dirname( $href->{infile} );

    return $params_ok;
}

#**********************************************************************************************

1;
