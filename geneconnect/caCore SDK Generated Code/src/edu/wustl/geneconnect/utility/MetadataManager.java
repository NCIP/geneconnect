/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.MetadataManager</p> 
 */

package edu.wustl.geneconnect.utility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class will implement all business logic to provide geneconnect metadata.
 * @author mahesh_nalkande
 */
public class MetadataManager //implements MetadataManagerInterface
{

	// Store datasoruceid , dataSource name
	private static List dataSourcesToDisplay = null;

	// represents teh DATSOURCE meta data table
	private static List dataSources = null;

	// represents teh ROLE_LOOKUP meta data table
	private static List roleLookup = null;

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

	public static void connect(Connection conn) throws Exception
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

		for (int i = 0; i < roleLookup.size(); i++)
		{
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
			System.out.println("MAp : " + map);
			String str = (String) map.get(searchkey);
			String str1 = (String) map.get(searchkey1);
			System.out.println("str---str1" + str + "----" + str1);
			if (str.equalsIgnoreCase(searchvalue) && str1.equalsIgnoreCase(searchvalue1))
			{
				roleLookupAttribute = (String) map.get(returnkey);
				return roleLookupAttribute;
			}
		}
		return null;
	}

}