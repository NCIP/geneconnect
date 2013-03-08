/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.bizlogic.BizLogicInterface</p> 
 */

package edu.wustl.geneconnect.bizlogic;

import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.dbManager.DAOException;

/**
 * THis interface will be implemented by all business logics. 
 * @author mahesh_nalkande
 * @version 1.0
 */
public interface BizLogicInterface
{

	/**
	 * Applies business logic on the passed input data and retunrs back the result.
	 * 
	 * @param inputData Data on which Business Logic will operate.
	 * @return ResultDataInterface Result data, which can be SuccessResultData or ValidationResultData.
	 * @throws Exception exception
	 */
	ResultDataInterface getResult(InputDataInterface inputData) throws BizLogicException,
			DAOException;
}