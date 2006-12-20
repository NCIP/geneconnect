/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.action.HomeAction</p> 
 */

package edu.wustl.geneconnect.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;

/**
 * Action class for Home page. This class will prepare required data for Home page 
 * @author krunal_thakkar
 * @version 1.0
 */
public class HomeAction extends Action
{

	/**
	 * Defalut Constructor
	 */
	public HomeAction()
	{
		super();
	}

	//TODO : Logging needs to be added.

	/**
	 * Execute method which will be invoked by struts framework.
	 * @param mapping -
	 *            This is the action mapping object
	 * @param form -
	 *            Contains the form data
	 * @param request -
	 *            Request Object
	 * @param response -
	 *            Response Object
	 * @return ActionForward - Returns the the next page to forward to
	 * @throws Exception -
	 *             Throws any action related exceptions
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		request.setAttribute(GCConstants.NO_OF_PARIWISE_LINKS, MetadataManager.getPairwiseLinks().toString());
		
		request.setAttribute(GCConstants.NO_OF_GI_SETS, MetadataManager.getDistinctGISets().toString());
		
		request.setAttribute(GCConstants.NO_OF_GRAPH_PATHS, MetadataManager.getPossibleGraphPaths().toString());
		
		return mapping.findForward(GCConstants.FORWARD_TO_HOME_PAGE);
	}

}