package adapter.out.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class WorkoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    LocalDateTime createdAt;
    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL,  orphanRemoval = true)
    List<ExerciseEntity> exercises = new ArrayList<>();
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "user_id"), nullable = false)
    UserEntity owner;

    public WorkoutEntity() {
    }

    public WorkoutEntity(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public List<ExerciseEntity> getExercises() {
        return exercises;
    }

    public void setExercises(List<ExerciseEntity> exercises) {
        this.exercises = exercises;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity createdBy) {
        this.owner = createdBy;
    }
}