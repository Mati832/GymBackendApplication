package adapter.mapper;

import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import adapter.out.Entities.UserEntity;
import domain.model.Exercise;
import jakarta.persistence.EntityManager;

public class JPAExerciseMapper {

    public Exercise toDomain(ExerciseEntity exerciseEntity){
        return new  Exercise(exerciseEntity.getId(), exerciseEntity.getName(), exerciseEntity.getType(), exerciseEntity.getDurationInSec(),
                exerciseEntity.getCreatedBy().getId(), exerciseEntity.getExerciseSets().stream().
                map(ExerciseSetEntity::getId).toList());
    }

    public  ExerciseEntity toEntity(Exercise exercise){
        return new ExerciseEntity(exercise.getId(), exercise.getName(), exercise.getType(), exercise.getDurationInSec());
    }
}