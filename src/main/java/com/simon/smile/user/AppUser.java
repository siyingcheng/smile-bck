package com.simon.smile.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Entity
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotEmpty(message = "username is required")
    @Length(min = 3, max = 16, message = "username length must between 3 and 16")
    @Column(unique = true)
    private String username;

    @Length(max = 32, message = "nickname length must between 0 and 32")
    private String nickname;

    private String password;

    @NotEmpty(message = "email is required")
    @Email(message = "email format is invalid")
    @Column(unique = true)
    private String email;

    private String roles;

    private boolean enabled;

    public AppUser() {
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roles='" + roles + '\'' +
                ", enabled=" + enabled +
                '}';
    }

    public AppUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public AppUser setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public AppUser setId(Integer id) {
        this.id = id;
        return this;
    }

    public AppUser setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public AppUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public AppUser setRoles(String roles) {
        this.roles = roles;
        return this;
    }

    public AppUser setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Integer getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getRoles() {
        return roles;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
