package adapter.in.mapper;

import adapter.in.DTOs.RequestDTOs.exerciseSet.ExerciseSetRequest;
import adapter.in.DTOs.ResponseDTOs.exerciseSets.ExerciseSetResponse;
import domain.model.ExerciseSet;

public class ExerciseSetMapper {
    public static ExerciseSet toDomain(ExerciseSetRequest request) {
        return new ExerciseSet(request.id(), request.reps(), request.weightInKg(), request.notes(),
                request.durationInSec(), request.createdAt(), null);
    }

    public static ExerciseSetResponse toResponse(ExerciseSet exerciseSet) {
        return new ExerciseSetResponse(exerciseSet.getReps(), exerciseSet.getWeightInKg(), exerciseSet.getNotes(),
                exerciseSet.getDurationInSec(), exerciseSet.getCreatedAt());
    }
}
