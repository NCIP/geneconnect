
/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.MetadataManager</p> 
 */
package edu.wustl.geneconnect.utility;

/**
 * This class stroed the constants used through out the application
 * @author sachin_lale
 *
 */
public class Constants
{

	/** 
	 * Constants for table and column names
	 */
	public static final String ATTRIBUTE = "ATTRIBUTE";
	public static final String OUTPUT_ATTRIBUTE = "OUTPUT_ATTRIBUTE";
	public static final String CLASS = "CLASS";
	public static final String GENOMIC_IDENTIFIER_CLASS = "GENOMIC_IDENTIFIER_CLASS";
	public static final String TYPE = "ATTRIBUTE_TYPE";
	public static final String SOURCE_CLASS = "SOURCE_CLASS";
	public static final String TARGET_CLASS = "TARGET_CLASS";
	public static final String ROLE_NAME = "ROLE_NAME";
	
	
	public static final String DATASOURCE_TABLE = "DATASOURCE";
	public static final String DATASOURCE_NAME = "DATASOURCE_NAME";
	public static final String DATASOURCE_ID = "DATASOURCE_ID";
	
	public static final String ONT_LINKTYPE_ID = "LINKTYPE_ID";
	public static final String ONT_PATH_ID = "PATH_ID";
	public static final String ONT_DATASOURCE_ID = "DATASOURCE_ID";
	public static final String ONT_NEXT_PATH_ID = "NEXT_PATH_ID";
	public static final String ONT_PREV_PATH_ID = "PREV_PATH_ID";
	public static final String ONT_SOURCE_DS_ID = "SOURCE_DS_ID";
	public static final String ONT_TABLE = "ONT";
	
	public static final String LINKTYPE_TABLE = "LINKTYPE";
	public static final String LINK_TYPE_ID = "LINK_TYPE_ID";
	public static final String LINK_TYPE_NAME = "LINK_TYPE_NAME";
	
	/** 
	 * Constants Of domain class names
	 */
	public static final String GENOMICIDENTIFIERSET_CLASS_NAME = "GenomicIdentifierSet";
	public static final String GENE_CLASS_NAME = "Gene";
	public static final String PROTEIN_CLASS_NAME = "Protein";
	public static final String MRNA_CLASS_NAME = "MessengerRNA";
	public static final String CONSENSUS_IDENTIFIERDATA_CLASS_NAME = "ConsensusIdentifierData";
	public static final String ONT_CLASS_NAME = "OrderOfNodeTraversal";
	public static final String DATASOURCE_CLASS_NAME = "DataSource";
	public static final String LINKTYPE_CLASS_NAME = "LinkType";
	public static final String DOMAIN_CLASSNAME_PREFIX = "edu.wustl.geneconnect.domain";
	public static final String ANY_LINKTYPE = "ANY";
	/**
	 * Constants for tokenizing HQL string
	 */
	public static final String SEARCH_FOR_CONSENSUS_IDENTIFIER = "consensusIdentifierDataCollection.id in";
	public static final String SEARCH_FOR_TARGETALIAS = "xxTargetAliasxx";
	public static final String SEARCH_FOR_FREQUENCY = "frequency";
	public static final String SEARCH_FOR_ONT = "orderOfNodeTraversalCollection.id in";
	public static final String SEARCH_FOR_DATASOURCE= "edu.wustl.geneconnect.domain.DataSource where name";
	public static final String SEARCH_FOR_LINKTYPE = "edu.wustl.geneconnect.domain.LinkType where type";
	public static final String SEARCH_FOR_GENE = "From edu.wustl.geneconnect.domain.Gene";
	public static final String SEARCH_FOR_MRNA = "From edu.wustl.geneconnect.domain.MessengerRNA";
	public static final String SEARCH_FOR_PROTEIN = "From edu.wustl.geneconnect.domain.Protein";
	public static final String SEARCH_FOR_CONFIDENCE = "confidenceScore";
	
	
}
