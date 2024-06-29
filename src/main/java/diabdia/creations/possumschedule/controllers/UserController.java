package diabdia.creations.possumschedule.controllers;

import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.entities.VerificationToken;
import diabdia.creations.possumschedule.events.OnRegistrationCompleteEvent;
import diabdia.creations.possumschedule.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Calendar;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private User user;

    private User getUser(){
        if(user != null)
            return user;
        else
            user = userService.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
        return user;
    }

    @GetMapping("/")
    public String mainPage(Model model) { return "index"; }

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
    public ModelAndView signUpForm(ModelMap model, @ModelAttribute User user, HttpServletRequest request){
        try {
            User registered = userService.registerUser(user);
            loginUser(user);

            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered,
                    request.getLocale(), appUrl));
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().endsWith("already present."))
                model.addAttribute("error", e.getMessage());
            else
                model.addAttribute("error", "Something went wrong, please try again.");
            model.addAttribute("user", user);
            return new ModelAndView("user/signUpForm", model);
        }

        return new ModelAndView("redirect:/confirmationLink", model);
    }

    @GetMapping("/confirmationLink")
    public String waitForConfirmation(Model model){
        String email = getUser().getEmail().substring(0,2) + "*****" + getUser().getEmail().split("@")[1];
        model.addAttribute("email", email);
        return "user/confirmationLink";
    }

    @PostMapping("/resendLink")
    public ResponseEntity<String> resendLink(HttpServletRequest request){
        try{
            String appUrl = request.getContextPath();
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(getUser(),
                    request.getLocale(), appUrl));
        } catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.ok("Confirmation link was sent to the email.");
    }

    @GetMapping("/confirmEmail")
    public String confirmRegistration(@RequestParam("token") String token, Model model) {
        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            model.addAttribute("error", "Invalid confirmation link, please try again.");
            return "user/confirmationLink";
        }
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("error", "Confirmation link has expired.");
            return "user/confirmationLink";
        }
        String username = verificationToken.getUser().getName();
        userService.activateUserAccount(username);
        return userInfo(model);
    }

    private void loginUser(User user){
        HttpServletRequest loginRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        loginRequest.setAttribute("username", user.getUsername());
        loginRequest.setAttribute("password", user.getPassword());
        authWithHttpServletRequest(loginRequest, user.getUsername(), user.getPassword());
    }

    private void authWithHttpServletRequest(HttpServletRequest request, String username, String password) {
        try {
            request.login(username, password);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/userInfo")
    public String userInfo(Model model){
        model.addAttribute("user", getUser());
        //model.addAttribute("stats", new Stats(getUser().getId()));
        return "user/userInfo";
    }

    @PostMapping("/updateProfilePicture")
    @ResponseBody
    public ResponseEntity<String> updateProfilePicture(@RequestParam("image") MultipartFile file){
        try {
            userService.updateProfilePicture(file, getUser());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok("Image updated successfully.");
    }

}
