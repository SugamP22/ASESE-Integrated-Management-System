package ui.models.smtp;

import authentication.AuthenticationService;
import smtp.SmtpException;

/**
 * Utility class to retrieve the SMTP credentials for the currently authenticated user.
 * Provides static methods to access user identity and security tokens.
 */
public class SmtpCredentials {

    /**
     * Retrieves the email address of the currently logged-in user.
     * * @return the current user's email address
     * @throws SmtpException if no user is currently authenticated
     */
    public static String getUser(){
        if(AuthenticationService.getCurrentUser()==null){
            throw new SmtpException("no user connected");
        }
        return AuthenticationService.getCurrentUser().getEmail();
    }

    /**
     * Retrieves the security token required for SMTP authentication.
     * * @return the current user's email token
     * @throws SmtpException if no user is currently authenticated
     */
    public static String getToken(){
        if(AuthenticationService.getCurrentUser()==null){
            throw new SmtpException("No user token yet");
        }
        return AuthenticationService.getCurrentUser().getEmailToken();
    }
}