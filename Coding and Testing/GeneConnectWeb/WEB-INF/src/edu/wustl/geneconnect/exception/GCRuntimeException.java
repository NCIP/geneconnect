/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.exception;

import java.util.ArrayList;
import java.util.List;

/**
 * This exception class will be used to wrap the exceptions
 * caught/to be thrown in GC.
 *
 * @author  mahesh_nalkande
 * @version 1.0
 */

public class GCRuntimeException extends RuntimeException
{

	/**
	 * Error Code of the exception
	 */
	private String errorCode;
	/**
	 * List of Error Parameters
	 */
	private List errorParams;

	/**
	 * Getter method for error code
	 * @return Error code
	 */
	public String getErrorCode()
	{
		return errorCode == null ? new String() : errorCode;
	}

	/**
	 * Setter method for Error code
	 * @param errorCode Error code for the exception
	 */
	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * Getter method for Error parameters
	 * @return List of Error parameters
	 */
	public List getErrorParams()
	{
		return ((List) (errorParams == null ? new ArrayList() : errorParams));
	}

	/**
	 * Setter method for Error parameters
	 * @param errorParams List of Error Parameters
	 */
	public void setErrorParams(List errorParams)
	{
		this.errorParams = errorParams;
	}

	/**
	 * Default constructor.
	 */
	public GCRuntimeException()
	{
		super();
	}

	/**
	 * @param errorCode the error code to set for this exception.
	 */
	public GCRuntimeException(String errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * @param errorCode the error code to set for this exception.
	 * @param message the message associated with this exception.
	 */
	public GCRuntimeException(String errorCode, String message)
	{
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * Creates an instance by wrapping the incoming exception.
	 * <P>
	 * Both checked and unchecked exception can be wrapped.This constructor
	 * is used primarily for wrapping checked exceptions that are to be
	 * directly passed to the client.
	 * <P>
	 * 
	 * @param t       Exception that is being wrapped
	 */
	public GCRuntimeException(Throwable t)
	{
		super(t);
	}

	/**
	 * @param t         Exception that is being wrapped
	 * @param errorCode Error code to be set
	 */
	public GCRuntimeException(Throwable t, String errorCode)
	{
		super(t);
		this.errorCode = errorCode;
	}
}