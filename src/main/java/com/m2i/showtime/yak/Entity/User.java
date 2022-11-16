package com.m2i.showtime.yak.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "_user")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long id;
    private String firstName;
    private String lastName;
    @Transient
    private String fullName;
    @Column(unique = true, name = "email")
    private String username;
    @Column(name = "profile_picture", length = 64)
    private String profilePicture;
    private String backgroundPicture;
    @Column(name = "_password")
    private String password;
    private String country;
    private LocalDateTime dateCreated = LocalDateTime.now();
    //-------------------------------------------------
    private Boolean isDeleted = false;
    private Boolean isNotificationsActive = false;
    private Boolean isNotificationsTrophiesActive = false;
    private Boolean isNotificationsCommentsActive = false;
    private Boolean isAccountPrivate = false;
    //-------------------------------------------------
    private Boolean isAccountNonExpired = true;
    private Boolean isAccountNonLocked = true;
    private Boolean isCredentialsNonExpired = true;
    private Boolean isEnabled = true;
    @Transient
    private Set<? extends GrantedAuthority> grantedAuthorities;
    //-------------------------------------------------
    private Duration totalMovieWatchedTime = Duration.ZERO;
    private Long totalMovieWatchedNumber = 0L;
    private Duration totalSeriesWatchedTime = Duration.ZERO;
    private Long totalSeriesWatchedNumber = 0L;
    private Long totalEpisodesWatchedNumber = 0L;
    //-------------------------------------------------


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_watched_movies",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "movie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<Movie> watchedMovies = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "users_watched_series",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "id",
                            nullable = false, updatable = false)},
            inverseJoinColumns = {
                    @JoinColumn(name = "serie_id", referencedColumnName = "id",
                            nullable = false, updatable = false)})
    private Set<Serie> watchedSeries = new HashSet<>();
    @ManyToOne
    private Role role;

    public User(String firstName,
                String lastName,
                String username,
                String password,
                String country,
                Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.country = country;
        this.role = role;
    }

    public User(String firstName,
                String lastName,
                String username,
                String password,
                String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.country = country;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getFullName() {
        return String.format("%s %s", getFirstName(), getLastName());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = role.getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + role.getRole()));
        return permissions;
    }
}
