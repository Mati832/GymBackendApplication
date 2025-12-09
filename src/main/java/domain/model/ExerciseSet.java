package domain.model;

import java.time.LocalDateTime;

public class ExerciseSet {
    long id;
    int reps;
    double weightInKg;
    String notes;
    long durationInSec;
    LocalDateTime createdAt;
    Long belongsToExercise;

    public ExerciseSet(long id, int reps, double weightInKg, String notes, long durationInSec, LocalDateTime createdAt, Long belongsToExercise) {
        this.id = id;
        this.reps = reps;
        this.weightInKg = weightInKg;
        this.notes = notes;
        this.durationInSec = durationInSec;
        this.createdAt = createdAt;
        this.belongsToExercise = belongsToExercise;
    }

    //getter and setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeightInKg() {
        return weightInKg;
    }

    public void setWeightInKg(double weightInKg) {
        this.weightInKg = weightInKg;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(long durationInSec) {
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
