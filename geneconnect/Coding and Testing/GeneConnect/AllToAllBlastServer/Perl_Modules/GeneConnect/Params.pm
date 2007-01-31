package GeneConnect::Params;

# This module provides an object interface to the data structure sent to the GeneConnect
# XML-RPC server.

# Author: Robert Freimuth, July 2006
# Washington University

use strict;
use warnings;

use Carp;

our $VERSION = 0.02;

#*****************************************************************************************
#                                       Example of Use
#*****************************************************************************************



#*****************************************************************************************
#                                       Public Methods
#*****************************************************************************************

sub new
{
	my ( $class, $href ) = @_;

	if( not defined $href || ref( $href ) ne 'HASH' )
	{
		croak "GeneConnect::Params->new() requires a hash ref as a parameter";
		return;
	}

	return bless( $href, $class );
}

#**************************************   PATHS   ****************************************

sub path_base
{
	return $_[0]->{PATHS}{base};
}

sub path_source
{
	return $_[0]->{PATHS}{source};
}

sub path_db
{
	return $_[0]->{PATHS}{db};
}

sub path_output
{
	return $_[0]->{PATHS}{output};
}

#*************************************   SRCFILE   ***************************************

sub source_file
{
	return $_[0]->{SRCFILE};
}

#**********************************   DESTINATIONDB   ************************************

sub target_db
{
	return $_[0]->{DESTINATIONDB};
}

#**************************************   BLAST   ****************************************

sub blast_program
{
	return $_[0]->{BLAST}{blastType};
}

sub blast_evalue
{
	return $_[0]->{BLAST}{EValue};
}

sub blast_matrix
{
	return $_[0]->{BLAST}{ScoringMatrix};
}

sub blast_gapopen
{
	return $_[0]->{BLAST}{GapOpenCost};
}

sub blast_gapextend
{
	return $_[0]->{BLAST}{GapExtensionCost};
}

sub blast_dropoff
{
	return $_[0]->{BLAST}{Dropoff};
}

sub blast_wordsize
{
	return $_[0]->{BLAST}{WordSize};
}

sub blast_seqtype
{
	return $_[0]->{BLAST}{seqType};
}

#***********************************   BLAST_FILTER   ************************************

sub blast_filter_min_pct_max_seq_align
{
	return $_[0]->{BLAST_FILTER}{PercentSequenceAlignment};
}

#**************************************   NEEDLE   ***************************************

sub needle_gapopen
{
	return $_[0]->{NEEDLE}{GapOpenCost};
}

sub needle_gapextend
{
	return $_[0]->{NEEDLE}{GapExtend};
}

sub needle_matrix
{
	return $_[0]->{NEEDLE}{Matrix};
}

#**********************************   NEEDLE_FILTER   ************************************

sub needle_filter_min_pct_max_align_len
{
	return $_[0]->{NEEDLE_FILTER}{PercentSequenceAlignment};
}

sub needle_filter_min_pct_sim
{
	return $_[0]->{NEEDLE_FILTER}{SimilarityScore};
}

sub needle_filter_min_pct_ident
{
	return $_[0]->{NEEDLE_FILTER}{IdentityScore};
}

sub needle_filter_max_mismat_per_kb
{
	return $_[0]->{NEEDLE_FILTER}{NumberMisMatch};
}

#**************************************   CONFIG   ***************************************

sub config_num_seqs_per_job
{
	return $_[0]->{CONFIG}{numSeqsPerJob};
}

sub config_save_align_files
{
	return $_[0]->{CONFIG}{saveAlignOutput};
}

sub config_control_file_header
{
	return $_[0]->{CONFIG}{controlFileHeader};
}

sub config_control_file_col_separator
{
	return $_[0]->{CONFIG}{controlFileColumnSeparator};
}

#**********************************************************************************************

1;
