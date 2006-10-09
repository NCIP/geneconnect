/*
 * Created on Jan 20, 2006
 *
 * Listener for cleanup after session invalidates.
 * 
 */

package edu.wustl.geneconnect.util.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.dao.DAOFactory;
import edu.wustl.common.dao.JDBCDAO;
import edu.wustl.common.util.dbManager.DAOException;
import edu.wustl.common.util.logger.Logger;

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