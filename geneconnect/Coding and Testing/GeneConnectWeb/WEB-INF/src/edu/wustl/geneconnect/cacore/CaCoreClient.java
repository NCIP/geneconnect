/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.actionForm.SimpleSearchForm</p> 
 */

package edu.wustl.geneconnect.cacore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;
import gov.nih.nci.common.util.StringClobType;
import gov.nih.nci.system.applicationservice.ApplicationService;
/**
 * caCOre Client provides method to access caCore Api and also provides implements the methods to 
 * crete DetachedCriteria object from inputs given by user on UI. 
 * @author sachin_lale
 *
 */
public class CaCoreClient
{

	static ApplicationService appService;
	
	/**
	 * Get the instace of ApplicationService of caCore API
	 * @throws DAOException
	 */
	public static void init() throws DAOException
	{
		try
		{

			appService = ApplicationService
					.getRemoteInstance(ApplicationProperties.getValue("app.cacore.url"));
			Logger.out.info("Got AppService of caCORe");
		}
		catch (Exception ex)
		{
			Logger.out.error(ex.getMessage(), ex);
			ex.printStackTrace();
			System.out.println("Test client throws Exception = " + ex);
			throw new DAOException(ex.getMessage(), ex);
		}
	}

	/**
	 * calls ApplicationService.query() method of caCOre to execute DetachedCriteria
	 * 
	 * @param criteria
	 * @param className
	 * @return
	 * @throws DAOException
	 */
	public static List appServiceQuery(DetachedCriteria criteria, String className)
			throws DAOException
	{
		List resultList = null;
		try
		{
			resultList = appService.query(criteria, className);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DAOException(e.getMessage(), e);
		}

		return resultList;
	}

	/**
	 * Builds the DetachedCriteria object from user's input
	 * @param inputDsList
	 * @return
	 * @throws DAOException
	 */

	public static DetachedCriteria querySimple(List inputDsList) throws DAOException
	{
		/**
		 * Create root DetachedCriteria for GenomicIdentifierSet
		 */
		DetachedCriteria genomicIdSetCriteria = DetachedCriteria
				.forClass(GenomicIdentifierSet.class);
		//System.out.println("caCore deprepnputDsList.size() :  " + inputDsList.size());
		Map addedAssociation = new HashMap();
		for (int i = 0; i < inputDsList.size(); i++)
		{
			NameValueBean bean = (NameValueBean) inputDsList.get(i);
			// Get Class of data source i.e Gene,MRNA or Protein
			String className = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
					bean.getName(), GCConstants.CLASS);
			// get attribute name of Gene.MRNA or protein representing data source 
			String classAttribute = MetadataManager.getDataSourceAttribute(
					GCConstants.DATASOURCE_NAME, bean.getName(), GCConstants.ATTRIBUTE);
			// get type of attribute
			String attributeType = MetadataManager.getDataSourceAttribute(
					GCConstants.DATASOURCE_NAME, bean.getName(), GCConstants.TYPE);
			className = className.substring(0, 1).toLowerCase()
					+ className.substring(1, className.length());

			Logger.out.info("Data Source CLASS :  " + className);
			Logger.out.info("Data Source ATTRIBUTE:  " + classAttribute);
			Logger.out.info("Data Source ATTRIBUTE TYPE :  " + attributeType);
			
			/**
			 * Create DetachedCriteria for search on given datasource and its genomicId
			 */
			/**
			 * Check if Criteria already created.IF yed teh get teh DetachedCriteria object from Map
			 * else crete new DetachedCriteria 
			 */
			DetachedCriteria genomicCriteria =(DetachedCriteria)addedAssociation.get(className);
			if(genomicCriteria==null)
			{
				genomicCriteria = genomicIdSetCriteria.createCriteria(className);
				addedAssociation.put(className,genomicCriteria);
			}
			if (attributeType.equalsIgnoreCase("Long"))
			{
				Long longValue = null;
				try
				{
					longValue = new Long(bean.getValue());
				}
				catch (Exception e)
				{
					Logger.out.error(e.getMessage(), e);
					throw new DAOException("Genomic Identifier for DataSource " + bean.getName()
							+ " must be Integer");
				}
				genomicCriteria.add(Restrictions.eq(classAttribute, longValue));
			}
			else
			{
				genomicCriteria.add(Restrictions.eq(classAttribute, bean.getValue()));
			}
		}
		
		Logger.out.info(genomicIdSetCriteria);
		return genomicIdSetCriteria;

	}
}
