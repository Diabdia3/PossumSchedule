package diabdia.creations.possumschedule.controllers;

import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.repositories.ActivityRepository;
import diabdia.creations.possumschedule.repositories.TaskRepository;
import diabdia.creations.possumschedule.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final TaskRepository taskRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User user;

    private User getUser(){
        if(user != null)
            return user;
        else
            user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        return "user/login";
    }


    @GetMapping("/signUp")
    public String registrationForm(Model model){
        model.addAttribute("user", new User());
        return "user/signUpForm";
    }

    @PostMapping("/signUp")
    public ModelAndView signUpForm(@ModelAttribute User user, Model model){
        user.setRegistration(LocalDate.now());
        String temp = user.getPassword();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                                      .getRequest();
        request.setAttribute("username", user.getUsername());
        request.setAttribute("password", temp);
        authWithHttpServletRequest(request, user.getUsername(), temp);
        return new ModelAndView("redirect:/tasks/all");
    }

    public void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/userInfo")
    public String userInfo(Model model){
        model.addAttribute("user", getUser());
        model.addAttribute("stats", new Stats(getUser().getId()));
        return "user/userInfo";
    }

    @PostMapping("/updateProfilePicture")
    @ResponseBody
    public ResponseEntity<String> updateProfilePicture(@RequestParam("image") MultipartFile file){
        try {
            InputStream initialStream = file.getInputStream();
            byte[] buffer = new byte[initialStream.available()];
            initialStream.read(buffer);
            String fileName = getUser().getId() + "picture" + "." + FilenameUtils.getExtension(file.getOriginalFilename());
            File targetFile = new File("src/main/resources/static/image/profilePicture/" + fileName);
            OutputStream outStream = new FileOutputStream(targetFile);
            outStream.write(buffer);
            outStream.close();
            user.setProfilePicture(fileName);
            userRepository.save(user);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Image updated successfully.");
    }

    @Getter
    public class Stats{
        private final int completedTasks ;
        private final int unfinishedTasks;
        private final int weeklyActivities;
        private final int remainingWeeklyActivities;

        public Stats(int userId){
            this.completedTasks = taskRepository.getCompletedTasksCount(userId);
            this.unfinishedTasks = taskRepository.getUnfinishedTasksCount(userId);
            this.weeklyActivities = activityRepository.getAllWeeklyActivitiesCount(userId);
            this.remainingWeeklyActivities = activityRepository.getRemainingWeeklyActivitiesCount(userId);
        }
    }
}
