DROP TABLE GENE CASCADE CONSTRAINTS
;
DROP TABLE GENE_MRNA CASCADE CONSTRAINTS
;
DROP TABLE GENOMIC_IDENTIFIER_SET CASCADE CONSTRAINTS
;
DROP TABLE MRNA CASCADE CONSTRAINTS
;
DROP TABLE MRNA_PROTEIN CASCADE CONSTRAINTS
;
DROP TABLE PROTEIN CASCADE CONSTRAINTS
;
DROP TABLE PROTEIN_GENE CASCADE CONSTRAINTS
;
DROP TABLE SET_ONT CASCADE CONSTRAINTS
;



ALTER TABLE GENE_U RENAME TO GENE
;
ALTER TABLE GENE_MRNA_U RENAME TO GENE_MRNA
;
ALTER TABLE GENOMIC_IDENTIFIER_SET_U RENAME TO GENOMIC_IDENTIFIER_SET
;
ALTER TABLE MRNA_U RENAME TO MRNA
;
ALTER TABLE MRNA_PROTEIN_U RENAME TO MRNA_PROTEIN
;
ALTER TABLE PROTEIN_U RENAME TO PROTEIN
; 
ALTER TABLE PROTEIN_GENE_U RENAME TO PROTEIN_GENE
;
ALTER TABLE SET_ONT_U RENAME TO SET_ONT
;


INSERT INTO GENE VALUES(0,null,null,null);
INSERT INTO MRNA VALUES(0,null,null,null);
INSERT INTO PROTEIN VALUES(0,null,null,null,null);


ALTER TABLE GENE ADD CONSTRAINT PK_GENE 
	PRIMARY KEY (GENE_ID)
;

ALTER TABLE GENOMIC_IDENTIFIER_SET ADD CONSTRAINT PK_GENOMIC_IDENTIFIER_SET 
	PRIMARY KEY (SET_ID)
;

ALTER TABLE MRNA ADD CONSTRAINT PK_MRNA 
	PRIMARY KEY (MRNA_ID)
;

ALTER TABLE PROTEIN ADD CONSTRAINT PK_PROTEIN 
	PRIMARY KEY (PROTEIN_ID)
;



ALTER TABLE GENE_MRNA RENAME CONSTRAINT PK_GENE_MRNA_ID_U TO PK_GENE_MRNA_ID
;

ALTER TABLE GENE_MRNA ADD CONSTRAINT FK_GENE_MRNA_GENE 
	FOREIGN KEY (GENE_ID) REFERENCES GENE (GENE_ID)
;

ALTER TABLE GENE_MRNA ADD CONSTRAINT FK_GENE_MRNA_MRNA 
	FOREIGN KEY (MRNA_ID) REFERENCES MRNA (MRNA_ID)
;



ALTER TABLE GENOMIC_IDENTIFIER_SET ADD CONSTRAINT FK_GENOMIC_IDENTIFIER__PROTEIN 
	FOREIGN KEY (PROTEIN_ID) REFERENCES PROTEIN (PROTEIN_ID)
;

ALTER TABLE GENOMIC_IDENTIFIER_SET ADD CONSTRAINT FK_GENOMIC_IDENTIFIER_SET_GENE 
	FOREIGN KEY (GENE_ID) REFERENCES GENE (GENE_ID)
;

ALTER TABLE GENOMIC_IDENTIFIER_SET ADD CONSTRAINT FK_GENOMIC_IDENTIFIER_SET_MRNA 
	FOREIGN KEY (MRNA_ID) REFERENCES MRNA (MRNA_ID)
;

ALTER TABLE GENOMIC_IDENTIFIER_SET ADD CONSTRAINT FK_GENOMIC_IDENTIFIER_SOLUTION 
	FOREIGN KEY (SOLUTION_ID) REFERENCES SOLUTION (SOLUTION_ID)
;



ALTER TABLE MRNA_PROTEIN RENAME CONSTRAINT PK_MRNA_PROTEIN_ID_U TO PK_MRNA_PROTEIN_ID
;

ALTER TABLE MRNA_PROTEIN ADD CONSTRAINT FK_MRNA_PROTEIN_MRNA 
	FOREIGN KEY (MRNA_ID) REFERENCES MRNA (MRNA_ID)
;

ALTER TABLE MRNA_PROTEIN ADD CONSTRAINT FK_MRNA_PROTEIN_PROTEIN 
	FOREIGN KEY (PROTEIN_ID) REFERENCES PROTEIN (PROTEIN_ID)
;


ALTER TABLE PROTEIN_GENE RENAME CONSTRAINT PK_PROTEIN_GENE_ID_U TO PK_PROTEIN_GENE_ID
;

ALTER TABLE PROTEIN_GENE ADD CONSTRAINT FK_PROTEIN_GENE_GENE 
	FOREIGN KEY (GENE_ID) REFERENCES GENE (GENE_ID)
;

ALTER TABLE PROTEIN_GENE ADD CONSTRAINT FK_PROTEIN_GENE_PROTEIN 
	FOREIGN KEY (PROTEIN_ID) REFERENCES PROTEIN (PROTEIN_ID)
;



ALTER TABLE SET_ONT ADD CONSTRAINT FK_SET_ONT_ONT 
	FOREIGN KEY (PATH_ID) REFERENCES ONT (PATH_ID)
;

ALTER TABLE SET_ONT ADD CONSTRAINT FK_SET_ONT_SET 
	FOREIGN KEY (SET_ID) REFERENCES GENOMIC_IDENTIFIER_SET (SET_ID)
;

ALTER TABLE SET_ONT RENAME CONSTRAINT PK_SET_ONT_U TO PK_SET_ONT
;



ALTER INDEX GENE_INDEX  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX GENE_MRNA_INDEX_GENE_ID  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX GENE_MRNA_INDEX_MRNA_ID  REBUILD NOPARALLEL NOLOGGING 
;


ALTER INDEX MRNA_INDEX  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX MRNA_PROTEIN_INDEX_MRNA_ID  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX MRNA_PROTEIN_INDEX_PROTEIN_ID  REBUILD NOPARALLEL NOLOGGING 
;


ALTER INDEX PROTEIN_INDEX  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX PROTEIN_GENE_INDEX_PROTEIN_ID  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX PROTEIN_GENE_INDEX_GENE_ID  REBUILD NOPARALLEL NOLOGGING 
;


ALTER INDEX GENOMIC_IDENTIFIER_INDEX  REBUILD NOPARALLEL NOLOGGING 
;


ALTER INDEX SET_SETID_INDEX  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX SET_GENE_ID_INDEX  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX SET_MRNA_ID_INDEX  REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX SET_PROTEIN_ID_INDEX  REBUILD NOPARALLEL NOLOGGING 
;


ALTER INDEX SET_ONT_SETID_INDEX REBUILD NOPARALLEL NOLOGGING 
;
ALTER INDEX SET_ONT_PATHID_INDEX REBUILD NOPARALLEL NOLOGGING 
;





ANALYZE TABLE GENE COMPUTE STATISTICS
;
ANALYZE TABLE GENE_MRNA COMPUTE STATISTICS
;
ANALYZE TABLE GENOMIC_IDENTIFIER_SET COMPUTE STATISTICS
;
ANALYZE TABLE MRNA COMPUTE STATISTICS
;
ANALYZE TABLE MRNA_PROTEIN COMPUTE STATISTICS
;
ANALYZE TABLE PROTEIN COMPUTE STATISTICS
;
ANALYZE TABLE PROTEIN_GENE COMPUTE STATISTICS
;
ANALYZE TABLE SET_ONT COMPUTE STATISTICS
;
