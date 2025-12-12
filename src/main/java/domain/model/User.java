package domain.model;

import domain.valueobject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public abstract class User {
    Long id;
    String firstName;
    String lastName;
    String email;
    String password;
    Gender gender;
    LocalDate bornOn;
    LocalDateTime createdAt;
    List<Long> exercises;
    List<Long> workouts;

    public User() {
    }

    public User(Long id, String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt,
                List<Long> exercises, List<Long> workouts) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.bornOn = bornOn;
        this.createdAt = createdAt;
        this.exercises = exercises;
        this.workouts = workouts;
    }

    public User(String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.bornOn = bornOn;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Gender getGender() {
        return gender;
    }

    public LocalDate getBornOn() {
        return bornOn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}