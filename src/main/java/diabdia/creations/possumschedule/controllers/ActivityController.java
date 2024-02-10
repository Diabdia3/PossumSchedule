package diabdia.creations.possumschedule.controllers;

import diabdia.creations.possumschedule.entities.Activity;
import diabdia.creations.possumschedule.entities.RepetitionRule;
import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.repositories.ActivityRepository;
import diabdia.creations.possumschedule.repositories.RepetitionRepository;
import diabdia.creations.possumschedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/a")
public class ActivityController {
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private RepetitionRepository repetitionRepository;
    @Autowired
    private UserService userService;

    private User user;

    private User getUser(){
        if(user != null)
            return user;
        else
            user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        return user;
    }

    @GetMapping("/activities")
    public String testAll(Model model){
        List<Activity> activities = activityRepository.findByDateSortByStartTime(getUser().getId(), LocalDate.now().atStartOfDay(), LocalDate.now().plusDays(1).atStartOfDay());
        model.addAttribute("activities", activities);
        model.addAttribute("current", getCurrentIndex(activities));
        model.addAttribute("today", LocalDate.now());
        String today =  LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy", Locale.ENGLISH));
        model.addAttribute("todaysDate", today);
        return "activities/allActivities";
    }

    @GetMapping("/activities/{date}")
    public String testAllDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model){
        List<Activity> activities = activityRepository.findByDateSortByStartTime(getUser().getId(), date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        model.addAttribute("activities", activities);
        model.addAttribute("today", date);
        model.addAttribute("current", getCurrentIndex(activities));
        String today =  date.format(DateTimeFormatter.ofPattern("EEEE dd-MM-yyyy", Locale.ENGLISH));
        model.addAttribute("todaysDate", today);
        return "activities/allActivities";
    }

    @GetMapping("/activityForm")
    public String activityForm(Model model) {
        Activity activity = new Activity();
        activity.setStartTime(LocalDateTime.now());
        activity.setEndTime(LocalDateTime.now().plusHours(1));
        activity.setDescription("");
        activity.setName("");
        model.addAttribute("activity", activity);
        return "activities/activityForm";
    }

    @PostMapping("/activityForm")
    public String activityForm(@ModelAttribute Activity activity, Model model) {
        try {
            activity.setUser(getUser());
            if(activity.getRepetition() != null){
                RepetitionRule rule = activity.getRepetition();
                List<RepetitionRule> rules = repetitionRepository.findByRuleAndDays(rule.getRule().toString(), rule.getDays());
                if(rules.isEmpty())
                    repetitionRepository.save(rule);
                else
                    activity.getRepetition().setId(rules.get(0).getId());
            }
            activityRepository.save(activity);
            model.addAttribute("added", "successfully");
        } catch (Exception e) {
            model.addAttribute("added", "error");
            model.addAttribute("activity", activity);
            e.printStackTrace();
            return "activities/activityForm";
        }
        return activityForm(model);
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeActivity(@RequestParam("activityId") int activityId) {
        try{
            Activity activity =  activityRepository.findById(activityId).orElse(null);
            if(!activity.getUser().getId().equals(getUser().getId()))
                throw new AccessDeniedException("403");
            activityRepository.deleteById(activityId);
        } catch (Exception e){
            if(e.getClass() != AccessDeniedException.class)
                e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Activity removed successfully.");
    }

    @PostMapping("/edit")
    public ResponseEntity<String> editActivity(@RequestParam("id") int id, @RequestParam("name") String name, @RequestParam("description") String description,
                                               @RequestParam("startTime") LocalDateTime startTime, @RequestParam("endTime") LocalDateTime endTime,
                                               @RequestParam("repetition") Optional<String> repetition, @RequestParam("repetitionDays") Optional<Integer> repetitionDays) {
        try{
            Activity activity = activityRepository.findById(id).orElse(null);
            if(!activity.getUser().getId().equals(getUser().getId()))
                throw new AccessDeniedException("403");
            activity.setName(name);
            activity.setDescription(description);
            activity.setStartTime(startTime);
            activity.setEndTime(endTime);
            if(repetition.isPresent()) {
                activity.setRepetition(repetition.orElseThrow());
                activity.setRepetitionDays(repetitionDays.orElseThrow());
                RepetitionRule rule = activity.getRepetition();
                rule.setId(null);
                List<RepetitionRule> rules = repetitionRepository.findByRuleAndDays(rule.getRule().toString(), rule.getDays());
                if (rules.isEmpty())
                    repetitionRepository.save(rule);
                else
                    activity.getRepetition().setId(rules.get(0).getId());
            }
            activityRepository.save(activity);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Activity edited successfully.");
    }

    private int getCurrentIndex(List<Activity> activities){
        LocalDateTime now = LocalDateTime.now();
        for(int i = 0; i < activities.size(); i++){
            if(now.isBefore(activities.get(i).getEndTime()))
                return i;
        }
        return 0;
    }
}
