package domain.model;

import domain.valueobject.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class User {
    Long id;
    String firstName;
    String lastName;
    String email;
    String password;
    Gender gender;
    LocalDate bornOn;
    LocalDateTime createdAt;

    public User() {
    }

    public User(String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.bornOn = bornOn;
    }

    public User(Long id, String firstName, String lastName, String email, String password, Gender gender, LocalDate bornOn, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.bornOn = bornOn;
        this.createdAt = createdAt;
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
