package adapter.out.Entities;

import domain.model.User;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class ExerciseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    //maybe enum later
    String type;
    long durationInSec;
    @ManyToOne
    @JoinColumn(name = "created_by_id", foreignKey = @ForeignKey(name = "fk_exercise_created_by"))
    UserEntity createdBy;

    @OneToMany(cascade = CascadeType.ALL)
    List<ExerciseSetEntity> exerciseSets;
    public UserEntity getCreatedBy() {
        return createdBy;
    }

    //getter and setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public long getDurationInSec() {
        return durationInSec;
    }

    public void setDurationInSec(long durationInSec) {
        this.durationInSec = durationInSec;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public List<ExerciseSetEntity> getExerciseSets() {
        return exerciseSets;
    }

    public void setExerciseSets(List<ExerciseSetEntity> exerciseSets) {
        this.exerciseSets = exerciseSets;
    }
}
