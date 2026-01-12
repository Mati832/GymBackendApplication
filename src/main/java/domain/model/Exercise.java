package domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Exercise {
    Long id;
    String name;
    //maybe enum later
    String type;
    Long durationInSec;
    Long createdByUserId;
    LocalDateTime createdAt;
    List<Long> exerciseSets = new ArrayList<>();
    Long workoutId;

    public Exercise(Long id, String name, String type, Long durationInSec, Long createdByUserId, List<Long> exerciseSets, LocalDateTime createdAt, Long workoutId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.durationInSec = durationInSec;
        this.createdByUserId = createdByUserId;
        this.exerciseSets = exerciseSets;
        this.createdAt = createdAt;
        this.workoutId = workoutId;
    }

    public Exercise() {

    }

    public Exercise(Long id, String name, String type, Long durationInSec, LocalDateTime createdAt, Long userId, Long workoutId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.durationInSec = durationInSec;
        this.createdByUserId = userId;
        this.workoutId = workoutId;
        this.createdAt = createdAt;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(Long durationInSec) {
        this.durationInSec = durationInSec;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {}

    public List<Long> getExerciseSets() {
        return exerciseSets;
    }

    public void setExerciseSets(List<Long> exerciseSets) {
        this.exerciseSets = exerciseSets;
    }

    public Long getWorkoutId() {
        return workoutId;
    }

    public void setWorkoutId(Long workoutId) {
        this.workoutId = workoutId;
    }

}