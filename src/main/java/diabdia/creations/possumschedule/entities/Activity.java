package diabdia.creations.possumschedule.entities;

import diabdia.creations.possumschedule.enums.Rules;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Getter
@Setter
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private String results = "";
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    @JoinColumn(name="repetition_rule_id", nullable = true)
    private RepetitionRule repetition;
    @ManyToOne(cascade = CascadeType.MERGE,fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    public Activity() {
    }

    public Activity(String name, String description, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getDuration(){
        return Math.abs(ChronoUnit.MINUTES.between(this.startTime, this.endTime));
    }

    public long getStartPoint(){
        return Math.abs(ChronoUnit.MINUTES.between(this.startTime.toLocalDate().atStartOfDay(),this.startTime));
    }

    public String getFormattedStartTime(){
        return startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedEndTime(){
        return endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String repetitionToString(){
        if(repetition == null)
            return "never";
        return switch (repetition.getRule()) {
            case DAY -> "daily";
            case DAYS -> "custom";
            case WEEK -> "weekly";
            case MONTH -> "monthly";
            case YEAR -> "annual";
        };
    }

    public void setRepetitionDays(int days){
        if(days <= 0)
            return;
        if(this.repetition == null || repetition.getRule() != Rules.DAYS)
            return;
        this.repetition = new RepetitionRule();
        if(days == 1)
            repetition.setRule(Rules.DAY);
        else if(days == 7)
            repetition.setRule(Rules.WEEK);
        else {
            this.repetition.setRule(Rules.DAYS);
            this.repetition.setDays(days);
        }
    }

    public void setRepetition(String rule){
        if(rule.equals("never"))
            return;
        this.repetition = new RepetitionRule();
        switch (rule){
            case "daily" -> this.repetition.setRule(Rules.DAY);
            case "weekly" -> this.repetition.setRule(Rules.WEEK);
            case "monthly" -> this.repetition.setRule(Rules.MONTH);
            case "annual" -> this.repetition.setRule(Rules.YEAR);
            case "custom" -> this.repetition.setRule(Rules.DAYS);
        }
    }

    public boolean isTodayOnly(){
        if(repetition != null)
            return true;
        return startTime.toLocalDate().equals(LocalDate.now()) && endTime.toLocalDate().equals(LocalDate.now());
    }

    @Override
    public String toString() {
        return name + "\n" + description + "\n" + startTime + "\n" + endTime + "\n" + repetition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;

        if (!id.equals(activity.id)) return false;
        if (!name.equals(activity.name)) return false;
        if (!description.equals(activity.description)) return false;
        if (!Objects.equals(results, activity.results)) return false;
        if (!startTime.equals(activity.startTime)) return false;
        return endTime.equals(activity.endTime);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + (results != null ? results.hashCode() : 0);
        result = 31 * result + startTime.hashCode();
        result = 31 * result + endTime.hashCode();
        return result;
    }
}
