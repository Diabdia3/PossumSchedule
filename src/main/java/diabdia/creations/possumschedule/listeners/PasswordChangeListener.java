package diabdia.creations.possumschedule.listeners;

import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.events.OnPasswordChangeEvent;
import diabdia.creations.possumschedule.service.EmailService;
import diabdia.creations.possumschedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PasswordChangeListener implements ApplicationListener<OnPasswordChangeEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;


    @Override
    public void onApplicationEvent(OnPasswordChangeEvent event) {
        this.emailPasswordChange(event);
    }

    private void emailPasswordChange(OnPasswordChangeEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token, 5);

        String recipientAddress = user.getEmail();
        String subject = "Password Change Confirmation";
        String confirmationUrl = event.getAppUrl() + "http://localhost:8080/passwordChange?token=" + token;
        String text = "Please, follow the link below if you want to change your password:\n" + confirmationUrl;
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
                		<h2 style="font-family: 'Brush Script MT', cursive; color: #ebcf8a; font-size: 30px;">Password Change Confirmation</h2>
                		<p style="font-family: 'Brush Script MT', cursive; color: #ebcf8a; font-size: 20px;">Please, click the link below if you want to change your password:</p>
                		<a style="font-family: 'Brush Script MT', cursive; color: #ebcf8a; font-size: 20px;" href=" 
                """;
        msg+=url;
        msg+= """
                ">Change password</a>
                	</div>
                </body>
                </html>
                """;
        return msg;
    }
}
