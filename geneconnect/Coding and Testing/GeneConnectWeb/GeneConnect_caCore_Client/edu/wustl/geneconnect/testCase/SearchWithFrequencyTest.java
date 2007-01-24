
package edu.wustl.geneconnect.testCase;

import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.domain.ConsensusIdentifierData;
import edu.wustl.geneconnect.domain.EnsemblGene;
import edu.wustl.geneconnect.domain.EnsemblPeptide;
import edu.wustl.geneconnect.domain.EnsemblTranscript;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifier;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.domain.Protein;
import edu.wustl.geneconnect.domain.UniGene;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
/**
 * Query: Unigene,EnsemblPeptide and EnsemblTranscript as output 
 * given EnsemblGene = 'ENSG00000120738' And freuency for Unigene >=0.3
 *
 */

public class SearchWithFrequencyTest extends TestCase
{

	ApplicationService appService;
	List dataList;
	List frequencyList;
	List dataSourceList;
	Map freqMap;
	Float expectedFrequency = new Float("0.3");
	/**
	 * Preapre the List of Map which contains the set of result expected form query.
	 */
	protected void setUp()
	{
//		 Get the instance of ApplicationService
		appService = ApplicationServiceProvider.getRemoteInstance();		dataList = new ArrayList();
		frequencyList = new ArrayList();
		/**
		 * Map of expected genomic identifier values
		 */
		Map dataMap1 = new HashMap();
		dataMap1.put("Ensembl Gene", "ENSG00000120738");
		dataMap1.put("Ensembl Protein", "ENSP00000239938");
		dataMap1.put("Ensembl Transcript", "ENST00000239938");
		dataMap1.put("UniGene", "Hs.326035");
		dataMap1.put("Confidence Score", new Float("0.7692308"));
		dataList.add(dataMap1);
		/**
		 * Prepare the Map conting expected frequency for expected genomic identfier
		 */
		freqMap = new HashMap();
		freqMap.put("ENSP00000239938", new Float("1.0"));
		freqMap.put("ENST00000239938", new Float("1.0"));
		freqMap.put("ENSG00000120738", new Float("1.0"));
		freqMap.put("Hs.326035", new Float("0.7692308"));

	}

	/**
	 * Query: Unigene,EnsemblPeptide and EnsemblTranscript as output 
	 * given EnsemblGene = 'ENSG00000120738' And freuency for Unigene >=0.3
	 *
	 */

	public void test()
	{
		try
		{
			System.out.println("\n\n-------------------------------------------------");
			System.out.println("Executing Search with Frequency criteria Test case");
			/**
			 * Create GenomicIdentifierSet object and set its asscoication with genomic identifer and 
			 * expected output data source values.
			 */			
			GenomicIdentifierSet set = new GenomicIdentifierSet();
			Gene gene = new Gene();
			// 	set EnsemblGene as input  
			gene.setEnsemblGeneId("ENSG00000120738");
			// set Unigene as output	
			gene.setUnigeneAsOutput(new Boolean(true));

			// set EnsemblPeptide as output
			Protein protein = new Protein();
			protein.setEnsemblPeptideAsOutput(new Boolean(true));

			// set EnsemblTranscript as output
			MessengerRNA mrna = new MessengerRNA();
			mrna.setEnsemblTranscriptAsOutput(new Boolean(true));
			/**
			 * Set freuency criteria
			 */
			ConsensusIdentifierData consensusdata = new ConsensusIdentifierData();
			consensusdata.setFrequency(expectedFrequency);
			consensusdata.setGenomicIdentifier(new UniGene());
			Collection consensus = new ArrayList();
			consensus.add(consensusdata);

			set.setGene(gene);
			set.setMessengerRNA(mrna);
			set.setProtein(protein);
			set.setConsensusIdentifierDataCollection(consensus);

			/**
			 * Execute the Query
			 */
			List resultList = appService.search(
					"edu.wustl.geneconnect.domain.GenomicIdentifierSet", set);

			assertTrue(
					"Failed. Returned results size should be equal to 7. Returned result size = "
							+ resultList.size(), resultList.size() == 7);
			/**
			 * Iterate over result set and compare the values with expected list
			 */		
			System.out.println("Set ID \tEnsembl Gene"+ "\t" + "UniGene" + "\t" + "Ensembl Transcript"+ "\t"
					+ "Ensembl Protein" + "\t" + "Confidence Score");		
			for (int i = 0; i < resultList.size(); i++)
			{
				GenomicIdentifierSet returnedSet = (GenomicIdentifierSet) resultList.get(i);
				Gene returnedGene = returnedSet.getGene();
				MessengerRNA returnedmRNA = returnedSet.getMessengerRNA();
				Protein returnedProtein = returnedSet.getProtein();
				String ensemblGeneId = returnedGene.getEnsemblGeneId();
				String unigeneId = returnedGene.getUnigeneClusterId();

				String ensemblTransId = returnedmRNA.getEnsemblTranscriptId();
				String ensemblPeptideId = returnedProtein.getEnsemblPeptideId();

				Float confidence = returnedSet.getConfidenceScore();
				System.out.println(returnedSet.getId()+"\t"+ensemblGeneId + "\t" + unigeneId + "\t" + ensemblTransId + "\t"
						+ ensemblPeptideId + "\t" + confidence);
				Collection ontCollection = returnedSet.getOrderOfNodeTraversalCollection();
				System.out.println("Associated Order of Node traversal with Set: " +returnedSet.getId());
				int k=1;
				for (Iterator iter1 = ontCollection.iterator(); iter1.hasNext();)
				{
					
					OrderOfNodeTraversal ont = (OrderOfNodeTraversal) iter1.next();
					List ontList = new ArrayList();
					OrderOfNodeTraversal tempont = ont;
					while (tempont != null)
					{
						LinkType ltype = tempont.getLinkType();
						String linkType = null;
						ontList.add(tempont.getSourceDataSource().getName());
						if (ltype != null)
						{
							ontList.add(ltype.getType());
						}	
						
						OrderOfNodeTraversal nextont = tempont.getChildOrderOfNodeTraversal();
						tempont = nextont;
					}
					int j=0;
					if(ontList.size()>0)
					{
						System.out.print("\t"+k+": ");
						k++;
					}	
					for(j=0;j<ontList.size()-1;j++)
					{
						System.out.print(ontList.get(j)+"--");
					}
					if(j>0)
					{
						System.out.println(ontList.get(j));
					}
				}
				
				boolean equalAll = false;
				/**
				 * Iterate over the expected list and find is the result is as expected
				 * if not thow error
				 */
				for (int j = 0; j < dataList.size(); j++)
				{
					Map dataMap = (Map) dataList.get(j);
					String ensGId = (String) dataMap.get("Ensembl Gene");
					String uniGId = (String) dataMap.get("UniGene");
					String ensTId = (String) dataMap.get("Ensembl Transcript");
					String ensPId = (String) dataMap.get("Ensembl Protein");
					Float conf = (Float) dataMap.get("Confidence Score");
					if (ensemblGeneId.equalsIgnoreCase(ensGId)
							&& unigeneId.equalsIgnoreCase(uniGId)
							&& ensemblTransId.equalsIgnoreCase(ensTId)
							&& ensemblPeptideId.equalsIgnoreCase(ensPId))
					{
						equalAll = true;
					}
				}

				assertTrue("Retreived data doesnot match with any expected data list.", equalAll);

			}
			/**
			 * Compare the frequeny of result with expected 
			 */
			GenomicIdentifierSet returnedSet = (GenomicIdentifierSet) resultList.get(0);
			GenomicIdentifierSolution solution = returnedSet.getGenomicIdentifierSolution();
			List consensusCollection = (List) solution.getConsensusIdentifierDataCollection();
			assertTrue("consensusCollection ", consensusCollection.size() > 0);
			for (int i = 0; i < consensusCollection.size(); i++)
			{
				ConsensusIdentifierData data = (ConsensusIdentifierData) consensusCollection.get(i);
				Float frequency = data.getFrequency();
				GenomicIdentifier genomicIdentifier = data.getGenomicIdentifier();
				if ((genomicIdentifier instanceof UniGene)
						|| (genomicIdentifier instanceof EnsemblGene)
						|| (genomicIdentifier instanceof EnsemblTranscript)
						|| (genomicIdentifier instanceof EnsemblPeptide))
				{

					assertTrue("Frequency doesnot match with expected value.", frequency
							.floatValue() > expectedFrequency.floatValue());

				}
			}

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			fail("Exception: " + ex.getMessage());
		}

	}
}
