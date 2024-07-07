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
        String message = "Please, confirm that you want to change your password by following the link below:\n" + confirmationUrl;

        emailService.sendEmail(recipientAddress, subject, message);
    }
}
