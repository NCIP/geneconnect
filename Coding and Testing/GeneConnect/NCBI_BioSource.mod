<!-- ============================================ -->
<!-- This section mapped from ASN.1 module NCBI-BioSource -->
 
<!-- Elements used by other modules:
          BioSource -->
 
 
<!-- Elements referenced from other modules:
          Org-ref FROM NCBI-Organism -->
 
 
<!-- ============================================ -->
<!-- Definition of BioSource -->
 
 
<!--
**********************************************************************
 
  NCBI BioSource
  by James Ostell, 1994
  version 3.0
 
**********************************************************************
********************************************************************
 
 BioSource gives the source of the biological material
   for sequences
 
********************************************************************
 -->
<!ELEMENT BioSource ( 
               BioSource_genome? ,
               BioSource_origin? ,
               BioSource_org ,
               BioSource_subtype? ,
               BioSource_is-focus? )>
 
 
<!-- 
 biological context
 -->
<!ELEMENT BioSource_genome ( %INTEGER; )>
<!ATTLIST BioSource_genome value ( 
               unknown |
               genomic |
               chloroplast |
               chromoplast |
               kinetoplast |
               mitochondrion |
               plastid |
               macronuclear |
               extrachrom |
               plasmid |
               transposon |
               insertion-seq |
               cyanelle |
               proviral |
               virion |
               nucleomorph |
               apicoplast |
               leucoplast |
               proplastid |
               endogenous-virus )  #IMPLIED >
 
<!-- 
 4 more genome values coming
 nucleomorph (15)
 apicoplast (16)
 leucoplast (17)
 proplastid (18)
 -->
<!ELEMENT BioSource_origin ( %INTEGER; )>
<!ATTLIST BioSource_origin value ( 
               unknown |
               natural |
               natmut |
               mut |
               artificial |
               synthetic |
               other )  #IMPLIED >
<!ELEMENT BioSource_org ( Org-ref )>
<!ELEMENT BioSource_subtype ( SubSource* )>
 
<!-- 
 to distinguish biological focus
 -->
<!ELEMENT BioSource_is-focus %NULL; >
 
 
 
 
<!-- Definition of SubSource -->
 
<!ELEMENT SubSource ( 
               SubSource_subtype ,
               SubSource_name ,
               SubSource_attrib? )>
 
<!ELEMENT SubSource_subtype ( %INTEGER; )>
<!ATTLIST SubSource_subtype value ( 
               chromosome |
               map |
               clone |
               subclone |
               haplotype |
               genotype |
               sex |
               cell-line |
               cell-type |
               tissue-type |
               clone-lib |
               dev-stage |
               frequency |
               germline |
               rearranged |
               lab-host |
               pop-variant |
               tissue-lib |
               plasmid-name |
               transposon-name |
               insertion-seq-name |
               plastid-name |
               country |
               segment |
               endogenous-virus-name |
               other )  #IMPLIED >
<!ELEMENT SubSource_name ( #PCDATA )>
 
<!-- 
 attribution/source of this name
 -->
<!ELEMENT SubSource_attrib ( #PCDATA )>
 
 
 
 
