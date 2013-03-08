/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package gov.nih.nci.system.dao.impl.orm;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.Criteria;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;

import edu.wustl.geneconnect.bizlogic.HQLProcessor;
import edu.wustl.geneconnect.bizlogic.ResultProcessor;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.utility.Constants;
import edu.wustl.geneconnect.utility.MetadataManager;
import gov.nih.nci.common.net.Request;
import gov.nih.nci.common.net.Response;
import gov.nih.nci.common.util.Constant;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.common.util.NestedCriteria;
import gov.nih.nci.common.util.NestedCriteria2HQL;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.servicelocator.ServiceLocator;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2001-2004 SAIC. Copyright 2001-2003 SAIC. This software was developed in conjunction with the National Cancer Institute,
 * and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
 * wherever such third-party acknowledgments normally appear.
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software.
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
 * the recipient to use any trademarks owned by either NCI or SAIC-Frederick.
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 * SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */
/**
 * ORMDAOImpl converts a request to a hibernate query that returns results from a data source
 * @author caBIO Team
 * @version 1.0
 */
public class ORMDAOImpl
{

	private static Logger log = Logger.getLogger(ORMDAOImpl.class.getName());
	public SessionFactory sf;
	int recordsPerQuery = 0;
	int maxRecordsPerQuery = 0;

	ResultProcessor rp = new ResultProcessor();

	/**
	 * Default Constructor
	 */
	public ORMDAOImpl()
	{
		try
		{
			/**
			 * @author sachin_lale
			 * 
			 * set  log4j configuration file for logging purpoese.
			 */
			Properties logproperties = new Properties();

			logproperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(
					"log4j.properties"));
			PropertyConfigurator.configure(logproperties);
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		loadProperties();
	}

	/**
	 * Return the resultset of the query embedded in an object of gov.nih.nci.common.net.Response
	 * @param request - a gov.nih.nci.common.net.Request object passed from client
	 * @return an object of gov.nih.nci.common.net.Response that contains the query resultset
	 * @throws DAOException
	 */
	public Response query(Request request) throws DAOException, Exception
	{

		List rs = null;
		int counter = 0;
		ORMConnection ormConn = ORMConnection.getInstance();
		org.hibernate.Session session = null;
		Criteria hCriteria = null;
		Integer rowCount = null;
		Query query = null;

		// Get the ORM counter from ServiceLocator
		ServiceLocator serviceLocator = new ServiceLocator();
		String entityName = request.getDomainObjectName();

		try
		{
			counter = serviceLocator.getORMCounter(entityName);
		}
		catch (Exception e)
		{
			log.error("Could not retrieve proper datasource: \n " + e.getMessage());
			throw new DAOException("Could not retrieve proper datasource:  " + e);
		}

		Object obj = (Object) request.getRequest();
		Integer firstRow = (Integer) request.getFirstRow();
		Integer resultsPerQuery = (Integer) request.getRecordsCount();
		Boolean isCount = (Boolean) request.getIsCount();
		session = ormConn.openSession(counter);
		/**
		 * Check if target domain object is GenomicIdentifierSet class
		 * if true then  calculate confidence and freuency
		 */
		boolean isGenomicIdSet = false;
		boolean processResult = false;
		if (entityName.equalsIgnoreCase(GenomicIdentifierSet.class.getName()))
		{
			isGenomicIdSet = true;
		}
		/**
		 * ***Begins****
		 * @author sachin_lale
		 * ********************************************************************************************************************
		 * Get JDNC connection and store metadata in cache
		 */
		if (session.isConnected())
		{
			/**
			 * Get JDBC connection to get Metadata of DATASOURCE table through SQL.
			 * Store the result in a List of Map where each map is stroe as KEY=ColumnName
			 * VALUE=ColumnValue for that row.
			 */
			Connection conn = session.connection();
			//log.info("JDBC Connetion successfull " + conn);
			MetadataManager.connect(conn);
		}
		try
		{
			if (obj instanceof DetachedCriteria)
			{

				hCriteria = ((org.hibernate.criterion.DetachedCriteria) request.getRequest())
						.getExecutableCriteria(session);

	
				

				CriteriaImpl impl = (CriteriaImpl) ((DetachedCriteria) obj)
						.getExecutableCriteria(session);
				rp.interpretCriteria(impl);

				/**
				 * Create new DetachedCriteria
				 */
				if (rp.isCreateNewCriteria() == true)
				{
					DetachedCriteria newCriteria = rp.createNewCriteria();
					hCriteria = newCriteria.getExecutableCriteria(session);
					processResult=true;
				}

				/**
				 * @author sachin_lale
				 * ****ENDS*******
				 * ********************************************************************************************************************
				 */

				if (hCriteria != null)
				{
					if (isCount != null && isCount.booleanValue())
					{
						rowCount = (Integer) hCriteria.setProjection(Projections.rowCount())
								.uniqueResult();

						hCriteria.setResultTransformer(Criteria.ROOT_ENTITY);
						hCriteria.setProjection(null);
					}
					else if ((isCount != null && !isCount.booleanValue()) || isCount == null)
					{
						if (firstRow != null)
							hCriteria.setFirstResult(firstRow.intValue());
						if (resultsPerQuery != null)
						{
							if (resultsPerQuery.intValue() > maxRecordsPerQuery)
							{
								String msg = "Illegal Value for RecordsPerQuery: recordsPerQuery cannot be greater than maxRecordsPerQuery. RecordsPerQuery = "
										+ recordsPerQuery
										+ " maxRecordsPerQuery = "
										+ maxRecordsPerQuery;
								log.error(msg);
								throw new Exception(msg);
							}
							else
							{
								//hCriteria.setMaxResults(resultsPerQuery.intValue());
								hCriteria.setMaxResults(recordsPerQuery);
							}
						}
						else
						{
							hCriteria.setMaxResults(recordsPerQuery);

						}
						rs = hCriteria.list();
						
					}
				}
			}
			else if (obj instanceof NestedCriteria)
			{
				NestedCriteria crit = (NestedCriteria) obj;
				List l = crit.getSourceObjectList();
				
				if(l.size()>0)
				{
					Object o = l.get(0);
					if (o instanceof GenomicIdentifierSet && isGenomicIdSet)
					{
						GenomicIdentifierSet set = (GenomicIdentifierSet) o;
						rp.interpretCriteria(set);
						processResult=true;
						
					}
					else if (o instanceof Gene && isGenomicIdSet)
					{
						GenomicIdentifierSet set = new GenomicIdentifierSet();
						set.setGene((Gene)o);
						rp.interpretCriteria(set);
						processResult=true;
						
					}
					else if (o instanceof MessengerRNA && isGenomicIdSet)
					{
						GenomicIdentifierSet set = new GenomicIdentifierSet();
						set.setMessengerRNA((MessengerRNA)o);
						rp.interpretCriteria(set);
						processResult=true;
						
					}
					else if (o instanceof Protein && isGenomicIdSet)
					{
						GenomicIdentifierSet set = new GenomicIdentifierSet();
						set.setProtein((Protein)o);
						rp.interpretCriteria(set);
						processResult=true;
						
					}
				}
				//System.out.println("ORMDAOImpl.query: it is a NestedCriteria Object ....");		
				NestedCriteria2HQL converter = new NestedCriteria2HQL((NestedCriteria) obj, ormConn
						.getConfiguration(counter), session);
				query = converter.translate();
				if (query != null)
				{
					if (isCount != null && isCount.booleanValue())
					{
						//						System.out.println("ORMDAOImpl.  isCount .... .... | converter.getCountQuery() = " + converter.getCountQuery().getQueryString());
						rowCount = (Integer) converter.getCountQuery().uniqueResult();
						//System.out.println("ORMDAOImpl HQL ===== count = " + rowCount);					
					}
					else if ((isCount != null && !isCount.booleanValue()) || isCount == null)
					{
						if (firstRow != null)
						{
							query.setFirstResult(firstRow.intValue());
						}
						if (resultsPerQuery != null)
						{
							if (resultsPerQuery.intValue() > maxRecordsPerQuery)
							{
								String msg = "Illegal Value for RecordsPerQuery: recordsPerQuery cannot be greater than maxRecordsPerQuery. RecordsPerQuery = "
										+ recordsPerQuery
										+ " maxRecordsPerQuery = "
										+ maxRecordsPerQuery;
								log.error(msg);
								throw new Exception(msg);
							}
							else
							{
								//query.setMaxResults(resultsPerQuery.intValue());
								query.setMaxResults(recordsPerQuery);
							}
						}
						else
						{
							query.setMaxResults(recordsPerQuery);

						}
					//	System.out.println("QUERY "+query.getQueryString());
						
						rs = query.list();
						
						
						
					}
				}
			}
			else if (obj instanceof HQLCriteria)
			{
				Query hqlQuery = session.createQuery(((HQLCriteria) obj).getHqlString());
				log.info("HQL: " +hqlQuery.getQueryString());
				boolean isToprocessGC=false;
				GenomicIdentifierSet gset =null;
				if(isGenomicIdSet)
				{
					log.info("sac1" );
					if(hqlQuery.getQueryString().indexOf("From " +Constants.DOMAIN_CLASSNAME_PREFIX+".GenomicIdentifierSet")==0)
					{
						log.info("got GSEt in From query" );
						HQLProcessor hqlProcessor = new HQLProcessor();
						gset = hqlProcessor.interpretHQL(hqlQuery.getQueryString());
						if(gset!=null)
						{
							log.info("isToprocessGC=true" );
							isToprocessGC=true;
						}
						else
						{
							processResult=false;
							isGenomicIdSet=false;
							log.info("processResult=false" );
						}
					}
				}
				if (isCount != null && isCount.booleanValue())
				{
					rowCount = new Integer(hqlQuery.list().size());
				}
				else if ((isCount != null && !isCount.booleanValue()) || isCount == null)
				{
					if (firstRow != null)
					{
						hqlQuery.setFirstResult(firstRow.intValue());
					}
					if (resultsPerQuery != null)
					{
						if (resultsPerQuery.intValue() > maxRecordsPerQuery)
						{
							String msg = "Illegal Value for RecordsPerQuery: recordsPerQuery cannot be greater than maxRecordsPerQuery. RecordsPerQuery = "
									+ recordsPerQuery
									+ " maxRecordsPerQuery = "
									+ maxRecordsPerQuery;
							log.error(msg);
							throw new Exception(msg);
						}
						else
						{
							//hqlQuery.setMaxResults(resultsPerQuery.intValue());
							hqlQuery.setMaxResults(recordsPerQuery);
						}
					}
					else
					{
						hqlQuery.setMaxResults(recordsPerQuery);
					}
					if(isToprocessGC)
					{
						log.info("Querying again with domin objects");
						ApplicationService applicationService = ApplicationServiceProvider.getApplicationService();
						rs = applicationService.search("edu.wustl.geneconnect.domain.GenomicIdentifierSet", gset);
						isGenomicIdSet=false;
						processResult=false;
					}
					else
					{	
						log.info("Querin HQL from CQL" );
						rs = hqlQuery.list();
						log.info("Querin HQL from CQL: rs size: "+rs.size());
					}	
				}
			}
			//System.out.println("rs " + rs.size());
			/**
			 * if target object is GenomicIdentifier class then calulate confidence and frequency.
			 */
			//if (isGenomicIdSet)
			if (processResult)
			{
				log.info("New search started");
				log.info("ResultSet Size before prepareResult(): " + rs.size());
				long t1 = System.currentTimeMillis();
				
				if(rp.getSelectedInputDataSourceList().size()>0)
				{
					rp.processResult(rs);
				}	
				long t2 = System.currentTimeMillis();
				log.info("Total TIme required: " +(t2-t1)/1000);

			}
		}
		catch (JDBCException ex)
		{
			ex.printStackTrace();
			log.error("JDBC Exception in the ORMDAOImpl - " + ex.getMessage());
			throw new DAOException("JDBC Exception in the ORMDAOImpl" + ex);
		}
		catch (org.hibernate.HibernateException hbmEx)
		{
			hbmEx.printStackTrace();
			log.error(hbmEx.getMessage());
			throw new DAOException("Hibernate problem " + hbmEx);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error(e.getMessage());
			throw new DAOException("Exception in the ORMDAOImpl " + e);
		}
		finally
		{
			try
			{
				session.clear();
				session.close();
			}
			catch (Exception eSession)
			{
				log.error("Could not close the session - " + eSession.getMessage());
				throw new DAOException("Could not close the session:  " + eSession);
			}
		}
		
		Response rsp = new Response();
		if (isCount != null && isCount.booleanValue())
			rsp.setRowCount(rowCount);
		else
			rsp.setResponse(rs);
		return rsp;
	}

	private void loadProperties()
	{

		try
		{
			Properties _properties = new Properties();

			_properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(
					"CORESystem.properties"));
			String resultsPerQuery = (String) _properties.getProperty("RECORDSPERQUERY");
			String maxResultsPerQuery = (String) _properties.getProperty("MAXRECORDSPERQUERY");

			if (resultsPerQuery != null)
			{
				recordsPerQuery = new Integer(resultsPerQuery).intValue();
			}
			else
			{
				recordsPerQuery = Constant.MAX_RESULT_COUNT_PER_QUERY;
			}

			if (maxResultsPerQuery != null)
			{
				maxRecordsPerQuery = new Integer(maxResultsPerQuery).intValue();
			}
		}
		catch (IOException e)
		{
			log.error(e.getMessage());

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			log.error(ex.getMessage());
		}
	}

	public static void main(String a[])
	{
		String predicate = "type=DIREXT or type=INFERRED";
		if (predicate.startsWith("type"))
		{
			int indexOfOR = predicate.indexOf("or type");
			if (indexOfOR > 0)
			{
				int firstEq = predicate.indexOf("=", 0);
				int seconfEq = predicate.indexOf(predicate
						.substring(indexOfOR + "or type".length()));

				String firsttype = predicate.substring(firstEq + 1, indexOfOR - 1);
				String secondType = predicate.substring(seconfEq + 1, predicate.length());
				System.out.println(firsttype + "--" + firsttype.length());
				System.out.println(secondType + "--" + secondType.length());

			}
			else
			{

			}
		}
	}

}
