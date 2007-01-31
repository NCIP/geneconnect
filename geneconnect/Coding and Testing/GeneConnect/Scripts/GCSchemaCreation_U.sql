CREATE TABLE ENSEMBLGENE_ENTREZGENE_U
(
	ESE_ENSEMBLGENEID VARCHAR2(30),
	ESE_GENEID NUMBER(12),
	ESE_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESE_ENSEMBLGENEID, ESE_GENEID, ESE_LINKTYPE)
	
);

CREATE TABLE ENSEMBLGENE_ENSEMBLTRANS_U
(
	ESN_ENSBLGENEID VARCHAR2(30),
	ESN_ENSBLTRANSCRIPTID VARCHAR2(30),
	ESN_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESN_ENSBLGENEID, ESN_ENSBLTRANSCRIPTID, ESN_LINKTYPE)
);

CREATE TABLE ENSEMBLTRANS_ENSEMBLPROT_U
(
	ESP_ENSBLTRANSCRIPTID VARCHAR2(30),
	ESP_ENSBLPROTEINID VARCHAR2(30),
	ESP_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESP_ENSBLTRANSCRIPTID, ESP_ENSBLPROTEINID, ESP_LINKTYPE)
);

CREATE TABLE ENSEMBLGENE_UNIGENE_U
(
	EBU_ENSEMBLGENEID VARCHAR2(30),
	EBU_UNIGENEID VARCHAR2(30),
	EBU_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (EBU_ENSEMBLGENEID, EBU_UNIGENEID, EBU_LINKTYPE)
);

CREATE TABLE ENSEMBLTRANS_REFSEQMRNA_U
(
	ESR_ENSBLTRANSCRIPTID VARCHAR2(30),
	ESR_REFSEQMRNAID VARCHAR2(30),
	ESR_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESR_ENSBLTRANSCRIPTID, ESR_REFSEQMRNAID, ESR_LINKTYPE)
);

CREATE TABLE ENSEMBLPROT_REFSEQPROTEIN_U
(
	EPR_ENSBLPROTEINID VARCHAR2(30),
	EPR_REFSEQPROTEINID VARCHAR2(30),
	EPR_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (EPR_ENSBLPROTEINID, EPR_REFSEQPROTEINID, EPR_LINKTYPE)
);

CREATE TABLE ENSEMBLPROT_UNIPROT_U
(
	ESU_ENSBLPROTEINID VARCHAR2(30),
	ESU_UNIPROTID VARCHAR2(30),
	ESU_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (ESU_ENSBLPROTEINID, ESU_UNIPROTID, ESU_LINKTYPE)
);

CREATE TABLE ENTREZGENE_REFSEQMRNA_U ( 
  ENR_GENEID        NUMBER (12), 
  ENR_REFSEQMRNAID  VARCHAR2 (30), 
  ENR_LINKTYPE      VARCHAR2 (30),
  PRIMARY KEY (ENR_GENEID, ENR_REFSEQMRNAID, ENR_LINKTYPE)
  );

CREATE TABLE ENTREZGENE_ENSEMBLGENE_U ( 
  EEG_GENEID         NUMBER (12)   NOT NULL, 
  EEG_ENSEMBLGENEID  VARCHAR2 (30)  NOT NULL, 
  EEG_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( EEG_GENEID, EEG_ENSEMBLGENEID, EEG_LINKTYPE )
);

CREATE TABLE ENTREZ_UNIGENE_U ( 
  EUG_GENEID       NUMBER (12)   NOT NULL, 
  EUG_LOCAL_TAXID  NUMBER (12)   NOT NULL, 
  EUG_UGID         VARCHAR2 (23)  NOT NULL, 
  EUG_LINKTYPE     VARCHAR2 (30), 
  PRIMARY KEY ( EUG_GENEID, EUG_UGID, EUG_LINKTYPE  )
);

CREATE TABLE GENBANKMRNA_ENSEMBLTRANS_U (
	GME_GENBANKMRNAID VARCHAR(30),
	GME_ENSBLTRANSCRIPTID VARCHAR2(30),
	GME_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GME_GENBANKMRNAID, GME_ENSBLTRANSCRIPTID, GME_LINKTYPE)
);

CREATE TABLE GENBANKMRNA_REFSEQMRNA_U (
	GMR_GENBANKMRNAID VARCHAR(30),
	GMR_REFSEQMRNAID VARCHAR2(30),
	GMR_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GMR_GENBANKMRNAID, GMR_REFSEQMRNAID, GMR_LINKTYPE)
);

CREATE TABLE GENBANKPROT_ENSEMBLPROT_U (
	GPE_GENBANKPROTID VARCHAR(30),
	GPE_ENSBLPROTEINID VARCHAR2(30),
	GPE_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GPE_GENBANKPROTID, GPE_ENSBLPROTEINID, GPE_LINKTYPE)
);

CREATE TABLE GENBANKPROT_REFSEQPROTEIN_U (
	GPR_GENBANKPROTID VARCHAR(30),
	GPR_REFSEQPROTEINID VARCHAR2(30),
	GPR_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GPR_GENBANKPROTID, GPR_REFSEQPROTEINID, GPR_LINKTYPE)
);

CREATE TABLE GENBANKPROT_UNIPROT_U (
	GPU_GENBANKPROTID VARCHAR(30),
	GPU_UNIPROTID VARCHAR2(30),
	GPU_LINKTYPE VARCHAR2(30),
	PRIMARY KEY (GPU_GENBANKPROTID, GPU_UNIPROTID, GPU_LINKTYPE)
);

CREATE TABLE REFSEQMRNA_REFSEQPROTEIN_U ( 
  RER_REFSEQMRNAID  VARCHAR2 (30), 
  RER_REFSEQPROTID  VARCHAR2 (30), 
  RER_LINKTYPE      VARCHAR2 (30),
  PRIMARY KEY (RER_REFSEQMRNAID, RER_REFSEQPROTID, RER_LINKTYPE)
);

CREATE TABLE REFSEQMRNA_ENSEMBLTRANS_U ( 
  RET_REFSEQMRNAID         VARCHAR2 (30) NOT NULL, 
  RET_ENSBLTRANSCRIPTID  VARCHAR2 (30)  NOT NULL, 
  RET_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( RET_REFSEQMRNAID, RET_ENSBLTRANSCRIPTID, RET_LINKTYPE )
);

CREATE TABLE REFSEQPROTEIN_UNIPROT_U ( 
  REU_REFSEQPROTEINID         VARCHAR2 (30) NOT NULL, 
  REU_UNIPROTKBID  VARCHAR2 (30)  NOT NULL, 
  REU_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( REU_REFSEQPROTEINID, REU_UNIPROTKBID, REU_LINKTYPE )
);

CREATE TABLE UNIGENE_ENTREZ_U ( 
  UEN_UNIGENEID     VARCHAR2 (30)  NOT NULL, 
  UEN_ENTREZGENEID  NUMBER (12)   NOT NULL, 
  UEN_LINKTYPE      VARCHAR2 (30), 
  PRIMARY KEY ( UEN_UNIGENEID, UEN_ENTREZGENEID, UEN_LINKTYPE )
);

CREATE TABLE UNIPROT_GENBANKPROTEIN_U( 
  UGE_UNIPROTKBID       VARCHAR2 (30), 
  UGE_GENBANKPROTEINID  VARCHAR2 (30), 
  UGE_LINKTYPE          VARCHAR2 (30),
  PRIMARY KEY (UGE_UNIPROTKBID, UGE_GENBANKPROTEINID, UGE_LINKTYPE)
);

CREATE TABLE UNIPROT_REFSEQPROTEIN_U ( 
  URE_UNIPROTKBID      VARCHAR2 (30), 
  URE_REFSEQPROTEINID  VARCHAR2 (30), 
  URE_LINKTYPE         VARCHAR2 (30),
  PRIMARY KEY (URE_UNIPROTKBID, URE_REFSEQPROTEINID, URE_LINKTYPE)
);

CREATE TABLE UNIGENE_ENSEMBLGENE_U ( 
  UEG_UNIGENEID      VARCHAR2 (30)  NOT NULL, 
  UEG_ENSEMBLGENEID  VARCHAR2 (30)  NOT NULL, 
  UEG_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( UEG_UNIGENEID, UEG_ENSEMBLGENEID, UEG_LINKTYPE )
);
  
CREATE TABLE UNIPROT_ENSEMBLPROT_U( 
  UEP_UNIPROTKBID   VARCHAR2 (30) NOT NULL, 
  UEP_ENSBLPROTEINID  VARCHAR2 (30)  NOT NULL, 
  UEP_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( UEP_UNIPROTKBID, UEP_ENSBLPROTEINID, UEP_LINKTYPE  )
);
  
CREATE TABLE REFSEQPROTEIN_ENSEMBLPROT_U (
  REP_REFSEQPROTEINID    VARCHAR2 (30) NOT NULL, 
  REP_ENSBLPROTEINID   VARCHAR2 (30)  NOT NULL, 
  REP_LINKTYPE       VARCHAR2 (30)  NOT NULL, 
  PRIMARY KEY ( REP_REFSEQPROTEINID, REP_ENSBLPROTEINID, REP_LINKTYPE )
);