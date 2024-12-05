package com.koi.api.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * consume  the  RabbitConfiguration queue
 */
@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler(isDefault = true)
    public void sendEmailMessage(Map<String, Object> data){
        String email = (String) data.get("email").toString();
        Integer code = (Integer) data.get("code");
        String type = (String) data.get("type");
        SimpleMailMessage message = switch (type) {
            case "register" -> createMessage("Welcome register",
                    "Your code is" + code +"Please not let others get you code", email);
            case "reset" ->
                createMessage("Your reset email", "Hi, you are reseting your password, code"+code+"validate time is 3 Min", email);
            default ->  null;
        };
        if(message == null) return;
        System.out.println("-========");
        System.out.println(message);
        System.out.println("-========");
        sender.send(message);
    }

    private SimpleMailMessage createMessage(String title, String  content, String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
