/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.MetadataManager</p> 
 */

package edu.wustl.geneconnect.utility;
/**
 * This class stored the meta data information from database used to process bizlogic
 */
import gov.nih.nci.system.dao.DAOException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * This class will implement all business logic to provide geneconnect metadata.
 * @author sachin_lale
 */
public class MetadataManager 
{

	private static Logger log = Logger.getLogger(MetadataManager.class.getName());
	
	// Store datasoruceid , dataSource name
	private static List dataSourcesToDisplay = null;

	// represents the DATSOURCE meta data table
	private static List dataSources = null;

	// represents the ROLE_LOOKUP meta data table
	private static List roleLookup = null;
	
	//	 represents the ONT list  meta data table
	private static Map ontMap = null;
	
//	 represents the LINKTYPE meta data table
	private static List linkType = null;
	
	private static Connection connection = null;

	/** MetadataManager as a singleton class */
	private static MetadataManager metadataManagerInstance = new MetadataManager();

	static final String ATTRIBUTE = "ATTRIBUTE";
	static final String OUTPUT_ATTRIBUTE = "OUTPUT_ATTRIBUTE";
	static final String CLASS = "CLASS";
	static final String GENOMIC_IDENTIFIER_CLASS = "GENOMIC_IDENTIFIER_CLASS";
	static final String DATASOURCE_NAME = "DATASOURCE_NAME";
	static final String DATASOURCE_ID = "DATASOURCE_ID";
	static final String TYPE = "ATTRIBUTE_TYPE";
	static final String SOURCE_CLASS = "SOURCE_CLASS";
	static final String TARGET_CLASS = "TARGET_CLASS";
	static final String ROLE_NAME = "ROLE_NAME";

	/** Method to return instance of this class
	 * @return MetadataManager Returns object of this class
	 */

	public static synchronized void connect(Connection conn) throws Exception
	{
		if (connection == null)
		{
			connection = conn;
			populateMetadata();
		}
	}

	/**
	 * Calls method to populate metadata     
	 * @throws DAOException
	 */
	public static void populateMetadata() throws Exception
	{
		try
		{

			setDataSource();
			setRoleLookup();
			setLinkType();
			setONTMap();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Execute query and store te meta data of DATASOURCE
	 * @throws DAOException
	 */
	private static void setDataSource() throws Exception
	{
		try
		{
			dataSources = new ArrayList();
			Statement st = connection.createStatement();
			ResultSet result = st.executeQuery("SELECT " + DATASOURCE_ID + "," + DATASOURCE_NAME
					+ "," + ATTRIBUTE + "," + OUTPUT_ATTRIBUTE + "," + GENOMIC_IDENTIFIER_CLASS
					+ "," + CLASS + "," + TYPE + " FROM DATASOURCE");

			while (result.next())
			{
				Map temp = new HashMap();
				temp.put(CLASS, result.getString(CLASS));
				temp.put(GENOMIC_IDENTIFIER_CLASS, result.getString(GENOMIC_IDENTIFIER_CLASS));
				temp.put(DATASOURCE_ID, result.getString(DATASOURCE_ID));
				temp.put(DATASOURCE_NAME, result.getString(DATASOURCE_NAME));
				temp.put(ATTRIBUTE, result.getString(ATTRIBUTE));
				temp.put(OUTPUT_ATTRIBUTE, result.getString(OUTPUT_ATTRIBUTE));
				temp.put(TYPE, result.getString(TYPE));
				dataSources.add(temp);
			}
		}
		catch (SQLException e)
		{
			//Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new Exception(e.getMessage());

		}
	}

	/**
	 * Execute query and store te meta data of ROLE_LOOKUP
	 * @throws DAOException
	 */
	private static void setRoleLookup() throws Exception
	{
		try
		{
			roleLookup = new ArrayList();
			Statement st = connection.createStatement();
			ResultSet result = st.executeQuery("SELECT " + SOURCE_CLASS + "," + TARGET_CLASS + ","
					+ ROLE_NAME + " FROM ROLE_LOOKUP");

			while (result.next())
			{
				Map temp = new HashMap();
				temp.put(SOURCE_CLASS, result.getString(SOURCE_CLASS));
				temp.put(TARGET_CLASS, result.getString(TARGET_CLASS));
				temp.put(ROLE_NAME, result.getString(ROLE_NAME));
				roleLookup.add(temp);
			}

		}
		catch (SQLException e)
		{
			//Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new Exception(e.getMessage());

		}
	}
	
	/**
	 * Execute query and store the meta data of LINKTYPE
	 * @throws DAOException
	 */
	private static void setLinkType() throws Exception
	{
		try
		{
			linkType = new ArrayList();
			Statement st = connection.createStatement();
			String sql = "SELECT "+Constants.LINK_TYPE_ID+","+Constants.LINK_TYPE_NAME+" FROM "+Constants.LINKTYPE_TABLE;
			log.info("SQL FO LINKTYPE: " +sql);
			ResultSet result = st.executeQuery(sql);

			while (result.next())
			{
				Map temp = new HashMap();
				temp.put(Constants.LINK_TYPE_ID, result.getString(Constants.LINK_TYPE_ID));
				temp.put(Constants.LINK_TYPE_NAME, result.getString(Constants.LINK_TYPE_NAME));
				linkType.add(temp);
			}

		}
		catch (SQLException e)
		{
			//Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new Exception(e.getMessage());

		}
	}
	
	/**
	 * Execute query and store the meta data of ONT
	 * @throws DAOException
	 */
	private static void setONTMap() throws Exception
	{
		try
		{
			long t1 = System.currentTimeMillis();
			ontMap = new Hashtable();
			Statement st = connection.createStatement();
			String sql = "SELECT o."+Constants.ONT_PATH_ID+",d."+Constants.DATASOURCE_NAME+",o."+
						 Constants.ONT_LINKTYPE_ID+",o."+Constants.ONT_NEXT_PATH_ID+",o."+ Constants.ONT_PREV_PATH_ID+
								" FROM "+Constants.ONT_TABLE +" o,"+Constants.DATASOURCE_TABLE+" d WHERE o."+Constants.ONT_SOURCE_DS_ID+"=d."+Constants.DATASOURCE_ID+" ORDER BY o."+Constants.ONT_PATH_ID;
			log.info("SQL FOR ONT: " +sql);
			log.info("ONT debug1: ");
			ResultSet result = st.executeQuery(sql);
			log.info("ONT debug2: "+result);
			List dataSourcelist = null;
			Long ontid=null;
			log.info("ONT debug3: ");
			while (result.next())
			{
				if(result.getLong(Constants.ONT_PREV_PATH_ID)==0)
				{
					dataSourcelist = new ArrayList();
					ontid = new Long(result.getLong(Constants.ONT_PATH_ID));
				}
				dataSourcelist.add(result.getString(Constants.DATASOURCE_NAME));
				if(result.getLong(Constants.ONT_LINKTYPE_ID)>0)
				{
					String linkTypeId =result.getString(Constants.ONT_LINKTYPE_ID);
					String linkTypeName = getLinkTypeAttribute(Constants.LINK_TYPE_ID,linkTypeId,Constants.LINK_TYPE_NAME);
					dataSourcelist.add(linkTypeName);
				}
				else
				{
					ontMap.put(ontid,dataSourcelist);
				}
			}
			log.info("ONT debug6: ");
			long t2 = System.currentTimeMillis();
			log.info("ONT debug7: ");
			log.info("Time for caching ONT :" + ((t2-t1)/1000));
			log.info("ONT debug8: ");
			log.info("ONT count---"+ontMap.keySet().size());
			log.info("ONT debug9: ");
//			for(int i=0;i<keyList.size();i++)
//			{
//				log.info("ss:"+keyList.get(i)+"---"+ontMap.get(keyList.get(i))); 
//			}
		}
		catch (SQLException e)
		{
			//Logger.out.error(e.getMessage(), e);
			e.printStackTrace();
			throw new Exception(e.getMessage());

		}
	}
	 
	/**
	 * Returns a list of all data sources.
	 * @return List of Data source names.
	 */
	public static List getDataSourcesToDisplay() throws Exception
	{
		return dataSourcesToDisplay;
	}

	/**
	 * Returns a list of all data sources.
	 * @return List of Data source names.
	 */
	public static List getDataSources() throws Exception
	{
		return dataSources;
	}

	/**
	 * Returns the value of given column name of DATASOURCE 
	 * @param searchkey - Search criteria on column name
	 * @param searchvalue - value to be search on column name
	 * @param returnkey - column name whose value to return
	 * @return
	 */
	public static String getDataSourceAttribute(String searchkey, String searchvalue,
			String returnkey)
	{
		String dataSourceAttribute = "";
		List l = new ArrayList();
		for (int i = 0; i < dataSources.size(); i++)
		{
			Map map = (Map) dataSources.get(i);
			String str = (String) map.get(searchkey);
			if (str.equalsIgnoreCase(searchvalue))
			{
				dataSourceAttribute = (String) map.get(returnkey);
				return dataSourceAttribute;
			}
		}
		return null;
	}

	public static String getDataSourceAttribute(String searchkey, String searchvalue,
			String searchkey1, String searchvalue1, String returnkey)
	{
		String dataSourceAttribute = "";
		List l = new ArrayList();
		for (int i = 0; i < dataSources.size(); i++)
		{
			Map map = (Map) dataSources.get(i);
			String str = (String) map.get(searchkey);
			String str1 = (String) map.get(searchkey1);
			if (str.equalsIgnoreCase(searchvalue) && str1.equalsIgnoreCase(searchvalue1))
			{
				dataSourceAttribute = (String) map.get(returnkey);
				return dataSourceAttribute;
			}
		}
		return null;
	}

	/** 
	 * @author sachin_lale
	 * 
	 * Returns a List containg MAP(record of DATASOURCE).
	 *   
	 * @param key
	 * @param value
	 * @return
	 */
	public static List getAttibutes(String key, String value)
	{
		List l = new ArrayList();
		for (int i = 0; i < dataSources.size(); i++)
		{
			Map map = (Map) dataSources.get(i);
			String str = (String) map.get(key);
			if (str.equalsIgnoreCase(value))
			{
				l.add(map);
			}
		}
		return l;
	}

	/**
	 * returns the association role name as specified in Object Model  
	 * @param sourceClass
	 * @param targetClass
	 * @return
	 */
	public static String getRoleName(String sourceClass, String targetClass)
	{
		String roleName = "";
		//System.out.println(sourceClass+"--"+targetClass);
		for (int i = 0; i < roleLookup.size(); i++)
		{
			//System.out.println("roleLookup :" +roleLookup.size());
			Map map = (Map) roleLookup.get(i);
			String sourceStr = (String) map.get(SOURCE_CLASS);
			String targetStr = (String) map.get(TARGET_CLASS);
			if ((sourceStr.equalsIgnoreCase(sourceClass))
					&& (targetStr.equalsIgnoreCase(targetClass)))
			{
				roleName = (String) map.get(ROLE_NAME);
				return roleName;
			}
		}
		return null;
	}

	public static String getRoleLookUpAttribute(String searchkey, String searchvalue,
			String searchkey1, String searchvalue1, String returnkey)
	{
		String roleLookupAttribute = "";

		for (int i = 0; i < roleLookup.size(); i++)
		{
			Map map = (Map) roleLookup.get(i);
			//System.out.println("MAp : " + map);
			String str = (String) map.get(searchkey);
			String str1 = (String) map.get(searchkey1);
			//System.out.println("str---str1" + str + "----" + str1);
			if (str.equalsIgnoreCase(searchvalue) && str1.equalsIgnoreCase(searchvalue1))
			{
				roleLookupAttribute = (String) map.get(returnkey);
				return roleLookupAttribute;
			}
		}
		return null;
	}
	/**
	 * Returns the value of given column name of LINKTYPE 
	 * @param searchkey - Search criteria on column name
	 * @param searchvalue - value to be search on column name
	 * @param returnkey - column name whose value to return
	 * @return
	 */
	public static String getLinkTypeAttribute(String searchkey, String searchvalue,
			String returnkey)
	{
		String linkTypeAttribute = "";
		for (int i = 0; i < linkType.size(); i++)
		{
			Map map = (Map) linkType.get(i);
			String str = (String) map.get(searchkey);
			if (str.equalsIgnoreCase(searchvalue))
			{
				linkTypeAttribute = (String) map.get(returnkey);
				return linkTypeAttribute;
			}
		}
		return null;
	}
	/**
	 * Returns the List of paht(datasource,linktype)for a given ONT id
	 * @param searchkey - Search criteria on column name
	 * @param searchvalue - value to be search on column name
	 * @param returnkey - column name whose value to return
	 * @return
	 */
	public static List  getONTList(Long ontId)
	{
		List ontList =null;
		if(ontMap!=null)
		{
			ontList = (List )ontMap.get(ontId);
		}
		return ontList;
	}
	/**
	 * Returns the List of dataource ONLY foa a given ONT id
	 * @param searchkey - Search criteria on column name
	 * @param searchvalue - value to be search on column name
	 * @param returnkey - column name whose value to return
	 * @return
	 */
	public static List  getDataSourceListFromONT(Long ontId)
	{
		List dsListfromONT =new ArrayList();
		if(ontMap!=null)
		{
			List ontList = (List )ontMap.get(ontId);
			for(int i=0;i<ontList.size();i++)
			{
				dsListfromONT.add(ontList.get(i));
				i++;
			}
		}
		return dsListfromONT;
	}

}