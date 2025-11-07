package adapter.out.Entities;

import domain.valueobject.Gender;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String firstName;
    String lastName;
    String email;
    String password;

    @Enumerated(EnumType.STRING)
    Gender gender;
    LocalDateTime bornOn;
    LocalDateTime createdAt;

    @PrePersist//ist so jpa konform(keine abhängigkeit zu hibernate)
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public UserEntity() {
    }

    public UserEntity(String firstName, String lastName, String email, String password, Gender gender, LocalDateTime bornOn, LocalDateTime createdAt) {
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

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDateTime getBornOn() {
        return bornOn;
    }

    public void setBornOn(LocalDateTime bornOn) {
        this.bornOn = bornOn;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
