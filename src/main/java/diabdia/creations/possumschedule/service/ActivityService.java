package diabdia.creations.possumschedule.service;

import diabdia.creations.possumschedule.entities.Activity;
import diabdia.creations.possumschedule.entities.RepetitionRule;
import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.repositories.ActivityRepository;
import diabdia.creations.possumschedule.repositories.RepetitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
                LocalDate.now());
    }

    public List<Activity> getActivitiesAtDate(int userId, LocalDate date){
        return activityRepository.findByDateSortByStartTime(
                userId,
                date);
    }

    public Activity getNewActivity(){
        Activity activity = new Activity();
        activity.setStartTime(LocalDateTime.now());
        activity.setEndTime(LocalDateTime.now().plusHours(1));
        activity.setDescription("");
        activity.setName("");
        return activity;
    }

    @Transactional
    public void saveActivity(Activity activity, User user){
        activity.setUser(user);
        if(activity.getRepetition() != null){
            setRepetition(activity);
        }
        if(activity.getStartTime().getDayOfMonth() == activity.getEndTime().getDayOfMonth())
            activityRepository.save(activity);
        else
            separateActivity(activity);
    }

    private void setRepetition(Activity a){
        RepetitionRule rule = a.getRepetition();
        List<RepetitionRule> rules = repetitionRepository
                .findByRuleAndDays(rule.getRule().toString(), rule.getDays());
        if(rules.isEmpty())
            repetitionRepository.save(rule);
        else
            a.getRepetition().setId(rules.get(0).getId());
    }

    private void separateActivity(Activity activity){
        Activity beforeMidnight = new Activity(activity);
        beforeMidnight.setEndTime(activity.getStartTime().toLocalDate().atTime(23, 59, 59));
        Activity afterMidnight = new Activity(activity);
        afterMidnight.setStartTime(activity.getEndTime().toLocalDate().atStartOfDay());
        activityRepository.save(beforeMidnight);
        activityRepository.save(afterMidnight);
    }

    @Transactional
    public void editActivity(Activity activity, int userId){
        Activity initialActivity = activityRepository.findById(activity.getId()).orElse(null);
        if(!initialActivity.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        activity.setUser(initialActivity.getUser());
        if(activity.getStartTime().getDayOfMonth() == activity.getEndTime().getDayOfMonth())
            activityRepository.save(activity);
        else
            separateActivity(activity);
    }

    @Transactional
    public void removeActivity(int id, int userId){
        Activity activity =  activityRepository.findById(id).orElse(null);
        if(!activity.getUser().getId().equals(userId))
            throw new AccessDeniedException("403");
        activity.setId(id);
        if(activity.getEndTime().toLocalTime().equals(LocalTime.of(23, 59, 59))
           || activity.getStartTime().toLocalTime().equals(LocalTime.of(0,0,0)))
            checkRelated(activity);
       activityRepository.deleteById(id);
    }

    private void checkRelated(Activity activity){
        List<Activity> list = activityRepository.findByNameAndUserSortByStartTime(
                activity.getUser().getId(), activity.getName());
        if(list.size() < 2)
            return;
        for(int i = 0; i < list.size()-1; i++){
            Activity temp = list.get(i);
            Activity related = list.get(i+1);
            if(temp.getEndTime().toLocalTime().equals(LocalTime.of(23, 59, 59))
                    && related.getStartTime().toLocalTime().equals(LocalTime.of(0,0,0))){
                activityRepository.deleteById(temp.getId());
                activityRepository.deleteById(related.getId());
            }
        }
    }

    public String todayToString(){
        return LocalDate.now()
                .format(DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy", Locale.ENGLISH));
    }

    public String dateToString(LocalDate date ){
        return date.format(DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy", Locale.ENGLISH));
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
