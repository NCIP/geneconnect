/*L
  Copyright Washington University at St. Louis

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/geneconnect/LICENSE.txt for details.
L*/

CREATE TABLE ENSEMBLGENE_ENTREZGENE
(
	ESE_ENSEMBLGENEID VARCHAR2(30),
	ESE_GENEID NUMBER(12),
	ESE_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESE_ENSEMBLGENEID, ESE_GENEID, ESE_LINKTYPE)
	
);

CREATE TABLE ENSEMBLGENE_ENSEMBLTRANS
(
	ESN_ENSBLGENEID VARCHAR2(30),
	ESN_ENSBLTRANSCRIPTID VARCHAR2(30),
	ESN_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESN_ENSBLGENEID, ESN_ENSBLTRANSCRIPTID, ESN_LINKTYPE)
);

CREATE TABLE ENSEMBLTRANS_ENSEMBLPROT
(
	ESP_ENSBLTRANSCRIPTID VARCHAR2(30),
	ESP_ENSBLPROTEINID VARCHAR2(30),
	ESP_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESP_ENSBLTRANSCRIPTID, ESP_ENSBLPROTEINID, ESP_LINKTYPE)
);

CREATE TABLE ENSEMBLGENE_UNIGENE
(
	EBU_ENSEMBLGENEID VARCHAR2(30),
	EBU_UNIGENEID VARCHAR2(30),
	EBU_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (EBU_ENSEMBLGENEID, EBU_UNIGENEID, EBU_LINKTYPE)
);

CREATE TABLE ENSEMBLTRANS_REFSEQMRNA
(
	ESR_ENSBLTRANSCRIPTID VARCHAR2(30),
	ESR_REFSEQMRNAID VARCHAR2(30),
	ESR_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESR_ENSBLTRANSCRIPTID, ESR_REFSEQMRNAID, ESR_LINKTYPE)
);

CREATE TABLE ENSEMBLPROT_REFSEQPROTEIN
(
	EPR_ENSBLPROTEINID VARCHAR2(30),
	EPR_REFSEQPROTEINID VARCHAR2(30),
	EPR_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (EPR_ENSBLPROTEINID, EPR_REFSEQPROTEINID, EPR_LINKTYPE)
);

CREATE TABLE ENSEMBLPROT_UNIPROT
(
	ESU_ENSBLPROTEINID VARCHAR2(30),
	ESU_UNIPROTID VARCHAR2(30),
	ESU_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESU_ENSBLPROTEINID, ESU_UNIPROTID, ESU_LINKTYPE)
);

CREATE TABLE ENTREZGENE_REFSEQMRNA ( 
  ENR_GENEID        NUMBER (12), 
  ENR_REFSEQMRNAID  VARCHAR2 (30), 
  ENR_LINKTYPE      VARCHAR2 (30),
  PRIMARY KEY (ENR_GENEID, ENR_REFSEQMRNAID, ENR_LINKTYPE)
  );

CREATE TABLE ENTREZGENE_ENSEMBLGENE ( 
  EEG_GENEID         NUMBER (12)   NOT NULL, 
  EEG_ENSEMBLGENEID  VARCHAR2 (30)  NOT NULL, 
  EEG_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( EEG_GENEID, EEG_ENSEMBLGENEID, EEG_LINKTYPE )
);

CREATE TABLE ENTREZ_UNIGENE ( 
  EUG_GENEID       NUMBER (12)   NOT NULL, 
  EUG_LOCAL_TAXID  NUMBER (12)   NOT NULL, 
  EUG_UGID         VARCHAR2 (23)  NOT NULL, 
  EUG_LINKTYPE     VARCHAR2 (30), 
  PRIMARY KEY ( EUG_GENEID, EUG_UGID, EUG_LINKTYPE  )
);

CREATE TABLE GENBANKMRNA_ENSEMBLTRANS (
	GME_GENBANKMRNAID VARCHAR(30),
	GME_ENSBLTRANSCRIPTID VARCHAR2(30),
	GME_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GME_GENBANKMRNAID, GME_ENSBLTRANSCRIPTID, GME_LINKTYPE)
);

CREATE TABLE GENBANKMRNA_REFSEQMRNA (
	GMR_GENBANKMRNAID VARCHAR(30),
	GMR_REFSEQMRNAID VARCHAR2(30),
	GMR_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GMR_GENBANKMRNAID, GMR_REFSEQMRNAID, GMR_LINKTYPE)
);

CREATE TABLE GENBANKPROT_ENSEMBLPROT (
	GPE_GENBANKPROTID VARCHAR(30),
	GPE_ENSBLPROTEINID VARCHAR2(30),
	GPE_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GPE_GENBANKPROTID, GPE_ENSBLPROTEINID, GPE_LINKTYPE)
);

CREATE TABLE GENBANKPROT_REFSEQPROTEIN (
	GPR_GENBANKPROTID VARCHAR(30),
	GPR_REFSEQPROTEINID VARCHAR2(30),
	GPR_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GPR_GENBANKPROTID, GPR_REFSEQPROTEINID, GPR_LINKTYPE)
);

CREATE TABLE GENBANKPROT_UNIPROT (
	GPU_GENBANKPROTID VARCHAR(30),
	GPU_UNIPROTID VARCHAR2(30),
	GPU_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GPU_GENBANKPROTID, GPU_UNIPROTID, GPU_LINKTYPE)
);

CREATE TABLE ORGANISM_TAXONOMYMAP ( 
  OTM_LOCAL_TAXID  NUMBER (12)   NOT NULL, 
  OTM_TAXID        NUMBER (12), 
  OTM_ORGNAME      VARCHAR2 (100), 
  PRIMARY KEY ( OTM_LOCAL_TAXID )
);

CREATE TABLE ORGANISM_TAXONOMYMAP_TMP ( 
  OTT_TAXID    NUMBER (12)   NOT NULL, 
  OTT_ORGNAME  VARCHAR2 (100), 
  PRIMARY KEY ( OTT_TAXID )
)TABLESPACE GENECONNECTDATA; 

CREATE TABLE ORGANISM_TAXONOMY_HISTORY ( 
  OTH_OLDTAXID  NUMBER (12)   NOT NULL, 
  OTH_NEWTAXID  NUMBER (12)   NOT NULL, 
  PRIMARY KEY ( OTH_OLDTAXID, OTH_NEWTAXID )
);

CREATE TABLE REFSEQMRNA_REFSEQPROTEIN ( 
  RER_REFSEQMRNAID  VARCHAR2 (30), 
  RER_REFSEQPROTID  VARCHAR2 (30), 
  RER_LINKTYPE      VARCHAR2 (30),
  PRIMARY KEY (RER_REFSEQMRNAID, RER_REFSEQPROTID, RER_LINKTYPE)
);

CREATE TABLE REFSEQMRNA_ENSEMBLTRANS ( 
  RET_REFSEQMRNAID         VARCHAR2 (30) NOT NULL, 
  RET_ENSBLTRANSCRIPTID  VARCHAR2 (30)  NOT NULL, 
  RET_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( RET_REFSEQMRNAID, RET_ENSBLTRANSCRIPTID, RET_LINKTYPE )
);

CREATE TABLE REFSEQPROTEIN_UNIPROT ( 
  REU_REFSEQPROTEINID         VARCHAR2 (30) NOT NULL, 
  REU_UNIPROTKBID  VARCHAR2 (30)  NOT NULL, 
  REU_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( REU_REFSEQPROTEINID, REU_UNIPROTKBID, REU_LINKTYPE )
);

CREATE TABLE REVISION_HISTORY ( 
  RVH_LOCAL_TAXID      NUMBER (12)   NOT NULL, 
  RVH_ENTREZ_VERSION   VARCHAR2 (30), 
  RVH_UNIGENE_VERSION  VARCHAR2 (30), 
  RVH_UNI_STS_VERSION  VARCHAR2 (30), 
  RVH_GO_VERSION       VARCHAR2 (30), 
  RVH_HML_VERSION      VARCHAR2 (30), 
  RVH_TAX_VERSION      VARCHAR2 (30), 
  RVH_DBSNP_VERSION    VARCHAR2 (30), 
  PRIMARY KEY ( RVH_LOCAL_TAXID )
);

CREATE TABLE SERVER_FILE_STATUS ( 
  SFS_ID           NUMBER (10), 
  SFS_MODIFY_DATE  DATE, 
  SFS_FILENAME     VARCHAR2 (50), 
  SFS_FILETYPE     VARCHAR2 (8), 
  SFS_DATABASE     VARCHAR2 (10), 
  SFS_PATH         VARCHAR2 (30), 
  REVISION_NUMBER  VARCHAR2 (30)
);

CREATE TABLE SERVER_STATUS ( 
  SST_ID                    NUMBER (10)   NOT NULL, 
  SST_EXECUTION_DATE        DATE, 
  SST_EXECUTION_MODE        CHAR (1), 
  SST_MACHINENAME           VARCHAR2 (30), 
  SST_PARSING_TIME          NUMBER (10), 
  SST_TOTAL_TIME            NUMBER (10), 
  SST_ERROR_COUNT           NUMBER (3), 
  SST_POSTWORK_ERROR_COUNT  NUMBER (3), 
  PRIMARY KEY ( SST_ID )
);

CREATE TABLE UNIGENE_ENTREZ ( 
  UEN_UNIGENEID     VARCHAR2 (30)  NOT NULL, 
  UEN_ENTREZGENEID  NUMBER (12)   NOT NULL, 
  UEN_LINKTYPE      VARCHAR2 (30), 
  PRIMARY KEY ( UEN_UNIGENEID, UEN_ENTREZGENEID, UEN_LINKTYPE )
);

CREATE TABLE UNIPROT_GENBANKPROTEIN ( 
  UGE_UNIPROTKBID       VARCHAR2 (30), 
  UGE_GENBANKPROTEINID  VARCHAR2 (30), 
  UGE_LINKTYPE          VARCHAR2 (30),
  PRIMARY KEY (UGE_UNIPROTKBID, UGE_GENBANKPROTEINID, UGE_LINKTYPE)
);

CREATE TABLE UNIPROT_REFSEQPROTEIN ( 
  URE_UNIPROTKBID      VARCHAR2 (30), 
  URE_REFSEQPROTEINID  VARCHAR2 (30), 
  URE_LINKTYPE         VARCHAR2 (30),
  PRIMARY KEY (URE_UNIPROTKBID, URE_REFSEQPROTEINID, URE_LINKTYPE)
);

CREATE TABLE UNIGENE_ENSEMBLGENE ( 
  UEG_UNIGENEID      VARCHAR2 (30)  NOT NULL, 
  UEG_ENSEMBLGENEID  VARCHAR2 (30)  NOT NULL, 
  UEG_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( UEG_UNIGENEID, UEG_ENSEMBLGENEID, UEG_LINKTYPE )
);
  
CREATE TABLE UNIPROT_ENSEMBLPROT ( 
  UEP_UNIPROTKBID   VARCHAR2 (30) NOT NULL, 
  UEP_ENSBLPROTEINID  VARCHAR2 (30)  NOT NULL, 
  UEP_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( UEP_UNIPROTKBID, UEP_ENSBLPROTEINID, UEP_LINKTYPE  )
);
  
CREATE TABLE REFSEQPROTEIN_ENSEMBLPROT (
  REP_REFSEQPROTEINID    VARCHAR2 (30) NOT NULL, 
  REP_ENSBLPROTEINID   VARCHAR2 (30)  NOT NULL, 
  REP_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( REP_REFSEQPROTEINID, REP_ENSBLPROTEINID, REP_LINKTYPE )
);
   
   