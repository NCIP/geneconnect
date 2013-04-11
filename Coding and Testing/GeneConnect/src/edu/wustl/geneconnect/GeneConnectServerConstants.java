/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.GeneConnectServerConstants</p> 
 */

package edu.wustl.geneconnect;


/**
 * GeneConnect server constants
 * @author mahesh_nalkande
 * @version 1.0
 */
public interface GeneConnectServerConstants
{
	public String FIELD_DELIMITER = "#";
	public String COLUMN_NAMES_DELIMITER = ", ";
	public String PATH_NODES_DELIMITER = "_";
	public String LINK_TYPES_DELIMITER = "-"; 
	public String SUBPATH_IDS_DELIMITER = "-";
	public String ONT_IDS_DELIMITER = "-";
	public String DATASOURCE_CLASS_DETAILS_DELIMITER = "-";
	public String DATASOURCE_TABLE_DETAILS_DELIMITER = "-";
	
	public static String FILE_SEPARATOR = System.getProperty("file.separator");
	
	/** Constants for link types */
	static int DIRECT_LINK = 1;
	static int INFERRED_LINK = 2;
	static int IDENTITY_LINK = 4;
	static int ALIGNMENT_LINK = 8;
	
	public int GENE_CACHE_INITIAL_SIZE = 500000;
	public int MRNA_CACHE_INITIAL_SIZE = 500000;
	public int PROTEIN_CACHE_INITIAL_SIZE = 500000;
	
	//public int MAX_GENOMIC_ID_SET_RECORDS_TO_BE_PROCESSED = 5000000;
	//public int MAX_GENOMIC_ID_SET_RECORDS_TO_BE_PROCESSED = 10000;
	public int MAX_GENOMIC_ID_SET_RECORDS_TO_BE_PROCESSED = 1000000;
	
	public static final int STEP_1_FOR_UNNORMALIZED_SET_ONT = 1;
	public static final int STEP_2_FOR_UNIQUE_GENES = 2;
	public static final int STEP_3_FOR_UNIQUE_MRNAS = 3;
	public static final int STEP_4_FOR_UNIQUE_PROTEINS = 4;
	public static final int STEP_5_FOR_UNIQUE_SET_ONT = 5;
	
	public String SCRIPTS_FOLDER_NAME = "Scripts";
	public String METADATA_INDEXES_REBUILD_SCRIPT_FILENAME = "GCMetadataIndexesRebuild.sql";
	public String SUMMARY_TABLES_RENAME_SCRIPT_FILENAME = "GCSummary_U_TablesRename.sql";
	public String SUMMARY_U_TABLES_CREATION_SCRIPT_FILENAME  = "GCSummary_U_TablesCreation.sql";
	public String BASE_U_TABLES_RENAME_SCRIPT_FILENAME = "GCSchema_U_TablesRename.sql";
	public String BASE_U_TABLES_CREATION_SCRIPT_FILENAME = "GCSchemaCreation_U.sql";
	
	public String GENE_DATA_FILENAME = "Gene_direct.dat";
	public String MRNA_DATA_FILENAME = "mRNA_direct.dat";
	public String PROTEIN_DATA_FILENAME = "Protein_direct.dat";

	public String GENE_MRNA_DATA_FILENAME = "Gene_Mrna.dat";
	public String MRNA_PROTEIN_DATA_FILENAME = "Mrna_Protein.dat";
	public String PROTEIN_GENE_DATA_FILENAME = "Protein_Gene.dat";
	
	public String GENOMIC_IDENTIFIER_SET_DATA_FILENAME = "GenomicIdentifierSet_direct.dat";
	public String SET_ONT_DATA_FILENAME = "Set_Ont.dat";
	
	public String GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP1_FILENAME = "GenomicIdentifierSet_Ont_Step1_direct.dat";
	public String GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP2_FILENAME = "GenomicIdentifierSet_Ont_Step2_direct.dat";
	public String GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP3_FILENAME = "GenomicIdentifierSet_Ont_Step3_direct.dat";
	public String GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP4_FILENAME = "GenomicIdentifierSet_Ont_Step4_direct.dat";
	
	
	public String NON_REDUNDANT_LONGEST_PATHS_FILE_NAME = "NonRedundantLongestPaths.txt";
	
	/** Constants for newlyAdded attribute of GenomicIdentifierSetOnt */
	static byte NEW_PROTEIN_ADDED = 1;
	static byte NEW_MRNA_ADDED = 2;
	static byte NEW_GENE_ADDED = 4;
	
	public static int MAX_INDEX_NAME_LENGTH = 30;
}
