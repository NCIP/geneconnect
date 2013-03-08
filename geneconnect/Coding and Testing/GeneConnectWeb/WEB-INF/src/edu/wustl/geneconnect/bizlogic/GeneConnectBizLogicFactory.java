/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.GeneConnectBizLogicFactory</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import java.util.Map;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.util.global.GCConstants;

/**
 * Business Logic Factory for Gene Connect web application.
 * @author mahesh_nalkande
 * @version 1.0
 */
public class GeneConnectBizLogicFactory extends AbstractBizLogicFactory
{

	/**
	 * GeneConnect Business Logic factory instance
	 */
	private static GeneConnectBizLogicFactory gcBizLogicFactory;

	/**
	 * Configuration file for GC Busines Logic factory
	 */
	private static String gcBizLogicConfigFile = GCConstants.GC_BUSINESS_LOGIC_CONFIG;

	/**
	 * Module map contains mappings for all Business Logics
	 */
	private Map moduleMap;

	/**
	 * Empty constructor
	 */
	protected GeneConnectBizLogicFactory()
	{
	}

	/**
	 * Sets the instance of GC Biz Logic factory
	 * @param tempGCBizLogicFactory instance of GeneConnectBizLogicFactory
	 */

	public static synchronized void setInstance(GeneConnectBizLogicFactory tempGCBizLogicFactory)
	{
		gcBizLogicFactory = tempGCBizLogicFactory;
	}

	/**
	 * Sets the config file name
	 * @param tempGCBizLogicConfigFile config file path.
	 */
	public static synchronized void setConfigFileName(String tempGCBizLogicConfigFile)
	{
		gcBizLogicConfigFile = tempGCBizLogicConfigFile;
	}

	/**
	 * Init method initializes module map
	 * @param gcBizLogicConfigFile config file path.
	 */
	protected void init(String gcBizLogicConfigFile)
	{
		moduleMap = updateModuleMap(gcBizLogicConfigFile);
	}

	/**
	 * Returns perticular Biz logic
	 * @param businessAction business action for which biz logic is required.
	 * @return BizLogic instance.
	 */
	public BizLogicInterface getBizLogic(String businessAction) throws BizLogicException
	{
		BizLogicInterface bizLogic = null;
		try
		{
			String bizLogicClass = (String) moduleMap.get(businessAction);
			//bizLogic = ((BizLogicInterface) moduleMap.get(businessAction));
			bizLogic = (BizLogicInterface) Class.forName(bizLogicClass).newInstance();
			Logger.out.info("Return bizLogic : " + bizLogic);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Logger.out.error(e.getMessage(), e);
			throw new BizLogicException(e.getMessage(), e);
		}
		return bizLogic;
	}

	/**
	 * This method returns the instance of BizLogicFactory
	 * @return bizLogicFactory
	 */
	public static synchronized GeneConnectBizLogicFactory getInstance()
	{
		if (gcBizLogicFactory == null)
		{
			gcBizLogicFactory = new GeneConnectBizLogicFactory();
			gcBizLogicFactory.init(gcBizLogicConfigFile);
		}
		return gcBizLogicFactory;
	}
}