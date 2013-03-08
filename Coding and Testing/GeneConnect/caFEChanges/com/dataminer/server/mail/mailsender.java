/*L
 * Copyright Washington University at St. Louis
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/geneconnect/LICENSE.txt for details.
 */

/**
 *<p>Copyright: (c) Washington University, School of Medicine 2005.</p>
 *<p>Company: Washington University, School of Medicine, St. Louis.</p>
 *<p>ClassName: java com.dataminer.server.mail.mailsender</p> 
 */
package com.dataminer.server.mail;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.dataminer.server.globals.Constants;
import com.dataminer.server.globals.Variables;
import com.dataminer.server.log.Logger;


/**
 * Description: Mail sending module used to send mails to specified addresses. 
 * @author Anuj Tiwari
 * @version 1.0
 */
public class mailsender
{
	private String ccAddress;
	private boolean ccFieldSet = false;
	private static String fileSep = System.getProperty("file.separator");
	/**
	 * Set CC address name
	 * @param address mail address to add in CC
	 */
	public void setCCAddress(String address)
	{
		this.ccAddress = address;
		this.ccFieldSet = true;
	}
	
	/**
	 * Used to send the mail with given parameters.
	 * @param to "To" Address for sending the mail
	 * @param from "From" Address for sending the mail
	 * @param password Password for  sending the mail
	 * @param host "Host" from where to send the mail
	 * @param subject "Subject" of the mail
	 * @param body "Body" of the mail
	 * @return true if mail was successfully sent, false if it fails
	 */
	public boolean sendmail(String to, String from, String password,String host,String subject, String body)
	{
		
		/**  create some properties and get the default Session*/
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.auth", "true");
		String cwd = System.getProperty("user.dir");
		
		String fileName =  cwd + fileSep + "Logs" + fileSep + Constants.loggerFileName;
		String statusfileName = cwd + Constants.statusFileName;
		Session session = Session.getDefaultInstance(props,new MailAuthenticator(from, password));
		
		session.setDebug(false);
		
		try
		{
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = {new InternetAddress(to)};
			
			msg.setRecipients(Message.RecipientType.TO, address); 
			
			if((true == ccFieldSet)&&(ccAddress != null)) 
			{
				msg.setRecipients(Message.RecipientType.CC, ccAddress);
			}
			msg.setSubject(subject);
			
			msg.setSentDate(new Date()); 
			
			/** create and fill the first message part*/
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(body);
			
			MimeBodyPart mbp2 = new MimeBodyPart();
			/** Attach the Logger file to the message*/
			FileDataSource fds = new FileDataSource(fileName);
			mbp2.setDataHandler(new DataHandler(fds));
			mbp2.setFileName(Constants.loggerFileName);
			
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(messageBodyPart);
			mp.addBodyPart(mbp2);
			
			if(false == Variables.createDBSchema)
			{
				/** filestatus.txt to be added.*/
				MimeBodyPart mbp3 = new MimeBodyPart();
				FileDataSource fds2 = new FileDataSource(statusfileName);
				mbp3.setDataHandler(new DataHandler(fds2));
				mbp3.setFileName(Constants.statusFileName);
				mp.addBodyPart(mbp3);
			}
			/** add the Multipart to the message*/
			msg.setContent(mp);
			
			/** send the message*/
			Transport.send(msg);
			Logger.log(Constants.statusFileName + " attached to mail.",Logger.INFO);
		}
		catch (MessagingException mex)
		{
			Logger.log("Unable to send mail to: " + to,Logger.WARNING);
			Logger.log("Exception= " + mex.getMessage(),Logger.WARNING);
			Exception ex = null;
			if ((ex = mex.getNextException()) != null)
			{
				Logger.log("More Exception= " + ex.getMessage(),Logger.WARNING);
			}
			return false;
		}
		
		return true;
		
	}
	
	
	
}