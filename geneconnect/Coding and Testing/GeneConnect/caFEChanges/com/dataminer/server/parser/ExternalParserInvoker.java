/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.parser.ExternalParserInvoker</p> 
 */
package com.dataminer.server.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import com.dataminer.server.exception.FatalException;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Utility;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.log.Logger;

/**
 * This class is used to invoke the external parsers which are plug-in to 
 * Annotation parser library.
 * The external parser command is invoke as :
 * 
 * externalParserCommand -f Configuraion_File
 * 
 * -f The Configuration File containing key value pair of   
 * 		BASEDIR
 * 		INPUTFILE
 * 		OUTPUTFILE
 * 		EXTRA_ARGS_IF_ANY
 *  
 * command will be invoked either with -f -option.
 * 
 * The output file should contain the list of filenames 
 * which has to be load (parsed data) in database by caFE's Database loader. 
 * 
 * Base on the configuration it adds the output file names of external parser to queue 
 * or ignore if writeTODB parameter is set to 'true'.
 *      
 * @author       Sachin Lale
 * @version 1.0
 */
public class ExternalParserInvoker extends Parser 
{
	/** Data source being parsed */
	private String m_dbType = null;
	
	/** File Info Object describing file or list of files in the same base directory to be parsed*/
	private FileInfo m_fileInfo;
	protected DPQueue m_filesParsed;
	
		
	/**
	 * Constructor method
	 * @param fileInfo  Information of the file to parse
	 * @param filesParsed List of parsed files
	 */
	public ExternalParserInvoker(FileInfo fileInfo, DPQueue filesParsed) 
	{
		super(fileInfo, filesParsed);
		m_fileInfo=fileInfo;
		m_filesParsed = filesParsed;
		m_dbType = fileInfo.getDatabaseType();
	}
	
	/**
	 * Method to parse file by invoking external parser command.
	 * This method builds the argument ot be pass to external parser and
	 * invokes the command specified in CommandFile.xml 
	 * as <ExternalParser> element.
	 * Then pushes the parsed data file in queue for DataLoader.
	 * @param file Information of the file to parse
	 * @exception FatalException throws exception if error during parsing
	 */
	public void parse(FileInfo file) throws FatalException
	{
	    try
	    {
	        /**Base directory of applcaiton. This is a directory where 
	         * applicatiom stores all output files*/
	        String baseDir = Variables.currentDir;
	        baseDir = baseDir.replace('\\','/');
	        
	        /** Comma separated list of input file names in the same base directory to be passed to the parser*/
	        String fileNames = file.getFileNames();
	        
	        /** File Name prefix to be used for config and output file names */  
	        String fileNamePrefix = (String) file.getFiles().firstElement();
	        
	        /**output file of parser which will contain list of parsed data filr name*/
	        String outputFile = null;
	        
	        /**Configuration file name passed as arg with -f option to parser**/
	        String config_file = fileNamePrefix + ".config";
	        
	        /**List of Extra arguments to parser*/
	        Vector extraArgs= m_fileInfo.getExternalParserArg();
	        
	        FileWriter configWriter = new FileWriter(new File(baseDir+Variables.fileSep+config_file));
	        
	        configWriter.write("BASEDIR="+baseDir+"\n");
	        configWriter.write("INPUTFILE="+fileNames+"\n");
	        if(false==m_fileInfo.IsWriteToDB())
	        {
	            outputFile = fileNamePrefix  + ".op";
	            configWriter.write("OUTPUTFILE=" + outputFile+"\n"); 
	        }
	        for(int i=0;i<extraArgs.size();i++)
	        {
	            configWriter.write((String)extraArgs.get(i)+"\n");
	        }		
	        configWriter.close();
	        StringBuffer parseCommandArguments = new StringBuffer();
	        parseCommandArguments.append(" -f " + baseDir+Variables.fileSep+config_file);
	        
	        String parseCommandToExecute= Variables.currentDir + Variables.fileSep + 
	        m_fileInfo.getExternalParserCommanFile() + parseCommandArguments; 
	        
	        /**
	         * Execute the external parser command
	         */
	        long startTime = System.currentTimeMillis();
	        System.out.println("Executing Parser: "+parseCommandToExecute);
	        
	        Process p = Runtime.getRuntime().exec(parseCommandToExecute);
	        
	        
	        long endTime = System.currentTimeMillis();
	        Logger.log((endTime-startTime)+" ms. required time(in ms.) to parser files " + fileNames,Logger.INFO);
	        
	        
	        /**
	         * Print the output get by executing external parser
	         */
	        InputStream in = p.getInputStream();
	        InputStreamReader inR = new InputStreamReader( in ); 
	        BufferedReader buf = new BufferedReader( inR ); 
	        String line; 
	        
	        while ( ( line = buf.readLine() ) != null ) 
	        { 
	            //System.out.println("O/P: "+line ); 
	        }
	        buf.close();
	        inR.close();
	        in.close();
	        
	        
	        /** Push parsed data filenames in queue*/
	        if(outputFile!=null)
	        {
	            FileInputStream fileInputStream = new FileInputStream(new File(baseDir+Variables.fileSep+outputFile));
	            InputStreamReader fileInReader = new InputStreamReader( fileInputStream );
	            BufferedReader fileReader = new BufferedReader( fileInReader );
	            String fileNameToPushInQueue; 
	            while ( ( fileNameToPushInQueue = fileReader.readLine() ) != null ) 
	            { 
	                m_filesParsed.add(fileNameToPushInQueue);
	                //System.out.println("fileNameToPushInQueue: "+fileNameToPushInQueue ); 
	            }
	            fileReader.close();
	            fileInReader.close();
	            fileInputStream.close();
	        }
	        Utility.deleteFile(baseDir+Variables.fileSep+outputFile);
	        System.out.println("Finished parsing files " + fileNames);
	    }
	    catch(Exception e)
	    {
	        System.out.println("Exception Invoker: " +e.getMessage());
	    }
	}
	
	/**
	 * Empty inplementation of super.Open() 
	 */
	protected void open(String fileName)
	throws IOException, FileNotFoundException 
	{
		// Emtpy implementation
	}
	
	/**
	 * Empty implemtatation of super.close()
	 * @throws IOException Throws exception if error during closing file
	 */
	
	protected void close() throws IOException 
	{
//		 Emtpy implementation

	}
	
	/**
	 * Main Method
	 * @param arg
	 */
	static public void main(String arg[])
	{
		try
		{
			
			long st = System.currentTimeMillis();
			Runtime run = Runtime.getRuntime();
			Process p = run.exec("perl D:/Eclipse/workspace/caFEServer/PerlScripts/Ensembl.pl E:/in.txt E:/ensembl.txt");
			InputStream in = p.getInputStream();
			InputStreamReader inR = new InputStreamReader( in ); 
			   BufferedReader buf = new BufferedReader( inR ); 
			   String line; 
			   while ( ( line = buf.readLine() ) != null ) { 
			     System.out.println( line ); 
			   } 
		   long et = System.currentTimeMillis();
		   System.out.println("Time(in ms.):" + (et-st));
		   StringBuffer cmd = new StringBuffer();
	   		cmd.append("mysqlimport ");
	   		cmd.append("-h " + 
	   				"localhost" + 
	        		" -u root" + 
	                " --password=mysql123" + 
	                " --fields-terminated-by=###" + " " +
	                "new"//+" --ignore"
	                + " -L " 
	                + "E:/ensembl.txt");
	    		
	        System.out.println("cmd "+cmd.toString());
	        //Process sqlldr =  run.exec(cmd.toString());
//	        String filename = "D:/Eclipse/workspace/GeneConnect/sachin.txt";
//	        FileInputStream fileInputStream = new FileInputStream(new File(filename));
//			InputStreamReader fileInReader = new InputStreamReader( fileInputStream );
//			BufferedReader fileReader = new BufferedReader( fileInReader );
//			String fileNameToPushInQueue; 
//			while ( ( fileNameToPushInQueue = fileReader.readLine() ) != null ) 
//			{ 
//				System.out.println("fileNameToPushInQueue: "+fileNameToPushInQueue ); 
//			}
//			fileReader.close();
//			fileInReader.close();
//			fileInputStream.close();
		   System.out.println("Finished");
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
