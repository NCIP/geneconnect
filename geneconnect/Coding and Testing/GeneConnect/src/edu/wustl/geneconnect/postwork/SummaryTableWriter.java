/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java edu.wustl.geneconnect.postwork.SummaryTableWriter</p> 
 */

package edu.wustl.geneconnect.postwork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.dataminer.server.log.Logger;

import edu.wustl.geneconnect.GeneConnectServerConstants;
import edu.wustl.geneconnect.metadata.MetadataWriter;
import edu.wustl.geneconnect.metadata.domain.GenomicIdentifierSetOntWithAllDSIds;

/**
 * This class is responsible for writing all summary table data to the files which will be 
 * uploaded to the database later on.
 * It also provides the functionality to upload those files into the data base using MetaDataWriter.
 * @author mahesh_nalkande
 * @version 1.0
 */

public class SummaryTableWriter implements GeneConnectServerConstants
{

	/**
	 * Default constructor
	 */
	public SummaryTableWriter()
	{
		super();
	}

	/**
	 * Output file writers - one for each table
	 */
	private BufferedWriter opGeneFile, opMrnaFile, opProteinFile, opGene_MrnaFile,
			opMrna_ProteinFile, opProtein_GeneFile, opGenomicIdentifierSetFile, opSet_OntFile;

	private BufferedWriter opGenomicIdentifierSet_OntFile;

	private StringBuffer sbGeneFile, sbMrnaFile, sbProteinFile, sbGene_MrnaFile,
			sbMrna_ProteinFile, sbProtein_GeneFile, sbGenomicIdentifierSetFile, sbSet_OntFile;

	/**
	 * open Output Data Files.
	 * @param stepCount Current step of summary execution
	 */
	void openOutputDataFiles(int stepCount)
	{
		try
		{
			switch (stepCount)
			{
				case STEP_1_FOR_UNNORMALIZED_SET_ONT :
					opGenomicIdentifierSet_OntFile = new BufferedWriter(new FileWriter(new File(
							GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP1_FILENAME)));
					break;

				case STEP_2_FOR_UNIQUE_GENES :
					opGenomicIdentifierSet_OntFile = new BufferedWriter(new FileWriter(new File(
							GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP2_FILENAME)));
					opGeneFile = new BufferedWriter(new FileWriter(new File(GENE_DATA_FILENAME)));
					break;

				case STEP_3_FOR_UNIQUE_MRNAS :
					opGenomicIdentifierSet_OntFile = new BufferedWriter(new FileWriter(new File(
							GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP3_FILENAME)));
					opMrnaFile = new BufferedWriter(new FileWriter(new File(MRNA_DATA_FILENAME)));
					break;

				case STEP_4_FOR_UNIQUE_PROTEINS :
					opGenomicIdentifierSet_OntFile = new BufferedWriter(new FileWriter(new File(
							GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP4_FILENAME)));
					opProteinFile = new BufferedWriter(new FileWriter(new File(
							PROTEIN_DATA_FILENAME)));
					break;

				case STEP_5_FOR_UNIQUE_SET_ONT :
					opGene_MrnaFile = new BufferedWriter(new FileWriter(new File(
							GENE_MRNA_DATA_FILENAME)));
					opMrna_ProteinFile = new BufferedWriter(new FileWriter(new File(
							MRNA_PROTEIN_DATA_FILENAME)));
					opProtein_GeneFile = new BufferedWriter(new FileWriter(new File(
							PROTEIN_GENE_DATA_FILENAME)));
					opGenomicIdentifierSetFile = new BufferedWriter(new FileWriter(new File(
							GENOMIC_IDENTIFIER_SET_DATA_FILENAME)));
					opSet_OntFile = new BufferedWriter(new FileWriter(new File(
							SET_ONT_DATA_FILENAME)));
					break;

				default :
					break;
			}
		}
		catch (IOException e2)
		{
			Logger.log("Failed to create an output file in current directory" + e2.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e2);
		}
		Logger.log("Created output files in current directory.", Logger.DEBUG);
	}

	/**
	 * Closes o/p data files
	 * @param stepCount Current step of summary execution
	 */
	void closeOutputDataFiles(int stepCount)
	{
		try
		{
			switch (stepCount)
			{
				case STEP_1_FOR_UNNORMALIZED_SET_ONT :
					opGenomicIdentifierSet_OntFile.close();
					break;

				case STEP_2_FOR_UNIQUE_GENES :
					opGenomicIdentifierSet_OntFile.close();
					opGeneFile.close();
					break;

				case STEP_3_FOR_UNIQUE_MRNAS :
					opGenomicIdentifierSet_OntFile.close();
					opMrnaFile.close();
					break;

				case STEP_4_FOR_UNIQUE_PROTEINS :
					opGenomicIdentifierSet_OntFile.close();
					opProteinFile.close();
					break;

				case STEP_5_FOR_UNIQUE_SET_ONT :
					opGene_MrnaFile.close();
					opMrna_ProteinFile.close();
					opProtein_GeneFile.close();
					opGenomicIdentifierSetFile.close();
					opSet_OntFile.close();
					break;

				default :
					break;
			}
		}
		catch (IOException e)
		{
			Logger
					.log("IOException occured while closing the file." + e.getMessage(),
							Logger.DEBUG);
			SummaryExceptionHandler.handleException(e);
		}
		Logger.log("All output files have been closed successfully.", Logger.DEBUG);
	}

	/**
	 * writes headers into o/p data file
	 * @param stepCount Current step of summary execution
	 */
	void writeOutputDataFileHeaders(int stepCount)
	{
		try
		{
			switch (stepCount)
			{
				case STEP_1_FOR_UNNORMALIZED_SET_ONT :
					opGenomicIdentifierSet_OntFile
							.write("UNRECOVERABLE\n LOAD DATA INFILE * APPEND INTO TABLE GENOMIC_IDENTIFIER_SET_ONT_1_U "
									+ "FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' ("
									+ SummaryReflectionUtil
											.getGenomicIdentifierSetColumnNamesString()
									+ ", PATH_ID) \n" + "BEGINDATA\n");
					opGenomicIdentifierSet_OntFile.flush();
					break;

				case STEP_2_FOR_UNIQUE_GENES :
					opGenomicIdentifierSet_OntFile
							.write("UNRECOVERABLE\n LOAD DATA INFILE * APPEND INTO TABLE GENOMIC_IDENTIFIER_SET_ONT_2_U "
									+ "FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' (GENE_ID, "
									+ SummaryReflectionUtil.getMrnaColumnNamesString()
									+ ", "
									+ SummaryReflectionUtil.getProteinColumnNamesString()
									+ " , PATH_ID) \n" + "BEGINDATA\n");
					opGenomicIdentifierSet_OntFile.flush();

					opGeneFile.write("UNRECOVERABLE\n LOAD DATA INFILE * APPEND INTO TABLE "
							+ SummaryReflectionUtil.getGeneTableName()
							+ "_U FIELDS TERMINATED BY '" + FIELD_DELIMITER + "' " + " (GENE_ID, "
							+ SummaryReflectionUtil.getGeneColumnNamesString() + ")\n"
							+ "BEGINDATA\n");
					opGeneFile.flush();
					break;

				case STEP_3_FOR_UNIQUE_MRNAS :
					opGenomicIdentifierSet_OntFile
							.write("UNRECOVERABLE\n  LOAD DATA INFILE * APPEND INTO TABLE GENOMIC_IDENTIFIER_SET_ONT_3_U "
									+ "FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' "
									+ "(GENE_ID, MRNA_ID, "
									+ SummaryReflectionUtil.getProteinColumnNamesString()
									+ ", PATH_ID)\n" + "BEGINDATA\n");
					opGenomicIdentifierSet_OntFile.flush();

					opMrnaFile.write("UNRECOVERABLE\n LOAD DATA INFILE * APPEND INTO TABLE "
							+ SummaryReflectionUtil.getMrnaTableName()
							+ "_U FIELDS TERMINATED BY '" + FIELD_DELIMITER + "' (MRNA_ID, "
							+ SummaryReflectionUtil.getMrnaColumnNamesString() + ")\n"
							+ "BEGINDATA\n");
					opMrnaFile.flush();
					break;

				case STEP_4_FOR_UNIQUE_PROTEINS :
					opGenomicIdentifierSet_OntFile
							.write("UNRECOVERABLE\n LOAD DATA INFILE * APPEND INTO TABLE GENOMIC_IDENTIFIER_SET_ONT_4_U "
									+ "FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' "
									+ "(GENE_ID, MRNA_ID, "
									+ "PROTEIN_ID, PATH_ID)\n"
									+ "BEGINDATA\n");
					opGenomicIdentifierSet_OntFile.flush();

					opProteinFile.write("UNRECOVERABLE\n LOAD DATA INFILE * APPEND INTO TABLE "
							+ SummaryReflectionUtil.getProteinTableName()
							+ "_U FIELDS TERMINATED BY '" + FIELD_DELIMITER + "' "
							+ " (PROTEIN_ID, "
							+ SummaryReflectionUtil.getProteinColumnNamesString() + ")\n"
							+ "BEGINDATA\n");
					opProteinFile.flush();
					break;

				case STEP_5_FOR_UNIQUE_SET_ONT :
					opGene_MrnaFile
							.write("LOAD DATA INFILE * APPEND INTO TABLE GENE_MRNA_U FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' "
									+ " (GENE_ID, MRNA_ID)\n"
									+ "BEGINDATA\n");
					opGene_MrnaFile.flush();

					opMrna_ProteinFile
							.write("LOAD DATA INFILE * APPEND INTO TABLE MRNA_PROTEIN_U FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' "
									+ " (MRNA_ID, PROTEIN_ID)\n"
									+ "BEGINDATA\n");
					opMrna_ProteinFile.flush();

					opProtein_GeneFile
							.write("LOAD DATA INFILE * APPEND INTO TABLE PROTEIN_GENE_U FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' "
									+ " (PROTEIN_ID, GENE_ID)\n"
									+ "BEGINDATA\n");
					opProtein_GeneFile.flush();

					opGenomicIdentifierSetFile
							.write("UNRECOVERABLE\n LOAD DATA INFILE * APPEND INTO TABLE GENOMIC_IDENTIFIER_SET_U FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' "
									+ " (SET_ID, GENE_ID, MRNA_ID, PROTEIN_ID, SOLUTION_ID)\n"
									+ "BEGINDATA\n");
					opGenomicIdentifierSetFile.flush();

					opSet_OntFile
							.write("LOAD DATA INFILE * APPEND INTO TABLE SET_ONT_U FIELDS TERMINATED BY '"
									+ FIELD_DELIMITER
									+ "' "
									+ "(SET_ID, PATH_ID)\n"
									+ "BEGINDATA\n");
					opSet_OntFile.flush();
					break;

				default :
					break;
			}
		}
		catch (IOException e)
		{
			Logger.log("IOException occured while writing heder into the file." + e.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e);
		}
		Logger.log("Headers have been written in all output files successfully.", Logger.DEBUG);
	}

	/**
	 * writes gene record to the o/p data file
	 * @param opGeneFileRecordCount record count
	 * @param geneValues gene values
	 */
	void writeGeneRecordToFile(long opGeneFileRecordCount, List geneValues)
	{
		sbGeneFile = new StringBuffer();
		sbGeneFile.append(opGeneFileRecordCount + FIELD_DELIMITER);

		Iterator iterator = geneValues.iterator();
		Object value = null;
		while (iterator.hasNext())
		{
			value = iterator.next();
			if (value != null)
				sbGeneFile.append(value + FIELD_DELIMITER);
			else
				sbGeneFile.append(FIELD_DELIMITER);
		}
		sbGeneFile.append("\n");

		try
		{
			opGeneFile.write(sbGeneFile.toString());
			opGeneFile.flush();
		}
		catch (IOException e)
		{
			Logger.log("Failed to create an output file in current directory. " + e.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e);
		}
	}

	/**
	 * 
	 * @param opMrnaFileRecordCount
	 * @param messengerRNAValues
	 */
	void writeMrnaRecordToFile(long opMrnaFileRecordCount, List messengerRNAValues)
	{
		sbMrnaFile = new StringBuffer();
		sbMrnaFile.append(opMrnaFileRecordCount + FIELD_DELIMITER);

		Iterator iterator = messengerRNAValues.iterator();
		Object value = null;
		while (iterator.hasNext())
		{
			value = iterator.next();
			if (value != null)
				sbMrnaFile.append(value + FIELD_DELIMITER);
			else
				sbMrnaFile.append(FIELD_DELIMITER);
		}
		sbMrnaFile.append("\n");

		try
		{
			opMrnaFile.write(sbMrnaFile.toString());
			opMrnaFile.flush();
		}
		catch (IOException e2)
		{
			Logger.log("Failed to create an output file in current directory. " + e2.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e2);
		}
	}

	/**
	 * 
	 * @param opProteinFileRecordCount
	 * @param proteinValues
	 */
	void writeProteinRecordToFile(long opProteinFileRecordCount, List proteinValues)
	{
		sbProteinFile = new StringBuffer();
		sbProteinFile.append(opProteinFileRecordCount + FIELD_DELIMITER);

		Iterator iterator = proteinValues.iterator();
		Object value = null;
		while (iterator.hasNext())
		{
			value = iterator.next();
			if (value != null)
				sbProteinFile.append(value + FIELD_DELIMITER);
			else
				sbProteinFile.append(FIELD_DELIMITER);
		}
		sbProteinFile.append("\n");

		try
		{
			opProteinFile.write(sbProteinFile.toString());
			opProteinFile.flush();
		}
		catch (IOException e2)
		{
			Logger.log("Failed to create an output file in current directory. " + e2.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e2);
		}
	}

	/**
	 * 
	 * @param opGenomicIdentifierSetFileRecordCount
	 * @param geneId
	 * @param mrnaId
	 * @param proteinId
	 */
	void writeGenomicIdentifierSetToFile(long opGenomicIdentifierSetFileRecordCount, long geneId,
			long mrnaId, long proteinId)
	{
		sbGenomicIdentifierSetFile = new StringBuffer();
		sbGenomicIdentifierSetFile.append(opGenomicIdentifierSetFileRecordCount + FIELD_DELIMITER);
		if (geneId != -1)
		{
			sbGenomicIdentifierSetFile.append(geneId + FIELD_DELIMITER);
		}
		else
		{
			sbGenomicIdentifierSetFile.append("0" + FIELD_DELIMITER);
		}
		if (mrnaId != -1)
		{
			sbGenomicIdentifierSetFile.append(mrnaId + FIELD_DELIMITER);
		}
		else
		{
			sbGenomicIdentifierSetFile.append("0" + FIELD_DELIMITER);
		}
		if (proteinId != -1)
		{
			sbGenomicIdentifierSetFile.append(proteinId + FIELD_DELIMITER);
		}
		else
		{
			sbGenomicIdentifierSetFile.append("0" + FIELD_DELIMITER);
		}
		sbGenomicIdentifierSetFile.append(FIELD_DELIMITER + "\n");
		try
		{
			opGenomicIdentifierSetFile.write(sbGenomicIdentifierSetFile.toString());
			opGenomicIdentifierSetFile.flush();
		}
		catch (IOException e2)
		{
			Logger.log("Failed to create an output file in current directory. " + e2.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e2);
		}
	}

	/**
	 * 
	 * @param opGene_MrnaFileRecordCount
	 * @param geneId
	 * @param mrnaId
	 */
	void writeGeneMrnaRecordToFile(long opGene_MrnaFileRecordCount, long geneId, long mrnaId)
	{
		try
		{
			opGene_MrnaFile.write(geneId + FIELD_DELIMITER + mrnaId + "\n");
			opGene_MrnaFileRecordCount++;
			opGene_MrnaFile.flush();
		}
		catch (IOException e2)
		{
			Logger.log("Failed to create an output file in current directory. " + e2.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e2);
		}
	}

	/**
	 * 
	 * @param opMrna_ProteinFileRecordCount
	 * @param mrnaId
	 * @param proteinId
	 */
	void writeMrnaProteinRecordToFile(long opMrna_ProteinFileRecordCount, long mrnaId,
			long proteinId)
	{
		try
		{
			opMrna_ProteinFile.write(mrnaId + FIELD_DELIMITER + proteinId + "\n");
			opMrna_ProteinFileRecordCount++;
			opMrna_ProteinFile.flush();
		}
		catch (IOException e)
		{
			Logger.log("Failed to create an output file in current directory. " + e.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e);
		}
	}

	void writeProteinGeneRecordToFile(long opProtein_GeneFileRecordCount, long proteinId,
			long geneId)
	{
		try
		{
			opProtein_GeneFile.write(proteinId + FIELD_DELIMITER + geneId + "\n");
			opProtein_GeneFileRecordCount++;
			opProtein_GeneFile.flush();
		}
		catch (IOException e2)
		{
			Logger.log("Failed to create an output file in current directory. " + e2.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e2);
		}
	}

	void writeSetOntMapping(long genomicIdentifierSetId, long ontId)
	{
		try
		{
			opSet_OntFile.write(genomicIdentifierSetId + FIELD_DELIMITER + ontId + "\n");
			opSet_OntFile.flush();
		}
		catch (IOException e)
		{
			Logger.log("Failed to create an output file in current directory. " + e.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e);
		}
	}

	void writeGenomicIdentifierSetWithAllDSIdsToFile(long opGenomicIdentifierSetFileRecordCount,
			GenomicIdentifierSetOntWithAllDSIds genomicIdentifierSetOnt)
	{
		try
		{
			opGenomicIdentifierSet_OntFile.write(genomicIdentifierSetOnt.toString() + "\n");
			opGenomicIdentifierSet_OntFile.flush();
		}
		catch (IOException e2)
		{
			Logger.log("Failed to create an output file in current directory. " + e2.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e2);
		}
	}

	void writeIntermediateGenomicIdentifierSetToFile(List genomicIdSetValues)
	{
		sbGenomicIdentifierSetFile = new StringBuffer();
		Iterator iterator = genomicIdSetValues.iterator();
		Object value = null;
		while (iterator.hasNext())
		{
			value = iterator.next();
			if (value != null)
				sbGenomicIdentifierSetFile.append(value + FIELD_DELIMITER);
			else
				sbGenomicIdentifierSetFile.append(FIELD_DELIMITER);
		}
		try
		{
			opGenomicIdentifierSet_OntFile.write(sbGenomicIdentifierSetFile.toString() + "\n");
			opGenomicIdentifierSet_OntFile.flush();
		}
		catch (IOException e2)
		{
			Logger.log("Failed to create an output file in current directory. " + e2.getMessage(),
					Logger.DEBUG);
			SummaryExceptionHandler.handleException(e2);
		}
	}

	void uploadOutputDataFiles(int stepCount)
	{
		//Invoke MetadataWriter to write all the calculated metadata into the database
		MetadataWriter metadataWriter = MetadataWriter.getInstance();

		//list of all output metadata files to upload into the database
		List filesToUpload = new ArrayList();

		switch (stepCount)
		{

			case STEP_1_FOR_UNNORMALIZED_SET_ONT :
				filesToUpload.add(GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP1_FILENAME);
				break;

			case STEP_2_FOR_UNIQUE_GENES :
				filesToUpload.add(GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP2_FILENAME);
				filesToUpload.add(GENE_DATA_FILENAME);
				break;

			case STEP_3_FOR_UNIQUE_MRNAS :
				filesToUpload.add(GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP3_FILENAME);
				filesToUpload.add(MRNA_DATA_FILENAME);
				break;

			case STEP_4_FOR_UNIQUE_PROTEINS :
				filesToUpload.add(GENOMIC_IDENTIFIER_SET_ONT_DATA_STEP4_FILENAME);
				filesToUpload.add(PROTEIN_DATA_FILENAME);
				break;

			case STEP_5_FOR_UNIQUE_SET_ONT :
				filesToUpload.add(GENE_MRNA_DATA_FILENAME);
				filesToUpload.add(MRNA_PROTEIN_DATA_FILENAME);
				filesToUpload.add(PROTEIN_GENE_DATA_FILENAME);
				filesToUpload.add(GENOMIC_IDENTIFIER_SET_DATA_FILENAME);
				filesToUpload.add(SET_ONT_DATA_FILENAME);
				break;

			default :
				break;
		}

		//Drop referance constraints on tables, while uploading the data.
		List preUploadQueries = getPreUploadQueries(stepCount);

		//Restore table reference constraints 
		List postUploadQueries = getPostUploadQueries();

		metadataWriter.uploadDataFilesIntoDataBase(filesToUpload, preUploadQueries,
				postUploadQueries);
	}

	private List getPreUploadQueries(int stepCount)
	{
		List preUploadQueries = new ArrayList();
		switch (stepCount)
		{

			case STEP_1_FOR_UNNORMALIZED_SET_ONT :
				preUploadQueries.add("TRUNCATE TABLE GENOMIC_IDENTIFIER_SET_ONT_1_U");
				break;

			case STEP_2_FOR_UNIQUE_GENES :
				preUploadQueries.add("TRUNCATE TABLE GENOMIC_IDENTIFIER_SET_ONT_2_U");
				preUploadQueries.add("TRUNCATE TABLE " + SummaryReflectionUtil.getGeneTableName()
						+ "_U");
				break;

			case STEP_3_FOR_UNIQUE_MRNAS :
				preUploadQueries.add("TRUNCATE TABLE GENOMIC_IDENTIFIER_SET_ONT_3_U");
				preUploadQueries.add("TRUNCATE TABLE " + SummaryReflectionUtil.getMrnaTableName()
						+ "_U");
				break;

			case STEP_4_FOR_UNIQUE_PROTEINS :
				preUploadQueries.add("TRUNCATE TABLE GENOMIC_IDENTIFIER_SET_ONT_4_U");
				preUploadQueries.add("TRUNCATE TABLE "
						+ SummaryReflectionUtil.getProteinTableName() + "_U");
				break;

			case STEP_5_FOR_UNIQUE_SET_ONT :
				preUploadQueries.add("TRUNCATE TABLE GENE_MRNA_U");
				preUploadQueries.add("TRUNCATE TABLE MRNA_PROTEIN_U");
				preUploadQueries.add("TRUNCATE TABLE PROTEIN_GENE_U");
				preUploadQueries.add("TRUNCATE TABLE GENOMIC_IDENTIFIER_SET_U");
				preUploadQueries.add("TRUNCATE TABLE SET_ONT_U");
				break;

			default :
				break;
		}
		return preUploadQueries;
	}

	private List getPostUploadQueries()
	{
		List postUploadQueries = new ArrayList();
		return postUploadQueries;
	}
}