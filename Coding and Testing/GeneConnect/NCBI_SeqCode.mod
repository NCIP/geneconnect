<!-- ============================================ -->
<!-- This section mapped from ASN.1 module NCBI-SeqCode -->
 
<!-- Elements used by other modules:
          Seq-code-table ,
          Seq-map-table ,
          Seq-code-set -->
 
 
<!-- ============================================ -->
<!-- Definition of Seq-code-type -->
 
 
<!--
$Revision: 1.1 $
  *********************************************************************
 
  These are code and conversion tables for NCBI sequence codes
  ASN.1 for the sequences themselves are define in seq.asn
 
  Seq-map-table and Seq-code-table REQUIRE that codes start with 0
    and increase continuously.  So IUPAC codes, which are upper case
    letters will always have 65 0 cells before the codes begin.  This
    allows all codes to do indexed lookups for things
 
  Valid names for code tables are:
    IUPACna
    IUPACaa
    IUPACeaa
    IUPACaa3     3 letter amino acid codes : parallels IUPACeaa
                   display only, not a data exchange type
    NCBI2na
    NCBI4na
    NCBI8na
    NCBI8aa
    NCBIstdaa
     probability types map to IUPAC types for display as characters
 sequence representations
 -->
<!ELEMENT Seq-code-type %ENUM; >
<!ATTLIST Seq-code-type value (
               iupacna |
               iupacaa |
               ncbi2na |
               ncbi4na |
               ncbi8na |
               ncbipna |
               ncbi8aa |
               ncbieaa |
               ncbipaa |
               iupacaa3 |
               ncbistdaa )  #REQUIRED >
 
 
 
<!-- Definition of Seq-map-table -->
 
 
<!--
 for tables of sequence mappings
 -->
<!ELEMENT Seq-map-table ( 
               Seq-map-table_from ,
               Seq-map-table_to ,
               Seq-map-table_num ,
               Seq-map-table_start-at? ,
               Seq-map-table_table )>
 
 
<!-- 
 code to map from
 -->
<!ELEMENT Seq-map-table_from ( Seq-code-type )>
 
<!-- 
 code to map to
 -->
<!ELEMENT Seq-map-table_to ( Seq-code-type )>
 
<!-- 
 number of rows in table
 -->
<!ELEMENT Seq-map-table_num ( %INTEGER; )>
 
<!-- 
 index offset of first element
 -->
<!ELEMENT Seq-map-table_start-at ( %INTEGER; )>
 
<!-- 
 table of values, in from-to order
 -->
<!ELEMENT Seq-map-table_table ( Seq-map-table_table_E+ )>
 
<!ELEMENT Seq-map-table_table_E ( %INTEGER; )>
 
 
<!-- Definition of Seq-code-table -->
 
 
<!-- 
 for names of coded values
 -->
<!ELEMENT Seq-code-table ( 
               Seq-code-table_code ,
               Seq-code-table_num ,
               Seq-code-table_one-letter ,
               Seq-code-table_start-at? ,
               Seq-code-table_table ,
               Seq-code-table_comps? )>
 
 
<!-- 
 name of code
 -->
<!ELEMENT Seq-code-table_code ( Seq-code-type )>
 
<!-- 
 number of rows in table
 -->
<!ELEMENT Seq-code-table_num ( %INTEGER; )>
 
<!-- 
 symbol is ALWAYS 1 letter?
 -->
<!ELEMENT Seq-code-table_one-letter %BOOLEAN; >
<!ATTLIST Seq-code-table_one-letter value ( true | false )  #REQUIRED >
 
<!-- 
 index offset of first element
 -->
<!ELEMENT Seq-code-table_start-at ( %INTEGER; )>
 
<!-- 
 an explanatory name or string
 -->
<!ELEMENT Seq-code-table_table ( Seq-code-table_table_E+ )>
 
<!-- 
 pointers to complement nuc acid
 -->
<!ELEMENT Seq-code-table_comps ( Seq-code-table_comps_E* )>
 
<!ELEMENT Seq-code-table_table_E ( 
               Seq-code-table_table_E_symbol ,
               Seq-code-table_table_E_name )>
 
 
<!-- 
 the printed symbol or letter
 -->
<!ELEMENT Seq-code-table_table_E_symbol ( #PCDATA )>
<!ELEMENT Seq-code-table_table_E_name ( #PCDATA )>
 
<!ELEMENT Seq-code-table_comps_E ( %INTEGER; )>
 
 
<!-- Definition of Seq-code-set -->
 
 
<!-- 
 for distribution
 -->
<!ELEMENT Seq-code-set ( 
               Seq-code-set_codes? ,
               Seq-code-set_maps? )>
 
<!ELEMENT Seq-code-set_codes ( Seq-code-table* )>
<!ELEMENT Seq-code-set_maps ( Seq-map-table* )>
 
 
 
 
 
 
