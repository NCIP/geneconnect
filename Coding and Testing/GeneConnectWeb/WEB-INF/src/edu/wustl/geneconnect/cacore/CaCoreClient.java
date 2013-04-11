/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java edu.wustl.geneconnect.actionForm.SimpleSearchForm</p> 
 */

package edu.wustl.geneconnect.cacore;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;

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

			appService = ApplicationService.getRemoteInstance(ApplicationProperties
					.getValue("app.cacore.url"));
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
	 * calls ApplicationService.search() method of caCore to execute query on domain object
	 * @param className
	 * @param object
	 * @return
	 * @throws DAOException
	 */
	public static List appServiceQuery(String className, Object object) throws DAOException
	{
		List resultList = null;
		try
		{
			resultList = appService.search(className, object);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new DAOException(e.getMessage(), e);
		}

		return resultList;
	}

	/**
	 * Builds the GenomicIdentifier object from user's input
	 * @param inputDsList
	 * @return
	 * @throws DAOException
	 */

	public static GenomicIdentifierSet querySimple(List inputDsList, List outputDsList)
			throws DAOException
	{
		/**
		 * Create root DetachedCriteria for GenomicIdentifierSet
		 */
		GenomicIdentifierSet genomicIdentifierSet = new GenomicIdentifierSet();

		try
		{

			//System.out.println("caCore deprepnputDsList.size() :  " + inputDsList.size());
			Map addedAssociation = new HashMap();
			for (int i = 0; i < inputDsList.size(); i++)
			{
				NameValueBean bean = (NameValueBean) inputDsList.get(i);
				// Get Class of data source i.e Gene,MRNA or Protein
				String className = MetadataManager.getDataSourceAttribute(
						GCConstants.DATASOURCE_NAME, bean.getName(), GCConstants.CLASS);

				String roleName = MetadataManager.getRoleName("GenomicIdentifierSet", className);

				// get attribute name of Gene.MRNA or protein representing data source 
				String classAttribute = MetadataManager.getDataSourceAttribute(
						GCConstants.DATASOURCE_NAME, bean.getName(), GCConstants.ATTRIBUTE);
				// get type of attribute
				String attributeType = MetadataManager.getDataSourceAttribute(
						GCConstants.DATASOURCE_NAME, bean.getName(), GCConstants.TYPE);

				//			className = className.substring(0, 1).toLowerCase()
				//					+ className.substring(1, className.length());

				Logger.out.info("Data Source ROLE :  " + roleName);
				Logger.out.info("Data Source ATTRIBUTE:  " + classAttribute);
				Logger.out.info("Data Source ATTRIBUTE TYPE :  " + attributeType);

				/**
				 * Create DetachedCriteria for search on given datasource and its genomicId
				 */
				/**
				 * Check if Criteria already created.IF yed teh get teh DetachedCriteria object from Map
				 * else crete new DetachedCriteria 
				 */
				Class associationClass = null;
				Object associationObject = addedAssociation.get(roleName);

				associationClass = Class.forName(GCConstants.DOAMIN_CLASSNAME_PREFIX + "."
						+ className);
				if (associationObject == null)
				{
					associationObject = associationClass.newInstance();
					Logger.out.info("associationObject null creating new for class :" + className);
					String temp = roleName.substring(0, 1).toUpperCase();
					String methodName = "set" + temp + roleName.substring(1, roleName.length());
					System.out.println("methodName: " + methodName);

					Class[] paramClass = new Class[]{associationClass};
					Object[] paramObject = new Object[]{associationObject};
					Method method = GenomicIdentifierSet.class.getDeclaredMethod(methodName,
							paramClass);
					method.invoke(genomicIdentifierSet, paramObject);
					addedAssociation.put(roleName, associationObject);

				}
				String temp = classAttribute.substring(0, 1).toUpperCase();
				String methodName = "set" + temp
						+ classAttribute.substring(1, classAttribute.length());
				Method method = null;
				Class[] attributes = null;

				Object[] attributesValue = null;

				if (attributeType.equalsIgnoreCase("java.lang.Long"))
				{
					attributes = new Class[]{Long.class};
					method = associationClass.getDeclaredMethod(methodName, attributes);
					attributesValue = new Object[]{new Long(bean.getValue())};
				}
				else
				{
					attributes = new Class[]{String.class};
					method = associationClass.getDeclaredMethod(methodName, attributes);
					attributesValue = new Object[]{new String(bean.getValue())};
				}
				method.invoke(associationObject, attributesValue);
				Logger.out.info("Added Input Attribute :" + classAttribute);

				/**
				 * Set association object in GenomicIdentifierSet
				 */

			}

			for (int j = 0; j < outputDsList.size(); j++)
			{
				String outputDSName = (String) outputDsList.get(j);
				String outputClassName = MetadataManager.getDataSourceAttribute(
						GCConstants.DATASOURCE_NAME, outputDSName, GCConstants.CLASS);
				Class associationClass = Class.forName(GCConstants.DOAMIN_CLASSNAME_PREFIX + "."
						+ outputClassName);
				String roleName = MetadataManager.getRoleName("GenomicIdentifierSet",
						outputClassName);
				Object associationObject = addedAssociation.get(roleName);
				if (associationObject == null)
				{
					Logger.out.info("associationObject null creating new for class :"
							+ outputClassName);
					associationObject = associationClass.newInstance();
					addedAssociation.put(roleName, associationObject);

					String temp = roleName.substring(0, 1).toUpperCase();
					String methodName = "set" + temp + roleName.substring(1, roleName.length());
					System.out.println("methodName: " + methodName);
					Class[] paramClass = new Class[]{associationClass};
					Object[] paramObject = new Object[]{associationObject};
					Method method = GenomicIdentifierSet.class.getDeclaredMethod(methodName,
							paramClass);
					method.invoke(genomicIdentifierSet, paramObject);

				}

				String outputAttribute = MetadataManager.getDataSourceAttribute(
						GCConstants.DATASOURCE_NAME, outputDSName, GCConstants.OUTPUT_ATTRIBUTE);
				String temp = outputAttribute.substring(0, 1).toUpperCase();
				String methodName = "set" + temp
						+ outputAttribute.substring(1, outputAttribute.length());
				Method method = null;
				Class[] attributes = new Class[]{Boolean.class};
				Object[] attributesValue = new Object[]{new Boolean(true)};

				method = associationClass.getDeclaredMethod(methodName, attributes);
				method.invoke(associationObject, attributesValue);
				Logger.out.info("Added Op Attribute :" + outputAttribute);

			}

			Logger.out.info(genomicIdentifierSet);
		}
		catch (Exception e)
		{
			Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new DAOException(e.getMessage(), e);
		}
		return genomicIdentifierSet;

	}

	//	public static DetachedCriteria querySimple(List inputDsList, List outputDsList)
	//			throws DAOException
	//	{
	//		/**
	//		 * Create root DetachedCriteria for GenomicIdentifierSet
	//		 */
	//		DetachedCriteria genomicIdSetCriteria = DetachedCriteria
	//				.forClass(GenomicIdentifierSet.class);
	//		//System.out.println("caCore deprepnputDsList.size() :  " + inputDsList.size());
	//		Map addedAssociation = new HashMap();
	//		for (int i = 0; i < inputDsList.size(); i++)
	//		{
	//			NameValueBean bean = (NameValueBean) inputDsList.get(i);
	//			// Get Class of data source i.e Gene,MRNA or Protein
	//			String className = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
	//					bean.getName(), GCConstants.CLASS);
	//
	//			String roleName = MetadataManager.getRoleName("GenomicIdentifierSet", className);
	//
	//			// get attribute name of Gene.MRNA or protein representing data source 
	//			String classAttribute = MetadataManager.getDataSourceAttribute(
	//					GCConstants.DATASOURCE_NAME, bean.getName(), GCConstants.ATTRIBUTE);
	//			// get type of attribute
	//			String attributeType = MetadataManager.getDataSourceAttribute(
	//					GCConstants.DATASOURCE_NAME, bean.getName(), GCConstants.TYPE);
	//
	//			//			className = className.substring(0, 1).toLowerCase()
	//			//					+ className.substring(1, className.length());
	//
	//			Logger.out.info("Data Source ROLE :  " + roleName);
	//			Logger.out.info("Data Source ATTRIBUTE:  " + classAttribute);
	//			Logger.out.info("Data Source ATTRIBUTE TYPE :  " + attributeType);
	//
	//			/**
	//			 * Create DetachedCriteria for search on given datasource and its genomicId
	//			 */
	//			/**
	//			 * Check if Criteria already created.IF yed teh get teh DetachedCriteria object from Map
	//			 * else crete new DetachedCriteria 
	//			 */
	//			DetachedCriteria genomicCriteria = (DetachedCriteria) addedAssociation.get(roleName);
	//			if (genomicCriteria == null)
	//			{
	//				genomicCriteria = genomicIdSetCriteria.createCriteria(roleName);
	//				addedAssociation.put(roleName, genomicCriteria);
	//			}
	//			if (attributeType.equalsIgnoreCase("java.lang.Long"))
	//			{
	//				Long longValue = null;
	//				try
	//				{
	//					longValue = new Long(bean.getValue());
	//				}
	//				catch (Exception e)
	//				{
	//					Logger.out.error(e.getMessage(), e);
	//					throw new DAOException("Genomic Identifier for DataSource " + bean.getName()
	//							+ " must be Integer");
	//				}
	//				genomicCriteria.add(Restrictions.eq(classAttribute, longValue));
	//			}
	//			else
	//			{
	//				genomicCriteria.add(Restrictions.eq(classAttribute, bean.getValue()));
	//			}
	//		}
	//
	//		for (int j = 0; j < outputDsList.size(); j++)
	//		{
	//			String outputDSName = (String) outputDsList.get(j);
	//			String outputClassName = MetadataManager.getDataSourceAttribute(
	//					GCConstants.DATASOURCE_NAME, outputDSName, GCConstants.CLASS);
	//			String roleName = MetadataManager.getRoleName("GenomicIdentifierSet", outputClassName);
	//			DetachedCriteria outputGenomicCriteria = (DetachedCriteria) addedAssociation
	//					.get(roleName);
	//			if (outputGenomicCriteria == null)
	//			{
	//				outputGenomicCriteria = genomicIdSetCriteria.createCriteria(roleName);
	//				addedAssociation.put(roleName, outputGenomicCriteria);
	//			}
	//
	//			String outputAttribute = MetadataManager.getDataSourceAttribute(
	//					GCConstants.DATASOURCE_NAME, outputDSName, GCConstants.OUTPUT_ATTRIBUTE);
	//			Logger.out.info("Added Op Attribute :" + outputAttribute);
	//			outputGenomicCriteria.add(Restrictions.eq(outputAttribute, new Boolean(true)));
	//		}
	//
	//		Logger.out.info(genomicIdSetCriteria);
	//
	//		return genomicIdSetCriteria;
	//
	//	}
}
