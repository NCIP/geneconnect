<!-- ============================================ -->
<!-- This section mapped from ASN.1 module NCBI-Protein -->
 
<!-- Elements used by other modules:
          Prot-ref -->
 
 
<!-- Elements referenced from other modules:
          Dbtag FROM NCBI-General -->
 
 
<!-- ============================================ -->
<!-- Definition of Prot-ref -->
 
 
<!--
**********************************************************************
 
  NCBI Protein
  by James Ostell, 1990
  version 0.8
 
**********************************************************************
*** Prot-ref ***********************************************
*
*  Reference to a protein name
*
 -->
<!ELEMENT Prot-ref ( 
               Prot-ref_name? ,
               Prot-ref_desc? ,
               Prot-ref_ec? ,
               Prot-ref_activity? ,
               Prot-ref_db? ,
               Prot-ref_processed? )>
 
 
<!-- 
 protein name
 -->
<!ELEMENT Prot-ref_name ( Prot-ref_name_E* )>
 
<!-- 
 description (instead of name)
 -->
<!ELEMENT Prot-ref_desc ( #PCDATA )>
 
<!-- 
 E.C. number(s)
 -->
<!ELEMENT Prot-ref_ec ( Prot-ref_ec_E* )>
 
<!-- 
 activities
 -->
<!ELEMENT Prot-ref_activity ( Prot-ref_activity_E* )>
 
<!-- 
 ids in other dbases
 -->
<!ELEMENT Prot-ref_db ( Dbtag* )>
 
<!-- 
 processing status
 -->
<!ELEMENT Prot-ref_processed %ENUM; >
<!ATTLIST Prot-ref_processed value ( 
               not-set |
               preprotein |
               mature |
               signal-peptide |
               transit-peptide )  #REQUIRED >
 
<!ELEMENT Prot-ref_name_E ( #PCDATA )>
 
<!ELEMENT Prot-ref_ec_E ( #PCDATA )>
 
<!ELEMENT Prot-ref_activity_E ( #PCDATA )>
 
 
 
 
 
