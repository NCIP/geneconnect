/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.ftp.FTP</p> 
 */
package com.dataminer.server.ftp;

import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPClient;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.jobmanager.DPQueue;
import com.dataminer.server.jobmanager.DownLoader;
import com.dataminer.server.log.Logger;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.database.DBManager;

import java.util.Date;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

/**
 * Performs the actual FTP'ing of the data
 * @author Meghana Chitale
 * @version 1.0
 */
public class FTP extends DownLoader
{
	String m_localDir;
	/** FTP client which will be used to get last modified date of the FTPed file*/
	FTPClient m_ftp; 
	/** Used to store the information about the File to be FTPed. It is initialised in constructor*/
	private FileInfo m_ftpInfo = null;
	/** DPQueue of downloaded files*/
	private DPQueue m_filesDownloaded;
	/**
	 * Constructor method
	 * @param dir local directory name
	 */
	public FTP(String dir) 
	{
		m_localDir = dir;
	}
	/**
	 * Constructor method
	 * @param ftpInfo FTP details
	 * @param filesDownLoaded Queue holding list of downloaded file
	 * @param dir local directory name
	 */
	public FTP(FileInfo ftpInfo , DPQueue filesDownLoaded , String dir)
	{
		m_ftpInfo = ftpInfo;
		m_filesDownloaded = filesDownLoaded;
		m_localDir = dir;
	}
	
	/**
	 * Connect to FTP url
	 * @param url Address of FTP site
	 * @param user login name
	 * @param pwd password string
	 * @throws IOException throws exception if error during connection
	 * @throws FTPException throws exception if error during connection
	 */
	public void connectSite(String url, String user, String pwd) throws IOException, FTPException 
	{
		Logger.log("Connection to " + url,Logger.DEBUG);
		m_ftp = new FTPClient(url, 21);
		m_ftp.login(user, pwd);
		Logger.log("Logged in " + url,Logger.INFO);
	}
	
	/** Base class method of Downloader which is implemented by FTP and HTTP downloaders to
	 * download the data in specific manner. After downloading the file it will add it to the
	 * DPQueue filesDownLoaded from which later on files are picked up for parsing
	 */
	public void getData()
	{
		ftpData(m_ftpInfo);
	}
	
	/**
	 * FTP data from the specified site using direct FTP commands. 
	 * @param FileInfo Details of file to download
	 */    
	private void ftpData(FileInfo ftpInfo)
	{
		String s;
		String filename = null;
		int ftpTrials = 0;
		boolean isSuccessFtpData = false;
		while((ftpTrials < 10) && (false == isSuccessFtpData))
		{
			try
			{
				String date = new String();
				Runtime rt = Runtime.getRuntime();
				
				Process p = rt.exec("ftp -n " + ftpInfo.getSite());
				
				BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
				
				PrintWriter out = new PrintWriter(new OutputStreamWriter(p.getOutputStream()));
				/** FTP commands are executed to log in to the site and get file*/
				out.print("user " + ftpInfo.getUser() + " " + ftpInfo.getPasswd() +"\n");
				out.print("binary"+"\n");
				out.print("cd " + ftpInfo.getBaseDir() + "\n");
				for (int i = 0; i < ftpInfo.getFiles().size() ; i++) 
				{
				    out.print("get " + (String) ftpInfo.getFiles().elementAt(i) + "\n");
				}
				out.print("bye\n");
				out.flush();
				p.waitFor();
				int exitVal = p.exitValue();
				Logger.log("Execution FTP command exit status " + exitVal,Logger.DEBUG);
				
				for (int i = 0; i < ftpInfo.getFiles().size() ; i++) 
				{
				    Logger.log("Got file " + ftpInfo.getFiles().elementAt(i),Logger.INFO);
				}
				
				while((s=in.readLine())!=null)
				{
					System.out.println(s+"\n");
				}
				out.close();
				in.close();
				m_filesDownloaded.add(ftpInfo);
				
				int getModDateTrials = 0;
			    boolean isSuccessGetData = false;
			    
				for (int i = 0; i < ftpInfo.getFiles().size() ; i++) 
				{
				    getModDateTrials = 0;
				    isSuccessGetData = false;
				    filename = (String) ftpInfo.getFiles().elementAt(i);
				    
				    while((getModDateTrials < 10) && (false == isSuccessGetData))
				    {
				        try
				        {
				            Logger.log("Trying connect to ftp Site for getting last modified date of " + filename,Logger.INFO);
				            connectSite(ftpInfo.getSite(), ftpInfo.getUser(),ftpInfo.getPasswd());
				            
				            m_ftp.chdir(ftpInfo.getBaseDir());
				            Logger.log("Changed directory to " + ftpInfo.getBaseDir(),Logger.INFO);
				            
				            SimpleDateFormat sdf = new SimpleDateFormat(Variables.dateString);
				            date=sdf.format(m_ftp.modtime(filename));
				            
				            Variables.ftpFiles.put(filename,date);
				            Logger.log("Got last modified date for file " + filename ,Logger.INFO);
				            m_ftp.quit();
				            isSuccessGetData = true;
				        }
				        /** The exception caught in getting last modified date for the ftp ed file will 
				         * not count towards error count as they can occur even through file has been 
				         * successfully ftp ed */
				        catch(IOException e)
				        {
				            Logger.log("IO Exception in getting last modified date of " + filename + e.getMessage(),Logger.FATAL);
				            getModDateTrials++;
				        }
				        catch(FTPException e)
				        {
				            Logger.log("FTP Exception in getting last modified date of " + filename + e.getMessage(),Logger.FATAL);
				            getModDateTrials++;
				        }
				    }
				    if(false == isSuccessGetData)
				    {
				        Logger.log("Failed to get last modified date for file " + filename + " after 10 Trials",Logger.DEBUG);
				    }
				}
				isSuccessFtpData = true;
			}
			catch(IOException e)
			{
				Logger.log("IO Exception in ftp Data " + e.getMessage(),Logger.FATAL);
				ftpTrials++;
			}
			catch(InterruptedException e)
			{
				Logger.log("Interrupted Exception in ftp Data " + e.getMessage(),Logger.FATAL);
				ftpTrials++;
			}
		}
		if(false == isSuccessFtpData)
		{
			Logger.log("Error in FTP ing data after 10 trials ",Logger.DEBUG);
			Variables.errorCount++;
		}
	}
	
	/**
	 *This function determines if files of a particular db need to be ftp'd or not.
	 *@param fileInfo The fileInfo object corresponding to a particular db.
	 *@return boolean true if files of the db need to be ftp'd,false otherwise.
	 */

    static public boolean checkDate(FileInfo fileInfo)
    {
		Date date = new Date();
		try
		{
			DBManager dbInterface = DBManager.getInstance();
			Logger.log("Connection to " + fileInfo.getSite(),Logger.DEBUG);
			FTPClient ftp = new FTPClient(fileInfo.getSite(), 21);
			ftp.login(fileInfo.getUser(), fileInfo.getPasswd());
			Logger.log("Logged in " + fileInfo.getSite(),Logger.INFO);

			ftp.debugResponses(true);
			/** change dir*/
			ftp.chdir(fileInfo.getBaseDir());
			Logger.log("Changed directory to " + fileInfo.getBaseDir(),Logger.INFO);
			/** get all the files*/
			for (int i=0; i< fileInfo.getFiles().size(); i++) 
			{
				String filename = (String) fileInfo.getFiles().elementAt(i);
				/** Now we have the last modified date of the file.*/
				date=ftp.modtime(filename);
				Logger.log("file name " + filename + " date " + date,Logger.INFO);
				/** This  function checks if file needs to be ftp'd. If file already has entry in 
				* server_file_status table with same last modified date then it won't be downloaded*/
				boolean check = dbInterface.dateCheck(date,filename);
				if(true == check)
				{
					ftp.quit();
					return true;
				}
			}/** if even 1 file is supposed to be modified the return true else false.*/
			ftp.quit();
		}
		catch (NullPointerException npex)
		{
			Logger.log("Caught NullPointer exception: " + npex.getMessage(),Logger.DEBUG);
		}
		catch (FTPException ex) 
		{
			Logger.log("Caught FTP exception: " + ex.getMessage(),Logger.DEBUG);
		}
		catch (IOException ioex) 
		{
			Logger.log("Caught IO exception: " + ioex.getMessage(),Logger.DEBUG);
		}
    	return false;
    }

}