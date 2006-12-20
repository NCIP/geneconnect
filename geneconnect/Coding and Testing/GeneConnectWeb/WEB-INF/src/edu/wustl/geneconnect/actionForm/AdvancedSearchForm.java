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
	
	/**
	 * map to store InputDatasources
	 */
	private Map inputDataSources = new HashMap();
	
	/**
	 * map to store OutputDatasources
	 */
	private Map outputDataSources = new HashMap();
	
	/**
	 * attribute that contains ConfidenceScore
	 */
	private String confidenceScore ="";
	
	/**
	 * attribute that contains StartWith Datasource filter option
	 */
	private String startsWithDataSources = "-1";
	
	/**
	 * attribute that contains EndsWith Datasource filter option
	 */
	private String endsWithDataSources = "-1";
	
	/**
	 * attribute that contains PathType filter option
	 */
	private String pathTypes = "-1";
	
	/**
	 * attribute that contains Path filter option
	 */
	private String ontFilterCode ="5";
	
	/**
	 * attribute that contains SelectedPaths (ONTs) by user
	 */
	private String selectedPaths;
	
	/**
	 * attribute indicating whether AdvnaceSearch page is called by 
	 * "Back To Query" selection of AdvanceSearchSelectPath page
	 */
	private boolean backFromSelectPath = false;
	
	/**
	 * attribute that contains initial InputOutput Datasources entered by user
	 */
	private String initialInputOutput;
	
	
	/**
	 * Getter method for ConfidenceScore
	 * @return confidenceScore
	 */
	public String getConfidenceScore()
	{
		return confidenceScore;
	}
	
	/**
	 * Setter method for ConfidenceScore
	 * @param confidenceScore to set
	 */
	public void setConfidenceScore(String confidenceScore)
	{
		this.confidenceScore = confidenceScore;
	}
	
	/**
	 * Getter method for TargetACtion
	 * @return Returns the targetAction.
	 */
	public String getTargetAction()
	{
		return targetAction;
	}
	
	/**
	 * Setter method for TargetAction
	 * @param targetAction The targetAction to set.
	 */
	public void setTargetAction(String targetAction)
	{
		this.targetAction = targetAction;
	}
	
	
	/**
	 * Getter method for InputDatasources
	 * @return Returns the InputDataSources.
	 */
	public Map getInputDataSources()
	{
		return inputDataSources;
	}
	
	/**
	 * Setter method for InputDatasources
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
    	Logger.out.debug("Setting "+key+" in FormBean setter method...");
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
     * Getter method for OutputDatasources
	 * @return Returns the OutputDataSources.
	 */
	public Map getOutputDataSources()
	{
		return outputDataSources;
	}
	
	/**
	 * Setter method for OutputDatasources
	 * @param builds The OutputDataSources to set.
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
	 * @return Returns the ontFilterCode.
	 */
	public String getOntFilterCode()
	{
		return ontFilterCode;
	}
	
	/**
	 * @param ontFilterCode The ontFilterCode to set.
	 */
	public void setOntFilterCode(String ontFilterCode)
	{
		this.ontFilterCode = ontFilterCode;
	}
	
	/**
	 * @return Returns the backFromSelectPath.
	 */
	public boolean isBackFromSelectPath()
	{
		return backFromSelectPath;
	}
	
	/**
	 * @param backFromSelectPath The backFromSelectPath to set.
	 */
	public void setBackFromSelectPath(boolean backFromSelectPath)
	{
		this.backFromSelectPath = backFromSelectPath;
	}
	
	/**
	 * @return Returns the initialInputOutput.
	 */
	public String getInitialInputOutput()
	{
		return initialInputOutput;
	}
	
	/**
	 * @param initialInputOutput The initialInputOutput to set.
	 */
	public void setInitialInputOutput(String initialInputOutput)
	{
		this.initialInputOutput = initialInputOutput;
	}
    
	/**
	 * Overrides the validate method of ActionForm
	 */
     public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) 
     {
     	Logger.out.debug("***********  Form Validate Method  **********");
     	
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
 					
 					Logger.out.debug("Entering into InputDataSources->"+key+" "+dataSourceName);
 					
 					inputDataSourcesList.add(id);
 					
 					inputDataSources.put(key, inputDataSourcesList);
 				}
 				else
 				{
 					String key = dataSourceName;
 					
 					dataSourceName = dataSourceToken.nextToken();
 					
 					String id = MetadataManager.getDataSourceAttribute(GCConstants.DATASOURCE_NAME,
 							dataSourceName, GCConstants.DATASOURCE_ID);
 					
 					Logger.out.debug("Entering into InputDataSources first time->"+key+" "+dataSourceName);
 					
 					inputDataSourcesList.add(id);
 					
 					inputDataSources.put(key, inputDataSourcesList);
 					
 				}
 				
// 				Logger.out.debug("InputDataSource entered on AdvanceSearch page:- "+dataSourceName+" id->"+id);
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
 	
 					Logger.out.debug("Entering into OutputDataSources->"+bean.getValue()+" "+bean.getName());
 					
 					outputs.add(bean.getValue());
 				}
	 		}
		}
 		catch(Exception e)
		{
 			
		}
 		
 		Collection inputKeySet = inputDataSources.keySet();
 		
 		List inputKeys = new ArrayList(inputKeySet);
 		
 		Logger.out.debug("No. of Output sources entered by User-->"+outputs.size());
 		
 		Logger.out.debug("No. of Input Rows entered by User-->"+inputKeys.size());
 		
 		for(int i=0; i<inputKeys.size(); i++)
 		{
 			List inputSourcesList = (List) inputDataSources.get(inputKeys.get(i));
 			
 			Logger.out.debug("No. of sources for "+inputKeys.get(i)+" "+inputSourcesList.size());
 			
 			if( (inputSourcesList.containsAll(outputs)) & (inputSourcesList.size() == outputs.size()))
 			{
 				Logger.out.debug("Input and Output DataSources are same for "+ (String)inputKeys.get(i));
 				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.advanceSeach.sameDataSources",(String)inputKeys.get(i)));
 			}
 		}
 		
 		if(errors.size()>0)
 			targetAction = "failure";
// 		selectedDataSources.put(GCConstants.INPUT_DATA_SOURCES, inputDataSources);
// 		selectedDataSources.put(GCConstants.OUTPUT_DATA_SOURCES, outputDataSources);
         
         return errors;
      }
 
     /**
      *This method resets attribtues/members of formbean as per the targetaction associated
      */
     public void reset(ActionMapping mapping, HttpServletRequest request)
     {
     	HttpSession session = request.getSession();
     	
     	Logger.out.debug("Reset Method-->"+targetAction);
     	
     	Logger.out.debug("TargetAction Paremeter-->"+request.getParameter("targetAction"));
     	
     	String targetActionParameter = request.getParameter("targetAction");
     	
     	if(targetActionParameter != null && targetActionParameter.equals("updateMap"))
     	{
     		
     	}
     	//repopulating formbean object
		else if(targetAction.equals("populate") || (targetActionParameter != null && targetActionParameter.equals("populate") ))
		{
     		Logger.out.debug("Reseting Form values...");
     		inputDataSources = new HashMap();
     		
     		outputDataSources = new HashMap();
     		
     		confidenceScore ="";
     		
     		backFromSelectPath = false;
     		initialInputOutput="";
     		
     		//setting repopulated formbean object into Sesstion
			session.setAttribute("advancedSearchForm", new AdvancedSearchForm());
		}
     }
}