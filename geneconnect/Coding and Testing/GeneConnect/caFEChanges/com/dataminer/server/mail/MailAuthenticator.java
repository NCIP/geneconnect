package com.dataminer.server.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


/**
 * @author mahesh_nalkande
 */
public class MailAuthenticator extends Authenticator {
    
    String userName = null;
    String password = null;
    
    /**
     * Constructor - Creates Authencator object using provided username and password.
     * @param userName Name of the User 
     * @param password Password 
     */
    public MailAuthenticator(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }
    
    /**
     * Returns Authentcator object required for authentication
     */
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName,password);
    }
}
