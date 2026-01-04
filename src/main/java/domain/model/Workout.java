package domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Workout {
    Long id;
    String name;
    String description;
    LocalDateTime createdAt;
    List<Long> exercises =  new ArrayList<>();
    Long createdByUserId;


    public Workout(Long id, String name, String description, LocalDateTime createdAt, List<Long> exercises, Long createdByUserId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.exercises = exercises;
        this.createdByUserId = createdByUserId;
    }

    public Workout() {

    }

    //getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Long> getExercises() {
        return exercises;
    }

    public void setExercises(List<Long> exercises) {
        this.exercises = exercises;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}