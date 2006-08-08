/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.jobmanager.FileList</p> 
 */

package com.dataminer.server.jobmanager;

import com.dataminer.server.ftp.FileInfo;

import java.util.Vector;


/**
 * This class stores the list of files to be downloaded. It has a vector which 
 * contains the FileInfo Objects one for each file. The vector of FileInfo 
 * objects is generated by command file Parser. If there multiple file from same 
 * database clubbed in same command file record then single fileInfo objec tis 
 * formed for all those files together We need one FileInfo object for each file 
 * which is to be downloaded and parsed. This is done by this class along with 
 * managing the file list.    
 * @author 		Meghana Chitale
 * @version		1.0
 */

public class FileList
{
	/** This vector stores the list of files which are to be downloaded*/
    private Vector m_filesToDownLoad = null;
    /** This variable is used to move over the fileList. It can only be moved 
     * in one direction using getNext function */
    private int m_index = -1; 
    
    /** Constructor method 
     * @param filesToDownLoad : It is list of FileInfo objects which are 
     * used to initialise the FileList */
    public FileList(Vector filesToDownLoad)
    {        
        
        /**  Modifying the existing FE Server logic of having seperate FileInfo Object for every file.
         * Now All the files in the same base directory will be kept in the single FileInfo object.
         * No separate FileInfo objects will be created and added to vector.
         */
        m_filesToDownLoad =  filesToDownLoad;
  
        //m_filesToDownLoad = new Vector();
        /** This function will create the actual list of FileInfo objects each 
         * of which will have just one file stored in it and not the vector of files */
        //createList(filesToDownLoad);
    }
    
    public int get_No_Of_Files()
    {
        return m_filesToDownLoad.size();
    }
    
    /**One record in the command file corresponds to one fileinfo object in the input vector. 
     * But the command file can have one record storing info about multiple files if they have 
     * same directory. This function will separate all such files into different fileinfo
     * objects so that each fileinfo object has one file to be downloaded. The separation will
     * be done by copying all the other parameters as they are from the main fileInfo object 
     * to craete new separate fileInfo objects which will have one file per fileInfo object
     * in the vecto   m_filesToDownLoad
     * @param filesToDownLoad Vector of files to be downloaded. This vector will be processed and 
     * m_filesToDownLoad member will be populated
     */
    private void createList(Vector filesToDownLoad)
    {
        FileInfo file =  null;
        FileInfo temp = null;
        Vector files = null;
        for(int i = 0 ;i < filesToDownLoad.size() ;i++ )
        {
            file = (FileInfo)filesToDownLoad.get(i);
            /** If there are more than one files in the fileinfo object then that fileInfo
             * object needs to be processed to form multiple objects
             */
            if(file.getNo_Of_Files() > 1)
            {
                files = file.getFiles(); 
                for(int j = 0 ;j < file.getNo_Of_Files() ; j++)
                {
                	/** For each of the files in current fileInfo object create a new FileInfo object*/
                    temp = new FileInfo();
                    
                    /** Copy the info like base directory,username,password,database type etc
                     *  from the FileInfo object being procesed into this new object*/
                    temp.addBaseDir(file.getBaseDir());
                    temp.addDatabaseType(file.getDatabaseType());
                    temp.addInputFormat(file.getInputFormat());
                    temp.addPasswd(file.getPasswd());
                    temp.addUser(file.getUser());
                    temp.addSite(file.getSite());
                    temp.localFile = file.localFile;
                    temp.addFile((String)files.get(j));
                    /**Added for External Parsers*/
                    temp.setIsExternalParser(file.IsExternalParser());
                    temp.setExternalParserCommanFile(file.getExternalParserCommanFile());
                    temp.setIsWriteToDB(file.IsWriteToDB());
                    
                    /** Add the new created FileInfo objet to the vector*/
                    this.m_filesToDownLoad.add(temp);
                }
            }
            else
            {
            	/** If there is single file in the current FileInfo object then nothing needs to 
            	 * be separated. So add that FileInfo object as it is to the vector m_filesToDownLoad
            	 */
                this.m_filesToDownLoad.add(file);
            }
        }
    }

    /**This method will return the fileInfo object at curren location pointed. If the index is at the 
     * end of list then null will be returned indicating end of the list. Each time the list
     * pointer is incremented by 1 
     * @return FileInfo object from vector at the location pointed by the index
     */
    protected synchronized FileInfo getNext()
    {
        if (m_index == m_filesToDownLoad.size() - 1)
        {
            System.out.println("Returned Null From File List");
            return null;
        }
        else
        {
            m_index++;
            return (FileInfo)m_filesToDownLoad.get(m_index);
            
        }
            
    }
}