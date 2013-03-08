/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.testCase;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Invokes all test cases
 * @author sachin_lale
 *
 */
public class GeneConnectTestSuite
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All Test Case");
		suite.addTestSuite(SimpleSearchTest.class);
		suite.addTestSuite(AdvancedSearchTest.class);
		suite.addTestSuite(SearchWithConfidenceTest.class);
		suite.addTestSuite(SearchWithFrequencyTest.class);
		suite.addTestSuite(SearchWithOntTest.class);
		return suite;
	}

}
