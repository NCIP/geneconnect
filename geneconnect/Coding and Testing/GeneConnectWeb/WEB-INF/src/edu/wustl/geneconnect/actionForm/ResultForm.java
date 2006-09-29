/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.actionForm.SimpleSearchForm</p> 
 */

package edu.wustl.geneconnect.actionForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts.action.ActionForm;

/**
 * Simple Search Action Form will hold the data required for simple search operation
 * @author mahesh_nalkande
 * @version 1.0
 */
public class ResultForm extends ActionForm
{

	/**
	 * Default constructor
	 */
	public ResultForm()
	{
	}

	/**
	 * List of Data sources
	 */
	private List columnHeaders;

	private List dataList;

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

	public List getColumnHeaders()
	{
		return this.columnHeaders;
	}

	public void setColumnHeaders(List columnHeaders)
	{
		this.columnHeaders = columnHeaders;
	}

	public List getDataList()
	{
		return this.dataList;
	}

	public void setDataList(List dataList)
	{
		this.dataList = dataList;
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

}