package diabdia.creations.possumschedule.controllers;

import diabdia.creations.possumschedule.entities.Activity;
import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.service.ActivityService;
import diabdia.creations.possumschedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/a")
public class ActivityController {
    @Autowired
    private ActivityService activityService;
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
    public String showAll(Model model){
        List<Activity> activities = activityService.getTodayActivities(getUser().getId());
        model.addAttribute("activities", activities);
        model.addAttribute("current", activityService.getCurrentIndex(activities));
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("todaysDate", activityService.todayToString());
        return "activities/allActivities";
    }

    @GetMapping("/activities/{date}")
    public String showAllAtDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model){
        List<Activity> activities = activityService.getActivitiesAtDate(getUser().getId(), date);
        model.addAttribute("activities", activities);
        model.addAttribute("today", date);
        model.addAttribute("current", activityService.getCurrentIndex(activities));
        model.addAttribute("todaysDate", activityService.dateToString(date));
        return "activities/allActivities";
    }

    @GetMapping("/activityForm")
    public String activityForm(Model model) {
        model.addAttribute("activity", activityService.getNewActivity());
        return "activities/activityForm";
    }

    @PostMapping("/activityForm")
    public String saveActivity(@ModelAttribute Activity activity, Model model) {
        try {
            activityService.saveActivity(activity, getUser());
            model.addAttribute("result", "successfully");
        } catch (Exception e) {
            model.addAttribute("result", "error");
            model.addAttribute("activity", activity);
            e.printStackTrace();
            return "activities/activityForm";
        }
        return activityForm(model);
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeActivity(@RequestParam("activityId") int activityId) {
        try{
            activityService.removeActivity(activityId, getUser().getId());
        } catch (Exception e){
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Activity removed successfully.");
    }

    @PostMapping("/edit")
    public ResponseEntity<String> editActivity(@RequestParam("id") int id, @RequestParam("name") String name, @RequestParam("description") String description,
                                               @RequestParam("startTime") LocalDateTime startTime, @RequestParam("endTime") LocalDateTime endTime,
                                               @RequestParam("repetition") Optional<String> repetition, @RequestParam("repetitionDays") Optional<Integer> repetitionDays) {
        try{
            activityService.editActivity(id, getUser().getId(), name, description, startTime, endTime, repetition, repetitionDays);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Activity edited successfully.");
    }
}
