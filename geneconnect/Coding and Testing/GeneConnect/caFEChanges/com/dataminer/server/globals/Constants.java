/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.globals.constanta</p> 
 */


package com.dataminer.server.globals;
/**
 * Description:  The global constants required by the package
 * @author       Meghana Chitale
 * @version      1.1
 */

public class Constants
{
	public static final String update = "_U";
	public static String fileSep = System.getProperty("file.separator");
	public static String StatusMailBody = fileSep + "Logs" + fileSep + "StatusMailBody.txt";
	public static String statusFileName = fileSep + "Logs" + fileSep + "FileStatus.txt";
	public static String loggerFileName = "ErrorLog.txt";
	
	public static String serverPropertiesFile = "server.properties";
	
	/** location and name of table creation scripts*/ 
	public static String feCreationFileOracle = Variables.currentDir + fileSep + "Scripts" + fileSep + "CreateFESchema_sql_Oracle.xml";
	public static String feCreationFileMysql = Variables.currentDir + fileSep + "Scripts" + fileSep + "CreateFESchema_sql_Mysql.xml";
	public static String feConstraintCreationFile = Variables.currentDir + fileSep + "Scripts" + fileSep + "ReferenceConstraints_sql.xml";
	public static String feConstraintDropFileOracle = Variables.currentDir + fileSep + "Scripts" + fileSep + "DropConstraintsOracle.xml";
	public static String feConstraintDropFileMysql = Variables.currentDir + fileSep + "Scripts" + fileSep + "DropConstraintsMysql.xml";
	
	/** location of scripts required for caCoreTables*/
	public static String caCoreSystemTables_creationFileMysql = Variables.currentDir + fileSep + "Scripts" + fileSep + "caCoreTables_createSchemaMysql.xml";
	public static String caCoreSystemTables_creationFileOracle = Variables.currentDir + fileSep + "Scripts" + fileSep + "caCoreTables_createSchemaOracle.xml";
	public static String caCoreSystemTables_dropConstraintsMysql = Variables.currentDir + fileSep + "Scripts" + fileSep + "caCoreTables_DropConstraintsMysql.xml";
	public static String caCoreSystemTables_dropConstraintsOracle = Variables.currentDir + fileSep + "Scripts" + fileSep + "caCoreTables_DropConstraintsOracle.xml";
	public static String caCoreSystemTables_constraintCreationOracle = Variables.currentDir + fileSep + "Scripts" + fileSep + "caCoreTables_RefConstraintsOracle.xml";
	public static String caCoreSystemTables_constraintCreationMysql = Variables.currentDir + fileSep + "Scripts" + fileSep + "caCoreTables_RefConstraintsMysql.xml";
	
	/* base table name */
	/** UNIGENE TABLE NAMES*/
	public static String ugBaseTableName = Variables.ugBaseTableName;
	public static final int ugBaseTableCols = 7;
	
	public static String ugSequenceTableName = Variables.ugSequenceTableName;
	public static final int ugSequenceTableCols = 4;
	
	public static String ugProtsimTableName = Variables.ugProtsimTableName;
	public static final int ugProtsimTableCols = 4;
	
	public static String ugExpressTableName =  Variables.ugExpressTableName;
	public static final int ugExpressTableCols = 2;
	
	public static String ugHistoryTableName =  Variables.ugHistoryTableName;
	public static final int ugHistoryTableCols = 3;
	
	/** dbSNP TABLE NAMES*/
	public static String dbSnpBaseTableName  = Variables.dbSnpBaseTableName;
	public static final int dbSnpBaseTableCols = 11;
	
	public static String dbSnpContigTableName = Variables.dbSnpContigTableName;
	public static final int dbSnpContigTableCols = 10;
	
	public static String dbSnpFxnTableName  = Variables.dbSnpFxnTableName;
	public static final int dbSnpFxnTableCols = 8;
	
	public static String dbSnpContigMapTableName = Variables.dbSnpContigMapTableName;
	public static final int dbSnpContigMapTableCols = 13;
	
	public static String dbSnpLocusTableName  = Variables.dbSnpLocusTableName;
	public static final int dbSnpLocusTableCols = 4;
	
	/** UNISTS TABLE NAMES*/
	public static String uniStsBaseTableName  = Variables.uniStsBaseTableName;
	public static final int uniStsTableCols = 3;
	
	public static String uniStsAliasTableName = Variables.uniStsAliasTableName;
	public static final int uniStsAliasTableCols = 2;
	
	public static String uniStsAccessionTableName = Variables.uniStsAccessionTableName;
	public static final int uniStsAccessionTableCols = 3;
	
	/** ENTREZGENE TABLE NAMES*/
	public static String locusBaseTableName = Variables.locusBaseTableName;
	public static final int llBaseTableCols = 7;
	
	public static String locusStsTableName = Variables.locusStsTableName;
	public static final int llStsTableCols = 4;
	
	public static String llUgTableName = Variables.llUgTableName;
	public static final int llUgTableCols = 3;
	
	public static String llOmimTableName = Variables.llOmimTableName;
	public static final int llOmimTableCols = 3;
	
	public static String llPmidTableName = Variables.llPmidTableName;
	public static final int llPmidTableCols = 3;
	
	public static String llGoidTableName =Variables.llGoidTableName;
	public static final int llGoidTableCols = 4;
	
	public static String llPhenotypeTableName = Variables.llPhenotypeTableName;
	public static final int llPhenotypeTableCols = 3;
	
	public static String llExtAnnotTableName = Variables.llExtAnnotTableName;
	public static final int llExtAnnotTableCols = 4;
	
	public static String llGeneNamesTableName = Variables.llGeneNamesTableName;
	public static final int llGeneNamesCols = 3;
	
	public static String llMapTableName = Variables.llMapTableName;
	public static final int llMapCols = 3;
	
	public static String locusFlyTableName = Variables.locusFlyTableName;
	public static final int llFlyCols = 2;
	
	public static String llHistoryTableName =  Variables.llHistoryTableName;
	public static final int llHistoryCols = 2;    
	
	public static String llGeneNamesTableCopyName = "ENTREZ_GENENAMES_COPY";
	public static final int llGeneNamesCopyCols = 3;
	
	public static String llPubMedIdTable = "LOCUS_PUBMEDIDS";
	public static final int llPubMedIdCols = 3;
	
	/** HOMOLOGENE TABLE NAMES*/
	public static String homologeneTableName = Variables.homologeneTableName;
	public static final int homologeneTableCols = 8;
	
	public static String homologeneXMLTableName = Variables.homologeneXMLTableName;
	public static final int homologeneXMLTableCols = 7;
	
	public static String orthologTableName = Variables.orthologTableName;
	public static final int orthologTableCols = 2;
	
	public static String orthologStartGeneName = Variables.orthologStartGeneName;
	public static final int orthologStartGeneCols = 3;
	
	public static String homologeneTmpTableName = Variables.homologeneTmpTableName;
	public static final int homologeneTmpTableCols = 6;
	
	public static String homologeneTempTableName = Variables.homologeneTempTableName;
	public static final int homologeneTempTableCols = 7;
	
	/** ORGANISM TAXONOMYMAP TABLE NAMES*/
	public static String organismTaxonomyMapTableName = Variables.organismTaxonomyMapTableName;
	public static final int organismTaxonomyMapTableCols = 2;
	
	/** TERM TABLE*/ 
	public static final String termTableName = Variables.termTableName;
	
	/** TREE TABLE*/
	public static final String treeTableName = Variables.treeTableName;
	
	/** CHIPINFORMATION TABLE*/
	public static final String chipTableName = "CHIPINFORMATION";
	
	/** LIST OF TABLES UPDATED BY UNIGENE DATA SOURCE*/
	public static final String ugTables [] =
	{
		"UG_SEQUENCE",
		"UG_PROTSIM",
		"UG_EXPRESS",
		"UNIGENE_HISTORY",
		"UNIGENE"
	};
	
	/** LIST OF TABLES UPDATED BY ENTREZGENE DATA SOURCE*/
	public static final String llTables [] =
	{
		"ENTREZGENE",                     
		"ENTREZ_FLY",                     
		"ENTREZ_GENENAMES",               
		"ENTREZ_GENENAMES_PUBMED",          
		"ENTREZ_GOID",                    
		"ENTREZ_MAP",                     
		"ENTREZ_OMIM",                    
		"ENTREZ_PHENOTYPE",               
		"ENTREZ_PMIDS",                   
		"ENTREZ_STS",                     
		"ENTREZ_UNIGENE",
		"GENE_HISTORY"
	};
	
	/** LIST OF TABLES UPDATED BY HOMOLOGENE DATA SOURCE*/
	public static final String hmlgTables [] =
	{
		"HOMOLOGENE_TMP",
		"HOMOLOGENE_XML",
		"ORTHOLOG",
		"ORTHOLOGSTARTGENE"
	};
	
	/** LIST OF TABLES UPDATED BY UNISTS DATA SOURCE*/
	public static final String unistsTables [] =
	{
		"UNISTS",
		"UNISTS_ALIAS",
		"UNISTS_ACCESSION"
	};
	
	
	/** SYSTEM TABLES HAVING TERM INFORMATION*/
	public static final String sysCopyTables [] =
	{
		"SYSTEM_TERMDATA",
		"SYSTEM_TERMTREE"
	};
	
	
	/** LIST OF TABLES UPDATED BY DBSNP DATA SOURCE*/
	public static final String dbSnpTables [] =
	{
		"FXNSET_CONTIGMAP",
		"CONTIG_MAPLOC",
		"CONTIG_INFO",
		"ENTREZ_SNP",
		"SNPTABLE"
	};
	
	public static final String geneinfo_summaryTables[] = 
	{
		"GENEINFO_SUMMARY"
	};
	
	public static final String chipdescriptionTables[] =
	{
		"CHIPDESCRIPTION"
	};
	
	public static final String geneinfoTables[] =
	{
		"GENEINFO"
	};
	
	public static final String chipinfo_homoloTables[] = 
	{
		"CHIPINFO_HOMOLO"
	};
	
	public static final String geneinfo_marray_summaryTables[] = 
	{
		"GENEINFO_MARRAY_SUMMARY"
	};
	
	public static final String chipinfo_omimTables[] = 
	{
		"CHIPINFO_OMIM"
	};
	
	public static final String chipinfo_termTables[] = 
	{
		"CHIPINFO_TERM",
		"CHIPINFO_TERM_DUPLICATES"
	};

	public static final String homolo_postworkTables [] =
	{
		"HOMOLOGENE"
	};

	
	/** Query to get free space in tablespaces.*/
	public static final String getfreeSpace="select TABLESPACE_NAME,sum(BYTES / (1024*1024)) from dba_free_space group by TABLESPACE_NAME ";
	/** Query to get allocated space to tablespaces.*/
	public static final String getAllocatedSpace="select TABLESPACE_NAME,sum(BYTES / (1024*1024)) from dba_data_files group by TABLESPACE_NAME";
	
	/** Get the difference between the chipinfo and geneinfo table counts in order to validate postwork*/
	public static final String getMaxId="select max(sst_id) from " + Constants.serverStatusTableName;
	public static final String checkChipinfoTableCount = "select count(*) from " + chipTableName;
	public static final String checktermtable1 = "select count(*) from system_termdata_u where std_termid like 'TISSUE_%'";
	public static final String checktermtable2 = " select count(*) from system_termdata_u where std_termid like 'MAP_%' ";
	public static final String checktermtable3 = " select count(*) from system_termdata_u where std_termid like 'GO:%' ";
	
	public static final String chipTypesTableName = "CHIPTYPES";
	public static final String serverStatusTableName = "SERVER_STATUS";
	public static final String serverFileStatusTableName = Variables.serverFileStatusTableName;
	
	/** TABLES WHOSE COUNT WILL BE ADDED IN THE MAIL SENT AFTER THE SERVER RUN*/
	public static final String countTableNames [] =
	{
		" UNIGENE ",
		" UNIGENE_ENTREZ ",
		" ENTREZGENE ",
		" HOMOLOGENE ",
		" HOMOLOGENE_XML ",
		" GENEINFO ",
		" CHIPDESCRIPTION ",
		" CHIPINFORMATION ",
		" CHIPINFO_HOMOLO ",
		" CHIPINFO_OMIM ",
		" CHIPINFO_TERM ",
		" UNISTS ",
		" UNISTS_ALIAS ",
		" SNPTABLE ",
		" SYSTEM_TERMDATA ",
		" SYSTEM_TERMTREE ",
		" GENE_HISTORY ",
		" UNIGENE_HISTORY ",
		" ORGANISM_TAXONOMYMAP ",
		" GENEINFO_MARRAY_SUMMARY",
		" GENEINFO_SUMMARY"
	};
	
	public static String caCoreSystemTables[] = 
	{
		"TISSUE_EXPRESSION",
		"FE_UNIGENE",
		"FE_UNIGENE_ENTREZGENE",
		"PROBESET",
		"GENE_PROBESET",
		"ACCESSION",
		"MICROARRAY",
		"PROBESET_MICROARRAY",
		"SNPDATA",
		"STSDATA",
		"GENE_STSDATA",
		"PHENOTYPE",
		"GENE_PHENOTYPE",
		"GENE_GOTERM",
		"CHROMOSOME",
		"LITERATURE",
		"GENE_LITERATURE",
		"GENEVERSION",
		"FE_OMIM",
		"GENE_OMIM",
		"FE_ENTREZGENE",
		"FE_SYSTEM_TERMDATA",
		"PROBESET_PROBESET",
		"FE_HOMOLOGENE",
		"ENTREZGENE_HOMOLOGENE"
	};
	
	public static String caCoreSequences[] =
	{
		"FE_UNIGENE_SEQUENCE",
		"PROBESET_SEQUENCE",
		"ACCESSION_SEQUENCE",
		"PHENOTYPE_SEQUENCE",
		"CHROMOSOME_SEQUENCE",
		"LITERATURE_SEQUENCE",
		"GENEVERSION_SEQUENCE",
		"FE_HOMOLOGENE_SEQUENCE"
	};
	
	/** constants added for generating orthologs*/
	public static float homologousCutOff = (float) 0.655;
	
	
	public static String getCountFromTable(String Table)
	{
		String query = "SELECT COUNT(*) FROM " + Table;
		return query;
	}
	
	public static String unigeneAbbreviatedOrgFile = "UnigeneOrganismAbbreviations.txt";

	public static String postWorkThreadSequence = "PostWorkExecutionSequence.txt"; 
	
	public static String columnSeparator = "###";
	
	/** queries for updating organism txonomymap table*/
	public static String queryOrganismTaxonomySync = 
		"UPDATE ORGANISM_TAXONOMYMAP SET OTM_TAXID = ( SELECT DISTINCT OTH_NEWTAXID FROM ORGANISM_TAXONOMY_HISTORY WHERE OTM_TAXID =OTH_OLDTAXID )";
	
	public static String queryTaxonomyMapMaxId = "SELECT MAX(OTM_ID) FROM ORGANISM_TAXONOMYMAP";
	
	public static String queryOrganismTaxonomyUpdate =
		"SELECT OTT_TAXID,OTT_ORGNAME FROM ORGANISM_TAXONOMYMAP_TMP WHERE OTT_TAXID NOT IN (SELECT OTM_TAXID FROM ORGANISM_TAXONOMYMAP)";
	public static String queryGetOrganismTaxonomyMap = 
		"SELECT OTM_TAXID FROM ORGANISM_TAXONOMYMAP ORDER BY OTM_TAXID";
	public static String queryGetOrganismTaxonomyMapTmp = 
		"SELECT * FROM ORGANISM_TAXONOMYMAP_TMP ORDER BY OTT_TAXID";
	
	public static String queryDeleteOrganismTaxonomyMapTmp = "DELETE FROM ORGANISM_TAXONOMYMAP_TMP";
	
	public static String queryDeleteTaxonomyHistory = "DELETE FROM ORGANISM_TAXONOMY_HISTORY";
	
	public static String queryFindModifiedTaxids = "SELECT OTH_NEWTAXID,OTH_OLDTAXID FROM ORGANISM_TAXONOMYMAP,ORGANISM_TAXONOMY_HISTORY WHERE OTM_TAXID=OTH_OLDTAXID";
	
	public static String queryReadTaxonomyMap = "SELECT OTM_LOCAL_TAXID,OTM_TAXID,OTM_ORGNAME FROM ORGANISM_TAXONOMYMAP";
	
	/**queries to set primary key constraints on the system table which are not added for oracle when 
	* tables are created from the select claues*/
	public static String querySystemTermdataPK = "ALTER TABLE SYSTEM_TERMDATA_U ADD CONSTRAINT PK_SYSTEM_TERMDATA_U PRIMARY KEY  (STD_TERMID)";
	
	public static String querysystemTermtreePK = "ALTER TABLE SYSTEM_TERMTREE_U ADD CONSTRAINT PK_SYSTEM_TERMTREE_U PRIMARY KEY  (STT_CHILD_TERMID, STT_PARENT_TERMID)";
	
	public static String queryServerStatusFK = "ALTER TABLE SERVER_FILE_STATUS ADD CONSTRAINT FK_SERVER_FILE_STATUS	FOREIGN KEY  (SFS_ID) REFERENCES SERVER_STATUS (SST_ID)";
	
	/** Defining string constants used in the code*/ 
	public static final String MYSQL = "Mysql";
	public static final String ORACLE = "Oracle";
	
	public static final int UPDATE_MODE = 1;
	public static final int ADD_CHIP_MODE = 2;
	public static final int CREATE_DB_SCHEMA_MODE = 3;
	
	public static final String GENE2XML = "gene2xml";
	
	public static final String FTP = "FTP";
	public static final String HTTP = "HTTP";
	
	/** String constants used to identify data sources*/
	public static final String UNIGENE = "UNIGENE";
	public static final String ENTREZGENE = "ENTREZGENE";
	public static final String HOMOLOGENE = "HOMOLOGENE";
	public static final String GO = "GO";
	public static final String UNISTS = "UNISTS";
	public static final String DBSNP = "DBSNP";
	public static final String CHIPINFORMATION = "CHIPINFORMATION";
	public static final String CAARRAY = "CAARRAY";
	public static final String TAXONOMY = "TAXONOMY"; 
	public static final String CACOREDATA = "CACOREDATA";
	public static final String TERMDATA = "TERMDATA";
	
	/** String constants used to identify postwork units*/
	public static final String GENEINFO = "GENEINFO";
	public static final String GENEINFO_SUMMARY = "GENEINFO_SUMMARY";
	public static final String GENEINFO_MARRAY_SUMMARY = "GENEINFO_MARRAY_SUMMARY";
	public static final String CHIPINFO_HOMOLO = "CHIPINFO_HOMOLO";
	public static final String CHIPDESCRIPTION = "CHIPDESCRIPTION";
	public static final String CHIPINFO_TERM = "CHIPINFO_TERM";
	public static final String CHIPINFO_OMIM = "CHIPINFO_OMIM";
	
	/** Names of properties read from server.properties file*/
	public static final String TOADDRESS = "toAddress";
	public static final String HOST = "host";
	public static final String FROMADDRESS = "fromAddress";
	public static final String MAIL_PASSWORD = "mailAccountpassword";
	public static final String SIGNATURE = "signature";
	public static final String SUBJECT = "subject";
	public static final String ANNOTATAION_TABLESPACE = "annotationtablespacename";
	public static final String CHIPDATA_TABLESPACE = "chipdatatablespacename";
	public static final String INDEX_TABLESPACE = "indextablespacename";
	public static final String NUMBER_OF_THREADS = "noofthreads";
	public static final String DBNAME = "databaseName";
	public static final String POSTWORK = "postWork";
	public static final String CACORE_TABLE_CREATION = "caCoreSystemTableCreation";
	
	/**Mode selection property for the server run */
	public static final String EXECUTION_MODE = "executionMode";
	
	/** Database connection parameters read from the server.properties file*/
	public static final String DATABASE_TYPE = "databaseType";
	public static final String DATABASE_USERNAME = "databaseLogin";
	public static final String DATABASE_PASSWORD = "databasePassword";
	public static final String DATABASE_URL = "databaseURL";
	public static final String DATABASE_DRIVER = "driverName";
	public static final String DATABASE_CONNECT = "databaseConnect";
	
	/** Property read to get the command file*/
	public static final String COMMAND_FILE_NAME = "commandFileName";
	
	/** Properties read for GO data source*/
	public static final String XML_ROOT = "xmlRoot";
	public static final String PARENT_TERMID = "parentTermid";
	public static final String PARENT_TERMNAME = "parentTermname";
	
	/** Homologene parser specific parameters*/
	public static final String CUT_OFF = "cutOff";
	
	/** Organism taxonomy related properties as read from server.properties file*/
	public static final String orgNames = "orgNamesFile";
	public static final String orgHistory = "orgHistoryFile";
	
	/** TABLES WHOSE COUNT WILL BE ADDED IN THE MAIL SENT AFTER THE SERVER RUN*/
	public static final String SERVER_TABLES = "serverTables";
	public static final String CACORE_SYSTEM_TABLES = "caCoreSystemTables";
	
	/** Parameter names for setting caArray database related parameters*/
	public static final String caArrayUserName = "caArrayUserName";
	public static final String caArraypassword = "caArraypassword";
	public static final String caArrayDatabaseUrl = "caArrayDatabaseUrl";
	
	/** Parameter indicating whether the FTPed files should be deleteed 
	 * after parsing or not */
	public static final String deleteDownloadedFiles = "deleteDownloadedFiles";
	
	/** Server run modes as stored in server_status table */
	public static final char CREATE_DATABASE_MODE_CHAR = 'C';
	public static final char ADD_CHIP_MODE_CHAR = 'A';
	public static final char UPDATE_MODE_CHAR = 'U';
	
	/** This string constant indicates the marker which will be searched at the end
	 * of each record in the command file pointing to particular source info */
	public static final String END_OF_RECORD_MARKER = "####";
	/** String constants used for identifying various sections in the command file record*/
	public static final String TYPE = "TYPE"; 
	public static final String PROXY = "PROXY";
	public static final String TRUE = "TRUE";
	public static final String PROXY_HOST = "PROXY_HOST";
	public static final String PROXY_PORT = "PROXY_PORT";
	public static final String SITE_URL = "SITE_URL";
	public static final String USER_ID = "USER_ID";
	public static final String PASSWORD = "PASSWD";
	public static final String BASE_DIR = "BASE_DIR";
	public static final String FILE = "FILE";
	public static final String DATABASE = "DATABASE";
	public static final String LOCAL_FILE = "LOCAL_FILE";
	public static final String INPUT_FORMAT = "INPUT_FORMAT";
	
	/** Taxonomy file name identifiers in configuration file*/
	public static final String TAXONOMY_NAMES_FILE = "NAMES";
	public static final String TAXONOMY_HISTORY_FILE = "HISTORY";
	
	/** Field to be read from caArray -> ontology entry for organism*/
	public static final String SPECIES = "species";
	
	/** The variables below are specific to EntrezGene parser */
	public static final String INITIALMAPTERMRECORD = "MAP_0###Chromosome Map###\n";
	public static final String INITIALMAPTREERECORD = "MAP_0###0###1###\n";
	public static final String ENTREZNODE = "Entrezgene";
	public static final String HISTORYFILE = "history";
	public static final int GOIDLENGTH = 7;
	
	/** Following variables are specific to HomoloGene parser */
	/** The following are the list of tags of XML nodes whose value we need to extract of */
	public static final String ALIGNMENTTAG = "HG-Stats_prot-change";
	public static final String GENETAG = "HG-Gene";
	public static final String GENEGENEIDTAG = "HG-Gene_geneid";
	public static final String GENETAXIDTAG = "HG-Gene_taxid";
	public static final String GENEPROTGIIDTAG = "HG-Gene_prot-gi";
	public static final String HOMOLOGENEENTRYDISTANCETAG = "HG-Entry_distances";
	public static final String STATSTAG = "HG-Stats";
	public static final String STATSGI1TAG = "HG-Stats_gi1";
	public static final String STATSGI2TAG = "HG-Stats_gi2";
	public static final String HOMOLOGENEENTRYTAG = "HG-Entry";
	public static final String HOMOLOGENEENTRYHGIDTAG = "HG-Entry_hg-id";
	public static final String STATSRECIPBESTTAG = "HG-Stats_recip-best";
	/**Contains the current Working Directory address.*/
	public static final String CWD = System.getProperty("user.dir");
	
	/**Added by Sachin*/
	/**Keys that will be contain in ApplicationConfig.properties*/
	public static final String TABLE_CREATION_FILE_ORACLE = "TABLE_CREATION_FILE_ORACLE";
	public static final String TABLE_CREATION_FILE_MYSQL = "TABLE_CREATION_FILE_MYSQL";
	public static final String CONSTRAINT_DROP_FILE_ORACLE = "CONSTRAINT_DROP_FILE_ORACLE";
	public static final String CONSTRAINT_DROP_FILE_MYSQL = "CONSTRAINT_DROP_FILE_MYSQL";
	public static final String CONSTRAINT_CREATION_FILE= "CONSTRAINT_CREATION_FILE";
	public static final String PROCESS_TAXONOMY = "processTaxonomy";
	
	/** Delimiter used by FileInfo object while forming list of all file names */
	public static final String DELIMITER = ",";
	
}


