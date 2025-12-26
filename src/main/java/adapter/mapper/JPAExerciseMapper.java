package adapter.mapper;

import adapter.out.Entities.ExerciseEntity;
import adapter.out.Entities.ExerciseSetEntity;
import domain.model.Exercise;

public class JPAExerciseMapper {

    public static Exercise toDomain(ExerciseEntity exerciseEntity){
        return new  Exercise(exerciseEntity.getId(), exerciseEntity.getName(), exerciseEntity.getType(), exerciseEntity.getDurationInSec(),
                exerciseEntity.getOwner().getId(), exerciseEntity.getExerciseSets().stream().
                map(ExerciseSetEntity::getId).toList(), exerciseEntity.getWorkout() == null ? null : exerciseEntity.getWorkout().getId());
    }

    public  static ExerciseEntity toEntity(Exercise exercise){
        return new ExerciseEntity(exercise.getId(), exercise.getName(), exercise.getType(), exercise.getDurationInSec());
    }

    public static ExerciseEntity copyFromDB(ExerciseEntity exerciseEntity){
        ExerciseEntity exerciseEntityCopy = new ExerciseEntity();
        exerciseEntityCopy.setName(exerciseEntity.getName());
        exerciseEntityCopy.setType(exerciseEntity.getType());
        exerciseEntityCopy.setDurationInSec(exerciseEntity.getDurationInSec());
        exerciseEntityCopy.setOwner(exerciseEntity.getOwner());
        exerciseEntityCopy.setExerciseSets(exerciseEntity.getExerciseSets().stream().map(eSetEntity ->{
            ExerciseSetEntity exerciseSetEntity = JPAExerciseSetMapper.copyFromDB(eSetEntity);
            exerciseSetEntity.setExercise(exerciseEntityCopy);
            return exerciseSetEntity;
        }).toList());
        return  exerciseEntityCopy;
    }
}