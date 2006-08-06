
  
  create table nonsuperset_geneids as 
   (select distinct A.ege_geneid from ENTREZ_GENENAMES_PUBMED A where not exists (select B.ege_geneid from ENTREZ_GENENAMES B
   where A.ege_gene_name = B.ege_gene_name and A.ege_geneid = B.ege_geneid));
   
   create table superset_geneids as
   (select distinct A.ege_geneid from ENTREZ_GENENAMES_PUBMED A where not exists  
    (select distinct B.ege_geneid from nonsuperset_geneids B where A.ege_geneid = B.ege_geneid));
    
    
 
   delete from ENTREZ_GENENAMES_PUBMED where ege_geneid in (select ege_geneid from nonsuperset_geneids);
    delete from GENE_PMIDS where GPI_GENEID in (select ege_geneid from nonsuperset_geneids);

   create table temp_addnames as
   select A.* from ENTREZ_GENENAMES A where A.ege_geneid in (select ege_geneid from superset_geneids)
   and A.ege_gene_name not in (select B.ege_gene_name from ENTREZ_GENENAMES_PUBMED B
    where A.ege_geneid = B.ege_geneid);
    
    insert into ENTREZ_GENENAMES_PUBMED
    (select t.*,null from temp_addnames t);
    
    
 
    create table new_genes as 
    (select distinct A.ege_geneid from ENTREZ_GENENAMES A where not exists 
    (select distinct B.ege_geneid from ENTREZ_GENENAMES_PUBMED B where A.ege_geneid = B.ege_geneid));
    
    
    insert into ENTREZ_GENENAMES_PUBMED
    (select T.*,null from ENTREZ_GENENAMES T where T.ege_geneid in (select ege_geneid from new_genes));
    
    
    create table retired_genes as
    (select distinct A.ege_geneid from ENTREZ_GENENAMES_PUBMED A where not exists
    (select distinct B.ege_geneid from ENTREZ_GENENAMES B where A.ege_geneid = B.ege_geneid));
    
    delete from ENTREZ_GENENAMES_PUBMED
    where ege_geneid in (select ege_geneid from retired_genes);
    
    delete from GENE_PMIDS
    where gpi_geneid in (select ege_geneid from retired_genes);
  
    
    drop table nonsuperset_geneids;
    drop table superset_geneids;
    drop table temp_addnames ;
    drop table new_genes ;
    drop table retired_genes;
  
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
