/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.actionForm.AdvancedSearchForm</p> 
 */

package edu.wustl.geneconnect.actionForm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;

/**
 * Advanced Search Form will hold the data required for advanced search operation
 * @author krunal_thakkar
 * @version 1.0
 */
public class AdvancedSearchForm extends ActionForm
{
	/**
	 * Indicates next action to be taken
	 */
	private String targetAction = "populate";
	
	private Map inputDataSources = new HashMap();
	
	private Map outputDataSources = new HashMap();
	
	private String confidenceScore ="";
	
	private String startsWithDataSources;
	
	private String endsWithDataSources;
	
	private String pathTypes;
	
	private String selectedPaths;
	
	public String getConfidenceScore()
	{
		return confidenceScore;
	}
	
	public void setConfidenceScore(String confidenceScore)
	{
		this.confidenceScore = confidenceScore;
	}
	/**
	 * @return Returns the targetAction.
	 */
	public String getTargetAction()
	{
		return targetAction;
	}
	/**
	 * @param targetAction The targetAction to set.
	 */
	public void setTargetAction(String targetAction)
	{
		this.targetAction = targetAction;
	}
	
	/**
	 * @return Returns the InputDataSources.
	 */
	public Map getInputDataSources()
	{
		return inputDataSources;
	}
	/**
	 * @param builds The InputDataSources to set.
	 */
	public void setInputDataSources(Map inputDataSources)
	{
		this.inputDataSources = inputDataSources;
	}
	
	/**
     * Associates the specified object with the specified key in the map.
     * @param key the key to which the object is mapped.
     * @param value the object which is mapped.
     */
    public void setInputDataSourcesValue(String key, Object value)
    {
    	System.out.println("Setting "+key+" in FormBean setter method...");
   		inputDataSources.put(key, value);
    }

    /**
     * Returns the object to which this map maps the specified key.
     * @param key the required key.
     * @return the object to which this map maps the specified key.
     */
    public Object getInputDataSourcesValue(String key)
    {
        return inputDataSources.get(key);
    }

    /**
     * Returns all the values in the map.
     * @return Collection all the values in the map.
     */
    public Collection getAllInputDataSources()
    {
        return inputDataSources.values();
    }
    
    /**
	 * @return Returns the InputDataSources.
	 */
	public Map getOutputDataSources()
	{
		return outputDataSources;
	}
	/**
	 * @param builds The InputDataSources to set.
	 */
	public void setOutputDataSources(Map outputDataSources)
	{
		this.outputDataSources = outputDataSources;
	}
	
	/**
     * Associates the specified object with the specified key in the map.
     * @param key the key to which the object is mapped.
     * @param value the object which is mapped.
     */
    public void setOutputDataSourcesValue(String key, Object value)
    {
    	outputDataSources.put(key, value);
    }

    /**
     * Returns the object to which this map maps the specified key.
     * @param key the required key.
     * @return the object to which this map maps the specified key.
     */
    public Object getOutputDataSourcesValue(String key)
    {
        return outputDataSources.get(key);
    }

    /**
     * Returns all the values in the map.
     * @return Collection all the values in the map.
     */
    public Collection getAllOutputDataSources()
    {
        return outputDataSources.values();
    }
    
	/**
	 * @return Returns the endsWithDataSources.
	 */
	public String getEndsWithDataSources()
	{
		return endsWithDataSources;
	}
	/**
	 * @param endsWithDataSources The endsWithDataSources to set.
	 */
	public void setEndsWithDataSources(String endsWithDataSources)
	{
		this.endsWithDataSources = endsWithDataSources;
	}
	/**
	 * @return Returns the pathTypes.
	 */
	public String getPathTypes()
	{
		return pathTypes;
	}
	/**
	 * @param pathTypes The pathTypes to set.
	 */
	public void setPathTypes(String pathTypes)
	{
		this.pathTypes = pathTypes;
	}
	/**
	 * @return Returns the startsWithDataSources.
	 */
	public String getStartsWithDataSources()
	{
		return startsWithDataSources;
	}
	/**
	 * @param startsWithDataSources The startsWithDataSources to set.
	 */
	public void setStartsWithDataSources(String startsWithDataSources)
	{
		this.startsWithDataSources = startsWithDataSources;
	}
	
	/**
	 * @return Returns the selectedPaths.
	 */
	public String getSelectedPaths()
	{
		return selectedPaths;
	}
	/**
	 * @param selectedPaths The selectedPaths to set.
	 */
	public void setSelectedPaths(String selectedPaths)
	{
		this.selectedPaths = selectedPaths;
	}
	
    /**
     * Overrides the validate method of ActionForm.
     * */
     public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) 
     {
     	System.out.println("***********  Form Validate Method  **********");
     	
         ActionErrors errors = new ActionErrors();
         
 		//Creating map of InputDataSources
 		Map sourcesMap = inputDataSources;
 		
 		Collection keySet = sourcesMap.keySet();
 		
 		List sortedKeys = new ArrayList(keySet);

 		Map inputDataSources = new HashMap();
 		
 		//Iterating through the list of InputDataSources submitted by User to generate set of GenomicIdentifierSet 
 		for (int i = 0; i < sortedKeys.size(); i++)
 		{
 			
 			//Checking whether user has submitted value for InputDataSource
 			if (sourcesMap.get((String) sortedKeys.get(i)) != null
 					& !(sourcesMap.get((String) sortedKeys.get(i)).equals("")))
 			{
 				StringTokenizer dataSourceToken = new StringTokenizer((String) sortedKeys.get(i), "_");

 				String dataSourceName = dataSourceToken.nextToken();
 				
 				List inputDataSourcesList = new ArrayList();
 				
 				if(inputDataSources.get(dataSourceName) != null)
 				{
 					inputDataSourcesList = (ArrayList) inputDataSources.get(dataSourceName);
 					
 					String key = dataSourceName;
 					
 					dataSourceName = dataSourceToken.nextToken();
 					
 					String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
 							dataSourceName, GCConstants.DATASOURCE_ID);
 					
 					System.out.println("Entering into InputDataSources->"+key+" "+dataSourceName);
 					
 					inputDataSourcesList.add(id);
 					
 					inputDataSources.put(key, inputDataSourcesList);
 				}
 				else
 				{
 					String key = dataSourceName;
 					
 					dataSourceName = dataSourceToken.nextToken();
 					
 					String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
 							dataSourceName, GCConstants.DATASOURCE_ID);
 					
 					System.out.println("Entering into InputDataSources first time->"+key+" "+dataSourceName);
 					
 					inputDataSourcesList.add(id);
 					
 					inputDataSources.put(key, inputDataSourcesList);
 					
 				}
 				
// 				System.out.println("InputDataSource entered on AdvanceSearch page:- "+dataSourceName+" id->"+id);
 			}
 		}
 		Collection outputs = new ArrayList();
 		
 		try
		{
 			List dataSources = MetadataManager.getDataSourcesToDisplay();
		
 			
 			
	 		for (int i = 0; i < dataSources.size(); i++)
	 		{
 				NameValueBean bean = (NameValueBean) dataSources.get(i);
 				if ((request.getParameter(bean.getName()) != null))
 				{
 					Logger.out.info("Frequency Value==>"
 							+ request.getParameter(bean.getName() + "_FrequenceValue"));
 	
 					System.out.println("Entering into OutputDataSources->"+bean.getValue()+" "+bean.getName());
 					
 					outputs.add(bean.getValue());
 				}
	 		}
		}
 		catch(Exception e)
		{
 			
		}
 		
 		Collection inputKeySet = inputDataSources.keySet();
 		
 		List inputKeys = new ArrayList(inputKeySet);
 		
 		System.out.println("No. of Output sources entered by User-->"+outputs.size());
 		
 		System.out.println("No. of Input Rows entered by User-->"+inputKeys.size());
 		
 		for(int i=0; i<inputKeys.size(); i++)
 		{
 			List inputSourcesList = (List) inputDataSources.get(inputKeys.get(i));
 			
 			System.out.println("No. of sources for "+inputKeys.get(i)+" "+inputSourcesList.size());
 			
 			if( (inputSourcesList.containsAll(outputs)) & (inputSourcesList.size() == outputs.size()))
 			{
 				System.out.println("Input and Output DataSources are same for "+ (String)inputKeys.get(i));
 				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.advanceSeach.sameDataSources",(String)inputKeys.get(i)));
 			}
 		}
 		
 		if(errors.size()>0)
 			targetAction = "failure";
// 		selectedDataSources.put(GCConstants.INPUT_DATA_SOURCES, inputDataSources);
// 		selectedDataSources.put(GCConstants.OUTPUT_DATA_SOURCES, outputDataSources);
         
         return errors;
      }
     
     public void reset(ActionMapping mapping, HttpServletRequest request)
     {
     	HttpSession session = request.getSession();
     	
     	System.out.println("Reset Method-->"+targetAction);
     	
//     	System.out.println("Paremeter-->"+request.getParameter("targetAction"));
     	
     	String targetActionParameter = request.getParameter("targetAction");
     	
     	if(targetActionParameter != null && targetActionParameter.equals("updateMap"))
     	{
     		
     	}
		else if(targetAction.equals("populate") || (targetActionParameter != null && targetActionParameter.equals("populate") ))
		{
     		System.out.println("Resetting Form values...");
     		inputDataSources = new HashMap();
     		
     		outputDataSources = new HashMap();
     		
     		confidenceScore ="";
     		
			session.setAttribute("advancedSearchForm", new AdvancedSearchForm());
		}
     }
	
}
