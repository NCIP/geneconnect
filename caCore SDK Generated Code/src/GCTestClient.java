/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ExistsSubqueryExpression;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.DataSource;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.domain.Protein;
import gov.nih.nci.system.applicationservice.ApplicationService;

/**
 * @author sachin_lale
 * @version 1.0
 */

/**
 * GCTestClient.java demonstrates various use cases of GeneConnect application 
 * with using Application Service Layer and building DetachedCriteria for querying the system.
 * 
 */

public class GCTestClient
{

	static ApplicationService appService;

	static void getApplicationService(String url)
	{
		try
		{

			appService = ApplicationService.getRemoteInstance(url);

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Test client throws Exception = " + ex);
		}
	}

	/**
	 * Use case : Basic Genomic ID Search 
	 * Search on one more attribute within a Gene,MessengerRNA or Protein and 
	 * return result from that search as a list of objects of the same class
	 *  
	 *  Query in this Method:
	 *  Search on Gene where ensemblGeneId='ENS2' and get associated MessengerRNA 
	 *  and print GenbankAccession.
	 *   
	 */
	static void querySimple() throws Exception
	{

		/**
		 * Create a DetachedCriteria for Gene with ensemblGeneId=ENS2
		 */
		DetachedCriteria geneCriteria = DetachedCriteria.forClass(Gene.class);
		geneCriteria.add(Restrictions.eq("ensemblGeneId", "ENS2"));

		List resultList = appService.query(geneCriteria, Gene.class.getName());

		for (Iterator iter1 = resultList.iterator(); iter1.hasNext();)
		{
			/** get Gene Object form resultList*/
			Gene gene = (Gene) iter1.next();
			System.out.println("EnsemblGeneId : " + gene.getEnsemblGeneId());
			/** get associated mRNAColelction from Gene*/
			Collection coll = gene.getMessengerRNACollection();
			for (Iterator iter = coll.iterator(); iter.hasNext();)
			{
				MessengerRNA mrna = (MessengerRNA) iter.next();

				/** Print value of GenbankAccession attribute of MessengerRNA object */
				System.out.println("GenbankAccession : " + mrna.getGenbankAccession());
			}
		}

	}

	/**
	 * Use case : Query Based on Confidence 
	 * Search on one more attribute within a Gene,MessengerRNA or Protein class
	 * with a given or higher confidence score (from GenomicIdentifierSet).
	 * Traverse the model to get data from the other classes.
	 *  
	 *  Query in this method:
	 *  Search on Protein where ensemblGeneId='ENS2' AND unigene,ensemblPeptide as output 
	 *  AND confidenceScore > 0.2
	 *  Print Set ID,Confidenscore and associated Gene,mRNA values with this Set 
	 *  
	 */

	static void queryConfScore() throws Exception
	{
		/**
		 * Create Detached for GenomicIdentifierSet Object and add restriction on confidence score
		 * confidenceScore>0.2
		 */
		DetachedCriteria genomicIdSetCriteria = DetachedCriteria
				.forClass(GenomicIdentifierSet.class);
		genomicIdSetCriteria.add(Restrictions.gt("confidenceScore", new Float("0.1")));

		/**
		 * Create Criteria for search on ensemblGeneId = ENS2 AND unigeneAsOutput = true
		 * AND ensemblPeptideAsOutput=true
		 */

		DetachedCriteria geneCriteria = genomicIdSetCriteria.createCriteria("gene");
		geneCriteria.add(Restrictions.eq("ensemblGeneId", "ENS2"));

		geneCriteria.add(Restrictions.eq("unigeneAsOutput", new Boolean(true)));

		DetachedCriteria proteinCriteria = genomicIdSetCriteria.createCriteria("protein");
		proteinCriteria.add(Restrictions.eq("ensemblPeptideAsOutput", new Boolean(true)));
		/**
		 * Execute the Query
		 */
		List resultList = appService.query(genomicIdSetCriteria,
				"edu.wustl.geneconnect.domain.GenomicIdentifierSet");
		System.out.println("Result Size: " + resultList.size());
		for (Iterator iter = resultList.iterator(); iter.hasNext();)
		{

			GenomicIdentifierSet gset = (GenomicIdentifierSet) iter.next();
			/**Print Set Id and Confidence Score*/
			System.out.println("\nSet Id: " + gset.getId() + "  Confidence Score: "
					+ gset.getConfidenceScore() + "\n");
			Gene gene = gset.getGene();
			MessengerRNA mrna = gset.getMessengerRNA();
			Protein protein = gset.getProtein();

			System.out.println("Ensembl Gene ID | UniGene cluster ID | Ensembl Peptide ID");
			System.out
					.println(gene.getEnsemblGeneId() + "           | " + gene.getUnigeneClusterId()
							+ "            | " + protein.getEnsemblPeptideId());

			System.out
					.println("-----------------------------------------------------------------------------------");
		}
	}

	/**
	 * Use case : Query By Node Traversal (With specfying LinkType) 
	 * Search MessengerRNA,Gene or Protein for given order of node traversal and for 
	 * given restriction of frequency.
	 *   
	 * Query in this method:
	 * Search MessengerRNA where ensemblTranscriptId = 'ENST1' AND EnsemblGene,EnsemblPeptide as output 
	 * and the Set must have a path 
	 * EnsemblGene ---Direct---> Entrez Gene ---Direct---> UniGene
	 *  
	 */
	/**
	 * @throws Exception
	 */
	static void querybyNodeTraversal() throws Exception
	{
		/*Detached Criteria for GenomicIdentifier Set object*/
		DetachedCriteria genomicIdSetCriteria = DetachedCriteria
				.forClass(GenomicIdentifierSet.class);

		/**
		 * Create criteria for ONT where Set should contain a ONT as 
		 * EnsemblGene ---Direct---> Entrez Gene ---Direct---> UniGene
		 * and optionally can also specify link type between each data source pair.
		 */

		/**
		 * Create criteria for ONT as:
		 * EnsemblGene ---Direct
		 */
		DetachedCriteria ontCrit = genomicIdSetCriteria
				.createCriteria("orderOfNodeTraversalCollection");
		DetachedCriteria ontCritF = ontCrit.createCriteria("sourceDataSource").add(
				Restrictions.eq("name", "Ensembl Gene"));
		ontCrit.createCriteria("linkType").add(Restrictions.eq("type", "DIRECT"));

		/**
		 * Create criteria for ONT as:
		 * EnsemblGene ---Direct---> Entrez Gene ---Direct
		 */
		DetachedCriteria ontCritF1 = ontCrit.createCriteria("childOrderOfNodeTraversal");

		ontCritF1.createCriteria("sourceDataSource").add(Restrictions.eq("name", "Entrez Gene"));
		ontCritF1.createCriteria("linkType").add(Restrictions.eq("type", "DIRECT"));

		/**
		 * Create criteria for ONT as:
		 * EnsemblGene ---Direct---> Entrez Gene ---Direct---> UniGene
		 */
		DetachedCriteria ontCritF2 = ontCritF1.createCriteria("childOrderOfNodeTraversal");
		DetachedCriteria ontCritF3 = ontCritF2.createCriteria("sourceDataSource");
		ontCritF3.add(Restrictions.eq("name", "UniGene"));
		ontCritF2.add(Restrictions.isNull("childOrderOfNodeTraversal"));

		/**
		 * Create Critria for ensemblTranscriptId = ENST1 AND
		 * ensemblGeneAsOutput = true AND ensemblPeptideAsOutput = true
		 */
		DetachedCriteria mrnaCriteria = genomicIdSetCriteria.createCriteria("messengerRNA");
		mrnaCriteria.add(Restrictions.eq("ensemblTranscriptId", "ENST1"));

		DetachedCriteria geneCriteria = genomicIdSetCriteria.createCriteria("gene");
		geneCriteria.add(Restrictions.eq("ensemblGeneAsOutput", new Boolean(true)));

		DetachedCriteria proteinCriteria = genomicIdSetCriteria.createCriteria("protein");
		proteinCriteria.add(Restrictions.eq("ensemblPeptideAsOutput", new Boolean(true)));

		// load all GenomicIdentifierSet objects with Gene.entrezgeneID = A1 and ONT A->C->D->B

		List resultList = appService.query(genomicIdSetCriteria,
				"edu.wustl.geneconnect.domain.GenomicIdentifierSet");

		System.out.println("Result Size: " + resultList.size());
		for (Iterator iter = resultList.iterator(); iter.hasNext();)
		{
			GenomicIdentifierSet gset = (GenomicIdentifierSet) iter.next();
			System.out.println("**************************************************************");
			System.out.println("Set id: " + gset.getId() + "  Confidence Score: "
					+ gset.getConfidenceScore());
			//			System.out.println("Gid: " + gset.getGene().getEntrezgeneID());
			Collection coll = gset.getOrderOfNodeTraversalCollection();

			/*Get and Print the Order of Node Traveersal associated with this GenomicIdentifierSet*/
			System.out.println("________________________________________________________");
			for (Iterator iter1 = coll.iterator(); iter1.hasNext();)
			{
				System.out.println("ONT Id----DataSource-------LinkType");
				OrderOfNodeTraversal ont = (OrderOfNodeTraversal) iter1.next();

				OrderOfNodeTraversal tempont = ont;
				while (tempont != null)
				{
					LinkType ltype = tempont.getLinkType();
					String linkType = null;
					if (ltype != null)
						linkType = ltype.getType();
					System.out.println(tempont.getId() + "----"
							+ tempont.getSourceDataSource().getName() + "------" + linkType);
					OrderOfNodeTraversal nextont = tempont.getChildOrderOfNodeTraversal();
					tempont = nextont;
				}
				System.out.println("________________________________________________________");
			}
			System.out.println("**************************************************************");
		}
	}

	/**
	 * Use case : Query By Limiting ID Frequency 
	 * Search on one ensemblPeptideId attribute within a Protein 
	 * with a given higher frequency (from GenomicIdentifierData) for Entrez Gene 
	 * data source.
	 *  
	 * Display the result contining Genomic IDs and associated Frequency
	 *   
	 * @throws Exception
	 */
	public static void queryByLimitingIDFrequency() throws Exception
	{

		/**
		 * Create DetachedCriteria for GenomicIdentifierSet with restriction for confidenceScore >=0.2 
		 */
		DetachedCriteria genomicIdSetCriteria = DetachedCriteria
				.forClass(GenomicIdentifierSet.class);

		/**
		 * Create Criteria to search on guven frequency of given data source 
		 */

		DetachedCriteria freqCriteria = genomicIdSetCriteria
				.createCriteria("consensusIdentifierDataCollection");

		freqCriteria.add(Restrictions.gt("frequency", new Float("0.1")));
		freqCriteria.add(Restrictions.gt("frequency", new Float("0.1")));

		/** 
		 * The dataSource value should be one of the Data Source Name
		 * 
		 */
		DetachedCriteria genomicIdCriteria = freqCriteria.createCriteria("genomicIdentifier");

		genomicIdCriteria.add(Restrictions.eq("dataSource", "Ensembl Gene"));
		genomicIdCriteria.add(Restrictions.eq("dataSource", "Entrez Gene"));

		/**
		 * Create Criteria for ensemblGene selected as ouput 
		 */
		DetachedCriteria geneCriteria = genomicIdSetCriteria.createCriteria("gene");
		geneCriteria.add(Restrictions.eq("ensemblGeneAsOutput", new Boolean(true)));

		/**
		 * Create Criteria for search on ensemblPeptideId attribute
		 */
		DetachedCriteria proteinCriteria = genomicIdSetCriteria.createCriteria("protein");
		proteinCriteria.add(Restrictions.eq("ensemblPeptideId", "ENSP1"));

		/**
		 * Create Criteria for refseqmRNA selected as ouput 
		 */
		DetachedCriteria mranCriteria = genomicIdSetCriteria.createCriteria("messengerRNA");
		mranCriteria.add(Restrictions.eq("refseqmRNAAsOutput", new Boolean(true)));

		List resultList = appService.query(genomicIdSetCriteria, GenomicIdentifierSet.class
				.getName());
		System.out.println("ResultSet Size: " + resultList.size());

		for (Iterator iter = resultList.iterator(); iter.hasNext();)
		{
			GenomicIdentifierSet gset = (GenomicIdentifierSet) iter.next();
			/*Print Set Id and Confidence Score*/
			System.out.println("\nSet Id: " + gset.getId() + "  Confidence Score: "
					+ gset.getConfidenceScore() + "\n");
			Gene gene = gset.getGene();
			MessengerRNA mrna = gset.getMessengerRNA();
			Protein p = gset.getProtein();

			System.out.println("Ensembl Gene ID | Ensembl Peptide ID | RefSeq mRNA ID");
			System.out.println(gene.getEnsemblGeneId() + "           | " + p.getEnsemblPeptideId()
					+ "            | " + mrna.getRefseqId());

			System.out
					.println("-----------------------------------------------------------------------------------");
		}
		/*
		 * Print the Genomic identiifer and its frequency throughout the GenomicIdentifierSolution
		 */
		System.out
				.println("Following is  a list of all genomic identifers (occured in this result)and its frequency");
		if (resultList.size() > 0)
		{
			GenomicIdentifierSet set = (GenomicIdentifierSet) resultList.get(0);

			GenomicIdentifierSolution solution = set.getGenomicIdentifierSolution();
			Collection coll = solution.getConsensusIdentifierDataCollection();
			System.out.println("Genomic Identifer\tFrequency");
			for (Iterator iter1 = coll.iterator(); iter1.hasNext();)
			{
				//OrderOfNodeTraversal ont = (OrderOfNodeTraversal)iter1.next();
				ConsensusIdentifierData ont = (ConsensusIdentifierData) iter1.next();
				GenomicIdentifier g = ont.getGenomicIdentifier();
				if (g != null)
					System.out.println("\t" + g.getGenomicIdentifier() + "\t\t\t"
							+ ont.getFrequency());
			}
		}
	}

	/**
	 * Main method which calling a method demonstarting use cases.  
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			getApplicationService(args[0]);
			System.out.println("Use case : Basic Genomic ID Search ");
			querySimple();
			System.out
					.println("===================================================================\n");

			System.out.println("Use case : Query Based on Confidence ");
			queryConfScore();
			System.out
					.println("===================================================================\n");

			System.out.println("Use case : Query By Node Traversal (With specfying LinkType) ");
			querybyNodeTraversal();

			System.out
					.println("===================================================================\n");

			System.out.println("Use case : Query By Limiting ID Frequency ");
			queryByLimitingIDFrequency();
			System.out
					.println("===================================================================");

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}