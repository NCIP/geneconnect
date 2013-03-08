/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.metadata.MetadataWriter</p> 
 */

package edu.wustl.geneconnect.metadata;

import java.util.Iterator;
import java.util.List;

import com.dataminer.server.database.DBManager;
import com.dataminer.server.database.DataLoadManager;
import com.dataminer.server.exception.FatalException;
import com.dataminer.server.jobmanager.BaseBuilder;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;

/**
 * This class writes all o/p data files into the database.
 * 
 * @author mahesh_nalkande
 * @version 1.0
 */
public class MetadataWriter
{

	/** MetadataWriter as a singleton class */
	private static MetadataWriter metadataWriter = new MetadataWriter();

	/** 
	 * Method to return instance of this class
	 * @return MetadataWriter Returns object of this class
	 */
	public static MetadataWriter getInstance()
	{
		return metadataWriter;
	}

	/**
	 * Upload files into the database.
	 * @param fileNames List of filenames to upload into the database. 
	 * @param preUploadQueries SQL queries which needs to be executed before uploading files. 
	 * Maily required to drop referance constraints on the tables, while uploading the data.
	 * @param postUploadQueries SQL queries which needs to be executed after uploading files.
	 * Mainly required to Restore table reference constraints 
	 */
	public void uploadDataFilesIntoDataBase(List fileNames, List preUploadQueries,
			List postUploadQueries)
	{
		String sqlQuery = "";

		DBManager dbManager = DBManager.getInstance();

		/** set up Data Base connection object*/
		try
		{
			dbManager.connect();
		}
		catch (FatalException fatal)
		{
			/** If database connection can not be established successfully it will throw 
			 * FatalException which will cause the program to terminate  */
			Logger.log("Fatal Exception occured while connecting to database", Logger.FATAL);
			Logger.log("Reason : " + fatal.getMessage(), Logger.FATAL);
			fatal.printStackTrace();
			fatal.printException();
			BaseBuilder.getInstance("edu.wustl.geneconnect.builder.GCBuilder")
					.handleFatalException();
		}

		Logger.log("Executing preUpLoad queries...", Logger.DEBUG);
		/** Execute preUpload quries **/
		Iterator iterator = preUploadQueries.iterator();
		while (iterator.hasNext())
		{
			sqlQuery = (String) iterator.next();
			dbManager.executeSQLQuery(sqlQuery);
		}
		dbManager.commit();
		Logger.log("PreUpLoad queries have been executed successfully.", Logger.DEBUG);
		
		Logger.log("Uploading data files...", Logger.DEBUG);
		//DataLoadManager to upload the metadata
		DataLoadManager dataLoadmanager = new DataLoadManager(1);
		DPQueue filesToUpload = new DPQueue(10);

		dataLoadmanager.start(filesToUpload);

		//Add List of filenames to the queue
		iterator = fileNames.iterator();
		while (iterator.hasNext())
		{
			filesToUpload.add(iterator.next());
		}
		filesToUpload.add(null);

		try
		{
			/**
			 *  Wait for data loader threads to get over
			 */
			dataLoadmanager.join();
		}
		catch (InterruptedException e)
		{
			Logger.log("Error in Execution of Threads" + e.getMessage(), Logger.WARNING);
			System.out.println("Exception in DownloadParse And LoadData:" + e.getMessage());
			System.exit(1);
		}
		Logger.log("Data files have been uploaded successfully.", Logger.DEBUG);

		
		/** Execute postUpload quries **/
		iterator = postUploadQueries.iterator();
		while (iterator.hasNext())
		{
			sqlQuery = (String) iterator.next();
			dbManager.executeSQLQuery(sqlQuery);
		}
		Logger.log("PostUpload queries have been executed successfully.", Logger.DEBUG);
		
		try
		{
			dbManager.disconnect();
		}
		catch (FatalException fatal)
		{
			Logger.log("Fatal Exception occured while disconnecting from the database",
					Logger.FATAL);
			Logger.log("Reason : " + fatal.getMessage(), Logger.FATAL);
			fatal.printStackTrace();
			fatal.printException();
			BaseBuilder.getInstance("edu.wustl.geneconnect.builder.GCBuilder")
					.handleFatalException();
		}
	}
}