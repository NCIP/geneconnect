
package edu.wustl.geneconnect.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.logger.Logger;
/**
 * Singlton class fro JDBC conenction and executing SQL query 
 * @author sachin_lale
 *
 */
public class JDBCDAO

{

	private static JDBCDAO jdbcDaoObject;
	private static Connection connection;
	private Connection jdbcConnection;

	private JDBCDAO()

	{

	}
/**
 * return the JDBCDAO instance
 * @return
 * @throws DAOException
 */
	public static JDBCDAO getInstance() throws DAOException
	{
		if (jdbcDaoObject == null)
		{
			Logger.out.info("New instance of JDBCDAO created");
			jdbcDaoObject = new JDBCDAO();
			if (!jdbcDaoObject.isConnected())
			{
				jdbcDaoObject.connect();
				Logger.out.info("JDBCDAO connected to database");
			}
		}
		return jdbcDaoObject;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isConnected()
	{
		return (connection != null);
	}
/**
 * opens jdbc connection to given database
 * @throws DAOException
 */
	private void connect() throws DAOException
	{
		String driverName = ApplicationProperties.getValue("db.driver");
		String host = ApplicationProperties.getValue("db.host");
		String port = ApplicationProperties.getValue("db.port");
		String schema = ApplicationProperties.getValue("db.schema");
		String userName = ApplicationProperties.getValue("db.user");
		String passWord = ApplicationProperties.getValue("db.password");
		String dbURL = "jdbc:oracle:thin:@" + host + ":" + port + ":" + schema;
		try
		{
			/** load the driver, which also registers the driver*/
			Class.forName(driverName);
			Logger.out.info("connecting to " + dbURL);

			connection = DriverManager.getConnection(dbURL, userName, passWord);

			Logger.out.info("connection successful");
			connection.setAutoCommit(false);
		}
		catch (ClassNotFoundException e)
		{
			throw new DAOException("Driver " + driverName + " not found");
		}
		catch (SQLException sqlEx)
		{
			/** Unable to establish a connection through the driver manager.*/
			throw new DAOException(sqlEx.getMessage());
		}

	}

	/**
	 * Method to execute given query and return query result. 
	 * @param query Query String to execute
	 * @return The query result set
	 */
	public ResultSet executeSQLQuery(String query) throws DAOException
	{
		ResultSet resultSet = null;

		/** Execute the query*/
		long startTime = System.currentTimeMillis();
		try
		{
			Logger.out.info("Executing query : " + query);
			Statement stmt = connection.createStatement();
			resultSet = stmt.executeQuery(query);
		}
		catch (SQLException sqlEx)
		{
			/** If query execution throws SQL Exception then null will be returned*/
			Logger.out.error(sqlEx.getMessage(), sqlEx);
			throw new DAOException(sqlEx.getMessage());
		}
		long endTime = System.currentTimeMillis();
		long queryTime = endTime - startTime;

		return resultSet;
	}

}
