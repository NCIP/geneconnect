<!-- ============================================ -->
<!-- This section mapped from ASN.1 module NCBI-RNA -->
 
<!-- Elements used by other modules:
          RNA-ref ,
          Trna-ext -->
 
 
<!-- Elements referenced from other modules:
          Seq-loc FROM NCBI-Seqloc -->
 
 
<!-- ============================================ -->
<!-- Definition of RNA-ref -->
 
 
<!--
**********************************************************************
 
  NCBI RNAs
  by James Ostell, 1990
  version 0.8
 
**********************************************************************
*** rnas ***********************************************
*
*  various rnas
*
 minimal RNA sequence
 -->
<!ELEMENT RNA-ref ( 
               RNA-ref_type ,
               RNA-ref_pseudo? ,
               RNA-ref_ext? )>
 
 
<!-- 
 type of RNA feature
 -->
<!ELEMENT RNA-ref_type %ENUM; >
<!ATTLIST RNA-ref_type value ( 
               unknown |
               premsg |
               mRNA |
               tRNA |
               rRNA |
               snRNA |
               scRNA |
               snoRNA |
               other )  #REQUIRED >
<!ELEMENT RNA-ref_pseudo %BOOLEAN; >
<!ATTLIST RNA-ref_pseudo value ( true | false )  #REQUIRED >
 
<!-- 
 for tRNAs
 -->
<!ELEMENT RNA-ref_ext ( 
               RNA-ref_ext_name |
               RNA-ref_ext_tRNA )>
 
 
<!-- 
 for naming "other" type
 -->
<!ELEMENT RNA-ref_ext_name ( #PCDATA )>
<!ELEMENT RNA-ref_ext_tRNA ( Trna-ext )>
 
 
 
<!-- Definition of Trna-ext -->
 
 
<!-- 
 tRNA feature extensions
 -->
<!ELEMENT Trna-ext ( 
               Trna-ext_aa? ,
               Trna-ext_codon? ,
               Trna-ext_anticodon? )>
 
 
<!-- 
 aa this carries
 -->
<!ELEMENT Trna-ext_aa ( 
               Trna-ext_aa_iupacaa |
               Trna-ext_aa_ncbieaa |
               Trna-ext_aa_ncbi8aa |
               Trna-ext_aa_ncbistdaa )>
 
<!-- 
 codon(s) as in Genetic-code
 -->
<!ELEMENT Trna-ext_codon ( Trna-ext_codon_E* )>
 
<!-- 
 location of anticodon
 -->
<!ELEMENT Trna-ext_anticodon ( Seq-loc )>
 
<!ELEMENT Trna-ext_aa_iupacaa ( %INTEGER; )>
<!ELEMENT Trna-ext_aa_ncbieaa ( %INTEGER; )>
<!ELEMENT Trna-ext_aa_ncbi8aa ( %INTEGER; )>
<!ELEMENT Trna-ext_aa_ncbistdaa ( %INTEGER; )>
 
<!ELEMENT Trna-ext_codon_E ( %INTEGER; )>
 
 
 
 
 
