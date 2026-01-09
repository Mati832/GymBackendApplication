package adapter.out.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ExerciseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    //maybe enum later
    String type;
    Long durationInSec;
    LocalDateTime createdAt;
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    UserEntity owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "exercise", orphanRemoval = true)
    List<ExerciseSetEntity> exerciseSets = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "workout_id")
    WorkoutEntity workout;

    public ExerciseEntity() {
    }

    public ExerciseEntity(Long id, String name, String type, Long durationInSec, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.durationInSec = durationInSec;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity createdBy) {
        this.owner = createdBy;
    }

    public List<ExerciseSetEntity> getExerciseSets() {
        return exerciseSets;
    }

    public void setExerciseSets(List<ExerciseSetEntity> exerciseSets) {
        this.exerciseSets = exerciseSets;
    }

    public WorkoutEntity getWorkout() {
        return workout;
    }

    public void setWorkout(WorkoutEntity workout) {
        this.workout = workout;
    }
}