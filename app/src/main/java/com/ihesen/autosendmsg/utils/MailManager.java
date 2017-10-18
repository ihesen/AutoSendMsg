package com.ihesen.autosendmsg.utils;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 类名
 *
 * @author hesen
 *         实现的主要功能：
 *         创建日期 2017/10/18
 *         修改者，修改日期，修改内容。
 */
public class MailManager {

    private static final String SENDER_NAME = "邮箱";
    private static final String SENDER_PASS = "密码";
    private static final String VALUE_MAIL_HOST = "smtp.163.com";
    private static final String KEY_MAIL_HOST = "mail.smtp.host";
    private static final String KEY_MAIL_AUTH = "mail.smtp.auth";
    private static final String VALUE_MAIL_AUTH = "true";

    public static MailManager getInstance() {
        return InstanceHolder.instance;
    }

    private MailManager() {
    }

    private static class InstanceHolder {
        private static MailManager instance = new MailManager();
    }

    class MailTask extends AsyncTask<Void, Void, Boolean> {

        private MimeMessage mimeMessage;

        public MailTask(MimeMessage mimeMessage) {
            this.mimeMessage = mimeMessage;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Transport.send(mimeMessage);
                return Boolean.TRUE;
            } catch (MessagingException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
        }
    }

    public void sendMail(final String title, final String content) {
        MimeMessage mimeMessage = createMessage(title, content);
        MailTask mailTask = new MailTask(mimeMessage);
        mailTask.execute();
    }

    public void sendMailWithFile(String title, String content, String filePath) {
        MimeMessage mimeMessage = createMessage(title, content);
        appendFile(mimeMessage, filePath);
        MailTask mailTask = new MailTask(mimeMessage);
        mailTask.execute();
    }

    public void sendMailWithMultiFile(String title, String content, List<String> pathList) {
        MimeMessage mimeMessage = createMessage(title, content);
        appendMultiFile(mimeMessage, pathList);
        MailTask mailTask = new MailTask(mimeMessage);
        mailTask.execute();
    }

    private Authenticator getAuthenticator() {
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_NAME, SENDER_PASS);
            }
        };
    }

    private MimeMessage createMessage(String title, String content) {
        Properties properties = System.getProperties();
        properties.put(KEY_MAIL_HOST, VALUE_MAIL_HOST);
        properties.put(KEY_MAIL_AUTH, VALUE_MAIL_AUTH);
        Session session = Session.getInstance(properties, getAuthenticator());
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom(new InternetAddress(SENDER_NAME));
            InternetAddress[] addresses = new InternetAddress[]{new InternetAddress("hesen@beiquan.org")};
            mimeMessage.setRecipients(Message.RecipientType.TO, addresses);
            mimeMessage.setSubject(title);
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(content, "text/html");
            textPart.setText(content,"UTF-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            mimeMessage.setContent(multipart);
            mimeMessage.setSentDate(new Date());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return mimeMessage;
    }

    private void appendFile(MimeMessage message, String filePath) {
        try {
            Multipart multipart = (Multipart) message.getContent();
            MimeBodyPart filePart = new MimeBodyPart();
            filePart.attachFile(filePath);
            multipart.addBodyPart(filePart);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void appendMultiFile(MimeMessage message, List<String> pathList) {
        try {
            Multipart multipart = (Multipart) message.getContent();
            for (String path : pathList) {
                MimeBodyPart filePart = new MimeBodyPart();
                filePart.attachFile(path);
                multipart.addBodyPart(filePart);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
