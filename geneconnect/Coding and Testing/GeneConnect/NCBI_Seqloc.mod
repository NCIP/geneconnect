<!-- ============================================ -->
<!-- This section mapped from ASN.1 module NCBI-Seqloc -->
 
<!-- Elements used by other modules:
          Seq-id ,
          Seq-loc ,
          Seq-interval ,
          Packed-seqint ,
          Seq-point ,
          Packed-seqpnt ,
          Na-strand ,
          Giimport-id -->
 
 
<!-- Elements referenced from other modules:
          Object-id ,
          Int-fuzz ,
          Dbtag ,
          Date FROM NCBI-General
          Id-pat FROM NCBI-Biblio
          Feat-id FROM NCBI-Seqfeat -->
 
 
<!-- ============================================ -->
<!-- Definition of Seq-id -->
 
 
<!--
  close homolog
 NOTE (1) length measurement of a reverse direction primer-extension
          product (blocked  by  RNA  5'end)  by  comparison with
          homologous sequence ladder (J. Mol. Biol. 199, 587)
$Revision: 1.1 $
**********************************************************************
 
  NCBI Sequence location and identifier elements
  by James Ostell, 1990
 
  Version 3.0 - 1994
 
**********************************************************************
*** Sequence identifiers ********************************
*
 -->
<!ELEMENT Seq-id ( 
               Seq-id_local |
               Seq-id_gibbsq |
               Seq-id_gibbmt |
               Seq-id_giim |
               Seq-id_genbank |
               Seq-id_embl |
               Seq-id_pir |
               Seq-id_swissprot |
               Seq-id_patent |
               Seq-id_other |
               Seq-id_general |
               Seq-id_gi |
               Seq-id_ddbj |
               Seq-id_prf |
               Seq-id_pdb |
               Seq-id_tpg |
               Seq-id_tpe |
               Seq-id_tpd )>
 
 
<!-- 
 local use
 -->
<!ELEMENT Seq-id_local ( Object-id )>
 
<!-- 
 Geninfo backbone seqid
 -->
<!ELEMENT Seq-id_gibbsq ( %INTEGER; )>
 
<!-- 
 Geninfo backbone moltype
 -->
<!ELEMENT Seq-id_gibbmt ( %INTEGER; )>
 
<!-- 
 Geninfo import id
 -->
<!ELEMENT Seq-id_giim ( Giimport-id )>
<!ELEMENT Seq-id_genbank ( Textseq-id )>
<!ELEMENT Seq-id_embl ( Textseq-id )>
<!ELEMENT Seq-id_pir ( Textseq-id )>
<!ELEMENT Seq-id_swissprot ( Textseq-id )>
<!ELEMENT Seq-id_patent ( Patent-seq-id )>
 
<!-- 
 catch all
 -->
<!ELEMENT Seq-id_other ( Textseq-id )>
 
<!-- 
 for other databases
 -->
<!ELEMENT Seq-id_general ( Dbtag )>
 
<!-- 
 GenInfo Integrated Database
 -->
<!ELEMENT Seq-id_gi ( %INTEGER; )>
 
<!-- 
 DDBJ
 -->
<!ELEMENT Seq-id_ddbj ( Textseq-id )>
 
<!-- 
 PRF SEQDB
 -->
<!ELEMENT Seq-id_prf ( Textseq-id )>
 
<!-- 
 PDB sequence
 -->
<!ELEMENT Seq-id_pdb ( PDB-seq-id )>
 
<!-- 
 Third Party Annot/Seq Genbank
 -->
<!ELEMENT Seq-id_tpg ( Textseq-id )>
 
<!-- 
 Third Party Annot/Seq EMBL
 -->
<!ELEMENT Seq-id_tpe ( Textseq-id )>
 
<!-- 
 Third Party Annot/Seq DDBJ
 -->
<!ELEMENT Seq-id_tpd ( Textseq-id )>
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
<!-- Definition of Patent-seq-id -->
 
<!ELEMENT Patent-seq-id ( 
               Patent-seq-id_seqid ,
               Patent-seq-id_cit )>
 
 
<!-- 
 number of sequence in patent
 -->
<!ELEMENT Patent-seq-id_seqid ( %INTEGER; )>
 
<!-- 
 patent citation
 -->
<!ELEMENT Patent-seq-id_cit ( Id-pat )>
 
 
 
<!-- Definition of Textseq-id -->
 
<!ELEMENT Textseq-id ( 
               Textseq-id_name? ,
               Textseq-id_accession? ,
               Textseq-id_release? ,
               Textseq-id_version? )>
 
<!ELEMENT Textseq-id_name ( #PCDATA )>
<!ELEMENT Textseq-id_accession ( #PCDATA )>
<!ELEMENT Textseq-id_release ( #PCDATA )>
<!ELEMENT Textseq-id_version ( %INTEGER; )>
 
 
<!-- Definition of Giimport-id -->
 
<!ELEMENT Giimport-id ( 
               Giimport-id_id ,
               Giimport-id_db? ,
               Giimport-id_release? )>
 
 
<!-- 
 the id to use here
 -->
<!ELEMENT Giimport-id_id ( %INTEGER; )>
 
<!-- 
 dbase used in
 -->
<!ELEMENT Giimport-id_db ( #PCDATA )>
 
<!-- 
 the release
 -->
<!ELEMENT Giimport-id_release ( #PCDATA )>
 
 
<!-- Definition of PDB-seq-id -->
 
<!ELEMENT PDB-seq-id ( 
               PDB-seq-id_mol ,
               PDB-seq-id_chain? ,
               PDB-seq-id_rel? )>
 
 
<!-- 
 the molecule name
 -->
<!ELEMENT PDB-seq-id_mol ( PDB-mol-id )>
 
<!-- 
 a single ASCII character, chain id
 -->
<!ELEMENT PDB-seq-id_chain ( %INTEGER; )>
 
<!-- 
 release date, month and year
 -->
<!ELEMENT PDB-seq-id_rel ( Date )>
 
 
 
<!-- Definition of PDB-mol-id -->
 
 
<!-- 
 name of mol, 4 chars
 -->
<!ELEMENT PDB-mol-id ( #PCDATA )>
 
 
 
<!-- Definition of Seq-loc -->
 
 
<!-- 
*** Sequence locations **********************************
*
 -->
<!ELEMENT Seq-loc ( 
               Seq-loc_null |
               Seq-loc_empty |
               Seq-loc_whole |
               Seq-loc_int |
               Seq-loc_packed-int |
               Seq-loc_pnt |
               Seq-loc_packed-pnt |
               Seq-loc_mix |
               Seq-loc_equiv |
               Seq-loc_bond |
               Seq-loc_feat )>
 
 
<!-- 
 not placed
 -->
<!ELEMENT Seq-loc_null %NULL; >
 
<!-- 
 to NULL one Seq-id in a collection
 -->
<!ELEMENT Seq-loc_empty ( Seq-id )>
 
<!-- 
 whole sequence
 -->
<!ELEMENT Seq-loc_whole ( Seq-id )>
 
<!-- 
 from to
 -->
<!ELEMENT Seq-loc_int ( Seq-interval )>
<!ELEMENT Seq-loc_packed-int ( Packed-seqint )>
<!ELEMENT Seq-loc_pnt ( Seq-point )>
<!ELEMENT Seq-loc_packed-pnt ( Packed-seqpnt )>
<!ELEMENT Seq-loc_mix ( Seq-loc-mix )>
 
<!-- 
 equivalent sets of locations
 -->
<!ELEMENT Seq-loc_equiv ( Seq-loc-equiv )>
<!ELEMENT Seq-loc_bond ( Seq-bond )>
 
<!-- 
 indirect, through a Seq-feat
 -->
<!ELEMENT Seq-loc_feat ( Feat-id )>
 
 
 
 
 
 
 
 
 
 
 
 
<!-- Definition of Seq-interval -->
 
<!ELEMENT Seq-interval ( 
               Seq-interval_from ,
               Seq-interval_to ,
               Seq-interval_strand? ,
               Seq-interval_id ,
               Seq-interval_fuzz-from? ,
               Seq-interval_fuzz-to? )>
 
<!ELEMENT Seq-interval_from ( %INTEGER; )>
<!ELEMENT Seq-interval_to ( %INTEGER; )>
<!ELEMENT Seq-interval_strand ( Na-strand )>
 
<!-- 
 WARNING: this used to be optional
 -->
<!ELEMENT Seq-interval_id ( Seq-id )>
<!ELEMENT Seq-interval_fuzz-from ( Int-fuzz )>
<!ELEMENT Seq-interval_fuzz-to ( Int-fuzz )>
 
 
 
 
 
<!-- Definition of Packed-seqint -->
 
<!ELEMENT Packed-seqint ( Seq-interval+ )>
 
 
 
<!-- Definition of Seq-point -->
 
<!ELEMENT Seq-point ( 
               Seq-point_point ,
               Seq-point_strand? ,
               Seq-point_id ,
               Seq-point_fuzz? )>
 
<!ELEMENT Seq-point_point ( %INTEGER; )>
<!ELEMENT Seq-point_strand ( Na-strand )>
 
<!-- 
 WARNING: this used to be optional
 -->
<!ELEMENT Seq-point_id ( Seq-id )>
<!ELEMENT Seq-point_fuzz ( Int-fuzz )>
 
 
 
 
<!-- Definition of Packed-seqpnt -->
 
<!ELEMENT Packed-seqpnt ( 
               Packed-seqpnt_strand? ,
               Packed-seqpnt_id ,
               Packed-seqpnt_fuzz? ,
               Packed-seqpnt_points )>
 
<!ELEMENT Packed-seqpnt_strand ( Na-strand )>
<!ELEMENT Packed-seqpnt_id ( Seq-id )>
<!ELEMENT Packed-seqpnt_fuzz ( Int-fuzz )>
<!ELEMENT Packed-seqpnt_points ( Packed-seqpnt_points_E+ )>
 
 
 
<!ELEMENT Packed-seqpnt_points_E ( %INTEGER; )>
 
 
<!-- Definition of Na-strand -->
 
 
<!-- 
 strand of nucleid acid
 -->
<!ELEMENT Na-strand %ENUM; >
<!ATTLIST Na-strand value ( 
               unknown |
               plus |
               minus |
               both |
               both-rev |
               other )  #REQUIRED >
 
 
 
<!-- Definition of Seq-bond -->
 
 
<!-- 
 bond between residues
 -->
<!ELEMENT Seq-bond ( 
               Seq-bond_a ,
               Seq-bond_b? )>
 
 
<!-- 
 connection to a least one residue
 -->
<!ELEMENT Seq-bond_a ( Seq-point )>
 
<!-- 
 other end may not be available
 -->
<!ELEMENT Seq-bond_b ( Seq-point )>
 
 
 
 
<!-- Definition of Seq-loc-mix -->
 
<!ELEMENT Seq-loc-mix ( Seq-loc+ )>
 
 
 
<!-- Definition of Seq-loc-equiv -->
 
<!ELEMENT Seq-loc-equiv ( Seq-loc+ )>
 
 
 
 
 
