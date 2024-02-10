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
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "http://localhost:8080/confirmEmail?token=" + token;
        String message = "Please, confirm your email by following the link below:\n" + confirmationUrl;

        emailService.sendEmail(recipientAddress, subject, message);
    }
}
