/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.AbstractBizLogicFactory</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.wustl.geneconnect.exception.GCRuntimeException;
import edu.wustl.geneconnect.util.global.GCConstants;

/**
 * This is a base class for all the Business logic factories.
 * This class implements the logic of reading the configuration XML file
 *
 * @author mahesh_nalkande
 * @version 1.0
 * TODO : Later on this needs to moved to the Common Package
 */
public abstract class AbstractBizLogicFactory
{

	/**
	 * This method updates module map by parsing xml file
	 * @param xmlFileName file to be parsed
	 * @return  moduleMap Map
	 */
	public final Map updateModuleMap(String xmlFileName)
	{
		Map moduleMap = new HashMap();
		SAXReader saxReader = new SAXReader();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFileName);
		Document document = null;

		try
		{

			document = saxReader.read(inputStream);
			Element businessLogics = document.getRootElement();
			Iterator businessLogicIterator = businessLogics
					.elementIterator(GCConstants.BUSINESS_LOGIC_ELEMENT_ITERATOR);
			Element businessLogic = null;
			Element businessAction = null;
			Element instanceType = null;
			String instanceTypeString = null;
			String businessActionString = null;
			while (businessLogicIterator.hasNext())
			{
				try
				{
					businessLogic = (Element) businessLogicIterator.next();
					businessAction = businessLogic.element(GCConstants.BUSINESS_ACTION_ELEMENT);
					instanceType = businessLogic.element(GCConstants.INSTANCE_TYPE_ELEMENT);
//					moduleMap.put(businessAction.getStringValue(), Class.forName(
//							instanceType.getStringValue()).newInstance());
					moduleMap.put(businessAction.getStringValue(), instanceType.getStringValue());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (DocumentException e)
		{
			throw new GCRuntimeException(e);
		}
		catch (Exception e)
		{
			throw new GCRuntimeException(e);
		}
		return moduleMap;
	}
}