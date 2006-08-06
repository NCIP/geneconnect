<!-- ============================================ -->
<!-- This section mapped from ASN.1 module NCBI-Gene -->
 
<!-- Elements used by other modules:
          Gene-ref -->
 
 
<!-- Elements referenced from other modules:
          Dbtag FROM NCBI-General -->
 
 
<!-- ============================================ -->
<!-- Definition of Gene-ref -->
 
 
<!--
**********************************************************************
 
  NCBI Genes
  by James Ostell, 1990
  version 0.8
 
**********************************************************************
*** Gene ***********************************************
*
*  reference to a gene
*
 -->
<!ELEMENT Gene-ref ( 
               Gene-ref_locus? ,
               Gene-ref_allele? ,
               Gene-ref_desc? ,
               Gene-ref_maploc? ,
               Gene-ref_pseudo? ,
               Gene-ref_db? ,
               Gene-ref_syn? )>
 
 
<!-- 
 Official gene symbol
 -->
<!ELEMENT Gene-ref_locus ( #PCDATA )>
 
<!-- 
 Official allele designation
 -->
<!ELEMENT Gene-ref_allele ( #PCDATA )>
 
<!-- 
 descriptive name
 -->
<!ELEMENT Gene-ref_desc ( #PCDATA )>
 
<!-- 
 descriptive map location
 -->
<!ELEMENT Gene-ref_maploc ( #PCDATA )>
 
<!-- 
 pseudogene
 -->
<!ELEMENT Gene-ref_pseudo %BOOLEAN; >
<!ATTLIST Gene-ref_pseudo value ( true | false ) "false" >
 
<!-- 
 ids in other dbases
 -->
<!ELEMENT Gene-ref_db ( Dbtag* )>
 
<!-- 
 synonyms for locus
 -->
<!ELEMENT Gene-ref_syn ( Gene-ref_syn_E* )>
 
 
<!ELEMENT Gene-ref_syn_E ( #PCDATA )>
 
 
 
 
