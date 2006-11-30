/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.action.GeneConnectGraphAction</p> 
 */

package edu.wustl.geneconnect.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.cacore.CaCoreClient;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;
import edu.wustl.geneconnect.util.global.Utility;


/**
 * Action class for GeneConnect Graph. This class will prepare data required to draw GeneConnect Graph.
 * @author krunal_thakkar
 * @version 1.0
 */
public class GeneConnectGraphAction extends Action
{
//	Map ontMap = null;
	
	
	
	/**
	 * Defalut Constructor
	 */
	public GeneConnectGraphAction()
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
		HttpSession session = request.getSession();
		List selectedInputOutputList = (List)session.getAttribute(GCConstants.SELECTED_DATASOURCES);
		
		List dataSources = MetadataManager.getDataSourcesToDisplay();
		
		List graphDataSources = new ArrayList();
		
		List dataSourcesLinks = MetadataManager.getDataSourcesLinks();
		
		List graphDataSourcesLinks = new ArrayList();
		
		String graphDataSourceString;
		
		Logger.out.info("Setting data source list in request object");
		request.setAttribute(GCConstants.DATA_SOURCES_KEY, dataSources);
		
		for(int i=0; i<dataSources.size(); i++)
		{
			NameValueBean dataSource = (NameValueBean)dataSources.get(i);
			
			String rowForGraph = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME, dataSource.getName(),GCConstants.ROW_FOR_GRAPH);
			
			String colForGraph = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME, dataSource.getName(),GCConstants.COL_FOR_GRAPH);
			
			graphDataSourceString = dataSource.getValue()+ ","+dataSource.getName()+","+rowForGraph+","+colForGraph;
			
			graphDataSources.add(graphDataSourceString);
			
			Logger.out.info("GraphDataSource==>"+graphDataSourceString);
			
		}
		
		Logger.out.info("Setting data source attributes list in request object");
		request.setAttribute(GCConstants.GRAPH_DATASOURCES, graphDataSources);

		String graphDataSourceLinkString;
		for(int i=0; i<dataSourcesLinks.size(); i++)
		{
			Map dataSourceLink = (HashMap)dataSourcesLinks.get(i);
			
			graphDataSourceLinkString = (String)dataSourceLink.get(GCConstants.SOURCE_DATASOURCE_ID)+","+(String)dataSourceLink.get(GCConstants.TARGET_DATASOURCE_ID)+","+(String)dataSourceLink.get(GCConstants.LINK_TYPE_ID);
			
			graphDataSourcesLinks.add(graphDataSourceLinkString);
			
			Logger.out.info("GraphDataSourceLink==>"+graphDataSourceLinkString);
			
		}
		
		Logger.out.info("Setting data source links attributes list in request object");
		request.setAttribute(GCConstants.GRAPH_DATASOURCES_LINKS, graphDataSourcesLinks);
		
		HashMap ontMap = new HashMap();
		
		int ontMapCounter=0;
		
		Logger.out.info("SetId parameter==>"+request.getParameter("setid"));
		String setIds = request.getParameter("setid");
		
		if(setIds != null)
		{
			StringTokenizer setIdsTokenizer = new StringTokenizer(setIds, ",");
			
			Logger.out.info("No. of Set Tokens->"+setIdsTokenizer.countTokens());
			System.out.println("No. of Set Tokens->"+setIdsTokenizer.countTokens());
			if(setIdsTokenizer.countTokens() > 0)
			{
				while(setIdsTokenizer.hasMoreTokens())
				{
					GenomicIdentifierSet gset = new GenomicIdentifierSet();
					Long setID = new Long(setIdsTokenizer.nextToken());
					List resultList = (List)session.getAttribute(GCConstants.GENOMICIDENTIIER_SET_RESULT_LIST);
					
				//	gset.setId(new Long(setIdsTokenizer.nextToken()));
					
	//				query(gset, ontMap);
					//List resultList = CaCoreClient.appServiceQuery(GenomicIdentifierSet.class.getName(),gset);
					
					//	if(resultList.size()>0)
					System.out.println("curretnt serach for :" +setID);
					GenomicIdentifierSet  set =null;
					boolean setFound=false;
					for(int i=0;i<resultList.size();i++)
						{
					
							set = (GenomicIdentifierSet )resultList.get(i);
							if(set.getId().longValue()==setID.longValue())
							{
								setFound=true;
								break;
								
							}
						}
					if(setFound)
					{	
							Collection coll = set.getOrderOfNodeTraversalCollection();
					
							Logger.out.info("asa :"+set.getId()+"-----"+ coll.size());
							
							/*Get and Print the Order of Node Traveersal associated with this GenomicIdentifierSet*/
							Logger.out.info("________________________________________________________");
	//						StringBuffer firstDataSource = new StringBuffer();
	//						StringBuffer lastDataSource = new StringBuffer();
							
							for (Iterator iter1 = coll.iterator(); iter1.hasNext();)
							{
	//							firstDataSource.setLength(0);
	//							lastDataSource.setLength(0);
								
								Logger.out.info("ONT Id----DataSource-------LinkType");
					
								OrderOfNodeTraversal ont = (OrderOfNodeTraversal) iter1.next();
								
	//							firstDataSource.append(ont.getSourceDataSource().getName());	
								
								OrderOfNodeTraversal tempont = ont;
								
								String highlightNodes ="";
								String highlightLinkTypes="";
								List dsList = new ArrayList();
								while (tempont != null)
								{
									LinkType ltype = tempont.getLinkType();
					                                                             
									String linkType = null;
									
									Long linkId = null;
									
									if (ltype != null)
									{
										linkType = ltype.getType();
										
										linkId = ltype.getId();
										
										if(linkId != null)
											highlightLinkTypes+=linkId+",";
									}
	//								dsList.add(tempont.getSourceDataSource().getName());
	//								highlightNodes +=tempont.getSourceDataSource().getId().toString()+"("+tempont.getSourceDataSource().getName()+")>";
									highlightNodes +=tempont.getSourceDataSource().getId().toString()+">";
									
									Logger.out.info(tempont.getId() + "----" + tempont.getSourceDataSource().getId() + "------" + linkId);
					
									OrderOfNodeTraversal nextont = tempont.getChildOrderOfNodeTraversal();
									
	//								if(nextont==null)
	//								{
	//									lastDataSource.append(tempont.getSourceDataSource().getName());
	//								}
									
									tempont = nextont;
								}
								
								Logger.out.info("________________________________________________________");
								ontMapCounter+=1;
								
								ontMap.put("highlightNodeList_"+ontMapCounter, highlightNodes);
								ontMap.put("highlightLinkTypes_"+ontMapCounter, highlightLinkTypes);
								
								Logger.out.info("NodeList==> "+highlightNodes+"  LinkTypeIds==>"+highlightLinkTypes);
	//							if((Utility.listContainValue(firstDataSource.toString(),selectedInputOutputList)
	//									&&Utility.listContainValue(lastDataSource.toString(),selectedInputOutputList)))
	//							{
	//								if(dsList.containsAll(selectedInputOutputList))
	//								{
	//									ontMapCounter+=1;
	//									
	//									ontMap.put("highlightNodeList_"+ontMapCounter, highlightNodes);
	//									ontMap.put("highlightLinkTypes_"+ontMapCounter, highlightLinkTypes);
	//									
	//									Logger.out.info("NodeList==> "+highlightNodes+"  LinkTypeIds==>"+highlightLinkTypes);
	//								}	
	//							}
								
							}
					}		
						
					System.out.println("Returning ontMap of size==>"+ontMap.size());
				}
			}
		}
		else
		{
			String selectedPathsForGraph = request.getParameter("selectedPathsForGraph");
			//System.out.println("SelectedPathsForGraph-->"+selectedPathsForGraph);
			
			StringTokenizer selectedPathsTokenized = new StringTokenizer(selectedPathsForGraph, "$");
			
			
			while(selectedPathsTokenized.hasMoreTokens())
			{
				String highlightNodes ="";
				String highlightLinkTypes="";
				
				String selectedPath = selectedPathsTokenized.nextToken();
				
				StringTokenizer selectedPathTokenized = new StringTokenizer(selectedPath, "_");
				
				while(selectedPathTokenized.hasMoreTokens())
				{
					highlightNodes += selectedPathTokenized.nextToken()+">";
					
					if(selectedPathTokenized.hasMoreTokens())
						highlightLinkTypes += selectedPathTokenized.nextToken()+",";
				}
				
				ontMapCounter+=1;
				
				ontMap.put("highlightNodeList_"+ontMapCounter, highlightNodes);
				ontMap.put("highlightLinkTypes_"+ontMapCounter, highlightLinkTypes);
				
				Logger.out.info("NodeList==> "+highlightNodes+"  LinkTypeIds==>"+highlightLinkTypes);
			}
			
		}
		
		request.setAttribute(GCConstants.GRAPH_HIGHLIGHT_PATHS, ontMap);
		
		Logger.out.info("Forwarding to GeneConnectGraph.jsp");
		return (mapping.findForward(GCConstants.FORWARD_TO_GRAPH_PAGE));
	}
	
	private void query(GenomicIdentifierSet gset, HashMap ontMap) throws Exception
	{
//		Map ontMap = new HashMap();
		
//		GenomicIdentifierSet gset = new GenomicIdentifierSet();
//	
//		gset.setId(new Long(1));
		int ontMapCounter=0;
	
		List resultList = CaCoreClient.appServiceQuery(GenomicIdentifierSet.class.getName(),gset);
	
		if(resultList.size()>0)
		{
	
			GenomicIdentifierSet  set = (GenomicIdentifierSet )resultList.get(0);
	
			Collection coll = set.getOrderOfNodeTraversalCollection();
	
			Logger.out.info("asa :" + coll.size());
	
			/*Get and Print the Order of Node Traveersal associated with this GenomicIdentifierSet*/
			Logger.out.info("________________________________________________________");
	
			
			
			for (Iterator iter1 = coll.iterator(); iter1.hasNext();)
			{
	
				Logger.out.info("ONT Id----DataSource-------LinkType");
	
				OrderOfNodeTraversal ont = (OrderOfNodeTraversal) iter1.next();
	
				OrderOfNodeTraversal tempont = ont;
				
				String highlightNodes ="";
				String highlightLinkTypes="";
				
				while (tempont != null)
				{
	
					LinkType ltype = tempont.getLinkType();
	                                                             
					String linkType = null;
					
					Long linkId = null;
					
	
					if (ltype != null)
					{
						linkType = ltype.getType();
						
						linkId = ltype.getId();
						
						if(linkId != null)
							highlightLinkTypes+=linkId+",";
					}
					
//					highlightNodes +=tempont.getSourceDataSource().getId().toString()+"("+tempont.getSourceDataSource().getName()+")>";
					highlightNodes +=tempont.getSourceDataSource().getId().toString()+">";
					
					
					Logger.out.info(tempont.getId() + "----" + tempont.getSourceDataSource().getId() + "------" + linkId);
	
					OrderOfNodeTraversal nextont = tempont.getChildOrderOfNodeTraversal();
	
					tempont = nextont;
	
				}
				
				Logger.out.info("________________________________________________________");
				
				ontMapCounter+=1;
				
				ontMap.put("highlightNodeList_"+ontMapCounter, highlightNodes);
				ontMap.put("highlightLinkTypes_"+ontMapCounter, highlightLinkTypes);
				
				Logger.out.info("NodeList==> "+highlightNodes+"  LinkTypeIds==>"+highlightLinkTypes);
			}
			
//			request.setAttribute(GCConstants.GRAPH_HIGHLIGHT_PATHS, ontMap);
		}
		System.out.println("Returning ontMap of size==>"+ontMap.size());
	}
}
