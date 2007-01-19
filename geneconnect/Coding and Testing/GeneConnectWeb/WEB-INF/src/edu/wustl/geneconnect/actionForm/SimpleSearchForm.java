/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.actionForm.SimpleSearchForm</p> 
 */

package edu.wustl.geneconnect.actionForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.exceptionformatter.DefaultExceptionFormatter;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.metadata.MetadataManager;
import edu.wustl.geneconnect.util.global.GCConstants;
import edu.wustl.geneconnect.util.global.Utility;

/**
 * Simple Search Action Form will hold the data required for simple search operation
 * @author mahesh_nalkande
 * @version 1.0
 */
public class SimpleSearchForm extends ActionForm
{

	/**
	 * Default constructor
	 */
	public SimpleSearchForm()
	{
	}

	/**
	 * List of Data sources
	 */
	private List datasources;
	
	/**
	 * Indicates next action to be taken
	 */
	private String targetAction = "populate";

	/**
	 * Counter that contains number of rows in the 'Add More' functionality.
	 */
	private int counter = 1;
	/**
	 * Map to handle values of all the Participant Medical Identifiers
	 */
	protected Map values = new HashMap();
	
	/**
	 * Strore the type of query by default batch search 
	 */
	private String queryType = GCConstants.BACTH_QUERY_VALUE;
	
	
	List outputDsList;

	/**
	 * Store the input data source and genomic id
	 * Input data list contains bean Name=DataSopurceName Value=GenomicID  
	 */
	List inputDsList;

	/**
	 * @param values
	 * The values to set.
	 */
	public void setValues(Map values)
	{
		this.values = values;
	}

	/**
	 * @param values
	 * The values to set.
	 */
	public Map getValues()
	{
		return this.values;
	}

	/**
	 * Associates the specified object with the specified key in the map.
	 * @param key the key to which the object is mapped.
	 * @param value the object which is mapped.
	 */
	public void setValue(String key, Object value)
	{

		values.put(key, value);
	}

	/**
	 * Returns the object to which this map maps the specified key.
	 * @param key the required key.
	 * @return the object to which this map maps the specified key.
	 */
	public Object getValue(String key)
	{
		return values.get(key);
	}

	/**
	 * Returns the counter.
	 * @return int the counter.
	 * @see #setCounter(int)
	 */
	public int getCounter()
	{
		return counter;
	}

	/**
	 * Sets the counter.
	 * @param counter The counter.
	 * @see #getCounter()
	 */
	public void setCounter(int counter)
	{
		this.counter = counter;
	}

	/**
	 * Getter method for datasources.
	 * @return Returns the datasources.
	 */
	public List getDatasources()
	{
		return datasources;
	}

	/**
	 * Setter method for datasources
	 * @param datasources The datasources to set.
	 */
	public void setDatasources(List datasources)
	{
		this.datasources = datasources;
	}

	/**
	 * Getter method for targetAction.
	 * @return Returns the targetAction.
	 */
	public String getTargetAction()
	{
		return targetAction;
	}

	/**
	 * Setter method for targetAction
	 * @param targetAction The targetAction to set.
	 */
	public void setTargetAction(String targetAction)
	{
		this.targetAction = targetAction;
	}
	  public void reset(ActionMapping mapping, HttpServletRequest request)
	  {
		  values = new HashMap();
		  counter = 1;
	  }
	/**
	 * Getter method for queryType
	 * @return
	 */
	
	public String getQueryType()
	{
		return queryType;
	}

	/**
	 * Setter method for queryType
	 * @param queryType
	 */
	public void setQueryType(String queryType)
	{
		this.queryType = queryType;
	}

	/**
	 * This method validates the inputs
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		ActionErrors errors = new ActionErrors();
		outputDsList = new ArrayList();
		inputDsList = new ArrayList();
		//metadataManager = MetadataManager.getInstance();
		
		Map inputMap = new HashMap();
		List testList = new ArrayList();

		
		Map map = getValues();
		Set s = map.keySet();

		/** Input format from Simple Search is stored in Map as 
		 * 	Key = Input:1_Genomic_Id
		 * 	Value : hh
		 * 	Key : Input:1_DataSource_Id
		 *  Value : 1
		 *  Key : Output:DataSource_Id_4
		 *  Value : on
		 *  Key : Input:1_systemIdentifier
		 *  Value :
		 *  
		 *  Loop over the map and parse the input.
		 *  Store the input data source and genomic id in inputDsList
		 *  and store the oputput  data source in outputDsList
		 */

		int noOfInputs = 0;
		for (Iterator iter = s.iterator(); iter.hasNext();)
		{
			String str = (String) iter.next();
			//Logger.out.debug("KEys: " + str);
			if (str.endsWith("_systemIdentifier"))
			{
				//noOfInputs++;
				//Logger.out.debug("noOfInputs: " + noOfInputs);
			}
			else if (str.startsWith("Input:"))
			{
				/**
				 * Store inmput data in MAp for further validating processs
				 */
				Logger.out.debug("map.get(str): " + map.get(str));
				if (str.endsWith("DataSource_Id") && ((String) map.get(str)).equalsIgnoreCase("-1"))
				{
					//throw new BizLogicException("Select valid data source");
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.valid.datasource"));
				}
				if ((((String) map.get(str)).equalsIgnoreCase(Constants.SELECT_OPTION)))
				{
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.valid.datasource"));
					//throw new BizLogicException("Select valid data source");
				}
				if (str.endsWith("DataSource_Id"))
					noOfInputs++;
				if (map.get(str) != null)
					inputMap.put(str, map.get(str));
				Logger.out.info("Input Key  " + str);
				Logger.out.info("Input Value : " + map.get(str));
			}
			else if (str.startsWith("Output:"))
			{
				/**
				 * Store output data in list
				 */
				String temp = "Output:DataSource_Id_";
				String dataSourceID = str.substring(temp.length(), str.length());
				String dataSourceName = MetadataManager.getDataSourceName(dataSourceID);
				Logger.out.info("Output DS selected : " + dataSourceName);
				outputDsList.add(dataSourceName);
			}
		}
		/**
		 * O/p DS should not be empty
		 */
		if (outputDsList.size() <= 0)
		{
			String arg[] = new String[]{""};
			String errmsg = new DefaultExceptionFormatter().getErrorMessage(
					"errors.no.outputDatasource", arg);
			Logger.out.info(errmsg);
			errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.no.outputDatasource",arg));
		}
		String temp = null;
		List dsName = new ArrayList();
		Logger.out.info("noOfInputs: " + noOfInputs);

		/**
		 * Loop over Input map 
		 * Validate input data and store it in list
		 */
		Set inputCounter = new HashSet();
		Set keySet = inputMap.keySet();
		for (Iterator it = keySet.iterator(); it.hasNext();)
		{
			String key = (String) it.next();
			if (key.startsWith("Input:") && key.endsWith("DataSource_Id"))
			{
				temp = key.substring(key.indexOf("Input:") + "Input:".length(), key.indexOf("_"));
				if (!inputCounter.contains(temp))
				{
					inputCounter.add(temp);
				}
			}
		}
		List inputCounterList = new ArrayList(inputCounter);
		Collections.sort(inputCounterList);
		for (Iterator it = inputCounterList.iterator(); it.hasNext();)
		{
			NameValueBean bean = new NameValueBean();
			String key = (String) it.next();
			//Logger.out.debug("KEEYY " +key);
			temp = "Input:" + key + "_DataSource_Id";
			String dataSourceID = (String) inputMap.get(temp);
			Logger.out.debug("dataSourceID " + dataSourceID);
			String dataSourceName = MetadataManager.getDataSourceName(dataSourceID);
			Logger.out.debug("dataSourceName " + dataSourceName);
			Logger.out.info("size: " + dsName.size());
			/**
			 * Logic to check whther user has enter more than one genomicId for same input data source
			 */
			if(!queryType.equalsIgnoreCase(GCConstants.BACTH_QUERY_VALUE))
			{
			for (int j = 0; j < dsName.size(); j++)
			{
				if (dataSourceName.equalsIgnoreCase(dsName.get(j).toString()))
				{
					String arg[] = new String[]{dataSourceName};
					String errmsg = new DefaultExceptionFormatter().getErrorMessage(
							"errors.duplicate.datasource", arg);
					Logger.out.info(errmsg);
					errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.duplicate.datasource",arg));
				}
			}
			}	
			dsName.add(dataSourceName);

			String tempkey = "Input:" + key + "_Genomic_Id";
			String genomicID = (String) inputMap.get(tempkey);
			/**
			 * Logic to check whther user has enter null value for any of genomicId
			 */
			if (genomicID.length() <= 0)
			{
				String arg[] = new String[]{dataSourceName};
				String errmsg = new DefaultExceptionFormatter().getErrorMessage(
						"error.null.genomicid", arg);
				Logger.out.info(errmsg);
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.null.genomicid",arg));
			}
			bean.setName(dataSourceName);
			bean.setValue(genomicID);
			inputDsList.add(bean);
			Logger.out.info("Input Ds selected : " + bean.getName() + "---" + bean.getValue());
			testList.add(dataSourceName);

		}

		/**
		 * Test if data source is in both Input and output list  
		 */
		for (int i = 0; i < testList.size(); i++)
		{
			String dataSourceName = (String) testList.get(i);
			if (Utility.listContainValue(dataSourceName, outputDsList))
			{
				String arg[] = new String[]{dataSourceName};
				String errmsg = new DefaultExceptionFormatter().getErrorMessage(
						"errors.datasource.inputOutput", arg);
				Logger.out.info(errmsg);
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("errors.datasource.inputOutput",arg));
				//throw new BizLogicException(errmsg);
			}

		}
		return errors;
	}

	/**
	 * Getter nethod for input data source list
	 * @return
	 */
	public List getInputDsList()
	{
		return inputDsList;
	}

	/**
	 * Setter nethod for input data source list
	 * @return
	 */
	public void setInputDsList(List inputDsList)
	{
		this.inputDsList = inputDsList;
	}

	/**
	 * Getter nethod for output data source list
	 * @return
	 */
	public List getOutputDsList()
	{
		return outputDsList;
	}

	/**
	 * Setter nethod for output data source list
	 * @return
	 */
	public void setOutputDsList(List outputDsList)
	{
		this.outputDsList = outputDsList;
	}

	
	
	
}