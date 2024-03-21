package Avange.apptip;

import android.os.AsyncTask;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
//Enviando las excepciones a un correo para determinar los bug en la aplicación
public class mailException {
  public static boolean sendMail(String body) {

            final String username = "senly.martin@gmail.com";
            final String password = "mfum jayn ogaw klxd";
            Properties prop = new Properties();
            prop.put("mail.smtp.host", "smtp.gmail.com");
            prop.put("mail.smtp.port", "587");
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true"); //TLS
            Session session = Session.getInstance(prop,new jakarta.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(username, password); }});
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(username));
                message.setRecipients(
                        Message.RecipientType.TO,
                        InternetAddress.parse("senlym2010@gmail.com")
                );
                message.setSubject("AppTip");
                message.setText("Usuario "+Variables.userfire.getEmail()+" experimentó un error \n\n"+ body
                        + "\n\n");
                new Thread(() -> sendi(message)).start();

            } catch (MessagingException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
    public static void sendi(Message message){
        try{
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}

