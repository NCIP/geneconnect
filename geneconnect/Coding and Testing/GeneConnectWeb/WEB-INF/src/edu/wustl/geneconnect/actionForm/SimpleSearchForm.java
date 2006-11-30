/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.actionForm.SimpleSearchForm</p> 
 */

package edu.wustl.geneconnect.actionForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

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
	
}