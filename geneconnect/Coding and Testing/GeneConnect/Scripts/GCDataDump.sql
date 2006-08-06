/*
SQLyog - Free MySQL GUI v4.1
Host - 4.1.10-nt : Database - geneconnect
*********************************************************************
Server version : 4.1.10-nt
*/


create database if not exists `geneconnect`;

USE `geneconnect`;

/*Table structure for table `entrez_fly` */

drop table if exists `entrez_fly`;

CREATE TABLE `entrez_fly` (
  `EFY_GENEID` int(12) NOT NULL default '0',
  `EFY_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EFY_FLYID` varchar(100) NOT NULL default '',
  PRIMARY KEY  (`EFY_GENEID`,`EFY_FLYID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrez_genenames` */

drop table if exists `entrez_genenames`;

CREATE TABLE `entrez_genenames` (
  `EGE_GENEID` int(12) NOT NULL default '0',
  `EGE_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EGE_GENE_NAME` varchar(100) NOT NULL default '',
  PRIMARY KEY  (`EGE_GENEID`,`EGE_GENE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrez_goid` */

drop table if exists `entrez_goid`;

CREATE TABLE `entrez_goid` (
  `EGO_GENEID` int(12) NOT NULL default '0',
  `EGO_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EGO_GOID` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`EGO_GENEID`,`EGO_GOID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrez_map` */

drop table if exists `entrez_map`;

CREATE TABLE `entrez_map` (
  `EMA_GENEID` int(12) NOT NULL default '0',
  `EMA_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EMA_MAPID` varchar(30) NOT NULL default '',
  PRIMARY KEY  (`EMA_GENEID`,`EMA_MAPID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrez_omim` */

drop table if exists `entrez_omim`;

CREATE TABLE `entrez_omim` (
  `EOM_GENEID` int(12) NOT NULL default '0',
  `EOM_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EOM_OMIM` int(8) NOT NULL default '0',
  PRIMARY KEY  (`EOM_GENEID`,`EOM_OMIM`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrez_phenotype` */

drop table if exists `entrez_phenotype`;

CREATE TABLE `entrez_phenotype` (
  `EPH_GENEID` int(12) NOT NULL default '0',
  `EPH_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EPH_PHENOTYPE` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`EPH_GENEID`,`EPH_PHENOTYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrez_pmids` */

drop table if exists `entrez_pmids`;

CREATE TABLE `entrez_pmids` (
  `EPI_GENEID` int(12) NOT NULL default '0',
  `EPI_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EPI_PUBMEDID` int(10) NOT NULL default '0',
  PRIMARY KEY  (`EPI_GENEID`,`EPI_PUBMEDID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrez_sts` */

drop table if exists `entrez_sts`;

CREATE TABLE `entrez_sts` (
  `EST_GENEID` int(12) NOT NULL default '0',
  `EST_LOCAL_TAXID` int(12) default NULL,
  `EST_STSID` int(10) NOT NULL default '0',
  PRIMARY KEY  (`EST_GENEID`,`EST_STSID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrez_unigene` */

drop table if exists `entrez_unigene`;

CREATE TABLE `entrez_unigene` (
  `EUG_GENEID` int(12) NOT NULL default '0',
  `EUG_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EUG_UGID` varchar(23) NOT NULL default '',
  PRIMARY KEY  (`EUG_GENEID`,`EUG_UGID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `entrezgene` */

drop table if exists `entrezgene`;

CREATE TABLE `entrezgene` (
  `EGE_GENEID` int(12) NOT NULL default '0',
  `EGE_LOCAL_TAXID` int(12) NOT NULL default '0',
  `EGE_SYMBOL` varchar(30) default NULL,
  `EGE_GENE_NAME` varchar(100) default NULL,
  `EGE_SUMMARY` text,
  `EGE_CHROMOSOME_MAP` varchar(30) default NULL,
  `EGE_CHROMOSOME` varchar(30) default NULL,
  PRIMARY KEY  (`EGE_GENEID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/*Table structure for table `server_file_status` */

drop table if exists `server_file_status`;

CREATE TABLE `server_file_status` (
  `SFS_ID` int(10) unsigned default NULL,
  `SFS_MODIFY_DATE` date default NULL,
  `SFS_FILENAME` varchar(50) default NULL,
  `SFS_FILETYPE` varchar(8) default NULL,
  `SFS_DATABASE` varchar(10) default NULL,
  `SFS_PATH` varchar(30) default NULL,
  `REVISION_NUMBER` varchar(30) default NULL,
  KEY `FK_SERVER_FILE_STATUS` (`SFS_ID`),
  CONSTRAINT `FK_SERVER_FILE_STATUS` FOREIGN KEY (`SFS_ID`) REFERENCES `server_status` (`SST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `server_status` */

drop table if exists `server_status`;

CREATE TABLE `server_status` (
  `SST_ID` int(10) unsigned NOT NULL default '0',
  `SST_EXECUTION_DATE` date default NULL,
  `SST_EXECUTION_MODE` char(1) default NULL,
  `SST_MACHINENAME` varchar(30) default NULL,
  `SST_PARSING_TIME` int(10) unsigned default NULL,
  `SST_TOTAL_TIME` int(10) unsigned default NULL,
  `SST_ERROR_COUNT` int(10) unsigned default NULL,
  `SST_POSTWORK_ERROR_COUNT` int(10) unsigned default NULL,
  PRIMARY KEY  (`SST_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `system_termdata` */

drop table if exists `system_termdata`;

CREATE TABLE `system_termdata` (
  `STD_TERMID` varchar(30) NOT NULL default '',
  `STD_TERM` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`STD_TERMID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `system_termtree` */

drop table if exists `system_termtree`;

CREATE TABLE `system_termtree` (
  `STT_CHILD_TERMID` varchar(30) NOT NULL default '',
  `STT_PARENT_TERMID` varchar(30) NOT NULL default '',
  `STT_ISPARENT` char(1) NOT NULL default '',
  PRIMARY KEY  (`STT_CHILD_TERMID`,`STT_PARENT_TERMID`),
  CONSTRAINT `FK_SYSTEM_TERMTREE` FOREIGN KEY (`STT_CHILD_TERMID`) REFERENCES `system_termdata` (`STD_TERMID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `unigene` */

drop table if exists `unigene`;

CREATE TABLE `unigene` (
  `id` varchar(30) default NULL,
  `title` text,
  `gene` varchar(30) default 'NULL',
  `org` varchar(30) default NULL,
  `taxid` varchar(20) default NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `organism_taxonomy_history` */

drop table if exists `organism_taxonomy_history`;

CREATE TABLE `organism_taxonomy_history` (
  `OTH_OLDTAXID` int(12) NOT NULL default '0',
  `OTH_NEWTAXID` int(12) NOT NULL default '0',
  PRIMARY KEY  (`OTH_OLDTAXID`,`OTH_NEWTAXID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `organism_taxonomymap` */

drop table if exists `organism_taxonomymap`;

CREATE TABLE `organism_taxonomymap` (
  `OTM_LOCAL_TAXID` int(12) NOT NULL default '0',
  `OTM_TAXID` int(12) default NULL,
  `OTM_ORGNAME` varchar(100) default NULL,
  PRIMARY KEY  (`OTM_LOCAL_TAXID`),
  KEY `ID_ORGANISM_TAXONOMYMAP` (`OTM_TAXID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*Table structure for table `organism_taxonomymap_tmp` */

drop table if exists `organism_taxonomymap_tmp`;

CREATE TABLE `organism_taxonomymap_tmp` (
  `OTT_TAXID` int(12) NOT NULL default '0',
  `OTT_ORGNAME` varchar(100) default NULL,
  PRIMARY KEY  (`OTT_TAXID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;