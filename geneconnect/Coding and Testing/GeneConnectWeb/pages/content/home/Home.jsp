<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<!-- Imports -->
<%@
      page language="java" contentType="text/html"
	import="edu.wustl.geneconnect.util.global.GCConstants"
	import="edu.wustl.common.util.global.ApplicationProperties,edu.wustl.common.beans.SessionDataBean"%>
<!-- Imports -->
<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">
	<tr>
    	<td height="100%">
        	<!-- target of anchor to skip menus --><a name="content" />
			<table summary="" cellpadding="0" cellspacing="0" border="0" height="100%">
          		<tr>
		            <td>
        		      	<!-- welcome begins -->
              			<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%" height="100%">
			                <tr>
            					<td class="welcomeTitle" height="20" width="100%" colspan="2">
			                		<bean:message key="app.welcomeNote" arg0="<%=ApplicationProperties.getValue("app.name")%>" arg1="<%=ApplicationProperties.getValue("app.version")%>"/>
								</td>
							</tr>
							<tr>
								<td class="formSerialNumberFieldHome" width="55%">
									<p align="justify">The NCI caBIG<sup>TM</sup> project is creating a common, extensible informatics platform that 
										integrates diverse data types and supports interoperable analytic tools. This 
										platform will allow research groups to tap into the rich collection of emerging 
										cancer research data while supporting their individual investigations. 
										However, because many software applications utilize non-overlapping sets of genomic 
										identifiers in their object models, they won't interoperate. <b>GeneConnect</b> 
										is a caBIG<sup>TM</sup> mapping service that makes this interoperability possible by 
										interlinking <b>approved</b> genomic identifiers. These include:</p>
									<p>
										<ul>
											<li>Ensembl Gene ID</li>
											<li>Ensembl Transcript ID</li>
											<li>Ensembl Protein ID</li>
											<li>Entrez Gene ID</li>
											<li>UniGene ID</li>
											<li>GenBank mRNA Accession Number</li>
											<li>GenBank Protein Accession Number</li>
											<li>RefSeq mRNA Accession Number</li>
											<li>RefSeq Protein Accession Number</li>
											<li>UniProtKB Primary Accession Number</li>
										</ul>
									</p>
									<p align="justify">To interlink all of these identifiers, database annotations (either direct or inferred) 
										and an alignment engine have been used to construct pairwise connections, and then 
										all-to-all relationships have been calculated by traversing all possible combinations 
										of edges in the graph (See <b>Figure</b>) using every node as the starting point. For each query, 
										composed of one or more input identifiers and a set of paths that may be traversed, 
										the <b>Path Score</b> and <b>Frequency</b> are calculated.  These are defined as:</p>
									<p align="justify">
										<ul>
											<li><b>Path Score:</b>  Path Score is calculated for each set of genomic identifiers in 
												the result set.  The Path Score is the frequency that a given set of genomic identifiers 
												was obtained across all traversed paths, given the query criteria composed of one or more input 
												identifiers and a set of paths that may be traversed. </li>
											<li><b>Frequency:</b>  Frequency is calculated for each genomic identifier in the result set.  
												The Frequency denotes how often a given genomic identifier was obtained from a given data 
												source across all traversed paths, given the query criteria composed of one or more input 
												identifiers and a set of paths that may be traversed.</li>
										</ul>
									</p>
								</td>
								<td class="formSerialNumberFieldHome" width="45%">
									<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%" height="100%">
										<tr>
											<td >
						                  		<img src="images/GCGraph.gif" alt="ApplicationProperties.getValue("app.name")" border="0" />
			            			      	</td>
										</tr>
										<tr>
											<td>
												<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
													<tr>
														<td colspan="2" align="center" class="formTitle">
															GeneConnect Build Information
														</td>
													</tr>
													<tr>
														<td class="formSerialNumberField" width="50%">Number of pairwise links</td>
														<td class="formField"><%=request.getAttribute(GCConstants.NO_OF_PARIWISE_LINKS)%></td>	
													</tr>	
													<tr>
														<td class="formSerialNumberField">Number of distinct genomic identifier sets</td>
														<td class="formField"><%=request.getAttribute(GCConstants.NO_OF_GI_SETS)%></td>	
													</tr>
													<tr>
														<td class="formSerialNumberField">Number of possible paths through the GeneConnect graph</td>
														<td class="formField"><%=request.getAttribute(GCConstants.NO_OF_GRAPH_PATHS)%></td>	
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td>
												<table summary="" cellpadding="3" cellspacing="0" border="0" width="100%">
													<tr>
														<td colspan="2" align="center" class="formTitle">
															Database Version Information
														</td>
													</tr>
													<tr>
														<td class="formSerialNumberField" width="50%">Ensembl</td>
														<td class="formField">Version 40</td>	
													</tr>
													<tr>
														<td class="formSerialNumberField">UniGene</td>
														<td class="formField">HomoSapiens Build#194 (26-July-2006)</td>	
													</tr>
													<tr>
														<td class="formSerialNumberField">EntrezGene</td>
														<td class="formField">HomoSapiens Build (1-August-2005)</td>	
													</tr>
													<tr>
														<td class="formSerialNumberField">GenBank Nucleotide</td>
														<td class="formField">Data currently not available</td>	
													</tr>
													<tr>
														<td class="formSerialNumberField">GenBank Protein</td>
														<td class="formField">Data currently not available</td>	
													</tr>
													<tr>
														<td class="formSerialNumberField">UniprotKB</td>
														<td class="formField">Version 8.0 Release(30-May-2006)</td>	
													</tr>	
													<tr>
														<td class="formSerialNumberField">RefSeq</td>
														<td class="formField">Release 18</td>	
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>
              			<!-- welcome ends -->
					</td>
					<td valign="top" halign="right">
					<!-- sidebar begins -->
					<!-- sidebar ends -->
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>