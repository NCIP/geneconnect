/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.jobmanager.DownLoaderThread</p> 
 */

package com.dataminer.server.jobmanager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.log.Logger;

/**
 * This is the class that extends the java.lang.Thread class and run 
 * as a thread.The run method of this class calls the methods of 
 * Downloader interface for downloading the data .
 * @author Meghana Chitale
 * @version 1.0 
 */
public class DownLoaderThread extends Thread
{
	/**List of files to be DownLoaded */
	private FileList m_filesToDownLoad = null ;
	
	
	/**Queue which holds the files Downloaded*/
	private DPQueue m_filesDownLoaded = null; 
	
	/**
	 * Constructor method
	 * @param filesToDownLoad List of files to download
	 * @param filesDownLoaded Queue mentaining list of downloaded files
	 */    
	public DownLoaderThread(FileList filesToDownLoad,DPQueue filesDownLoaded )
	{
		m_filesToDownLoad = filesToDownLoad;         
		m_filesDownLoaded = filesDownLoaded;
	}
	
	/**
	 * Retrive FileInfo Object from the filelist and for each create a Downloader 
	 * object and start downloading.
	 */
	public void run()
	{
		//FileInfo object which contains the details of the file to be downloaded 
		FileInfo file = null;	
		//The Downloader object which will Download the files  
		DownLoader downLoader;
		
		Logger.log("In The Run Method Thread Td "+this.getName(),Logger.DEBUG);
		Logger.log("Test for no of files",Logger.DEBUG);
		
		//TODO : this needs to corrected. FileInfo object can have more than one file now.
		Logger.log("No of files to download " + m_filesToDownLoad.get_No_Of_Files(),Logger.DEBUG);
		
		while(( file = m_filesToDownLoad.getNext() )!= null)
		{
			Logger.log("inside run of thread ",Logger.DEBUG);//+this.getId(),Logger.INFO);
			if(file.localFile == false) // Check if Local file or Remote. 
			{
				
				downLoader = DownLoaderFactory.getDownLoader(file,m_filesDownLoaded);
				// Start Downloading
				Logger.log("start get data on thread ",Logger.DEBUG); //+ this.getId(),Logger.INFO);
				downLoader.getData();
			}
			else
			{
				if(!file.getDatabaseType().equalsIgnoreCase("caArray"))
				{
					//storing the last modified date is done by FTP and HTTP downloaders
					//but for the local files it needs to be done here since after parsing
					//the files may be deleted to clean local directory
					String fileName = (String)file.getFiles().firstElement();
					SimpleDateFormat sdf = new SimpleDateFormat(Variables.dateString);//("dd-MMM-yyyy");
					File tempFile=new File(fileName);
					Date date = new Date(tempFile.lastModified());
					String dat=sdf.format(date);
					Logger.log("filename " +tempFile+" modified date " + dat,Logger.DEBUG);
					Variables.localFiles.put(fileName,dat);
				}
				m_filesDownLoaded.add(file); //Add it to queue of DownLoaded Files.
			}
		}
		Logger.log(this.getName() + " Recieved Null Object",Logger.INFO);
	}
}
