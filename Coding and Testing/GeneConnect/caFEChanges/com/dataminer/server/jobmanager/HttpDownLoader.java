/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.jobmanager.HttpDownLoader</p> 
 */

package com.dataminer.server.jobmanager;

import com.dataminer.server.database.DBManager;
import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.log.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to handle file download using http protocol
 * @author Meghana Chitale
 * @version 1.0
 */
public class HttpDownLoader extends DownLoader
{

	/**Downloaded files queue.*/
    private DPQueue m_filesDownLoaded = null;
    
    /**FileInfo instance for the file to be downloaded using HTTP protocol */
    private FileInfo m_httpInfo = null;
    
    /**
     * Constructor method
     * @param fileInfo FileInfo details for http protocol 
     * @param fileDownLoaded Queue holding details of downloaded files
     */
    public HttpDownLoader(FileInfo fileInfo,DPQueue fileDownLoaded)    
    {
        m_httpInfo = fileInfo;            
        m_filesDownLoaded = fileDownLoaded;
        
        if(fileInfo.isProxy())
        {
            System.getProperties().setProperty("setProxy","true");
            System.getProperties().setProperty("http.proxyHost",fileInfo.getProxyHost());
            System.getProperties().setProperty("http.proxyPort",fileInfo.getProxyPort());
        }
    }
    
    /**
     * Method to get details from given http url 
     */
    public void getData()
    {
	    byte buff[] = new byte[256];
	    int noOfBytes;
	    Logger.log("Get data of HTTP downloder entered",Logger.INFO);
	    try
	    {
	        	URL url = new URL(m_httpInfo.getSite());
	            Logger.log("Connecting To Site " + m_httpInfo.getSite() + "...",Logger.DEBUG);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
	            Logger.log("Connected To Site " + m_httpInfo.getSite() + ".",Logger.DEBUG);

				//m_httpInfo.addFile(url.getFile().substring(url.getFile().lastIndexOf('/') + 1));
				
				Logger.log("content length " + con.getContentLength(),Logger.DEBUG);
				
				InputStream inputFileReader = con.getInputStream();

				FileOutputStream file = new FileOutputStream(url.getFile().substring(url.getFile().lastIndexOf('/') + 1));
				
				while((noOfBytes = inputFileReader.read(buff,0,256)) != -1) 
				{
				    file.write(buff,0,noOfBytes);
				}
				file.close();
				
				
				String date = new String();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

                /** There should be separate collection for http files just like local
                * and ftps or rename the variable.*/
                Date tDate = new Date(con.getLastModified());
                date=sdf.format(tDate);
                String fName = new String((String)m_httpInfo.getFiles().firstElement());
                Variables.httpFiles.put(file,date);
                Logger.log("Got file " + fName ,Logger.INFO);
					
				m_filesDownLoaded.add(m_httpInfo);
				Logger.log("Get data of HTTP download over",Logger.INFO);
	    }
	    catch(MalformedURLException exp)
	    {
	        Logger.log("error caught MalformedURLException in HTTPDownloader: " + exp,Logger.WARNING);
	    }

	    catch(IOException exp)
	    {
	        Logger.log("error caught IOException in HTTPDownloader:  " + exp,Logger.WARNING);
	    }
        
    }
    
    /**
     * Method to check the last modified date for the http downloaded file with the date in the
     * database
     * @param httpURL The http url for which to check modified date 
     * @return true if last date of the httpURL is recent than what is there in database
     */   
    static public boolean checkDate(FileInfo fileInfo)
    {
    	String httpURL = fileInfo.getSite();
    	boolean check = false;
    	Logger.log("URL passed to check date function "+httpURL,Logger.INFO);
        URL url = null;
        HttpURLConnection con = null;
        DBManager dbInterface = DBManager.getInstance();
        String fileName = null;
        Date modifiedDate = new Date();
        try
        {            
            url = new URL(httpURL);
            
            Logger.log("Connecting To Site " + httpURL + "...",Logger.INFO);
            
            con = (HttpURLConnection) url.openConnection();           
            
            Logger.log("Connected To Site " + httpURL + ".",Logger.INFO);

        }
        catch(MalformedURLException exp)
        {
            Logger.log("Caught Malformed URL Exception " + exp,Logger.DEBUG);
            return false;
        }
        catch(IOException exp)
        {
            Logger.log("Caught IO Exception " + exp,Logger.DEBUG);
            return false;
        }
        Logger.log("Retriving Last Modified Date from site." + con.getLastModified(),Logger.INFO);
        Logger.log("con date " + con.getDate(),Logger.INFO);
        modifiedDate=new Date(con.getLastModified());
        Logger.log("modified date "+modifiedDate.toString(),Logger.INFO);
        SimpleDateFormat sdf = new SimpleDateFormat(Variables.dateString);
        String dat = sdf.format(modifiedDate);
        
        fileName = url.getFile().substring(url.getFile().lastIndexOf('/') + 1);
        Variables.httpFiles.put(fileName.trim(),dat);
        
        check = dbInterface.dateCheck(modifiedDate,fileName); 
        return check; 
    }

    
}
