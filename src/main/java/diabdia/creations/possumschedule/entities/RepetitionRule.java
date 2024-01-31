package diabdia.creations.possumschedule.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepetitionRule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "days")
    private Integer days;
    @Enumerated(EnumType.STRING)
    @Column(name = "rule", nullable = false)
    private Rules rule;

    public Integer getDays(){
        return Objects.requireNonNullElse(days, 0);
    }

    @Override
    public String toString(){
        return switch (rule){
            case DAY -> "Daily";
            case DAYS -> days + "Days";
            case WEEK -> "Weekly";
            case MONTH -> "Monthly";
            case YEAR -> "Annual";
        };
    }
}
