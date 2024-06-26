package org.eventhub.main.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Pattern(regexp = "\\p{Lu}\\p{Ll}+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Pattern(regexp = "\\p{Lu}\\p{Ll}+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Size(min = 3, max = 20)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Pattern(regexp = "[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

//    @Pattern(regexp = "[A-Za-z\\d]{6,}",
//            message = "Must be minimum 6 symbols long, using digits and latin letters")
//    @Pattern(regexp = ".*\\d.*",
//            message = "Must contain at least one digit")
//    @Pattern(regexp = ".*[A-Z].*",
//            message = "Must contain at least one uppercase letter")
//    @Pattern(regexp = ".*[a-z].*",
//            message = "Must contain at least one lowercase letter")
    @Column(name = "password", nullable = true)
    private String password;

    @Size(max = 255,
            message = "Description length cannot be greater than 255 symbols")
    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "location_city", nullable = true)
    private String city;

    @Past
    @Column(name = "birth_date", nullable = true)
    private LocalDate birthDate;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "provider", nullable = true)
    private String provider;

    @Column (name = "show_email")
    private boolean showEmail;

    @Column (name = "is_verified")
    private boolean isVerified;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<Event> userEvents;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Participant> userParticipants;

    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Photo> profileImages = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private ConfirmationToken confirmationToken;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private PasswordResetToken passwordResetToken;

//    @Enumerated(EnumType.STRING)
//    Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //return List.of(new SimpleGrantedAuthority(role.name()));
        return Collections.emptyList();
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

    @Override
    public String getUsername() {
        return email;
    }

    public String getNickname() {return username;}
}