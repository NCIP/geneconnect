drop table ORGANISM_TAXONOMYMAP;
drop table ORGANISM_TAXONOMYMAP_TMP;
drop table ORGANISM_TAXONOMY_HISTORY;

drop table ensemblprotein_uniprot;
drop table ensemblgene_entrezgene;
drop table ensemblgene_unigene;
drop table ensembltranscript_refseqmrna;
drop table ensembltrans_ensemblprotein;
drop table ensemblprotein_refseqprotein;
drop table ensemblgene_ensembltranscript;
drop table refseqprotein_ensemblprotein;
drop table refseqmrna_ensembltranscript;
drop table uniprot_ensemblprotein;
drop table unigene;
drop table unigene_entrez;


/*UNIGENE*/
CREATE TABLE unigene_entrez (                      
  UNIGENE_GENEID varchar(23),  
  ENTREZ_GENEID number(12),      
  PRIMARY KEY  (UNIGENE_GENEID,ENTREZ_GENEID)    
);
CREATE TABLE unigene (                
   geneid varchar(30),        
   title varchar(30),                         
   gene varchar(30),    
   org varchar(30),       
   taxid varchar(20)      
 );                
         
         
/*ENSEMBL*/

create table ensemblprotein_uniprot
( ensemblprotein_id varchar2(30),
  uniprot_id varchar2(30));
 

create table ensemblgene_entrezgene
( ensemblgene_id varchar2(30),
  entrezgene_id varchar2(30));
 
create table ensemblgene_unigene
( ensemblgene_id varchar2(30),
  unigene_id varchar2(30));
 
 
create table ensembltranscript_refseqmrna
( ensembltranscript_id varchar2(30),
  refseqmrna_id varchar2(30));
 
create table ensembltrans_ensemblprotein
( ensembltranscript_id varchar2(30),
  ensemblprotein_id varchar2(30));
 

create table ensemblprotein_refseqprotein
( ensemblprotein_id varchar2(30),
  refseqprotein_id varchar2(30));
 
create table ensemblgene_ensembltranscript
( ensemblgene_id varchar2(30),
  ensembltranscript_id varchar2(30));
 
 
create table refseqprotein_ensemblprotein
( refseqprotein_id varchar2(30),
  ensemblprotein_id varchar2(30));
 

create table refseqmrna_ensembltranscript
( refseqmrna_id varchar2(30),
  ensembltranscript_id varchar2(30));
 

create table uniprot_ensemblprotein
( uniprot_id varchar2(30),
  ensemblprotein_id varchar2(30));
  
  
  
/*ORGANISM TAXONOMY*/
create table ORGANISM_TAXONOMYMAP
(
OTM_LOCAL_TAXID NUMBER(12),
OTM_TAXID NUMBER(12),
OTM_ORGNAME VARCHAR2(100),
PRIMARY KEY (OTM_LOCAL_TAXID)
);
create table ORGANISM_TAXONOMYMAP_TMP
(
OTT_TAXID NUMBER(12),
OTT_ORGNAME VARCHAR2(100),
PRIMARY KEY (OTT_TAXID)
);
create table ORGANISM_TAXONOMY_HISTORY
(
OTH_OLDTAXID NUMBER(12),
OTH_NEWTAXID NUMBER(12),
PRIMARY KEY (OTH_OLDTAXID,OTH_NEWTAXID)
); 