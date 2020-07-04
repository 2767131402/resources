package com.zhenglei.utils.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Created by Kevin on 2017/8/8. <br>
 * Email 发送工具类 v1.0 <br>
 * EmailUtils.sendHtml HTMl文本信息(可带附近)<br>
 * EmailUtils.sendText 普通文本信息 <br>
 * EmailUtils.EmailData 邮件数据类 <br>
 * 可以通过一下两种方式构建 <br>
 * EmailUtils.EmailData data = new EmailUtils.EmailData("开发测试1","发送邮件的内容",fileList) <br>
 * EmailUtils.EmailData data = getEmailData("开发测试1","发送邮件的内容",fileList) <br>
 */
//@Component
//@Scope(value = "singleton")
public class EmailUtils {


    private final static JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();
    //@Value("${email.smtp}")
    private static String host;
    //@Value("${email.username}")
    private static String userName;
    //@Value("${email.password}")
    private static String password;
    //@Value("${email.port}")
    private static int port;
    //@Value("${email.smtp.timeout}")
    private static int timeout;
    //@Value("${email.from}")
    private static String from;


    private EmailUtils() {

    }

    static {
        Properties prop = new Properties();
        try {
            prop.load(EmailUtils.class.getClassLoader().getResourceAsStream("asgi-ext.properties"));
            host = prop.getProperty("email.smtp");
            userName = prop.getProperty("email.username");
            password = prop.getProperty("email.password");
            port = Integer.parseInt(prop.getProperty("email.port", "25"));
            timeout = Integer.parseInt(prop.getProperty("email.smtp.timeout", "2500"));
            from = prop.getProperty("email.from");
            setConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //@PostConstruct
    private static void setConfig() {
        senderImpl.setHost(host); //smtp服务器
        senderImpl.setUsername(userName); // 根据自己的情况,设置username
        senderImpl.setPassword(password); // 根据自己的情况, password
        senderImpl.setPort(port);

        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确
        prop.put("mail.smtp.timeout", timeout);
        senderImpl.setJavaMailProperties(prop);
    }

    /**
     * 发送text邮件(多收件人)
     *
     * @param to
     * @param data
     */
    public static void sendText(String[] to, EmailData data) {
        // 建立邮件消息
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setFrom(from);
        mailMessage.setSubject(data.getSubject());
        mailMessage.setText(data.getContent());
        // 发送邮件
        senderImpl.send(mailMessage);
    }

    /**
     * 发送text邮件
     *
     * @param to
     * @param data
     */
    public static void sendText(String to, EmailData data) {
        sendText(new String[]{to}, data);
    }

    /**
     * 发送Html邮件 可带附件 (多收件人)
     *
     * @param to
     * @param data
     * @throws MessagingException
     */
    public static void sendHtml(String[] to, EmailData data) throws MessagingException {
        // 建立邮件消息,发送简单邮件和html邮件的区别
        MimeMessage mailMessage = senderImpl.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "UTF-8");

        // 设置收件人，寄件人
        messageHelper.setTo(to);
        messageHelper.setFrom(from);
        messageHelper.setSubject(data.getSubject());
        // true 表示启动HTML格式的邮件
        messageHelper.setText(data.getContent(), true);
        //附件
        List<File> files = data.getAttachment();
        if (files != null && !files.isEmpty()) {
            for (File f : files) {
                messageHelper.addAttachment(f.getName(), f);
            }
        }

        // 发送邮件
        senderImpl.send(mailMessage);
    }

    /**
     * 发送Html 可带附件 邮件
     *
     * @param to
     * @param data
     * @throws MessagingException
     */
    public static void sendHtml(String to, EmailData data) throws MessagingException {
        sendHtml(new String[]{to}, data);
    }


    /**
     * Email 邮件内容 data
     *
     * @return
     */
    public static EmailData getEmailData() {
        return new EmailData();
    }

    /**
     * Email 邮件内容 data
     *
     * @return
     */
    public static EmailData getEmailData(String subject, String content) {
        return new EmailData(subject, content);
    }

    /**
     * Email 邮件内容 data
     *
     * @return
     */
    public static EmailData getEmailData(String subject, String content, List<File> files) {
        return new EmailData(subject, content, files);
    }

    public static class EmailData {
        private String subject;
        private String content;
        private List<File> attachment;

        public EmailData() {
        }

        public EmailData(String subject, String content) {
            this.subject = subject;
            this.content = content;
        }

        public EmailData(String subject, String content, List<File> attachment) {
            this.subject = subject;
            this.content = content;
            this.attachment = attachment;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public List<File> getAttachment() {
            return attachment;
        }

        public void setAttachment(List<File> attachment) {
            this.attachment = attachment;
        }
    }
}
