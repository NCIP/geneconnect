/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>ClassName: java com.dataminer.server.parser.XmlParser</p> 
 */

package com.dataminer.server.parser;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.dataminer.server.ftp.FileInfo;
import com.dataminer.server.jobmanager.DPQueue;

abstract public class XmlParser extends Parser implements ContentHandler,ErrorHandler,EntityResolver{

 protected static XMLReader xmlReader;

    public XmlParser(FileInfo fileToParse , DPQueue filesParsed)
    {
        super(fileToParse,filesParsed);
        try{
            SAXParserFactory sax = SAXParserFactory.newInstance();
            sax.setValidating(false);
            xmlReader=sax.newSAXParser().getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.setErrorHandler(this);
            xmlReader.setEntityResolver(this);
            xmlReader.setFeature("http://xml.org/sax/features/validation",false);
            //System.out.println("validation = "+xmlReader.getFeature("http://xml.org/sax/features/validation"));
        }
        catch(Exception sax){}

    }
    
    public static Parser getParser(String parserName, FileInfo fileToParse , DPQueue filesParsed)
    {
        Parser parser = null; //parser instance

        if (parserName.equalsIgnoreCase("DBSNP")) {
            //instantiates the DBSNP parser
            parser = new DbSnpParser(fileToParse,filesParsed,xmlReader);
          }else if (parserName.equalsIgnoreCase("GO")) {
              //instantiates the GO parser
              parser = new GOXMLParser(fileToParse,filesParsed,xmlReader);
          }else if(parserName.equalsIgnoreCase("HOMOLOGENE")){
              parser = new HomoloGeneXMLParser(fileToParse,filesParsed,xmlReader);
          }
              

        return parser;
    }
    public void setDocumentLocator(Locator locator){

    }

    public void startDocument() throws SAXException{

    }

    public void startPrefixMapping(String prefix,String uri)
                        throws SAXException{

    }

    public void endPrefixMapping(String prefix)
                      throws SAXException{
    }

    public void endDocument() throws SAXException{

    }

    public void startElement(String namespaceURI,String localName,String qName,
                         Attributes atts)throws SAXException {
    }

    public void endElement(String namespaceURI,String localName,String qName)
                throws SAXException{

    }

    public void characters(char[] ch,int start,int length)
                throws SAXException{

    }

    public void ignorableWhitespace(char[] ch,int start,int length)
                         throws SAXException{
    }

    public void processingInstruction(String target,String data)
                           throws SAXException{
    }

    public void skippedEntity(String name) throws SAXException{
    }

    public void warning(SAXParseException exception)throws SAXException{

    }
    public void error(SAXParseException exception) throws SAXException {

    }
    public void fatalError(SAXParseException exception) throws SAXException{

    }
    public InputSource resolveEntity(String publicId,String systemId)throws SAXException,IOException {

        return new InputSource();
    }

    public InputSource createInputStream(String fileName)throws IOException, FileNotFoundException
    {
      InputStream inputStream;
      InputSource insource=null;
      if(fileName.indexOf("ds_ch")!=-1 && fileName.endsWith(".gz")){
            inputStream = new WorkingGZIPInputStream(new FileInputStream(fileName));
            insource= new InputSource(inputStream);
      }else if (fileName.endsWith(".gz") && fileName.indexOf("go_")!=-1){
            inputStream = new WorkingGZIPInputStream(new FileInputStream(fileName));
            insource= new InputSource(inputStream);
      }else if (fileName.endsWith(".gz") && fileName.indexOf("homologene")!=-1){
            inputStream = new WorkingGZIPInputStream(new FileInputStream(fileName));
            insource= new InputSource(inputStream);
      }else{
            insource = new InputSource(new FileInputStream(fileName));
      }
         return insource;
    }

    /**
     * @param string
     * @param file
     * @return
     */

}
