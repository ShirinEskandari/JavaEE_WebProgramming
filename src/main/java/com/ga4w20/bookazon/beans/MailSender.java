package com.ga4w20.bookazon.beans;


import java.io.Serializable;
import java.time.LocalDateTime;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import jodd.mail.Email;
import jodd.mail.MailServer;
import jodd.mail.SendMailSession;
import jodd.mail.SmtpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Action bean that sends email
 *
 * @author Ken Fogel
 */
@Named
@SessionScoped
public class MailSender implements Serializable {

    private final static Logger LOG = LoggerFactory.getLogger(MailSender.class);

    @Inject
    private MailBean mailBean;

    private final String emailSender;
    private final String smtpPassword;
    private final String smtpServerName;

    /**
     * Constructor that retrieves the email credentials from the web.xml
     */
    public MailSender() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        emailSender
                = ctx.getExternalContext().getInitParameter("emailSender");
        smtpPassword
                = ctx.getExternalContext().getInitParameter("smtpPassword");
        smtpServerName
                = ctx.getExternalContext().getInitParameter("smtpServerName");
    }

    /**
     * Standard send routine using Jodd. Jodd knows about GMail so no need to
     * include port information
     */
    public void sendEmail() {

        // Create am SMTP server object
        SmtpServer smtpServer = MailServer.create()
                .ssl(true)
                .host(smtpServerName)
                .auth(emailSender, smtpPassword)
                //.debugMode(true)
                .buildSmtpMailServer();

        // Using the fluent style of coding create a plain and html text message
        Email email = Email.create().from(emailSender)
                .to(mailBean.getSendTo())
                .subject(mailBean.getSubject())
                .textMessage(mailBean.getPlainTextMsg() + "\n\n" + LocalDateTime.now())
                .htmlMessage(mailBean.getHtmlTextMsg());

        // Like a file we open the session, send the message and close the
        // session. Session automatically closed by using try-with-resources
        try ( // A session is the object responsible for communicating with the server
                SendMailSession session = smtpServer.createSession()) {
            // Like a file we open the session, send the message and close the
            // session
            session.open();
            session.sendMail(email);
            LOG.info("Email sent");
        }
    }
}
