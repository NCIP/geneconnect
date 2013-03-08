/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

package edu.wustl.geneconnect.testCase;

import edu.wustl.geneconnect.GenomicIdentifierSolution;
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
 * Query: EnsemblPeptide and EnsemblTranscript as output 
 * given EnsemblGene = 'ENSG00000120738 and Unigene=Hs.326035'
 *
 */
public class AdvancedSearchTest extends TestCase
{

	ApplicationService appService;
	List dataList;
	List frequencyList;
	List dataSourceList;
	Map freqMap;
	/**
	 * Preapre the List of Map which contains the set of result expected form query.
	 */
	protected void setUp()
	{
		// Get the instance of ApplicationService
		
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
		dataMap1.put("Confidence Score", new Float("0.7692308"));
		dataList.add(dataMap1);
		/**
		 * Map of expected genomic identifier values
		 */
		Map dataMap2 = new HashMap();
		dataMap2.put("Ensembl Gene", "ENSG00000120738");
		dataMap2.put("Ensembl Protein", "ENSP00000239938");
		dataMap2.put("Ensembl Transcript", "ENST00000239938");
		dataMap2.put("UniGene", "Hs.634208");
		dataMap2.put("Confidence Score", new Float("0.23076923"));
		dataList.add(dataMap2);
		/**
		 * Prepare the Map conting expected frequency for expected genomic identfier
		 */
		freqMap = new HashMap();
		freqMap.put("ENSP00000239938", new Float("1.0"));
		freqMap.put("ENST00000239938", new Float("1.0"));
		freqMap.put("ENSG00000120738", new Float("1.0"));
		freqMap.put("Hs.634208", new Float("0.23076923"));
		freqMap.put("Hs.326035", new Float("0.7692308"));
	}
	/**
	 *  Query: Unigene,EnsemblPeptide and EnsemblTranscript as output 
	 * given EnsemblGene = 'ENSG00000120738'
	 *
	 */
	public void testSimpleUseCase()
	{
		try
		{
			System.out.println("\n\n-------------------------------------------------");
			System.out.println("Excecuting Advanced Search with multiple inputs Test case");
			System.out.println("Query: EnsemblPeptide and EnsemblTranscript as output\ngiven EnsemblGene = 'ENSG00000120738 and Unigene=Hs.326035'\n");
			 
			 
			/**
			 * Create GenomicIdentifierSet object and set its asscoication with genomic identifer and 
			 * expected output data source values.
			 */
			GenomicIdentifierSet set = new GenomicIdentifierSet();
			Gene gene = new Gene();
			// set EnsemblGene as input  
			gene.setEnsemblGeneId("ENSG00000120738");
			gene.setUnigeneClusterId("Hs.326035");
			// set Unigene as output
			//gene.setUnigeneAsOutput(new Boolean(true));

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
			 * Execute the Query
			 */
			List resultList = appService.search(
					"edu.wustl.geneconnect.domain.GenomicIdentifierSet", set);
			System.out.println("resultList: " +resultList.size());
			assertTrue(
					"Failed. Returned results size should be equal to 10. Returned result size = "
							+ resultList.size(), resultList.size() == 7);
			/**
			 * Iterate over result set and compare the values with expected list
			 */
			System.out.println("Set ID \tEnsembl Gene"+ "\t" + "UniGene" + "\t" + "Ensembl Transcript"+ "\t"
					+ "Ensembl Protein" + "\t" + "Path Score");
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
//				Collection ontCollection = returnedSet.getOrderOfNodeTraversalCollection();
//				System.out.println("Associated Order of Node traversal with Set: " +returnedSet.getId());
//				int k=1;
//				for (Iterator iter1 = ontCollection.iterator(); iter1.hasNext();)
//				{
//					
//					OrderOfNodeTraversal ont = (OrderOfNodeTraversal) iter1.next();
//					List ontList = new ArrayList();
//					OrderOfNodeTraversal tempont = ont;
//					while (tempont != null)
//					{
//						LinkType ltype = tempont.getLinkType();
//						String linkType = null;
//						ontList.add(tempont.getSourceDataSource().getName());
//						if (ltype != null)
//						{
//							ontList.add(ltype.getType());
//						}	
//						
//						OrderOfNodeTraversal nextont = tempont.getChildOrderOfNodeTraversal();
//						tempont = nextont;
//					}
//					int j=0;
//					if(ontList.size()>0)
//					{
//						System.out.print("\t"+k+": ");
//						k++;
//					}	
//					for(j=0;j<ontList.size()-1;j++)
//					{
//						System.out.print(ontList.get(j)+"--");
//					}
//					if(j>0)
//					{
//						System.out.println(ontList.get(j));
//					}
//				}

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
