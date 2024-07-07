package diabdia.creations.possumschedule.events;

import diabdia.creations.possumschedule.entities.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnPasswordChangeEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnPasswordChangeEvent(
            User user, Locale locale, String appUrl) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }
}
