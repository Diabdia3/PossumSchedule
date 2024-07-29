package diabdia.creations.possumschedule.listeners;

import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.events.OnRegistrationCompleteEvent;
import diabdia.creations.possumschedule.service.EmailService;
import diabdia.creations.possumschedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;


    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token, 1440);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "http://localhost:8080/confirmEmail?token=" + token;
        String text = "Please, confirm your email by following the link below:\n" + confirmationUrl;
        String html = formMessage(confirmationUrl);

        emailService.sendEmail(recipientAddress, subject, text, html);
    }

    private String formMessage(String url){
        String msg = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                </head>
                <body style="background: #9A7EA6;">
                	<div style="">
                		<h2 style="font-family: 'Brush Script MT', cursive; color: #ebcf8a; font-size: 30px;">Registration Confirmation</h2>
                		<p style="font-family: 'Brush Script MT', cursive; color: #ebcf8a; font-size: 20px;">Please, confirm your email by clicking the link below:</p>
                		<a style="font-family: 'Brush Script MT', cursive; color: #ebcf8a; font-size: 20px;" href=" 
                """;
        msg+=url;
        msg+= """
                ">Confirm Email</a>
                	</div>
                </body>
                </html>
                """;
        return msg;
    }
}
