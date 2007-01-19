------------------------------------------------------------------------
Executing GeneCOnnect caCore client
------------------------------------------------------------------------
This section describes the step to execute the provided sample client.

Download caCORE sample client
--------------------------------
1. Go to the caBIG CVS download web site 
	http://cabigcvs.nci.nih.gov/viewcvs/viewcvs.cgi/catissuecore/CODE%20AND%20TESTING/Latest%20Code/Installable%20zip/

2. In the GeneConnect_Web_Installable/GeneConnect_caCore_Client/ directory contains the files 
   and libraries that are required to compile and run the caCORE client program.

3. Before using caCORE API configure the host property in remoteService.xml file. 
   File: remoteService.xml
   Parameter 	Description
    {host}	Host URL of the application to which caCORE client will connect. 
		Replace the host parameter with host:port at which the server is configured. 
		For example, '128.252.178.209:9092' to access the GeneConnect public demo site.

4. Running caCORE sample client
   To compile and run the demo client program:
   Step		Action
    1.		Open command prompt and change your current directory to GeneConnect_caCORE_Client folder.
    2.		Run ANT task on command prompt to compile and run the client program. The syntax of ANT task is as follows:

5. ant <target_name>
   The following table describes the different targets of the ANT script:
   Task	        Description
   compile	compile example client application
   runJunit	Run the client program. This target executes the Junit test cases and prepares the output file result.xml.

