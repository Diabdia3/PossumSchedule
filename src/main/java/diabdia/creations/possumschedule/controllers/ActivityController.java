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

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
        model.addAttribute("dayOfTheWeek", LocalDate.now().getDayOfWeek().name().substring(0, 3));
        return "activities/allActivities";
    }

    @GetMapping("/activities/{date}")
    public String showAllAtDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, Model model){
        List<Activity> activities = activityService.getActivitiesAtDate(getUser().getId(), date);
        model.addAttribute("activities", activities);
        model.addAttribute("today", date);
        model.addAttribute("current", activityService.getCurrentIndex(activities));
        model.addAttribute("todaysDate", activityService.dateToString(date));
        model.addAttribute("dayOfTheWeek", date.getDayOfWeek().name().substring(0, 3));
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

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model){
        Activity activity = activityService.findActivity(id, getUser().getId());
        model.addAttribute("activity", activity);
        return "activities/activityEditForm";
    }

    @PostMapping("/edit")
    public String editActivity(@ModelAttribute Activity activity, Model model) {
        try{
            activityService.editActivity(activity, getUser().getId());
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("activity", activity);
            return "activities/activityEditForm";
        }
        return showAll(model);
    }
}
