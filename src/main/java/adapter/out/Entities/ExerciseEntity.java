package adapter.out.Entities;

import domain.model.User;
import jakarta.persistence.*;

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

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }
}
