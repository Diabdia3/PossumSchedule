package diabdia.creations.possumschedule.service;

import diabdia.creations.possumschedule.entities.Activity;
import diabdia.creations.possumschedule.entities.RepetitionRule;
import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.repositories.ActivityRepository;
import diabdia.creations.possumschedule.repositories.RepetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ActivityService {
    @Autowired
    public ActivityRepository activityRepository;

    @Autowired
    private RepetitionRepository repetitionRepository;

    public Activity findActivity(int id, int userId){
        Activity activity =  activityRepository.findById(id).orElse(null);
        if(!activity.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        return activity;
    }

    public List<Activity> getTodayActivities(int userId){
        return activityRepository.findByDateSortByStartTime(
                userId,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay());
    }

    public List<Activity> getActivitiesAtDate(int userId, LocalDate date){
        return activityRepository.findByDateSortByStartTime(
                userId,
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay());
    }

    public Activity getNewActivity(){
        Activity activity = new Activity();
        activity.setStartTime(LocalDateTime.now());
        activity.setEndTime(LocalDateTime.now().plusHours(1));
        activity.setDescription("");
        activity.setName("");
        return activity;
    }

    public void saveActivity(Activity activity, User user){
        activity.setUser(user);
        if(activity.getRepetition() != null){
            RepetitionRule rule = activity.getRepetition();
            List<RepetitionRule> rules = repetitionRepository.findByRuleAndDays(rule.getRule().toString(), rule.getDays());
            if(rules.isEmpty())
                repetitionRepository.save(rule);
            else
                activity.getRepetition().setId(rules.get(0).getId());
        }
        activityRepository.save(activity);
    }

    public void removeActivity(int id, int userId){
        Activity activity =  activityRepository.findById(id).orElse(null);
        if(!activity.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        activityRepository.deleteById(id);
    }

    public void editActivity(Activity activity, int userId){
        Activity initialActivity = activityRepository.findById(activity.getId()).orElse(null);
        if(!initialActivity.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        activity.setUser(initialActivity.getUser());
        activityRepository.save(activity);
    }

    public String todayToString(){
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
    }

    public String dateToString(LocalDate date ){
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
    }

    public int getCurrentIndex(List<Activity> activities){
        LocalDateTime now = LocalDateTime.now();
        int size = activities.size();
        if(size == 0)
            return 0;
        for(int i = 0; i < activities.size(); i++){
            if(now.isBefore(activities.get(i).getEndTime()))
                return i;
        }
        return size-1;
    }
}
