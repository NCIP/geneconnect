/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.cacore;

import java.util.List;

import edu.wustl.common.util.logger.Logger;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.exception.GCRuntimeException;

public class caCoreThread extends Thread
{

	GenomicIdentifierSet querySet;
	List result;
	Exception exp;
	static int counter;

	public caCoreThread(GenomicIdentifierSet gset, String v)
	{
		super(v);
		querySet = gset;
		result = null;
		exp = null;
	}

	public void run()
	{
		try
		{

			Logger.out.info("Executing query for:" + getName());
			List resultList = CaCoreClient.appServiceQuery(GenomicIdentifierSet.class.getName(),
					querySet);
			setResult(resultList);
			Logger.out.info("resultList for :" + getName() + "--" + resultList.size());
		}
		catch (Exception exp)
		{
			Logger.out.error(exp.getMessage(), exp);
			setExp(exp);
			throw new GCRuntimeException(exp.getMessage());

		}

		//		for(int i=0;i<6;i++)
		//		{
		//			System.out.println("IN caCoreThread run " +s);
		//		}	
		incrementCounter();
	}

	synchronized void incrementCounter()
	{
		counter++;
	}

	public static int getCounter()
	{
		return counter;
	}

	public List getResult()
	{
		return result;
	}

	public void setResult(List result)
	{
		this.result = result;
	}

	public Exception getExp()
	{
		return exp;
	}

	public void setExp(Exception exp)
	{
		this.exp = exp;
	}
}
