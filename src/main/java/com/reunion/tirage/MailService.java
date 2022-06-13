/**
 * 
 */
package com.reunion.tirage;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @author pmekeze
 *
 */
public class MailService {

    private static MailService instance;

    private Transport transport;

    private MimeMessage message;

    private Session session;

    final String username = "bomintech@gmail.com";
    final String password = "MbomintecH11";

    public MailService() {
        configure();
    }

    public static MailService getInstance() {

        if (instance == null) {
            instance = new MailService();
        }

        return instance;
    }

    public void configure() {

        // 1 -> Création de la session
        Properties properties = new Properties();
        // properties.setProperty("mail.transport.protocol", "smtps");
        // properties.setProperty("mail.smtp.host", "smtp.live.com");
        // properties.setProperty("mail.smtp.port", "587");
        // properties.setProperty("mail.smtp.auth", "true");
        // properties.setProperty("mail.smtp.starttls.enable", "true");

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        message = new MimeMessage(session);

    }

    public void sendMail(String to, String company, String criteria) throws MessagingException {

        String texte = "Bonjour " + to.substring(0, to.indexOf("@")) + ",\n";

        texte = texte
                + "Veuillez cliquer sur le lien ci-dessous pour participer au tirage au sort de la reunion \n      https://www.lagrangien.fr/tirageapp/index.html#/tirage/"
                + company + "/" + to + " \nVotre code de sécurité pour le tirage est : " + criteria
                + " \n Bonne Change et a bientot \n \n Dr Ing Col Cpt Lagrange";

        try {
            message.setText(texte);
            message.setFrom(new InternetAddress(username));
            message.setSubject("Tirage au sort");
            message.addRecipients(Message.RecipientType.TO, to);

            try {
                // transport = session.getTransport("smtps");
                // transport.connect("tiragesausort@hotmail.com",
                // "MbomintecH11");
                transport = session.getTransport();
                transport.connect();
                transport.sendMessage(message, new Address[] { new InternetAddress(to) });

            } catch (MessagingException e) {
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    if (transport != null) {
                        transport.close();
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        } catch (MessagingException e) {
            throw e;
        }

    }
}
