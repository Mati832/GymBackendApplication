package domain.model;

import java.time.LocalDateTime;

public class ExerciseSet {
    long id;
    int reps;
    double weightInKg;
    String notes;
    long durationInSec;
    Exercise belongsTo;
    User createdBy;
    LocalDateTime createdAt;
}
