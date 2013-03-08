/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.ResultDataInterface</p> 
 */

package edu.wustl.geneconnect.util.listener;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.global.ApplicationProperties;
import edu.wustl.common.util.global.Variables;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.cacore.CaCoreClient;
import edu.wustl.geneconnect.metadata.MetadataManager;

/**
 * 
 * @author sachin_lale
 * 
 * */
public class GeneConnectServletContextListener implements ServletContextListener
{

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent sce)
	{
		/**
		 * Getting Application Properties file path
		 */

		String applicationResourcesPath = sce.getServletContext().getRealPath("WEB-INF")
				+ System.getProperty("file.separator") + "classes"
				+ System.getProperty("file.separator")
				+ sce.getServletContext().getInitParameter("applicationproperties");

		/**
		 * Initializing ApplicationProperties with the class 
		 * corresponding to resource bundle of the application
		 */
		ApplicationProperties.initBundle(sce.getServletContext().getInitParameter(
				"resourcebundleclass"));

		/**
		 * Getting and storing Home path for the application
		 */
		Variables.applicationHome = sce.getServletContext().getRealPath("");

		System.setProperty("geneconnect.home", Variables.applicationHome);

		/**
		 * Creating Logs Folder inside catissue home
		 */
		try
		{
			File logfolder = new File(Variables.applicationHome + "/Logs");
			if (!logfolder.exists())
			{
				logfolder.mkdir();
			}
		}
		catch (Exception ex)
		{
			Logger.out.error(ex.getMessage(), ex);
		}

		/**
		 * Configuring the Logger class so that it can be utilized by
		 * the entire application
		 */
		Logger.configure("geneconnect.logger");

		Variables.applicationName = ApplicationProperties.getValue("app.name");
		Variables.applicationVersion = ApplicationProperties.getValue("app.version");

		Logger.out.info("========================================================");
		Logger.out.info("Application Information");
		Logger.out.info("Name: " + Variables.applicationName);
		Logger.out.info("Version: " + Variables.applicationVersion);
		//        Logger.out.info("CVS TAG: "+cvsTag);
		Logger.out.info("Path: " + Variables.applicationHome);
		Logger.out.info("Database Name: " + Variables.databaseName);
		Logger.out.info("========================================================");

		try
		{
			Logger.out.info("Populating Metadata");
			MetadataManager.populateMetadata();
			Logger.out.info("Connecting to caCOre");
			CaCoreClient.init();
		}
		catch (DAOException daoExp)
		{
			Logger.out.error(daoExp.getMessage(), daoExp);
			daoExp.printStackTrace();
		}

		//        QueryBizLogic.initializeQueryData();

		//        JDBCDAO dao =null;
		//        try
		//        {
		//            dao = (JDBCDAO)DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);;
		//            dao.openSession(null);
		//            
		//            Logger.out.debug("Able to open Session using DAO in ContextListener");
		//        }
		//        catch (DAOException daoExp)
		//        {
		//            Logger.out.debug("Could not obtain table object relation. Exception:"
		//                    + daoExp.getMessage(), daoExp);
		//        }
		//        finally
		//        {
		//            try
		//            {
		//                dao.closeSession();
		//            }
		//            catch (DAOException e)
		//            {
		//                Logger.out.debug(e.getMessage(),e);
		//            }
		//        }

		// get database name and set variables used in query
		//        Variables.databaseName=HibernateMetaData.getDataBaseName();
		//        
		//        String fileName = Variables.applicationHome + System.getProperty("file.separator")+ ApplicationProperties.getValue("application.version.file");
		////        CVSTagReader cvsTagReader = new CVSTagReader();
		////        String cvsTag = cvsTagReader.readTag(fileName);
		//        

		//        try
		//        {
		//            if(Variables.databaseName.equals(Constants.ORACLE_DATABASE))
		//            {
		//            	//set string/function for oracle
		//            	Variables.datePattern = "mm-dd-yyyy";
		//            	Variables.timePattern = "hh-mi-ss";
		//            	Variables.dateFormatFunction="TO_CHAR";
		//            	Variables.timeFormatFunction="TO_CHAR";
		//            	Variables.dateTostrFunction = "TO_CHAR";
		//            	Variables.strTodateFunction = "TO_DATE";
		//            }
		//            else
		//            {
		//            	Variables.datePattern = "%m-%d-%Y";
		//            	Variables.timePattern = "%H:%i:%s";
		//            	Variables.dateFormatFunction="DATE_FORMAT";
		//            	Variables.timeFormatFunction="TIME_FORMAT";
		//            	Variables.dateTostrFunction = "TO_CHAR";
		//            	Variables.strTodateFunction = "STR_TO_DATE";
		//            }
		//        }
		//        catch (Exception ex)
		//        {
		//        	Logger.out.error(ex.getMessage(), ex);
		//        }  

		//Initialize XML properties Manager
		//        try
		//		{
		//        	String path = System.getProperty("app.propertiesFile");
		//        	XMLPropertyHandler.init(path);
		//        	
		//        	File propetiesDirPath = new File(path);
		//        	Variables.propertiesDirPath = propetiesDirPath.getParent();
		//        	Logger.out.debug("propetiesDirPath "+Variables.propertiesDirPath);
		//        	
		//        	String propertyValue = XMLPropertyHandler.getValue("server.port");
		//            Logger.out.debug("property Value "+propertyValue);
		//		}
		//        catch(Exception ex)
		//		{
		//        	Logger.out.error("Could not initialized application, Error in creating XML Property handler.");
		//        	Logger.out.error(ex.getMessage(), ex);
		//		}
		//        
		//        Logger.out.debug("System property : "+System.getProperty("gov.nih.nci.security.configFile"));
		//        Logger.out.debug("System property : "+System.getProperty("edu.wustl.catissuecore.contactUsFile"));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent sce)
	{
	}
}