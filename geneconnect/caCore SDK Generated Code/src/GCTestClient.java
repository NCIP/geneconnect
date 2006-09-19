import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

/**
 * @author sachin_lale
 * @version 1.0
 */

/**
 * GCTestClient.java demonstartes various use cases of GeneConnect application 
 * with using Application Service Layer and building DetachedCriteria for querying the system.
 * 
 */

public class GCTestClient
{

	static ApplicationService appService;
	static
	{
		try
		{

			appService = ApplicationService
					.getRemoteInstance("http://localhost:9091/geneconnect/http/remoteService");

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

		/*
		 * Create a DetachedCriteria for Gene with ensemblGeneId=ENS2
		 */
		DetachedCriteria geneCriteria = DetachedCriteria.forClass(Gene.class);
		geneCriteria.add(Restrictions.eq("ensemblGeneId", "ENS2"));

		List resultList = appService.query(geneCriteria, Gene.class.getName());

		for (Iterator iter1 = resultList.iterator(); iter1.hasNext();)
		{
			/* get Gene Object form resultList*/
			Gene gene = (Gene) iter1.next();
			System.out.println("EnsemblGeneId : "+gene.getEntrezGeneId());
			/*get associated mRNAColelction from Gene*/
			Collection coll = gene.getMessengerRNACollection();
			for (Iterator iter = coll.iterator(); iter.hasNext();)
			{
				MessengerRNA mrna = (MessengerRNA) iter.next();
				
				/*Print value of GenbankAccession attribute of MessengerRNA object */
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
	 *  Search on Protein where ensemblPeptideId='ENSP1' and confidenceScore > 0.2
	 *  Print Set ID,Confidenscore and associated Gene,mRNA values with this Set 
	 *  
	 */
	
	static void queryConfScore() throws Exception
	{
		/*
		 * Create Detached for GenomicIdentifierSet Object and add restriction on confidence score
		 * confidenceScore>0.2
		 */
		DetachedCriteria genomicIdSetCriteria = DetachedCriteria
				.forClass(GenomicIdentifierSet.class);
		genomicIdSetCriteria.add(Restrictions.gt("confidenceScore", new Float("0.2")));

		/*
		 * Create Criteria for search on ensemblPeptideId = ENSP1
		 */

		DetachedCriteria geneCriteria = genomicIdSetCriteria.createCriteria("protein");
		geneCriteria.add(Restrictions.eq("ensemblPeptideId", "ENSP1"));

		/* load all GenomicIdSetCriteria objects with Gene.entrezgeneID = A1 and
		 * GenomicIdentifierSet.confidenceScore > 0.5 
		 */
		List resultList = appService.query(genomicIdSetCriteria,"edu.wustl.geneconnect.domain.GenomicIdentifierSet");
		System.out.println("Result Size: " + resultList.size());
		for (Iterator iter = resultList.iterator(); iter.hasNext();)
		{
			GenomicIdentifierSet gset = (GenomicIdentifierSet) iter.next();
			/*Print Set Id and Confidence Score*/
			System.out.println("\nSet Id: " + gset.getId() + "  Confidence Score: " + gset.getConfidenceScore()+"\n");
			Gene gene = gset.getGene();
			MessengerRNA mrna = gset.getMessengerRNA();
					
			/*Print the Gene and mRNA values associated with this GenomicIdentifierSet*/
			System.out.println("Entrez Gene ID | Ensembl Gene ID | Ensembl Transcript ID | GeneBank mRNA Accession ");
			System.out.println(gene.getEntrezGeneId()+"           | "
								+ gene.getEnsemblGeneId()+"            | "
								+mrna.getEnsemblTranscriptId()+"                 |"
								+mrna.getGenbankAccession());
			System.out.println("-----------------------------------------------------------------------------------");
		}
	}
	
	/**
	 * Use case : Query By Node Traversal (With specfying LinkType) 
	 * Search MessengerRNA,Gene or Protein for given order of node traversal and for 
	 * given restriction of frequency.
	 *   
	 * Query in this method:
	 * Search MessengerRNA where ensemblTranscriptId = 'ENST1' and the Set must have a path 
	 * EnsemblGene ---Direct---> EnsemblTranscript ---Direct---> RefSeqmRNA
	 *  
	 */
	static void querybyNodeTraversal() throws Exception
	{
		/*Detached Criteria for GenomicIdentifier Set object*/
		DetachedCriteria genomicIdSetCriteria = DetachedCriteria
				.forClass(GenomicIdentifierSet.class);
		
			/*
		 * Create criteria for ONT where Set should contain a ONT as 
		 * EnsemblGene ---Direct---> EnsemblTranscript ---Direct---> RefSeqmRNA
		 * and optionally can also specify link type between each data source pair.
		 */
		
		/*
		 * Create criteria for ONT as:
		 * EnsemblGene ---Direct
		 */
		DetachedCriteria ontCrit = genomicIdSetCriteria
				.createCriteria("orderOfNodeTraversalCollection");
		DetachedCriteria ontCritF = ontCrit.createCriteria("sourceDataSource").add(
				Restrictions.eq("name", "EnsemblGene"));
		ontCrit.createCriteria("linkType").add(Restrictions.eq("type","DIRECT"));

		/*
		 * Create criteria for ONT as:
		 * EnsemblGene ---Direct---> EnsemblTranscript---Direct
		 */
		DetachedCriteria ontCritF1 = ontCrit.createCriteria("childOrderOfNodeTraversal");
		ontCritF1.createCriteria("sourceDataSource").add(
				Restrictions.eq("name", "EnsemblTranscript"));
		ontCritF1.createCriteria("linkType").add(Restrictions.eq("type","INFERRED"));
		
		/*
		 * Create criteria for ONT as:
		 * EnsemblGene ---Direct---> EnsemblTranscript ---Direct---> RefSeqmRNA
		 */
		DetachedCriteria ontCritF2 = ontCritF1.createCriteria("childOrderOfNodeTraversal");
		DetachedCriteria ontCritF3 = ontCritF2.createCriteria("sourceDataSource");
		ontCritF3.add(Restrictions.eq("name", "RefSeqmRNA"));
		ontCritF2.add(Restrictions.isNull("childOrderOfNodeTraversal"));
		
		/**/
		DetachedCriteria geneCriteria = genomicIdSetCriteria.createCriteria("messengerRNA");
		geneCriteria.add(Restrictions.eq("ensemblTranscriptId", "ENST1"));

		// load all GenomicIdentifierSet objects with Gene.entrezgeneID = A1 and ONT A->C->D->B

		List resultList = appService.query(genomicIdSetCriteria,
				"edu.wustl.geneconnect.domain.GenomicIdentifierSet");

		System.out.println("Result Size: " + resultList.size());
		for (Iterator iter = resultList.iterator(); iter.hasNext();)
		{
			GenomicIdentifierSet gset = (GenomicIdentifierSet) iter.next();
			System.out.println("**************************************************************");
			System.out.println("Set id: " + gset.getId() + "  Confidence Score: " + gset.getConfidenceScore());
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
					String linkType=null;
					if(ltype!=null)
						linkType=ltype.getType();
					System.out.println(tempont.getId() + "----"
							+ tempont.getSourceDataSource().getName() + "------"
							+ linkType);
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
	 * with a given higher frequency (from GenomicIdentifierData) for RefSeqProtein 
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
		
		genomicIdSetCriteria.add(Restrictions.ge("confidenceScore" , new Float("0.2")));

		/**
		 * Create Criteria to search on guven frequency of given data source 
		 */
		DetachedCriteria freqCriteria = genomicIdSetCriteria
				.createCriteria("consensusIdentifierDataCollection");
		freqCriteria.add(Restrictions.gt("frequency", new Float("0.1")));

		DetachedCriteria identiCrit = freqCriteria.createCriteria("genomicIdentifier");

		/* 
		 * The dataSource value should be one of the GenomicIdentifier class name (excluding package name)
		 * 
		 */
		identiCrit.add(Restrictions.eq("dataSource", "RefSeqProtein"));

		/**
		 * Create Criteria for search on ensemblPeptideId attribute
		 */
		DetachedCriteria geneCriteria = genomicIdSetCriteria.createCriteria("protein");
		geneCriteria.add(Restrictions.eq("ensemblPeptideId", "ENSP1"));

		List resultList = appService.query(genomicIdSetCriteria, GenomicIdentifierSet.class
				.getName());
		System.out.println("ResultSet Size: " + resultList.size());
		for (Iterator iter = resultList.iterator(); iter.hasNext();)
		{
			GenomicIdentifierSet gset = (GenomicIdentifierSet) iter.next();
			/*Print Set Id and Confidence Score*/
			System.out.println("\nSet Id: " + gset.getId() + "  Confidence Score: " + gset.getConfidenceScore()+"\n");
			Gene gene = gset.getGene();
			MessengerRNA mrna = gset.getMessengerRNA();
					
			/*Print the Gene and mRNA values associated with this GenomicIdentifierSet*/
			System.out.println("Entrez Gene ID | Ensembl Gene ID | Ensembl Transcript ID | GeneBank mRNA Accession ");
			System.out.println(gene.getEntrezGeneId()+"           | "
								+ gene.getEnsemblGeneId()+"            | "
								+mrna.getEnsemblTranscriptId()+"                 |"
								+mrna.getGenbankAccession());
			System.out.println("-----------------------------------------------------------------------------------");
		}
		/*
		 * Print the Genomic identiifer and its frequency throughout the GenomicIdentifierSolution
		 */
		System.out.println("Following is  a list of all genomic identifers (occured in this result)and its frequency");
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
	
	public static void main(String[] args)
	{
		try
		{
//			System.out.println("Use case : Basic Genomic ID Search ");
//			querySimple();
//			System.out.println("===================================================================\n");
//			
//			System.out.println("Use case : Query Based on Confidence ");
//			queryConfScore();
//			System.out.println("===================================================================\n");

			System.out.println("Use case : Query By Node Traversal (With specfying LinkType) ");
			querybyNodeTraversal();
			System.out.println("===================================================================\n");
			
//			System.out.println("Use case : Query By Limiting ID Frequency ");
//			queryByLimitingIDFrequency();
//			System.out.println("===================================================================");
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
}