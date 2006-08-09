/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.EntrezGeneElementParser</p> 
 */

package com.dataminer.server.parser;

import java.util.Iterator;
import java.util.Vector;
import org.jdom.Element;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.exception.InsertException;
import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.log.Logger;

/**
 * EntrezGeneElementParser class parses one node of entrez gene (named "Entrezgene")
 * and filters out all the required fields like geneId, Name, Symbol etc. And then writes
 * them to text files.
 * @author   Anuj Tiwari
 * @version  1.0
 */

public class EntrezGeneElementParser extends XMLElementParser 
{
	/** Gene Id of Current gene node */
	private String m_geneId = null;
	/** Information about the current gene-id. Indicates whether its been discontinued or replaced by a new geneid */ 
	private String m_geneTrackStatus = null;
	/** Taxonomy-Id for gene in the current node */
	private String m_geneTaxId = null;
	/** Product of gene in the current node*/
	private String m_geneProduct = null;
	/** Official name of gene in the current node */
	private String m_geneName = null;
	/** Official symbol of gene in the current node */
	private String m_geneSymbol = null;
	/** Map location of gene in the current node */
	private String m_geneMapLoc = null;
	/** Map type of gene in the current node */
	private String m_geneMapType = null;
	/** Chromosome number of gene in the current node */
	private String m_geneChromosome = null;
	/** Summary Information of gene in the current node*/
	private String m_geneSummary = null;
	/** Current GeneId of gene in the current node*/
	private String m_geneCurrentId = null;
	/** List of PMIDS for gene in the current node */
	private Vector m_genePMIDSVector = null;
	/** List of synonyms for gene in the current node */
	private Vector m_geneSynVector = null;
	/** List of MIM IDs for gene in the current node */
	private Vector m_geneMIMVector = null;
	/** List of GOIDs for gene in the current node */
	private Vector m_geneGOIDSVector = null;
	/** List of STS Ids for gene in the current node */
	private Vector m_geneSTSIDVector = null;
	/** List of Phenotypes for gene in the current node */
	private Vector m_genePhenotypeVector = null;
	/** List of Fly Ids for gene in the current node */
	private Vector m_geneFlyIdVector = null;
	/** List of UniGene Ids for gene in the current node */
	private Vector m_geneUnigeneVector = null;
	/** An instance of EntrezParser class which is to used to access fileWriters to write records */
	public EntrezParser m_entrezParser = null;
	

	/**
	 * Cosntructor method
	 * @param dbType Type of the database (mySQL / Oracle)
	 * @param entrezParser Instance of Entrez gene parser
	 */
	public EntrezGeneElementParser(String dbType, EntrezParser entrezParser)
	{
		m_entrezParser = entrezParser;
	}
	
	/**
	 * Method to parse xml element
	 */
	public void parseElement(Element elementToBeParsed)
	{
		
		try
		{
			/** reset all the records. */
			
			m_entrezParser.resetRecords(false);
			m_geneId = getGeneId(elementToBeParsed);
			
			m_geneTrackStatus = getGeneTrackStatus(elementToBeParsed);
			
			if(m_geneTrackStatus != null)
			{
				/** return: if the gene was discontinued... */
				if (m_geneTrackStatus.equalsIgnoreCase("discontinued"))
					return; 
			}
			m_geneCurrentId = getGeneCurrentId(elementToBeParsed);
			if (m_geneCurrentId != null)
				if ((m_geneCurrentId.equalsIgnoreCase(m_geneId) == false))
					return;	/** the current gene is retired, so do not parse this element. */
			m_entrezParser.m_mapRecord.fields[0].append(m_geneId);
			m_geneTaxId = getTaxId(elementToBeParsed);
			/**	return to process next record if we dont have the taxid */
			if (m_geneTaxId == null) 
				return;
			m_entrezParser.m_mapRecord.fields[1].append(m_geneTaxId);
			
			m_geneProduct = getGeneProduct(elementToBeParsed);
			
			m_geneName = getOfficialName(elementToBeParsed);
			
			m_geneSymbol = getOfficialSymbol(elementToBeParsed);
			
			m_geneMapLoc = getMapLocation(elementToBeParsed);
			
			m_geneMapType = getMapType(elementToBeParsed);
			
			m_geneChromosome =  getChromosome(elementToBeParsed);
			
			m_geneSummary = getGeneSummary(elementToBeParsed);
			
			m_genePMIDSVector = getGenePMIDS(elementToBeParsed);
			
			m_geneSynVector = getGeneSynonyms(elementToBeParsed);
			
			m_geneMIMVector = getMIMID(elementToBeParsed);
			
			m_geneGOIDSVector = getGOID(elementToBeParsed);
			
			m_geneFlyIdVector = getFlyId(elementToBeParsed);
			
			m_geneUnigeneVector = getUniGeneId(elementToBeParsed);
			
			m_genePhenotypeVector = getGenePhenotype(elementToBeParsed);
			
			m_geneSTSIDVector = getUniSTSID(elementToBeParsed);
			/** preProcess the GOIDS */
			preProcessGOIDS(m_geneGOIDSVector);
			String hsLocalTaxid = (String) Variables.hmOrganismLocalId.get("Homo sapiens");
			String rnLocalTaxid = (String) Variables.hmOrganismLocalId.get("Rattus norvegicus");
			String mmLocalTaxid = (String) Variables.hmOrganismLocalId.get("Mus musculus");
			if(m_geneTaxId == null)
			{
				Logger.log("m_geneTaxId read from map is null",Logger.DEBUG);
				Logger.log("possibly the organism name is not present in the taxonomy map",Logger.DEBUG);
			}
			if((m_geneTaxId.equalsIgnoreCase(hsLocalTaxid) || m_geneTaxId.equalsIgnoreCase(rnLocalTaxid) || m_geneTaxId.equalsIgnoreCase(mmLocalTaxid)) && (m_geneMapLoc != null)&& m_geneMapType.equals("cyto") )
			{
				/** generate MAP tree for chromosome map location. */
				Vector mapTree = generateMapTree(m_geneMapLoc);
				try
				{
					writeMapInformation(mapTree);
				}
				catch (FatalException fexcp)
				{
					Logger.log("Fatal Exception (Entrez Gene): " + fexcp.getMessage(), Logger.WARNING);
				}
			}
		}
		catch(NullPointerException nullexcp)
		{
			Logger.log("Null Pointer Exception(Entrez Gene): " + nullexcp.getMessage(), Logger.WARNING);
			return;
		}
		/** Populate the output data files. */
		populateOutputFiles();
	}
	
	/**
	 * prefix the GOIDS with 0's so as to make them of 7 charactors
	 * and then prefix 'GO:' to the result obtained in earlier step.
	 * @param geneGOIDSVector Vector of all the GO terms associated with 
	 *  this gene
	 */
	private void preProcessGOIDS(Vector geneGOIDSVector)
	{
		String goid = null;
		int prefixCount = 0;
		StringBuffer zeroPrefix = new StringBuffer("");
		StringBuffer finalString = new StringBuffer("");
		if (geneGOIDSVector != null)
		{
			for (int iCount = 0; iCount < geneGOIDSVector.size(); iCount++)
			{
				goid = (String)geneGOIDSVector.get(iCount);
				geneGOIDSVector.remove(iCount);
				prefixCount = Constants.GOIDLENGTH - goid.length();
				for (int iCount1 = 0; iCount1 < prefixCount ; iCount1++)
				{
					zeroPrefix.append("0");
				}
				zeroPrefix.append("1");
				zeroPrefix.reverse();
				String goidAppended = new String("GO:" + zeroPrefix + goid);
				finalString.append(goidAppended);
				finalString.deleteCharAt(3);
				
				geneGOIDSVector.add(iCount,finalString.toString());
				zeroPrefix.delete(0,zeroPrefix.length());
				finalString.delete(0, finalString.length());
			}
		}
	}
	/**
	 * Method to get GeneID from given xml parser
	 * @param elementToBeParsed XML element to parse
	 * @return returns geneId extrated from xml-element
	 */ 
	private String getGeneId(Element elementToBeParsed)
	{
		
		Element geneIdElement = null;
		
		geneIdElement = (Element)getElement(elementToBeParsed, "Gene-track_geneid").firstElement();
		
		if (geneIdElement != null)
		{
			return geneIdElement.getText();
		}
		return null;
		
	} /**  end of getGeneId function*/
	
	/**
	 * Method to get taxonomy Id from XML element 
	 * @param elementToBeParsed XML element to parse
	 * @return Extracted taxonomy ID from XML element
	 */
	private String getTaxId(Element elementToBeParsed)
	{
		Element geneTaxNameElement = null;
		Element geneTaxIdElement = null;
		String geneOrganism = null;
		String geneTaxId = null;
		Vector geneTaxIdVector = getParentElement(elementToBeParsed, "Dbtag_db", "taxon");
		geneTaxIdElement = (Element)getElement((Element)geneTaxIdVector.firstElement(), "Object-id_id").firstElement();
		geneTaxNameElement = (Element)getElement(elementToBeParsed, "Org-ref_taxname").firstElement();
		if (geneTaxNameElement != null)
		{
			geneOrganism = (String)Variables.hmOrganismLocalId.get(geneTaxNameElement.getTextTrim());
		}
		if (geneOrganism == null && geneTaxIdVector != null)
		{
			geneTaxId =  geneTaxIdElement.getTextTrim();
			geneOrganism = (String)Variables.hmTaxidLocalId.get(geneTaxId);
		}
		Variables.entrezGeneRevisionHistory.put(geneOrganism,m_entrezParser.getFileRevisionHistory(m_entrezParser.m_fileName));
		return geneOrganism;
	}
	/**
	 * Method to get Gene status from XML element
	 * @param elementToBeParsed XML element to parse
	 * @return Extracted gene status from XML element
	 */
	private String getGeneTrackStatus(Element elementToBeParsed)
	{
		Element geneTrackStatusElement = null;
		String trackStatus = null;
		Vector geneTrackStatusVector = null;
		
		geneTrackStatusVector = getElement(elementToBeParsed, "Gene-track_status");
		if (geneTrackStatusVector != null)
		{
			geneTrackStatusElement = (Element)geneTrackStatusVector.firstElement();
			trackStatus = geneTrackStatusElement.getAttributeValue("value");
		}
		return trackStatus;
	}
	
	/**
	 * Method to get gene product from XML element
	 * @param elementToBeParsed XML element to parse
	 * @return Extracted gene product string from XML element
	 */
	private String getGeneProduct(Element elementToBeParsed)
	{
		Element geneProductElement = null;
		Vector geneProductVector = null;
		String geneProduct = null;
		geneProductVector = getElement(elementToBeParsed, "Prot-ref_name_E");
		if (geneProductVector != null)
		{
			geneProductElement = (Element)geneProductVector.firstElement();
			geneProduct = geneProductElement.getTextTrim();
		}
		return geneProduct;
	}
	/**
	 * Method to get chromosomal map location of gene from XML element
	 * @param elementToBeParsed XML element to parse
	 * @return Extracted chromosomal location of gene from XML element
	 */
	private String getMapLocation(Element elementToBeParsed)
	{
		Element geneMapLocElement = null;
		String mapLoc = null;
		Vector geneMapLocVector = null;
		
		geneMapLocVector = getElement(elementToBeParsed, "Maps_display-str");
		if (geneMapLocVector != null)
		{
			geneMapLocElement = (Element)geneMapLocVector.firstElement();
			mapLoc = geneMapLocElement.getText();
		}
		return mapLoc;
	}
	/**
	 *  Method to get map value
	 * @param elementToBeParsed XML element to parse
	 * @return Extracted map type from XML element
	 */
	private String getMapType(Element elementToBeParsed)
	{
		Element geneMapTypeElement = null;
		String mapType = null;
		Vector geneMapTypeVector = null;
		
		geneMapTypeVector = getElement(elementToBeParsed, "Maps_method_map-type");
		if (geneMapTypeVector != null)
		{
			geneMapTypeElement = (Element)geneMapTypeVector.firstElement();
			mapType = geneMapTypeElement.getAttributeValue("value");
		}
		return mapType;
	}
	/**
	 * Get chromosome from xml element
	 * @param elementToBeParsed XML element to parse
	 * @return Extracted chromosomal location of gene from XML element
	 */
	private String getChromosome(Element elementToBeParsed)
	{
		Element geneChromosomeElement = null;
		String geneChromosome = null;
		Vector geneChromosomeVector = null;
		
		geneChromosomeVector = getElement(elementToBeParsed, "SubSource_name");
		if (geneChromosomeVector != null)
		{
			geneChromosomeElement = (Element)geneChromosomeVector.firstElement();
			geneChromosome = geneChromosomeElement.getText();
		}
		return geneChromosome;
	}
	/**
	 * Get gene summary
	 * @param elementToBeParsed XML element to parse
	 * @return Extracted gene summary of gene from XML element
	 */
	private String getGeneSummary(Element elementToBeParsed)
	{
		Element geneSummaryElement = null;
		String geneSummary = null;
		Vector geneSummaryVector = null;
		
		geneSummaryVector = getElement(elementToBeParsed, "Entrezgene_summary");
		if (geneSummaryVector != null)
		{
			geneSummaryElement = (Element)geneSummaryVector.firstElement();
			geneSummary = geneSummaryElement.getText();
		}
		return geneSummary;
	}
	/**
	 * Get gene pubmed IDs from XML element
	 * @param elementToBeParsed XML element to parse
	 * @return vector of pubmed IDs
	 */
	private Vector getGenePMIDS(Element elementToBeParsed)
	{
		Vector genePMIDElementVector = null;
		String genePMID = null;
		Element genePMIDElement = null;
		Vector genePMIDVector = new Vector();
		
		genePMIDElementVector = getElement(elementToBeParsed, "PubMedID");
		while ((genePMIDElementVector!= null) && genePMIDElementVector.size() > 0)
		{
			genePMIDElement = (Element)genePMIDElementVector.firstElement();
			genePMID = genePMIDElement.getTextTrim();
			if (genePMIDVector.contains(genePMID) == false)
				genePMIDVector.add(genePMID);
			genePMIDElementVector.remove(0);
		}
		return genePMIDVector;
	}
	/**
	 * Get gene synonyms
	 * @param elementToBeParsed XML element to parse
	 * @return Vector of gene synonyms
	 */
	private Vector getGeneSynonyms(Element elementToBeParsed)
	{
		Vector geneSynElementVector = null;
		String geneSyn = null;
		Element geneSynElement = null;
		Vector geneSynVector = new Vector();
		
		geneSynElementVector = getElement(elementToBeParsed, "Gene-ref_syn_E");
		while (geneSynElementVector != null && geneSynElementVector.size() > 0)
		{
			geneSynElement = (Element)geneSynElementVector.firstElement();
			geneSyn = geneSynElement.getTextTrim();
			if (geneSynVector.contains(geneSyn) == false)
				geneSynVector.add(geneSyn);
			geneSynElementVector.remove(0);
		}
		return geneSynVector;
	}
	/**
	 * Get Offical name of the gene from xml element
	 * @param elementToBeParsed XML element to parse
	 * @return Office gene name
	 */
	private String getOfficialName(Element elementToBeParsed)
	{
		/**
		 * write a getParent(elementToBeParsed, "content of child node desired")
		 * and utilise it to get the desired node
		 * this method will inturn make use of getElement method
		 * this function can be used for officialName, Official Symbol, GO, Unigene, MIM, FLYBASE
		 * and current-id 408550
		 */
		Vector geneOffNameVector = null;
		String geneOffName = null;
		
		geneOffNameVector = getParentElement(elementToBeParsed, "Gene-commentary_label", "Official Full Name");
		if (geneOffNameVector != null)
		{
			Element parentOfOffName = (Element)geneOffNameVector.firstElement();
			
			geneOffName =  parentOfOffName.getChildText("Gene-commentary_text");
			
		}
		return geneOffName;
	}
	/**
	 * Method to get official gene symbol
	 * @param elementToBeParsed XML element to parse
	 * @return Official gene symbol
	 */
	private String getOfficialSymbol(Element elementToBeParsed)
	{
		Vector geneOffSymbolVector = null;
		String geneOffSymbol = null;
		
		geneOffSymbolVector = getParentElement(elementToBeParsed, "Gene-commentary_label", "Official Symbol");
		if (geneOffSymbolVector != null)
		{
			Element parentOffSymbol = (Element)geneOffSymbolVector.firstElement();
			
			geneOffSymbol =  parentOffSymbol.getChildText("Gene-commentary_text");
			
		}
		return geneOffSymbol;
	}
	/**
	 * Get OMIM ids for a gene
	 * @param elementToBeParsed XML element to parse
	 * @return vector of MIMIDs for a gene
	 */
	private Vector getMIMID(Element elementToBeParsed)
	{
		
		
		Vector geneMIMIdVector = null;
		geneMIMIdVector = getRequiredFieldValues(elementToBeParsed, "Dbtag_db", "MIM", "Object-ID_ID");
		return geneMIMIdVector;
	}
	
	/**
	 * Get list of GO tetms associated with this gene
	 * @param elementToBeParsed XML element to parse
	 * @return Vector of GO IDs
	 */
	private Vector getGOID(Element elementToBeParsed)
	{
		Vector geneGOIdVector = null;
		geneGOIdVector = getRequiredFieldValues(elementToBeParsed, "Dbtag_db", "GO", "Object-ID_ID");
		return geneGOIdVector;
	}
	/**
	 * Get list of flybase IDs
	 * @param elementToBeParsed XML element to parse
	 * @return Vector of flybase IDs
	 */
	private Vector getFlyId(Element elementToBeParsed)
	{
		Vector geneFlyIdVector = null;
		geneFlyIdVector = getRequiredFieldValues(elementToBeParsed, "Dbtag_db", "FLYBASE", "Object-id_str");
		return geneFlyIdVector;
	}
	/**
	 * Get Unigene IDs associated with the gene represented by XML element
	 * @param elementToBeParsed XML element to parse
	 * @return Vector of Unigene IDs
	 */
	private Vector getUniGeneId(Element elementToBeParsed)
	{
		Vector geneUniGeneIdVector = null;
		geneUniGeneIdVector = getRequiredFieldValues(elementToBeParsed, "Gene-commentary_text", "UniGene", "Xtra-Terms_value");
		return geneUniGeneIdVector;
	}
	/**
	 *  Get list of Phynotypes associated with gene represented by XML element
	 * @param elementToBeParsed XML element to parse
	 * @return Vector of phynotype
	 */
	private Vector getGenePhenotype(Element elementToBeParsed)
	{
		Vector genePhenotypeVector = null;
		genePhenotypeVector = getRequiredFieldValues(elementToBeParsed, "Gene-commentary_heading", "Phenotypes", "Gene-commentary_text");
		
		return genePhenotypeVector;
	}
	/**
	 * Get current Gene ID
	 * @param elementToBeParsed XML element to parse
	 * @return current geneID
	 */
	private String getGeneCurrentId(Element elementToBeParsed)
	{
		Vector geneCurrentIdVector = null;
		String geneCurrentId = null;
		geneCurrentIdVector = getRequiredFieldValues(elementToBeParsed, "Dbtag_db", "LocusID", "Object-id_id");
		if (geneCurrentIdVector != null && geneCurrentIdVector.size() > 0)
		{
			geneCurrentId = geneCurrentIdVector.firstElement().toString();
		}
		return geneCurrentId;
	}
	/**
	 * Get list of uniSTS IDs associated with the gene represented by XML element
	 * @param elementToBeParsed XML element to parse
	 * @return vector of uniSTS ids
	 */
	private Vector getUniSTSID(Element elementToBeParsed)
	{
		Vector geneUniSTSIDVector = null;
		
		geneUniSTSIDVector = getRequiredFieldValues(elementToBeParsed, "Gene-commentary_heading", "Markers (Sequence Tagged Sites/STS)", "Object-id_id");
		return geneUniSTSIDVector;
	}
	
	
	/**
	 * This function returns the vector of required values when we want to have the
	 * content of a tag requireTagName only if the tag dependTagName has value equal to dependTagValue
	 * @param elementToBeParsed XML element to parse
	 * @param dependTagName Depend tag name
	 * @param dependTagValue Depend tag value
	 * @param requireTagName Required tag names
	 * @return Vector of required field's values
	 */
	
	
	private Vector getRequiredFieldValues(Element elementToBeParsed, String dependTagName, String dependTagValue, String requireTagName)
	{
		Vector parentElementVector = getParentElement(elementToBeParsed, dependTagName, dependTagValue);
		Vector requiredContentVector = new Vector();
		Vector requiredTagVector = null;
		Element nextRequiredElement = null;
		String requiredContent = null;
		
		try
		{
			while((parentElementVector != null) && (parentElementVector.size() > 0))
			{
				requiredTagVector = getElement((Element)parentElementVector.firstElement(), requireTagName);
				
				while ((requiredTagVector != null) && requiredTagVector.size() > 0)
				{
					nextRequiredElement = (Element)requiredTagVector.firstElement();
					requiredContent = nextRequiredElement.getTextTrim();
					if( (requiredContentVector.contains(requiredContent) == false) || (dependTagValue.equalsIgnoreCase("Markers (Sequence Tagged Sites/STS)") == true))
					{
						requiredContentVector.addElement(requiredContent);
					}
					requiredTagVector.remove(0);
				}
				parentElementVector.remove(0);
			}
		}
		catch (NullPointerException excp)
		{
			Logger.log("NullPointerException (Entrez Gene) : " + excp.getMessage(), Logger.WARNING);
		}
		
		return requiredContentVector;
	}
	/**
	 * Returns all possible parent elements of the tag where the tag childElement
	 * has content as childText
	 * @param elementToBeParsed XML node to parse
	 * @param childElement Child element name
	 * @param childText Child element text
	 * @return
	 */
	private Vector getParentElement(Element elementToBeParsed, String childElement, String childText)
	{
		Iterator elementIterator = elementToBeParsed.getDescendants();
		Element nextNodeElement = null;
		Vector matchedElements = new Vector();
		
		Object nextObject = null;
		while (elementIterator.hasNext())
		{
			
			if((nextObject=elementIterator.next()) instanceof Element)
			{
				nextNodeElement = (Element)nextObject;
				if ( nextNodeElement.getChild(childElement) != null)
				{
					if (nextNodeElement.getChild(childElement).getTextTrim().equalsIgnoreCase(childText))
						matchedElements.addElement(nextNodeElement);
				}
			}
		}//end while
		if (matchedElements.size() == 0)
			return null; // if not found, then return null
		else
			return matchedElements;
	}
	/**
	 *  Returns the vector of elements with name equal to requiredValue
	 * @param elementToBeParsed XML element to parse
	 * @param requiredValue The name of the required field
	 * @return Vector all the elements having value equal to requiredValue
	 */
	
	private Vector getElement(Element elementToBeParsed, String requiredValue )
	{
		Iterator elementIterator = elementToBeParsed.getDescendants();
		Element nextNodeElement = null;
		Vector matchedElements = new Vector(10,5);
		
		Object nextObject = null;
		while (elementIterator.hasNext())
		{
			
			if((nextObject=elementIterator.next()) instanceof Element)
			{
				nextNodeElement = (Element)nextObject;
				if ( nextNodeElement.toString().equalsIgnoreCase("[Element: <" + requiredValue + "/>]"))
				{
					matchedElements.addElement(nextNodeElement);
				}
			}
		}//end while
		if (matchedElements.size() == 0)
			return null; // if not found, then return null
		else
			return matchedElements;
	}//end function getElement
	
	/**
	 * Generates the chromosome map tree and add the map terms and tree to db
	 * @param mapElement name of the map element
	 * @return vector of map terms
	 */
	private Vector generateMapTree(String mapElement) 
	{
		/** vector to store map tree elements */
		Vector tree = new Vector();
		boolean baseCase = false;
		if (mapElement.equalsIgnoreCase("X")|| mapElement.equalsIgnoreCase("Y")) 
		{
			/** found base case*/ 
			baseCase = true;
			tree.addElement(mapElement);
		}
		/** check if the mapElement is an integer*/
		try 
		{
			int value = Integer.parseInt(mapElement);

			value = value*1;
			baseCase = true;
			tree.addElement(mapElement);
		} 
		catch ( NumberFormatException numEx) 
		{
			/**no, mapElement is a sequence, so generate the map tree... */
			baseCase = false;
		} 
		finally 
		{
			if (baseCase == false) 
			{
				/** no base case, generate the map tree...*/
				int pIndex = 0, qIndex = 0, p_qIndex = 0;
				/** temp variable to store last concatinated string*/
				String lastElement = null;
				char charVal = 'p';
				boolean p_qPresent = false;
				if (((pIndex = mapElement.indexOf('p')) != -1) ||
						((qIndex = mapElement.indexOf('q')) != -1))	
				{
					/** one of 'p' or 'q' is present*/
					p_qPresent = true; 
					if (pIndex >= qIndex) 
					{
						charVal = 'p';
						p_qIndex = pIndex;
					} 
					else 
					{
						charVal = 'q';
						p_qIndex = qIndex;
					}
				}
				/** remove case with #pter-p#*/
				int dash = mapElement.indexOf('-');
				int orPosition = mapElement.indexOf(" or ");
				if ((dash != -1)  || (orPosition != -1)) 
				{
					/** e.g 19cen-q13.2*/
					String baseData = getBaseData(mapElement);
					if (baseData != null) 
					{
						tree.addElement(baseData);
					}

					tree.addElement(mapElement);
                } 
                else if (p_qPresent)
				{
					/** 'p' present, get the number before 'p'*/
					String number1 = mapElement.substring(0, p_qIndex);
					tree.addElement(number1);
					lastElement = number1 + charVal;
					tree.addElement(lastElement);
					if (mapElement.length() > p_qIndex +1) 
					{
						/** more elements after 'p', get them...
						*   check for '.' and '-' in the remaining string */
						String rem = mapElement.substring(p_qIndex+1);
						int dotIndex = rem.indexOf('.');
						int dashIndex = rem.indexOf('-');
						/** 4 cases;
						* 1 '.' absent and '-' absent
						* 2 '.' present and '-' absent
						* 3 '.' absent and '-' present
						* 4 '.' present and '-' present */
						
						if ((dotIndex == -1) && (dashIndex == -1)) 
						{
							
							/** case 1: '.' absent and '-' absent 
							* e.g #[p,q]#
							* rem is the number2 */
							String number2 = rem;
							tree.addElement(lastElement + number2);
						} 
						else if ((dotIndex != -1) && (dashIndex == -1)) 
						{
							/** case 2: '.' present and '-' absent 
							* e.g #[p,q]#.#
							* get number2 - after 'p' and before the '.' */
							String number2 = rem.substring(0, dotIndex);
							tree.addElement(lastElement + number2);
							lastElement = lastElement + number2;
							/** get number3 - after the '.' */ 
							String number3 = rem.substring(dotIndex+1);
							tree.addElement(lastElement + "." + number3);
						} 
						else if ((dotIndex == -1) && (dashIndex != -1)) 
						{
							/** case 3: '.' absent and '-' present 
							* e.g #[p,q]#-[p,q]#
							* get number2 - after 'p,q' and before the '-' */
							String number2 = rem.substring(0, dashIndex);							

							int nextp_qIndex = rem.indexOf(charVal);
							/** if nextp_qIndex is -1 then remove the lastElement from the tree, else continue */
							if (-1 == nextp_qIndex)
							{
								tree.remove(lastElement);
								tree.addElement(mapElement);
							}
							else
							{
								tree.addElement(lastElement + number2);
								String number3 = rem.substring(nextp_qIndex+1);
								try 
								{
									int num2 = Integer.parseInt(number2);
									int num3 = Integer.parseInt(number3);
									while (num3 > num2) 
									{
										num2++;
										tree.addElement(lastElement + num2);
									}
								} 
								catch (NumberFormatException numEx) 
								{
									
									tree.clear();
									String baseData = getBaseData(mapElement);
									if (baseData != null) 
									{
										tree.addElement(baseData);
									}
									tree.addElement(mapElement);
								}
							}
							
						}
						else if ((dotIndex != -1) && (dashIndex != -1)) 
						{
							/** here both '.' and '-' are present. There can be
							* 2 cases:
							*  case 3: #[p,q]#-[p,q]#.#
							*  case 4: #[p,q]#.#-[p,q]#, #[p,q]#.#-[p,q]#.# */
							if (dotIndex > dashIndex) 
							{
								String number2 = rem.substring(0, dashIndex);
								int nextp_qIndex = rem.indexOf(charVal);
								if (-1 == nextp_qIndex)
								{
									tree.remove(lastElement);
									tree.addElement(mapElement);
								}
								else
								{
									tree.addElement(lastElement + number2);
									String number3 = rem.substring(nextp_qIndex+1,dotIndex);
									String number4 = rem.substring(dotIndex+1);
									try 
									{
										
										int num2 = Integer.parseInt(number2);
										int num3 = Integer.parseInt(number3);
										while (num3 > num2) 
										{
											num2++;
											tree.addElement(lastElement + num2);
										}
										tree.addElement(lastElement + number3 + "." + number4);
									} 
									catch (NumberFormatException numEx) 
									{
										tree.clear();
										String baseData = getBaseData(mapElement);
										if (baseData != null) 
										{
											tree.addElement(baseData);
										}
										tree.addElement(mapElement);
									}
								}
								
							} 
							else 
							{
								/** case 4: #[p,q]#.#-[p,q]#, #[p,q]#.#-[p,q]#.#
								*   find number2, number3, number4 and number5 */
								String number2 = rem.substring(0, dotIndex);
								
								String number3 = rem.substring(dotIndex+1,dashIndex);
								
								/** int nextp_qIndex = dashIndex + 1;//= rem.indexOf('p'); */
								int nextp_qIndex = rem.indexOf(charVal);
								if (-1 == nextp_qIndex)
								{
									tree.remove(lastElement);									
									tree.addElement(mapElement);
								}
								else
								{
									tree.addElement(lastElement + number2);
									/** add the third element */
									tree.addElement(lastElement + number2 + "." + number3);
									
									/** find which of the following 2 cases apply */
									/** a  #[p,q]#.#-[p,q]#  AND
									*   b  #[p,q]#.#-[p,q]#.# */
									
									boolean caseb = true;
									String number4, number5 = null;
									
									int lastDotIndex = rem.lastIndexOf('.');
									if (lastDotIndex <= nextp_qIndex) 
									{
										/** case a - #[p,q]#.#-[p,q]#*/
										caseb = false;
										number4 = rem.substring(nextp_qIndex+1);
									} 
									else 
									{
										/** case b - #[p,q]#.#-[p,q]#.#*/
										number4 = rem.substring(nextp_qIndex+1,lastDotIndex);
										number5 = rem.substring(lastDotIndex+1);
									}
									/** generate range */
									try 
									{
										int num2 = Integer.parseInt(number2);
										int num2bak = num2;
										int num4 = Integer.parseInt(number4);
										/** add range */
										while (num4 > num2) 
										{
											num2++;
											tree.addElement(lastElement + num2);
										}
										/** add the fourth and fifth element */
										if (caseb) 
										{
											if ((num4 > num2bak) ||
													((num4 == num2bak) &&
															(!number5.equalsIgnoreCase(number3)))) {
												
												/** add the fourth element*/
												tree.addElement(lastElement + number4 + "." + number5);
											}
										}
									} 
									catch (NumberFormatException numEx) 
									{
										tree.clear();
										String baseData = getBaseData(mapElement);
										if (baseData != null) 
										{
											tree.addElement(baseData);
										}
										tree.addElement(mapElement);
									}
								}
								
							} /** else case 4 */
							
						}/** else case 3 OR 4 */
					}
                }
                else 
                {
					String baseData = getBaseData(mapElement);
					if (baseData != null) 
					{
						tree.addElement(baseData);
					}
					tree.addElement(mapElement);
				}
			}  
			
		}  
		return tree;
		
	}
     /**
      * get the base chromosome # from the mapElement
      * @param mapElement Name of the map element
      * @return The base chromome number
      */
	private String getBaseData(String mapElement)
	{
		int mapLength = mapElement.length();
		StringBuffer baseData = new StringBuffer();
		for (int i=0; i<mapLength; i++) 
		{
			if ((mapElement.charAt(i) == 'X') ||
					(mapElement.charAt(i) == 'Y')) 
			{
				baseData.append(mapElement.substring(0,i+1));
				break;
			}
			char iChar = mapElement.charAt(i);
			if ((iChar >= '0') && (iChar <= '9')) 
			{
				baseData.append(iChar);
			}  
			else 
			{
				break;
			}
		}
		return baseData.toString();
	}
	
	/**
	 * Write the Map information to database tables - system_termdata and system_termtree
	 * @param termTree List of system term tree
	 * @throws FatalException Throws exception if error during writing
	 */
	private void writeMapInformation(Vector termTree) throws FatalException 
	{
		int numTreeElements = termTree.size();
		String parentid = m_entrezParser.m_maptermIDPrefix + "0";
		/** isParent is always = 1 in this case*/
		m_entrezParser.m_maptreeRecord.fields[2].append("1");
		String lastTerm = null;
		String lastTermId = null;
		String oldParent = null;
		boolean insertMap=true;
		for (int i=0; i<numTreeElements; i++) 
		{
			insertMap=true;
			String term = (String) termTree.elementAt(i);
			if(true==EntrezParser.m_mapHTable.containsKey(term))
			{
				insertMap = false;
			}
			String termid = getMapID(term);
			if(true==insertMap)
			{
				
			}
			m_entrezParser.m_mapRecord.fields[2].append(termid);
			
			try
			{
				m_entrezParser.writeRecordToDb(Variables.llMapTableName, m_entrezParser.m_mapRecord );
			}
			catch(InsertException ie)
			{
				Logger.log("Exception (Entrez Gene) : " + ie.getMessage(), Logger.WARNING);
			}
			if ((i > 0) && (i <= 2))
			{
				if (term.indexOf(lastTerm) != -1) 
				{
					parentid = lastTermId;
				}
			}
			else if (i > 3)
			{
				parentid = oldParent;
			}
			if(true==insertMap)
			{
				/** write the mapterm Record*/
				m_entrezParser.m_maptermRecord.fields[0].append(termid);
				m_entrezParser.m_maptermRecord.fields[1].append(term);

				/** write the maptermtree Record*/
				m_entrezParser.m_maptreeRecord.fields[0].append(termid);
				m_entrezParser.m_maptreeRecord.fields[1].append(parentid);
				try
				{
					m_entrezParser.writeRecordToDb(Variables.termTableName, m_entrezParser.m_maptermRecord );
					m_entrezParser.writeRecordToDb(Variables.treeTableName, m_entrezParser.m_maptreeRecord );
				}
				catch(InsertException ie)
				{
					Logger.log("Exception (Entrez Gene):" + ie.getMessage(), Logger.WARNING);
				}
			}
			/** restore the earlier parent */
			parentid = oldParent;
			
			/** child becomes the parent for the next record in vector
			* only for the first 2 terms*/
			if (i < 3)
			{
				parentid = termid;
				oldParent = parentid;
			}
			lastTerm = term;
			lastTermId = termid;
			
			/**reset Fields*/
			m_entrezParser.m_maptermRecord.fields[0].setLength(0);
			m_entrezParser.m_maptermRecord.fields[1].setLength(0);
			m_entrezParser.m_mapRecord.fields[2].setLength(0);
			m_entrezParser.m_maptreeRecord.fields[0].setLength(0);
			m_entrezParser.m_maptreeRecord.fields[1].setLength(0);
		}
	}
	/**
	 * Returns an ID for the chromosome map
	 * @param mapterm the chrosome map term for which to get ID
	 * @return ID for the chromosome map
	 */
	private String getMapID(String mapterm)
	{
		/** initialMapID*/
		String id = (String) EntrezParser.m_mapHTable.get(mapterm);
		if (id == null)
		{ /** new parent*/
			id = m_entrezParser.m_maptermIDPrefix + EntrezParser.m_maptermID++;
			EntrezParser.m_mapHTable.put(mapterm, id);
		}
		return id;
	}
	/**
	 * Populate the output file 
	 */
	private void populateOutputFiles()
	{
		
		try
		{
			populateEntrezBaseFile();
			String fbLocalTaxid = (String) Variables.hmOrganismLocalId.get("Drosophila melanogaster");
			if (m_geneTaxId.equalsIgnoreCase(fbLocalTaxid) && m_geneFlyIdVector != null)
			{
				populateEntrezFlyFile();
			}
			
			if (m_geneMIMVector != null)
			{
				populateEntrezMIMFile();
			}
			
			if ( m_genePhenotypeVector != null )
			{
				populateEntrezPhenotypeFile();
			}
			if (m_geneUnigeneVector != null)
			{
				populateEntrezUnigeneFile();
			}
			
			if ( m_geneSTSIDVector != null )
			{
				populateEntrezSTSFile();
			}
			
			if (m_genePMIDSVector != null)
			{
				populateEntrezPMIDSFile();
			}
			
			if ((m_geneSynVector != null) || (m_geneProduct != null) || (m_geneSymbol != null) || (m_geneSymbol != null))
			{
				populateGeneNamesFile();
			}
			
			if(m_geneGOIDSVector != null)
			{
				populateEntrezGOIDFile();
			}
		}
		catch (FatalException fexcp)
		{
			Logger.log("FatalException: " + fexcp.getMessage(), Logger.WARNING);
		}
		catch (InsertException insertExcp)
		{
			Logger.log("InsertException: " + insertExcp.getMessage(), Logger.WARNING);
		}
	}
	
	/**
	 * populate the file for EntrezGene table
	 * @throws FatalException Throws exception if error during populating entrez base file
	 * @throws InsertException Throws exception if error during populating entrez base file
	 */
	private void populateEntrezBaseFile() throws FatalException,InsertException
	{
		
		m_entrezParser.m_baseRecord.fields[0].append(m_geneId);
		if (m_geneTaxId != null)
			m_entrezParser.m_baseRecord.fields[1].append(m_geneTaxId);
		if (m_geneSymbol != null)
			m_entrezParser.m_baseRecord.fields[2].append(m_geneSymbol);
		
		if (m_geneName != null)
			m_entrezParser.m_baseRecord.fields[3].append(m_geneName);
		
		if (m_geneSummary != null)
			m_entrezParser.m_baseRecord.fields[4].append(m_geneSummary);
		
		/** write the map location only for non-Fly genes */
		if ((m_geneTaxId.equalsIgnoreCase("Dm") == false) && (m_geneMapLoc != null))
			m_entrezParser.m_baseRecord.fields[5].append(m_geneMapLoc);
		
		if(m_geneChromosome != null)
			m_entrezParser.m_baseRecord.fields[6].append(m_geneChromosome);
		
		m_entrezParser.writeRecordToDb(Variables.locusBaseTableName,m_entrezParser.m_baseRecord);
	}
	/**
	 * This function populates the file for Entrez_Fly table
	 * @throws FatalException Throws exception if error during populating entrez flybase file
	 * @throws InsertException Throws exception if error during populating entrez flybase file
	 */
	private void populateEntrezFlyFile() throws FatalException,InsertException
	{
		for (int iCount = 0 ; iCount < m_geneFlyIdVector.size() ; iCount++)
		{
			m_entrezParser.m_dmRecord.fields[0].append(m_geneId);
			m_entrezParser.m_dmRecord.fields[1].append(m_geneTaxId);
			m_entrezParser.m_dmRecord.fields[2].append(m_geneFlyIdVector.get(iCount));
			m_entrezParser.writeRecordToDb(Variables.locusFlyTableName, m_entrezParser.m_dmRecord);
			/** reset the record fields to store next value of fly-id. */
			m_entrezParser.m_dmRecord.resetAllFields();
		}
	}
	
	/**
	 * This function populates the file for Entrez_Unigene table.
	 * @throws FatalException Throws exception if error during populating entrez unigene file 
	 * @throws InsertException Throws exception if error during populating entrez unigene file
	 */   
	private void populateEntrezUnigeneFile() throws FatalException,InsertException
	{
		for (int iCount = 0 ; iCount < m_geneUnigeneVector.size() ; iCount++)
		{
			m_entrezParser.m_ugRecord.fields[0].append(m_geneId);
			m_entrezParser.m_ugRecord.fields[1].append(m_geneTaxId);
			m_entrezParser.m_ugRecord.fields[2].append(m_geneUnigeneVector.get(iCount));
			m_entrezParser.m_ugRecord.fields[3].append("direct_annotation");
			m_entrezParser.writeRecordToDb(Variables.llUgTableName, m_entrezParser.m_ugRecord);
			
			/** reset the record fields to store next value of Unigene. */
			m_entrezParser.m_ugRecord.resetAllFields();
		}
	}
	
	/**
	 * This function populates the file for Entrez_OMIM table.
	 * @throws FatalException Throws exception if error during populating entrez OMIM file
	 * @throws InsertException Throws exception if error during populating entrez OMIM file
	 */
	private void populateEntrezMIMFile() throws FatalException,InsertException
	{
		for (int iCount = 0 ; iCount < m_geneMIMVector.size() ; iCount++)
		{
			m_entrezParser.m_omimRecord.fields[0].append(m_geneId);
			m_entrezParser.m_omimRecord.fields[1].append(m_geneTaxId);
			m_entrezParser.m_omimRecord.fields[2].append(m_geneMIMVector.get(iCount));
			
			m_entrezParser.writeRecordToDb(Variables.llOmimTableName, m_entrezParser.m_omimRecord);
			
			/** reset the record fields to store next value of MIM-ID.*/
			m_entrezParser.m_omimRecord.resetAllFields();
		}
	}
	
	/**
	 * This function populates the file for Entrez_Phenotype table.
	 * @throws FatalException Throws exception if error during populating entrez phynotype file
	 * @throws InsertException Throws exception if error during populating entrez phynotype file
	 */
	private void populateEntrezPhenotypeFile() throws FatalException,InsertException
	{
		for (int iCount = 0 ; iCount < m_genePhenotypeVector.size() ; iCount++)
		{
			m_entrezParser.m_phenotypeRecord.fields[0].append(m_geneId);
			m_entrezParser.m_phenotypeRecord.fields[1].append(m_geneTaxId);
			m_entrezParser.m_phenotypeRecord.fields[2].append(m_genePhenotypeVector.get(iCount));
			
			m_entrezParser.writeRecordToDb(Variables.llPhenotypeTableName, m_entrezParser.m_phenotypeRecord);
			
			/** reset the record fields to store next value of Phenotype.*/
			m_entrezParser.m_phenotypeRecord.resetAllFields();
		}
	}
	/**
	 * This function populates the file for Entrez_STS table.
	 * @throws FatalException Throws exception if error during populating entrez STS file
	 * @throws InsertException Throws exception if error during populating entrez flybase file
	 */
	private void populateEntrezSTSFile() throws FatalException,InsertException
	{
		for (int iCount = 0 ; iCount < m_geneSTSIDVector.size() ; iCount++)
		{
			m_entrezParser.m_stsRecord.fields[0].append(m_geneId);
			m_entrezParser.m_stsRecord.fields[1].append(m_geneTaxId);
			/** check whether to add the marker name into this table or not.Since it
			* is present in unists table and hence can be mapped.*/
			m_entrezParser.m_stsRecord.fields[2].append(m_geneSTSIDVector.get(iCount));
			
			m_entrezParser.writeRecordToDb(Variables.locusStsTableName, m_entrezParser.m_stsRecord);
			
			/** reset the record fields to store next value of STS ID.*/
			m_entrezParser.m_stsRecord.resetAllFields();
		}
	}
	
	/**
	 * This function populates the file for Entrez_PMIDS table.
	 * @throws FatalException Throws exception if error during populating entrez pmid file
	 * @throws InsertException Throws exception if error during populating entrez pmid file
	 */
	private void populateEntrezPMIDSFile() throws FatalException,InsertException
	{
		for (int iCount = 0 ; iCount < m_genePMIDSVector.size() ; iCount++)
		{
			m_entrezParser.m_pmidRecord.fields[0].append(m_geneId);
			m_entrezParser.m_pmidRecord.fields[1].append(m_geneTaxId);
			m_entrezParser.m_pmidRecord.fields[2].append(m_genePMIDSVector.get(iCount));
			
			m_entrezParser.writeRecordToDb(Variables.llPmidTableName, m_entrezParser.m_pmidRecord);
			
			/** reset the record fields to store next value of PMID.*/
			m_entrezParser.m_pmidRecord.resetAllFields();
		}
	}
	
	/**
	 * This function populates the file for Entrez_GOID table.
	 * @throws FatalException Throws exception if error during populating entrez GOID file
	 * @throws InsertException Throws exception if error during populating entrez GOID file
	 */
	private void populateEntrezGOIDFile() throws FatalException,InsertException
	{
		for (int iCount = 0 ; iCount < m_geneGOIDSVector.size() ; iCount++)
		{
			m_entrezParser.m_goRecord.fields[0].append(m_geneId);
			m_entrezParser.m_goRecord.fields[1].append(m_geneTaxId);
			m_entrezParser.m_goRecord.fields[2].append(m_geneGOIDSVector.get(iCount));
			
			m_entrezParser.writeRecordToDb(Variables.llGoidTableName, m_entrezParser.m_goRecord);
			
			/** reset the record fields to store next value of GOID. */
			m_entrezParser.m_goRecord.resetAllFields();
		}
	}
	/**
	 * This function populates the file for Entrez_Genenames table.
	 * @throws FatalException Throws exception if error during populating entrez genenames file
	 * @throws InsertException Throws exception if error during populating entrez genenames file
	 */
	private void populateGeneNamesFile() throws FatalException,InsertException
	{
		if (m_geneSynVector == null)
			m_geneSynVector = new Vector ();
		
		if (m_geneProduct != null)
		{
			if (m_geneSynVector.contains(m_geneProduct) == false)
				m_geneSynVector.addElement(m_geneProduct);
		}
		
		if (m_geneSymbol != null)
		{
			if (m_geneSynVector.contains(m_geneSymbol) == false)
				m_geneSynVector.addElement(m_geneSymbol);
		}
		if (m_geneName != null)
		{
			if (m_geneSynVector.contains(m_geneName) == false)
				m_geneSynVector.addElement(m_geneName);
		}
		
		if (m_geneSynVector  != null)
		{
			for (int iCount = 0 ; iCount < m_geneSynVector.size() ; iCount++)
			{
				m_entrezParser.m_genenamesRecord.fields[0].append(m_geneId);
				m_entrezParser.m_genenamesRecord.fields[1].append(m_geneTaxId);
				m_entrezParser.m_genenamesRecord.fields[2].append(m_geneSynVector.get(iCount));
				
				m_entrezParser.writeRecordToDb(Variables.llGeneNamesTableName, m_entrezParser.m_genenamesRecord);
				
				/** reset the record fields to store next genename */
				m_entrezParser.m_genenamesRecord.resetAllFields();
			}
		}
	}
}
