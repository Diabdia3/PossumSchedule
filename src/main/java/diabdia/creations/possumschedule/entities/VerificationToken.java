package diabdia.creations.possumschedule.entities;

import diabdia.creations.possumschedule.repositories.VerificationTokenRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public VerificationToken(){
        this.expiryDate = calculateExpiryDate(1440);
    }

    public VerificationToken(User user, String token){
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(1440);
    }

    public VerificationToken(User user, String token, int minutesToExpire){
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate(minutesToExpire);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
