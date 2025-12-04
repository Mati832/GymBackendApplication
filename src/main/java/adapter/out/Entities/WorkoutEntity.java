package adapter.out.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class WorkoutEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    LocalDateTime createdAt;
    @OneToMany(cascade = CascadeType.ALL)
    List<ExerciseEntity> exercises;
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", foreignKey = @ForeignKey(name = "user_id"))
    UserEntity createdBy;
}
