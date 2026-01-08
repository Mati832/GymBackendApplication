package adapter.mapper;

import adapter.out.Entities.ExerciseSetEntity;
import domain.model.ExerciseSet;

public class JPAExerciseSetMapper {

    public static ExerciseSet toDomain(ExerciseSetEntity  exerciseSetEntity){
        return new ExerciseSet(exerciseSetEntity.getId(), exerciseSetEntity.getReps(), exerciseSetEntity.getWeightInKg(),
                exerciseSetEntity.getNotes(), exerciseSetEntity.getDurationInSec(), exerciseSetEntity.getCreatedAt(),
                exerciseSetEntity.getExercise().getId());
    }

    public static ExerciseSetEntity toEntity(ExerciseSet  exerciseSet){
        return new ExerciseSetEntity(exerciseSet.getId(), exerciseSet.getReps(), exerciseSet.getWeightInKg(), exerciseSet.getNotes(),
                exerciseSet.getDurationInSec(), exerciseSet.getCreatedAt());
    }
}