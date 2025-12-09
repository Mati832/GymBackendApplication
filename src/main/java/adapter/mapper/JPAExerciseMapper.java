package adapter.mapper;

import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import adapter.out.Entities.UserEntity;
import domain.model.Exercise;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class JPAExerciseMapper {
    @PersistenceContext
    private static EntityManager em;

    public Exercise toDomain(ExerciseEntity exerciseEntity){
        return new  Exercise(exerciseEntity.getId(), exerciseEntity.getName(), exerciseEntity.getType(), exerciseEntity.getDurationInSec(),
                exerciseEntity.getCreatedBy().getId(), exerciseEntity.getExerciseSets().stream().
                map(ExerciseSetEntity::getId).toList());
    }

    public  ExerciseEntity toEntity(Exercise exercise){
        return new ExerciseEntity(exercise.getId(), exercise.getName(), exercise.getType(), exercise.getDurationInSec(),
                em.find(UserEntity.class, exercise.getCreatedByUserId()), exercise.getExerciseSets().stream().
                map(eSetId -> em.find(ExerciseSetEntity.class, eSetId)).toList());
    }
}