
package edu.wustl.geneconnect.testCase;

import edu.wustl.geneconnect.GenomicIdentifierSolution;
import edu.wustl.geneconnect.domain.DataSource;
import edu.wustl.geneconnect.domain.Gene;
import edu.wustl.geneconnect.domain.GenomicIdentifierSet;
import edu.wustl.geneconnect.domain.LinkType;
import edu.wustl.geneconnect.domain.MessengerRNA;
import edu.wustl.geneconnect.domain.OrderOfNodeTraversal;
import edu.wustl.geneconnect.domain.Protein;
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
 * given EnsemblGene = 'ENSG00000120738' 
 * AND ONT = UniGene {INFERRED} Ensembl Gene {DIRECT} Ensembl Transcript {DIRECT} Ensembl Protein
 *
 */
public class SearchWithOntTest extends TestCase
{

	ApplicationService appService;
	List dataList;
	List frequencyList;
	List expectedONTList;
	Map freqMap;
	/**
	 * Preapre the List of Map which contains the set of result expected form query.
	 */
	protected void setUp()
	{
//		 Get the instance of ApplicationService
		appService = ApplicationServiceProvider.getRemoteInstance();
		dataList = new ArrayList();
		frequencyList = new ArrayList();
		/**
		 * Map of expected genomic identifier values
		 */
		Map dataMap1 = new HashMap();
		dataMap1.put("Ensembl Gene", "ENSG00000120738");
		dataMap1.put("Ensembl Protein", "ENSP00000239938");
		dataMap1.put("Ensembl Transcript", "ENST00000239938");
		dataMap1.put("UniGene", "Hs.326035");
		dataMap1.put("Confidence Score", new Float("1.0"));
		dataMap1.put("ONT Count", new Long("1"));
		dataList.add(dataMap1);
		
		/**
		 * Prepare the Map conting expected frequency for expected genomic identfier
		 */
		freqMap = new HashMap();
		freqMap.put("ENSP00000239938", new Float("1.0"));
		freqMap.put("ENST00000239938", new Float("1.0"));
		freqMap.put("ENSG00000120738", new Float("1.0"));
		freqMap.put("Hs.326035", new Float("1.0"));
		/**
		 * Prepare the List conting expected ONT associated the result 
		 */
		expectedONTList = new ArrayList();
		expectedONTList.add("UniGene");
		expectedONTList.add("INFERRED");
		expectedONTList.add("Ensembl Gene");
		expectedONTList.add("DIRECT");
		expectedONTList.add("Ensembl Transcript");
		expectedONTList.add("DIRECT");
		expectedONTList.add("Ensembl Protein");
		
	}

	/**
	 * Query: Unigene,EnsemblPeptide and EnsemblTranscript as output 
	 * given EnsemblGene = 'ENSG00000120738' 
	 * AND ONT = UniGene {INFERRED} Ensembl Gene {DIRECT} Ensembl Transcript {DIRECT} Ensembl Protein
	 *
	 */

	public void testSimpleUseCase()
	{
		try
		{
			System.out.println("\n\n-------------------------------------------------");
			System.out.println("Executing Search with ONT criteria Test case");
			/**
			 * Create GenomicIdentifierSet object and set its asscoication with genomic identifer and 
			 * expected output data source values.
			 */					
			GenomicIdentifierSet set = new GenomicIdentifierSet();
			Gene gene = new Gene();
			// set EnsemblGene as input			
			gene.setEnsemblGeneId("ENSG00000120738");
			// set Unigene as output	
			gene.setUnigeneAsOutput(new Boolean(true));

			// set EnsemblPeptide as output
			Protein protein = new Protein();
			protein.setEnsemblPeptideAsOutput(new Boolean(true));

			// set EnsemblTranscript as output
			MessengerRNA mrna = new MessengerRNA();
			mrna.setEnsemblTranscriptAsOutput(new Boolean(true));

			set.setGene(gene);
			set.setMessengerRNA(mrna);
			set.setProtein(protein);
			/**
			 * Set ont criteria 
			 * UniGene {INFERRED} Ensembl Gene {DIRECT} Ensembl Transcript {DIRECT} Ensembl Protein
			 */
			
			OrderOfNodeTraversal ontA = new OrderOfNodeTraversal();
			DataSource dA = new DataSource();
			LinkType lA = new LinkType();
			dA.setName("UniGene");
			lA.setType("INFERRED");
			ontA.setSourceDataSource(dA);
			ontA.setLinkType(lA);
			
			OrderOfNodeTraversal ontB = new OrderOfNodeTraversal();
			DataSource dB = new DataSource();
			LinkType lB = new LinkType();
			dB.setName("Ensembl Gene");
			lB.setType("DIRECT");
			ontB.setSourceDataSource(dB);
			ontB.setLinkType(lB);
			
			OrderOfNodeTraversal ontC = new OrderOfNodeTraversal();
			DataSource dC = new DataSource();
			LinkType lC = new LinkType();
			dC.setName("Ensembl Transcript");
			lC.setType("DIRECT");
			ontC.setSourceDataSource(dC);
			ontC.setLinkType(lC);
			
			OrderOfNodeTraversal ontD = new OrderOfNodeTraversal();
			DataSource dD = new DataSource();
			dD.setName("Ensembl Protein");
			ontD.setSourceDataSource(dD);
			
			ontA.setChildOrderOfNodeTraversal(ontB);
			ontB.setChildOrderOfNodeTraversal(ontC);
			ontC.setChildOrderOfNodeTraversal(ontD);
			
			Collection ontCollection = new ArrayList();
			ontCollection.add(ontA);
			set.setOrderOfNodeTraversalCollection(ontCollection);
			
			/**
			 * Execute the Query
			 */
			List resultList = appService.search(
					"edu.wustl.geneconnect.domain.GenomicIdentifierSet", set);

			assertTrue(
					"Failed. Returned results size should be equal to 10. Returned result size = "
							+ resultList.size(), resultList.size() == 1);
			/**
			 * Iterate over result set and compare the values with expected list
			 */		
			System.out.println("Ensembl Gene"+ "\t" + "UniGene" + "\t" + "Ensembl Transcript"+ "\t"
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
				System.out.println(ensemblGeneId + "\t" + unigeneId + "\t" + ensemblTransId + "\t"
						+ ensemblPeptideId + "\t" + confidence);
				boolean equalAll = false;
				/**
				 * Iterate over result set and compare the values with expected list
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
				/**
				 * Compare ONT of result set with expected
				 */
				Collection ontColl = returnedSet.getOrderOfNodeTraversalCollection();
				assertTrue("Retreived ONT list size doesnot match with any expected size.", ontColl.size()==1);
				for(Iterator it = ontColl.iterator();it.hasNext();)
				{
					OrderOfNodeTraversal ont = (OrderOfNodeTraversal )it.next();
					OrderOfNodeTraversal  tempOnt = ont;
					List ontList = new ArrayList();
					while(tempOnt!=null)
					{
						ontList.add(tempOnt.getSourceDataSource().getName());
						if(tempOnt.getLinkType()!=null)
						{
							ontList.add(tempOnt.getLinkType().getType());
							
						}
						OrderOfNodeTraversal nextOnt = tempOnt.getChildOrderOfNodeTraversal();
						tempOnt=nextOnt;
					}
					assertTrue("Ont List does not match with expected.",ontList.size()==expectedONTList.size());
					assertTrue("Ont List does not match with expected.",ontList.containsAll(expectedONTList));
				}

			}
			GenomicIdentifierSet returnedSet = (GenomicIdentifierSet) resultList.get(0);
			GenomicIdentifierSolution solution = returnedSet.getGenomicIdentifierSolution();
			List consensusCollection = (List) solution.getConsensusIdentifierDataCollection();
			assertTrue("consensusCollection ", consensusCollection.size() > 0);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			fail("Exception: " + ex.getMessage());
		}

	}
}
