package diabdia.creations.possumschedule.entities;

import diabdia.creations.possumschedule.enums.Role;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private boolean activated;
    @Column(unique = true)
    private String name;
    private String password;
    @Column(unique = true)
    private String email;
    @Nullable
    private LocalDate birthday;
    private LocalDate registration;
    @Nullable
    @Basic(fetch = FetchType.EAGER)
    private String profilePicture;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER_UNACTIVATED;
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy="user")
    private List<Activity> activities;
    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy="user")
    private List<Task> tasks;

    public User(){}

    public User(String name, String password, LocalDate birthday, LocalDate registration){
        this.name = name;
        this.password = password;
        this.birthday = birthday;
        this.registration = registration;
    }

    public String getProfilePicture(){
        return Objects.requireNonNullElse(profilePicture, "250.png");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
