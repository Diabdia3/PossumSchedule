package diabdia.creations.possumschedule.service;

import diabdia.creations.possumschedule.entities.User;
import diabdia.creations.possumschedule.entities.VerificationToken;
import diabdia.creations.possumschedule.repositories.UserRepository;
import diabdia.creations.possumschedule.repositories.VerificationTokenRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository repository;
    @Autowired
    VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUser(String name){
        return repository.findByName(name).orElseThrow(()-> new UsernameNotFoundException("Unauthenticated or failed to find the user."));
    }

    public Optional<User> getUserByEmail(String email){
        return repository.findByEmail(email);
    }

    public User registerUser(User user) throws Exception {
        if(repository.findByName(user.getName()).isPresent())
            throw new Exception("User with this name already present.");
        if(repository.findByEmail(user.getEmail()).isPresent())
            throw new Exception("User with this email already present.");
        User temp = new User();
        temp.setName(user.getName());
        temp.setEmail(user.getEmail());
        temp.setRegistration(LocalDate.now());
        temp.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(temp);
        return temp;
    }

    public void createVerificationToken(User user, String token, int minutes){
        tokenRepository.deleteByUser(user.getId());
        VerificationToken t = new VerificationToken(user, token, minutes);
        tokenRepository.save(t);
    }

    public VerificationToken getVerificationToken(String token){
        return tokenRepository.findByToken(token).orElse(null);
    }

    public VerificationToken getVerificationToken(User u){
        return tokenRepository.findByUser(u.getId()).orElse(null);
    }

    public void activateUserAccount(String username){
        User user = repository.findByName(username).orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found."));
        repository.activateUser(user.getId());
        Authentication reAuth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                new HashSet<GrantedAuthority>(Collections.singleton(new SimpleGrantedAuthority("USER"))));
        SecurityContextHolder.getContext().setAuthentication(reAuth);
        RequestContextHolder.currentRequestAttributes().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext(), RequestAttributes.SCOPE_SESSION);

    }

    public void updateProfilePicture(MultipartFile file, User user) throws IOException {
        InputStream initialStream = file.getInputStream();
        byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);
        String fileName = user.getId() + "picture" + "." + FilenameUtils.getExtension(file.getOriginalFilename());
        File targetFile = new File("src/main/resources/static/image/profilePicture/" + fileName);
        OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.close();
        user.setProfilePicture(fileName);
        repository.save(user);
    }

    public void changePassword(String password, User user) {
        user.setPassword(passwordEncoder.encode(password));
        repository.save(user);
    }

}
