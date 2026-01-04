package domain.model;

import java.time.LocalDateTime;

public class ExerciseSet {
    Long id;
    Integer reps;
    Double weightInKg;
    String notes;
    Long durationInSec;
    LocalDateTime createdAt;
    Long belongsToExercise;

    public ExerciseSet(Long id, Integer reps, Double weightInKg, String notes, Long durationInSec, LocalDateTime createdAt, Long belongsToExercise) {
        this.id = id;
        this.reps = reps;
        this.weightInKg = weightInKg;
        this.notes = notes;
        this.durationInSec = durationInSec;
        this.createdAt = createdAt;
        this.belongsToExercise = belongsToExercise;
    }

    public ExerciseSet() {

    }

    //getter and setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getReps() {
        return reps;
    }

    public void setReps(Integer reps) {
        this.reps = reps;
    }

    public Double getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(Double weightInKg) {
        this.weightInKg = weightInKg;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(Long durationInSec) {
        this.durationInSec = durationInSec;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getBelongsToExercise() {
        return belongsToExercise;
    }

    public void setBelongsToExercise(Long belongsToExercise) {
        this.belongsToExercise = belongsToExercise;
    }
}