/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.postwork.SummaryExceptionHandler</p> 
 */
package edu.wustl.geneconnect.postwork;


/**
 * Exception handler
 * @author mahesh_nalkande
 * @version 1.0
 */
public class SummaryExceptionHandler
{

	/**
	 * Default constructor
	 */
	public SummaryExceptionHandler()
	{
		super();
	}
	
	/**
	 * Exception handler
	 * @param e exception
	 */
	public static void handleException(Exception e)
	{
		//TODO : this method can be extended further to check for type of exception (error code)
		//and take the action accordingly.
		e.printStackTrace();
		System.out.println("Exception has occured during the calculation of "
				+ "all-to-all genomic links.");
		System.out.println("Calculation of all-to-all genomic links can not be continued further.");
		System.out.println("Please report this problem to 'help@mga.wustl.edu' and "
				+ "attach the log file Errorlog.txt (located ./Logs folder) to the mail.");
	}

}
