package gov.nih.nci.system.dao.impl.orm;

import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.Protein;
import gov.nih.nci.common.net.Request;
import gov.nih.nci.common.net.Response;
import gov.nih.nci.common.util.Constant;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.common.util.NestedCriteria;
import gov.nih.nci.common.util.NestedCriteria2HQL;
import gov.nih.nci.system.dao.DAOException;
import gov.nih.nci.system.servicelocator.ServiceLocator;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.condition.IsReference;
import org.hibernate.Criteria;
import org.hibernate.JDBCException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.impl.CriteriaImpl;

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
    int recordsPerQuery=0;
	int maxRecordsPerQuery=0;
	static List dataSourceResult;
	static final String ATTRIBUTE = "ATTRIBUTE";
	static final String CLASS = "CLASS";
	static final String DATASOURCE = "DATASOURCE_NAME";
	static final String TYPE = "ATTRIBUTE_TYPE";
    /**
     * Default Constructor
     */
    public ORMDAOImpl()
	{
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

		try{
			counter = serviceLocator.getORMCounter(entityName);
		}
		catch(Exception e)
		{
			log.error("Could not retrieve proper datasource: \n " + e.getMessage());
			throw new DAOException("Could not retrieve proper datasource:  " + e);
		}


		Object obj = (Object)request.getRequest();
		Integer firstRow = (Integer)request.getFirstRow();
		Integer resultsPerQuery = (Integer)request.getRecordsCount();
		Boolean isCount = (Boolean)request.getIsCount();
		//System.out.println("boolean iscount = " + isCount.booleanValue());
		session = ormConn.openSession(counter);

		try
		{
			if (obj instanceof DetachedCriteria) 
			{
				hCriteria = ((org.hibernate.criterion.DetachedCriteria)request.getRequest()).getExecutableCriteria(session);
				
/**
 * @author sachin_lale
* ********************************************************************************************************************
				 * Get Whether predicate on confscore and / or frequency from Criteria Object
*/
				if(session.isConnected() && dataSourceResult==null)
				{
					Connection conn = session.connection();
					System.out.println(conn);
					Statement st = conn.createStatement();			
					ResultSet result = st.executeQuery("SELECT "+DATASOURCE+","+ATTRIBUTE+","+CLASS+","+TYPE+" FROM DATASOURCE");
					dataSourceResult = new ArrayList();
					while(result.next())
					{
						Map temp = new HashMap();
						temp.put(CLASS,result.getString(CLASS));
						temp.put(DATASOURCE,result.getString(DATASOURCE));
						temp.put(ATTRIBUTE,result.getString(ATTRIBUTE));
						temp.put(TYPE,result.getString(TYPE));
						dataSourceResult.add(temp);
					}
					System.out.println(st);
				}
				boolean isGenomicIdSet =false;
				System.out.println("entityName :" + entityName);
				if(entityName.equalsIgnoreCase(GenomicIdentifierSet.class.getName()))
				{
					isGenomicIdSet=true;
				}
				boolean isGreaterThanEqual =false;
				boolean isFreqGreaterThanEqual =false;
				List freqList = new ArrayList();
				int freqCounter=-1;
				int dsCounter=-1;
    			String confScorevalue="0";
    			String intValue="0";
    			String strValue="0";
		        CriteriaImpl impl = (CriteriaImpl)((DetachedCriteria)obj).getExecutableCriteria(session);
		        Iterator iter =impl.iterateExpressionEntries();
		        int j=0;
		        while(iter.hasNext())
		        {        	
		        	Object o = iter.next();
		        	String predicate = o.toString();
		        	CriteriaImpl.CriterionEntry a = (CriteriaImpl.CriterionEntry)o;
		        	System.out.println("Expression Predicates************** "+ predicate); 
		        	if(predicate.startsWith("confidenceScore"))
		        	{
		        		//isConfScore=true;
		        		if(predicate.indexOf(">=")>=0)
			        	{
		        			confScorevalue = predicate.substring("confidenceScore>=".length(),predicate.length());
			        		isGreaterThanEqual=true;
			        		System.out.println("isGreaterThanEqual************** "+ isGreaterThanEqual);
			        	}
		        		else if(predicate.indexOf(">")>=0)
			        	{
		        			confScorevalue = predicate.substring("confidenceScore>".length(),predicate.length());
			        		System.out.println("isGreaterThanEqual************** "+ isGreaterThanEqual);
			        	}
			        	
		        		//break;
		        	}	
		        	if(predicate.startsWith("frequency"))
		        	{
		        		//isFrequency=true;
		        		if(predicate.indexOf(">=")>=0)
			        	{
			        		FreqCriteria f = new FreqCriteria();
			        		intValue = predicate.substring("frequency>=".length(),predicate.length());
			        		isFreqGreaterThanEqual=true;
			        		f.setPredicate(Float.valueOf(intValue).floatValue());
			        		freqCounter++;
			        		freqList.add(f);
			        		System.out.println("isFreqGreaterThanEqual************** "+ isFreqGreaterThanEqual);
			        	}
		        		else if(predicate.indexOf(">")>=0)
			        	{
		        			FreqCriteria f = new FreqCriteria();
		        			intValue = predicate.substring("frequency>".length(),predicate.length());
			        		isFreqGreaterThanEqual=true;
			        		f.setPredicate(Float.valueOf(intValue).floatValue());
			        		freqCounter++;
			        		freqList.add(f);
			        		System.out.println("isFreqGreaterThanEqual************** "+ isFreqGreaterThanEqual);
			        	}
			        	
		        		//break;
		        	}
		        	if(predicate.startsWith("dataSource")&& !predicate.startsWith("dataSourceName"))
		        	{
		        		//isFrequency=true;
		        		if(predicate.indexOf("=")>=0)
			        	{
			        		
			        		strValue = predicate.substring("dataSource=".length(),predicate.length());
			        		//isFreqGreaterThanEqual=true;
			        		dsCounter++;
			        		FreqCriteria f = (FreqCriteria)freqList.get(dsCounter);
			        		f.setDataSource(strValue);
			        		System.out.println("isdataSpirce************** ");
			        	}
			        	
		        		//break;
		        	}	
		        }
		        System.out.println("freqList.size() :" +freqList.size());
		        for(int i=0;i<freqList.size();i++)
				{
					FreqCriteria f = (FreqCriteria)freqList.get(i);
					System.out.println("Freq: " + f.getDataSource() + " " + f.getPredicate());
				}
/**
 * @author sachin_lale
* ********************************************************************************************************************
*/				
				
				if (hCriteria != null)
				{
				    if(isCount != null && isCount.booleanValue())
				    {
				        rowCount = (Integer)hCriteria.setProjection(Projections.rowCount()).uniqueResult();
						//System.out.println("DetachedCriteria ORMDAOImpl ===== count = " + rowCount);
						hCriteria.setResultTransformer( Criteria.ROOT_ENTITY );
						hCriteria.setProjection( null );
				    }
				    else if((isCount != null && !isCount.booleanValue()) || isCount == null)
				    {
						if(firstRow != null)
				            hCriteria.setFirstResult(firstRow.intValue());
				        if(resultsPerQuery != null)
				        {
					        if(resultsPerQuery.intValue() > maxRecordsPerQuery)
					        {
					        	String msg = "Illegal Value for RecordsPerQuery: recordsPerQuery cannot be greater than maxRecordsPerQuery. RecordsPerQuery = " +
			                    recordsPerQuery + " maxRecordsPerQuery = " + maxRecordsPerQuery;
					        	log.error(msg);
					            throw new Exception(msg);
					        }
					        else
					        {
					            hCriteria.setMaxResults(resultsPerQuery.intValue());
					        }
				        }
				        else
				        {
				            hCriteria.setMaxResults(recordsPerQuery);

				        }
//				        Set resultSet = new HashSet(hCriteria.list());
//						rs = new ArrayList((Collection)resultSet);
				        rs = hCriteria.list();
				        if(isGenomicIdSet)
				        {
				        	float totalSet = calculateTotalNumberOfSets(rs);
				        	processConfidenceScoreQuery(rs,Float.valueOf(confScorevalue).floatValue(),totalSet,isGreaterThanEqual);
				        	processFrequency(rs,freqList,totalSet,isFreqGreaterThanEqual);
				        }
				    }
				}				
			}
			else if (obj instanceof NestedCriteria)
			{
				NestedCriteria crit = (NestedCriteria)obj ;
				List l = crit.getSourceObjectList();
				
				//System.out.println("ORMDAOImpl.query: it is a NestedCriteria Object ....");		
				NestedCriteria2HQL converter = new NestedCriteria2HQL((NestedCriteria)obj, ormConn.getConfiguration(counter), session);
				query = converter.translate();
				if (query != null)
				{
					if(isCount != null && isCount.booleanValue())
				    {			
//						System.out.println("ORMDAOImpl.  isCount .... .... | converter.getCountQuery() = " + converter.getCountQuery().getQueryString());
						rowCount = (Integer)converter.getCountQuery().uniqueResult();
						//System.out.println("ORMDAOImpl HQL ===== count = " + rowCount);					
					}
					else if((isCount != null && !isCount.booleanValue()) || isCount == null)
				    {	
				    	if(firstRow != null)
				    	{
					        query.setFirstResult(firstRow.intValue());				    		
				    	}
				    	if(resultsPerQuery != null)
				    	{
					        if(resultsPerQuery.intValue() > maxRecordsPerQuery)
					        {
					        	String msg = "Illegal Value for RecordsPerQuery: recordsPerQuery cannot be greater than maxRecordsPerQuery. RecordsPerQuery = " + recordsPerQuery + " maxRecordsPerQuery = " + maxRecordsPerQuery ;
					        	log.error(msg);
					            throw new Exception(msg);
					        }
					        else
					        {
					            query.setMaxResults(resultsPerQuery.intValue());
					        }				    		
				    	}
				        else
				        {
				            query.setMaxResults(recordsPerQuery);

				        }
				    	rs = query.list();
				    }				
				}
			}
			else if (obj instanceof HQLCriteria)
			{
				Query hqlQuery = session.createQuery(((HQLCriteria)obj).getHqlString());
				if(isCount != null && isCount.booleanValue())
			    {
					rowCount = new Integer(hqlQuery.list().size());
				}
				else if((isCount != null && !isCount.booleanValue()) || isCount == null)
			    {	
			    	if(firstRow != null)
			    	{
			    		hqlQuery.setFirstResult(firstRow.intValue());				    		
			    	}
			    	if(resultsPerQuery != null)
			    	{
				        if(resultsPerQuery.intValue() > maxRecordsPerQuery)
				        {
				        	String msg = "Illegal Value for RecordsPerQuery: recordsPerQuery cannot be greater than maxRecordsPerQuery. RecordsPerQuery = " + recordsPerQuery + " maxRecordsPerQuery = " + maxRecordsPerQuery ;
				        	log.error(msg);
				            throw new Exception(msg);
				        }
				        else
				        {
				        	hqlQuery.setMaxResults(resultsPerQuery.intValue());
				        }				    		
			    	}
			        else
			        {
			        	hqlQuery.setMaxResults(recordsPerQuery);
			        }
			    	rs = hqlQuery.list();
			    }				
			}
		}
		catch (JDBCException ex)
		{
			ex.printStackTrace();
			log.error("JDBC Exception in the ORMDAOImpl - " +ex.getMessage());
			throw new DAOException("JDBC Exception in the ORMDAOImpl" + ex);
		}
		catch(org.hibernate.HibernateException hbmEx)
		{
			hbmEx.printStackTrace();
			log.error(hbmEx.getMessage());
			throw new DAOException("Hibernate problem " +hbmEx);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			log.error(e.getMessage());
			throw new DAOException("Exception in the ORMDAOImpl "+e);
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
				log.error("Could not close the session - "+ eSession.getMessage());
				throw new DAOException("Could not close the session:  " + eSession);
			}
		}

		Response rsp = new Response();
		if(isCount != null && isCount.booleanValue())
		    rsp.setRowCount(rowCount);
		else
		    rsp.setResponse(rs);
		return rsp;
	}

	private  void loadProperties(){

		try{
			Properties _properties = new Properties();

			_properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("CORESystem.properties"));
			String resultsPerQuery = (String)_properties.getProperty("RECORDSPERQUERY");
			String maxResultsPerQuery = (String)_properties.getProperty("MAXRECORDSPERQUERY");

			if(resultsPerQuery != null)
			{
			    recordsPerQuery = new Integer(resultsPerQuery).intValue();
			}
			else
			{
			    recordsPerQuery = Constant.MAX_RESULT_COUNT_PER_QUERY;
			}

			if(maxResultsPerQuery != null)
			{
			    maxRecordsPerQuery = new Integer(maxResultsPerQuery).intValue();
			}

		}catch(IOException e)
		{
			log.error(e.getMessage());
			
			
		}
		catch(Exception ex){
			ex.printStackTrace();
			log.error(ex.getMessage());			
		}
	}
	/**
	 * 
	 * @return
	 */
	float calculateTotalNumberOfSets(List rs)
	{
		float totalScore = 0.0f; 
    	
    	/**
    	 * Calculate Total score
    	 */
		for(int i=0;i<rs.size();i++)
		{
			GenomicIdentifierSet set = (GenomicIdentifierSet)rs.get(i);
			Collection ontColl = set.getOrderOfNodeTraversalCollection();
			if(ontColl!=null)
			{
				totalScore =  totalScore+ontColl.size();
				set.setConfidenceScore(new Float(ontColl.size()));
			}	
		}
		return totalScore;
	}
	/**
	 * @author sachin_lale
	 * Business Logic to calculate Confidence score
	 * @param rs
	 * @param predicateValue
	 */
	void processConfidenceScoreQuery(List rs,float predicateValue,float totalScore,boolean isGreaterThanEqual)
	{
		System.out.println("totalScore ******"+totalScore);
		
		/**
		 * Business logic to calculate confscore and remove the filter 
		 * GenomicIdentifierSet   
		 * which 
		 */
		for(Iterator iter1 = rs.iterator();iter1.hasNext();)
		{
			GenomicIdentifierSet set = (GenomicIdentifierSet)iter1.next();
			System.out.println("Set ID In List******"+ set.getId());
			float confScore = set.getConfidenceScore().floatValue();
			confScore = confScore/totalScore;
			set.setConfidenceScore(new Float(confScore));
			if(isGreaterThanEqual && !(confScore>=predicateValue))
			{
				System.out.println("Set ID Removed******"+ set.getId());
				System.out.println("confScore ******"+ confScore);
				//newRs.add(set);
				iter1.remove();
			}
			else if(!(confScore>predicateValue))
			{
				System.out.println("Set ID Removed******"+ set.getId());
				System.out.println("confScore ******"+ confScore);
				//newRs.add(set);
				iter1.remove();
			}

		}
	}
	
	/**
	 * @author sachin_lale
	 * Business Logic to caluculate frequency and filter the result
	 * Frequency is calculated as count of genomicIdentifier throughout the set divided by Total_No_of_ONTs 
	 * throughout the set. 
	 * @param rs
	 * @param freqList
	 */
	void processFrequency(List rs,List freqList,float totalScore,boolean isFreqGreaterThanEqual) throws Exception
	{
		GenomicIdentifierSolution sol = new GenomicIdentifierSolution();
		
		System.out.println("freqList.size() :" +freqList.size());
		try
		{
			/**
			 * Attributes of Gene,Mrna,Protein domain objects
			 */
//			String dataSourceList[][] = new String[][]{{"ensemblGeneId","entrezGeneId","unigeneClusterId",},
//													{"ensemblTranscriptId","genbankAccession","refseqId"},
//													{"ensemblPeptideId","refseqId","uniprotkbPrimaryAccession","genbankAccession"}};
//			/**
//			 * DataSource name as stored in Schema DATASOURCE
//			 */
//			String dataSourceNames[][] = new String[][]{{"EnsemblGene","EntrezGene","UniGene"},
//					{"EnsemblTranscript","GenBankmRNA","RefSeqmRNA"},
//					{"EnsemblPeptide","RefSeqProtein","UniProtKB","GenBankProtein"}};
			List dsl =null;
			//0 - Gene 1- mrna 2 - prot
			HashMap frequencyMap = new HashMap();
			/**
			 * Loop over the GenomicIdentifierSet and count each genomic identifer appeared throughout 
			 * the result set 
			 * adn stored it in frequencyMap as genomicIdentifer(Key) and 
			 * count,DataSourceName,GenomicIdentifierSet Ids(values as ArrayLIst)
			 */
			System.out.println("rs.size(): "+rs.size());
			
			if(rs.size()==0)
				return;
			for(int i=0;i<rs.size();i++)
			{
				System.out.println("rs.size()1: "+rs.size());
				GenomicIdentifierSet set = (GenomicIdentifierSet)rs.get(i);
				Collection dataColl = new HashSet();
				set.setConsensusIdentifierDataCollection(dataColl);
				set.setGenomicIdentifierSolution(sol);	
				Long setID =new Long(set.getId());
				Gene gene = set.getGene();
				dsl = getAttibutes(CLASS,"Gene"); 
				for(int j=0;j<dsl.size();j++)
				{
					Map map = (Map)dsl.get(j);
					String dataSourceName = (String)map.get(DATASOURCE);
					String dataSourceAttribute = (String)map.get(ATTRIBUTE);
					String temp= dataSourceAttribute.substring(0,1).toUpperCase();
					String methodName = "get"+temp + dataSourceAttribute.substring(1,dataSourceAttribute.length());
					//System.out.println(methodName);
					Method method = Gene.class.getDeclaredMethod(methodName,null);
					Object value = method.invoke(gene,null);
					if(value!=null && frequencyMap.get(value.toString())==null)
					{
						List dsList = new ArrayList();
						dsList.add(new Integer(1));
						dsList.add(map);
						dsList.add(setID);						
						frequencyMap.put(value.toString(),dsList);
					}
					else if(value!=null)
					{
						List dsList = (ArrayList)frequencyMap.get(value.toString());
						Integer val = (Integer)dsList.get(0);
						int count = val.intValue();
						count++;
						dsList.set(0,new Integer(count));
						dsList.add(setID);
					}
				}
				MessengerRNA mrna = set.getMessengerRNA();
				dsl = getAttibutes(CLASS,"MessengerRNA");
				for(int j=0;j<dsl.size();j++)
				{
					Map map = (Map)dsl.get(j);
					String dataSourceName = (String)map.get(DATASOURCE);
					String dataSourceAttribute = (String)map.get(ATTRIBUTE);
					String temp= dataSourceAttribute.substring(0,1).toUpperCase();
					String methodName = "get"+temp + dataSourceAttribute.substring(1,dataSourceAttribute.length());
					//System.out.println(methodName);
					Method method = MessengerRNA.class.getDeclaredMethod(methodName,null);
					Object value = method.invoke(mrna,null);
					if(value!=null && frequencyMap.get(value.toString())==null)
					{
						List dsList = new ArrayList();
						dsList.add(new Integer(1));
						dsList.add(map);
						dsList.add(setID);
						frequencyMap.put(value.toString(),dsList);
					}
					else if(value!=null)
					{
						List dsList = (ArrayList)frequencyMap.get(value.toString());
						Integer val = (Integer)dsList.get(0);
						int count = val.intValue();
						count++;
						dsList.set(0,new Integer(count));
						dsList.add(setID);
					}
				}
				Protein protein = set.getProtein();
				dsl = getAttibutes(CLASS,"Protein");
				for(int j=0;j<dsl.size();j++)
				{
					Map map = (Map)dsl.get(j);
					String dataSourceName = (String)map.get(DATASOURCE);
					String dataSourceAttribute = (String)map.get(ATTRIBUTE);
					String temp= dataSourceAttribute.substring(0,1).toUpperCase();
					String methodName = "get"+temp + dataSourceAttribute.substring(1,dataSourceAttribute.length());
					//System.out.println(methodName);
					Method method = Protein.class.getDeclaredMethod(methodName,null);
					Object value = method.invoke(protein,null);
					if(value!=null && frequencyMap.get(value.toString())==null)
					{
						List dsList = new ArrayList();
						dsList.add(new Integer(1));
						dsList.add(map);
						dsList.add(setID);
						frequencyMap.put(value.toString(),dsList);
					}
					else if(value!=null)
					{
						List dsList = (ArrayList)frequencyMap.get(value.toString());
						Integer val = (Integer)dsList.get(0);
						int count = val.intValue();
						count++;
						dsList.set(0,new Integer(count));
						dsList.add(setID);
					}
				}
			}
			/**
			 * Construct ConsensusData object at calculate frequency as genomicIdentifier_Count/Total_No_of_ONTs 
			 * 
			 */
			Collection consensusDataCollection = new ArrayList();
			Set keySet = frequencyMap.keySet();
			for(Iterator iter = keySet.iterator();iter.hasNext();)
			{
				boolean isSetToRemove =false;
				int setIdCounter=0;
				String key = (String)iter.next();
				List dsList =  (List)frequencyMap.get(key);
				StringBuffer dsClassName = new StringBuffer("edu.wustl.geneconnect.domain.");
				Map tempMap = (Map)dsList.get(1);
				dsClassName.append((String)tempMap.get(DATASOURCE));
				StringBuffer dataSourceType = new StringBuffer("java.lang."); 
				dataSourceType.append((String)tempMap.get(TYPE));
				Integer count = (Integer)dsList.get(0);
				float frequency = count.floatValue()/totalScore;
				for(int i=0;i<freqList.size();i++)
				{
					FreqCriteria fq = (FreqCriteria)freqList.get(i);
					if(fq.getDataSource().equalsIgnoreCase((String)tempMap.get(DATASOURCE)))
					{
						if(isFreqGreaterThanEqual && !(frequency>=fq.getPredicate()))
						{
							isSetToRemove=true;
						}
						else if(!(frequency>fq.getPredicate()))
						{
							isSetToRemove=true;
						}
					}
				}
				/**
				 * Construct  GenomicIdentifer object with respect to ots DataSource 
				 */
				Class dataSourceClass = Class.forName(dsClassName.toString());
				Class typeClass = Class.forName(dataSourceType.toString());
				Method methodToSetId =null;
				GenomicIdentifier genomicIdentifier = (GenomicIdentifier)Class.forName(dsClassName.toString()).newInstance();
				System.out.println("123: " + genomicIdentifier);
				methodToSetId= dataSourceClass.getMethod("setGenomicIdentifier",typeClass);
				if(typeClass.getName().equalsIgnoreCase(Long.class.getName()))
				{
					methodToSetId.invoke(genomicIdentifier,new Long[]{new Long(key)});
				}
				else
				{
					methodToSetId.invoke(genomicIdentifier,new String[]{key});
				}
				
//				if(((String)dsList.get(1)).equalsIgnoreCase("EntrezGene"))
//				{
//					methodToSetId= klass.getMethod("setGenomicIdentifier",Long.class);
//					methodToSetId.invoke(genomicIdentifier,new Long[]{new Long(key)});
//				}
//				else
//				{
//					methodToSetId= klass.getMethod("setGenomicIdentifier",String.class);
//					methodToSetId.invoke(genomicIdentifier,new String[]{key});
//				}
//				genomicIdentifier.setGenomicIdentifier((Object)key);
				System.out.println("124: " + genomicIdentifier);
				
				//System.out.println("genomicIdentifier: "+genomicIdentifier.getGenomicIdentifier());
				ConsensusIdentifierData data = new ConsensusIdentifierData();
				data.setFrequency(new Float(frequency));
				data.setGenomicIdentifier((GenomicIdentifier)genomicIdentifier);
				for(int i =2;i<dsList.size();i++)
				{
					
					/**
					 * if the genomic identiifer appears in this GenomicIDSet then set IdentiferData object.
					 */
					Long setID = (Long)dsList.get(i);
					for(Iterator rsIter = rs.iterator();rsIter.hasNext();)
					{
						GenomicIdentifierSet set = (GenomicIdentifierSet)rsIter.next();
						System.out.println("Set in LIst for FREQ: " + set.getId()+"-----"+key+"------"+frequency);
						if(isSetToRemove && set.getId().compareTo(setID)==0)
						{
							System.out.println("Set ti rempve for FREQ: " + set.getId());
							rsIter.remove();
						}
						else if(set.getId().compareTo(setID)==0)
						{
							Collection temp  = set.getConsensusIdentifierDataCollection();
							temp.add(data);
							set.setGenomicIdentifierSolution(sol);
						}
					}
//					if(isSetToRemove)
//					{
//						GenomicIdentifierSet set = (GenomicIdentifierSet)rs.get(setID.intValue()-setIdCounter);
//						System.out.println("Set ti rempve for FREQ: " + set.getId());
//						System.out.println("ID and Counter: " + setID +"---" + setIdCounter);
//						
//						rs.remove(setID.intValue()-setIdCounter);
//						setIdCounter++;
//					}
//					else
//					{	
//						GenomicIdentifierSet set = (GenomicIdentifierSet)rs.get(setID.intValue()-setIdCounter);
//						
//					}
				}
				if(!isSetToRemove)
					consensusDataCollection.add(data);
			}
			sol.setConsensusIdentifierDataCollection(consensusDataCollection);
			for(int i=0;i<rs.size();i++)
			{
				GenomicIdentifierSet set = (GenomicIdentifierSet)rs.get(i);
				System.out.println("Data Collection: " +set.getConsensusIdentifierDataCollection().size());
			}	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			log.error("Exception in the ORMDAOImpl while calcuting Frequency - " +ex.getMessage());
			throw new Exception("Exception in the ORMDAOImpl while calcuting Frequency - " + ex);
		}
	}
	List getAttibutes(String key,String value)
	{
		List l = new ArrayList();
		for(int i=0;i<dataSourceResult.size();i++)
		{
			Map map = (Map)dataSourceResult.get(i);
			String str= (String)map.get(key);
			if(str.equalsIgnoreCase(value))
			{
				l.add(map);
			}
		}
		return l;
	}
	public static void main(String a[])
	{
		try
		{
			String dataSourceList[][] = new String[][]{{"ensemblGeneId","entrezGeneId","unigeneClusterId",},
													{"ensemblTranscriptId","genbankAccession","refseqId"},
													{"ensemblPeptideId","refseqId","uniprotkbPrimaryAccession","genbankAccession"}};
			//0 - Gene 1- mrna 2 - prot
			HashMap frequencyMap = new HashMap();
			Gene gene = new Gene();
					
			for(int i=0;i<dataSourceList[2].length;i++)
			{
				//System.out.println(dataSourceList[0][i].substring(0,1));
				//System.out.println(dataSourceList[0][i].substring(1,dataSourceList[0][i].length()));
				String temp= dataSourceList[2][i];
				
				System.out.println(temp);
				
				
			}
		}
		catch(Exception ex)
		{
			
		}
	}
}
class FreqCriteria
{
	String dataSource;
	float predicate;
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public float getPredicate() {
		return predicate;
	}
	public void setPredicate(float predicate) {
		this.predicate = predicate;
	}

}
