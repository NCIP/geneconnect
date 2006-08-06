/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.globals.Variables</p> 
 */

package com.dataminer.server.globals;
import java.util.HashMap;
import java.util.Vector;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
/**
 * Description: Stores the global variables that are used in most of the
 * server classes.
 * @author      Meghana Chitale
 * @version     2.0
 */

public class Variables {
	
	/**This flag is true if unigene filename present in the final Command File.*/
	public static  boolean ugupdate = false;
	/**This flag is true if Locus Link filename present in the final Command File.*/
	public static  boolean llupdate = false;
	/**This flag is true if homologene filename present in the final Command File.*/
	public static  boolean hmlgupdate = false;
	public static  boolean goupdate = false;
	/**This flag is true if unists filename present in the final Command File.*/
	public static  boolean ustupdate = false;
	/**This flag is true if dbsnp filename present in the final Command File.*/
	public static  boolean dbsnpupdate = false;
	/**This flag is set to enable or disable the post work processing.*/
	public static  boolean postWork = true;
	/**This flag is set to indicate whether tables used by caCore like system should be populated in this run or not*/
	public static  boolean caCoreSystemPostWork = true;
	
	public static  boolean taxdmpUpdate = false;
	
	/**all the above values will be set to true based on the postwork processing to be done.*/
	public static  boolean homologenePostWork = false;
	public static  boolean geneinfoPostWork = false;
	public static  boolean geneinfo_summaryPostWork = false;
	public static  boolean geneinfo_marray_summaryPostWork = false;
	public static  boolean chipdescriptionPostWork = false;
	public static  boolean chipinfo_homoloPostWork = false;
	public static  boolean chipinfo_omimPostWork = false;
	public static  boolean chipinfo_termPostWork = false;
	
	/**this is set true so that create update tables is called.But no post work done for time being.*/ 
	public static long startJobTime;
	public static long endJobTime;
	public static long startParsingTime;
	public static long totalPrsingTime;
	public static long startJobTimeinSec;

	public static int batchValue = 100;
	/**temporary directory*/
	public static String tempDir;
	/** database related */
	/**database name*/
	public static String dbConnect = "CHIPDB.MGA.WUSTL.EDU";
	public static String dbName = "navsari"; 

	/**db user id and passwd*/
	public static String dbUserId;
	public static String dbUserPsswd;
	/**jdbc driver*/
	public static String driverName = "oracle.jdbc.driver.OracleDriver";
	public static String dbURL = "jdbc:oracle:oci8:@"+dbName;
	
	/**cmd file*/
	public static String CommandFile;
	
	public static String taxonomyCmdFile = "OrganismTaxonomyConfig.txt";
	public static String taxonomyFileNames = "OrganismTaxonomyFiles.txt";
	
	/**tells which mode - normal(1st time) or update mode*/
	public static boolean updateMode = false;
	public static HashSet updateDbList = new HashSet();
	
	public static boolean addChip = false;
	/**List of new chips*/
	public static Vector newChipList = new Vector();
	
	/**createDBSchema mode 3*/
	public static boolean createDBSchema = false;
	/**stores query time for parsing inserts*/
	public static long insertTime = 0;
	
	public static String[] databases = {"UNIGENE", "LOCUSLINK", "GO", "HOMOLOGENE","UNISTS","DBSNP"};
	public static int numberOfDbs = (databases.length+1);
	public static int numberOfFtpDbs = databases.length;
	public static int indexOfLocalFile = numberOfDbs;
	/**Hashtables to contain chip list*/
	public static Hashtable[] updateFiles = new Hashtable[numberOfDbs];
	
	/**Hashtables For ChipLibrary*/
	public static Hashtable chipLibFiles = new Hashtable();
	
	/**FTP information for 4 database types*/
	public static String[][] ftpInfo = new String[numberOfFtpDbs][4];
	/**flag to signify whether to rewrite the command file or not*/
	public static boolean modified = false;
	/**Stores mode number for the current server mode*/
	public static int mode = 0;
	
	/**UNIGENE TABLE NAMES*/
	public static String ugBaseTableName = "UNIGENE";
	public static String ugSequenceTableName = "UG_SEQUENCE";
	public static String ugProtsimTableName = "UG_PROTSIM";
	public static String ugExpressTableName =  "UG_EXPRESS";
	public static String ugHistoryTableName = "UNIGENE_HISTORY";
	
	/**ENTREZGENE TABLE NAMES*/
	public static String locusBaseTableName = "ENTREZGENE";
	public static String llUgTableName = "ENTREZ_UNIGENE";
	public static String llOmimTableName = "ENTREZ_OMIM";
	public static String llPmidTableName = "ENTREZ_PMIDS";
	public static String llGoidTableName = "ENTREZ_GOID";
	public static String llPhenotypeTableName = "ENTREZ_PHENOTYPE";
	public static String llExtAnnotTableName = "LOCUS_EXTANNOT";
	public static String llGeneNamesTableName = "ENTREZ_GENENAMES";
	public static String llMapTableName = "ENTREZ_MAP";
	public static String locusStsTableName = "ENTREZ_STS";
	public static String locusFlyTableName = "ENTREZ_FLY";
	public static String llGeneNamesCopyTableName = "ENTREZ_GENENAMES_PUBMED";
	public static String llHistoryTableName = "GENE_HISTORY";
	
	/**HOMOLOGENE TABLE NAMES*/
	public static String homologeneTableName = "HOMOLOGENE";
	public static String homologeneTempTableName = "HOMOLOGENE_TMP";
	
	/**ORGANISM TAXONOMYMAP TABLE NAMES*/
	public static String organismTaxonomyMapTableName = "ORGANISM_TAXONOMYMAP";
	public static String organismTaxonomyMapTmpTableName = "ORGANISM_TAXONOMYMAP_TMP";
	public static String organismTaxonomyHistoryTableName = "ORGANISM_TAXONOMY_HISTORY";
	
	/**HOMOLOGENE TABLE NAMES*/
	public static String homologeneXMLTableName = "HOMOLOGENE_XML";
	public static String orthologTableName = "ORTHOLOG";
	public static String orthologStartGeneName = "ORTHOLOGSTARTGENE";
	public static String homologeneTmpTableName = "HOMOLOGENE_TMP";
	
	/**UNISTS TABLE NAMES*/
	public static String uniStsBaseTableName = "UNISTS";
	public static String uniStsAliasTableName = "UNISTS_ALIAS";
	public static String uniStsAccessionTableName = "UNISTS_ACCESSION";
	
	/**DBSNP TABLE NAMES*/
	public static String dbSnpBaseTableName = "SNPTABLE";
	public static String dbSnpContigTableName = "CONTIG_INFO";
	public static String dbSnpContigMapTableName = "CONTIG_MAPLOC";
	public static String dbSnpFxnTableName = "FXNSET_CONTIGMAP";
	public static String dbSnpLocusTableName = "ENTREZ_SNP";
	
	public static String termTableName = "SYSTEM_TERMDATA";
	public static String treeTableName = "SYSTEM_TERMTREE";
	
	/**Decides if renaming of tables must take place.*/
	public static boolean noError = true;
	
	/**Decides if renaming of tables must take place by giving the error count.*/
	public static int errorCount=0;
	
	/**This variable acts as a flag for writing an entry into the ServerFileStatus Table.*/
	public static int postWorkErrorCount=0;
	
	/**Hash table that stores the name and last modified date of the files.*/
	public static Hashtable ftpFiles = new Hashtable();
	/**Hash table that stores the name and last modified date of the HTTP files.*/
	public static Hashtable httpFiles = new Hashtable();
	/**Hash table that stores the name and last modified date of the local files.*/
	public static Hashtable localFiles = new Hashtable();
	
	/** Hash Table to store the filename and revision history map per data source
	 * file. Later on this same information is also found in the below mentioned 
	 * maps but it is stored with referenece to the local organism taxonomy id */
	public static Hashtable fileRevisionHistory = new Hashtable();
	
	/**Hash Tables which stores the local taxid and the revision history for that 
	 * organism. If the source file type to be parsed explicitely does not provide 
	 * the revision or build number then the last modified date of the file will 
	 * be used in that place.*/
	public static Hashtable entrezGeneRevisionHistory = new Hashtable();
	public static Hashtable UniGeneRevisionHistory = new Hashtable();
	public static String UniSTSRevisionHistory;
	public static String goRevisionHistory;
	public static String homologeneRevisionHistory;
	public static String taxonomyRevisionHistory;
	public static Hashtable dbSNPRevisionHistory = new Hashtable() ;
	
	
	public static Hashtable filesData = new Hashtable();
	
	/**Table name of the Server file status table.*/
	public static String serverFileStatusTableName = "SERVER_FILE_STATUS";
	
	/** Table containing revision history about the gene information*/
	public static String revisionHistoryTableName = "REVISION_HISTORY";
	
	/**This Vector Keeps a list of fileInfo Objects.*/
	public static Vector fileInfoList = new Vector();
	/**
	 * Hash tables for obtaining and storing table space related data.
	 * The ones with "Bef" prefix contain data before renaming of _U tables
	 * While the ones with "Aft" prefix contain data after renaming of _U tables
	 */
	public static Hashtable freeSpaceBefDrop = new Hashtable();
	public static Hashtable allocSpaceBefDrop = new Hashtable();
	public static Hashtable freeSpaceAftDrop = new Hashtable();
	public static Hashtable allocSpaceAftDrop = new Hashtable();
	/**end of table space hashtables.
	* Server Ran From this machine name.*/
	public static String machineName;
	
	public static String[]multipleParserDB  = {"UNISTS"};
	/** hashtable containing mapping of organism names,taxonomy id's.*/
	public static Hashtable orgtaxMap = new Hashtable();
	
	public static Set unKnownOrgtaxMap = new HashSet();
	
	/** flag to signify whether to update the command file or not*/
	public static boolean updateOrgMapTable=false;
	
	public static String dbNameUrl = "CHIPDB.MGA.WUSTL.EDU";
	public static String dbLoginName = "chip_admin";
	public static String dbDriver = "oracle.jdbc.driver.OracleDriver";
	public static String cmdFile = "CMDFile.txt";
	
	public static String currentDir = System.getProperty("user.dir");
	public static String fileSep = System.getProperty("file.separator");
	
	/**Constants for mail sending utility.*/
	public static String toAddress = new String("washu@persistent.co.in");
	public static String ccAddress;
	public static String host = "smtp.persistent.co.in";
	public static String fromAddress = "washu@persistent.co.in";
	public static String password = "";
	public static String subject = "F.E. Server Update on ";
	public static String signature = "regards,\nServer Administrator";
	/**End of Constants for mail Sending Utility.*/
	
	public static String serverTables = "UNIGENE,UNIGENE_ENTREZ,ENTREZGENE,HOMOLOGENE," +
			"HOMOLOGENE_XML,GENEINFO,CHIPDESCRIPTION,CHIPINFORMATION,CHIPINFO_HOMOLO," +
	    		"CHIPINFO_OMIM,CHIPINFO_TERM,UNISTS,UNISTS_ALIAS,SNPTABLE,YSTEM_TERMDATA" +
	    		",SYSTEM_TERMTREE,GENE_HISTORY,UNIGENE_HISTORY,ORGANISM_TAXONOMYMAP," +
	    		"GENEINFO_MARRAY_SUMMARY,GENEINFO_SUMMARY";
	
	public static String caCoreSystemTables = "TISSUE_EXPRESSION,FE_UNIGENE,FE_UNIGENE_ENTREZGENE," +
			"PROBESET,GENE_PROBESET,ACCESSION,MICROARRAY,PROBESET_MICROARRAY,SNPDATA,STSDATA," +
			"GENE_STSDATA,PHENOTYPE,GENE_PHENOTYPE,GENE_GOTERM,CHROMOSOME,LITERATURE,GENE_LITERATURE," +
			"GENEVERSION,FE_OMIM,GENE_OMIM,FE_ENTREZGENE,FE_SYSTEM_TERMDATA,PROBESET_PROBESET,FE_HOMOLOGENE," +
			"ENTREZGENE_HOMOLOGENE"; 
	
	/**Constants for initialising Tablespaces.*/
	public static String refdataTblSpace = "REFDATA";
	
	/** The No.of Threads that Server will spawn.*/
	public static int noOfThreads = 1;
	
	public static int noOfPostWorkthreads = 3;
	/**Boolean variable for using SQL loader*/
	public static boolean useLoader = true;//false;
	
	/**data base specific settings required to take care when inserting dates(String)in diff.databases.*/ 
	public static String dbIdentifier = "Oracle";
	public static String dateString = "dd-MMM-yyyy";
	public static String dateFormat = null;
	public static String dateFunction = null;
	
	public static String dbSpecificNullCharacter = "";
	/**ortholog id to be used to add single gene groups which dont have any ortholog in other species*/
	public static long tempOrthologId = 0;
	
	public static String chipTableName = "CHIPINFORMATION";
	
	public static HashMap hmOrganismLocalId = new HashMap();
	public static HashMap hmTaxidLocalId = new HashMap();
	public static HashMap hmOrgAbbreviationName = new HashMap();
	
	public static HashMap hmPostWorkSeq = new HashMap();
	
	/**table names of summary tables*/
	public static String geneinfo_summaryTableName = "GENEINFO_SUMMARY"; 
	public static String chipdescriptionTableName = "CHIPDESCRIPTION";
	public static String geneinfoTableName = "GENEINFO";
	public static String chipinfoHomoloTableName = "CHIPINFO_HOMOLO";
	public static String geneinfo_marray_summaryTableName = "GENEINFO_MARRAY_SUMMARY"; 
	public static String chipinfoOmimTableName = "CHIPINFO_OMIM";
	public static String chipinfoTermTableName = "CHIPINFO_TERM";
	public static String chipinfoTermWithDuplicatesTableName = "CHIPINFO_TERM_DUPLICATES";
	
	public static HashMap serverProperties = new HashMap();
	
	public static boolean deleteDownloadedFiles = false;
	
	/** HomoloGene specific variable */
	public static long orthologId = 1; /** ortholog group id*/
	
	/**Added by Sachin **/
	/** check to process taxonomy*/
	public static boolean processTaxonomy=false;
	
}

