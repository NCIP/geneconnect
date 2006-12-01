/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.postwork.SummaryReflectionUtil</p> 
 */

package edu.wustl.geneconnect.postwork;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.GeneConnectServerConstants;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.metadata.domain.DataSource;

/**
 * @author mahesh_nalkande
 * @version 1.0
 */
public class SummaryReflectionUtil implements GeneConnectServerConstants
{

	/**
	 * Genomic Classes required for reflcetion
	 */
	private static Class geneClass = getGeneClass();
	private static Class messengerRNAClass = getMrnaClass();
	private static Class proteinClass = getProteinClass();

	/**
	 * Metadata Manager instance used to obtain all metadata. 
	 */
	private static MetadataManager metadataManager = MetadataManager.getInstance();

	private static List geneDataSources = getGeneDataSources();
	private static List mrnaDataSources = getMrnaDataSources();
	private static List proteinDataSources = getProteinDataSources();

	/**
	 * Default constructor
	 */
	public SummaryReflectionUtil()
	{
		super();
	}

	/**
	 * Initialize genomic classes - required for reflection
	 */
	public static Class getGeneClass()
	{
		Class geneClass = null;
		try
		{
			geneClass = Class.forName("edu.wustl.geneconnect.domain.Gene");
		}
		catch (ClassNotFoundException e)
		{
			Logger.log("ClassNotFoundException occured while instantiating the gene class.",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		return geneClass;
	}

	/**
	 * Initialize genomic classes - required for reflection
	 */
	public static Class getMrnaClass()
	{
		Class mrnaClass = null;
		try
		{
			mrnaClass = Class.forName("edu.wustl.geneconnect.domain.MessengerRNA");
		}
		catch (ClassNotFoundException e)
		{
			Logger.log("ClassNotFoundException occured while instantiating the mrna class.",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		return mrnaClass;
	}

	/**
	 * Initialize genomic classes - required for reflection
	 */
	public static Class getProteinClass()
	{
		Class proteinClass = null;
		try
		{
			proteinClass = Class.forName("edu.wustl.geneconnect.domain.Protein");
		}
		catch (ClassNotFoundException e)
		{
			Logger.log("ClassNotFoundException occured while instantiating the protein class.",
					Logger.FATAL);
			SummaryExceptionHandler.handleException(e);
		}
		return proteinClass;
	}

	/**
	 * Returns the list of gene Data sources.
	 * @return List of gene Data sources.
	 */
	public static List getGeneDataSources()
	{
		Map datasources = metadataManager.getDataSources();
		DataSource dataSource = null;
		List geneDataSources = new ArrayList();
		Iterator iterator = datasources.values().iterator();
		while (iterator.hasNext())
		{
			dataSource = (DataSource) iterator.next();
			if (dataSource.getClassName().equals("Gene"))
			{
				geneDataSources.add(dataSource);
			}
		}
		return geneDataSources;
	}

	/**
	 * Returns the list of mrna Data sources.
	 * @return List of mrna Data sources.
	 */
	public static List getMrnaDataSources()
	{
		Map datasources = metadataManager.getDataSources();
		DataSource dataSource = null;
		List mrnaDataSources = new ArrayList();
		Iterator iterator = datasources.values().iterator();
		while (iterator.hasNext())
		{
			dataSource = (DataSource) iterator.next();
			if (dataSource.getClassName().equals("MessengerRNA"))
			{
				mrnaDataSources.add(dataSource);
			}
		}
		return mrnaDataSources;
	}

	/**
	 * Returns the list of protein Data sources.
	 * @return List of protein Data sources.
	 */
	public static List getProteinDataSources()
	{
		Map datasources = metadataManager.getDataSources();
		DataSource dataSource = null;
		List proteinDataSources = new ArrayList();
		Iterator iterator = datasources.values().iterator();
		while (iterator.hasNext())
		{
			dataSource = (DataSource) iterator.next();
			if (dataSource.getClassName().equals("Protein"))
			{
				proteinDataSources.add(dataSource);
			}
		}
		return proteinDataSources;
	}

	public static String getSentenceCase(String argString)
	{
		return argString.substring(0, 1).toUpperCase() + argString.substring(1, argString.length());
	}

	public static String getGeneValueString(Gene gene)
	{
		String geneValues = null;
		Object currentValue;
		List geneValuesList = getGeneValues(gene);
		if (geneValuesList.isEmpty())
		{
			geneValues = "";
		}
		else
		{
			Iterator iterator = geneValuesList.iterator();
			while (iterator.hasNext())
			{
				currentValue = iterator.next();
				if (geneValues == null) //Initialized to null and checked 
					//for null instead of "" beacuse the first value can be " 
					geneValues = currentValue.toString();
				else
					geneValues = geneValues + FIELD_DELIMITER + currentValue;
			}
		}
		return geneValues;
	}

	public static String getMessengeRNAValuesString(MessengerRNA messengerRNA)
	{
		String mrnaValues = null;
		Object currentValue;
		List mrnaValuesList = getMessengeRNAValues(messengerRNA);
		if (mrnaValuesList.isEmpty())
		{
			mrnaValues = "";
		}
		else
		{
			Iterator iterator = mrnaValuesList.iterator();
			while (iterator.hasNext())
			{
				currentValue = iterator.next();
				if (mrnaValues == null)
					mrnaValues = currentValue.toString();
				else
					mrnaValues = mrnaValues + FIELD_DELIMITER + currentValue;
			}
		}
		return mrnaValues;
	}

	public static String getProteinValuesString(Protein protein)
	{
		String proteinValues = null;
		Object currentValue;
		List proteinValuesList = getProteinValues(protein);
		if (proteinValuesList.isEmpty())
		{
			proteinValues = "";
		}
		else
		{
			Iterator iterator = proteinValuesList.iterator();
			while (iterator.hasNext())
			{
				currentValue = iterator.next();
				if (proteinValues == null)
					proteinValues = currentValue.toString();
				else
					proteinValues = proteinValues + FIELD_DELIMITER + currentValue;
			}
		}
		return proteinValues;
	}

	public static List getGeneValues(Gene gene)
	{
		List geneValues = new ArrayList();
		DataSource dataSource = null;

		if (geneDataSources != null)
		{
			//Class[] argType = new Class[1];
			Object returnValue = null;
			try
			{
				Iterator iterator = geneDataSources.iterator();
				while (iterator.hasNext())
				{
					dataSource = (DataSource) iterator.next();
					returnValue = null;

					if (gene != null)
					{
						//Class typeClass = Class.forName(dataSource.getAttributeType());
						//argType[0] = typeClass;
						Method method = geneClass.getMethod("get"
								+ getSentenceCase(dataSource.getAttributeName()), null);
						returnValue = method.invoke(gene, null);
					}

					if (returnValue == null)
						returnValue = "";

					geneValues.add(returnValue);
				}
			}
			catch (IllegalAccessException e)
			{
				Logger.log(
						"IllegalAccessException occured while getting the genomic identifier value "
								+ "from gene using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (SecurityException e)
			{
				Logger.log("SecurityException occured while getting the genomic identifier value "
						+ "from gene using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (NoSuchMethodException e)
			{
				Logger.log(
						"NoSuchMethodException occured while getting the genomic identifier value "
								+ "from gene using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (IllegalArgumentException e)
			{
				Logger.log(
						"IllegalArgumentException occured while getting the genomic identifier value "
								+ "from gene using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (InvocationTargetException e)
			{
				Logger.log(
						"InvocationTargetException occured while getting the genomic identifier value "
								+ "from gene using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
		}
		return geneValues;
	}

	public static List getMessengeRNAValues(MessengerRNA messengerRNA)
	{
		List mrnaValues = new ArrayList();
		DataSource dataSource = null;

		if (mrnaDataSources != null)
		{
			//Class[] argType = new Class[1];
			Object returnValue = null;
			try
			{
				Iterator iterator = mrnaDataSources.iterator();
				while (iterator.hasNext())
				{
					dataSource = (DataSource) iterator.next();
					returnValue = null;

					if (messengerRNA != null)
					{
						//Class typeClass = Class.forName(dataSource.getAttributeType());
						//argType[0] = typeClass;
						Method method = messengerRNAClass.getMethod("get"
								+ getSentenceCase(dataSource.getAttributeName()), null);
						returnValue = method.invoke(messengerRNA, null);
					}

					if (returnValue == null)
						returnValue = "";

					mrnaValues.add(returnValue);
				}
			}
			catch (IllegalAccessException e)
			{
				Logger.log(
						"IllegalAccessException occured while getting the genomic identifier value "
								+ "from mrna using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (SecurityException e)
			{
				Logger.log("SecurityException occured while getting the genomic identifier value "
						+ "from mrna using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (NoSuchMethodException e)
			{
				Logger.log(
						"NoSuchMethodException occured while getting the genomic identifier value "
								+ "from mrna using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (IllegalArgumentException e)
			{
				Logger.log(
						"IllegalArgumentException occured while getting the genomic identifier value "
								+ "from mrna using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (InvocationTargetException e)
			{
				Logger.log(
						"InvocationTargetException occured while getting the genomic identifier value "
								+ "from mrna using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
		}
		return mrnaValues;
	}

	public static List getProteinValues(Protein protein)
	{
		List proteinValues = new ArrayList();
		DataSource dataSource = null;

		if (proteinDataSources != null)
		{
			//Class[] argType = new Class[1];
			Object returnValue = null;
			try
			{
				Iterator iterator = proteinDataSources.iterator();
				while (iterator.hasNext())
				{
					dataSource = (DataSource) iterator.next();
					returnValue = null;

					if (protein != null)
					{
						//Class typeClass = Class.forName(dataSource.getAttributeType());
						///argType[0] = typeClass;
						Method method = proteinClass.getMethod("get"
								+ getSentenceCase(dataSource.getAttributeName()), null);
						returnValue = method.invoke(protein, null);
					}

					if (returnValue == null)
						returnValue = "";

					proteinValues.add(returnValue);
				}
			}
			catch (IllegalAccessException e)
			{
				Logger.log(
						"IllegalAccessException occured while getting the genomic identifier value "
								+ "from protein using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (SecurityException e)
			{
				Logger.log("SecurityException occured while getting the genomic identifier value "
						+ "from protein using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (NoSuchMethodException e)
			{
				Logger.log(
						"NoSuchMethodException occured while getting the genomic identifier value "
								+ "from protein using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (IllegalArgumentException e)
			{
				Logger.log(
						"IllegalArgumentException occured while getting the genomic identifier value "
								+ "from protein using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
			catch (InvocationTargetException e)
			{
				Logger.log(
						"InvocationTargetException occured while getting the genomic identifier value "
								+ "from protein using reflection.", Logger.FATAL);
				SummaryExceptionHandler.handleException(e);
			}
		}
		return proteinValues;
	}

	public static List getGenomicIdentifierSetValues(Gene gene, MessengerRNA messengerRNA,
			Protein protein)
	{
		List genomicIdentifierSetValues = getGeneValues(gene);
		genomicIdentifierSetValues.addAll(getMessengeRNAValues(messengerRNA));
		genomicIdentifierSetValues.addAll(getProteinValues(protein));
		return genomicIdentifierSetValues;
	}

	public static String getGeneTableName()
	{
		String geneTableName = "";
		if (geneDataSources != null && geneDataSources.get(0) != null)
		{
			geneTableName = ((DataSource) geneDataSources.get(0)).getTableName();
		}
		return geneTableName;
	}

	public static String getMrnaTableName()
	{
		String mrnaTableName = "";
		if (mrnaDataSources != null && mrnaDataSources.get(0) != null)
		{
			mrnaTableName = ((DataSource) mrnaDataSources.get(0)).getTableName();
		}
		return mrnaTableName;
	}

	public static String getProteinTableName()
	{
		String proteinTableName = "";
		if (proteinDataSources != null && proteinDataSources.get(0) != null)
		{
			proteinTableName = ((DataSource) proteinDataSources.get(0)).getTableName();
		}
		return proteinTableName;
	}

	public static List getGeneColumnNames()
	{
		List geneColumnNames = new ArrayList();

		if (geneDataSources != null)
		{
			DataSource dataSource = null;
			Iterator iterator = geneDataSources.iterator();
			while (iterator.hasNext())
			{
				dataSource = (DataSource) iterator.next();
				geneColumnNames.add(dataSource.getColumnName());
			}
		}
		return geneColumnNames;
	}

	public static List getMrnaColumnNames()
	{
		List mrnaColumnNames = new ArrayList();

		if (mrnaDataSources != null)
		{
			DataSource dataSource = null;
			Iterator iterator = mrnaDataSources.iterator();
			while (iterator.hasNext())
			{
				dataSource = (DataSource) iterator.next();
				mrnaColumnNames.add(dataSource.getColumnName());
			}
		}
		return mrnaColumnNames;
	}

	public static List getProteinColumnNames()
	{
		List proteinColumnNames = new ArrayList();

		if (proteinDataSources != null)
		{
			DataSource dataSource = null;
			Iterator iterator = proteinDataSources.iterator();
			while (iterator.hasNext())
			{
				dataSource = (DataSource) iterator.next();
				proteinColumnNames.add(dataSource.getColumnName());
			}
		}
		return proteinColumnNames;
	}

	public static List getGenomicIdentifierSetColumnNames()
	{
		List genomicIdentifierSetColumnNames = getGeneColumnNames();
		genomicIdentifierSetColumnNames.addAll(getMrnaColumnNames());
		genomicIdentifierSetColumnNames.addAll(getProteinColumnNames());
		return genomicIdentifierSetColumnNames;
	}

	public static String getGeneColumnNamesString()
	{
		List geneColumnNames = getGeneColumnNames();
		String geneColumnNamesString = "";
		Iterator iterator = geneColumnNames.iterator();
		while (iterator.hasNext())
		{
			if (geneColumnNamesString.equals(""))
				geneColumnNamesString = (String) iterator.next();
			else
				geneColumnNamesString = geneColumnNamesString + COLUMN_NAMES_DELIMITER
						+ (String) iterator.next();
		}
		return geneColumnNamesString;
	}

	public static String getMrnaColumnNamesString()
	{
		List mrnaColumnNames = getMrnaColumnNames();
		String mrnaColumnNamesString = "";
		Iterator iterator = mrnaColumnNames.iterator();
		while (iterator.hasNext())
		{
			if (mrnaColumnNamesString.equals(""))
				mrnaColumnNamesString = (String) iterator.next();
			else
				mrnaColumnNamesString = mrnaColumnNamesString + COLUMN_NAMES_DELIMITER
						+ (String) iterator.next();
		}
		return mrnaColumnNamesString;
	}

	public static String getProteinColumnNamesString()
	{
		List proteinColumnNames = getProteinColumnNames();
		String proteinColumnNamesString = "";
		Iterator iterator = proteinColumnNames.iterator();
		while (iterator.hasNext())
		{
			if (proteinColumnNamesString.equals(""))
				proteinColumnNamesString = (String) iterator.next();
			else
				proteinColumnNamesString = proteinColumnNamesString + COLUMN_NAMES_DELIMITER
						+ (String) iterator.next();
		}
		return proteinColumnNamesString;
	}

	public static String getGenomicIdentifierSetColumnNamesString()
	{
		return getGeneColumnNamesString() + COLUMN_NAMES_DELIMITER + getMrnaColumnNamesString()
				+ COLUMN_NAMES_DELIMITER + getProteinColumnNamesString();

	}
}