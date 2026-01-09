package adapter.mapper;

import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import domain.model.Exercise;

import java.util.ArrayList;

public class JPAExerciseMapper {

    public static Exercise toDomain(ExerciseEntity exerciseEntity){
        return new  Exercise(exerciseEntity.getId(), exerciseEntity.getName(), exerciseEntity.getType(), exerciseEntity.getDurationInSec(),
                exerciseEntity.getOwner().getId(), new ArrayList<>(exerciseEntity.getExerciseSets().stream().
                map(ExerciseSetEntity::getId).toList()), exerciseEntity.getCreatedAt(),
                exerciseEntity.getWorkout() == null ? null : exerciseEntity.getWorkout().getId());
    }

    public  static ExerciseEntity toEntity(Exercise exercise){
        return new ExerciseEntity(exercise.getId(), exercise.getName(), exercise.getType(), exercise.getDurationInSec(), exercise.getCreatedAt());
    }
}