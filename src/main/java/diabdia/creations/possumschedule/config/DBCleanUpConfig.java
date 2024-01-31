package diabdia.creations.possumschedule.config;

import diabdia.creations.possumschedule.repositories.ActivityRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class DBCleanUpConfig {
    @Autowired
    ActivityRepository activityRepository;

    @Transactional
    @Scheduled(cron="@midnight")
    public void deleteOldNonRepeatable(){
        try{
            activityRepository.deleteActivitiesFromTwoWeeksAgo();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
