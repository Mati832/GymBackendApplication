package adapter.mapper;

import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.UserEntity;
import adapter.out.Entities.WorkoutEntity;
import domain.model.Workout;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class JPAWorkoutMapper {
    @PersistenceContext
    private static EntityManager em;

    public static Workout toDomain(WorkoutEntity workoutEntity) {
        return new Workout(workoutEntity.getId(), workoutEntity.getName(), workoutEntity.getDescription(), workoutEntity.getCreatedAt(),
                workoutEntity.getExercises().stream().map(ExerciseEntity::getId).toList(), workoutEntity.getCreatedBy().getId());
    }

    public static WorkoutEntity toEntity(Workout workout) {
        return new WorkoutEntity(workout.getId(), workout.getName(), workout.getDescription(), workout.getCreatedAt(),
                workout.getExercises().stream().map(eId -> em.find(ExerciseEntity.class, eId)).toList(),
                em.find(UserEntity.class, workout.getCreatedByUserId()));
    }
}
