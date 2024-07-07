package diabdia.creations.possumschedule.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeWrapper {
    private String password;
    private String repeatedPassword;
    private String token;
}
