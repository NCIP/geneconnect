/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/*
 * Created on Jan 20, 2006
 *
 * Listener for cleanup after session invalidates.
 * 
 */

package edu.wustl.geneconnect.util.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import edu.wustl.common.beans.SessionDataBean;

/**
 * @author poornima_govindrao
 *
 * Listener for cleanup after session invalidates.
 */
public class GeneConnectSessionListener implements HttpSessionListener
{

	public void sessionCreated(HttpSessionEvent arg0)
	{

	}

	//Cleanup after session invalidates.
	public void sessionDestroyed(HttpSessionEvent arg0)
	{
		//		HttpSession session = arg0.getSession();
		//		
		//		SessionDataBean sessionData= (SessionDataBean)session.getAttribute(Constants.SESSION_DATA);
		//		if(sessionData!=null)
		//			cleanUp(sessionData);
	}

	private void cleanUp(SessionDataBean sessionData)
	{
		//Delete Advance Query table if exists
		//Advance Query table name with userID attached
		//		String tempTableName = Constants.QUERY_RESULTS_TABLE+"_"+sessionData.getUserId();
		//		try
		//		{
		//			JDBCDAO jdbcDao = (JDBCDAO)DAOFactory.getInstance().getDAO(Constants.JDBC_DAO);;
		//			jdbcDao.openSession(sessionData);
		//			jdbcDao.delete(tempTableName);
		//			jdbcDao.closeSession();
		//		}
		//		catch(DAOException ex)
		//		{
		//			Logger.out.error("Could not delete the Advance Search temporary table."+ex.getMessage(),ex);
		//		}
	}
}
